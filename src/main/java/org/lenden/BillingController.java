package org.lenden;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.lenden.dao.DaoImpl;
import org.lenden.model.Bill;
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
    @FXML
    Label grandTotalLabel;
    @FXML
    Label cgstLabel;
    @FXML
    Label sgstLabel;
    @FXML
    Label serviceChargeLabel;
    @FXML
    Label totalLabel;
    @FXML
    TextField discountField;

    Bill bill = new Bill();
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
                        setStyle("-fx-background-color: #c9f5c9;");
                    } else {
                        // Set the background color of the cell to red if the food item is not available
                        setStyle("-fx-background-color: #f5c9c9;");
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
                        setStyle("-fx-background-color: #c9f5c9;");
                    } else {
                        // Set the background color of the cell to red if the food item is not available
                        setStyle("-fx-background-color: #f5c9c9;");
                    }
                }
            }
        });
    }

    public void addMenuItemtoBill(MouseEvent e)
    {
        //Getting Selected Food Items
        FoodItems selectedFoodItem = foodItemsTable.getSelectionModel().getSelectedItem();
        String selectedFoodItemName = selectedFoodItem.getFoodItemName();
        int selectedFoodItemprice = selectedFoodItem.getFoodItemPrice();
        String selectedFoodItemAvailability = selectedFoodItem.getFoodItemAvailability();

        // Create a cell value factory for the Name column
        TableColumn<FoodItems, String> nameColB = new TableColumn<>("Name");
        nameColB.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));

        // Create a cell value factory for the Price column
        TableColumn<FoodItems, String> priceColB = new TableColumn<>("Price");
        priceColB.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));

        // Create a cell value factory for the Quantity column
        TableColumn<BillItems, Integer> quantColB = new TableColumn<>("Quantity");
        quantColB.setMinWidth(100);
        quantColB.setCellValueFactory(new PropertyValueFactory<>("foodItemQuantity"));
        quantColB.setCellFactory(col -> {
            TableCell<BillItems, Integer> cell = new TableCell<>() {
                @Override
                public void updateItem(Integer quantity, boolean empty) {
                    super.updateItem(quantity, empty);
                    if (empty)
                    {
                        setGraphic(null);
                        setText(null);
                    }
                    else
                    {
                        HBox hbox = new HBox(20);
                        Text txtQuantity = new Text(quantity.toString());
                        Button btnMinus = new Button("-");
                        btnMinus.setStyle("-fx-background-color: #fa8484; -fx-text-fill: white;");
                        Button btnPlus = new Button("+");
                        btnPlus.setStyle("-fx-background-color: #96fa84; -fx-text-fill: white;");

                        btnMinus.setOnAction(event -> {

                            BillItems item = getTableView().getItems().get(getIndex());
                            int currentQuantity = item.getFoodItemQuantity();
                            if (currentQuantity > 1) {
                                item.setFoodItemQuantity(currentQuantity - 1);
                                txtQuantity.setText(String.valueOf(currentQuantity - 1));
                                int index = billTableItems.indexOf(item);
                                billTableItems.set(index, item);
                            } else {
                                billTableItems.remove(item);
                                billTable.setItems(billTableItems);
                            }

                            //update Grand Total
                            updateGrandTotal(billTableItems);

                        });

                        btnPlus.setOnAction(event -> {

                            BillItems item = getTableView().getItems().get(getIndex());
                            int currentQuantity = item.getFoodItemQuantity();
                            item.setFoodItemQuantity(currentQuantity + 1);
                            txtQuantity.setText(String.valueOf(currentQuantity + 1));
                            int index = billTableItems.indexOf(item);
                            billTableItems.set(index, item);

                            //update Grand Total
                            updateGrandTotal(billTableItems);
                        });
                        hbox.getChildren().addAll(btnMinus, txtQuantity, btnPlus);
                        setGraphic(hbox);
                        setText(null);
                    }
                }
            };
            return cell;
        });
        quantColB.setCellValueFactory(new PropertyValueFactory<>("foodItemQuantity"));

//----------------------------------------------------------------------------------------------------------------------

        //Adding only if the Item in available in Menu
        if(selectedFoodItemAvailability.equals("NOT Available"))
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
                    //updating quantity in object
                    item.setFoodItemQuantity(item.getFoodItemQuantity() + 1);

                    //update Grand Total
                    updateGrandTotal(billTableItems);

                    //finding index of item in list
                    int index = billTableItems.indexOf(item);

                    //updating updated quantity object in list
                    billTableItems.set(index, item);

                    itemFound = true;
                    break;
                }
            }
            if (!itemFound) {
                BillItems newItem = new BillItems(selectedFoodItemName,selectedFoodItemprice,1 );

                //add the item to the bill items list
                billTableItems.add(newItem);

                //update Grand Total
                updateGrandTotal(billTableItems);

                //set columns and display list items
                billTable.getColumns().setAll(nameColB, priceColB, quantColB);
                billTable.setItems(billTableItems);
            }

        }

    }

    public void computeDiscount(KeyEvent e)
    {
        if(discountField.getText().isEmpty())
        {
            discountField.setText("");
            bill.setDiscount(0);
            updateGrandTotal(billTableItems);
        }
        else
        {
            //checking if the discount value is more than 0 and less than 35
            if( discountField.getText().matches("[0-9]*\\.?[0-9]*") && Double.parseDouble(discountField.getText()) >= 0 && Double.parseDouble(discountField.getText()) < 35)
            {
                double newDiscount = Double.parseDouble(discountField.getText());
                bill.setDiscount(newDiscount);
                updateGrandTotal(billTableItems);
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Discount Out of Limits", ButtonType.OK);
                alert.setHeaderText("Discount should be more than 0 & less than 35");
                alert.setTitle("Attention!");
                alert.showAndWait();

                discountField.setText("");
                bill.setDiscount(0);

                updateGrandTotal(billTableItems);
            }
        }
    }

    public void updateGrandTotal(ObservableList<BillItems> billTableItems)
    {

        double total = 0 ;
        double discount = bill.getDiscount();

        for(BillItems item : billTableItems)
        {
            total += item.getFoodItemPrice() * item.getFoodItemQuantity();
        }

        //discounted Total
        total = total - (total*((discount)/100));

        double cgst = bill.getCgst();
        double sgst = bill.getSgst();
        double servicecharge = bill.getServiceCharge();
        double tax = total * ( ( cgst + sgst + servicecharge  ) / 100 );
        double grandTotal = total + tax;

        cgstLabel.setText(Double.toString(cgst));
        sgstLabel.setText(Double.toString(sgst));
        serviceChargeLabel.setText(Double.toString(servicecharge));
        totalLabel.setText(Double.toString(total));
        grandTotalLabel.setText(Double.toString(grandTotal));

    }

    public void clearBill(MouseEvent e)
    {
        billTableItems.clear();
        discountField.setText("");
        bill.setDiscount(0);
        updateGrandTotal(billTableItems);
    }

    public void generateBill(MouseEvent e)
    {

    }



}
