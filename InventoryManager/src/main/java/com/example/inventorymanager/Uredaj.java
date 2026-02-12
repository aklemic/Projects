package com.example.inventorymanager;

public abstract class Uredaj implements PrikazDetalja {

    private Long id;
    private String naziv;
    private double cijena;
    private int kolicina;
    private Proizvodjac proizvodjac;

    public Uredaj(Long id, String naziv, double cijena, int kolicina, Proizvodjac proizvodjac) {
        this.id = id;
        this.naziv = naziv;
        this.cijena = cijena;
        this.kolicina = kolicina;
        this.proizvodjac = proizvodjac;
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

    public double getCijena() {
        return cijena;
    }

    public void setCijena(double cijena) {
        this.cijena = cijena;
    }

    public int getKolicina() {
        return kolicina;
    }

    public void setKolicina(int kolicina) {
        this.kolicina = kolicina;
    }

    public Proizvodjac getProizvodjac() {
        return proizvodjac;
    }

    public void setProizvodjac(Proizvodjac proizvodjac) {
        this.proizvodjac = proizvodjac;
    }

    @Override
    public String prikaziDetalje() {
        return "ID: " + id +
                ", Naziv: " + naziv +
                ", Cijena: " + cijena +
                ", Količina: " + kolicina +
                ", Proizvođač: " + (proizvodjac != null ? proizvodjac.getNaziv() : "N/A");
    }
}
