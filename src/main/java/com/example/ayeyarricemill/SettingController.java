package com.example.ayeyarricemill;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SettingController {

    @FXML private ScrollPane mainScrollPane;
    @FXML private VBox contentVBox;

    // Section များအတွက် FXID (Scene Builder မှာ သေချာပေးထားရပါမယ်)
    @FXML private AnchorPane projectInfoSection;
    @FXML private AnchorPane ruleSection;
    @FXML private AnchorPane aboutUsSection;

    // --- အောက်ဆင်းမယ့် Method များ ---
    @FXML
    private void handleProjectInfo(ActionEvent event) {
        scrollToSection(projectInfoSection);
    }

    @FXML
    private void handleRule(ActionEvent event) {
        scrollToSection(ruleSection);
    }

    @FXML
    private void handleAboutUs(ActionEvent event) {
        scrollToSection(aboutUsSection);
    }

    // --- အပေါ်ဆုံးပြန်တက်မယ့် Method (Page တိုင်းမှာရှိတဲ့ Up Buttons အားလုံးအတွက်) ---
    @FXML
    private void handleScrollToTop(ActionEvent event) {
        animateScroll(0.0); // 0.0 သည် ScrollPane ၏ ထိပ်ဆုံး (Home) ဖြစ်သည်
    }

    private void scrollToSection(AnchorPane section) {
        // ၁။ Content (VBox) ရဲ့ Scene ပေါ်က တည်နေရာကို ယူပါ
        double contentSceneY = contentVBox.localToScene(0, 0).getY();

        // ၂။ သွားချင်တဲ့ Section ရဲ့ Scene ပေါ်က တည်နေရာကို ယူပါ
        double sectionSceneY = section.localToScene(0, 0).getY();

        // ၃။ VBox ထဲမှာ အဲဒီ Section က တကယ်တမ်း ဘယ်လောက် အမြင့်မှာ ရှိနေလဲဆိုတာ နှိုင်းယှဉ်တွက်ပါ
        double targetY = sectionSceneY - contentSceneY;

        double contentHeight = contentVBox.getBoundsInLocal().getHeight();
        double viewportHeight = mainScrollPane.getViewportBounds().getHeight();
        double maxScroll = contentHeight - viewportHeight;

        double targetVvalue = (maxScroll > 0) ? (targetY / maxScroll) : 0;

        // တန်ဖိုးကို 0 နဲ့ 1 ကြားမှာပဲ ရှိအောင် ကန့်သတ်ပါ
        targetVvalue = Math.max(0, Math.min(1.0, targetVvalue));

        animateScroll(targetVvalue);
    }
    // လက်တွေ့ Scroll ဆင်းပေးမယ့် Animation Method
    private void animateScroll(double vValue) {
        Timeline timeline = new Timeline();
        // Interpolator.EASE_BOTH က အသွားအလာကို ပိုမိုနူးညံ့ချောမွေ့စေပါတယ်
        KeyValue kv = new KeyValue(mainScrollPane.vvalueProperty(), vValue, javafx.animation.Interpolator.EASE_BOTH);
        KeyFrame kf = new KeyFrame(Duration.millis(800), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }
}