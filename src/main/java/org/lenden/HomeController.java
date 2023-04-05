package org.lenden;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable
{
    @FXML
    AnchorPane navMenu;
    @FXML
    Button openMenuButton;
    @FXML
    Button closeMenuButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        navMenu.setVisible(false);
        closeMenuButton.setVisible(false);
    }

    @FXML
    public void openMenuBar(MouseEvent e)
    {
        navMenu.setVisible(true);
        closeMenuButton.setVisible(true);
        openMenuButton.setVisible(false);
    }

    @FXML
    public void closeMenuBar(MouseEvent e)
    {
        navMenu.setVisible(false);
        closeMenuButton.setVisible(false);
        openMenuButton.setVisible(true);
    }


}
