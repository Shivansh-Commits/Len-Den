package org.lenden;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.lenden.dao.daoImpl;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable
{

    public TextField username;
    public PasswordField password;
    @FXML
    private ComboBox userChoice;

    @FXML
    public void onSignIn(ActionEvent event) throws IOException {
        String user = username.getText();
        String pass = password.getText();

        daoImpl obj = new daoImpl();

        if( obj.login(user,pass) )
        {
            System.out.println("LOGIN SUCCESS");
            App.setRoot("home");
        }
        else
        {
            System.out.println("LOGIN FAILURE");
        }
    }

    public static String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Color promptTextColor = Color.web("#B0B0B0");
        String css = "-fx-prompt-text-fill: " + toRGBCode(promptTextColor) + ";";
        userChoice.setStyle(css);

        userChoice.setStyle(css);
        userChoice.getItems().add("Admin");
        userChoice.getItems().add("Staff");
    }

}
