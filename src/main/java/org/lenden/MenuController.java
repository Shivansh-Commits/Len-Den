package org.lenden;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.lenden.dao.DaoImpl;
import org.lenden.model.Menu;
import org.lenden.model.Variant;

import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;


public class MenuController implements Initializable
{
    @FXML
    VBox categoriesVBox;
    @FXML
    TableView<Menu> menuTable;
    @FXML
    TextField itemNameTextField;
    @FXML
    TextField itemPriceTextField;
    @FXML
    ComboBox<String> itemCategoryComboBox;
    @FXML
    ComboBox<String> itemAvailabilityComboBox;
    @FXML
    Button addToMenuButton;
    @FXML
    Button updateMenuItemButton;
    @FXML
    VBox variantVbox;
    @FXML
    Label itemId;


    int variantCount=0;
    ObservableList<Menu> menuTableItems =  FXCollections.observableArrayList();
    DaoImpl daoimpl = new DaoImpl();
    Stage currentStage;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

        //--------------------------------------------------------------------------------------------------------------
        //Setting Tips for Different Buttons - Will be Visible on Hover
        addToMenuButton.setTooltip(new javafx.scene.control.Tooltip("Add Item to Menu"));

        updateMenuItemButton.setTooltip(new javafx.scene.control.Tooltip("Click to Update, after adding new details"));


        //--------------------------------------------------------------------------------------------------------------
        //Displaying Categories
        ObservableList<String> categories = null;
        try
        {
            categories = daoimpl.fetchCategories();
        }
        catch(Exception e)
        {
            e.getMessage();
            currentStage = (Stage) menuTable.getScene().getWindow(); // For displaying alerts on top of current window.
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+e.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.initOwner(currentStage);
            alert.showAndWait();
        }

        for(String category:categories)
        {
            Button button = new Button(category); // Create a new button

            button.setOnMouseClicked(this::displayCategoryItems);
            button.getStyleClass().add("category-button");
            button.setPrefWidth(160);
            button.setPrefHeight(80);
            button.setCursor(Cursor.HAND);
            categoriesVBox.getChildren().add(button);
        }

        //Populating category drop down menu (ADD ITEM)
        itemCategoryComboBox.setItems(categories);

        //Populating availability drop down menu (ADD ITEM)
        itemAvailabilityComboBox.setItems(FXCollections.observableArrayList("Available","NOT Available"));

        //Adding Input Validation
        addNumericInputFieldValidation(itemPriceTextField);

        //Adding Input Validation
        addAlphabeticInputFieldValidation(itemNameTextField);


