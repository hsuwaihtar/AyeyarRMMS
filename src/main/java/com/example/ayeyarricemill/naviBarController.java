package com.example.ayeyarricemill;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class naviBarController {
    public static String loggedInUsername = "Unknown user";
    public static String loggedInRole = "admin";
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML
    private Label day;
    @FXML
    private Label dateCount;

    @FXML
    public void initialize() {
        displayCurrentDate();
        if (userNameLabel != null) {
            userNameLabel.setText(loggedInUsername);
        }
        if (userRoleLabel != null) {
            userRoleLabel.setText(loggedInRole);
        }
    }

    private void displayCurrentDate() {
        LocalDate today = LocalDate.now();

        DateTimeFormatter DayOfWeek = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH);
        String dayOfWeek = today.format(DayOfWeek);

        DateTimeFormatter DateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
        String dateFormat = today.format(DateFormat);

        if (day != null && dateCount != null) {
            day.setText(dayOfWeek);
            dateCount.setText(dateFormat);
        }

    }

    @FXML
    private void imgHome(javafx.scene.input.MouseEvent event) {
        try {
            Node source = (Node) event.getSource();
            Scene scene = source.getScene();
            Stage stage = (Stage) scene.getWindow();

            Parent root = FXMLLoader.load(getClass().getResource("/com/example/ayeyarricemill/HomePage.fxml"));
            scene.setRoot(root);
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading to vital scene: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void textHome(javafx.scene.input.MouseEvent event) {
        try {
            Node source = (Node) event.getSource();
            Scene scene = source.getScene();
            Stage stage = (Stage) scene.getWindow();

            Parent root = FXMLLoader.load(getClass().getResource("/com/example/ayeyarricemill/HomePage.fxml"));
            scene.setRoot(root);
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading to vital scene: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
