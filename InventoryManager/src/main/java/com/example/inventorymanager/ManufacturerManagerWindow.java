package com.example.inventorymanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ManufacturerManagerWindow {

    private final ProizvodjacRepository proizvodjacRepository;

    private final ObservableList<Proizvodjac> data = FXCollections.observableArrayList();
    private TableView<Proizvodjac> table;

    public ManufacturerManagerWindow(ProizvodjacRepository proizvodjacRepository) {
        this.proizvodjacRepository = proizvodjacRepository;
    }

    public void show(Stage owner) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Proizvođači");

        BorderPane root = new BorderPane();

        // tablica
        table = new TableView<>();
        configureTable();
        root.setCenter(table);

        // gumbi
        Button refreshButton = new Button("Osvježi iz baze");
        Button addButton = new Button("Dodaj");
        Button editButton = new Button("Uredi");
        Button deleteButton = new Button("Obriši");

        refreshButton.setOnAction(e -> loadFromDb());
        addButton.setOnAction(e -> openEditDialog(null));
        editButton.setOnAction(e -> {
            Proizvodjac sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                openEditDialog(sel);
            }
        });
        deleteButton.setOnAction(e -> {
            Proizvodjac sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                proizvodjacRepository.deleteById(sel.getId());
                proizvodjacRepository.deleteFromDb(sel.getId());
                loadFromDb();
            }
        });

        HBox buttons = new HBox(10, refreshButton, addButton, editButton, deleteButton);
        buttons.setPadding(new Insets(10));
        root.setBottom(buttons);

        // početno punjenje iz baze
        loadFromDb();

        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    private void configureTable() {
        TableColumn<Proizvodjac, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Proizvodjac, String> nazivCol = new TableColumn<>("Naziv");
        nazivCol.setCellValueFactory(new PropertyValueFactory<>("naziv"));

        TableColumn<Proizvodjac, String> drzavaCol = new TableColumn<>("Država");
        drzavaCol.setCellValueFactory(new PropertyValueFactory<>("drzava"));

        table.getColumns().addAll(idCol, nazivCol, drzavaCol);
        table.setItems(data);
    }

    private void loadFromDb() {
        proizvodjacRepository.loadAllFromDb();
        data.setAll(proizvodjacRepository.findAll());
    }

    private void openEditDialog(Proizvodjac postojeći) {
        Dialog<Proizvodjac> dialog = new Dialog<>();
        dialog.setTitle(postojeći == null ? "Dodaj proizvođača" : "Uredi proizvođača");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField idField = new TextField();
        TextField nazivField = new TextField();
        TextField drzavaField = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        grid.addRow(0, new Label("ID:"), idField);
        grid.addRow(1, new Label("Naziv:"), nazivField);
        grid.addRow(2, new Label("Država:"), drzavaField);

        if (postojeći != null) {
            idField.setText(postojeći.getId().toString());
            idField.setDisable(true); // ID se ne mijenja kod editiranja
            nazivField.setText(postojeći.getNaziv());
            drzavaField.setText(postojeći.getDrzava());
        }

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Long id = Long.parseLong(idField.getText());
                String naziv = nazivField.getText();
                String drzava = drzavaField.getText();
                return new Proizvodjac(id, naziv, drzava);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(proiz -> {
            // spremi u memoriju i bazu
            proizvodjacRepository.save(proiz);
            proizvodjacRepository.saveToDb(proiz);
            loadFromDb();
        });
    }
}
