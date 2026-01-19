package com.example.ayeyarricemill;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class SceneController {

    private static StackPane mainPane;

    // MainDashboard ကနေ Pane လာအပ်မယ့် method
    public static void setMainPane(StackPane pane) {
        mainPane = pane;
    }

    // Page ပြောင်းပေးမယ့် method
    public static void switchCenter(String fxmlFile) {
        try {
            // ၁။ FXML အသစ်ကို load လုပ်မယ်
            Parent newPage = FXMLLoader.load(SceneController.class.getResource(fxmlFile));

            // ၂။ StackPane ထဲက အဟောင်းတွေရှင်းပြီး အသစ်ထည့်မယ်
            if (mainPane != null) {
                mainPane.getChildren().setAll(newPage);
            } else {
                System.err.println("Main Pane not initialized!");
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Page ရှာမတွေ့ပါ: " + fxmlFile);
        }
    }
}