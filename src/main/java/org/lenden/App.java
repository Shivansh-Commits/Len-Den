package org.lenden;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException
    {
        scene = new Scene(loadFXML());

        scene.getStylesheets().add("loginStyleSheet.css");

        stage.setScene(scene);

        //set title for window
        stage.setTitle("LenDen Login");
        stage.setResizable(false);

        //Setting the Login window Icon
        Image whiteLogo = new Image(getClass().getResource("/logos/png/logo-white.png").toExternalForm());
        stage.getIcons().add(whiteLogo);

        stage.show();
    }

    private static Parent loadFXML() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("login.fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args)
    {
        launch();
    }

}