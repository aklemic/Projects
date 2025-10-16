package com.example.filmmanager;

import javafx.beans.property.*;

import java.io.Serializable;

/**
 * Klasa Glumac predstavlja glumca u aplikaciji.
 * Sadrži osnovne atribute: ime, prezime, godinu rođenja i spol,
 * koji su implementirani kao JavaFX property objekti radi potpune podrške za reaktivni UI.
 * Implementira Prikaz za standardizirani prikaz i Serializable za pohranu.
 */
public class Glumac implements Prikaz, Serializable {

    // JavaFX propertyji, transient zbog serijalizacije
    private transient StringProperty ime;
    private transient StringProperty prezime;
    private transient IntegerProperty godinaRodjenja;
    private transient StringProperty spol;

    /**
     * Konstruktor za inicijalizaciju glumca.
     * Postavlja početne vrijednosti i kreira JavaFX propertyje.
     * @param ime ime glumca
     * @param prezime prezime glumca
     * @param godinaRodjenja godina rođenja
     * @param spol spol ("M" ili "Ž")
     */
    public Glumac(String ime, String prezime, int godinaRodjenja, String spol) {
        this.ime = new SimpleStringProperty(ime);
        this.prezime = new SimpleStringProperty(prezime);
        this.godinaRodjenja = new SimpleIntegerProperty(godinaRodjenja);
        this.spol = new SimpleStringProperty(spol);
    }

    // Getteri, setteri i propertyji za vezu UI i modela:

    public String getIme() {
        return ime.get();
    }

    public void setIme(String ime) {
        this.ime.set(ime);
    }

    public StringProperty imeProperty() {
        return ime;
    }

    public String getPrezime() {
        return prezime.get();
    }

    public void setPrezime(String prezime) {
        this.prezime.set(prezime);
    }

    public StringProperty prezimeProperty() {
        return prezime;
    }

    public int getGodinaRodjenja() {
        return godinaRodjenja.get();
    }

    public void setGodinaRodjenja(int godinaRodjenja) {
        this.godinaRodjenja.set(godinaRodjenja);
    }

    public IntegerProperty godinaRodjenjaProperty() {
        return godinaRodjenja;
    }

    public String getSpol() {
        return spol.get();
    }

    public void setSpol(String spol) {
        this.spol.set(spol);
    }

    public StringProperty spolProperty() {
        return spol;
    }

    /**
     * Daje puni naziv glumca u obliku imena i prezimena.
     * Korisno za prikaz npr. u ComboBox ili filtriranju.
     * @return spojeno ime i prezime
     */
    public String getImePrezime() {
        return getIme() + " " + getPrezime();
    }

    /**
     * Prikaz detalja glumca s pripadnim rodom i godinom rođenja.
     * @return tekstualni opis glumca
     */
    @Override
    public String prikaziDetalje() {
        String rodniIzraz = getSpol().equals("M") ? "rođen" : "rođena";
        return "Glumac: " + getImePrezime() + " (" + rodniIzraz + " " + getGodinaRodjenja() + ")";
    }

    /**
     * Standardni string prikaz s imenom, prezimenom i ostalim podacima.
     * Koristi se u UI listama i izborima.
     * @return tekstualni opis glumca
     */
    @Override
    public String toString() {
        String rodniIzraz = getSpol().equals("M") ? "rođen" : "rođena";
        return getImePrezime() + " (" + rodniIzraz + " " + getGodinaRodjenja() + ")";
    }

    /**
     * Prilagođena serijalizacija za propertyje koji nisu Serializable.
     * Sprema vrijednosti propertyja u tok.
     * @param out tok za pisanje objekta
     * @throws java.io.IOException pri greškama u IO
     */
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject();
        out.writeUTF(getIme());
        out.writeUTF(getPrezime());
        out.writeInt(getGodinaRodjenja());
        out.writeUTF(getSpol());
    }

    /**
     * Prilagođeno učitavanje pri deserijalizaciji.
     * Inicijalizira propertyje sa spremljenim vrijednostima.
     * @param in tok za čitanje objekta
     * @throws java.io.IOException pri greškama u IO
     * @throws ClassNotFoundException pri nedostatku klase
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.ime = new SimpleStringProperty(in.readUTF());
        this.prezime = new SimpleStringProperty(in.readUTF());
        this.godinaRodjenja = new SimpleIntegerProperty(in.readInt());
        this.spol = new SimpleStringProperty(in.readUTF());
    }
}
