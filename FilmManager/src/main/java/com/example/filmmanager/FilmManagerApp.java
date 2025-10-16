package com.example.filmmanager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Glavna JavaFX aplikacija za upravljanje filmovima, glumcima i žanrovima.
 * Puni property binding koristi se za ažuriranje UI elemenata u realnom vremenu.
 * Omogućuje CRUD operacije, filtriranje, te CSV uvoz/izvoz.
 */
public class FilmManagerApp extends Application {

    // Observable liste podataka koje JavaFX UI prati za promjene
    private ObservableList<Glumac> glumci;
    private ObservableList<Žanr> žanrovi;
    private ObservableList<Film> filmovi;

    // ComboBox kontrole za odabir glumaca i žanrova kod dodavanja filmova
    private ComboBox<Glumac> cbGlumac;
    private ComboBox<Žanr> cbŽanr;

    /**
     * Metoda start pokreće aplikaciju prikazom login dijaloga.
     * @param primaryStage primarni prozor
     */
    @Override
    public void start(Stage primaryStage) {
        showLoginDialog(primaryStage);
    }

    /**
     * Prikazuje modalni login dijalog prije glavne aplikacije.
     * Korisnička autentifikacija je hardkodirana s "admin" i "admin".
     * @param owner vlasnik dijaloga
     */
    private void showLoginDialog(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Prijava");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        Label lblUser = new Label("Korisničko ime:");
        TextField tfUser = new TextField();
        Label lblPass = new Label("Lozinka:");
        PasswordField pfPass = new PasswordField();
        Button btnLogin = new Button("Prijava");
        Button btnCancel = new Button("Odustani");

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: red;");

        grid.add(lblUser, 0, 0);
        grid.add(tfUser, 1, 0);
        grid.add(lblPass, 0, 1);
        grid.add(pfPass, 1, 1);
        grid.add(btnLogin, 0, 2);
        grid.add(btnCancel, 1, 2);
        grid.add(lblError, 0, 3, 2, 1);

        btnLogin.setOnAction(e -> {
            String user = tfUser.getText().trim();
            String pass = pfPass.getText();

            if (user.equals("admin") && pass.equals("admin")) {
                dialog.close();
                Platform.runLater(this::showMainApp);
            } else {
                lblError.setText("Neispravno korisničko ime ili lozinka.");
            }
        });

        btnCancel.setOnAction(e -> Platform.exit());

        Scene scene = new Scene(grid, 330, 180);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.show();
    }

