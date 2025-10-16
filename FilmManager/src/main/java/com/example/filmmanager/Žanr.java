package com.example.filmmanager;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;

/**
 * Klasa Žanr predstavlja filmski žanr.
 * Koristi JavaFX StringProperty za naziv radi automatskog osvježavanja UI elemenata
 * (kao što su TableView ili ComboBox) prilikom promjena naziva žanra.
 * Implementira sučelje Prikaz za standardizirani prikaz detalja i Serializable za serijalizaciju.
 */
public class Žanr implements Prikaz, Serializable {

    // JavaFX property koji sadrži naziv žanra. Oznaka transient jer nije direktno serijaliziran.
    private transient StringProperty naziv;

    /**
     * Konstruktor za inicijalizaciju žanra s nazivom.
     * @param naziv naziv žanra, npr. "Drama", "Komedija"
     */
    public Žanr(String naziv) {
        this.naziv = new SimpleStringProperty(naziv);
    }

    /**
     * Getter za naziv žanra.
     * @return naziv žanra kao string
     */
    public String getNaziv() {
        return naziv.get();
    }

    /**
     * Setter za naziv žanra.
     * Postavlja novu vrijednost i automatski osvježava povezane UI elemente.
     * @param naziv novi naziv žanra
     */
    public void setNaziv(String naziv) {
        this.naziv.set(naziv);
    }

    /**
     * Vraća StringProperty objekta radi vezanja (binding) s JavaFX UI komponentama.
     * @return naziv žanra kao property
     */
    public StringProperty nazivProperty() {
        return naziv;
    }

    /**
     * Implementacija metode prikaza detalja iz sučelja Prikaz.
     * Koristi se uglavnom za prikaz u obliku stringa ili info dijalogu.
     * @return detaljni opis žanra
     */
    @Override
    public String prikaziDetalje() {
        return "Žanr: " + getNaziv();
    }

    /**
     * Standardna metoda za pretvorbu u string.
     * Koristi se u ComboBox i drugim UI elementima gdje se traži tekstualni prikaz.
     * @return naziv žanra kao string
     */
    @Override
    public String toString() {
        return getNaziv();
    }

    /**
     * Prilagođena serijalizacija zbog JavaFX propertyja koji nisu Serializable.
     * Prvo se poziva defaultna serijalizacija,
     * zatim se zapisuje vrijednost naziva u UTF-8.
     * @param out ObjectOutputStream za pisanje podataka
     * @throws java.io.IOException u slučaju IO greške
     */
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject(); // serijalizira ostale eventualne polja (trenutno nema)
        out.writeUTF(getNaziv()); // zapisuje naziv posebno
    }

    /**
     * Prilagođeno učitavanje pri deserijalizaciji.
     * Vraća vrijednost naziva i inicijalizira JavaFX StringProperty na taj naziv.
     * @param in ObjectInputStream za čitanje podataka
     * @throws java.io.IOException u slučaju IO greške
     * @throws ClassNotFoundException ako klasa nije pronađena tijekom učitavanja
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject(); // učitava moguće druge polja (trenutno nema)
        this.naziv = new SimpleStringProperty(in.readUTF()); // inicijalizira property tijekom učitavanja
    }
}
