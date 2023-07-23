package org.lenden;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.lenden.dao.DaoImpl;
import org.lenden.model.MenuItems;
import java.net.URL;
import java.util.ResourceBundle;
public class MenuController implements Initializable
{
    @FXML
    VBox categoriesVBox;
    @FXML
    TableView<MenuItems> menuTable;
    @FXML
    TextField addItemName;
    @FXML
    TextField addItemPrice;
    @FXML
    ComboBox addItemCategory;
    @FXML
    ComboBox addItemAvailability;

    @FXML
    TextField updateItemName;
    @FXML
    TextField updateItemPrice;
    @FXML
    ComboBox updateItemCategory;
    @FXML
    ComboBox updateItemAvailability;


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
            button.getStyleClass().add("category-button");
            button.setPrefWidth(218);
            button.setPrefHeight(114);
            button.setCursor(Cursor.HAND);
            categoriesVBox.getChildren().add(button);
        }

        //Populating category drop down menu (ADD ITEM)
        addItemCategory.setItems(categories);

        //Populating availability drop down menu (ADD ITEM)
        addItemAvailability.setItems(FXCollections.observableArrayList("Available","NOT Available"));

        //Populating category drop down menu (UPDATE ITEM)
        updateItemCategory.setItems(categories);

        //Populating availability drop down menu (UPDATE ITEM)
        updateItemAvailability.setItems(FXCollections.observableArrayList("Available","NOT Available"));
    }

    public void displayMenuItems(String category)
    {
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
            alert.setHeaderText("Values not Entered");
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

            boolean isSuccess = daoimpl.addItemToMenu(item);

            if (isSuccess)
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Item added Successfully", ButtonType.OK);
                alert.setHeaderText("Success");
                alert.setTitle("Information");
                alert.showAndWait();

                displayMenuItems(itemCategory); //TO SHOW TABLE WITH UPDATED ITEMS
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
                boolean isSuccess = daoimpl.addItemToMenu(item);

                if(isSuccess)
                {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Item added Successfully", ButtonType.OK);
                    alert.setHeaderText("Success");
                    alert.setTitle("Information");
                    alert.showAndWait();
                }
                else
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Item with same Name already Exists! If item does not already exist and you are still seeing this error, Contact customer Support!", ButtonType.OK);
                    alert.setHeaderText("Duplicate Item Entry");
                    alert.setTitle("Information");
                    alert.showAndWait();
                }
            }
        }
    }

    public void populateUpdateDeleteForm(MouseEvent event)
    {
        MenuItems selectedItem = menuTable.getSelectionModel().getSelectedItem();

        if(selectedItem == null)
            return;

        updateItemName.setText(selectedItem.getFoodItemName());
        updateItemPrice.setText( Integer.toString(selectedItem.getFoodItemPrice()) );

        updateItemCategory.setValue(selectedItem.getFoodItemCategory());

        updateItemAvailability.setValue(selectedItem.getFoodItemAvailability());
    }

    public void updateItem(MouseEvent event)
    {
        if(updateItemName.getText().isEmpty() || updateItemPrice.getText().isEmpty() || updateItemCategory.getSelectionModel().getSelectedItem() == null || updateItemAvailability.getSelectionModel().getSelectedItem() == null )
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Fields Cannot be Empty, Select an item and modify values and click 'Update'", ButtonType.OK);
            alert.setHeaderText("No Item Selected");
            alert.setTitle("Alert!");
            alert.showAndWait();

            return;
        }

        Alert updateAlert = new Alert(Alert.AlertType.CONFIRMATION, "ARE YOU SURE ?", ButtonType.YES , ButtonType.NO);
        updateAlert.setHeaderText("Item will be Updated");
        updateAlert.setTitle("Alert!");
        updateAlert.showAndWait();

        if(updateAlert.getResult() == ButtonType.YES)
        {
            MenuItems selectedItem = menuTable.getSelectionModel().getSelectedItem();
            if(selectedItem == null)
                return;

            String itemName = updateItemName.getText();
            int itemPrice = Integer.parseInt(   updateItemPrice.getText()  );
            String itemCategory = updateItemCategory.getSelectionModel().getSelectedItem().toString();
            String itemAvailability = updateItemAvailability.getSelectionModel().getSelectedItem().toString();

            MenuItems item = new MenuItems();
            item.setFoodItemName(   itemName   );
            item.setFoodItemPrice(  itemPrice   );
            item.setFoodItemCategory(   itemCategory    );
            item.setFoodItemAvailability(   itemAvailability    );

            if(!daoimpl.updateMenuItem(item))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "COULD NOT UPDATE ITEM. If this error keeps occuring contact customer support.", ButtonType.OK);
                alert.setHeaderText("Item not Update!");
                alert.setTitle("Alert!");
                alert.showAndWait();
            }
            else
            {
                displayMenuItems(item.getFoodItemCategory()); //SHOW TABLE WITH UPDATED ITEMS
            }
        }

    }

    public void deleteItem(MouseEvent event)
    {
        if(updateItemName.getText().isEmpty() || updateItemPrice.getText().isEmpty() || updateItemCategory.getSelectionModel().getSelectedItem() == null || updateItemAvailability.getSelectionModel().getSelectedItem() == null )
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Fields Cannot be Empty, Select an item first.", ButtonType.OK);
            alert.setHeaderText("No Item Selected");
            alert.setTitle("Alert!");
            alert.showAndWait();

            return;
        }

        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION, "ARE YOU SURE ?", ButtonType.YES , ButtonType.NO);
        deleteAlert.setHeaderText("Item will be deleted");
        deleteAlert.setTitle("Alert!");
        deleteAlert.showAndWait();

        if(deleteAlert.getResult() == ButtonType.YES)
        {
            MenuItems selectedItem = menuTable.getSelectionModel().getSelectedItem();
            if(selectedItem == null)
                return;
            String itemName = updateItemName.getText();
            String itemCategory = updateItemCategory.getSelectionModel().getSelectedItem().toString();

            MenuItems item = new MenuItems();
            item.setFoodItemName(   itemName   );
            item.setFoodItemCategory(   itemCategory    );

            if(!daoimpl.deleteMenuItem(item))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "COULD NOT DELETE ITEM. If this error keeps occuring contact customer support.", ButtonType.OK);
                alert.setHeaderText("Item not deleted!");
                alert.setTitle("Alert!");
                alert.showAndWait();
            }
            else
            {
                updateItemName.setText("");
                updateItemPrice.setText("");
                updateItemAvailability.setValue("");
                updateItemCategory.setValue("");
                displayMenuItems(item.getFoodItemCategory()); //SHOW TABLE WITH UPDATED ITEMS
            }
        }

    }
}
