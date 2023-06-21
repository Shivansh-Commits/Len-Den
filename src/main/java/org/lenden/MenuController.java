package org.lenden;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.lenden.dao.DaoImpl;
import org.lenden.model.MenuItems;
import org.postgresql.util.PSQLException;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
public class MenuController implements Initializable
{
    @FXML
    VBox categoriesVBox;
    @FXML
    TableView menuTable;
    @FXML
    TextField addItemName;
    @FXML
    TextField addItemPrice;
    @FXML
    ComboBox addItemCategory;
    @FXML
    ComboBox addItemAvailability;


    ObservableList<MenuItems> menuTableItems =  FXCollections.observableArrayList();
    DaoImpl daoimpl = new DaoImpl();

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        //Setting CSS Classes
        menuTable.getStyleClass().add("table-items");

        //Displaying Categories
        ObservableList<String> categories = daoimpl.getCategories();

        for(String category:categories)
        {
            Button button = new Button(category); // Create a new button

            button.setOnMouseClicked(this::displayMenuItems);
            button.getStyleClass().add("menu-page-category-button");
            button.setPrefWidth(218);
            button.setPrefHeight(114);
            categoriesVBox.getChildren().add(button);
        }

        //Populating category drop down menu
        addItemCategory.setItems(categories);

        //Populating availability drop down menu
        addItemAvailability.setItems(FXCollections.observableArrayList("Available","NOT Available"));
    }

    public void displayMenuItems(MouseEvent e)
    {
        Button clickedButton = (Button) e.getSource();
        String category = clickedButton.getText();

        menuTableItems = daoimpl.getCategoryItems(category);

        // Create a cell value factory for the Name column
        TableColumn<MenuItems, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));
        nameCol.setPrefWidth(250);

        // Create a cell value factory for the Price column
        TableColumn<MenuItems, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));
        priceCol.setPrefWidth(200);

        // Create a cell value factory for the Availability column
        TableColumn<MenuItems, String> availCol = new TableColumn<>("Availability");
        availCol.setCellValueFactory(new PropertyValueFactory<>("foodItemAvailability"));
        availCol.setPrefWidth(200);

        // Set the cell value factories for the table columns
        menuTable.getColumns().setAll(nameCol, priceCol, availCol);

        menuTable.setItems(menuTableItems);

        // Set the background color of the "Availability" cell based on its content
        availCol.setCellFactory(column -> new TableCell<MenuItems, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText("");
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Available")) {
                        // Set the background color of the cell to green if the food item is available
                        setStyle("-fx-background-color: #c9f5c9;");
                    } else {
                        // Set the background color of the cell to red if the food item is not available
                        setStyle("-fx-background-color: #f5c9c9;");
                    }
                }
            }
        });
    }

    public void addToMenu(MouseEvent event)
    {
        if(addItemName.getText().isEmpty() || addItemPrice.getText().isEmpty() || addItemCategory.getSelectionModel().getSelectedItem() == null || addItemAvailability.getSelectionModel().getSelectedItem() == null )
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Fields Cannot be Empty", ButtonType.OK);
            alert.setHeaderText("Warning");
            alert.setTitle("Alert!");
            alert.showAndWait();

            return;
        }

        String itemName = addItemName.getText();
        int itemPrice = Integer.parseInt(   addItemPrice.getText()  );
        String itemCategory = addItemCategory.getSelectionModel().getSelectedItem().toString();
        String itemAvailability = addItemAvailability.getSelectionModel().getSelectedItem().toString();

        ObservableList<String> categories = daoimpl.getCategories();

        MenuItems item = new MenuItems();
        item.setFoodItemName(   itemName   );
        item.setFoodItemPrice(   itemPrice   );
        item.setFoodItemAvailability(   itemAvailability    );
        item.setFoodItemCategory(   itemCategory    );


        //Checking if user has Entered pre-existing category
        if(categories.contains(item.getFoodItemCategory()))  //If category already EXISTS
        {

            Boolean isSuccess = daoimpl.addItemToMenu(item);

            if (isSuccess)
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Item added Successfully", ButtonType.OK);
                alert.setHeaderText("Success");
                alert.setTitle("Information");
                alert.showAndWait();
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Item Already Exists! If item does not already exist and you are still seeing this error, Contact customer Support!", ButtonType.OK);
                alert.setHeaderText("Duplicate Item Entry");
                alert.setTitle("Information");
                alert.showAndWait();
            }
        }
        else //If category DOES NOT already exist asking user ,If they want NEW cateogry to be created
        {

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to add a new Category - "+ item.getFoodItemCategory() +" ?", ButtonType.YES,ButtonType.NO);
            confirmationAlert.setHeaderText("Alert! ");
            confirmationAlert.setTitle("Information");
            confirmationAlert.showAndWait();

            if(confirmationAlert.getResult() == ButtonType.YES)//Checking if user has selected YES or NO
            {
                Boolean isSuccess = daoimpl.addItemToMenu(item);

                if (isSuccess)
                {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Item added Successfully", ButtonType.OK);
                    alert.setHeaderText("Success");
                    alert.setTitle("Information");
                    alert.showAndWait();
                }
                else
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Item Already Exists! If item does not already exist and you are still seeing this error, Contact customer Support!", ButtonType.OK);
                    alert.setHeaderText("Duplicate Item Entry");
                    alert.setTitle("Information");
                    alert.showAndWait();
                }
            }

        }



    }
}
