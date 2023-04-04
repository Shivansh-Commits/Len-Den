package org.lenden;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
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
    ImageView menuOpenButton;
    @FXML
    ImageView menuCloseButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        navMenu.setVisible(false);
        menuCloseButton.setVisible(false);
        menuOpenButton.setVisible(true);

    }

    public void menuOpenButtonClicked(MouseEvent e)
    {
        navMenu.setVisible(true);
        menuCloseButton.setVisible(true);
        menuOpenButton.setVisible(false);
    }

    public void menuCloseButtonClicked(MouseEvent e)
    {
        navMenu.setVisible(false);
        menuCloseButton.setVisible(false);
        menuOpenButton.setVisible(true);
    }


}
