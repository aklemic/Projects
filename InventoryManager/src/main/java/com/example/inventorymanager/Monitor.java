package com.example.inventorymanager;

public class Monitor extends Uredaj {

    private double dijagonalaInch;
    private String rezolucija;

    public Monitor(Long id,
                   String naziv,
                   double cijena,
                   int kolicina,
                   Proizvodjac proizvodjac,
                   double dijagonalaInch,
                   String rezolucija) {

        super(id, naziv, cijena, kolicina, proizvodjac);
        this.dijagonalaInch = dijagonalaInch;
        this.rezolucija = rezolucija;
    }

    public double getDijagonalaInch() {
        return dijagonalaInch;
    }

    public void setDijagonalaInch(double dijagonalaInch) {
        this.dijagonalaInch = dijagonalaInch;
    }

    public String getRezolucija() {
        return rezolucija;
    }

    public void setRezolucija(String rezolucija) {
        this.rezolucija = rezolucija;
    }

    @Override
    public String prikaziDetalje() {
        return super.prikaziDetalje()
                + ", Dijagonala: " + dijagonalaInch + "\""
                + ", Rezolucija: " + rezolucija;
    }
}
