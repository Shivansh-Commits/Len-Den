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
import org.lenden.dao.DaoImpl;
import org.lenden.model.Inventory;
import org.lenden.model.Menu;
import org.lenden.model.Recipe;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class RecepieController implements Initializable {

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
    Button addRawMaterialButton;

    ObservableList<Menu> menuTableItems =  FXCollections.observableArrayList();
    DaoImpl daoimpl = new DaoImpl();
    int rawMaterialCount = 0;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
    /**
     *
     * For Input Validation.
     * Desc - Function adds event listener to the fields and uses regex to match input .
     * only allows Aplhabets , " - " and nums 1-9
     * **/
    private void addAlphabeticInputFieldValidation(ComboBox comboBox) {
        // Event filter to allow only alphabetic characters and the hyphen "-"
        comboBox.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String input = event.getCharacter();

            // Use a regular expression to check if the input is alphabetic or a hyphen
            if (!input.matches("[a-zA-Z0-9 ]")) {
                event.consume(); // Ignore the input if it's not alphabetic or a hyphen
            }
        });
    }

    private void makeComboBoxSearchable(ComboBox<String> comboBox, ObservableList<String> originalItems) {
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


    public void displayAllMenuItems() throws Exception {

        try
        {
            menuTableItems = daoimpl.fetchAllCategoryItems();
        }
        catch(Exception e)
         {
             Alert alert = new Alert(Alert.AlertType.ERROR, "Could not display Menu Items. Database Exception" + e.getMessage(), ButtonType.OK);
             alert.setHeaderText("Exception in displaying itmes");
             alert.setTitle("Exception!");
             alert.showAndWait();
         }


        // Create a cell value factory for the ID column
        TableColumn<org.lenden.model.Menu, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        idCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Category column
        TableColumn<org.lenden.model.Menu, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("foodItemCategory"));
        categoryCol.setPrefWidth(100);
        categoryCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Name column
        TableColumn<org.lenden.model.Menu, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));
        nameCol.setPrefWidth(200);
        nameCol.setStyle("-fx-alignment: CENTER;");


        // Create a cell value factory for the Price column
        TableColumn<org.lenden.model.Menu, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));
        priceCol.setPrefWidth(100);
        priceCol.setStyle("-fx-alignment: CENTER;");
        //Displaying the price value in col, only if variant is not added
        priceCol.setCellValueFactory(cellData -> {
            org.lenden.model.Menu menuItem = cellData.getValue();
            if (menuItem.getVariantData() == null || menuItem.getVariantData().isEmpty()) {
                return new SimpleStringProperty(String.valueOf(menuItem.getFoodItemPrice()));
            } else {
                return new SimpleStringProperty("N/A");
            }
        });


        // Create a cell value factory for the Stock Quantity column
        TableColumn<org.lenden.model.Menu, Integer> stockCol = new TableColumn<>("Stock Quantity");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        stockCol.setPrefWidth(100);
        stockCol.setStyle("-fx-alignment: CENTER;");


        // Create a cell value factory for the Variant column
        TableColumn<org.lenden.model.Menu, String> variantCol = new TableColumn<>("Variants");
        variantCol.setCellValueFactory(new PropertyValueFactory<>("variantData"));
        variantCol.setPrefWidth(200);
        variantCol.setStyle("-fx-alignment: CENTER;");
        //Displaying variant data after formating
        variantCol.setCellValueFactory(cellData -> {
            org.lenden.model.Menu menuItem = cellData.getValue();
            Map<String, Double> variantData = menuItem.getVariantData();
            if (variantData != null && !variantData.isEmpty()) {
                StringBuilder variants = new StringBuilder();
                variantData.keySet().forEach(variant -> variants.append(variant).append(", "));
                // Remove the trailing comma and space
                return new SimpleStringProperty(variants.substring(0, variants.length() - 2));
            } else {
                return new SimpleStringProperty(" N/A");
            }
        });



        //Create col for Buttons
        TableColumn<org.lenden.model.Menu,Void> buttonCol = new TableColumn<>("Raw Material Usage");
        buttonCol.setCellFactory(param -> new TableCell<org.lenden.model.Menu, Void>()
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
                    Button activateTrackingButton = new Button("Activate Tracking");
                    activateTrackingButton.setCursor(Cursor.HAND);
                    activateTrackingButton.setPrefWidth(170);
                    activateTrackingButton.getStyleClass().add("activate-tracking-button");
                    activateTrackingButton.setTooltip(new Tooltip("Activate raw material usage and automatically deduct from Inventory"));
                    Image view_symbol = new Image(Objects.requireNonNull(getClass().getResource("/images/black/toggle_on_FILL0_wght400_GRAD0_opsz48.png")).toExternalForm());
                    ImageView imageView = new ImageView(view_symbol);
                    imageView.setFitWidth(16); // Adjust the width as needed
                    imageView.setFitHeight(16);
                    activateTrackingButton.setGraphic(imageView);
                    activateTrackingButton.setOnAction(event -> {

                    if(activateTrackingButton.getStyleClass().contains("activate-tracking-button"))
                    {
                        activateTrackingButton.getStyleClass().remove("activate-tracking-button");
                        activateTrackingButton.getStyleClass().add("deactivate-tracking-button");

                        activateTrackingButton.setText("Deactivate Tracking");

                        Image view_symbol1 = new Image(Objects.requireNonNull(getClass().getResource("/images/black/toggle_off_black_48pt_3x.png")).toExternalForm());
                        ImageView imageView1 = new ImageView(view_symbol1);
                        imageView1.setFitWidth(16); // Adjust the width as needed
                        imageView1.setFitHeight(16);
                        activateTrackingButton.setGraphic(imageView1);

                    }
                    else
                    {
                        activateTrackingButton.getStyleClass().remove("deactivate-tracking-button");
                        activateTrackingButton.getStyleClass().add("activate-tracking-button");

                        activateTrackingButton.setText("Activate Tracking");

                        Image view_symbol2 = new Image(Objects.requireNonNull(getClass().getResource("/images/black/toggle_on_FILL0_wght400_GRAD0_opsz48.png")).toExternalForm());
                        ImageView imageView2 = new ImageView(view_symbol2);
                        imageView2.setFitWidth(16); // Adjust the width as needed
                        imageView2.setFitHeight(16);
                        activateTrackingButton.setGraphic(imageView2);

                    }

                    });

                    // Set buttons into cell
                    HBox hBox = new HBox();
                    hBox.setSpacing(10);
                    hBox.setPadding(new Insets(5,10,5,10));
                    hBox.getChildren().addAll(activateTrackingButton);
                    setGraphic(hBox);
                }
            }
        });

        // Set the cell value factories for the table columns
        menuItemsTable.getColumns().setAll(idCol, categoryCol, nameCol, priceCol, stockCol, variantCol, buttonCol);
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

        Button removeVariantButton = new Button();
        removeVariantButton.setId("removeRawMaterial"+rawMaterialCount);
        removeVariantButton.setText("X");
        removeVariantButton.getStyleClass().add("delete-button");
        removeVariantButton.setCursor(Cursor.HAND);


        HBox hBox = new HBox();
        hBox.setId("rawMaterialHbox"+rawMaterialCount);
        hBox.setMinHeight(40);
        hBox.getChildren().addAll(rawMaterialNameComboBox,rawMaterialQuantity,rawMaterialUnitComboBox,removeVariantButton);
        hBox.setSpacing(10);

        removeVariantButton.setOnAction(e -> {

            rawMaterialCount--;

            Node parent = removeVariantButton.getParent();

            if (parent instanceof HBox) {
                HBox hBoxToRemove = (HBox) parent;
                rawMaterialVbox.getChildren().remove(hBoxToRemove);
            }
        });

        rawMaterialVbox.getChildren().add(rawMaterialVbox.getChildren().size() - 1, hBox); // Add above the button

        rawMaterialCount++;

    }
    public void populateFieldsAndRecipeData(MouseEvent event)
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

            // Create a flow pane to hold the variant buttons
            FlowPane variantPane = new FlowPane();
            variantPane.setHgap(10);
            variantPane.setVgap(5);

            // Add a button for each variant
            for (Map.Entry<String, Double> entry : selectedItem.getVariantData().entrySet()) {
                String variantName = entry.getKey();
                Double variantPrice = entry.getValue();
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



       /*
        //Displaying Recipe Data
        HashMap<String,Double> variantData = selectedItem.getVariantData();

        //Clearing Previously Displayed data
        clearPreviousItemsVariantData();

        if(variantData != null)
        {

            //Clearing Previously Displayed data
            clearPreviousItemsVariantData();

            rawMaterialVbox.setDisable(false);

            rawMaterialCount=0;

            for(Map.Entry<String,Double> variant : variantData.entrySet())
            {
                TextField variantName = new TextField();
                variantName.setId("variant"+variantCount);
                variantName.setPrefSize(309,38);
                variantName.setMinHeight(38);
                variantName.setPromptText("Variants");
                variantName.setText(variant.getKey());
                addAlphabeticInputFieldValidation(variantName);

                TextField variantPrice = new TextField();
                variantPrice.setId("variantPrice"+variantCount);
                variantPrice.setPrefSize(208,38);
                variantPrice.setMinHeight(38);
                variantPrice.setPromptText("Variant Price");
                variantPrice.setText(String.valueOf(variant.getValue()));
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

        */
    }

    public void saveRecipe(MouseEvent event)
    {
        //Checking if all field values are filled by the user (except price)
        if (menuItemIdLabel.getText().equals("_ _") || menuItemIdLabel.getText().equals("_ _") || variantLabel.getText().isEmpty() || categoryLabel.getText().equals("_ _") )
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Select a Menu item to add Recipe", ButtonType.OK);
            alert.setHeaderText("No Item Selected");
            alert.setTitle("Alert!");
            alert.showAndWait();

            return;
        }


        // Checking and Adding Variants
        ArrayList<Inventory> rawMaterialData = new ArrayList<Inventory>();
        int temp = rawMaterialCount;

        while(temp>=0)
        {
            HBox hBox = (HBox) rawMaterialVbox.lookup("#rawMaterialHbox" + temp);
            if(hBox != null)
            {
                ComboBox rawMaterialComboBox = (ComboBox) hBox.lookup("#rawMaterial" + temp);
                TextField rawMaterialQuantityTextField = (TextField) hBox.lookup("#rawMaterialQuantity" + temp);
                ComboBox rawMaterialUnitComboBox = (ComboBox) hBox.lookup("#rawMaterialUnit" + temp);

                if( rawMaterialComboBox.getSelectionModel().isEmpty() || rawMaterialQuantityTextField.getText().isEmpty() || rawMaterialUnitComboBox.getSelectionModel().isEmpty())
                {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Variants Name or Price fields cannot be empty", ButtonType.OK);
                    alert.setHeaderText("Can not Add item!");
                    alert.setTitle("Alert!");
                    alert.showAndWait();

                    return;
                }
                else
                {
                   Inventory rawMaterial = new Inventory();
                   String rawMaterialName = rawMaterialComboBox.getSelectionModel().getSelectedItem().toString();
                   rawMaterial.setId( Integer.parseInt(rawMaterialName.split("\\[")[1].split("]")[0]));
                   rawMaterial.setInventoryItemName(rawMaterialName.split("\\[")[0].trim());
                   rawMaterial.setInventoryItemQuantity(Double.parseDouble( rawMaterialQuantityTextField.getText() ));
                   rawMaterial.setInventoryItemUnit(rawMaterialUnitComboBox.getSelectionModel().getSelectedItem().toString());

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
    }

    public void clearPreviousItemsVariantData()
    {
        int temp = rawMaterialCount;
        for(int i=0;i<temp;i++)
        {
            HBox variantHbox = (HBox) rawMaterialVbox.lookup("#rawMaterialHbox"+i);
            rawMaterialVbox.getChildren().remove(variantHbox);
        }

        rawMaterialCount=0;
    }

}
