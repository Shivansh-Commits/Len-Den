package org.lenden;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.lenden.dao.DaoImpl;
import org.lenden.model.Tenants;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;


public class LoginController
{
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button signInButton;

    static Tenants tenant = new Tenants();

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

            //set title for window
            stage.setTitle("LenDen");

            //get icon path from properties file
            Path logoPath = Paths.get("src","main", "resources","logos","png", "logo-white.png");
            //set icon for home window
            stage.getIcons().add(new Image(logoPath.toUri().toString()));

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

    public static String getTenant()
    {
        return tenant.getUsername();
    }


}
