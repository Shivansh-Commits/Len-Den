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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void openTableSettings(MouseEvent event) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("tableSettings.fxml"));
        AnchorPane home = loader.load();

        Scene scene = tableSettingsButton.getScene();
        scene.getStylesheets().add("tableSettingsStyleSheet.css");

        mainPane.setCenter(home);
    }

    public void openTaxAndBillingSettings(MouseEvent event)
    {

    }


}
