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
import javafx.scene.control.TabPane;
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
    AnchorPane centerContainer;

    //Button openMenuButton;

    //Button closeMenuButton;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        /*

        centerContainer.setTranslateX(-100);

        navMenu.setTranslateX(-200);

        closeMenuButton.setVisible(false);
        openMenuButton.setVisible(true);

        */
    }

    @FXML
    public void openMenuBar(MouseEvent e)
    {
        /*

        navMenu.setVisible(true);

        //Sliding side menu towards right
        TranslateTransition openNav=new TranslateTransition(new Duration(200),navMenu);
        openNav.setToX(0);
        openNav.play();

        //Sliding the center container towards right
        TranslateTransition slideCenterContainer = new TranslateTransition(Duration.seconds(0.2), centerContainer);
        slideCenterContainer.setToX(0);
        slideCenterContainer.play();

        closeMenuButton.setVisible(true);
        openMenuButton.setVisible(false);

        */
    }

    @FXML
    public void closeMenuBar(MouseEvent e)
    {
        /*

        //Sliding nav Menu back in toward left
        TranslateTransition closeNav=new TranslateTransition(new Duration(200),navMenu);
        closeNav.setToX(-(navMenu.getWidth()));
        closeNav.play();

        //Sliding the center container back in towards left
        TranslateTransition slideCenterContainer = new TranslateTransition(Duration.seconds(0.2), centerContainer);
        slideCenterContainer.setToX(-100);
        slideCenterContainer.play();

        closeNav.setOnFinished(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event) {
                navMenu.setVisible(false);
            }
        });

        closeMenuButton.setVisible(false);
        openMenuButton.setVisible(true);

        */
    }

}
