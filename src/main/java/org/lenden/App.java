package org.lenden;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException
    {
        scene = new Scene(loadFXML("login"));
        stage.setScene(scene);

        //set title for window
        stage.setTitle("LenDen Login");

        //set login window icon
        Path logoPath = Paths.get("src","main", "resources","logos","png", "logo-white.png");
        stage.getIcons().add(new Image(logoPath.toUri().toString()));

        stage.show();
    }

// NOT USED
//    static void setRoot(String fxml) throws IOException
//    {
//        scene.setRoot(loadFXML(fxml));
//    }

    private static Parent loadFXML(String fxml) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args)
    {
        launch();
    }

}