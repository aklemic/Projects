package com.example.filmmanager;

import java.util.ArrayList;
import java.util.List;

public class GenerirajPodatke {

    public static List<Glumac> generirajGlumce() {
        List<Glumac> glumci = new ArrayList<>();
        glumci.add(new Glumac("Leonardo", "DiCaprio", 1974, "M"));
        glumci.add(new Glumac("Natalie", "Portman", 1981, "Ž"));
        glumci.add(new Glumac("Brad", "Pitt", 1963, "M"));
        glumci.add(new Glumac("Scarlett", "Johansson", 1984, "Ž"));
        glumci.add(new Glumac("Morgan", "Freeman", 1937, "M"));
        return glumci;
    }

    public static List<Žanr> generirajŽanrove() {
        List<Žanr> žanrovi = new ArrayList<>();
        žanrovi.add(new Žanr("Drama"));
        žanrovi.add(new Žanr("Akcija"));
        žanrovi.add(new Žanr("Misterija"));
        žanrovi.add(new Žanr("Komedija"));
        žanrovi.add(new Žanr("Thriller"));
        return žanrovi;
    }

    public static List<Film> generirajFilmove(List<Glumac> glumci, List<Žanr> žanrovi) {
        List<Film> filmovi = new ArrayList<>();
        filmovi.add(new Film("Titanic", 1997, žanrovi.get(0), glumci.get(0))); // Drama, Leonardo DiCaprio
        filmovi.add(new Film("Black Swan", 2010, žanrovi.get(0), glumci.get(1))); // Drama, Natalie Portman
        filmovi.add(new Film("Fight Club", 1999, žanrovi.get(0), glumci.get(2))); // Drama, Brad Pitt
        filmovi.add(new Film("Lost in Translation", 2003, žanrovi.get(3), glumci.get(3))); // Komedija, Scarlett Johansson
        filmovi.add(new Film("Se7en", 1995, žanrovi.get(4), glumci.get(4))); // Thriller, Morgan Freeman
        return filmovi;
    }
}
