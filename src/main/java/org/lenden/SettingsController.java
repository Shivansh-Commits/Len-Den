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
    Button AreaSettingsButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void openAreaSettings(MouseEvent ignoredEvent) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("area_manager.fxml"));
        BorderPane home = loader.load();

        Scene scene = AreaSettingsButton.getScene();
        scene.getStylesheets().add("settingsStyleSheet.css");

        mainPane.setCenter(home);
    }

    public void openBillingManager(MouseEvent ignoredEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("billing_manager.fxml"));
        BorderPane home = loader.load();

        Scene scene = AreaSettingsButton.getScene();
        scene.getStylesheets().add("settingsStyleSheet.css");

        mainPane.setCenter(home);
    }

    public void openInventoryManager(MouseEvent ignoredEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("inventory_manager.fxml"));
        BorderPane home = loader.load();

        Scene scene = AreaSettingsButton.getScene();
        scene.getStylesheets().add("settingsStyleSheet.css");

        mainPane.setCenter(home);
    }

    public void openSubscriptionInfo(MouseEvent ignoredEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("subscription_Info.fxml"));
        BorderPane home = loader.load();

        Scene scene = AreaSettingsButton.getScene();
        scene.getStylesheets().add("settingsStyleSheet.css");

        mainPane.setCenter(home);
    }

    public void openRecepieManager(MouseEvent ignoredEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("recepie_manager.fxml"));
        BorderPane home = loader.load();

        Scene scene = AreaSettingsButton.getScene();
        scene.getStylesheets().add("settingsStyleSheet.css");

        mainPane.setCenter(home);
    }
}
