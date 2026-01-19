package com.example.ayeyarricemill;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import static javafx.scene.layout.AnchorPane.setLeftAnchor;
import static javafx.scene.layout.AnchorPane.setTopAnchor;

public class SignUpController {
    @FXML
    private AnchorPane SignUpRootPane; // အပေါ် root AnchorPane variable
    // sign up page ကို responsive ညီအောင်လုပ်ဖို့အတွက်
    @FXML
    private AnchorPane SignUpModalPane; // အောက် root AnchorPane variable


    public void initialize(){

        if(SignUpRootPane != null && SignUpModalPane != null){
            SignUpRootPane.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldNumber, Number newNumber) {
                    double newleftAnchor = (newNumber.doubleValue() - SignUpModalPane.getPrefWidth())/ 2.0;
                    setLeftAnchor(SignUpModalPane,newleftAnchor);
                }
            });

            SignUpRootPane.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldNumber, Number newNumber) {
                    double newTopAnchor = (newNumber.doubleValue() - SignUpModalPane.getPrefHeight()) / 2.0;
                    setTopAnchor(SignUpModalPane, newTopAnchor);
                }
            });

            setLeftAnchor(SignUpModalPane, (SignUpRootPane.getWidth() - SignUpModalPane.getPrefWidth() ) / 2.0);
            setTopAnchor(SignUpModalPane, (SignUpRootPane.getHeight() - SignUpModalPane.getPrefHeight()) /2.0);
        }
    }
}


