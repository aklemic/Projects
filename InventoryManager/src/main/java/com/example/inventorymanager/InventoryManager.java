package com.example.inventorymanager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class InventoryManager extends Application {

    private InMemoryDataSource dataSource;
    private UredajRepository uredajRepository;
    private ProizvodjacRepository proizvodjacRepository;
    private javafx.scene.control.TextField filterField;

    private TableView<Uredaj> uredajTable;

    @Override
    public void start(Stage stage) {

        LoginDialog loginDialog = new LoginDialog();
        Boolean loggedIn = loginDialog.showAndWait().orElse(false);
        if (!loggedIn) {
            stage.close();
            return;
        }

        dataSource = new InMemoryDataSource();
        uredajRepository = new UredajRepository(dataSource);
        proizvodjacRepository = new ProizvodjacRepository(dataSource);

        // Početna sinkronizacija proizvođača u SQLite bazu
        proizvodjacRepository.syncAllToDb();

        BorderPane root = new BorderPane();

        Label titleLabel = new Label("Inventory Manager - Uređaji");

        filterField = new javafx.scene.control.TextField();
        filterField.setPromptText("Filtriraj po nazivu ili proizvođaču...");

        // promjena layouta: naslov + filter u istom redu
        HBox topBox = new HBox(10, titleLabel, filterField);
        topBox.setSpacing(10);
        root.setTop(topBox);

        uredajTable = new TableView<>();
        configureUredajTable();
        root.setCenter(uredajTable);

        filterField.textProperty().addListener((obs, oldValue, newValue) -> applyFilter());

        Button addButton = new Button("Dodaj");
        Button editButton = new Button("Uredi");
        Button deleteButton = new Button("Obriši");
        Button exportButton = new Button("Export");
        Button manufacturersButton = new Button("Proizvođači");

        addButton.setOnAction(event -> {
            UredajFormDialog dialog = new UredajFormDialog(proizvodjacRepository);
            dialog.showAndWait().ifPresent(uredaj -> {
                uredajRepository.save(uredaj);
                loadUredaji();
                try {
                    dataSource.persist();
                } catch (IOException e) {
                    System.err.println("Greška pri spremanju podataka: " + e.getMessage());
                }
            });
        });

        editButton.setOnAction(event -> {
            Uredaj odabrani = uredajTable.getSelectionModel().getSelectedItem();
            if (odabrani == null) {
                return;
            }

            UredajFormDialog dialog = new UredajFormDialog(proizvodjacRepository, odabrani);
            dialog.showAndWait().ifPresent(uredaj -> {
                uredajRepository.save(uredaj);
                loadUredaji();
                try {
                    dataSource.persist();
                } catch (IOException e) {
                    System.err.println("Greška pri spremanju podataka: " + e.getMessage());
                }
            });
        });

        deleteButton.setOnAction(event -> {
            Uredaj odabrani = uredajTable.getSelectionModel().getSelectedItem();
            if (odabrani != null) {
                uredajRepository.deleteById(odabrani.getId());
                loadUredaji();
                try {
                    dataSource.persist();
                } catch (IOException e) {
                    System.err.println("Greška pri spremanju podataka: " + e.getMessage());
                }
            }
        });

        exportButton.setOnAction(event -> {
            try {
                dataSource.persist(); // osiguramo da su podaci ažurni
                dataSource.getFileDataStore().exportUredaji(uredajRepository.findAll());
                showInfo("Export uspješno dovršen u datoteku 'export_uredaji.txt'.");
            } catch (IOException e) {
                System.err.println("Greška pri exportu podataka: " + e.getMessage());
                showError("Greška pri exportu podataka: " + e.getMessage());
            }
        });

        manufacturersButton.setOnAction(event -> {
            ManufacturerManagerWindow window = new ManufacturerManagerWindow(proizvodjacRepository);
            window.show(stage);
        });

        HBox bottomBox = new HBox(10, addButton, editButton, deleteButton, exportButton, manufacturersButton);
        bottomBox.setSpacing(10);
        root.setBottom(bottomBox);

        loadUredaji();

        Scene scene = new Scene(root, 900, 600);
        stage.setTitle("Inventory Manager");
        stage.setScene(scene);
        stage.show();
    }

    private void configureUredajTable() {
        TableColumn<Uredaj, Long> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Uredaj, String> nazivColumn = new TableColumn<>("Naziv");
        nazivColumn.setCellValueFactory(new PropertyValueFactory<>("naziv"));

        TableColumn<Uredaj, Double> cijenaColumn = new TableColumn<>("Cijena");
        cijenaColumn.setCellValueFactory(new PropertyValueFactory<>("cijena"));

        TableColumn<Uredaj, Integer> kolicinaColumn = new TableColumn<>("Količina");
        kolicinaColumn.setCellValueFactory(new PropertyValueFactory<>("kolicina"));

        TableColumn<Uredaj, String> proizvodjacColumn = new TableColumn<>("Proizvođač");
        proizvodjacColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createObjectBinding(
                        () -> cellData.getValue().getProizvodjac() != null
                                ? cellData.getValue().getProizvodjac().getNaziv()
                                : ""
                )
        );

        uredajTable.getColumns().addAll(
                idColumn, nazivColumn, cijenaColumn, kolicinaColumn, proizvodjacColumn
        );
    }

    private void loadUredaji() {
        // ako nema filtera ili je prazan → svi uređaji
        if (filterField == null || filterField.getText() == null || filterField.getText().isEmpty()) {
            uredajTable.getItems().setAll(uredajRepository.findAll());
        } else {
            // primijeni trenutni filter
            applyFilter();
        }
    }

    private void applyFilter() {
        String filterText = filterField.getText();
        if (filterText == null) {
            filterText = "";
        }
        String lowerFilter = filterText.toLowerCase();

        // ako je filter prazan, prikazujemo sve
        if (lowerFilter.isEmpty()) {
            uredajTable.getItems().setAll(uredajRepository.findAll());
            return;
        }

        // filtriramo listu iz repozitorija
        var filtrirani = uredajRepository.findAll().stream()
                .filter(u -> {
                    String naziv = u.getNaziv() != null ? u.getNaziv().toLowerCase() : "";
                    String proizNaziv = "";
                    if (u.getProizvodjac() != null && u.getProizvodjac().getNaziv() != null) {
                        proizNaziv = u.getProizvodjac().getNaziv().toLowerCase();
                    }
                    return naziv.contains(lowerFilter) || proizNaziv.contains(lowerFilter);
                })
                .toList();

        uredajTable.getItems().setAll(filtrirani);
    }

    private void showInfo(String message) {
        javafx.scene.control.Alert alert =
                new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Informacija");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        javafx.scene.control.Alert alert =
                new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Greška");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
