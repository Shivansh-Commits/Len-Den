package org.lenden;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.lenden.dao.DaoImpl;
import org.lenden.model.Bill;
import org.lenden.model.BillItems;
import org.lenden.model.MenuItems;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

public class TableBillingController implements Initializable {

    @FXML
    HBox categoryHbox;
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
    Label tableGrandTotalLabel;

    @FXML
    Accordion accordion;
    Bill bill = new Bill();
    DaoImpl daoimpl = new DaoImpl();
    HashMap<String,ObservableList<BillItems>> openTables = new HashMap<String,ObservableList<BillItems>>();
    MainController mainController = new MainController();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        //--------------------------------------------------------------------------------------------------------------
        //Setting CSS Classes
        foodItemsTable.getStyleClass().add("menu-table-items");

        //--------------------------------------------------------------------------------------------------------------
        //Setting Category Buttons
        List<String> categories = daoimpl.getCategories();

        for(String category:categories)
        {
            Button button = new Button(category); // Create a new button

            button.getStyleClass().add("categoryButtons");
            button.setPrefWidth(122);
            button.setPrefHeight(109);
            button.setOnMouseClicked(this::displayMenuItems);
            categoryHbox.getChildren().add(button);
        }

        //--------------------------------------------------------------------------------------------------------------
        //Getting Menu Items FOR MENU TABLE
        menuTableItems = daoimpl.getCategoryItems("Main Course");

