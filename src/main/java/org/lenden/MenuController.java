package org.lenden;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lenden.dao.DaoImpl;
import org.lenden.model.MenuItems;
import java.net.URL;
import java.sql.SQLException;
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
    ComboBox<String> addItemCategory;
    @FXML
    ComboBox<String> addItemAvailability;

    @FXML
    TextField updateItemName;
    @FXML
    TextField updateItemPrice;
    @FXML
    ComboBox<String> updateItemCategory;
    @FXML
    ComboBox<String> updateItemAvailability;
    @FXML
    Button addRecipeItemButton;
    @FXML
    VBox recipeVbox;
    @FXML
    CheckBox trackRawMaterialCheckbox;

    int recipeItemsCount=1;
    ObservableList<MenuItems> menuTableItems =  FXCollections.observableArrayList();
    DaoImpl daoimpl = new DaoImpl();

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

        //Displaying Categories
        ObservableList<String> categories = null;
        try
        {
            categories = daoimpl.getCategories();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+e.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
        }

        for(String category:categories)
        {
            Button button = new Button(category); // Create a new button

            button.setOnMouseClicked(this::displayMenuItems);
            button.getStyleClass().add("category-button");
            button.setPrefWidth(160);
            button.setPrefHeight(80);
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

        addNumericInputFieldValidation(addItemPrice);
        addNumericInputFieldValidation(updateItemPrice);

        addAlphabeticInputFieldValidation(addItemName);
        addAlphabeticInputFieldValidation(updateItemName);

        //Adding event listener to "Track Raw Material Checkbox"
        trackRawMaterialCheckbox.setOnAction(event -> {recipeVbox.setDisable(!trackRawMaterialCheckbox.isSelected());});
    }

    /**
     *
     * For Input Validation.
     * Desc - Function adds event listener to the fields and uses regex to match input .
     * only allows Aplhabets , " - " and nums 1-9
     *
     * **/
    private void addAlphabeticInputFieldValidation(TextField textField) {
        // Event filter to allow only alphabetic characters and the hyphen "-"
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String input = event.getCharacter();

            // Use a regular expression to check if the input is alphabetic or a hyphen
            if (!input.matches("[a-zA-Z\\-1-9 ]")) {
                event.consume(); // Ignore the input if it's not alphabetic or a hyphen
            }
        });
    }

    /**
     *
     * For Input Validation.
     * Desc - Function adds event listener to the fields and uses regex to match input .
     * only allows " . " and nums 1-9
     *
     * **/
    private void addNumericInputFieldValidation(TextField textField) {
        // Event filter to allow only numeric characters and the decimal point "."
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String input = event.getCharacter();

            // Use a regular expression to check if the input is numeric or a decimal point
            if (!input.matches("[0-9.]")) {
                event.consume(); // Ignore the input if it's not numeric or a decimal point
            }
        });
    }



    public void addRecipeItemButtonListener()
    {
        TextField rawMaterialTextField = new TextField();
        rawMaterialTextField.setId("rawMaterial"+recipeItemsCount);
        rawMaterialTextField.setPrefSize(309,38);
        rawMaterialTextField.setPromptText("Raw Material *");

        TextField rawMaterialQuantity = new TextField();
        rawMaterialQuantity.setId("rawMaterialQuantity"+recipeItemsCount);
        rawMaterialQuantity.setPrefSize(208,38);
        rawMaterialQuantity.setPromptText("Quantity (Unit) *");

        Button removeRawMaterial = new Button();
        removeRawMaterial.setId("removeRawMaterial"+recipeItemsCount);
        removeRawMaterial.setText("X");
        removeRawMaterial.getStyleClass().add("remove-raw-material-button");
        removeRawMaterial.setCursor(Cursor.HAND);


        HBox hBox = new HBox();
        hBox.getChildren().addAll(rawMaterialTextField,rawMaterialQuantity,removeRawMaterial);
        hBox.setSpacing(10);

        removeRawMaterial.setOnAction(e -> {
            Node parent = removeRawMaterial.getParent();

            if (parent instanceof HBox) {
                HBox hBoxToRemove = (HBox) parent;
                recipeVbox.getChildren().remove(hBoxToRemove);
            }
        });

        recipeVbox.getChildren().add(recipeVbox.getChildren().size() - 1, hBox); // Add above the button

        recipeItemsCount++;
    }
    public void displayUpdatedCategoryList()
    {
        //Displaying Updated Category List
        try {
            ObservableList<String> categories = daoimpl.getCategories();

            categoriesVBox.getChildren().clear();

            for(String category:categories)
            {
                Button button = new Button(category); // Create a new button

                button.setOnMouseClicked(this::displayMenuItems);
                button.getStyleClass().add("category-button");
                button.setPrefWidth(171);
                button.setPrefHeight(80);
                button.setCursor(Cursor.HAND);
                categoriesVBox.getChildren().add(button);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Alert exAlert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+e.getMessage(), ButtonType.OK);
            exAlert.setHeaderText("Failed");
            exAlert.setTitle("Error!");
            exAlert.showAndWait();
        }
    }

    public void displayMenuItems(String category)
    {
        try {
            menuTableItems = daoimpl.getCategoryItems(category);
        }
        catch (Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
        }

        // Create a cell value factory for the Name column
        TableColumn<MenuItems, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));
        nameCol.setPrefWidth(250);
        nameCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Price column
        TableColumn<MenuItems, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));
        priceCol.setPrefWidth(200);
        priceCol.setStyle("-fx-alignment: CENTER;");

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
                        setAlignment(Pos.CENTER);
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

        try {
            menuTableItems = daoimpl.getCategoryItems(category);
        }
        catch (Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
        }

        // Create a cell value factory for the Name column
        TableColumn<MenuItems, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));
        nameCol.setPrefWidth(250);
        nameCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Price column
        TableColumn<MenuItems, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));
        priceCol.setPrefWidth(200);
        priceCol.setStyle("-fx-alignment: CENTER;");

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
                        setAlignment(Pos.CENTER);
                    } else {
                        // Set the background color of the cell to red if the food item is not available
                        setStyle("-fx-background-color: #f5c9c9;");
                        setAlignment(Pos.CENTER);
                    }
                }
            }
        });
    }

    public void addToMenu(MouseEvent ignoredEvent) throws SQLException
    {
        if(addItemName.getText().isEmpty() || addItemPrice.getText().isEmpty() || addItemCategory.getSelectionModel().getSelectedItem() == null || addItemAvailability.getSelectionModel().getSelectedItem() == null )
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Fields Cannot be Empty", ButtonType.OK);
            alert.setHeaderText("Values not Entered");
            alert.setTitle("Alert!");
            alert.showAndWait();

            return;
        }
        if(addItemPrice.getText().length() > 5 )
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "The price of a single food item can not be more than 10,000", ButtonType.OK);
            alert.getDialogPane().setMinHeight(Region.USE_COMPUTED_SIZE);
            alert.getDialogPane().setMaxHeight(Region.USE_COMPUTED_SIZE);
            alert.setHeaderText("Price Limit Exceeded");
            alert.setTitle("Alert!");
            alert.showAndWait();

            addItemPrice.setText("");
            addItemPrice.requestFocus();
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

            boolean isSuccess=false;
            try
            {
                isSuccess = daoimpl.addItemToMenu(item);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+e.getMessage(), ButtonType.OK);
                alert.setHeaderText("Failed");
                alert.setTitle("Error!");
                alert.showAndWait();
            }

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
                alert.getDialogPane().setMinHeight(Region.USE_COMPUTED_SIZE);
                alert.getDialogPane().setMaxHeight(Region.USE_COMPUTED_SIZE);
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
                boolean isSuccess=false;
                try
                {
                    isSuccess = daoimpl.addItemToMenu(item);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+e.getMessage(), ButtonType.OK);
                    alert.setHeaderText("Failed");
                    alert.setTitle("Error!");
                    alert.showAndWait();
                }

                if(isSuccess)
                {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Item added Successfully", ButtonType.OK);
                    alert.setHeaderText("Success");
                    alert.setTitle("Information");
                    alert.showAndWait();

                    //Displaying Updated Category List
                    displayUpdatedCategoryList();

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

            try {
                if (!daoimpl.updateMenuItem(item))
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
            catch (SQLException ex)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Database update operation Exception - "+ex.getMessage(), ButtonType.OK);
                alert.setHeaderText("Failed");
                alert.setTitle("Error!");
                alert.showAndWait();
            }
        }

    }

    public void deleteItem(MouseEvent ignoredEvent)
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

            try {
                if (!daoimpl.deleteMenuItem(item)) {
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

                    //SHOW TABLE WITH UPDATED ITEMS
                    displayMenuItems(item.getFoodItemCategory());

                    //SHOW UPDATED CATEGORY LIST
                    displayUpdatedCategoryList();
                }
            }
            catch (SQLException ex)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Database delete operation Exception - "+ex.getMessage(), ButtonType.OK);
                alert.setHeaderText("Failed");
                alert.setTitle("Error!");
                alert.showAndWait();
            }
        }

    }
}
