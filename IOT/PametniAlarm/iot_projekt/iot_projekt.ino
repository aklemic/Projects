#include <WiFi.h>
#include <PubSubClient.h> // NOVO: Biblioteka za MQTT komunikaciju

const char* ssid = "IME_WIFIJA";
const char* password = "LOZINKA WIFIJA";

const char* mqtt_server = "broker.hivemq.com"; // Besplatni javni server (broker)
const char* temaSlanje = "student/faks/alarm/stanje";    // Ovdje ESP32 šalje podatke
const char* temaKontrola = "student/faks/alarm/kontrola"; // Ovdje ESP32 sluša naredbe

WiFiClient espClient;
PubSubClient client(espClient);

int ledPin = 4;      
int buzzerPin = 5;   
int pirPin = 15;     
int buttonPin = 18;  
int reedPin = 19;    

int alarmAktiviran = 0; 
unsigned long zadnjaPoruka = 0; 

void callback(char* topic, byte* payload, unsigned int length) {
  String poruka = "";
  for (int i = 0; i < length; i++) {
    poruka += (char)payload[i];
  }
  
  Serial.print("Stigla MQTT naredba: ");
  Serial.println(poruka);

  if (poruka == "UPALI") {
    alarmAktiviran = 1;
    Serial.println("--- ALARM AKTIVIRAN PREKO INTERNETA! ---");
    client.publish(temaSlanje, "Status: Alarm je UPALJEN (Daljinski)");
    digitalWrite(ledPin, HIGH); 
    delay(1000); 
    digitalWrite(ledPin, LOW);
  } 
  // Ako stigne poruka "UGASI" preko interneta
  else if (poruka == "UGASI") {
    alarmAktiviran = 0;
    Serial.println("--- ALARM ISKLJUČEN PREKO INTERNETA! ---");
    client.publish(temaSlanje, "Status: Alarm je UGAŠEN (Daljinski)");
    digitalWrite(buzzerPin, LOW);
    digitalWrite(ledPin, LOW);
  }
}


void reconnect() {
  while (!client.connected()) {
    Serial.print("Spajam se na MQTT broker...");
    // Spajamo se s nasumičnim imenom kako ne bi bilo konflikta
    String clientId = "ESP32Student-";
    clientId += String(random(0xffff), HEX);
    
    if (client.connect(clientId.c_str())) {
      Serial.println(" Spojen!");
      client.subscribe(temaKontrola);
    } else {
      Serial.print(" Greska, stanje=");
      Serial.print(client.state());
      Serial.println(" Pokusavam ponovno za 5 sekundi");
      delay(5000);
    }
  }
}

void setup() {
  Serial.begin(115200);

  pinMode(ledPin, OUTPUT);
  pinMode(buzzerPin, OUTPUT);
  pinMode(pirPin, INPUT);
  pinMode(reedPin, INPUT_PULLUP);
  pinMode(buttonPin, INPUT_PULLUP); 


  Serial.print("Spajam se na Wi-Fi mrezu: ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("\nUspjesno spojen na Wi-Fi!");
  Serial.print("IP adresa: ");
  Serial.println(WiFi.localIP());


  client.setServer(mqtt_server, 1883);
  client.setCallback(callback); // Kažemo ESP-u koju funkciju da zove kad stigne poruka
}

void loop() {
  if (!client.connected()) {
    reconnect();
  }
  client.loop(); 


  if (digitalRead(buttonPin) == LOW) { 
    delay(250); 
    if (alarmAktiviran == 0) {
      alarmAktiviran = 1;
      Serial.println("--- ALARM JE AKTIVIRAN (Gumb)! ---");
      client.publish(temaSlanje, "Status: Alarm je UPALJEN (Lokalno)");
      digitalWrite(ledPin, HIGH); delay(1000); digitalWrite(ledPin, LOW);
    } else {
      alarmAktiviran = 0;
      Serial.println("--- ALARM JE ISKLJUČEN (Gumb)! ---");
      client.publish(temaSlanje, "Status: Alarm je UGAŠEN (Lokalno)"); 
      digitalWrite(buzzerPin, LOW); 
      digitalWrite(ledPin, LOW);
    }
  }

  if (alarmAktiviran == 1) {
    int pirStanje = digitalRead(pirPin);
    int reedStanje = digitalRead(reedPin);

    if (pirStanje == HIGH || reedStanje == HIGH) {
      digitalWrite(ledPin, HIGH);
      digitalWrite(buzzerPin, HIGH);
      
      unsigned long trenutnoVrijeme = millis();
      if (trenutnoVrijeme - zadnjaPoruka > 1000) {
        if (pirStanje == HIGH) {
          Serial.println("UPOZORENJE! Pokret!");
          client.publish(temaSlanje, "UPOZORENJE: Detektiran pokret!"); 
        }
        if (reedStanje == HIGH) {
          Serial.println("UPOZORENJE! Vrata!");
          client.publish(temaSlanje, "UPOZORENJE: Vrata su otvorena!"); 
        }
        zadnjaPoruka = trenutnoVrijeme;
      }
    } 
    else {
      digitalWrite(ledPin, LOW);
      digitalWrite(buzzerPin, LOW);
    }
  }
}