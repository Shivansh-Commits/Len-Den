package org.lenden;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.lenden.dao.DaoImpl;
import org.lenden.model.BillItems;
import org.lenden.model.FoodItems;
import java.net.URL;
import java.util.ResourceBundle;


public class BillingController implements Initializable
{

    @FXML
    Button mainCourseCategoryButton;
    @FXML
    Button beveregesCategoryButton;
    @FXML
    Button breadsCategoryButton;
    @FXML
    Button dessertCategoryButton;
    @FXML
    Button snackCategoryButton;
    @FXML
    Button extraCategoryButton;
    @FXML
    AnchorPane categoryAnchorPane;
    @FXML
    ScrollPane categoryScrollPane;

    ObservableList<FoodItems> menuTableItems;
    ObservableList<BillItems> billTableItems = FXCollections.observableArrayList();
    @FXML
    TableView<FoodItems> foodItemsTable;
    @FXML
    TableView billTable;

    DaoImpl daoimpl = new DaoImpl();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //FOR MENU TABLE
        menuTableItems = daoimpl.getCategoryItems("Main Course");

        TableColumn<FoodItems, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));

        // Create a cell value factory for the Price column
        TableColumn<FoodItems, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));

        // Create a cell value factory for the Availability column
        TableColumn<FoodItems, String> availCol = new TableColumn<>("Availability");
        availCol.setCellValueFactory(new PropertyValueFactory<>("foodItemAvailability"));

        // Set the cell value factories for the table columns
        foodItemsTable.getColumns().setAll(nameCol, priceCol, availCol);

        foodItemsTable.setItems(menuTableItems);

        // Set the background color of the "Availability" cell based on its content
        availCol.setCellFactory(column -> new TableCell<FoodItems, String>() {
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
                        setStyle("-fx-background-color: green;");
                    } else {
                        // Set the background color of the cell to red if the food item is not available
                        setStyle("-fx-background-color: red;");
                    }
                }
            }
        });

    }

    @FXML
    public void displayMenuItems(MouseEvent e)
    {
        Button clickedButton = (Button) e.getSource();
        String category = clickedButton.getText();

        menuTableItems = daoimpl.getCategoryItems(category);

        TableColumn<FoodItems, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));

        // Create a cell value factory for the Price column
        TableColumn<FoodItems, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));

        // Create a cell value factory for the Availability column
        TableColumn<FoodItems, String> availCol = new TableColumn<>("Availability");
        availCol.setCellValueFactory(new PropertyValueFactory<>("foodItemAvailability"));

        // Set the cell value factories for the table columns
        foodItemsTable.getColumns().setAll(nameCol, priceCol, availCol);

        foodItemsTable.setItems(menuTableItems);

        // Set the background color of the "Availability" cell based on its content
        availCol.setCellFactory(column -> new TableCell<FoodItems, String>() {
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
                        setStyle("-fx-background-color: green;");
                    } else {
                        // Set the background color of the cell to red if the food item is not available
                        setStyle("-fx-background-color: red;");
                    }
                }
            }
        });
    }

    public void addMenuItemtoBill(MouseEvent e)
    {
        TableColumn<FoodItems, String> nameColB = new TableColumn<>("Name");
        nameColB.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));

        // Create a cell value factory for the Price column
        TableColumn<FoodItems, String> priceColB = new TableColumn<>("Price");
        priceColB.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));

        // Create a cell value factory for the Availability column
        TableColumn<FoodItems, String> quantColB = new TableColumn<>("Quantity");
        quantColB.setCellValueFactory(new PropertyValueFactory<>("foodItemQuantity"));

//----------------------------------------------------------------------------------------------------------------------

        //Getting Selected Food Items
        FoodItems selectedFoodItem = foodItemsTable.getSelectionModel().getSelectedItem();
            String selectedFoodItemName = selectedFoodItem.getFoodItemName();
            int selectedFoodItemprice = selectedFoodItem.getFoodItemPrice();
            String selectedFoodItemAvailability = selectedFoodItem.getFoodItemAvailability();

        //Adding only if the Item in available in Menu
        if(selectedFoodItemAvailability.equals("NOT AVAILABLE"))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Selected Item Not Available", ButtonType.OK);
            alert.setHeaderText("Item Not Available");
            alert.setTitle("Sorry!");
            alert.showAndWait();
        }
        else
        {
            boolean itemFound = false;
            for (BillItems item : billTableItems) {
                if (item.getFoodItemName().equals(selectedFoodItemName))
                {
                    item.setFoodItemQuantity(item.getFoodItemQuantity() + 1); //updating quantity in object
                    int index = billTableItems.indexOf(item);  //finding index of item in list
                    billTableItems.set(index, item);  //updating updated quantity object in list
                    itemFound = true;
                    break;
                }
            }
            if (!itemFound) {
                BillItems newItem = new BillItems(selectedFoodItemName,selectedFoodItemprice,1 );
                billTableItems.add(newItem);
                billTable.getColumns().setAll(nameColB, priceColB, quantColB);
                billTable.setItems(billTableItems);
            }

        }

    }






}
