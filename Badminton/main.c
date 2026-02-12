/*
 * Badminton.c
 *
 * Elektroni?ki broja? poena za badminton
 */

#define F_CPU 16000000UL
#include <avr/io.h>
#include <util/delay.h>
#include <stdint.h>
#include <avr/eeprom.h>
#include <avr/interrupt.h>
#include "lcd.h"

#define LED_LEFT_PIN      PB4
#define LED_RIGHT_PIN     PB5

#define BUTTON_LEFT_PIN   PD0   // lijevi igrac (INT0)
#define BUTTON_RIGHT_PIN  PD1   // desni igrac (polling)
#define BUTTON_RESET_PIN  PF6   // reset seta

#define BAUDRATE   9600UL
#define UBRR_VALUE ((F_CPU / (16UL * BAUDRATE)) - 1)

// EEPROM adrese za zadnji zavrseni set
uint8_t EEMEM ee_lastLeft  = 0;
uint8_t EEMEM ee_lastRight = 0;

// stanje igre: u tijeku ili pobjeda lijevog/desnog
typedef enum {
    GAME_PLAYING,
    GAME_WIN_LEFT,
    GAME_WIN_RIGHT
} GameState;

GameState gameState = GAME_PLAYING;

// Timer0 tick za debounce tipkala
volatile uint8_t g_timerTick = 0;

// RX znak s PC-a (r/R = reset)
volatile char g_rxCommand = 0;

// zahtjev za poen lijevog igraca (postavlja INT0 ISR)
volatile uint8_t g_leftPointRequest = 0;

// omogucavanje INT0 nakon inicijalizacije
volatile uint8_t g_int0Enabled = 0;

// -------------------- USART ------------------------
// Inicijalizacija USART1 na 9600 bps, format 8N1
void usart_init(void)
{
    UBRR1H = (uint8_t)(UBRR_VALUE >> 8);
    UBRR1L = (uint8_t)UBRR_VALUE;

    UCSR1C = (1 << UCSZ11) | (1 << UCSZ10);      // 8 bita podataka, 1 stop-bit
    UCSR1B = (1 << TXEN1) | (1 << RXEN1) | (1 << RXCIE1); // TX, RX i RX interrupt
}

void usart_send_char(char c)
{
    while (!(UCSR1A & (1 << UDRE1))) { }        // cekaj prazan TX buffer
    UDR1 = c;
}

void usart_send_string(const char *s)
{
    while (*s) {
        usart_send_char(*s++);
    }
}

// prekidna rutina za prijem jednog znaka s PC-a
ISR(USART1_RX_vect)
{
    char c = UDR1;
    g_rxCommand = c;
}

// ---------------- EEPROM i set ---------------------
// spremanje rezultata zavrsenog seta u EEPROM
void save_last_set(uint8_t scoreLeft, uint8_t scoreRight)
{
    eeprom_update_byte(&ee_lastLeft,  scoreLeft);
    eeprom_update_byte(&ee_lastRight, scoreRight);
}

// prikaz rezultata zadnjeg zavrsenog seta pri ukljucenju
void show_last_set(void)
{
    uint8_t lastL = eeprom_read_byte(&ee_lastLeft);
    uint8_t lastR = eeprom_read_byte(&ee_lastRight);

    if (lastL == 0xFF) lastL = 0;
    if (lastR == 0xFF) lastR = 0;

    lcd_clrscr();
    lcd_gotoxy(0, 0);
    lcd_print("LAST L:%02u R:%02u", lastL, lastR);
    _delay_ms(2000);
}

// postavljanje novog seta na 0:0
void reset_set(uint8_t *scoreLeft, uint8_t *scoreRight)
{
    *scoreLeft = 0;
    *scoreRight = 0;
    gameState = GAME_PLAYING;

    PORTB &= ~((1 << LED_LEFT_PIN) | (1 << LED_RIGHT_PIN)); // ugasi LED

    lcd_clrscr();
    lcd_gotoxy(0, 0);
    lcd_print("L:00 R:00 PLY");

    usart_send_string("RESET L=00 R=00\r\n");
}

// pravila badmintona: do 21 (+2), produzetak do max 30
void check_winner(uint8_t scoreLeft, uint8_t scoreRight)
{
    if (gameState != GAME_PLAYING)
        return;

    if (scoreLeft >= 21 || scoreRight >= 21) {
        if (scoreLeft == 30 && scoreRight <= 29) {
            gameState = GAME_WIN_LEFT;
        } else if (scoreRight == 30 && scoreLeft <= 29) {
            gameState = GAME_WIN_RIGHT;
        } else if (scoreLeft >= scoreRight + 2 && scoreLeft >= 21) {
            gameState = GAME_WIN_LEFT;
        } else if (scoreRight >= scoreLeft + 2 && scoreRight >= 21) {
            gameState = GAME_WIN_RIGHT;
        }
    }

    if (gameState == GAME_WIN_LEFT) {
        lcd_gotoxy(0, 0);
        lcd_print("L:%02u R:%02u LW ", scoreLeft, scoreRight);
        save_last_set(scoreLeft, scoreRight);

        usart_send_string("WIN LEFT L=");
        char buf[8];
        sprintf(buf, "%02u", scoreLeft);
        usart_send_string(buf);
        usart_send_string(" R=");
        sprintf(buf, "%02u", scoreRight);
        usart_send_string(buf);
        usart_send_string("\r\n");
    } else if (gameState == GAME_WIN_RIGHT) {
        lcd_gotoxy(0, 0);
        lcd_print("L:%02u R:%02u RW ", scoreLeft, scoreRight);
        save_last_set(scoreLeft, scoreRight);

        usart_send_string("WIN RIGHT L=");
        char buf[8];
        sprintf(buf, "%02u", scoreLeft);
        usart_send_string(buf);
        usart_send_string(" R=");
        sprintf(buf, "%02u", scoreRight);
        usart_send_string(buf);
        usart_send_string("\r\n");
    }
}

