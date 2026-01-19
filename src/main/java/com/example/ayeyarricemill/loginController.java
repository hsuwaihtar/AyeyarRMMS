package com.example.ayeyarricemill;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import static javafx.scene.layout.AnchorPane.setLeftAnchor;
import static javafx.scene.layout.AnchorPane.setTopAnchor;

public class loginController {
    @FXML
    private AnchorPane LogRoot; // Login ပထမ anchorPane၏ variable
//    login page ကို responsive ညီအောင်လုပ်နိုင်ရန်
    @FXML
    private AnchorPane LogModal; // Login ဒုတိယ anchorPane၏ variable

    public void initialize(){
        if(LogRoot != null && LogModal != null){
            LogRoot.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldNumber, Number newNumber) {
                    double newleftAnchor = (newNumber.doubleValue() - LogModal.getPrefWidth())/ 2.0;
                    setLeftAnchor(LogModal,newleftAnchor);
                }
            });

            LogRoot.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldNumber, Number newNumber) {
                    double newTopAnchor = (newNumber.doubleValue() - LogModal.getPrefHeight()) / 2.0;
                    setTopAnchor(LogModal, newTopAnchor);
                }
            });

            setLeftAnchor(LogModal, (LogRoot.getWidth() - LogModal.getPrefWidth() ) / 2.0);
            setTopAnchor(LogModal, (LogRoot.getHeight() - LogModal.getPrefHeight()) /2.0);
        
        }
    }
}
