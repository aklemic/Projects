import paho.mqtt.client as mqtt
import sqlite3
import tkinter as tk
from tkinter import ttk
from datetime import datetime

def inicijaliziraj_bazu():
    conn = sqlite3.connect('alarm_baza.db')
    cursor = conn.cursor()
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS dogadaji (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            vrijeme TEXT,
            poruka TEXT
        )
    ''')
    conn.commit()
    conn.close()

def spremi_u_bazu(poruka):
    conn = sqlite3.connect('alarm_baza.db')
    cursor = conn.cursor()
    trenutno_vrijeme = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    cursor.execute("INSERT INTO dogadaji (vrijeme, poruka) VALUES (?, ?)", (trenutno_vrijeme, poruka))
    conn.commit()
    conn.close()
    
    osvjezi_tablicu()

def ucitaj_iz_baze():
    conn = sqlite3.connect('alarm_baza.db')
    cursor = conn.cursor()
    cursor.execute("SELECT vrijeme, poruka FROM dogadaji ORDER BY id DESC")
    podaci = cursor.fetchall()
    conn.close()
    return podaci


TEMA_STANJE = "student/faks/alarm/stanje"
TEMA_KONTROLA = "student/faks/alarm/kontrola"

def on_connect(client, userdata, flags, rc, *args):
    print("Uspješno spojen na HiveMQ broker!")
    client.subscribe(TEMA_STANJE)
    status_label.config(text="Status mreže: SPOJEN NA BROKER", fg="green")

def on_message(client, userdata, msg):
    poruka = msg.payload.decode("utf-8")
    print(f"Stigla poruka sa senzora: {poruka}")
    
    spremi_u_bazu(poruka)
    
    zadnji_dogadaj_label.config(text=f"Zadnji događaj:\n{poruka}")

try:
    client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION1)
except AttributeError:
    client = mqtt.Client()

client.on_connect = on_connect
client.on_message = on_message


def upali_alarm():
    client.publish(TEMA_KONTROLA, "UPALI")
    print("Poslana naredba: UPALI")

def ugasi_alarm():
    client.publish(TEMA_KONTROLA, "UGASI")
    print("Poslana naredba: UGASI")

root = tk.Tk()
root.title("Nadzorna Ploča - Pametni Alarm")
root.geometry("600x500")
root.configure(bg="#f0f0f0")

inicijaliziraj_bazu() 


naslov = tk.Label(root, text="UPRAVLJANJE ALARMOM", font=("Helvetica", 16, "bold"), bg="#f0f0f0")
naslov.pack(pady=10)

status_label = tk.Label(root, text="Status mreže: Spajam se...", font=("Helvetica", 10), bg="#f0f0f0", fg="orange")
status_label.pack()

zadnji_dogadaj_label = tk.Label(root, text="Zadnji događaj:\nNema novih događaja", font=("Helvetica", 12), bg="#e0e0e0", width=50, height=3, relief="ridge")
zadnji_dogadaj_label.pack(pady=15)

frame_gumbi = tk.Frame(root, bg="#f0f0f0")
frame_gumbi.pack(pady=10)

btn_upali = tk.Button(frame_gumbi, text="🚨 UPALI ALARM", font=("Helvetica", 12, "bold"), bg="#ff4d4d", fg="white", width=20, height=2, command=upali_alarm)
btn_upali.grid(row=0, column=0, padx=10)

btn_ugasi = tk.Button(frame_gumbi, text="✅ UGASI ALARM", font=("Helvetica", 12, "bold"), bg="#4da6ff", fg="white", width=20, height=2, command=ugasi_alarm)
btn_ugasi.grid(row=0, column=1, padx=10)

tk.Label(root, text="Povijest događaja (Spremljeno u bazu):", font=("Helvetica", 11, "bold"), bg="#f0f0f0").pack(pady=5)

stupci = ("Vrijeme", "Događaj")
tablica = ttk.Treeview(root, columns=stupci, show="headings", height=8)
tablica.heading("Vrijeme", text="Datum i Vrijeme")
tablica.heading("Događaj", text="Događaj / Poruka")
tablica.column("Vrijeme", width=150)
tablica.column("Događaj", width=350)
tablica.pack(pady=5)

def osvjezi_tablicu():
    for row in tablica.get_children():
        tablica.delete(row)
    podaci = ucitaj_iz_baze()
    for redak in podaci:
        tablica.insert("", "end", values=redak)

osvjezi_tablicu()

client.connect("broker.hivemq.com", 1883, 60)
client.loop_start() 

root.mainloop()

client.loop_stop()
client.disconnect()