// ---------------- Timer0 (debounce) ----------------
// Timer0 u normalnom nacinu rada, overflow za debounce
void timer0_init(void)
{
    TCCR0A = 0x00;                               // normal mode
    TCCR0B = (1 << CS02) | (1 << CS00);          // preskaler 1024
    TIMSK0 = (1 << TOIE0);                       // overflow interrupt omogucen
    TCNT0  = 0;
}

// overflow Timer0 – generira periodicni tick
ISR(TIMER0_OVF_vect)
{
    g_timerTick = 1;
}

// ---------------- INT0 (PD0) -----------------------
// vanjski prekid INT0 na uzlazni brid tipkala lijevog igraca
void int0_init(void)
{
    EICRA |= (1 << ISC01) | (1 << ISC00);        // rising edge
    EIMSK |= (1 << INT0);                        // omoguci INT0

    g_int0Enabled = 0;
}

// INT0 – prijava poena lijevog igraca
ISR(INT0_vect)
{
    if (g_int0Enabled) {
        g_leftPointRequest = 1;
    }
}

// ---------------------- main -----------------------

int main(void)
{
    // LED-ice kao izlazi
    DDRB |= (1 << LED_LEFT_PIN) | (1 << LED_RIGHT_PIN);

    // Tipke PD0, PD1 kao ulazi s pull-upom
    DDRD &= ~((1 << BUTTON_LEFT_PIN) | (1 << BUTTON_RIGHT_PIN));
    PORTD |= (1 << BUTTON_LEFT_PIN) | (1 << BUTTON_RIGHT_PIN);

    // Reset na PF6 kao ulaz s pull-upom
    DDRF &= ~(1 << BUTTON_RESET_PIN);
    PORTF |= (1 << BUTTON_RESET_PIN);

    uint8_t scoreLeft = 0;
    uint8_t scoreRight = 0;

    uint8_t prevRight = 1;
    uint8_t prevReset = 1;

    lcd_init();       // inicijalizacija LCD-a
    usart_init();     // serijska komunikacija s PC-jem
    timer0_init();    // debounce tipkala
    int0_init();      // vanjski prekid za lijevo tipkalo

    sei();            // globalno omoguci prekide

    show_last_set();                       // kratki prikaz zadnjeg seta
    reset_set(&scoreLeft, &scoreRight);    // novi set 0:0

    g_int0Enabled = 1; // tek sada dozvoli INT0 da broji poene

    while (1)
    {
        // cekaj Timer0 tick
        if (!g_timerTick)
            continue;
        g_timerTick = 0;

        // RX komande s PC-a (r/R = reset)
        if (g_rxCommand != 0) {
            char cmd = g_rxCommand;
            g_rxCommand = 0;

            if (cmd == 'r' || cmd == 'R') {
                reset_set(&scoreLeft, &scoreRight);
            }
        }

        // ocitavanje tipkala za desnog igraca i reset
        uint8_t rightPressed = !(PIND & (1 << BUTTON_RIGHT_PIN));
        uint8_t resetPressed = !(PINF & (1 << BUTTON_RESET_PIN));

        // reset seta tipkalom
        if (resetPressed && !prevReset) {
            reset_set(&scoreLeft, &scoreRight);
        }

        if (gameState == GAME_PLAYING) {
            // lijevi poen – postavlja ga INT0 ISR
            if (g_leftPointRequest) {
                g_leftPointRequest = 0;
                if (scoreLeft < 30) {
                    scoreLeft++;

                    char buf[32];
                    sprintf(buf, "L=%02u R=%02u\r\n", scoreLeft, scoreRight);
                    usart_send_string(buf);
                }
            }

            // desni poen – polling tipkala
            if (rightPressed && !prevRight) {
                if (scoreRight < 30) {
                    scoreRight++;

                    char buf[32];
                    sprintf(buf, "L=%02u R=%02u\r\n", scoreLeft, scoreRight);
                    usart_send_string(buf);
                }
            }
        }

        prevRight = rightPressed;
        prevReset = resetPressed;

        // osnovni prikaz rezultata dok je set u tijeku
        if (gameState == GAME_PLAYING) {
            lcd_gotoxy(0, 0);
            lcd_print("L:%02u R:%02u PLY", scoreLeft, scoreRight);
        }

        // upravljanje LED-icama ovisno o pobjedniku
        if (gameState == GAME_WIN_LEFT) {
            PORTB |=  (1 << LED_LEFT_PIN);
            PORTB &= ~(1 << LED_RIGHT_PIN);
        } else if (gameState == GAME_WIN_RIGHT) {
            PORTB |=  (1 << LED_RIGHT_PIN);
            PORTB &= ~(1 << LED_LEFT_PIN);
        } else {
            PORTB &= ~((1 << LED_LEFT_PIN) | (1 << LED_RIGHT_PIN));
        }

        // provjera je li netko osvojio set
        check_winner(scoreLeft, scoreRight);
    }
}
