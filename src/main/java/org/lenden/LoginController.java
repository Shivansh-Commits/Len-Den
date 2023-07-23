package org.lenden;

import com.zaxxer.hikari.pool.HikariPool;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.lenden.dao.DaoImpl;
import org.lenden.model.Tenants;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

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

        if(tenant.getUsername().equals("") || tenant.getPassword().equals(""))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Enter Username & Password", ButtonType.OK);
            alert.setHeaderText("Fields can not be Empty");
            alert.setTitle("Alert!");
            alert.showAndWait();
            return;
        }

        DaoImpl daoimpl = new DaoImpl();

        try {
            if (daoimpl.login(tenant)) {

                //Opening up new Window (Home Screen)
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("Main.fxml"));

                Scene scene = new Scene(fxmlLoader.load(), 600, 400);
                Stage stage = new Stage();

                //set title for window
                stage.setTitle("LenDen");

                //Setting the window logo
                Image window_icon = new Image(getClass().getResource("/logos/png/logo-white.png").toExternalForm());
                stage.getIcons().add(window_icon);

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
        catch(SQLException e)
        {
            //e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.WARNING, "LenDen Was unable to connect to the Database,check you network Connection. If this error keeps occouring ,contact Customer Support.", ButtonType.OK);
            alert.setHeaderText("Database Connection Error");
            alert.setTitle("Alert!");
            alert.setHeight(500);
            alert.showAndWait();
        }
    }

    public static String getTenant()
    {
        return tenant.getUsername();
    }


}