    /**
     * Glavni prikaz aplikacije - sadrži tabove za Glumce, Žanrove i Filmove.
     * Svaki tab ima svoju tablicu, kontrolere za filtriranje, dodavanje i uređivanje.
     */
    private void showMainApp() {
        // Inicijalizacija ObservableList podataka iz početnih podataka
        glumci = FXCollections.observableArrayList(GenerirajPodatke.generirajGlumce());
        žanrovi = FXCollections.observableArrayList(GenerirajPodatke.generirajŽanrove());
        filmovi = FXCollections.observableArrayList(GenerirajPodatke.generirajFilmove(glumci, žanrovi));

        TabPane tabPane = new TabPane();

        // --- Tab Glumci ---
        Tab tabGlumci = new Tab("Glumci");
        FilteredList<Glumac> filteredGlumci = new FilteredList<>(glumci, p -> true);
        TableView<Glumac> tvGlumci = new TableView<>(filteredGlumci);

        // Polje za filtriranje glumaca po imenu/prezimenu
        TextField tfSearchGlumci = new TextField();
        tfSearchGlumci.setPromptText("Pretraži glumce...");
        tfSearchGlumci.textProperty().addListener((obs, oldVal, newVal) -> {
            final String lowerVal = newVal == null ? "" : newVal.toLowerCase();
            filteredGlumci.setPredicate(glumac -> {
                if (lowerVal.isEmpty()) return true;
                return glumac.getImePrezime().toLowerCase().contains(lowerVal);
            });
        });

        // Definicija stupaca s property vezama za automatsko ažuriranje
        TableColumn<Glumac, String> colIme = new TableColumn<>("Ime");
        colIme.setCellValueFactory(cell -> cell.getValue().imeProperty());

        TableColumn<Glumac, String> colPrezime = new TableColumn<>("Prezime");
        colPrezime.setCellValueFactory(cell -> cell.getValue().prezimeProperty());

        TableColumn<Glumac, Number> colGodinaRodjenja = new TableColumn<>("Godina rođenja");
        colGodinaRodjenja.setCellValueFactory(cell -> cell.getValue().godinaRodjenjaProperty());

        TableColumn<Glumac, String> colSpol = new TableColumn<>("Spol");
        colSpol.setCellValueFactory(cell -> cell.getValue().spolProperty());

        tvGlumci.getColumns().addAll(colIme, colPrezime, colGodinaRodjenja, colSpol);
        tvGlumci.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Polja za unos novog glumca
        TextField tfIme = new TextField();
        tfIme.setPromptText("Ime");
        TextField tfPrezime = new TextField();
        tfPrezime.setPromptText("Prezime");
        TextField tfGodina = new TextField();
        tfGodina.setPromptText("Godina rođenja");
        ComboBox<String> cbSpol = new ComboBox<>();
        cbSpol.getItems().addAll("M", "Ž");
        cbSpol.setPromptText("Spol");

        Button btnDodajGlumca = new Button("Dodaj glumca");
        Button btnObrisiGlumca = new Button("Obriši odabranog");

        HBox hboxGlumci = new HBox(10, tfIme, tfPrezime, tfGodina, cbSpol, btnDodajGlumca, btnObrisiGlumca);
        hboxGlumci.setPadding(new Insets(10));

        VBox vboxGlumci = new VBox(10, tfSearchGlumci, tvGlumci, hboxGlumci);
        tabGlumci.setContent(vboxGlumci);
        tabGlumci.setClosable(false);

        // Dodavanje novog glumca uz validaciju
        btnDodajGlumca.setOnAction(ev -> {
            String ime = tfIme.getText().trim();
            String prezime = tfPrezime.getText().trim();
            String godinaStr = tfGodina.getText().trim();
            String spol = cbSpol.getValue();

            if (ime.isEmpty() || prezime.isEmpty() || godinaStr.isEmpty() || spol == null) {
                showAlert("Greška", "Sve polja moraju biti ispunjena.", Alert.AlertType.ERROR);
                return;
            }
            try {
                int godina = Integer.parseInt(godinaStr);
                Glumac novi = new Glumac(ime, prezime, godina, spol);
                glumci.add(novi);
                tfIme.clear();
                tfPrezime.clear();
                tfGodina.clear();
                cbSpol.setValue(null);
            } catch (NumberFormatException ex) {
                showAlert("Greška", "Godina rođenja mora biti cijeli broj.", Alert.AlertType.ERROR);
            }
        });

        // Brisanje selektiranog glumca
        btnObrisiGlumca.setOnAction(ev -> {
            Glumac selected = tvGlumci.getSelectionModel().getSelectedItem();
            if (selected != null) {
                glumci.remove(selected);
            }
        });

        // Uređivanje glumca kroz dvostruki klik na redak
        tvGlumci.setRowFactory(tv -> {
            TableRow<Glumac> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    showEditGlumacDialog(row.getItem());
                }
            });
            return row;
        });

        // --- Tab Žanrovi ---
        Tab tabŽanrovi = new Tab("Žanrovi");
        FilteredList<Žanr> filteredŽanrovi = new FilteredList<>(žanrovi, p -> true);
        TableView<Žanr> tvŽanrovi = new TableView<>(filteredŽanrovi);

        TextField tfSearchŽanrovi = new TextField();
        tfSearchŽanrovi.setPromptText("Pretraži žanrove...");
        tfSearchŽanrovi.textProperty().addListener((obs, oldVal, newVal) -> {
            final String lowerVal = newVal == null ? "" : newVal.toLowerCase();
            filteredŽanrovi.setPredicate(žanr -> {
                if (lowerVal.isEmpty()) return true;
                return žanr.getNaziv().toLowerCase().contains(lowerVal);
            });
        });

        TableColumn<Žanr, String> colNaziv = new TableColumn<>("Naziv");
        colNaziv.setCellValueFactory(cell -> cell.getValue().nazivProperty());

        tvŽanrovi.getColumns().add(colNaziv);
        tvŽanrovi.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TextField tfNazivŽanra = new TextField();
        tfNazivŽanra.setPromptText("Naziv žanra");
        Button btnDodajŽanr = new Button("Dodaj žanr");
        Button btnObrisiŽanr = new Button("Obriši odabrani");

        HBox hboxŽanrovi = new HBox(10, tfNazivŽanra, btnDodajŽanr, btnObrisiŽanr);
        hboxŽanrovi.setPadding(new Insets(10));

        VBox vboxŽanrovi = new VBox(10, tfSearchŽanrovi, tvŽanrovi, hboxŽanrovi);
        tabŽanrovi.setContent(vboxŽanrovi);
        tabŽanrovi.setClosable(false);

        btnDodajŽanr.setOnAction(ev -> {
            String naziv = tfNazivŽanra.getText().trim();
            if (naziv.isEmpty()) {
                showAlert("Greška", "Naziv žanra ne može biti prazan.", Alert.AlertType.ERROR);
                return;
            }
            Žanr novi = new Žanr(naziv);
            žanrovi.add(novi);
            tfNazivŽanra.clear();
        });

        btnObrisiŽanr.setOnAction(ev -> {
            Žanr selected = tvŽanrovi.getSelectionModel().getSelectedItem();
            if (selected != null) {
                žanrovi.remove(selected);
            }
        });

        tvŽanrovi.setRowFactory(tv -> {
            TableRow<Žanr> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    showEditŽanrDialog(row.getItem());
                }
            });
            return row;
        });

        // --- Tab Filmovi ---
        Tab tabFilmovi = new Tab("Filmovi");
        FilteredList<Film> filteredFilmovi = new FilteredList<>(filmovi, p -> true);
        TableView<Film> tvFilmovi = new TableView<>(filteredFilmovi);

        TextField tfSearchFilmovi = new TextField();
        tfSearchFilmovi.setPromptText("Pretraži filmove...");
        tfSearchFilmovi.textProperty().addListener((obs, oldVal, newVal) -> {
            final String lowerVal = newVal == null ? "" : newVal.toLowerCase();
            filteredFilmovi.setPredicate(film -> {
                if (lowerVal.isEmpty()) return true;
                boolean naslovMatch = film.getNaslov().toLowerCase().contains(lowerVal);
                boolean glumacMatch = film.getGlumac().getImePrezime().toLowerCase().contains(lowerVal);
                boolean žanrMatch = film.getŽanr().getNaziv().toLowerCase().contains(lowerVal);
                return naslovMatch || glumacMatch || žanrMatch;
            });
        });

        TableColumn<Film, String> colNaslov = new TableColumn<>("Naslov");
        colNaslov.setCellValueFactory(cell -> cell.getValue().naslovProperty());

        TableColumn<Film, Number> colGodina = new TableColumn<>("Godina");
        colGodina.setCellValueFactory(cell -> cell.getValue().godinaProperty());

        TableColumn<Film, String> colZanr = new TableColumn<>("Žanr");
        colZanr.setCellValueFactory(cell -> cell.getValue().getŽanr().nazivProperty());

        TableColumn<Film, String> colGlumac = new TableColumn<>("Glumac");
        colGlumac.setCellValueFactory(cell -> cell.getValue().getGlumac().imeProperty()
                .concat(new SimpleStringProperty(" "))
                .concat(cell.getValue().getGlumac().prezimeProperty()));

        tvFilmovi.getColumns().addAll(colNaslov, colGodina, colZanr, colGlumac);
        tvFilmovi.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TextField tfNaslovF = new TextField();
        tfNaslovF.setPromptText("Naslov");
        TextField tfGodinaF = new TextField();
        tfGodinaF.setPromptText("Godina");

        cbŽanr = new ComboBox<>(žanrovi);
        cbGlumac = new ComboBox<>(glumci);

        Button btnDodajFilm = new Button("Dodaj film");
        Button btnObrisiFilm = new Button("Obriši odabrani film");
        Button btnIzvozCSV = new Button("Izvoz CSV");
        Button btnUvozCSV = new Button("Uvoz CSV");

        HBox hboxFilmoviControls = new HBox(10, tfNaslovF, tfGodinaF, cbŽanr, cbGlumac, btnDodajFilm, btnObrisiFilm);
        hboxFilmoviControls.setPadding(new Insets(10));

        HBox hboxCsvButtons = new HBox(10, btnIzvozCSV, btnUvozCSV);
        hboxCsvButtons.setPadding(new Insets(10));

        VBox vboxFilmovi = new VBox(10, tfSearchFilmovi, tvFilmovi, hboxFilmoviControls, hboxCsvButtons);
        tabFilmovi.setContent(vboxFilmovi);
        tabFilmovi.setClosable(false);

        btnDodajFilm.setOnAction(ev -> {
            String naslov = tfNaslovF.getText().trim();
            String godinaStr = tfGodinaF.getText().trim();
            Žanr žanr = cbŽanr.getValue();
            Glumac glumac = cbGlumac.getValue();

            if (naslov.isEmpty() || godinaStr.isEmpty() || žanr == null || glumac == null) {
                showAlert("Greška", "Ispunite sve podatke!", Alert.AlertType.ERROR);
                return;
            }
            try {
                int godina = Integer.parseInt(godinaStr);
                Film novi = new Film(naslov, godina, žanr, glumac);
                filmovi.add(novi);
                tfNaslovF.clear();
                tfGodinaF.clear();
                cbŽanr.setValue(null);
                cbGlumac.setValue(null);
            } catch (NumberFormatException ex) {
                showAlert("Greška", "Godina mora biti broj!", Alert.AlertType.ERROR);
            }
        });

        btnObrisiFilm.setOnAction(ev -> {
            Film selected = tvFilmovi.getSelectionModel().getSelectedItem();
            if (selected != null) {
                filmovi.remove(selected);
            }
        });

        btnIzvozCSV.setOnAction(ev -> izvozCSV());
        btnUvozCSV.setOnAction(ev -> uvozCSV());

        tvFilmovi.setRowFactory(tv -> {
            TableRow<Film> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    showEditFilmDialog(row.getItem());
                }
            });
            return row;
        });

        tabPane.getTabs().addAll(tabGlumci, tabŽanrovi, tabFilmovi);
        BorderPane root = new BorderPane(tabPane);
        Scene scene = new Scene(root, 1000, 700);

        Stage mainStage = new Stage();
        mainStage.setTitle("Film Manager");
        mainStage.setScene(scene);
        mainStage.show();
    }


    /**
     * Metoda za izvoz svih filmova u CSV datoteku filmovi_export.csv.
     * Dodaje UTF-8 BOM za ispravnu korektnu obradu kodova i znakova u Excelu.
     */
    private void izvozCSV() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("naslov,godina,zanr,glumac_ime,glumac_prezime,glumac_godina,glumac_spol\n");
            for (Film f : filmovi) {
                sb.append(String.format("\"%s\",%d,\"%s\",\"%s\",\"%s\",%d,\"%s\"\n",
                        f.getNaslov(), f.getGodina(), f.getŽanr().getNaziv(),
                        f.getGlumac().getIme(), f.getGlumac().getPrezime(),
                        f.getGlumac().getGodinaRodjenja(), f.getGlumac().getSpol()));
            }
            byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            byte[] data = sb.toString().getBytes("UTF-8");
            byte[] dataWithBom = new byte[bom.length + data.length];
            System.arraycopy(bom, 0, dataWithBom, 0, bom.length);
            System.arraycopy(data, 0, dataWithBom, bom.length, data.length);

            Files.write(Paths.get("filmovi_export.csv"), dataWithBom);

            showAlert("Izvoz CSV", "Podaci uspješno izvezeni u 'filmovi_export.csv'.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Greška pri izvozu", "Nije moguće izvesti podatke: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Metoda za uvoz podataka iz CSV datoteke filmovi_export.csv.
     * Izvršava validaciju, popunjavanje postojećih i kreiranje novih entiteta.
     */
    private void uvozCSV() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("filmovi_export.csv"));
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] cells = parseCSVLine(line);
                if (cells.length < 7)
                    continue;

                String naslov = cells[0].replace("\"", "");
                int godina = Integer.parseInt(cells[1].replace("\"", ""));
                String zanrNaziv = cells[2].replace("\"", "");
                String imeGlumca = cells[3].replace("\"", "");
                String prezimeGlumca = cells[4].replace("\"", "");

                String spolRaw = cells[6].replace("\"", "").trim();
                String spol;
                if (spolRaw.equalsIgnoreCase("Å½") || spolRaw.equalsIgnoreCase("Ž")) {
                    spol = "Ž";
                } else if (spolRaw.equalsIgnoreCase("M")) {
                    spol = "M";
                } else {
                    spol = spolRaw;
                }

                int godRodjenja = Integer.parseInt(cells[5].replace("\"", ""));

                Žanr zanr = žanrovi.stream().
                        filter(z -> z.getNaziv().equalsIgnoreCase(zanrNaziv)).
                        findFirst().
                        orElseGet(() -> {
                            Žanr novi = new Žanr(zanrNaziv);
                            žanrovi.add(novi);
                            cbŽanr.getItems().add(novi);
                            return novi;
                        });

                Glumac glumac = glumci.stream().
                        filter(g -> g.getIme().equalsIgnoreCase(imeGlumca) && g.getPrezime().equalsIgnoreCase(prezimeGlumca)).
                        findFirst().
                        orElseGet(() -> {
                            Glumac novi = new Glumac(imeGlumca, prezimeGlumca, godRodjenja, spol);
                            glumci.add(novi);
                            cbGlumac.getItems().add(novi);
                            return novi;
                        });

                Film noviFilm = new Film(naslov, godina, zanr, glumac);
                filmovi.add(noviFilm);
            }
            showAlert("Uvoz CSV", "Podaci uspješno učitani iz 'filmovi_export.csv'.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Greška pri uvozu", "Nije moguće učitati podatke: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Parsira redak CSV s obzirom na navodnike i zareze.
     * @param line redak CSV datoteke
     * @return niz polja iz retka
     */
    private String[] parseCSVLine(String line) {
        return line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
    }

    /**
     * Prikaz dijaloga za uređivanje podataka glumca.
     * UI elementi vezani na propertyje prve klase.
     * @param glumac entitet koji se uređuje
     */
    private void showEditGlumacDialog(Glumac glumac) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Uredi glumca");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField tfIme = new TextField(glumac.getIme());
        TextField tfPrezime = new TextField(glumac.getPrezime());
        TextField tfGodinaRodjenja = new TextField(String.valueOf(glumac.getGodinaRodjenja()));

        ComboBox<String> cbSpol = new ComboBox<>();
        cbSpol.getItems().addAll("M", "Ž");
        cbSpol.setValue(glumac.getSpol());

        grid.add(new Label("Ime:"), 0, 0);
        grid.add(tfIme, 1, 0);
        grid.add(new Label("Prezime:"), 0, 1);
        grid.add(tfPrezime, 1, 1);
        grid.add(new Label("Godina rođenja:"), 0, 2);
        grid.add(tfGodinaRodjenja, 1, 2);
        grid.add(new Label("Spol:"), 0, 3);
        grid.add(cbSpol, 1, 3);

        Button btnSave = new Button("Spremi");
        Button btnCancel = new Button("Odustani");

        HBox hBox = new HBox(10, btnSave, btnCancel);
        grid.add(hBox, 1, 4);

        btnSave.setOnAction(e -> {
            String ime = tfIme.getText().trim();
            String prezime = tfPrezime.getText().trim();
            String godinaStr = tfGodinaRodjenja.getText().trim();
            String spol = cbSpol.getValue();

            if (ime.isEmpty() || prezime.isEmpty() || godinaStr.isEmpty() || spol == null) {
                showAlert("Greška", "Ispunite sve podatke!", Alert.AlertType.ERROR);
                return;
            }
            try {
                int godina = Integer.parseInt(godinaStr);
                glumac.setIme(ime);
                glumac.setPrezime(prezime);
                glumac.setGodinaRodjenja(godina);
                glumac.setSpol(spol);
                dialog.close();
            } catch (NumberFormatException ex) {
                showAlert("Greška", "Godina mora biti broj!", Alert.AlertType.ERROR);
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        Scene scene = new Scene(grid, 400, 250);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    /**
     * Prikaz dijaloga za uređivanje žanra.
     * Direktno mijenja naziv u propertyju.
     */
    private void showEditŽanrDialog(Žanr žanr) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Uredi žanr");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField tfNaziv = new TextField(žanr.getNaziv());

        grid.add(new Label("Naziv žanra:"), 0, 0);
        grid.add(tfNaziv, 1, 0);

        Button btnSave = new Button("Spremi");
        Button btnCancel = new Button("Odustani");

        HBox hBox = new HBox(10, btnSave, btnCancel);
        grid.add(hBox, 1, 1);

        btnSave.setOnAction(e -> {
            String naziv = tfNaziv.getText().trim();
            if (naziv.isEmpty()) {
                showAlert("Greška", "Naziv ne može biti prazan!", Alert.AlertType.ERROR);
                return;
            }
            žanr.setNaziv(naziv);
            dialog.close();
        });

        btnCancel.setOnAction(e -> dialog.close());

        Scene scene = new Scene(grid, 350, 150);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    /**
     * Prikaz dijaloga za uređivanje filma.
     */
    private void showEditFilmDialog(Film film) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Uredi film");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setVgap(10);
        grid.setHgap(10);

        TextField tfNaslov = new TextField(film.getNaslov());
        TextField tfGodina = new TextField(String.valueOf(film.getGodina()));

        ComboBox<Žanr> cbŽanrEdit = new ComboBox<>();
        cbŽanrEdit.getItems().addAll(žanrovi);
        cbŽanrEdit.setValue(film.getŽanr());

        ComboBox<Glumac> cbGlumacEdit = new ComboBox<>();
        cbGlumacEdit.getItems().addAll(glumci);
        cbGlumacEdit.setValue(film.getGlumac());

        grid.add(new Label("Naslov:"), 0, 0);
        grid.add(tfNaslov, 1, 0);
        grid.add(new Label("Godina:"), 0, 1);
        grid.add(tfGodina, 1, 1);
        grid.add(new Label("Žanr:"), 0, 2);
        grid.add(cbŽanrEdit, 1, 2);
        grid.add(new Label("Glumac:"), 0, 3);
        grid.add(cbGlumacEdit, 1, 3);

        Button btnSave = new Button("Spremi");
        Button btnCancel = new Button("Odustani");
        HBox buttons = new HBox(10, btnSave, btnCancel);
        grid.add(buttons, 1, 4);

        btnSave.setOnAction(e -> {
            try {
                String naslov = tfNaslov.getText().trim();
                String godinaStr = tfGodina.getText().trim();
                Žanr žanr = cbŽanrEdit.getValue();
                Glumac glumac = cbGlumacEdit.getValue();

                if (naslov.isEmpty() || godinaStr.isEmpty() || žanr == null || glumac == null) {
                    showAlert("Greška", "Ispunite sve podatke!", Alert.AlertType.ERROR);
                    return;
                }

                int godina = Integer.parseInt(godinaStr);

                film.setNaslov(naslov);
                film.setGodina(godina);
                film.setŽanr(žanr);
                film.setGlumac(glumac);

                dialog.close();
            } catch (NumberFormatException ex) {
                showAlert("Greška", "Godina mora biti broj!", Alert.AlertType.ERROR);
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        Scene scene = new Scene(grid, 400, 250);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    /**
     * Pomoćna metoda za prikaz alert dijaloga s porukom.
     * @param title naslov dijaloga
     * @param message poruka korisniku
     * @param type tip alert (INFORMATION, ERROR, WARNING)
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Glavna metoda pokretanja JavaFX aplikacije.
     * @param args argumenti komandne linije (nisu korišteni)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