        TableColumn<MenuItems, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));
        nameCol.setPrefWidth(200);

        // Create a cell value factory for the Price column
        TableColumn<MenuItems, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));
        priceCol.setPrefWidth(200);

        // Create a cell value factory for the Availability column
        TableColumn<MenuItems, String> availCol = new TableColumn<>("Availability");
        availCol.setCellValueFactory(new PropertyValueFactory<>("foodItemAvailability"));
        availCol.setPrefWidth(200);

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

        //--------------------------------------------------------------------------------------------------------------
        //Setting Open Table Details

        openTables = daoimpl.fetchOpenTableDetails();

        //--------------------------------------------------------------------------------------------------------------
        //Display Areas & Tables
        HashMap<String, Integer> areaAndTables = daoimpl.fetchAreaAndTables();

        int tableNumCounter = 1; // Counter for naming tables
        for (Map.Entry<String, Integer> entry : areaAndTables.entrySet())
        {
            String areaName = entry.getKey();
            Integer tablesInArea = entry.getValue();

            // Create Title Pane
            TitledPane titledPane1 = new TitledPane();
            titledPane1.setText(areaName);
            //titledPane1.setMaxSize(Region.USE_COMPUTED_SIZE,Region.USE_COMPUTED_SIZE);
            titledPane1.setExpanded(true);

            // Create Anchor Pane
            AnchorPane anchorpane = new AnchorPane();
            anchorpane.setPrefSize(730, 690);
            //anchorpane.setMaxSize(2000,690);
            anchorpane.setMinSize(500,690);
            anchorpane.setStyle("-fx-background-color: black;");

            // Create GridPane
            GridPane gridPane = new GridPane();
            gridPane.setLayoutX(15);
            gridPane.setLayoutY(21);
            gridPane.setPrefSize(710, 690);
            //gridPane.setMaxSize(2000,690);
            gridPane.setStyle("-fx-background-color: black;");
            gridPane.setHgap(5);
            gridPane.setVgap(5);

            AnchorPane.setLeftAnchor(gridPane,15.0);
            AnchorPane.setRightAnchor(gridPane,15.0);


            //Adding Panes (tables) in the grid
            int row = 10;
            int col = 6;
            int temp = 0;
            for(int i=0;i<row;i++)
            {
                for(int j=0;j<col;j++)
                {
                    if(temp>tablesInArea) {
                        break;
                    }

                    Pane table = new Pane();
                    table.setCursor(Cursor.HAND); //Setting the cursor to "hand" when hovered on the pane

                    if(!openTables.isEmpty() && openTables.containsKey("Table "+tableNumCounter))
                    {
                        table.setOnMouseClicked(this::viewTableBillItems);
                        table.getStyleClass().add("open-table");
                        table.setPrefSize(120,90);
                        table.setMaxSize(200,Region.USE_COMPUTED_SIZE);
                        table.setId("Table "+tableNumCounter);

                        Label name = new Label();
                        name.setText("Table " + tableNumCounter);
                        name.setTextFill(Color.WHITE);
                        name.setLayoutX(36);
                        name.setLayoutY(14);
                        name.layoutXProperty().bind(table.widthProperty().subtract(name.widthProperty()).divide(2)); // TO center whole label inside Pane
                        name.setId("tableNumber");

                        //Displaying Grand Total on the table pane
                        ObservableList<BillItems> billTableItems = openTables.get("Table "+tableNumCounter);
                        double subTotal = 0 ;
                        double discount = bill.getDiscount();
                        for (BillItems item : billTableItems) {
                            subTotal += item.getFoodItemPrice() * item.getFoodItemQuantity();
                        }
                        double total = subTotal - (subTotal*((discount)/100));
                        double cgst = bill.getCgst();
                        double sgst = bill.getSgst();
                        double servicecharge = bill.getServiceCharge();
                        double tax = total * ( ( cgst + sgst + servicecharge  ) / 100 );
                        double grandTotal = total + tax;

                        Label grandTotalLabel = new Label();
                        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                        grandTotalLabel.setText(    decimalFormat.format(grandTotal)   );
                        grandTotalLabel.setTextFill(Color.WHITE);
                        grandTotalLabel.setLayoutX(42);
                        grandTotalLabel.setLayoutY(46);
                        grandTotalLabel.layoutXProperty().bind(table.widthProperty().subtract(grandTotalLabel.widthProperty()).divide(2));  // TO center whole label inside Pane
                        grandTotalLabel.setId("tableGrandTotalLabel");

                        table.getChildren().add(name);
                        table.getChildren().add(grandTotalLabel);
                    }
                    else
                    {
                        table.setOnMouseClicked(this::viewTableBillItems);
                        table.getStyleClass().add("close-table");
                        table.setPrefSize(120,90);
                        table.setMaxSize(Region.USE_COMPUTED_SIZE,Region.USE_COMPUTED_SIZE);
                        table.setId("Table "+tableNumCounter);

                        Label name = new Label();
                        name.setText("Table " + tableNumCounter);
                        name.setTextFill(Color.WHITE);
                        name.setLayoutX(36);
                        name.setLayoutY(14);
                        name.layoutXProperty().bind(table.widthProperty().subtract(name.widthProperty()).divide(2)); // TO center whole label inside Pane
                        name.setId("tableNumber");

                        Label grandTotalLabel = new Label();
                        grandTotalLabel.setText("_ : _");
                        grandTotalLabel.setTextFill(Color.WHITE);
                        grandTotalLabel.setLayoutX(42);
                        grandTotalLabel.setLayoutY(46);
                        grandTotalLabel.layoutXProperty().bind(table.widthProperty().subtract(grandTotalLabel.widthProperty()).divide(2)); // TO center whole label inside Pane
                        grandTotalLabel.setId("tableGrandTotalLabel");

                        table.getChildren().add(name);
                        table.getChildren().add(grandTotalLabel);
                    }

                    gridPane.add(table,j,i);

                    temp++;
                    tableNumCounter++;
                }
            }

            anchorpane.getChildren().add(gridPane);
            titledPane1.setContent(anchorpane);
            accordion.getPanes().add(titledPane1);
        }


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
        nameCol.setPrefWidth(200);

        // Create a cell value factory for the Price column
        TableColumn<MenuItems, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));
        priceCol.setPrefWidth(200);

        // Create a cell value factory for the Availability column
        TableColumn<MenuItems, String> availCol = new TableColumn<>("Availability");
        availCol.setCellValueFactory(new PropertyValueFactory<>("foodItemAvailability"));
        availCol.setPrefWidth(200);

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

    public void addMenuItemtoBill(MouseEvent e) {
        //Getting Selected Food Items
        MenuItems selectedFoodItem = foodItemsTable.getSelectionModel().getSelectedItem();
        if (selectedFoodItem == null || tableNumberLabel.getText().equals("_ : _"))
        {
            return;
        }


        String selectedFoodItemName = selectedFoodItem.getFoodItemName();
        int selectedFoodItemprice = selectedFoodItem.getFoodItemPrice();
        String selectedFoodItemAvailability = selectedFoodItem.getFoodItemAvailability();

        // Create a cell value factory for the Name column
        TableColumn<MenuItems, String> nameColB = new TableColumn<>("Name");
        nameColB.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));
        nameColB.setPrefWidth(150);

        // Create a cell value factory for the Price column
        TableColumn<MenuItems, String> priceColB = new TableColumn<>("Price");
        priceColB.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));
        priceColB.setPrefWidth(150);

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
                        btnMinus.getStyleClass().add("minus-button");
                        Button btnPlus = new Button("+");
                        btnPlus.getStyleClass().add("plus-button");

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
            //Checking if the discount value is more than 0 and less than 35
            if( discountField.getText().matches("[0-9]*\\.?[0-9]*") && Double.parseDouble(discountField.getText()) >= 0 && Double.parseDouble(discountField.getText()) < 35)
            {
                double newDiscount = Double.parseDouble(discountField.getText());
                bill.setDiscount(newDiscount);
                updateTotals(billTableItems);
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid Discount Value", ButtonType.OK);
                alert.setHeaderText("Discount should be more than 0 & less than 35 and a number");
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
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");

        double subTotal = 0 ;
        double discount = bill.getDiscount();

        if(billTableItems==null)
            billTableItems = FXCollections.observableArrayList();

        for (BillItems item : billTableItems) {
                subTotal += item.getFoodItemPrice() * item.getFoodItemQuantity();
            }
        //Setting SubTotal
        bill.setSubTotal(subTotal);
        //Displaying Subtotal
        subTotalLabel.setText(decimalFormat.format(subTotal));

        //Setting Discounted Total
        double total = subTotal - (subTotal*((discount)/100));
        bill.setTotal(total);
        //Displaying subtotal
        totalLabel.setText(decimalFormat.format(total));

        double cgst = bill.getCgst();
        double sgst = bill.getSgst();
        double servicecharge = bill.getServiceCharge();
        double tax = total * ( ( cgst + sgst + servicecharge  ) / 100 );
        double grandTotal = total + tax;

        //Setting Grand Total in bill
        bill.setGrandTotal(grandTotal);

        //Diplaying Grandtotal
        grandTotalLabel.setText(decimalFormat.format(grandTotal));
        tableGrandTotalLabel.setText(decimalFormat.format(grandTotal));

        //Displaying taxes
        cgstLabel.setText(decimalFormat.format(cgst));
        sgstLabel.setText(decimalFormat.format(sgst));
        serviceChargeLabel.setText(decimalFormat.format(servicecharge));


        //Saving open table Details
        daoimpl.saveOpenTableDetails(openTables);
    }

    private Pane findPaneById(Parent parent, String fxId) //to get the table (pane)
    {
        if (parent != null) {
            for (Node node : parent.getChildrenUnmodifiable()) {
                if (node instanceof Pane && fxId.equals(node.getId())) {
                    return (Pane) node;
                } else if (node instanceof Parent) {
                    Pane pane = findPaneById((Parent) node, fxId);
                    if (pane != null) {
                        return pane;
                    }
                }
            }
        }
        return null;
    }

    @FXML
    public void clearBill(MouseEvent e)
    {
        if(billTableItems.isEmpty()) // A Case where no items are added in the bill table
        {
            return;
        }

        if(tableNumberLabel.getText().equals("_ : _")) // A Case where no table is selected , but user has added items to the bill table
        {
            billTableItems.clear();
            return;
        }

        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION, "ARE YOU SURE ?", ButtonType.YES , ButtonType.NO);
        deleteAlert.setHeaderText("Items in Invoice will be deleted");
        deleteAlert.setTitle("Alert!");
        deleteAlert.showAndWait();

        if(deleteAlert.getResult() != ButtonType.YES)
            return;

        String tableNumber = tableNumberLabel.getText();

        Pane table = findPaneById(accordion, tableNumber);
        table.getStyleClass().clear();
        table.getStyleClass().add("close-table");

        //Removing open-tables bill items from DB
        daoimpl.closeTable(tableNumber);

        //Removing open-table from openTables hashMap
        openTables.remove(tableNumber);

        //Clearing the items in bill table
        billTableItems.clear();

        //Clearing 'discount' label
        discountField.setText("");
        bill.setDiscount(0);

        //Clearing 'table number' label
        tableNumberLabel.setText("_ : _");
        tableGrandTotalLabel.setText("_:_");

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
            nameColB.setPrefWidth(150);

            // Create a cell value factory for the Price column
            TableColumn<MenuItems, String> priceColB = new TableColumn<>("Price");
            priceColB.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));
            priceColB.setPrefWidth(150);

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
                            btnMinus.setCursor(Cursor.HAND);
                            btnMinus.getStyleClass().add("minus-button");

                            Button btnPlus = new Button("+");
                            btnPlus.setCursor(Cursor.HAND);
                            btnPlus.getStyleClass().add("plus-button");

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
            quantColB.setPrefWidth(150);


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
            //Changing the color of table (pane) to green by setting css style to 'open-table'
            clickedTable.getStyleClass().clear();
            clickedTable.getStyleClass().add("open-table");

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
