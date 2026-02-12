package com.example.inventorymanager;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class UredajFormDialog extends Dialog<Uredaj> {

    private final TextField idField = new TextField();
    private final TextField nazivField = new TextField();
    private final Spinner<Double> cijenaSpinner =
            new Spinner<>(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 100000, 1000, 50));
    private final Spinner<Integer> kolicinaSpinner =
            new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 1, 1));
    private final ChoiceBox<Proizvodjac> proizvodjacChoice = new ChoiceBox<>();
    private final ChoiceBox<String> tipUredajaChoice = new ChoiceBox<>();

    private final TextField dodatno1Field = new TextField();
    private final TextField dodatno2Field = new TextField();
    private final TextField dodatno3Field = new TextField();

    private final Label dodatno1Label = new Label("Polje 1:");
    private final Label dodatno2Label = new Label("Polje 2:");
    private final Label dodatno3Label = new Label("Polje 3:");

    public UredajFormDialog(ProizvodjacRepository proizvodjacRepository) {
        setTitle("Uređaj");

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        tipUredajaChoice.getItems().addAll("Laptop", "Monitor", "Tipkovnica");
        tipUredajaChoice.getSelectionModel().selectFirst();

        proizvodjacChoice.getItems().addAll(proizvodjacRepository.findAll());

        grid.addRow(0, new Label("ID:"), idField);
        grid.addRow(1, new Label("Naziv:"), nazivField);
        grid.addRow(2, new Label("Cijena:"), cijenaSpinner);
        grid.addRow(3, new Label("Količina:"), kolicinaSpinner);
        grid.addRow(4, new Label("Proizvođač:"), proizvodjacChoice);
        grid.addRow(5, new Label("Tip uređaja:"), tipUredajaChoice);
        grid.addRow(6, dodatno1Label, dodatno1Field);
        grid.addRow(7, dodatno2Label, dodatno2Field);
        grid.addRow(8, dodatno3Label, dodatno3Field);

        tipUredajaChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if ("Laptop".equals(newVal)) {
                dodatno1Label.setText("RAM (GB):");
                dodatno2Label.setText("SSD (GB):");
                dodatno3Label.setText("Procesor:");
                dodatno3Field.setDisable(false);
            } else if ("Monitor".equals(newVal)) {
                dodatno1Label.setText("Dijagonala (inch):");
                dodatno2Label.setText("Rezolucija:");
                dodatno3Label.setText("Nije potrebno");
                dodatno3Field.setDisable(true);
            } else if ("Tipkovnica".equals(newVal)) {
                dodatno1Label.setText("Mehanička (true/false):");
                dodatno2Label.setText("Layout (HR/US):");
                dodatno3Label.setText("Nije potrebno");
                dodatno3Field.setDisable(true);
            }
        });

        // inicijalno podešavanje labela prema default tipu (Laptop)
        dodatno1Label.setText("RAM (GB):");
        dodatno2Label.setText("SSD (GB):");
        dodatno3Label.setText("Procesor:");
        dodatno3Field.setDisable(false);

        getDialogPane().setContent(grid);

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Long id = Long.parseLong(idField.getText());
                String naziv = nazivField.getText();
                double cijena = cijenaSpinner.getValue();
                int kolicina = kolicinaSpinner.getValue();
                Proizvodjac proiz = proizvodjacChoice.getValue();

                String tip = tipUredajaChoice.getValue();

                switch (tip) {
                    case "Laptop":
                        int ram = Integer.parseInt(dodatno1Field.getText());
                        int ssd = Integer.parseInt(dodatno2Field.getText());
                        String proc = dodatno3Field.getText();
                        return new Laptop(id, naziv, cijena, kolicina, proiz, ram, ssd, proc);
                    case "Monitor":
                        double dijagonala = Double.parseDouble(dodatno1Field.getText());
                        String rez = dodatno2Field.getText();
                        return new Monitor(id, naziv, cijena, kolicina, proiz, dijagonala, rez);
                    case "Tipkovnica":
                        boolean mehanicka = Boolean.parseBoolean(dodatno1Field.getText());
                        String layout = dodatno2Field.getText();
                        return new Tipkovnica(id, naziv, cijena, kolicina, proiz, mehanicka, layout);
                    default:
                        return null;
                }
            }
            return null;
        });
    }

    public UredajFormDialog(ProizvodjacRepository proizvodjacRepository, Uredaj postojeći) {
        this(proizvodjacRepository);

        idField.setText(postojeći.getId().toString());
        nazivField.setText(postojeći.getNaziv());
        cijenaSpinner.getValueFactory().setValue(postojeći.getCijena());
        kolicinaSpinner.getValueFactory().setValue(postojeći.getKolicina());
        proizvodjacChoice.setValue(postojeći.getProizvodjac());

        if (postojeći instanceof Laptop laptop) {
            tipUredajaChoice.setValue("Laptop");
            dodatno1Field.setText(String.valueOf(laptop.getRamGB()));
            dodatno2Field.setText(String.valueOf(laptop.getSsdGB()));
            dodatno3Field.setText(laptop.getProcesor());
        } else if (postojeći instanceof Monitor monitor) {
            tipUredajaChoice.setValue("Monitor");
            dodatno1Field.setText(String.valueOf(monitor.getDijagonalaInch()));
            dodatno2Field.setText(monitor.getRezolucija());
        } else if (postojeći instanceof Tipkovnica tipkovnica) {
            tipUredajaChoice.setValue("Tipkovnica");
            dodatno1Field.setText(String.valueOf(tipkovnica.isMehanicka()));
            dodatno2Field.setText(tipkovnica.getLayout());
        }
    }

}
