package com.example.inventorymanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InMemoryDataSource {

    private final List<Proizvodjac> proizvodjaci = new ArrayList<>();
    private final List<Uredaj> uredaji = new ArrayList<>();

    // Singleton FileDataStore
    private final FileDataStore fileDataStore = FileDataStore.getInstance();

    public InMemoryDataSource() {
        try {
            if (fileDataStore.dataFilesExist()) {
                List<Proizvodjac> ucitaniProizvodjaci = fileDataStore.loadProizvodjaci();
                proizvodjaci.addAll(ucitaniProizvodjaci);

                List<Uredaj> ucitaniUredaji = fileDataStore.loadUredaji(proizvodjaci);
                uredaji.addAll(ucitaniUredaji);
            } else {
                generirajProizvodjace();
                generirajUredaje();
                fileDataStore.saveProizvodjaci(proizvodjaci);
                fileDataStore.saveUredaji(uredaji);
            }
        } catch (IOException e) {
            System.err.println("Greška pri radu s datotekama: " + e.getMessage());
            generirajProizvodjace();
            generirajUredaje();
        }
    }

    public FileDataStore getFileDataStore() {
        return fileDataStore;
    }

    private void generirajProizvodjace() {
        proizvodjaci.clear();
        proizvodjaci.add(new Proizvodjac(1L, "Dell", "SAD"));
        proizvodjaci.add(new Proizvodjac(2L, "HP", "SAD"));
        proizvodjaci.add(new Proizvodjac(3L, "Lenovo", "Kina"));
    }

    private void generirajUredaje() {
        uredaji.clear();

        Proizvodjac dell = proizvodjaci.get(0);
        Proizvodjac hp = proizvodjaci.get(1);
        Proizvodjac lenovo = proizvodjaci.get(2);

        uredaji.add(new Laptop(1L, "Dell XPS 15", 1500.0, 5, dell, 16, 512, "Intel i7"));
        uredaji.add(new Laptop(2L, "HP Pavilion", 900.0, 3, hp, 8, 256, "Intel i5"));

        uredaji.add(new Monitor(3L, "Dell UltraSharp 24", 300.0, 10, dell, 24.0, "1920x1080"));
        uredaji.add(new Monitor(4L, "Lenovo ThinkVision 27", 400.0, 4, lenovo, 27.0, "2560x1440"));

        uredaji.add(new Tipkovnica(5L, "HP žična tipkovnica", 25.0, 20, hp, false, "HR"));
        uredaji.add(new Tipkovnica(6L, "Lenovo mehanička", 80.0, 7, lenovo, true, "US"));
    }

    public List<Proizvodjac> getProizvodjaci() {
        return proizvodjaci;
    }

    public List<Uredaj> getUredaji() {
        return uredaji;
    }

    public void persist() throws IOException {
        fileDataStore.saveProizvodjaci(proizvodjaci);
        fileDataStore.saveUredaji(uredaji);
    }
}
