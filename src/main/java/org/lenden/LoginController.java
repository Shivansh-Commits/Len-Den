package org.lenden;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.lenden.dao.DaoImpl;
import org.lenden.model.Tenants;

import java.io.IOException;


public class LoginController
{
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button signInButton;

    Tenants tenant = new Tenants();

    @FXML
    public void onSignIn(ActionEvent event) throws IOException
    {

        tenant.setUsername(username.getText());
        tenant.setPassword(password.getText());

        DaoImpl daoimpl = new DaoImpl();

        if( daoimpl.login(tenant) )
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

            Stage loginStage = (Stage) signInButton.getScene().getWindow();
            loginStage.close();
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

    public Tenants getTenant()
    {
        return tenant;
    }


}
