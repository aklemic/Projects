package com.example.filmmanager;

import javafx.concurrent.Task;

import java.io.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * DataManager odgovoran je za pohranu i učitavanje podataka o glumcima,
 * žanrovima i filmovima u/z datoteku koristeći serijalizaciju.
 * Također pruža asinhrone metode za rad u pozadinskoj niti (JavaFX Task).
 */
public class DataManager {

    private static final String DATA_FILE = "podaci.ser";

    /**
     * Asinhrona metoda za spremanje podataka u datoteku u zasebnoj niti.
     * @param glumci lista glumaca za spremanje
     * @param žanrovi lista žanrova za spremanje
     * @param filmovi lista filmova za spremanje
     * @param onFinished callback koji prima true ako je spremanje uspješno, false ako nije
     */
    public static void saveDataAsync(List<Glumac> glumci, List<Žanr> žanrovi, List<Film> filmovi, Consumer<Boolean> onFinished) {
        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
                    oos.writeObject(glumci);
                    oos.writeObject(žanrovi);
                    oos.writeObject(filmovi);
                }
                return null;
            }
        };
        saveTask.setOnSucceeded(ev -> {
            if (onFinished != null) onFinished.accept(true);
        });
        saveTask.setOnFailed(ev -> {
            if (onFinished != null) onFinished.accept(false);
        });
        new Thread(saveTask).start();
    }

    /**
     * Asinhrona metoda za učitavanje podataka iz datoteke u zasebnoj niti.
     * @param onSuccess callback koji prima učitane podatke ako je učitavanje uspješno
     * @param onError callback koji prima iznimku ako dođe do greške pri učitavanju
     */
    public static void loadDataAsync(Consumer<DataBundle> onSuccess, Consumer<Exception> onError) {
        Task<DataBundle> loadTask = new Task<>() {
            @Override
            protected DataBundle call() throws Exception {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
                    List<Glumac> glumci = (List<Glumac>) ois.readObject();
                    List<Žanr> žanrovi = (List<Žanr>) ois.readObject();
                    List<Film> filmovi = (List<Film>) ois.readObject();
                    return new DataBundle(glumci, žanrovi, filmovi);
                }
            }
        };
        loadTask.setOnSucceeded(ev -> {
            if (onSuccess != null) onSuccess.accept(loadTask.getValue());
        });
        loadTask.setOnFailed(ev -> {
            if (onError != null) onError.accept(new Exception(loadTask.getException()));
        });
        new Thread(loadTask).start();
    }

    /**
     * Sinkrona metoda za spremanje podataka u datoteku (blokirajući poziv).
     * @param glumci lista glumaca
     * @param žanrovi lista žanrova
     * @param filmovi lista filmova
     * @throws IOException u slučaju greške u pisanju datoteke
     */
    public static void saveData(List<Glumac> glumci, List<Žanr> žanrovi, List<Film> filmovi) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(glumci);
            oos.writeObject(žanrovi);
            oos.writeObject(filmovi);
        }
    }

    /**
     * Sinkrona metoda za učitavanje podataka iz datoteke.
     * @return DataBundle koji sadrži liste glumaca, žanrova i filmova
     * @throws IOException u slučaju greške pri čitanju datoteke
     * @throws ClassNotFoundException ako se ne može pronaći izvorni tip objekta
     */
    @SuppressWarnings("unchecked")
    public static DataBundle loadData() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            List<Glumac> glumci = (List<Glumac>) ois.readObject();
            List<Žanr> žanrovi = (List<Žanr>) ois.readObject();
            List<Film> filmovi = (List<Film>) ois.readObject();
            return new DataBundle(glumci, žanrovi, filmovi);
        }
    }

    /**
     * DataBundle služi kao kontejner za grupiranje svih glavnih podataka.
     */
    public static class DataBundle {
        public final List<Glumac> glumci;
        public final List<Žanr> žanrovi;
        public final List<Film> filmovi;

        public DataBundle(List<Glumac> glumci, List<Žanr> žanrovi, List<Film> filmovi) {
            this.glumci = glumci;
            this.žanrovi = žanrovi;
            this.filmovi = filmovi;
        }
    }
}