        /*
        //Binding keyboard keys with respective function
        MouseEvent fakeEvent = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null);

        menuTable.setOnKeyPressed(event ->
        {
            if(event.getCode()==KeyCode.BACK_SPACE || event.getCode()==KeyCode.DELETE) // DELETE KEY FOR DELETING ITEM
            {
                deleteItem(fakeEvent);
            }

            if(event.getCode() == KeyCode.ENTER) //ENTER KEY FOR SELECTING A ITEM
            {
                populateAddUpdateDeleteForm(fakeEvent);
            }
        });
        */

    }

    /**
     *
     * For Input Validation.
     * Desc - Function adds event listener to the fields and uses regex to match input .
     * only allows Aplhabets , " - " and nums 1-9
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



    public void addVariantButtonListener()
    {

        TextField variant = new TextField();
        variant.setId("variant"+variantCount);
        variant.setPrefSize(309,38);
        variant.setMinHeight(38);
        variant.setPromptText("Variants");
        addAlphabeticInputFieldValidation(variant);

        TextField variantPrice = new TextField();
        variantPrice.setId("variantPrice"+variantCount);
        variantPrice.setPrefSize(208,38);
        variantPrice.setMinHeight(38);
        variantPrice.setPromptText("Variant Price");
        addNumericInputFieldValidation(variantPrice);

        Button removeVariantButton = new Button();
        removeVariantButton.setId("removeVariant"+variantCount);
        removeVariantButton.setText("X");
        removeVariantButton.getStyleClass().add("remove-variant-button");
        removeVariantButton.setCursor(Cursor.HAND);


        HBox hBox = new HBox();
        hBox.setId("variantHbox"+variantCount);
        hBox.setMinHeight(40);
        hBox.getChildren().addAll(variant,variantPrice,removeVariantButton);
        hBox.setSpacing(10);

        removeVariantButton.setOnAction(e -> {

            variantCount--;

            Node parent = removeVariantButton.getParent();

            if (parent instanceof HBox) {
                HBox hBoxToRemove = (HBox) parent;
                variantVbox.getChildren().remove(hBoxToRemove);
            }
        });

        variantVbox.getChildren().add(variantVbox.getChildren().size() - 1, hBox); // Add above the button

        variantCount++;

    }

    public void displayUpdatedCategoryList()
    {
        //Displaying Updated Category List
        try {
            ObservableList<String> categories = daoimpl.fetchCategories();

            categoriesVBox.getChildren().clear();

            for(String category:categories)
            {
                Button button = new Button(category); // Create a new button

                button.setOnMouseClicked(this::displayCategoryItems);
                button.getStyleClass().add("category-button");
                button.setPrefWidth(171);
                button.setPrefHeight(80);
                button.setCursor(Cursor.HAND);
                categoriesVBox.getChildren().add(button);
            }
        }
        catch(Exception e)
        {
            e.getMessage();
            currentStage = (Stage) menuTable.getScene().getWindow(); // For displaying alerts on top of current window.
            Alert exAlert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+e.getMessage(), ButtonType.OK);
            exAlert.setHeaderText("Failed");
            exAlert.setTitle("Error!");
            exAlert.initOwner(currentStage);
            exAlert.showAndWait();
        }
    }

    public void displayCategoryItems(String category)
    {
        try
        {
            menuTableItems = daoimpl.fetchCategoryItems(category);
        }
        catch (Exception ex)
        {
            currentStage = (Stage) menuTable.getScene().getWindow(); // For displaying alerts on top of current window.
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.initOwner(currentStage);
            alert.showAndWait();
        }

        // Create a cell value factory for the Name column
        TableColumn<Menu, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));
        nameCol.setPrefWidth(200);
        nameCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Price column
        TableColumn<Menu, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));
        priceCol.setPrefWidth(200);
        priceCol.setStyle("-fx-alignment: CENTER;");
        //Displaying the price value in col, only if variant is not added
        priceCol.setCellValueFactory(cellData -> {
            Menu menuItem = cellData.getValue();
            if (menuItem.getVariantData() == null || menuItem.getVariantData().isEmpty()) {
                return new SimpleStringProperty(String.valueOf(menuItem.getFoodItemPrice()));
            } else {
                return new SimpleStringProperty("N/A");
            }
        });

        // Create a cell value factory for the Availability column
        TableColumn<Menu, String> availCol = new TableColumn<>("Availability");
        availCol.setCellValueFactory(new PropertyValueFactory<>("foodItemAvailability"));
        availCol.setPrefWidth(150);
        // Set the background color of the "Availability" cell based on its content
        availCol.setCellFactory(column -> new TableCell<Menu, String>()
        {
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

        // Create a cell value factory for the Stock Quantity column
        TableColumn<Menu, String> stockCol = new TableColumn<>("Stock Quantity");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        stockCol.setPrefWidth(100);
        stockCol.setStyle("-fx-alignment: CENTER;");
        stockCol.setCellValueFactory(cellData ->
        {
            Menu menuItem = cellData.getValue();
            ObservableList<Variant> variants = menuItem.getVariantData();

            if (variants == null || variants.isEmpty())
            {

                // If variantData is null or empty, display default stock quantity
                return new SimpleStringProperty(String.valueOf(menuItem.getStockQuantity()));
            }
            else
            {
                try {

                    boolean doesAllVariantRecipeExists = true;
                    for (Variant variant : variants)
                    {
                        int recipeId = daoimpl.checkIfRecipeExists(menuItem.getId(),variant.getVariantName());
                        if( recipeId <= 0)
                        {
                            doesAllVariantRecipeExists = false;
                            return new SimpleStringProperty(String.valueOf("N/A"));
                        }
                    }

                    if(doesAllVariantRecipeExists)
                    {
                        //Fetch variants stock
                        String variantsAndQuantity = "";
                        for (Variant variant : variants) {
                            int quantity = 0;

                            quantity = daoimpl.calculateStockQuantity(menuItem, variant.getVariantName());

                            variantsAndQuantity = variantsAndQuantity + quantity + ", ";
                            variant.setStockQuantity(String.valueOf(quantity));
                        }

                        return new SimpleStringProperty(variantsAndQuantity);
                    }
                }
                catch (Exception e) {

                    throw new RuntimeException(e);
                }
            }
            return new SimpleStringProperty(String.valueOf("N/A"));
        });

        // Create a cell value factory for the Variant column
        TableColumn<Menu, String> variantCol = new TableColumn<>("Variant");
        variantCol.setCellValueFactory(new PropertyValueFactory<>("variantData"));
        variantCol.setPrefWidth(200);
        variantCol.setStyle("-fx-alignment: CENTER;");
        //Displaying variant data after formating
        variantCol.setCellValueFactory(cellData ->
        {
            Menu menuItem = cellData.getValue();
            ObservableList<Variant> variantData = menuItem.getVariantData();
            if (variantData != null && !variantData.isEmpty()) {
                StringBuilder variants = new StringBuilder();
                variantData.forEach(variant -> variants.append(variant.getVariantName()).append(", "));
                // Remove the trailing comma and space
                return new SimpleStringProperty(variants.substring(0, variants.length() - 2));
            } else {
                return new SimpleStringProperty(" N/A");
            }
        });


        //Create col for Buttons
        TableColumn<Menu,Void> buttonCol = new TableColumn<>("");
        buttonCol.setCellFactory(param -> new TableCell<Menu, Void>()
        {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                {
                    setGraphic(null);
                }
                else
                {
                    Button deleteButton = new Button("Delete");
                    Image delete_symbol = new Image(Objects.requireNonNull(getClass().getResource("/images/black/delete_FILL0_wght400_GRAD0_opsz48.png")).toExternalForm());
                    ImageView imageView = new ImageView(delete_symbol);
                    imageView.setFitWidth(16); // Adjust the width as needed
                    imageView.setFitHeight(16);
                    deleteButton.setGraphic(imageView);

                    {
                        deleteButton.getStyleClass().add("delete-button");
                        deleteButton.setTooltip(new javafx.scene.control.Tooltip("Delete the Menu Item"));
                        deleteButton.setOnAction(event ->
                        {
                            Menu selectedItem = getTableView().getItems().get(getIndex());

                            deleteItem(selectedItem);

                        });
                    }

                    // Set buttons into cell
                    HBox hBox = new HBox();
                    hBox.setSpacing(10);
                    hBox.setPadding(new Insets(10,10,10,10));
                    hBox.getChildren().addAll(deleteButton);
                    setGraphic(hBox);
                }
            }
        });

        // Set the cell value factories for the table columns
        menuTable.getColumns().setAll(nameCol, priceCol, availCol, stockCol, variantCol, buttonCol);
        menuTable.setItems(menuTableItems);


    }

    public void displayCategoryItems(MouseEvent e)
    {
        Button clickedButton = (Button) e.getSource();
        String category = clickedButton.getText();

        try {
            menuTableItems = daoimpl.fetchCategoryItems(category);
        }
        catch (Exception ex)
        {
            currentStage = (Stage) menuTable.getScene().getWindow(); // For displaying alerts on top of current window.
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.initOwner(currentStage);
            alert.showAndWait();
        }

        // Create a cell value factory for the Name column
        TableColumn<Menu, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(80);
        idCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Name column
        TableColumn<Menu, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));
        nameCol.setPrefWidth(200);
        nameCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Price column
        TableColumn<Menu, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));
        priceCol.setPrefWidth(200);
        priceCol.setStyle("-fx-alignment: CENTER;");
        //Displaying the price value in col, only if variant is not added
        priceCol.setCellValueFactory(cellData -> {
            Menu menuItem = cellData.getValue();
            if (menuItem.getVariantData() == null || menuItem.getVariantData().isEmpty()) {
                return new SimpleStringProperty(String.valueOf(menuItem.getFoodItemPrice()));
            } else {
                return new SimpleStringProperty("N/A");
            }
        });

        // Create a cell value factory for the Availability column
        TableColumn<Menu, String> availCol = new TableColumn<>("Availability");
        availCol.setCellValueFactory(new PropertyValueFactory<>("foodItemAvailability"));
        availCol.setPrefWidth(150);
        // Set the background color of the "Availability" cell based on its content
        availCol.setCellFactory(column -> new TableCell<Menu, String>()
        {
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

        // Create a cell value factory for the Stock Quantity column
        TableColumn<Menu, String> stockCol = new TableColumn<>("Stock Quantity");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        stockCol.setPrefWidth(100);
        stockCol.setStyle("-fx-alignment: CENTER;");
        stockCol.setCellValueFactory(cellData ->
        {
            Menu menuItem = cellData.getValue();
            ObservableList<Variant> variants = menuItem.getVariantData();

            if (variants == null || variants.isEmpty())
            {

                // If variantData is null or empty, display default stock quantity
                return new SimpleStringProperty(String.valueOf(menuItem.getStockQuantity()));
            }
            else
            {
                try {

                    boolean doesAllVariantRecipeExists = true;
                    for (Variant variant : variants)
                    {
                        int recipeId = daoimpl.checkIfRecipeExists(menuItem.getId(),variant.getVariantName());
                        if( recipeId <= 0)
                        {
                            doesAllVariantRecipeExists = false;
                            return new SimpleStringProperty(String.valueOf("N/A"));
                        }
                    }

                    if(doesAllVariantRecipeExists)
                    {
                        //Fetch variants stock
                        String variantsAndQuantity = "";
                        for (Variant variant : variants) {
                            int quantity = 0;

                            quantity = daoimpl.calculateStockQuantity(menuItem, variant.getVariantName());

                            variantsAndQuantity = variantsAndQuantity + quantity + ", ";
                            variant.setStockQuantity(String.valueOf(quantity));
                        }

                        return new SimpleStringProperty(variantsAndQuantity);
                    }
                }
                catch (Exception ex) {

                    throw new RuntimeException(ex);
                }
            }
            return new SimpleStringProperty(String.valueOf("N/A"));
        });

        // Create a cell value factory for the Variant column
        TableColumn<Menu, String> variantCol = new TableColumn<>("Variant");
        variantCol.setCellValueFactory(new PropertyValueFactory<>("variantData"));
        variantCol.setPrefWidth(200);
        variantCol.setStyle("-fx-alignment: CENTER;");
        //Displaying variant data after formating
        variantCol.setCellValueFactory(cellData -> {
            Menu menuItem = cellData.getValue();
            ObservableList<Variant> variantData = menuItem.getVariantData();
            if (variantData != null && !variantData.isEmpty()) {
                StringBuilder variants = new StringBuilder();
                variantData.forEach(variant -> variants.append(variant.getVariantName()).append(", "));
                // Remove the trailing comma and space
                return new SimpleStringProperty(variants.substring(0, variants.length() - 2));
            } else {
                return new SimpleStringProperty(" N/A");
            }
        });

        //Create col for Buttons
        TableColumn<Menu,Void> buttonCol = new TableColumn<>("");
        buttonCol.setCellFactory(param -> new TableCell<Menu, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                {
                    setGraphic(null);
                }
                else
                {
                    Button deleteButton = new Button("Delete");
                    Image delete_symbol = new Image(Objects.requireNonNull(getClass().getResource("/images/black/delete_FILL0_wght400_GRAD0_opsz48.png")).toExternalForm());
                    ImageView imageView = new ImageView(delete_symbol);
                    imageView.setFitWidth(16); // Adjust the width as needed
                    imageView.setFitHeight(16);
                    deleteButton.setGraphic(imageView);
                    deleteButton.setCursor(Cursor.HAND);

                    {
                        deleteButton.getStyleClass().add("delete-button");
                        deleteButton.setTooltip(new javafx.scene.control.Tooltip("Delete the Menu Item"));
                        deleteButton.setOnAction(event ->
                        {
                            Menu selectedItem = getTableView().getItems().get(getIndex());

                            deleteItem(selectedItem);

                        });
                    }

                    // Set buttons into cell
                    HBox hBox = new HBox();
                    hBox.setSpacing(10);
                    hBox.setPadding(new Insets(10,10,10,10));
                    hBox.getChildren().addAll(deleteButton);
                    setGraphic(hBox);
                }
            }
        });

        // Set the cell value factories for the table columns
        menuTable.getColumns().setAll(idCol, nameCol, priceCol, availCol, stockCol, variantCol, buttonCol);
        menuTable.setItems(menuTableItems);

    }

    public void populateAddUpdateDeleteForm(MouseEvent event)
    {

        Menu selectedItem = menuTable.getSelectionModel().getSelectedItem();

        if(selectedItem == null)
            return;

        //Populating ID Field
        itemId.setText(Integer.toString(selectedItem.getId()));

        //Populating Name Field
        itemNameTextField.setText(selectedItem.getFoodItemName());

        //Populating Category Field
        itemCategoryComboBox.setValue(selectedItem.getFoodItemCategory());

        //Populating Availability Field
        itemAvailabilityComboBox.setValue(selectedItem.getFoodItemAvailability());

        //Displaying Variant Data
        ObservableList<Variant> variantData = selectedItem.getVariantData();
        if(variantData == null)
        {
            //Clearing Previously Displayed data
            clearPreviousItemsVariantData();
        }
        else
        {

            //Clearing Previously Displayed data
            clearPreviousItemsVariantData();


            variantVbox.setDisable(false);

            variantCount=0;
            for(Variant variant : variantData)
            {
                TextField variantName = new TextField();
                variantName.setId("variant"+variantCount);
                variantName.setPrefSize(309,38);
                variantName.setMinHeight(38);
                variantName.setPromptText("Variants");
                variantName.setText(variant.getVariantName());
                addAlphabeticInputFieldValidation(variantName);

                TextField variantPrice = new TextField();
                variantPrice.setId("variantPrice"+variantCount);
                variantPrice.setPrefSize(208,38);
                variantPrice.setMinHeight(38);
                variantPrice.setPromptText("Variant Price");
                variantPrice.setText(String.valueOf(variant.getVariantPrice()));
                addNumericInputFieldValidation(variantPrice);

                Button removeVariantButton = new Button();
                removeVariantButton.setId("removeVariant"+variantCount);
                removeVariantButton.setText("X");
                removeVariantButton.getStyleClass().add("remove-variant-button");
                removeVariantButton.setCursor(Cursor.HAND);


                HBox hBox = new HBox();
                hBox.setId("variantHbox"+variantCount);
                hBox.setMinHeight(38);
                hBox.getChildren().addAll(variantName,variantPrice,removeVariantButton);
                hBox.setSpacing(10);

                removeVariantButton.setOnAction(e -> {

                    variantCount--;

                    Node parent = removeVariantButton.getParent();

                    if (parent instanceof HBox) {
                        HBox hBoxToRemove = (HBox) parent;
                        variantVbox.getChildren().remove(hBoxToRemove);
                    }
                });

                variantVbox.getChildren().add(variantVbox.getChildren().size() - 1, hBox); // Add above the button

                variantCount++;
            }

        }

        //Populating Price Field (if Variants dont Exist)
        HBox variantHbox = (HBox) variantVbox.lookup("#variantHbox0");
        if(variantHbox==null)
        {
            itemPriceTextField.setDisable(false);
            itemPriceTextField.setText(Double.toString(selectedItem.getFoodItemPrice()));
        }
        else
        {
            itemPriceTextField.setDisable(true);
            itemPriceTextField.setText(Double.toString(selectedItem.getFoodItemPrice()));
        }
    }

    public void addToMenu(MouseEvent ignoredEvent) throws SQLException
    {

        //Checking if all field values are filled by the user (except price)
        if (itemNameTextField.getText().isEmpty() || itemCategoryComboBox.getSelectionModel().getSelectedItem() == null || itemAvailabilityComboBox.getSelectionModel().getSelectedItem() == null)
        {
            currentStage = (Stage) menuTable.getScene().getWindow(); // For displaying alerts on top of current window.
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Fields Cannot be Empty, populate all fields", ButtonType.OK);
            alert.setHeaderText("No Item Selected");
            alert.setTitle("Alert!");
            alert.initOwner(currentStage);
            alert.showAndWait();

            return;
        }

        //Checking if Price field value is entered or not. If not entered, only allow to proceed if variants are added.
        if(itemPriceTextField.getText().isEmpty() && variantCount <= 0 )
        {
            currentStage = (Stage) menuTable.getScene().getWindow(); // For displaying alerts on top of current window.
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Fields Cannot be Empty, populate all fields", ButtonType.OK);
            alert.setHeaderText("No Item Selected");
            alert.setTitle("Alert!");
            alert.initOwner(currentStage);
            alert.showAndWait();

            return;
        }
        else
        {
            if(itemPriceTextField.getText().length() > 6 )
            {
                currentStage = (Stage) menuTable.getScene().getWindow(); // For displaying alerts on top of current window.
                Alert alert = new Alert(Alert.AlertType.WARNING, "The price of a single food item can not be more than 1,00,000", ButtonType.OK);
                alert.getDialogPane().setMinHeight(Region.USE_COMPUTED_SIZE);
                alert.getDialogPane().setMaxHeight(Region.USE_COMPUTED_SIZE);
                alert.setHeaderText("Price Limit Exceeded");
                alert.setTitle("Alert!");
                alert.initOwner(currentStage);
                alert.showAndWait();

                itemPriceTextField.setText("");
                itemPriceTextField.requestFocus();
                return;
            }
        }


        String itemName = itemNameTextField.getText();
        Double itemPrice =  itemPriceTextField.getText()  == null ||  itemPriceTextField.getText().isEmpty() ? 0.0 : Double.parseDouble(   itemPriceTextField.getText()  ) ;
        String itemCategory = itemCategoryComboBox.getSelectionModel().getSelectedItem().toString();
        String itemAvailability = itemAvailabilityComboBox.getSelectionModel().getSelectedItem().toString();

        // Checking and Adding Variants
        ObservableList<Variant> variantData = FXCollections.observableArrayList();
        int temp = variantCount;

        while(temp>=0)
        {
            HBox hBox = (HBox) variantVbox.lookup("#variantHbox" + temp);
            if(hBox != null)
            {
                TextField variantNameField = (TextField) hBox.lookup("#variant" + temp);
                TextField variantPriceField = (TextField) hBox.lookup("#variantPrice" + temp);

                if( variantNameField.getText().isEmpty() || variantPriceField.getText().isEmpty() )
                {
                    currentStage = (Stage) menuTable.getScene().getWindow(); // For displaying alerts on top of current window.
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Variants Name or Price fields cannot be empty", ButtonType.OK);
                    alert.setHeaderText("Can not Add item!");
                    alert.setTitle("Alert!");
                    alert.initOwner(currentStage);
                    alert.showAndWait();

                    return;
                }
                else
                {
                    variantData.add( new Variant( variantNameField.getText() , Double.parseDouble(variantPriceField.getText()) ,"0") );
                }
            }

            temp--;
        }


        ObservableList<String> categories = daoimpl.fetchCategories();

        Menu item = new Menu();
        item.setFoodItemName(   itemName   );
        item.setFoodItemPrice(   itemPrice   );
        item.setFoodItemAvailability(   itemAvailability    );
        item.setFoodItemCategory(   itemCategory    );
        item.setVariantData(    variantData   );
        item.setStockQuantity(String.valueOf(0.0));


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
                e.getMessage();
                currentStage = (Stage) menuTable.getScene().getWindow(); // For displaying alerts on top of current window.
                Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+e.getMessage(), ButtonType.OK);
                alert.setHeaderText("Failed");
                alert.setTitle("Error!");
                alert.initOwner(currentStage);
                alert.showAndWait();
            }

            if (isSuccess)
            {
                currentStage = (Stage) menuTable.getScene().getWindow(); // For displaying alerts on top of current window.
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Item added Successfully", ButtonType.OK);
                alert.setHeaderText("Success");
                alert.setTitle("Information");
                alert.initOwner(currentStage);
                alert.showAndWait();

                //Clearing All fields after adding successfully
                clearAllFields(variantCount);

                //Displaying Updated Category List
                displayCategoryItems(itemCategory);
            }
            else
            {
                currentStage = (Stage) menuTable.getScene().getWindow(); // For displaying alerts on top of current window.
                Alert alert = new Alert(Alert.AlertType.ERROR, "Item Already Exists! If item does not already exist and you are still seeing this error, Contact customer Support!", ButtonType.OK);
                alert.getDialogPane().setMinHeight(Region.USE_COMPUTED_SIZE);
                alert.getDialogPane().setMaxHeight(Region.USE_COMPUTED_SIZE);
                alert.setHeaderText("Duplicate Item Entry");
                alert.setTitle("Information");
                alert.initOwner(currentStage);
                alert.showAndWait();
            }
        }
        else //If category DOES NOT already exist asking user ,If user wants NEW cateogry to be created
        {
            currentStage = (Stage) menuTable.getScene().getWindow(); // For displaying alerts on top of current window.
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to add a new Category - "+ item.getFoodItemCategory() +" ?", ButtonType.YES,ButtonType.NO);
            confirmationAlert.setHeaderText("Alert! ");
            confirmationAlert.setTitle("Information");
            confirmationAlert.initOwner(currentStage);
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
                    e.getMessage();
                    currentStage = (Stage) menuTable.getScene().getWindow(); // For displaying alerts on top of current window.
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+e.getMessage(), ButtonType.OK);
                    alert.setHeaderText("Failed");
                    alert.setTitle("Error!");
                    alert.initOwner(currentStage);
                    alert.showAndWait();
                }

                if(isSuccess)
                {
                    currentStage = (Stage) menuTable.getScene().getWindow(); // For displaying alerts on top of current window.
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Item added Successfully", ButtonType.OK);
                    alert.setHeaderText("Success");
                    alert.setTitle("Information");
                    alert.initOwner(currentStage);
                    alert.showAndWait();

                    //Clearing All fields after adding successfully
                    clearAllFields(variantCount);

                    //Displaying Updated Category List
                    displayUpdatedCategoryList();

                }
                else
                {
                    currentStage = (Stage) menuTable.getScene().getWindow(); // For displaying alerts on top of current window.
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Item with same Name already Exists! If item does not already exist and you are still seeing this error, Contact customer Support!", ButtonType.OK);
                    alert.setHeaderText("Duplicate Item Entry");
                    alert.setTitle("Information");
                    alert.initOwner(currentStage);
                    alert.showAndWait();
                }
            }
        }
    }

    public void updateItem(MouseEvent event)
    {
        //Checking if all field values are filled by the user (except price)
        if (itemNameTextField.getText().isEmpty() || itemCategoryComboBox.getSelectionModel().getSelectedItem() == null || itemAvailabilityComboBox.getSelectionModel().getSelectedItem() == null)
        {
            currentStage = (Stage) menuTable.getScene().getWindow(); // For displaying alerts on top of current window.
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Fields Cannot be Empty, Select an item and modify values and click 'Update'", ButtonType.OK);
            alert.setHeaderText("Empty fields found!");
            alert.setTitle("Alert!");
            alert.initOwner(currentStage);
            alert.showAndWait();

            return;
        }

        //Checking if Price field value is entered or not. If not entered, only allow to proceed if variants are added.
        if(itemPriceTextField.getText().isEmpty() && (HBox)variantVbox.lookup("#variantHbox0") == null )
        {
            currentStage = (Stage) menuTable.getScene().getWindow(); // For displaying alerts on top of current window.
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Fields Cannot be Empty, Select an item and modify values and click 'Update'", ButtonType.OK);
            alert.setHeaderText("Empty fields found!");
            alert.setTitle("Alert!");
            alert.initOwner(currentStage);
            alert.showAndWait();

            return;
        }

        //Checking if user has selected any item to be updated or not
        Menu selectedItem = menuTable.getSelectionModel().getSelectedItem();
        if(selectedItem == null)
        {
            currentStage = (Stage) menuTable.getScene().getWindow(); // For displaying alerts on top of current window.
            Alert updateAlert = new Alert(Alert.AlertType.WARNING, "Select an item from Menu to Update", ButtonType.OK);
            updateAlert.setHeaderText("No item selected to update.");
            updateAlert.setTitle("Warning!");
            updateAlert.initOwner(currentStage);
            updateAlert.showAndWait();

            return;
        }

        //Update Confirmation
        currentStage = (Stage) menuTable.getScene().getWindow(); // For displaying alerts on top of current window.
        Alert updateAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?", ButtonType.YES , ButtonType.NO);
        updateAlert.setHeaderText("Item will be Updated and Inventory tracking will be turned OFF. \n (Can be turned ON manually)");
        updateAlert.setTitle("Alert!");
        updateAlert.initOwner(currentStage);
        updateAlert.showAndWait();

        if(updateAlert.getResult() == ButtonType.YES)
        {

            int id = Integer.parseInt(itemId.getText());
            String itemName = itemNameTextField.getText();
            Double itemPrice = Double.parseDouble(   itemPriceTextField.getText()  );
            String itemCategory = itemCategoryComboBox.getSelectionModel().getSelectedItem().toString();
            String itemAvailability = itemAvailabilityComboBox.getSelectionModel().getSelectedItem().toString();

            // Checking and Adding Variants
            ObservableList<Variant> variantData = FXCollections.observableArrayList();
            int temp = variantCount;
            //temp--;

            while(temp>=0)
            {
                HBox hBox = (HBox) variantVbox.lookup("#variantHbox" + temp);
                if(hBox != null)
                {
                    TextField variantNameField = (TextField) hBox.lookup("#variant" + temp);
                    TextField variantPriceField = (TextField) hBox.lookup("#variantPrice" + temp);

                    if( variantNameField.getText().isEmpty() || variantPriceField.getText().isEmpty() )
                    {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Variants Name or Price fields cannot be empty", ButtonType.OK);
                        alert.setHeaderText("Item not Updated!");
                        alert.setTitle("Alert!");
                        alert.showAndWait();

                        return;
                    }
                    else
                    {
                        variantData.add( new Variant( variantNameField.getText() , Double.parseDouble(variantPriceField.getText()) , "0") );
                    }
                }

                temp--;
            }

            Menu item = new Menu();
            item.setId( id  );
            item.setFoodItemName(   itemName    );
            item.setFoodItemPrice(  itemPrice   );
            item.setFoodItemCategory(   itemCategory    );
            item.setFoodItemAvailability(   itemAvailability    );
            item.setVariantData(    variantData    );
            item.setIsInventoryTracked("OFF");

            try {
                if (!daoimpl.updateMenuItem(item))
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Could not Update Item. If this error keeps occurring contact customer support.", ButtonType.OK);
                    alert.setHeaderText("Item not Update!");
                    alert.setTitle("Alert!");
                    alert.showAndWait();
                }
                else
                {
                    displayCategoryItems(selectedItem.getFoodItemCategory()); //SHOW TABLE WITH UPDATED ITEMS
                }
            }
            catch (Exception ex)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Database update operation Exception - "+ex.getMessage(), ButtonType.OK);
                alert.setHeaderText("Failed");
                alert.setTitle("Error!");
                alert.showAndWait();
            }
        }

    }

    public void deleteItem(Menu selectedItem)
    {

        currentStage = (Stage) menuTable.getScene().getWindow(); // For displaying alerts on top of current window.
        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION, "ARE YOU SURE ?", ButtonType.YES , ButtonType.NO);
        deleteAlert.setHeaderText("Item will be deleted");
        deleteAlert.setTitle("Alert!");
        deleteAlert.initOwner(currentStage);
        deleteAlert.showAndWait();

        if(deleteAlert.getResult() == ButtonType.YES)
        {
            if(selectedItem == null)
                return;

            try {
                if (!daoimpl.deleteMenuItem(selectedItem.getId()))
                {
                    currentStage = (Stage) menuTable.getScene().getWindow(); // For displaying alerts on top of current window.
                    Alert alert = new Alert(Alert.AlertType.ERROR, "COULD NOT DELETE ITEM. If this error keeps occuring contact customer support.", ButtonType.OK);
                    alert.setHeaderText("Item not deleted!");
                    alert.setTitle("Alert!");
                    alert.initOwner(currentStage);
                    alert.showAndWait();
                }
                else
                {
                    //Clearing Fields
                    itemNameTextField.clear();
                    itemPriceTextField.clear();
                    itemPriceTextField.setDisable(false);
                    itemAvailabilityComboBox.getSelectionModel().clearSelection();
                    itemId.setText("_ _");
                    itemCategoryComboBox.getSelectionModel().clearSelection();

                    //Clearing variants
                    for (int i = 0; i < variantVbox.getChildren().size(); i++) {
                        Node child = variantVbox.getChildren().get(i);
                        if (child instanceof HBox) {
                            variantVbox.getChildren().remove(i);
                            i--; // Decrement i since we removed an element
                        }
                    }


                    //SHOW TABLE WITH UPDATED ITEMS
                    displayCategoryItems(selectedItem.getFoodItemCategory());

                    //SHOW UPDATED CATEGORY LIST
                    displayUpdatedCategoryList();
                }
            }
            catch (Exception ex)
            {
                currentStage = (Stage) menuTable.getScene().getWindow(); // For displaying alerts on top of current window.
                Alert alert = new Alert(Alert.AlertType.ERROR, "Database delete operation Exception - "+ex.getMessage(), ButtonType.OK);
                alert.setHeaderText("Failed");
                alert.setTitle("Error!");
                alert.initOwner(currentStage);
                alert.showAndWait();
            }
        }

    }

    public void clearPreviousItemsVariantData()
    {
        int temp = variantCount;
        for(int i=0;i<temp;i++)
        {
            HBox variantHbox = (HBox) variantVbox.lookup("#variantHbox"+i);
            variantVbox.getChildren().remove(variantHbox);
        }

        variantCount=0;
    }

    public void clearAllFields(int variantCount)
    {
        itemNameTextField.clear();
        itemPriceTextField.clear();
        itemCategoryComboBox.getSelectionModel().clearSelection();
        itemAvailabilityComboBox.getSelectionModel().clearSelection();


        int temp = variantCount;
        while(temp>=0)
        {
            HBox hboxToRemove = (HBox) variantVbox.lookup("#variantHbox"+temp);

            if (hboxToRemove != null)
            {
                variantVbox.getChildren().remove(hboxToRemove);
            }
            temp--;
        }
    }
}
