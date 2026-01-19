package com.example.ayeyarricemill;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainDashboardController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        // ၁။ SceneController ကို contentArea အပ်လိုက်မယ်
        SceneController.setMainPane(contentArea);

        // ၂။ စစဖွင့်ချင်း Home page ကို အရင်ဆုံး ပြထားမယ်
        try {
            Parent home = FXMLLoader.load(getClass().getResource("HomeContent.fxml"));
            contentArea.getChildren().setAll(home);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}