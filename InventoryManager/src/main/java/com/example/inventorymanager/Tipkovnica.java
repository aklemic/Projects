package com.example.inventorymanager;

public class Tipkovnica extends Uredaj {

    private boolean mehanicka;
    private String layout;

    public Tipkovnica(Long id,
                      String naziv,
                      double cijena,
                      int kolicina,
                      Proizvodjac proizvodjac,
                      boolean mehanicka,
                      String layout) {

        super(id, naziv, cijena, kolicina, proizvodjac);
        this.mehanicka = mehanicka;
        this.layout = layout;
    }

    public boolean isMehanicka() {
        return mehanicka;
    }

    public void setMehanicka(boolean mehanicka) {
        this.mehanicka = mehanicka;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    @Override
    public String prikaziDetalje() {
        return super.prikaziDetalje()
                + ", Mehanička: " + (mehanicka ? "da" : "ne")
                + ", Layout: " + layout;
    }
}
