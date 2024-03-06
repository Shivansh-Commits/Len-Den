package org.lenden;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.util.Duration;
import org.lenden.model.Tenants;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static org.lenden.LoginController.getTenant;

public class SubscriptionInfoController implements Initializable {

    @FXML
    Label userLabel;
    @FXML
    Label subscriptionEndDateLabel;
    @FXML
    Label subscriptionStartDateLabel;


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        Tenants tenant = getTenant();
        userLabel.setText(userLabel.getText().replaceAll("User", tenant.getUsername()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d'rd' MMMM yyyy");
        String formattedSubEndDate = tenant.getSubscriptionEndDate().format(formatter);
        subscriptionEndDateLabel.setText(subscriptionEndDateLabel.getText()+ " " +formattedSubEndDate);

        String formattedSubStartDate = tenant.getSubscriptionStartDate().format(formatter);
        subscriptionStartDateLabel.setText(subscriptionStartDateLabel.getText()+ " " +formattedSubStartDate);


        //Adding Animations
        FadeTransition userFadeTransition = new FadeTransition(Duration.seconds(2), userLabel);
        FadeTransition subEndDateFadeTransition = new FadeTransition(Duration.seconds(3), subscriptionEndDateLabel);
        FadeTransition subStartDateFadeTransition = new FadeTransition(Duration.seconds(3), subscriptionStartDateLabel);
        // Set the initial opacity of the label
        userLabel.setOpacity(0);
        subscriptionEndDateLabel.setOpacity(0);
        subscriptionStartDateLabel.setOpacity(0);
        // Set the target opacity for the label
        userFadeTransition.setToValue(1);
        subEndDateFadeTransition.setToValue(1);
        subStartDateFadeTransition.setToValue(1);
        // Play the fade transition
        userFadeTransition.play();
        subEndDateFadeTransition.play();
        subStartDateFadeTransition.play();


    }
}
