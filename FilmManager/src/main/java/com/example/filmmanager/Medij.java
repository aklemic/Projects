package com.example.filmmanager;

public abstract class Medij implements Prikaz {
    protected String naslov;
    protected int godinaIzdanja;

    public Medij(String naslov, int godinaIzdanja) {
        this.naslov = naslov;
        this.godinaIzdanja = godinaIzdanja;
    }

    public String getNaslov() {
        return naslov;
    }

    public void setNaslov(String naslov) {
        this.naslov = naslov;
    }

    public int getGodinaIzdanja() {
        return godinaIzdanja;
    }

    public void setGodinaIzdanja(int godinaIzdanja) {
        this.godinaIzdanja = godinaIzdanja;
    }

    // Ovdje ne pišemo implementaciju prikaziDetalje() jer je klasa apstraktna,
    // izvedene klase (poput Film) će ju implementirati.
}
