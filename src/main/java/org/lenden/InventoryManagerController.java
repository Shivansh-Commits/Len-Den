package org.lenden;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.lenden.dao.DaoImpl;
import org.lenden.model.Inventory;
import org.lenden.model.InventoryPurchase;

import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class InventoryManagerController implements Initializable {

    @FXML
    TableView<Inventory> inventoryTable;

    @FXML
    TextField searchTextField;

    @FXML
    Label inventoryItemIdLabel;
    @FXML
    TextField inventoryItemNameTextField;
    @FXML
    TextField inventoryItemPurchaseCostTextField;
    @FXML
    TextField inventoryItemStockQuantityTextField;
    @FXML
    ComboBox<String> inventoryItemUnitComboBox;
    @FXML
    Button addToInventoryButton;
    @FXML
    Label inventoryItemNameInfoLabel;
    @FXML
    Label inventoryItemUnitInfoLabel;


    @FXML
    TextField newInventoryItemNameTextField;
    @FXML
    ComboBox<String> newInventoryItemUnitComboBox;
    @FXML
    Button addPurchaseButton;
    @FXML
    Label newInventoryItemNameInfoLabel;

    ObservableList<Inventory> inventoryTableItems =  FXCollections.observableArrayList();

    DaoImpl daoimpl = new DaoImpl();

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        //--------------------------------------------------------------------------------------------------------------

        //Setting Tips for Different Elements - Will be Visible on Hover
        //BUTTONS
        addToInventoryButton.setTooltip(new Tooltip("Add new Raw Material to Inventory"));
        addPurchaseButton.setTooltip(new Tooltip("Add existing Items Purchase"));

        //FIELDS
        inventoryItemUnitComboBox.setTooltip(new Tooltip("Search Existing Inventory item Unit"));
        inventoryItemUnitInfoLabel.setTooltip(new Tooltip("Search Existing Inventory item Unit"));

        newInventoryItemNameTextField.setTooltip(new Tooltip("Enter New Inventory Item name"));
        newInventoryItemNameInfoLabel.setTooltip(new Tooltip("Enter New Inventory Item name"));

        //--------------------------------------------------------------------------------------------------------------


        //Populating tables
        populateTable();


        //--------------------------------------------------------------------------------------------------------------

        //Input Validation
        //EXISTING ITEMS FIELDS
        addAlphabeticInputFieldValidation(inventoryItemNameTextField);
        addAlphabeticInputFieldValidation(inventoryItemUnitComboBox);

        addNumericInputFieldValidation(inventoryItemPurchaseCostTextField);
        addNumericInputFieldValidation(inventoryItemStockQuantityTextField);

        //NEW ITEMS FIELDS
        addAlphabeticInputFieldValidation(newInventoryItemNameTextField);


        //--------------------------------------------------------------------------------------------------------------

        //Populating unit combo box & making it searchable
        final ObservableList<String> inventoryItemUnits;
        try
        {
            inventoryItemUnits = daoimpl.fetchUnits();

            inventoryItemUnitComboBox.setItems(inventoryItemUnits);
            makeComboBoxSearchable(inventoryItemUnitComboBox,inventoryItemUnits);

            //Populating new inventory item unit combo box & making it searchable
            newInventoryItemUnitComboBox.setItems(inventoryItemUnits);
            makeComboBoxSearchable(newInventoryItemUnitComboBox,inventoryItemUnits);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }


        //--------------------------------------------------------------------------------------------------------------

        //Populate fields on table click
        inventoryTable.setOnMouseClicked(event -> {
            if (event.getClickCount() > 0) {
                Inventory selectedItem = inventoryTable.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    // Populate the values into the fields
                    inventoryItemNameTextField.setText(String.valueOf(selectedItem.getInventoryItemName()));
                    //inventoryItemPurchaseCostTextField.setText(String.valueOf(selectedItem.getInventoryItemPrice()));
                    inventoryItemUnitComboBox.getSelectionModel().select(selectedItem.getInventoryItemUnit());
                    //inventoryItemStockQuantityTextField.setText(String.valueOf(selectedItem.getInventoryItemQuantity()));
                    inventoryItemIdLabel.setText(String.valueOf(selectedItem.getId()));
                }
            }
        });


        //-----------------ADDING EVENT LISTENER TO SEARCH FIELD-------------------------

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Filter the data based on the text in the TextField
            // You may need to implement your own filtering logic here
            ObservableList<Inventory> filteredItems = FXCollections.observableArrayList();

            try {
                for (Inventory item : daoimpl.fetchInventoryItems()) {
                    // Add items that match the filter criteria
                    if (item.getInventoryItemName().toLowerCase().contains(newValue.toLowerCase())) {
                        filteredItems.add(item);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // Set the filtered items to be displayed in the TableView
            inventoryTable.setItems(filteredItems);
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



    public void populateTable()
    {
        try
        {
            inventoryTableItems = daoimpl.fetchInventoryItems();

        }
        catch (Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
        }

        // Create a cell value factory for the ID column
        TableColumn<Inventory, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        idCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Name column
        TableColumn<Inventory, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("inventoryItemName"));
        nameCol.setPrefWidth(150);
        nameCol.setStyle("-fx-alignment: CENTER;");

        /*
        nameCol.setCellFactory(column -> {
            return new TableCell<Inventory, String>() {
                private TextField textField;
                private HBox hBox = new HBox();

                {
                    textField = new TextField();
                    textField.setEditable(true);
                    textField.setId("name");

                    hBox.setPadding(new Insets(10,10,10,10));
                    hBox.getChildren().add(textField);
                }
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        textField.setText(item);
                        setGraphic(hBox);
                    }
                }
            };
        });

         */


        // Create a cell value factory for the Price column
        TableColumn<Inventory, Double> rateCol = new TableColumn<>("Avg Rate/Unit");
        rateCol.setCellValueFactory(new PropertyValueFactory<>("inventoryItemRate"));
        rateCol.setPrefWidth(150);
        rateCol.setStyle("-fx-alignment: CENTER;");
        rateCol.setCellFactory(column -> {
            return new TableCell<Inventory, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(String.format("%.2f", (Double) item));
                    }
                }
            };
        });

        /*

            rateCol.setCellFactory(column -> {
            return new TableCell<Inventory, Double>() {
                private TextField textField;
                private HBox hBox = new HBox();
                {
                    textField = new TextField();
                    textField.setEditable(true);
                    textField.setId("price");

                    hBox.setPadding(new Insets(10,10,10,10));
                    hBox.getChildren().add(textField);
                }
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        textField.setText(String.valueOf(item));
                        setGraphic(hBox);
                    }
                }
            };
        });

         */


        // Create a cell value factory for the Stock Quantity column
        TableColumn<Inventory, String> unitCol = new TableColumn<>("Unit");
        unitCol.setCellValueFactory(new PropertyValueFactory<>("inventoryItemUnit"));
        unitCol.setPrefWidth(120);
        unitCol.setStyle("-fx-alignment: CENTER;");

        /*
        unitCol.setCellFactory(column -> {
            return new TableCell<Inventory, String>() {
                private ComboBox comboBox;
                private HBox hBox = new HBox();

                {
                    comboBox = new ComboBox<String>();
                    comboBox.setEditable(true);
                    comboBox.setId("unit");
                    try
                    {
                        ObservableList<String> units = daoimpl.fetchUnits();

                        comboBox.setItems(units);
                        makeComboBoxSearchable(comboBox,units);
                    }
                    catch (SQLException e) {
                        throw new RuntimeException(e);
                    }



                    hBox.setPadding(new Insets(10,10,10,10));
                    hBox.getChildren().add(comboBox);
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null)
                    {
                        setText(null);
                        setGraphic(null);
                    }
                    else
                    {
                        comboBox.setValue(item);
                        setGraphic(hBox);
                    }
                }
            };
        });
        */

        // Create a cell value factory for the Variant column
        TableColumn<Inventory, Double> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("inventoryItemQuantity"));
        quantityCol.setPrefWidth(150);
        quantityCol.setStyle("-fx-alignment: CENTER;");

        /*
        quantityCol.setCellFactory(column -> {
            return new TableCell<Inventory, Double>() {
                private TextField textField;
                private HBox hBox = new HBox();
                {
                    textField = new TextField();
                    textField.setEditable(true);
                    textField.setId("quantity");

                    hBox.setPadding(new Insets(10,10,10,10));
                    hBox.getChildren().add(textField);
                }
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    }
                    else
                    {
                        textField.setText(String.valueOf(item));
                        setGraphic(hBox);
                    }
                }
            };
        });
        */

        //Create col for Buttons col
        TableColumn<Inventory,Void> buttonCol = new TableColumn<>("");
        buttonCol.setCellFactory(param -> new TableCell<Inventory, Void>()
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
                    /*
                    Button updateButton = new Button("Update");
                    updateButton.setCursor(Cursor.HAND);
                    updateButton.setPrefWidth(100);
                    Image update_symbol = new Image(Objects.requireNonNull(getClass().getResource("/images/black/publish_FILL0_wght400_GRAD0_opsz48.png")).toExternalForm());
                    ImageView imageView = new ImageView(update_symbol);
                    imageView.setFitWidth(16); // Adjust the width as needed
                    imageView.setFitHeight(16);
                    updateButton.setGraphic(imageView);
                    */

                    Button viewPurchases = new Button("View Purchases");
                    viewPurchases.setCursor(Cursor.HAND);
                    viewPurchases.setPrefWidth(135);
                    viewPurchases.getStyleClass().add("view-purchases-button");
                    viewPurchases.setTooltip(new Tooltip("View all Purchases of this item"));
                    Image view_symbol = new Image(Objects.requireNonNull(getClass().getResource("/images/black/outline_visibility_black_24dp.png")).toExternalForm());
                    ImageView imageView = new ImageView(view_symbol);
                    imageView.setFitWidth(16); // Adjust the width as needed
                    imageView.setFitHeight(16);
                    viewPurchases.setGraphic(imageView);
                    viewPurchases.setOnAction(event -> {

                        try {

                            Inventory selectedItem = getTableView().getItems().get(getIndex());
                            viewPurchasesTablePopup(selectedItem.getId());

                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });


                    Button deleteButton = new Button("Delete");
                    deleteButton.setCursor(Cursor.HAND);
                    deleteButton.setPrefWidth(100);
                    deleteButton.getStyleClass().add("delete-button");
                    deleteButton.setTooltip(new Tooltip("Delete the Inventory Item"));
                    Image delete_symbol = new Image(Objects.requireNonNull(getClass().getResource("/images/black/delete_FILL0_wght400_GRAD0_opsz48.png")).toExternalForm());
                    ImageView imageView1 = new ImageView(delete_symbol);
                    imageView1.setFitWidth(16); // Adjust the width as needed
                    imageView1.setFitHeight(16);
                    deleteButton.setGraphic(imageView1);
                    deleteButton.setOnAction(event -> {

                        Inventory selectedItem = getTableView().getItems().get(getIndex());

                        //Database Operation
                        deleteInventoryItem(selectedItem);

                        //Remove deletes item from list
                        inventoryTableItems.remove(selectedItem);

                        //Refresh table
                        inventoryTable.refresh();
                    });



                    /*
                        updateButton.getStyleClass().add("update-button");
                        updateButton.setTooltip(new javafx.scene.control.Tooltip("Update the Inventory Item Details"));
                        updateButton.setOnAction(event ->
                        {
                            Inventory selectedItem = getTableView().getItems().get(getIndex());
                            TableRow<Inventory> row = getTableRow();
                            if (row != null) {
                                TextField nameField = (TextField) row.lookup("#name");
                                TextField priceField = (TextField) row.lookup("#price");
                                ComboBox unitComboBox = (ComboBox) row.lookup("#unit");
                                TextField quantityField = (TextField) row.lookup("#quantity");

                                if (nameField != null && priceField != null && unitComboBox != null && quantityField != null) {
                                    String newName = nameField.getText();
                                    Double newPrice = Double.valueOf(priceField.getText());
                                    String newUnit = unitComboBox.getSelectionModel().getSelectedItem().toString();
                                    Double newQuantity = Double.valueOf(quantityField.getText());

                                    selectedItem.setInventoryItemName(newName);
                                    selectedItem.setInventoryItemPrice(newPrice);
                                    selectedItem.setInventoryItemUnit(newUnit);
                                    selectedItem.setInventoryItemQuantity(newQuantity);

                                    updateInventoryItem(selectedItem);
                                }
                            }
                        });
                         */


                    // Set buttons into cell
                    HBox hBox = new HBox();
                    hBox.setSpacing(10);
                    hBox.setPadding(new Insets(5,10,5,10));
                    hBox.getChildren().addAll(viewPurchases, deleteButton);
                    setGraphic(hBox);
                }
            }
        });

        // Set the cell value factories for the table columns
        inventoryTable.getColumns().setAll(idCol,nameCol, rateCol, unitCol, quantityCol,buttonCol);
        inventoryTable.setItems(inventoryTableItems);

    }

    public void deleteInventoryItem(Inventory inventoryItem)
    {
        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION, "ARE YOU SURE ?", ButtonType.YES , ButtonType.NO);
        deleteAlert.setHeaderText("Item will be deleted");
        deleteAlert.setTitle("Alert!");
        deleteAlert.showAndWait();

        if(deleteAlert.getResult() == ButtonType.YES)
        {

            if(inventoryItem == null)
                return;

            try {
                if (!daoimpl.deleteInventoryItem(inventoryItem.getId()))
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "COULD NOT DELETE ITEM. If this error keeps occuring contact customer support.", ButtonType.OK);
                    alert.setHeaderText("Item not deleted!");
                    alert.setTitle("Alert!");
                    alert.showAndWait();
                }
                else
                {
                    //Clearing Fields
                    inventoryItemNameTextField.clear();
                    inventoryItemPurchaseCostTextField.clear();
                    inventoryItemUnitComboBox.getSelectionModel().clearSelection();
                    inventoryItemStockQuantityTextField.clear();

                }
            }
            catch (Exception ex)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Database delete operation Exception - "+ex.getMessage(), ButtonType.OK);
                alert.setHeaderText("Failed");
                alert.setTitle("Error!");
                alert.showAndWait();
            }
        }

    }

    public void addNewInventoryItem()
    {
        //Checking if all field values are filled by the user
        if (newInventoryItemNameTextField.getText().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Fields Cannot be Empty, populate all fields", ButtonType.OK);
            alert.setHeaderText("No Item Selected");
            alert.setTitle("Alert!");
            alert.showAndWait();

            return;
        }

        // Assuming you have fields for the new item's details such as name, price, unit, and quantity
        String newName = newInventoryItemNameTextField.getText();
        Double newPrice = 0.0;
        String newUnit =  newInventoryItemUnitComboBox.getSelectionModel().getSelectedItem();
        Double newQuantity = 0.0;

        // Create a new Inventory object with the provided details
        Inventory newItem = new Inventory(newName, newPrice, newUnit, newQuantity);

        try {
            // Attempt to add the new item to the database
            if (daoimpl.addNewInventoryItem(newItem))
            {
                // If successful, clear the input fields and refresh the table
                newInventoryItemNameTextField.clear();
                newInventoryItemUnitComboBox.getSelectionModel().clearSelection();

                // Refresh the table with updated items
                populateTable();

                //Refresh Unit Combo Box
                makeComboBoxSearchable(newInventoryItemUnitComboBox,daoimpl.fetchUnits());

                //Show success alert
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Inventory Item added successfully", ButtonType.OK);
                alert.setHeaderText("Item Added");
                alert.setTitle("Success!");
                alert.showAndWait();
            }
            else
            {
                // If adding the item failed, show an error message
                Alert alert = new Alert(Alert.AlertType.ERROR, "Could not add item. If this error keeps occurring, please contact customer support.", ButtonType.OK);
                alert.setHeaderText("Item not added!");
                alert.setTitle("Alert!");
                alert.showAndWait();
            }
        } catch (Exception ex) {
            // If an exception occurred during the operation, show an error message
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - " + ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
        }
    }

    public void addInventoryItemPurchase()
    {
        //Checking if all field values are filled by the user
        if (inventoryItemNameTextField.getText().isEmpty() || inventoryItemUnitComboBox.getSelectionModel().getSelectedItem() == null || inventoryItemPurchaseCostTextField.getText().isEmpty() || inventoryItemStockQuantityTextField.getText().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Fields Cannot be Empty, populate all fields", ButtonType.OK);
            alert.setHeaderText("No Item Selected");
            alert.setTitle("Alert!");
            alert.showAndWait();

            return;
        }

        int inventoryItemid = Integer.parseInt(inventoryItemIdLabel.getText());
        String name = inventoryItemNameTextField.getText();
        Double price = Double.valueOf(inventoryItemPurchaseCostTextField.getText());
        String unit =  inventoryItemUnitComboBox.getSelectionModel().getSelectedItem();
        Double quantity = Double.valueOf(inventoryItemStockQuantityTextField.getText());

        // Create a new Inventory object with the provided details
        InventoryPurchase purchaseItem = new InventoryPurchase(inventoryItemid, name ,price ,quantity ,unit);

        try {
            // Attempt to add the new item to the database
            if (daoimpl.addInventoryItemPurchase(purchaseItem))
            {
                // If successful, clear the input fields and refresh the table
                inventoryItemNameTextField.clear();
                inventoryItemPurchaseCostTextField.clear();
                inventoryItemUnitComboBox.getSelectionModel().clearSelection();
                inventoryItemStockQuantityTextField.clear();
                inventoryItemIdLabel.setText("_ _");

                // Refresh the table with updated items
                populateTable();

                //Show success alert
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Purchase Item added successfully", ButtonType.OK);
                alert.setHeaderText("Item Added");
                alert.setTitle("Success!");
                alert.showAndWait();
            }
            else
            {
                // If adding the item failed, show an error message
                Alert alert = new Alert(Alert.AlertType.ERROR, "Could not add item. If this error keeps occurring, please contact customer support.", ButtonType.OK);
                alert.setHeaderText("Item not added!");
                alert.setTitle("Alert!");
                alert.showAndWait();
            }
        } catch (Exception ex) {
            // If an exception occurred during the operation, show an error message
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - " + ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
        }
    }

    public void updateInventoryPurchaseItem(InventoryPurchase selectedItem)
    {
        Alert updateAlert = new Alert(Alert.AlertType.CONFIRMATION, "ARE YOU SURE ?", ButtonType.YES , ButtonType.NO);
        updateAlert.setHeaderText("Item will be Updated");
        updateAlert.setTitle("Alert!");
        updateAlert.showAndWait();

        if(updateAlert.getResult() == ButtonType.YES)
        {
            if(selectedItem == null)
                return;
            else
            {
                try {
                    if (!daoimpl.updateInventoryPurchaseItem(selectedItem))
                    {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "COULD NOT UPDATE ITEM. If this error keeps occurring contact customer support.", ButtonType.OK);
                        alert.setHeaderText("Item not Update!");
                        alert.setTitle("Alert!");
                        alert.showAndWait();
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
    }

    public void deleteInventoryPurchaseItem(InventoryPurchase inventoryItem)
    {
        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION, "ARE YOU SURE ?", ButtonType.YES , ButtonType.NO);
        deleteAlert.setHeaderText("Item will be deleted");
        deleteAlert.setTitle("Alert!");
        deleteAlert.showAndWait();

        if(deleteAlert.getResult() == ButtonType.YES)
        {

            if(inventoryItem == null)
                return;

            try {
                if (!daoimpl.deleteInventoryPurchaseItem(inventoryItem.getPurchaseId()))
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Could not delete Purchase. If this error keeps occurring contact customer support.", ButtonType.OK);
                    alert.setHeaderText("Item not deleted!");
                    alert.setTitle("Alert!");
                    alert.showAndWait();
                }
            }
            catch (Exception ex)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Database delete operation Exception - "+ex.getMessage(), ButtonType.OK);
                alert.setHeaderText("Failed");
                alert.setTitle("Error!");
                alert.showAndWait();
            }
        }

    }

    private void viewPurchasesTablePopup(int inventoryItemId) throws SQLException {
        // Create a new stage for the popup window
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Purchases");


        // Create a table
        TableView<InventoryPurchase> purchaseTable = new TableView<>();
        final ObservableList<InventoryPurchase> purchaseItems = daoimpl.fetchInventoryPurchaseItems(inventoryItemId);

        TableColumn<InventoryPurchase, Object> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("purchaseId"));
        idCol.setPrefWidth(50);
        idCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<InventoryPurchase, Object> dateCol = new TableColumn<>("Purchase Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));
        dateCol.setStyle("-fx-alignment: CENTER;");
        dateCol.setPrefWidth(100);

        TableColumn<InventoryPurchase, Object> nameCol = new TableColumn<>("Raw Material");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("inventoryItemName"));
        nameCol.setStyle("-fx-alignment: CENTER;");
        nameCol.setPrefWidth(150);

        TableColumn<InventoryPurchase, Object> purchaseCostCol = new TableColumn<>("Purchase Cost");
        purchaseCostCol.setCellValueFactory(new PropertyValueFactory<>("inventoryItemPrice"));
        purchaseCostCol.setStyle("-fx-alignment: CENTER;");
        purchaseCostCol.setPrefWidth(120);
        purchaseCostCol.setCellFactory(column -> {
            return new TableCell<InventoryPurchase, Object>()
            {
                private TextField textField;
                private HBox hBox = new HBox();
                {
                    textField = new TextField();
                    textField.setEditable(true);
                    textField.setId("price");

                    hBox.setPadding(new Insets(10,10,10,10));
                    hBox.getChildren().add(textField);
                }
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        textField.setText(String.valueOf(String.format("%.2f", (Double) item)));
                        setGraphic(hBox);
                    }
                }
            };
        });


        TableColumn<InventoryPurchase, Object> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("inventoryItemQuantity"));
        quantityCol.setStyle("-fx-alignment: CENTER;");
        quantityCol.setPrefWidth(120);
        quantityCol.setCellFactory(column -> {
            return new TableCell<InventoryPurchase, Object>()
            {
                private TextField textField;
                private HBox hBox = new HBox();
                {
                    textField = new TextField();
                    textField.setEditable(true);
                    textField.setId("quantity");

                    hBox.setPadding(new Insets(10,10,10,10));
                    hBox.getChildren().add(textField);
                }
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        textField.setText(String.valueOf(item));
                        setGraphic(hBox);
                    }
                }
            };
        });

        TableColumn<InventoryPurchase, Object> unitCol = new TableColumn<>("Unit");
        unitCol.setCellValueFactory(new PropertyValueFactory<>("inventoryItemUnit"));
        unitCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<InventoryPurchase, Object> buttonCol = new TableColumn<>("");
        buttonCol.setStyle("-fx-alignment: CENTER;");
        buttonCol.setCellFactory(param -> new TableCell<InventoryPurchase, Object>()
        {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                {
                    setGraphic(null);
                }
                else
                {

                    Button updateButton = new Button("Update");
                    updateButton.setCursor(Cursor.HAND);
                    updateButton.setPrefWidth(100);
                    Image update_symbol = new Image(Objects.requireNonNull(getClass().getResource("/images/black/publish_FILL0_wght400_GRAD0_opsz48.png")).toExternalForm());
                    ImageView imageView = new ImageView(update_symbol);
                    imageView.setFitWidth(16); // Adjust the width as needed
                    imageView.setFitHeight(16);
                    updateButton.setGraphic(imageView);
                    updateButton.getStyleClass().add("update-button");
                    updateButton.setTooltip(new javafx.scene.control.Tooltip("Update the Inventory Item Details"));
                    updateButton.setOnAction(event ->
                    {
                            InventoryPurchase selectedItem = getTableView().getItems().get(getIndex());
                            TableRow<InventoryPurchase> row = getTableRow();
                            if (row != null) {
                                TextField priceField = (TextField) row.lookup("#price");
                                TextField quantityField = (TextField) row.lookup("#quantity");

                                if ( priceField != null && quantityField != null) {
                                    Double newPrice = Double.valueOf(priceField.getText());
                                    Double newQuantity = Double.valueOf(quantityField.getText());

                                    selectedItem.setInventoryItemName(selectedItem.getInventoryItemName());
                                    selectedItem.setInventoryItemPrice(newPrice);
                                    selectedItem.setInventoryItemUnit(selectedItem.getInventoryItemUnit());
                                    selectedItem.setInventoryItemQuantity(newQuantity);

                                    updateInventoryPurchaseItem(selectedItem);
                                }
                            }
                        });

                    Button deleteButton = new Button("Delete");
                    deleteButton.setCursor(Cursor.HAND);
                    deleteButton.setPrefWidth(100);
                    deleteButton.getStyleClass().add("delete-button");
                    deleteButton.setTooltip(new Tooltip("Delete the Inventory Item"));
                    Image delete_symbol = new Image(Objects.requireNonNull(getClass().getResource("/images/black/delete_FILL0_wght400_GRAD0_opsz48.png")).toExternalForm());
                    ImageView imageView1 = new ImageView(delete_symbol);
                    imageView1.setFitWidth(16); // Adjust the width as needed
                    imageView1.setFitHeight(16);
                    deleteButton.setGraphic(imageView1);
                    deleteButton.setOnAction(event -> {

                        InventoryPurchase selectedItem = getTableView().getItems().get(getIndex());

                        deleteInventoryPurchaseItem(selectedItem);

                        purchaseItems.remove(selectedItem);

                        purchaseTable.refresh();

                    });



                    // Set buttons into cell
                    HBox hBox = new HBox();
                    hBox.setSpacing(10);
                    hBox.setPadding(new Insets(5,10,5,10));
                    hBox.getChildren().addAll(updateButton,deleteButton);
                    setGraphic(hBox);
                }
            }
        });

        purchaseTable.getColumns().addAll(idCol,dateCol,nameCol,purchaseCostCol,quantityCol,unitCol,buttonCol);
        purchaseTable.setItems(purchaseItems);

        // Add the table to the popup window
        StackPane root = new StackPane();
        root.getChildren().add(purchaseTable);
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add("settingsStyleSheet.css");
        popupStage.setScene(scene);

        // Show the popup window
        popupStage.show();
    }




}
