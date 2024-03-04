package org.lenden;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.lenden.dao.DaoImpl;
import org.lenden.model.Inventory;

import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class InventorySettingsController implements Initializable {


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
    TableView<Inventory> inventoryTable;

    ObservableList<Inventory> inventoryTableItems =  FXCollections.observableArrayList();

    DaoImpl daoimpl = new DaoImpl();

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        //--------------------------------------------------------------------------------------------------------------
        //Setting Tips for Different Buttons - Will be Visible on Hover
        addToInventoryButton.setTooltip(new javafx.scene.control.Tooltip("Add Raw Material to Menu"));

        //--------------------------------------------------------------------------------------------------------------
        //Populating tables

        populateTable();

        //--------------------------------------------------------------------------------------------------------------
        //Input Validation
        addAlphabeticInputFieldValidation(inventoryItemNameTextField);
        addAlphabeticInputFieldValidation(inventoryItemUnitComboBox);

        addNumericInputFieldValidation(inventoryItemPurchaseCostTextField);
        addNumericInputFieldValidation(inventoryItemStockQuantityTextField);

        //--------------------------------------------------------------------------------------------------------------
        //Populating unit combo box

        final ObservableList<String> items;
        try
        {
            items = daoimpl.fetchUnits();
            inventoryItemUnitComboBox.setItems(items);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        
        //Search functionality for combobox


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
            if (!input.matches("[a-zA-Z]")) {
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

        // Create a cell value factory for the Name column
        TableColumn<Inventory, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("inventoryItemName"));
        nameCol.setPrefWidth(150);
        nameCol.setStyle("-fx-alignment: CENTER;");
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


        // Create a cell value factory for the Price column
        TableColumn<Inventory, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("inventoryItemPrice"));
        priceCol.setPrefWidth(150);
        priceCol.setStyle("-fx-alignment: CENTER;");
        priceCol.setCellFactory(column -> {
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


        // Create a cell value factory for the Stock Quantity column
        TableColumn<Inventory, String> unitCol = new TableColumn<>("Unit");
        unitCol.setCellValueFactory(new PropertyValueFactory<>("inventoryItemUnit"));
        unitCol.setPrefWidth(100);
        unitCol.setStyle("-fx-alignment: CENTER;");
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
                        comboBox.setItems(daoimpl.fetchUnits());
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


        // Create a cell value factory for the Variant column
        TableColumn<Inventory, Double> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("inventoryItemQuantity"));
        quantityCol.setPrefWidth(150);
        quantityCol.setStyle("-fx-alignment: CENTER;");
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
                    } else {
                        textField.setText(String.valueOf(item));
                        setGraphic(hBox);
                    }
                }
            };
        });


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
                    Button updateButton = new Button("Update");
                    updateButton.setCursor(Cursor.HAND);
                    updateButton.setPrefWidth(100);
                    Image update_symbol = new Image(Objects.requireNonNull(getClass().getResource("/images/black/publish_FILL0_wght400_GRAD0_opsz48.png")).toExternalForm());
                    ImageView imageView = new ImageView(update_symbol);
                    imageView.setFitWidth(16); // Adjust the width as needed
                    imageView.setFitHeight(16);
                    updateButton.setGraphic(imageView);

                    Button deleteButton = new Button("Delete");
                    deleteButton.setCursor(Cursor.HAND);
                    updateButton.setPrefWidth(100);
                    Image delete_symbol = new Image(Objects.requireNonNull(getClass().getResource("/images/black/delete_FILL0_wght400_GRAD0_opsz48.png")).toExternalForm());
                    ImageView imageView1 = new ImageView(delete_symbol);
                    imageView1.setFitWidth(16); // Adjust the width as needed
                    imageView1.setFitHeight(16);
                    deleteButton.setGraphic(imageView1);

                    {
                        updateButton.getStyleClass().add("update-button");
                        updateButton.setTooltip(new javafx.scene.control.Tooltip("Update the Inventory Item Details"));
                        updateButton.setOnAction(event ->
                        {
                            Inventory selectedItem = getTableView().getItems().get(getIndex());
                            TableRow<Inventory> row = getTableRow();
                            if (row != null) {
                                TextField nameField = (TextField) row.lookup("#name");
                                TextField priceField = (TextField) row.lookup("#price");
                                TextField unitField = (TextField) row.lookup("#unit");
                                TextField quantityField = (TextField) row.lookup("#quantity");

                                if (nameField != null && priceField != null && unitField != null && quantityField != null) {
                                    String newName = nameField.getText();
                                    Double newPrice = Double.valueOf(priceField.getText());
                                    String newUnit = unitField.getText();
                                    Double newQuantity = Double.valueOf(quantityField.getText());

                                    selectedItem.setInventoryItemName(newName);
                                    selectedItem.setInventoryItemPrice(newPrice);
                                    selectedItem.setInventoryItemUnit(newUnit);
                                    selectedItem.setInventoryItemQuantity(newQuantity);

                                    updateInventoryItem(selectedItem);
                                }
                            }
                        });

                        deleteButton.getStyleClass().add("delete-button");
                        deleteButton.setTooltip(new javafx.scene.control.Tooltip("Delete the Inventory Item"));
                        deleteButton.setOnAction(event ->
                        {
                            Inventory selectedItem = getTableView().getItems().get(getIndex());

                            deleteInventoryItem(selectedItem);

                        });
                    }

                    // Set buttons into cell
                    HBox hBox = new HBox();
                    hBox.setSpacing(10);
                    hBox.setPadding(new Insets(10,10,10,10));
                    hBox.getChildren().addAll(updateButton,deleteButton);
                    setGraphic(hBox);
                }
            }
        });

        // Set the cell value factories for the table columns
        inventoryTable.getColumns().setAll(nameCol, priceCol, unitCol, quantityCol,buttonCol);
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

                    //SHOW TABLE WITH UPDATED ITEMS
                    populateTable();

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

    public void updateInventoryItem(Inventory selectedItem)
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
                    if (!daoimpl.updateInventoryItem(selectedItem))
                    {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "COULD NOT UPDATE ITEM. If this error keeps occuring contact customer support.", ButtonType.OK);
                        alert.setHeaderText("Item not Update!");
                        alert.setTitle("Alert!");
                        alert.showAndWait();
                    }
                    else
                    {
                        populateTable();
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

    public void addInventoryItem()
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

        // Assuming you have fields for the new item's details such as name, price, unit, and quantity
        String newName = inventoryItemNameTextField.getText();
        Double newPrice = Double.valueOf(inventoryItemPurchaseCostTextField.getText());
        String newUnit = (String) inventoryItemUnitComboBox.getSelectionModel().getSelectedItem();
        Double newQuantity = Double.valueOf(inventoryItemStockQuantityTextField.getText());

        // Create a new Inventory object with the provided details
        Inventory newItem = new Inventory(newName, newPrice, newUnit, newQuantity);

        try {
            // Attempt to add the new item to the database
            if (daoimpl.addInventoryItem(newItem))
            {
                // If successful, clear the input fields and refresh the table
                inventoryItemNameTextField.clear();
                inventoryItemPurchaseCostTextField.clear();
                inventoryItemUnitComboBox.getSelectionModel().clearSelection();
                inventoryItemStockQuantityTextField.clear();

                // Refresh the table with updated items
                populateTable();
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



}
