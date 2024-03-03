package org.lenden;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML
    BorderPane mainPane;
    @FXML
    Button tableSettingsButton;
    @FXML
    Button billingSettingsButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void openTableSettings(MouseEvent ignoredEvent) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("tableSettings.fxml"));
        AnchorPane home = loader.load();

        Scene scene = tableSettingsButton.getScene();
        scene.getStylesheets().add("tableSettingsStyleSheet.css");

        mainPane.setCenter(home);
    }

    public void openBillingManager(MouseEvent ignoredEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("billingSettings.fxml"));
        BorderPane home = loader.load();

        Scene scene = billingSettingsButton.getScene();
        scene.getStylesheets().add("tableSettingsStyleSheet.css");

        mainPane.setCenter(home);
    }

    public void openInventoryManager(MouseEvent ignoredEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("inventorySettings.fxml"));
        BorderPane home = loader.load();

        Scene scene = billingSettingsButton.getScene();
        scene.getStylesheets().add("tableSettingsStyleSheet.css");

        mainPane.setCenter(home);
    }

    public void openSubscriptionInfo(MouseEvent ignoredEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("subscriptionInfo.fxml"));
        BorderPane home = loader.load();

        Scene scene = billingSettingsButton.getScene();
        scene.getStylesheets().add("tableSettingsStyleSheet.css");

        mainPane.setCenter(home);
    }

}
