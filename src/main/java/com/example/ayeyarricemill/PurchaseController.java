package com.example.ayeyarricemill;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import static javafx.scene.layout.AnchorPane.setLeftAnchor;
import static javafx.scene.layout.AnchorPane.setTopAnchor;

public class PurchaseController {

    @FXML
    private AnchorPane PaddyRoot; // Login ပထမ anchorPane၏ variable
    //    login page ကို responsive ညီအောင်လုပ်နိုင်ရန်
    @FXML
    private AnchorPane PaddyModal; // Login ဒုတိယ anchorPane၏ variable

    public void initialize(){
        if(PaddyRoot != null && PaddyModal != null){
            PaddyRoot.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldNumber, Number newNumber) {
                    double newleftAnchor = (newNumber.doubleValue() - PaddyModal.getPrefWidth())/ 2.0;
                    setLeftAnchor(PaddyModal,newleftAnchor);
                }
            });

            PaddyRoot.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldNumber, Number newNumber) {
                    double newTopAnchor = (newNumber.doubleValue() - PaddyModal.getPrefHeight()) / 2.0;
                    setTopAnchor(PaddyModal, newTopAnchor);
                }
            });

            setLeftAnchor(PaddyModal, (PaddyRoot.getWidth() - PaddyModal.getPrefWidth() ) / 2.0);
            setTopAnchor(PaddyModal, (PaddyRoot.getHeight() - PaddyModal.getPrefHeight()) /2.0);

        }
    }
}
