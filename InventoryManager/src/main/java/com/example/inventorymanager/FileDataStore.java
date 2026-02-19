package com.example.inventorymanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileDataStore {

    private static final Logger LOGGER = Logger.getLogger(FileDataStore.class.getName());

    // Singleton instanca
    private static FileDataStore instance;

    public static FileDataStore getInstance() {
        if (instance == null) {
            instance = new FileDataStore();
        }
        return instance;
    }

    private final Path proizvodjaciFile = Path.of("proizvodjaci.txt");
    private final Path uredajiFile = Path.of("uredaji.txt");
    private final Path exportUredajiFile = Path.of("export_uredaji.txt");

    // Privatni konstruktor – nitko ne može raditi new FileDataStore izvana
    private FileDataStore() {
        LOGGER.info("FileDataStore singleton inicijaliziran.");
    }

    public boolean dataFilesExist() {
        boolean exist = Files.exists(proizvodjaciFile) && Files.exists(uredajiFile);
        LOGGER.info("Provjera postojanja datoteka (proizvodjaci/uredaji): " + exist);
        return exist;
    }

    public void saveProizvodjaci(List<Proizvodjac> proizvodjaci) throws IOException {
        LOGGER.info("Spremanje proizvođača u " + proizvodjaciFile.toAbsolutePath());
        try (BufferedWriter writer = Files.newBufferedWriter(proizvodjaciFile)) {
            for (Proizvodjac p : proizvodjaci) {
                String line = p.getId() + ";" + p.getNaziv() + ";" + p.getDrzava();
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Greška pri spremanju proizvođača", e);
            throw e;
        }
    }

    public List<Proizvodjac> loadProizvodjaci() throws IOException {
        LOGGER.info("Učitavanje proizvođača iz " + proizvodjaciFile.toAbsolutePath());
        List<Proizvodjac> result = new ArrayList<>();
        if (!Files.exists(proizvodjaciFile)) {
            LOGGER.warning("Datoteka proizvodjaci.txt ne postoji, vraćam praznu listu.");
            return result;
        }

        try (BufferedReader reader = Files.newBufferedReader(proizvodjaciFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                Long id = Long.parseLong(parts[0]);
                String naziv = parts[1];
                String drzava = parts[2];
                result.add(new Proizvodjac(id, naziv, drzava));
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Greška pri učitavanju proizvođača", e);
            throw e;
        }
        return result;
    }

    public void saveUredaji(List<Uredaj> uredaji) throws IOException {
        LOGGER.info("Spremanje uređaja u " + uredajiFile.toAbsolutePath());
        try (BufferedWriter writer = Files.newBufferedWriter(uredajiFile)) {
            for (Uredaj u : uredaji) {
                String tip;
                String p1 = "";
                String p2 = "";
                String p3 = "";

                if (u instanceof Laptop l) {
                    tip = "LAPTOP";
                    p1 = String.valueOf(l.getRamGB());
                    p2 = String.valueOf(l.getSsdGB());
                    p3 = l.getProcesor();
                } else if (u instanceof Monitor m) {
                    tip = "MONITOR";
                    p1 = String.valueOf(m.getDijagonalaInch());
                    p2 = m.getRezolucija();
                } else if (u instanceof Tipkovnica t) {
                    tip = "TIPKOVNICA";
                    p1 = String.valueOf(t.isMehanicka());
                    p2 = t.getLayout();
                } else {
                    LOGGER.warning("Nepoznat tip uređaja, preskačem zapis: " + u);
                    continue;
                }

                Long proizvodjacId = u.getProizvodjac() != null ? u.getProizvodjac().getId() : -1L;

                String line = u.getId() + ";" + tip + ";" + u.getNaziv() + ";" +
                        u.getCijena() + ";" + u.getKolicina() + ";" + proizvodjacId + ";" +
                        p1 + ";" + p2 + ";" + p3;

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Greška pri spremanju uređaja", e);
            throw e;
        }
    }

    public List<Uredaj> loadUredaji(List<Proizvodjac> proizvodjaci) throws IOException {
        LOGGER.info("Učitavanje uređaja iz " + uredajiFile.toAbsolutePath());
        List<Uredaj> result = new ArrayList<>();
        if (!Files.exists(uredajiFile)) {
            LOGGER.warning("Datoteka uredaji.txt ne postoji, vraćam praznu listu.");
            return result;
        }

        try (BufferedReader reader = Files.newBufferedReader(uredajiFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                Long id = Long.parseLong(parts[0]);
                String tip = parts[1];
                String naziv = parts[2];
                double cijena = Double.parseDouble(parts[3]);
                int kolicina = Integer.parseInt(parts[4]);
                Long proizvodjacId = Long.parseLong(parts[5]);

                Proizvodjac proiz = proizvodjaci.stream()
                        .filter(p -> p.getId().equals(proizvodjacId))
                        .findFirst()
                        .orElse(null);

                String p1 = parts.length > 6 ? parts[6] : "";
                String p2 = parts.length > 7 ? parts[7] : "";
                String p3 = parts.length > 8 ? parts[8] : "";

                switch (tip) {
                    case "LAPTOP" -> {
                        int ram = Integer.parseInt(p1);
                        int ssd = Integer.parseInt(p2);
                        String proc = p3;
                        result.add(new Laptop(id, naziv, cijena, kolicina, proiz, ram, ssd, proc));
                    }
                    case "MONITOR" -> {
                        double dijagonala = Double.parseDouble(p1);
                        String rez = p2;
                        result.add(new Monitor(id, naziv, cijena, kolicina, proiz, dijagonala, rez));
                    }
                    case "TIPKOVNICA" -> {
                        boolean mehanicka = Boolean.parseBoolean(p1);
                        String layout = p2;
                        result.add(new Tipkovnica(id, naziv, cijena, kolicina, proiz, mehanicka, layout));
                    }
                    default -> LOGGER.warning("Nepoznat tip uređaja u datoteci: " + tip);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Greška pri učitavanju uređaja", e);
            throw e;
        }
        return result;
    }

    public void exportUredaji(List<Uredaj> uredaji) throws IOException {
        LOGGER.info("Export uređaja u " + exportUredajiFile.toAbsolutePath());
        try (BufferedWriter writer = Files.newBufferedWriter(exportUredajiFile)) {
            writer.write("ID;Tip;Naziv;Cijena;Kolicina;Proizvodjac;Detalji");
            writer.newLine();

            for (Uredaj u : uredaji) {
                String tip;
                String detalji;

                if (u instanceof Laptop l) {
                    tip = "Laptop";
                    detalji = "RAM=" + l.getRamGB() + "GB, SSD=" + l.getSsdGB()
                            + "GB, CPU=" + l.getProcesor();
                } else if (u instanceof Monitor m) {
                    tip = "Monitor";
                    detalji = "Dijagonala=" + m.getDijagonalaInch()
                            + "\", Rezolucija=" + m.getRezolucija();
                } else if (u instanceof Tipkovnica t) {
                    tip = "Tipkovnica";
                    detalji = "Mehanicka=" + t.isMehanicka()
                            + ", Layout=" + t.getLayout();
                } else {
                    tip = "Nepoznat";
                    detalji = "";
                }

                String proizvodjacNaziv =
                        u.getProizvodjac() != null ? u.getProizvodjac().getNaziv() : "";

                String line = u.getId() + ";" + tip + ";" + u.getNaziv() + ";"
                        + u.getCijena() + ";" + u.getKolicina() + ";"
                        + proizvodjacNaziv + ";" + detalji;

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Greška pri exportu uređaja", e);
            throw e;
        }
    }
}
