package org.lenden;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
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

    private Timeline timeline;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        navMenu.setTranslateX(-200);
        closeMenuButton.setVisible(false);
        openMenuButton.setVisible(true);
    }

    @FXML
    public void openMenuBar(MouseEvent e)
    {
        navMenu.setVisible(true);
        TranslateTransition openNav=new TranslateTransition(new Duration(200),navMenu);
        openNav.setToX(0);
        openNav.play();
        closeMenuButton.setVisible(true);
        openMenuButton.setVisible(false);
    }

    @FXML
    public void closeMenuBar(MouseEvent e)
    {
        TranslateTransition closeNav=new TranslateTransition(new Duration(200),navMenu);
        closeNav.setToX(-(navMenu.getWidth()));
        closeNav.play();
        closeNav.setOnFinished(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event) {
                navMenu.setVisible(false);
            }
        });
        closeMenuButton.setVisible(false);
        openMenuButton.setVisible(true);
    }


}
