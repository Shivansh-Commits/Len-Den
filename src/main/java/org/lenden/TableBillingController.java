package org.lenden;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.lenden.dao.DaoImpl;
import org.lenden.model.Bill;
import org.lenden.model.BillItems;
import org.lenden.model.MenuItems;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class TableBillingController implements Initializable {
    @FXML
    Tab singleBillTab;
    @FXML
    Tab tableBillTab;
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
    ObservableList<MenuItems> menuTableItems;
    ObservableList<BillItems> billTableItems = FXCollections.observableArrayList();
    @FXML
    TableView<MenuItems> foodItemsTable;
    @FXML
    Label tableNumberLabel;
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
    Label subTotalLabel;
    @FXML
    TextField discountField;
    @FXML
    Button generateBillButton;

    @FXML
    Label tableGrandTotalLabel;

    Bill bill = new Bill();
    DaoImpl daoimpl = new DaoImpl();
    HashMap<String,ObservableList<BillItems>> openTables = new HashMap<String,ObservableList<BillItems>>();
    MainController mainController = new MainController();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Getting Menu Items FOR MENU TABLE
        menuTableItems = daoimpl.getCategoryItems("Main Course");

        TableColumn<MenuItems, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));

        // Create a cell value factory for the Price column
        TableColumn<MenuItems, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));

        // Create a cell value factory for the Availability column
        TableColumn<MenuItems, String> availCol = new TableColumn<>("Availability");
        availCol.setCellValueFactory(new PropertyValueFactory<>("foodItemAvailability"));

        // Set the cell value factories for the table columns
        foodItemsTable.getColumns().setAll(nameCol, priceCol, availCol);

        foodItemsTable.setItems(menuTableItems);

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


        //-------------------------------
        //Setting Open Table Details
        //-------------------------------
        openTables = daoimpl.fetchOpenTableDetails();
        //updateTotals(openTables.get("Table 1"));
        //billTable.setItems(openTables.get("Table 1"));
