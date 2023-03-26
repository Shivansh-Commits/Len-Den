package org.lenden;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.lenden.dao.daoImpl;
import java.io.IOException;


public class LoginController
{
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button signInButton;

    @FXML
    public void onSignIn(ActionEvent event) throws IOException
    {
        String user = username.getText();
        String pass = password.getText();

        daoImpl obj = new daoImpl();

        if( obj.login(user,pass) )
        {
            System.out.println("LOGIN SUCCESS");

            //Opening up new Window (Home Screen)
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("home.fxml"));

            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = new Stage();
            stage.setTitle("LenDen Home");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        }
        else
        {
            System.out.println("LOGIN FAILURE");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Wrong username or password.", ButtonType.OK);
            alert.setHeaderText("Login Error");
            alert.setTitle("Alert!");
            alert.showAndWait();

            username.clear();
            password.clear();
        }
    }

    @FXML
    public void onMouseEnteredSignInButton(MouseEvent e)
    {
        signInButton.setStyle("-fx-background-color:white; -fx-text-fill:black;-fx-border-color:black; -fx-border-width:0.5;");
    }
    @FXML
    public void onMouseExitedSignInButton(MouseEvent e)
    {
        signInButton.setStyle("-fx-background-color:black; -fx-text-fill:white;");
    }


}
