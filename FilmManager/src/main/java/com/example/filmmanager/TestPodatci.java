package com.example.filmmanager;

import java.util.List;

public class TestPodatci {
    public static void main(String[] args) {

        // Generiraj stvarne glumce
        List<Glumac> glumci = GenerirajPodatke.generirajGlumce();

        // Generiraj stvarne žanrove
        List<Žanr> žanrovi = GenerirajPodatke.generirajŽanrove();

        // Generiraj stvarne filmove
        List<Film> filmovi = GenerirajPodatke.generirajFilmove(glumci, žanrovi);

        // Ispiši glumce
        System.out.println("Glumci:");
        for (Glumac g : glumci) {
            System.out.println(g.prikaziDetalje());
        }

        // Ispiši žanrove
        System.out.println("\nŽanrovi:");
        for (Žanr z : žanrovi) {
            System.out.println(z.prikaziDetalje());
        }

        // Ispiši filmove
        System.out.println("\nFilmovi:");
        for (Film f : filmovi) {
            System.out.println(f.prikaziDetalje());
        }
    }
}
