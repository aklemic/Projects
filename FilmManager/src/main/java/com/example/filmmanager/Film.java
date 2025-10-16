package com.example.filmmanager;

import javafx.beans.property.*;

import java.io.Serializable;

/**
 * Klasa Film predstavlja filmsku instancu.
 * Koristi JavaFX propertyje za naslove, godine, žanr i glumca,
 * čime omogućuje automatsku detekciju promjena i reaktivnu nadogradnju UI‑a.
 * Nasljeđuje apstraktnu klasu Medij.
 * Implementira Serializable radi pohrane i Prikaz radi standardiziranog ispisa.
 */
public class Film extends Medij implements Serializable {

    private transient StringProperty naslov;
    private transient IntegerProperty godina;
    private transient ObjectProperty<Žanr> žanr;
    private transient ObjectProperty<Glumac> glumac;

    /**
     * Konstruktor s početnim vrijednostima i inicijalizacijom propertyja.
     * Postavlja i bazne vrijednosti u Medij klasi.
     * @param naslov naslov filma
     * @param godina godina izlaska
     * @param žanr žanr
     * @param glumac glumac
     */
    public Film(String naslov, int godina, Žanr žanr, Glumac glumac) {
        super(naslov, godina);
        this.naslov = new SimpleStringProperty(naslov);
        this.godina = new SimpleIntegerProperty(godina);
        this.žanr = new SimpleObjectProperty<>(žanr);
        this.glumac = new SimpleObjectProperty<>(glumac);
    }

    public String getNaslov() {
        return naslov.get();
    }

    public void setNaslov(String naslov) {
        this.naslov.set(naslov);
        super.setNaslov(naslov);
    }

    public StringProperty naslovProperty() {
        return naslov;
    }

    public int getGodina() {
        return godina.get();
    }

    public void setGodina(int godina) {
        this.godina.set(godina);
        super.setGodinaIzdanja(godina);
    }

    public IntegerProperty godinaProperty() {
        return godina;
    }

    public Žanr getŽanr() {
        return žanr.get();
    }

    public void setŽanr(Žanr žanr) {
        this.žanr.set(žanr);
    }

    public ObjectProperty<Žanr> žanrProperty() {
        return žanr;
    }

    public Glumac getGlumac() {
        return glumac.get();
    }

    public void setGlumac(Glumac glumac) {
        this.glumac.set(glumac);
    }

    public ObjectProperty<Glumac> glumacProperty() {
        return glumac;
    }

    /**
     * Prikazuje detalje o filmu u obliku stringa.
     * @return detaljni opis filma
     */
    @Override
    public String prikaziDetalje() {
        return String.format("Film: %s (%d), Žanr: %s, Glumac: %s",
                getNaslov(), getGodina(), getŽanr().getNaziv(), getGlumac().getImePrezime());
    }

    @Override
    public String toString() {
        return prikaziDetalje();
    }

    /**
     * Prilagođena serijalizacija radi spremanja propertyja kao običnih vrijednosti.
     * @param out tok za pisanje
     * @throws java.io.IOException pri greškama IO-a
     */
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject();
        out.writeUTF(getNaslov());
        out.writeInt(getGodina());
        out.writeObject(getŽanr());
        out.writeObject(getGlumac());
    }

    /**
     * Prilagođeno učitavanje radi inicijalizacije propertyja sa spremljenim vrijednostima.
     * @param in tok za čitanje
     * @throws java.io.IOException pri greškama IO-a
     * @throws ClassNotFoundException pri nedostatku klasa
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        String naslovVal = in.readUTF();
        int godinaVal = in.readInt();
        Žanr žanrVal = (Žanr) in.readObject();
        Glumac glumacVal = (Glumac) in.readObject();

        this.naslov = new SimpleStringProperty(naslovVal);
        this.godina = new SimpleIntegerProperty(godinaVal);
        this.žanr = new SimpleObjectProperty<>(žanrVal);
        this.glumac = new SimpleObjectProperty<>(glumacVal);

        super.setNaslov(naslovVal);
        super.setGodinaIzdanja(godinaVal);
    }
}
