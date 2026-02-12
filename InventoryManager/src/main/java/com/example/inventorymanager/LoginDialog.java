package com.example.inventorymanager;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class LoginDialog extends Dialog<Boolean> {

    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();

    public LoginDialog() {
        setTitle("Prijava");

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        grid.addRow(0, new Label("Korisničko ime:"), usernameField);
        grid.addRow(1, new Label("Lozinka:"), passwordField);

        getDialogPane().setContent(grid);

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String username = usernameField.getText();
                String password = passwordField.getText();

                // Hardkodirane vjerodajnice za projekt
                return "admin".equals(username) && "lozinka".equals(password);
            }
            return false;
        });
    }
}
