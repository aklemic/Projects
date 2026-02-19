package com.example.inventorymanager;

public class Proizvodjac implements PrikazDetalja {

    private Long id;
    private String naziv;
    private String drzava;

    public Proizvodjac(Long id, String naziv, String drzava) {
        this.id = id;
        this.naziv = naziv;
        this.drzava = drzava;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getDrzava() {
        return drzava;
    }

    public void setDrzava(String drzava) {
        this.drzava = drzava;
    }

    @Override
    public String prikaziDetalje() {
        return "Proizvođač: " + naziv + " (" + drzava + "), ID: " + id;
    }

    @Override
    public String toString() {
        return naziv;
    }
}
