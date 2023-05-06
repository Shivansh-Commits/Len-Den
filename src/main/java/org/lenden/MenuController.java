package org.lenden;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.lenden.dao.DaoImpl;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MenuController implements Initializable
{
    @FXML
    VBox categoriesVBox;
    DaoImpl daoimpl = new DaoImpl();

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        List<String> categories = daoimpl.getCategories();

        for(String category:categories)
        {
            Button button = new Button(category); // Create a new button

            button.getStyleClass().add("menu-page-category-button");
            button.setPrefWidth(218);
            button.setPrefHeight(114);
            categoriesVBox.getChildren().add(button);
        }


    }
}
