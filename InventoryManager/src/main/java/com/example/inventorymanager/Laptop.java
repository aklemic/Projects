package com.example.inventorymanager;

public class Laptop extends Uredaj {

    private int ramGB;
    private int ssdGB;
    private String procesor;

    public Laptop(Long id,
                  String naziv,
                  double cijena,
                  int kolicina,
                  Proizvodjac proizvodjac,
                  int ramGB,
                  int ssdGB,
                  String procesor) {

        super(id, naziv, cijena, kolicina, proizvodjac);
        this.ramGB = ramGB;
        this.ssdGB = ssdGB;
        this.procesor = procesor;
    }

    public int getRamGB() {
        return ramGB;
    }

    public void setRamGB(int ramGB) {
        this.ramGB = ramGB;
    }

    public int getSsdGB() {
        return ssdGB;
    }

    public void setSsdGB(int ssdGB) {
        this.ssdGB = ssdGB;
    }

    public String getProcesor() {
        return procesor;
    }

    public void setProcesor(String procesor) {
        this.procesor = procesor;
    }

    @Override
    public String prikaziDetalje() {
        return super.prikaziDetalje()
                + ", RAM: " + ramGB + " GB"
                + ", SSD: " + ssdGB + " GB"
                + ", Procesor: " + procesor;
    }
}
