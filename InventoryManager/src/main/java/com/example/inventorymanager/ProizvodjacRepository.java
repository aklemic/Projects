package com.example.inventorymanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProizvodjacRepository {

    private static final Logger LOGGER = Logger.getLogger(ProizvodjacRepository.class.getName());

    private final InMemoryDataSource dataSource;

    public ProizvodjacRepository(InMemoryDataSource dataSource) {
        this.dataSource = dataSource;
        createTableIfNotExists();
    }

    // ---------- In-memory operacije (kao prije) ----------

    public List<Proizvodjac> findAll() {
        return dataSource.getProizvodjaci();
    }

    public Optional<Proizvodjac> findById(Long id) {
        return dataSource.getProizvodjaci().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public void save(Proizvodjac proizvodjac) {
        List<Proizvodjac> proizvodjaci = dataSource.getProizvodjaci();

        findById(proizvodjac.getId()).ifPresentOrElse(
                postojeći -> {
                    int index = proizvodjaci.indexOf(postojeći);
                    proizvodjaci.set(index, proizvodjac);
                },
                () -> proizvodjaci.add(proizvodjac)
        );
    }

    public void deleteById(Long id) {
        List<Proizvodjac> proizvodjaci = dataSource.getProizvodjaci();
        proizvodjaci.removeIf(p -> p.getId().equals(id));
    }

    // ---------- JDBC dio: SQLite baza ----------

    private void createTableIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS proizvodjac (
                    id INTEGER PRIMARY KEY,
                    naziv TEXT NOT NULL,
                    drzava TEXT NOT NULL
                )
                """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            LOGGER.info("Tablica 'proizvodjac' je provjerena/kreirana u bazi.");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Greška pri kreiranju tablice 'proizvodjac'", e);
        }
    }

    public void saveToDb(Proizvodjac proizvodjac) {
        String insertOrReplace = """
                INSERT INTO proizvodjac (id, naziv, drzava)
                VALUES (?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    naziv = excluded.naziv,
                    drzava = excluded.drzava
                """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(insertOrReplace)) {

            ps.setLong(1, proizvodjac.getId());
            ps.setString(2, proizvodjac.getNaziv());
            ps.setString(3, proizvodjac.getDrzava());

            ps.executeUpdate();
            LOGGER.info("Proizvođač spremljen u bazu, ID=" + proizvodjac.getId());

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Greška pri spremanju proizvođača u bazu", e);
        }
    }

    public void deleteFromDb(Long id) {
        String sql = "DELETE FROM proizvodjac WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
            LOGGER.info("Proizvođač obrisan iz baze, ID=" + id);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Greška pri brisanju proizvođača iz baze", e);
        }
    }

    public void loadAllFromDb() {
        String sql = "SELECT id, naziv, drzava FROM proizvodjac";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<Proizvodjac> lista = dataSource.getProizvodjaci();
            lista.clear();

            while (rs.next()) {
                Long id = rs.getLong("id");
                String naziv = rs.getString("naziv");
                String drzava = rs.getString("drzava");
                lista.add(new Proizvodjac(id, naziv, drzava));
            }

            LOGGER.info("Učitano proizvođača iz baze: " + lista.size());

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Greška pri učitavanju proizvođača iz baze", e);
        }
    }

    /**
     * Pomoćna metoda: spremi sve trenutne proizvođače iz memorije u bazu.
     */
    public void syncAllToDb() {
        for (Proizvodjac p : dataSource.getProizvodjaci()) {
            saveToDb(p);
        }
        LOGGER.info("Sinkronizirani svi proizvođači iz memorije u bazu.");
    }
}