//        for(Map.Entry<String, ObservableList<BillItems>> opentables : openTables.entrySet())
//        {
//            updateTotals(opentables.getValue());
//            billTable.setItems(opentables.getValue());
//        }


    }
    public void setMainController(MainController mainController)
    {
        this.mainController = mainController;
    }
    @FXML
    public void displayMenuItems(MouseEvent e)
    {
        Button clickedButton = (Button) e.getSource();
        String category = clickedButton.getText();

        menuTableItems = daoimpl.getCategoryItems(category);

        TableColumn<MenuItems, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));

        // Create a cell value factory for the Price column
        TableColumn<MenuItems, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));

        // Create a cell value factory for the Availability column
        TableColumn<MenuItems, String> availCol = new TableColumn<>("Availability");
        availCol.setCellValueFactory(new PropertyValueFactory<>("foodItemAvailability"));

        // Set the cell value factories for the table columns
        foodItemsTable.getColumns().setAll(nameCol, priceCol, availCol);

        foodItemsTable.setItems(menuTableItems);

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

    public void addMenuItemtoBill(MouseEvent e)
    {
        //Getting Selected Food Items
        MenuItems selectedFoodItem = foodItemsTable.getSelectionModel().getSelectedItem();
        if(selectedFoodItem == null)
        {
            return;
        }

        String selectedFoodItemName = selectedFoodItem.getFoodItemName();
        int selectedFoodItemprice = selectedFoodItem.getFoodItemPrice();
        String selectedFoodItemAvailability = selectedFoodItem.getFoodItemAvailability();

        // Create a cell value factory for the Name column
        TableColumn<MenuItems, String> nameColB = new TableColumn<>("Name");
        nameColB.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));

        // Create a cell value factory for the Price column
        TableColumn<MenuItems, String> priceColB = new TableColumn<>("Price");
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
                            updateTotals(billTableItems);

                        });

                        btnPlus.setOnAction(event -> {

                            BillItems item = getTableView().getItems().get(getIndex());
                            int currentQuantity = item.getFoodItemQuantity();
                            item.setFoodItemQuantity(currentQuantity + 1);
                            txtQuantity.setText(String.valueOf(currentQuantity + 1));
                            int index = billTableItems.indexOf(item);
                            billTableItems.set(index, item);

                            //update Grand Total
                            updateTotals(billTableItems);
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
                    updateTotals(billTableItems);

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
                updateTotals(billTableItems);

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
            updateTotals(billTableItems);
        }
        else
        {
            //checking if the discount value is more than 0 and less than 35
            if( discountField.getText().matches("[0-9]*\\.?[0-9]*") && Double.parseDouble(discountField.getText()) >= 0 && Double.parseDouble(discountField.getText()) < 35)
            {
                double newDiscount = Double.parseDouble(discountField.getText());
                bill.setDiscount(newDiscount);
                updateTotals(billTableItems);
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Discount Out of Limits", ButtonType.OK);
                alert.setHeaderText("Discount should be more than 0 & less than 35");
                alert.setTitle("Attention!");
                alert.showAndWait();

                discountField.setText("");
                bill.setDiscount(0);

                updateTotals(billTableItems);
            }
        }
    }

    public void updateTotals(ObservableList<BillItems> billTableItems)
    {

        double subTotal = 0 ;
        double discount = bill.getDiscount();

        if(billTableItems==null)
            billTableItems = FXCollections.observableArrayList();

        for (BillItems item : billTableItems) {
                subTotal += item.getFoodItemPrice() * item.getFoodItemQuantity();
            }
        //Setting SubTotal
        bill.setSubTotal(subTotal);
        subTotalLabel.setText(Double.toString(subTotal));

        //Setting Discounted Total
        double total = subTotal - (subTotal*((discount)/100));
        bill.setTotal(total);
        totalLabel.setText(Double.toString(total));

        double cgst = bill.getCgst();
        double sgst = bill.getSgst();
        double servicecharge = bill.getServiceCharge();
        double tax = total * ( ( cgst + sgst + servicecharge  ) / 100 );
        double grandTotal = total + tax;

        //setting Grand Total
        bill.setGrandTotal(grandTotal);
        grandTotalLabel.setText(Double.toString(grandTotal));
        tableGrandTotalLabel.setText(Double.toString(grandTotal));

        cgstLabel.setText(Double.toString(cgst));
        sgstLabel.setText(Double.toString(sgst));
        serviceChargeLabel.setText(Double.toString(servicecharge));

        daoimpl.saveOpenTableDetails(openTables);
    }

    @FXML
    public void clearBill(MouseEvent e)
    {
        daoimpl.closeTable(tableNumberLabel.getText());

        billTableItems.clear();

        discountField.setText("");
        bill.setDiscount(0);

        updateTotals(billTableItems);
    }

    @FXML
    public void generateInvoice(MouseEvent e) throws IOException
    {
        if(billTableItems.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No Items Added. Invoice can not be generated", ButtonType.OK);
            alert.setHeaderText("Can't Generate Invoice");
            alert.setTitle("Sorry!");
            alert.showAndWait();
            return;
        }

        bill.setBillItems(billTableItems);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("bill_preview.fxml"));
        Parent root = loader.load();

        // Get the controller of the preview window
        BillPrintController controller = loader.getController();

        // Set the bill details in the controller
        controller.setPreviewBillValues(bill);

        // Create a new stage for the preview window
        Stage stage = new Stage();
        stage.setTitle("Preview");
        stage.setScene(new Scene(root));

        // Show the preview window
        stage.show();

    }

    @FXML
    private void saveInvoice(MouseEvent e)
    {
        if(billTableItems.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No Items Added. Invoice can not be generated", ButtonType.OK);
            alert.setHeaderText("Can't Generate Invoice");
            alert.setTitle("Sorry!");
            alert.showAndWait();
            return;
        }

        bill.setBillItems(billTableItems);

        //ADD BILL Details to DB
        int rowsUpdated = daoimpl.addBillToDB(bill);

        if(rowsUpdated>0)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Bill Added Successfully", ButtonType.OK);
            alert.setHeaderText("Saved");
            alert.setTitle("Success!");
            alert.showAndWait();

            billTableItems.clear(); //Clearing the bill table

            Bill newBill = new Bill(); //Generating new bill after bill is saved
            bill=newBill;

            discountField.setText(""); //Setting Discount field to blank

            updateTotals(billTableItems);// Updating total labels back to 0
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Bill Not Added", ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
        }
    }

    @FXML
    public void openSingleBillingPage(MouseEvent e) throws IOException, SQLException
    {
        mainController.openSingleBillPageFlag=true;
        mainController.openSingleBillingPage(e);
    }

    @FXML
    public void viewTableBillItems(MouseEvent e)
    {
        Pane clickedTable = (Pane) e.getSource();

        Label clickedTableLabel = (Label) clickedTable.lookup("#tableNumber"); // Getting TABLE NUMBER label of Table
        String tableNumber = clickedTableLabel.getText();

        Label selectedTableGrandTotalLabel = (Label) clickedTable.lookup("#tableGrandTotalLabel"); //Getting GRAND TOTAL lablel of Table
        tableGrandTotalLabel = selectedTableGrandTotalLabel;


        if(openTables.containsKey(tableNumber))
        {
            //----------------------SETTING COLUMNS FOR BILL TABLE, TO DISPLAY OPEN TABLE BILL ITEMS--------------------

            // Create a cell value factory for the Name column
            TableColumn<MenuItems, String> nameColB = new TableColumn<>("Name");
            nameColB.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));

            // Create a cell value factory for the Price column
            TableColumn<MenuItems, String> priceColB = new TableColumn<>("Price");
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
                                    daoimpl.deleteOpenTableDetails(tableNumber,item.getFoodItemName());
                                    billTableItems.remove(item);
                                    billTable.setItems(billTableItems);
                                }

                                //Updating Open tables data
                                openTables.put(tableNumber,billTableItems);

                                //update Grand Total
                                updateTotals(billTableItems);

                            });

                            btnPlus.setOnAction(event -> {

                                BillItems item = getTableView().getItems().get(getIndex());
                                int currentQuantity = item.getFoodItemQuantity();
                                item.setFoodItemQuantity(currentQuantity + 1);
                                txtQuantity.setText(String.valueOf(currentQuantity + 1));
                                int index = billTableItems.indexOf(item);
                                billTableItems.set(index, item);

                                //Updating Open tables data
                                openTables.put(tableNumber,billTableItems);

                                //update Grand Total
                                updateTotals(billTableItems);
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
            //----------------------------------------------------------------------------------------------------------



            //Getting Table Bill items
            billTableItems = openTables.get(tableNumber);

            //Setting Table Bill items in Bill Table
            billTable.getColumns().setAll(nameColB, priceColB, quantColB);
            billTable.setItems(billTableItems);

            //Updating Total labels
            updateTotals(billTableItems);

            //Updating Table no. in bill
            bill.setTableNumber(tableNumber);

            //Setting Table Number label for Identification of Open Table (Over Bill Items Table)
            tableNumberLabel.setText(tableNumber);

        }
        else
        {

            //Creating a blank list of bill items
            ObservableList<BillItems> newBillTableItems = FXCollections.observableArrayList();
            openTables.put(tableNumber,newBillTableItems);

            //Setting the empty bill item list
            billTableItems = newBillTableItems;

            //Setting Table Bill items in Bill Table
            billTable.setItems(billTableItems);

            //Updating Total labels
            updateTotals(billTableItems);

            //Updating Table no. in bill
            bill.setTableNumber(tableNumber);

            //Setting Table Number label for Identification of Open Table (Over Bill Items Table)
            tableNumberLabel.setText(tableNumber);

        }
    }
}
