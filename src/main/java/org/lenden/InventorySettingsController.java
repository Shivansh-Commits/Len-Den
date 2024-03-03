package org.lenden;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.lenden.dao.DaoImpl;
import org.lenden.model.Inventory;
import org.lenden.model.Menu;

import java.net.URL;
import java.util.ResourceBundle;

public class InventorySettingsController implements Initializable {

    @FXML
    Label inventoryItemId;

    @FXML
    TextField inventoryItemNameTextField;

    @FXML
    TextField inventoryItemPurchaseCostTextField;

    @FXML
    TextField inventoryItemStockQuantityTextField;

    @FXML
    ComboBox inventoryItemUnitComboBox;

    @FXML
    Button addToInventoryButton;

    @FXML
    Button updateInventoryItemButton;

    @FXML
    Button deleteInventoryItemButton;

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

        updateInventoryItemButton.setTooltip(new javafx.scene.control.Tooltip("Update Raw Material Details"));

        deleteInventoryItemButton.setTooltip(new javafx.scene.control.Tooltip("Delete raw Material"));

        //--------------------------------------------------------------------------------------------------------------
        //Populating tables

        populateTable();

        //--------------------------------------------------------------------------------------------------------------

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
        nameCol.setPrefWidth(200);
        nameCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Price column
        TableColumn<Inventory, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("inventoryItemPrice"));
        priceCol.setPrefWidth(200);
        priceCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Stock Quantity column
        TableColumn<Inventory, String> stockCol = new TableColumn<>("Unit");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("inventoryItemUnit"));
        stockCol.setPrefWidth(100);
        stockCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Variant column
        TableColumn<Inventory, Double> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("inventoryItemQuantity"));
        quantityCol.setPrefWidth(200);
        quantityCol.setStyle("-fx-alignment: CENTER;");

        //Create col for Buttons col
        TableColumn<Inventory,Void> buttonCol = new TableColumn<>("");
        buttonCol.setCellFactory(param -> new TableCell<Inventory, Void>() {
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
                    Button deleteButton = new Button("Delete");

                    {
                        updateButton.getStyleClass().add("saveEdit-button");
                        // Set actions for the edit button
                        updateButton.setOnAction(event -> {
                            Inventory selectedItem = getTableView().getItems().get(getIndex());

                        });

                        deleteButton.getStyleClass().add("saveEdit-button");
                        // Set actions for the delete button
                        deleteButton.setOnAction(event -> {
                            Inventory selectedItem = getTableView().getItems().get(getIndex());
                            deleteInventoryItem(selectedItem);

                        });
                    }

                    // Set buttons into cell
                    HBox hBox = new HBox();
                    hBox.setSpacing(10);
                    hBox.getChildren().addAll(updateButton,deleteButton);
                    setGraphic(hBox);
                }
            }
        });

        // Set the cell value factories for the table columns
        inventoryTable.getColumns().setAll(nameCol, priceCol, stockCol, quantityCol,buttonCol);
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
                    inventoryItemId.setText("_ _");


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

}
