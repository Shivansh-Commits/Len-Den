package org.lenden;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.lenden.dao.DaoImpl;
import org.lenden.model.Inventory;
import org.lenden.model.Menu;
import org.lenden.model.Recipe;
import org.lenden.model.Variant;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class RecipeManagerController implements Initializable {

    @FXML
    TableView<Menu> menuItemsTable;
    @FXML
    TextField searchTextField;
    @FXML
    Label menuItemIdLabel;
    @FXML
    Label menuItemNameLabel;
    @FXML
    Label variantLabel;
    @FXML
    Label categoryLabel;
    @FXML
    VBox rawMaterialVbox;
    @FXML
    Label recipeIdLabel;
    @FXML
    Button addRawMaterialButton;

    ObservableList<Menu> menuTableItems =  FXCollections.observableArrayList();
    DaoImpl daoimpl = new DaoImpl();
    int rawMaterialCount = 0;
    Stage currentStage;
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

        //Displaying All Menu Items
        try
        {
            displayAllMenuItems();
        }
        catch (Exception e)
        {

            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not display Menu Items." + e.getMessage(), ButtonType.OK);
            alert.setHeaderText("Exception in displaying itmes");
            alert.setTitle("Exception!");
            alert.initOwner(currentStage);
            alert.showAndWait();


            throw new RuntimeException(e);
        }

        //-----------------ADDING EVENT LISTENER TO SEARCH FIELD-------------------------

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Filter the data based on the text in the TextField
            // You may need to implement your own filtering logic here
            ObservableList<Menu> filteredItems = FXCollections.observableArrayList();

            try {

                for (Menu item : daoimpl.fetchAllCategoryItems()) {
                    // Add items that match the filter criteria
                    if (item.getFoodItemName().toLowerCase().contains(newValue.toLowerCase())) {
                        filteredItems.add(item);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // Set the filtered items to be displayed in the TableView
            menuItemsTable.setItems(filteredItems);
        });

    }


    private void addAlphabeticInputFieldValidation(TextField textField)
    {
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
    private void addNumericInputFieldValidation(TextField textField)
    {
        // Event filter to allow only numeric characters and the decimal point "."
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String input = event.getCharacter();

            // Use a regular expression to check if the input is numeric or a decimal point
            if (!input.matches("[0-9.]")) {
                event.consume(); // Ignore the input if it's not numeric or a decimal point
            }
        });
    }

    /**
     *
     * For Input Validation.
     * Desc - Function adds event listener to the fields and uses regex to match input .
     * only allows Aplhabets , " - " and nums 1-9
     * **/
    private void addAlphabeticInputFieldValidation(ComboBox comboBox)
    {
        // Event filter to allow only alphabetic characters and the hyphen "-"
        comboBox.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String input = event.getCharacter();

            // Use a regular expression to check if the input is alphabetic or a hyphen
            if (!input.matches("[a-zA-Z0-9 ]")) {
                event.consume(); // Ignore the input if it's not alphabetic or a hyphen
            }
        });
    }

    private void makeComboBoxSearchable(ComboBox<String> comboBox, ObservableList<String> originalItems)
    {
        //comboBox.setEditable(true);

        // Event listener for when the user types in the combo box editor
        comboBox.getEditor().setOnKeyReleased(event -> {
            String filter = comboBox.getEditor().getText().toLowerCase();
            ObservableList<String> filteredItems = originalItems.filtered(item -> item.toLowerCase().contains(filter));
            comboBox.setItems(filteredItems);
        });

        // Event listener for when the user selects an item from the drop-down list
        comboBox.setOnAction(event -> comboBox.setItems(originalItems));

        // Event listener for when the combo box editor loses focus
        comboBox.getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                comboBox.setItems(originalItems);
            }
        });
    }


    public void displayAllMenuItems() throws Exception
    {

        try
        {
            menuTableItems = daoimpl.fetchAllCategoryItems();
        }
        catch(Exception e)
         {
             Alert alert = new Alert(Alert.AlertType.ERROR, "Could not display Menu Items. Database Exception" + e.getMessage(), ButtonType.OK);
             alert.setHeaderText("Exception in displaying itmes");
             alert.setTitle("Exception!");
             alert.initOwner(currentStage);
             alert.showAndWait();
         }


        // Create a cell value factory for the ID column
        TableColumn<Menu, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(80);
        idCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Category column
        TableColumn<Menu, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("foodItemCategory"));
        categoryCol.setPrefWidth(100);
        categoryCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Name column
        TableColumn<Menu, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));
        nameCol.setPrefWidth(200);
        nameCol.setStyle("-fx-alignment: CENTER;");


        // Create a cell value factory for the Price column
        TableColumn<Menu, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));
        priceCol.setPrefWidth(100);
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
            return new SimpleStringProperty("N/A");
        });


        // Create a cell value factory for the Variant column
        TableColumn<Menu, String> variantCol = new TableColumn<>("Variants");
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
        TableColumn<Menu,String> isInventoryTrackedCol = new TableColumn<>("Inventory Tracking");
        isInventoryTrackedCol.setCellValueFactory(new PropertyValueFactory<>("isInventoryTracked"));
        isInventoryTrackedCol.setCellFactory(param -> new TableCell<Menu, String>()
        {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                {
                    setGraphic(null);
                }
                else
                {
                    Menu selectedItem = getTableView().getItems().get(getIndex());

                    Button inventoryTrackingButton = new Button();
                    if(item.equals("ON") )
                    {
                        inventoryTrackingButton.setCursor(Cursor.HAND);
                        inventoryTrackingButton.setId("inventoryTrackingButton"+selectedItem.getId());
                        inventoryTrackingButton.setPrefWidth(170);
                        inventoryTrackingButton.setText("Tracking ON");
                        inventoryTrackingButton.getStyleClass().add("tracking-on-button");
                        inventoryTrackingButton.setTooltip(new Tooltip("Activate raw material usage and automatically deduct from Inventory"));
                        Image view_symbol = new Image(Objects.requireNonNull(getClass().getResource("/images/black/toggle_on_FILL0_wght400_GRAD0_opsz48.png")).toExternalForm());
                        ImageView imageView = new ImageView(view_symbol);
                        imageView.setFitWidth(28); // Adjust the width as needed
                        imageView.setFitHeight(28);
                        inventoryTrackingButton.setGraphic(imageView);
                    }
                    else
                    {
                        inventoryTrackingButton.setCursor(Cursor.HAND);
                        inventoryTrackingButton.setId("inventoryTrackingButton"+selectedItem.getId());
                        inventoryTrackingButton.setPrefWidth(170);
                        inventoryTrackingButton.setText("Tracking OFF");
                        inventoryTrackingButton.getStyleClass().add("tracking-off-button");
                        inventoryTrackingButton.setTooltip(new Tooltip("De-activate raw material usage and don't deduct from Inventory"));
                        Image view_symbol = new Image(Objects.requireNonNull(getClass().getResource("/images/black/toggle_off_black_48pt_3x.png")).toExternalForm());
                        ImageView imageView = new ImageView(view_symbol);
                        imageView.setFitWidth(28); // Adjust the width as needed
                        imageView.setFitHeight(28);
                        inventoryTrackingButton.setGraphic(imageView);
                    }

                    inventoryTrackingButton.setOnAction(event -> {

                    if(inventoryTrackingButton.getStyleClass().contains("tracking-off-button"))
                    {
                        try
                        {
                            //Checking if variants exists or not and IF they do/do not exists, does recipe for them exists
                            ObservableList<Variant> variants = selectedItem.getVariantData();

                            if(variants == null)
                            {
                                int recipeId = daoimpl.checkIfRecipeExists(selectedItem.getId(),null);
                                if( recipeId > 0)
                                {
                                    // DB OPERATIONS

                                    // Change Tracking status in MENU Table
                                    daoimpl.changeTrackingStatus( selectedItem.getId(), "ON" );

                                    //Fetch Raw material Stock and Update menu item quantity in Table
                                    int quantity = daoimpl.calculateStockQuantity( selectedItem,"N/A" );
                                    selectedItem.setStockQuantity(String.valueOf(quantity));
                                    menuItemsTable.refresh();

                                    //Updating quantity in MENU table
                                    Menu updatedQuantityMenuObject = new Menu();
                                    updatedQuantityMenuObject.setId(selectedItem.getId());
                                    updatedQuantityMenuObject.setStockQuantity(String.valueOf(quantity));
                                    daoimpl.updateMenuItem(updatedQuantityMenuObject);


                                    // UI CHANGES
                                    inventoryTrackingButton.getStyleClass().remove("tracking-off-button");
                                    inventoryTrackingButton.getStyleClass().add("tracking-on-button");
                                    inventoryTrackingButton.setText("Tracking ON");
                                    Image view_symbol1 = new Image(Objects.requireNonNull(getClass().getResource("/images/black/toggle_on_FILL0_wght400_GRAD0_opsz48.png")).toExternalForm());
                                    ImageView imageView1 = new ImageView(view_symbol1);
                                    imageView1.setFitWidth(28); // Adjust the width as needed
                                    imageView1.setFitHeight(28);
                                    inventoryTrackingButton.setGraphic(imageView1);

                                    selectedItem.setIsInventoryTracked("ON");
                                    menuItemsTable.refresh();
                                }
                                else
                                {
                                    currentStage = (Stage) rawMaterialVbox.getScene().getWindow(); // For displaying alerts on top of current window.
                                    Alert delete_alert = new Alert(Alert.AlertType.WARNING, "Add a Recipe to Turn on Inventory Tracking ",ButtonType.OK);
                                    delete_alert.setHeaderText("No Recipe Found");
                                    delete_alert.setTitle("Alert!");
                                    delete_alert.initOwner(currentStage);
                                    delete_alert.showAndWait();
                                }

                            }
                            else
                            {
                                boolean doesAllVariantRecipeExists = true;
                                for (Variant variant : variants)
                                {
                                    int recipeId = daoimpl.checkIfRecipeExists(selectedItem.getId(),variant.getVariantName());
                                    if( recipeId <= 0)
                                    {
                                        currentStage = (Stage) rawMaterialVbox.getScene().getWindow(); // For displaying alerts on top of current window.

                                        Alert delete_alert = new Alert(Alert.AlertType.WARNING, "All Variants should have a Recipe to Activate Inventory Tracking",ButtonType.OK);
                                        delete_alert.setHeaderText("Recipe of '"+ variant.getVariantName() + "' variant Not Found");
                                        delete_alert.setTitle("Alert!");
                                        delete_alert.initOwner(currentStage);
                                        delete_alert.showAndWait();
                                        doesAllVariantRecipeExists = false;
                                        return;
                                    }
                                }

                                if(doesAllVariantRecipeExists)
                                {
                                    // DB OPERATIONS

                                    // Change Tracking status in MENU Table
                                    daoimpl.changeTrackingStatus(selectedItem.getId(), "ON");

                                    //Fetch Raw material Stock and Update menu item quantity in Table
                                    String variantsAndQuantity = "";
                                    for(Variant variant:variants)
                                    {
                                        int recipeId = daoimpl.checkIfRecipeExists(selectedItem.getId(),variant.getVariantName());

                                        int quantity = daoimpl.calculateStockQuantity( selectedItem,variant.getVariantName());
                                        variantsAndQuantity = variantsAndQuantity + quantity + ", ";
                                        variant.setStockQuantity(String.valueOf(quantity));

                                        daoimpl.updateVariants(selectedItem.getId(),variants);
                                    }
                                    selectedItem.setStockQuantity(variantsAndQuantity);
                                    menuItemsTable.refresh();


                                    // UI CHANGES
                                    inventoryTrackingButton.getStyleClass().remove("tracking-off-button");
                                    inventoryTrackingButton.getStyleClass().add("tracking-on-button");
                                    inventoryTrackingButton.setText("Tracking ON");
                                    Image view_symbol1 = new Image(Objects.requireNonNull(getClass().getResource("/images/black/toggle_on_FILL0_wght400_GRAD0_opsz48.png")).toExternalForm());
                                    ImageView imageView1 = new ImageView(view_symbol1);
                                    imageView1.setFitWidth(28); // Adjust the width as needed
                                    imageView1.setFitHeight(28);
                                    inventoryTrackingButton.setGraphic(imageView1);

                                    selectedItem.setIsInventoryTracked("ON");
                                    menuItemsTable.refresh();
                                }

                            }
                        }
                        catch (SQLException e)
                        {
                            currentStage = (Stage) rawMaterialVbox.getScene().getWindow(); // For displaying alerts on top of current window.
                            Alert delete_alert = new Alert(Alert.AlertType.ERROR, "Database Operation Exception : "+e.getMessage(),ButtonType.OK);
                            delete_alert.setHeaderText("Database Exception");
                            delete_alert.setTitle("Error!");
                            delete_alert.initOwner(currentStage);
                            delete_alert.showAndWait();

                            throw new RuntimeException(e);
                        }
                        catch (Exception e)
                        {

                            currentStage = (Stage) rawMaterialVbox.getScene().getWindow(); // For displaying alerts on top of current window.
                            Alert delete_alert = new Alert(Alert.AlertType.ERROR, "Database Operation Exception : "+e.getMessage(),ButtonType.OK);
                            delete_alert.setHeaderText("Database Exception");
                            delete_alert.setTitle("Error!");
                            delete_alert.initOwner(currentStage);
                            delete_alert.showAndWait();

                            throw new RuntimeException(e);
                        }

                    }
                    else
                    {
                        inventoryTrackingButton.getStyleClass().remove("tracking-on-button");
                        inventoryTrackingButton.getStyleClass().add("tracking-off-button");
                        inventoryTrackingButton.setText("Tracking OFF");
                        Image view_symbol2 = new Image(Objects.requireNonNull(getClass().getResource("/images/black/toggle_off_black_48pt_3x.png")).toExternalForm());
                        ImageView imageView2 = new ImageView(view_symbol2);
                        imageView2.setFitWidth(28); // Adjust the width as needed
                        imageView2.setFitHeight(28);
                        inventoryTrackingButton.setGraphic(imageView2);

                        // DB OPERATIONS
                        try
                        {
                            daoimpl.changeTrackingStatus(selectedItem.getId(),"OFF");
                        }
                        catch (SQLException e)
                        {
                            throw new RuntimeException(e);
                        }

                    }

                    });

                    // Set buttons into cell
                    HBox hBox = new HBox();
                    hBox.setSpacing(5);
                    hBox.setPadding(new Insets(5,5,5,5));
                    hBox.getChildren().addAll(inventoryTrackingButton);
                    setGraphic(hBox);
                }
            }
        });


        // Set the cell value factories for the table columns
        menuItemsTable.getColumns().setAll(idCol, categoryCol, nameCol, priceCol, stockCol, variantCol, isInventoryTrackedCol);
        menuItemsTable.setItems(menuTableItems);

    }

    public void addRawMaterialButtonListener()
    {

        ComboBox rawMaterialNameComboBox = new ComboBox();
        rawMaterialNameComboBox.setId("rawMaterial"+rawMaterialCount);
        rawMaterialNameComboBox.setPrefSize(215,38);
        rawMaterialNameComboBox.setMinHeight(38);
        rawMaterialNameComboBox.setPromptText("Raw Material *");
        rawMaterialNameComboBox.setEditable(true);
        addAlphabeticInputFieldValidation(rawMaterialNameComboBox);
        //Populating unit combo box & making it searchable
        final ObservableList<String> rawMaterialNamesList;
        try
        {
            rawMaterialNamesList = daoimpl.fetchInventoryItemsNames();

            rawMaterialNameComboBox.setItems(rawMaterialNamesList);

            makeComboBoxSearchable(rawMaterialNameComboBox,rawMaterialNamesList);

        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        TextField rawMaterialQuantity = new TextField();
        rawMaterialQuantity.setId("rawMaterialQuantity"+rawMaterialCount);
        rawMaterialQuantity.setPrefSize(215,38);
        rawMaterialQuantity.setMinHeight(38);
        rawMaterialQuantity.setPromptText("Quantity *");
        addNumericInputFieldValidation(rawMaterialQuantity);

        ComboBox rawMaterialUnitComboBox = new ComboBox();
        rawMaterialUnitComboBox.setId("rawMaterialUnit"+rawMaterialCount);
        rawMaterialUnitComboBox.setPrefSize(215,38);
        rawMaterialUnitComboBox.setMinHeight(38);
        rawMaterialUnitComboBox.setPromptText("Unit *");
        rawMaterialUnitComboBox.setEditable(true);
        addAlphabeticInputFieldValidation(rawMaterialUnitComboBox);
        //Populating unit combo box & making it searchable
        final ObservableList<String> rawMaterialUnitsList;
        try
        {
            rawMaterialUnitsList = daoimpl.fetchUnits();

            rawMaterialUnitComboBox.setItems(rawMaterialUnitsList);
            makeComboBoxSearchable(rawMaterialUnitComboBox,rawMaterialUnitsList);

        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        Button removeRawMaterialButton = new Button();
        removeRawMaterialButton.setId("removeRawMaterial"+rawMaterialCount);
        removeRawMaterialButton.setText("X");
        removeRawMaterialButton.getStyleClass().add("delete-button");
        removeRawMaterialButton.setCursor(Cursor.HAND);
        removeRawMaterialButton.setOnAction(e -> {

            if(rawMaterialCount < 2)
            {
                currentStage = (Stage) rawMaterialVbox.getScene().getWindow(); // For displaying alerts on top of current window.

                Alert alert = new Alert(Alert.AlertType.WARNING, "Do you want to delete this Recipe?", ButtonType.YES,ButtonType.NO);
                alert.setHeaderText("Recipe will be deleted");
                alert.setTitle("Alert!");
                alert.initOwner(currentStage);
                alert.showAndWait();

                if(alert.getResult() == ButtonType.YES)
                {
                    try
                    {
                        //DELETE FROM DB
                        if( daoimpl.deleteRecipe(Integer.parseInt(recipeIdLabel.getText())) && daoimpl.changeTrackingStatus( Integer.parseInt(menuItemIdLabel.getText()),"OFF") )
                        {
                            //DELETE FROM UI
                            rawMaterialCount--;

                            Node parent = removeRawMaterialButton.getParent();

                            if (parent instanceof HBox) {
                                HBox hBoxToRemove = (HBox) parent;
                                rawMaterialVbox.getChildren().remove(hBoxToRemove);
                            }

                            // UPDATE THE STYLE CLASS OF THE INVENTORYTRACKINGBUTTON IN INVENTORY TRACKING COLUMN OF MENU ITEMS CELL WHOSE RECIPE ITEM IS DELETED


                        }
                        else
                        {
                            currentStage = (Stage) rawMaterialVbox.getScene().getWindow(); // For displaying alerts on top of current window.

                            Alert delete_alert = new Alert(Alert.AlertType.ERROR, "Recipe could not be deleted.",ButtonType.OK);
                            delete_alert.setHeaderText("Could not delete Recipe");
                            delete_alert.setTitle("Alert!");
                            alert.initOwner(currentStage);
                            delete_alert.showAndWait();
                        }
                    }
                    catch (SQLException ex)
                    {
                        currentStage = (Stage) rawMaterialVbox.getScene().getWindow(); // For displaying alerts on top of current window.

                        Alert delete_alert = new Alert(Alert.AlertType.ERROR, "Recipe could not be deleted.Exception : "+ ex.getMessage(),ButtonType.OK);
                        delete_alert.setHeaderText("Could not delete Recipe.");
                        delete_alert.setTitle("Alert!");
                        alert.initOwner(currentStage);
                        delete_alert.showAndWait();
                        throw new RuntimeException(ex);
                    }

                    recipeIdLabel.setText("_ _");
                }
            }
            else
            {

                //DELETE FROM UI   (WIll be updated in DB on click of save Recipe Button)
                rawMaterialCount--;

                Node parent = removeRawMaterialButton.getParent();

                if (parent instanceof HBox) {
                    HBox hBoxToRemove = (HBox) parent;
                    rawMaterialVbox.getChildren().remove(hBoxToRemove);
                }
            }
        });

        HBox hBox = new HBox();
        hBox.setId("rawMaterialHbox"+rawMaterialCount);
        hBox.setMinHeight(40);
        hBox.getChildren().addAll(rawMaterialNameComboBox,rawMaterialQuantity,rawMaterialUnitComboBox,removeRawMaterialButton);
        hBox.setSpacing(10);



        rawMaterialVbox.getChildren().add(rawMaterialVbox.getChildren().size() - 1, hBox); // Add above the button

        rawMaterialCount++;


    }

    public void populateFieldsAndRecipeData(MouseEvent event) throws SQLException
    {

        Menu selectedItem = menuItemsTable.getSelectionModel().getSelectedItem();

        if(selectedItem == null)
            return;

        //Clear previous raw material data
        clearPreviousItemsVariantData();

        // Populating Variant Fields after Variant Selection
        if (selectedItem.getVariantData() != null)
        {
            Dialog<String> variantDialog = new Dialog<>();
            variantDialog.setTitle("Variant Selection");
            variantDialog.setHeaderText("Select Variant");
            currentStage = (Stage) rawMaterialVbox.getScene().getWindow(); // For displaying alerts on top of current window.
            variantDialog.initOwner(currentStage);

            // Create a flow pane to hold the variant buttons
            FlowPane variantPane = new FlowPane();
            variantPane.setHgap(10);
            variantPane.setVgap(5);

            // Add a button for each variant
            for (Variant variant : selectedItem.getVariantData()) {
                String variantName = variant.getVariantName();
                Double variantPrice = variant.getVariantPrice();
                Button variantButton = new Button(variantName + " - " + variantPrice);
                variantButton.setOnAction(e -> {
                    // Set the selected variant and close the dialog
                    variantDialog.setResult(variantName + " - " + variantPrice);
                });
                variantPane.getChildren().add(variantButton);
            }

            variantDialog.getDialogPane().setContent(variantPane);

            // Show the dialog and wait for a response
            Optional<String> result = variantDialog.showAndWait();
            result.ifPresent(selectedVariant -> {
                String[] variantNameAndPrice = selectedVariant.split(" - ");
                String variantName = variantNameAndPrice[0];
                Double variantPrice = Double.parseDouble(variantNameAndPrice[1]);

                //Setting variant Label
                variantLabel.setText(variantName);
            });
        }
        else
        {
            variantLabel.setText("N/A");
        }

        //Populating ID Field
        menuItemIdLabel.setText(Integer.toString(selectedItem.getId()));

        //Populating Name Field
        menuItemNameLabel.setText(selectedItem.getFoodItemName());

        //Populating Category Field
        categoryLabel.setText(selectedItem.getFoodItemCategory());

        //Populating Recipe items
        Recipe recipe = new Recipe();
        if(variantLabel.getText().equals("N/A"))
        {
            recipe = daoimpl.fetchRecipe(selectedItem.getId(),"N/A");
        }
        else
        {
            recipe = daoimpl.fetchRecipe(selectedItem.getId(),variantLabel.getText());
        }

        clearPreviousItemsVariantData(); //Clearing Previously Displayed data

        if(recipe != null)
        {

            rawMaterialVbox.setDisable(false);

            rawMaterialCount = 0;

            recipeIdLabel.setText(String.valueOf(recipe.getId()));

            for(Inventory rawMaterial : recipe.getRawMaterials())
            {
                ComboBox rawMaterialNameComboBox = new ComboBox();
                rawMaterialNameComboBox.setValue(rawMaterial.getInventoryItemName());
                rawMaterialNameComboBox.setId("rawMaterial"+rawMaterialCount);
                rawMaterialNameComboBox.setPrefSize(215,38);
                rawMaterialNameComboBox.setMinHeight(38);
                rawMaterialNameComboBox.setPromptText("Raw Material *");
                rawMaterialNameComboBox.setEditable(true);
                addAlphabeticInputFieldValidation(rawMaterialNameComboBox);
                //Populating unit combo box & making it searchable
                final ObservableList<String> rawMaterialNamesList;
                try
                {
                    rawMaterialNamesList = daoimpl.fetchInventoryItemsNames();

                    rawMaterialNameComboBox.setItems(rawMaterialNamesList);
                    makeComboBoxSearchable(rawMaterialNameComboBox,rawMaterialNamesList);

                }
                catch (SQLException e)
                {
                    throw new RuntimeException(e);
                }

                TextField rawMaterialQuantity = new TextField();
                rawMaterialQuantity.setText( String.valueOf(rawMaterial.getInventoryItemQuantity()) );
                rawMaterialQuantity.setId("rawMaterialQuantity"+rawMaterialCount);
                rawMaterialQuantity.setPrefSize(215,38);
                rawMaterialQuantity.setMinHeight(38);
                rawMaterialQuantity.setPromptText("Quantity *");
                addNumericInputFieldValidation(rawMaterialQuantity);

                ComboBox rawMaterialUnitComboBox = new ComboBox();
                rawMaterialUnitComboBox.setValue(rawMaterial.getInventoryItemUnit());
                rawMaterialUnitComboBox.setId("rawMaterialUnit"+rawMaterialCount);
                rawMaterialUnitComboBox.setPrefSize(215,38);
                rawMaterialUnitComboBox.setMinHeight(38);
                rawMaterialUnitComboBox.setPromptText("Unit *");
                rawMaterialUnitComboBox.setEditable(true);
                addAlphabeticInputFieldValidation(rawMaterialUnitComboBox);
                //Populating unit combo box & making it searchable
                final ObservableList<String> rawMaterialUnitsList;
                try
                {
                    rawMaterialUnitsList = daoimpl.fetchUnits();

                    rawMaterialUnitComboBox.setItems(rawMaterialUnitsList);
                    makeComboBoxSearchable(rawMaterialUnitComboBox,rawMaterialUnitsList);

                }
                catch (SQLException e)
                {
                    throw new RuntimeException(e);
                }


                Button removeVariantButton = new Button();
                removeVariantButton.setId("removeRawMaterial"+rawMaterialCount);
                removeVariantButton.setText("X");
                removeVariantButton.getStyleClass().add("delete-button");
                removeVariantButton.setCursor(Cursor.HAND);
                removeVariantButton.setOnAction(e -> {

                    if(rawMaterialCount < 2)
                    {
                        currentStage = (Stage) rawMaterialVbox.getScene().getWindow(); // For displaying alerts on top of current window.

                        Alert alert = new Alert(Alert.AlertType.WARNING, "Do you want to delete this Recipe?", ButtonType.YES,ButtonType.NO);
                        alert.setHeaderText("Recipe will be deleted");
                        alert.setTitle("Alert!");
                        alert.initOwner(currentStage);
                        alert.showAndWait();

                        if(alert.getResult() == ButtonType.YES)
                        {
                            try
                            {
                                //DELETE FROM DB
                                if( daoimpl.deleteRecipe(Integer.parseInt(recipeIdLabel.getText())) && daoimpl.changeTrackingStatus( Integer.parseInt(menuItemIdLabel.getText()),"OFF") )
                                {

                                    //DELETE FROM UI
                                    rawMaterialCount--;

                                    Node parent = removeVariantButton.getParent();

                                    if (parent instanceof HBox) {
                                        HBox hBoxToRemove = (HBox) parent;
                                        rawMaterialVbox.getChildren().remove(hBoxToRemove);
                                    }

                                }
                                else
                                {
                                    currentStage = (Stage) rawMaterialVbox.getScene().getWindow(); // For displaying alerts on top of current window.

                                    Alert delete_alert = new Alert(Alert.AlertType.ERROR, "Recipe could not be deleted.",ButtonType.OK);
                                    delete_alert.setHeaderText("Could not delete Recipe");
                                    delete_alert.setTitle("Alert!");
                                    alert.initOwner(currentStage);
                                    delete_alert.showAndWait();
                                }
                            }
                            catch (SQLException ex)
                            {
                                currentStage = (Stage) rawMaterialVbox.getScene().getWindow(); // For displaying alerts on top of current window.

                                Alert delete_alert = new Alert(Alert.AlertType.ERROR, "Recipe could not be deleted.Exception : "+ ex.getMessage(),ButtonType.OK);
                                delete_alert.setHeaderText("Could not delete Recipe.");
                                delete_alert.setTitle("Alert!");
                                alert.initOwner(currentStage);
                                delete_alert.showAndWait();
                                throw new RuntimeException(ex);
                            }

                            recipeIdLabel.setText("_ _");
                        }
                    }
                    else
                    {

                        //DELETE FROM UI   (WIll be updated in DB on click of save Recipe Button)
                        rawMaterialCount--;

                        Node parent = removeVariantButton.getParent();

                        if (parent instanceof HBox) {
                            HBox hBoxToRemove = (HBox) parent;
                            rawMaterialVbox.getChildren().remove(hBoxToRemove);
                        }
                    }

                });


                HBox hBox = new HBox();
                hBox.setId("rawMaterialHbox"+rawMaterialCount);
                hBox.setMinHeight(40);
                hBox.getChildren().addAll(rawMaterialNameComboBox,rawMaterialQuantity,rawMaterialUnitComboBox,removeVariantButton);
                hBox.setSpacing(10);

                rawMaterialVbox.getChildren().add(rawMaterialVbox.getChildren().size() - 1, hBox); // Add above the button

                rawMaterialCount++;

            }

        }

    }

    public void saveRecipe(MouseEvent event)
    {


        //Checking if all field values are filled by the user
        if (menuItemIdLabel.getText().equals("_ _") || menuItemIdLabel.getText().equals("_ _") || variantLabel.getText().isEmpty() || categoryLabel.getText().equals("_ _") )
        {
            currentStage = (Stage) rawMaterialVbox.getScene().getWindow(); // For displaying alerts on top of current window.
            Alert alert = new Alert(Alert.AlertType.WARNING, "Select a Menu item to add Recipe", ButtonType.OK);
            alert.setHeaderText("No Item Selected");
            alert.setTitle("Alert!");
            alert.initOwner(currentStage);
            alert.showAndWait();

            return;
        }


        // Checking and Adding Variants
        ArrayList<Inventory> rawMaterialData = new ArrayList<Inventory>();
        Set<String> selectedRawMaterials = new HashSet<>();
        int temp = rawMaterialCount;

        while(temp>=0)
        {
            HBox hBox = (HBox) rawMaterialVbox.lookup("#rawMaterialHbox" + temp);
            if(hBox != null)
            {
                ComboBox rawMaterialComboBox = (ComboBox) hBox.lookup("#rawMaterial" + temp);
                TextField rawMaterialQuantityTextField = (TextField) hBox.lookup("#rawMaterialQuantity" + temp);
                ComboBox rawMaterialUnitComboBox = (ComboBox) hBox.lookup("#rawMaterialUnit" + temp);

                if( rawMaterialComboBox.getValue() == null || rawMaterialComboBox.getSelectionModel().getSelectedItem() == "" || rawMaterialQuantityTextField.getText().isEmpty() || rawMaterialUnitComboBox.getSelectionModel().getSelectedItem() == "" || rawMaterialUnitComboBox.getValue() == null)
                {
                    currentStage = (Stage) rawMaterialVbox.getScene().getWindow(); // For displaying alerts on top of current window.

                    Alert alert = new Alert(Alert.AlertType.WARNING, "Raw material Name , Quantity or Unit fields cannot be empty", ButtonType.OK);
                    alert.setHeaderText("Can not Add item!");
                    alert.setTitle("Alert!");
                    alert.initOwner(currentStage);
                    alert.showAndWait();

                    return;
                }
                else
                {

                   Inventory rawMaterial = new Inventory();
                   String rawMaterialName = rawMaterialComboBox.getSelectionModel().getSelectedItem().toString();
                   rawMaterial.setId( Integer.parseInt(rawMaterialName.split("\\[")[1].split("]")[0]));
                   rawMaterial.setInventoryItemName(rawMaterialName);
                   rawMaterial.setInventoryItemQuantity(Double.parseDouble( rawMaterialQuantityTextField.getText() ));
                   rawMaterial.setInventoryItemUnit(rawMaterialUnitComboBox.getSelectionModel().getSelectedItem().toString());

                    if (!selectedRawMaterials.add(rawMaterialName)) {
                        // Duplicate raw material selected
                        currentStage = (Stage) rawMaterialVbox.getScene().getWindow(); // For displaying alerts on top of current window.

                        Alert alert = new Alert(Alert.AlertType.ERROR, "Each Raw material can be only used once in a single recipe.", ButtonType.OK);
                        alert.setHeaderText("Duplicate Raw Materials found in Recipe");
                        alert.setTitle("Error!");
                        alert.initOwner(currentStage);
                        alert.showAndWait();
                        return;
                    }

                   rawMaterialData.add(rawMaterial);
                }
            }

            temp--;
        }


        //Creating recipe objects
        Recipe recipe = new Recipe();

        recipe.setMenuItemId(Integer.parseInt( menuItemIdLabel.getText() ));
        recipe.setVariant(variantLabel.getText());
        recipe.setRawMaterials(rawMaterialData);

        if(rawMaterialData.size() <=0)
            return;
        
        try
        {
            if(recipeIdLabel.getText().equals("_ _") || recipeIdLabel.getText().isEmpty())
            {
                long generatedRecipeID = daoimpl.addRecipe(recipe);
                if ( generatedRecipeID > 0 )
                {
                    currentStage = (Stage) rawMaterialVbox.getScene().getWindow(); // For displaying alerts on top of current window.
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Recipe added Successfully", ButtonType.OK);
                    alert.setHeaderText("Recipe Added");
                    alert.setTitle("Success!");
                    alert.initOwner(currentStage);
                    alert.showAndWait();

                    recipeIdLabel.setText(String.valueOf(generatedRecipeID) );
                }
                else
                {
                    currentStage = (Stage) rawMaterialVbox.getScene().getWindow(); // For displaying alerts on top of current window.

                    Alert alert = new Alert(Alert.AlertType.ERROR, "Recipe could not be saved!", ButtonType.OK);
                    alert.setHeaderText("Can not Add Recipe!");
                    alert.setTitle("Alert!");
                    alert.initOwner(currentStage);
                    alert.showAndWait();
                }
            }
            else
            {
                recipe.setId(Integer.parseInt( recipeIdLabel.getText()) );

                if (daoimpl.updateRecipe(recipe))
                {
                    currentStage = (Stage) rawMaterialVbox.getScene().getWindow(); // For displaying alerts on top of current window.
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Recipe Updated Successfully", ButtonType.OK);
                    alert.setHeaderText("Recipe Updated");
                    alert.setTitle("Success!");
                    alert.initOwner(currentStage);
                    alert.showAndWait();
                }
                else
                {
                    currentStage = (Stage) rawMaterialVbox.getScene().getWindow(); // For displaying alerts on top of current window.
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Recipe could not be Updated!", ButtonType.OK);
                    alert.setHeaderText("Can not Update Recipe!");
                    alert.setTitle("Alert!");
                    alert.initOwner(currentStage);
                    alert.showAndWait();
                }
            }
        }
        catch(SQLException e)
        {
            currentStage = (Stage) rawMaterialVbox.getScene().getWindow(); // For displaying alerts on top of current window.
            Alert alert = new Alert(Alert.AlertType.WARNING, "Database operation exception : " + e.getMessage(), ButtonType.OK);
            alert.setHeaderText("Database Exception");
            alert.setTitle("Alert!");
            alert.initOwner(currentStage);
            alert.showAndWait();
        }
        catch (Exception e)
        {
            currentStage = (Stage) rawMaterialVbox.getScene().getWindow(); // For displaying alerts on top of current window.
            Alert alert = new Alert(Alert.AlertType.WARNING, "Exception occured : " + e.getMessage(), ButtonType.OK);
            alert.setHeaderText("Database Exception");
            alert.setTitle("Alert!");
            alert.initOwner(currentStage);
            alert.showAndWait();


        }
    }

    public void clearPreviousItemsVariantData()
    {
        int temp = rawMaterialCount;
        for(int i=0;i<temp;i++)
        {
            HBox variantHbox = (HBox) rawMaterialVbox.lookup("#rawMaterialHbox"+i);
            rawMaterialVbox.getChildren().remove(variantHbox);
        }

        recipeIdLabel.setText("_ _");

        rawMaterialCount=0;
    }

}
