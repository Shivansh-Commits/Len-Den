package org.lenden;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    Button reserveTableButton;
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
    HashMap<String,ObservableList<BillItems>> openTables = new HashMap<>();
    ArrayList<String> reservedTables = new ArrayList<>();
    MainController mainController = new MainController();

    /**
     * Used to pre-populate, display and set all necessary items before page is loaded, like :-
     * -> setting CSS style class for menu Table.
     * -> Displaying Category buttons based on menu items in databse.
     * -> Populating Menu table with MainCourse food category items.
     * -> Displaying Areas and Open/close Tables.
     *
     * @param url
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resourceBundle
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
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
        availCol.setPrefWidth(170);

        // Set the cell value factories for the table columns
        foodItemsTable.getColumns().setAll(nameCol, priceCol, availCol);
        foodItemsTable.setItems(menuTableItems);

        // Set the background color of the "Availability" cell based on its content
        availCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty || item.equals("NOT Available"))
                {
                    setText("");
                    setStyle("");
                    setGraphic(null);
                }
                else
                {
                    MenuItems selectedMenuItem = getTableRow().getItem();

                    Button addItemToBill = new Button();
                    addItemToBill.setText("Add ");
                    Image add_image = new Image(getClass().getResource("/images/white/outline_add_white_36pt_2x.png").toExternalForm());
                    ImageView add_icon = new ImageView(add_image);
                    add_icon.setFitHeight(20);
                    add_icon.setFitWidth(20);
                    addItemToBill.setGraphic(add_icon);
                    addItemToBill.setCursor(Cursor.HAND);
                    addItemToBill.setPrefSize(150, 25);
                    addItemToBill.getStyleClass().add("menu-add-button");
                    addItemToBill.setOnMouseClicked(event -> addMenuItemtoBill(selectedMenuItem));

                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER);
                    hBox.getChildren().add(addItemToBill);

                    setGraphic(hBox);

                    /*

                    if (item.equals("Available")) {
                        // Set the background color of the cell to green if the food item is available
                        setStyle("-fx-background-color: #c9f5c9;");
                    } else {
                        // Set the background color of the cell to red if the food item is not available
                        setStyle("-fx-background-color: #f5c9c9;");
                    }
                     */
                }
            }
        });

        //--------------------------------------------------------------------------------------------------------------
        //Setting Open Table & Reserved Table Details

        openTables = daoimpl.fetchOpenTableDetails();
        reservedTables = daoimpl.fetchReservedTables();

        //--------------------------------------------------------------------------------------------------------------


        //Display Areas & Tables
        HashMap<String, Integer> areaAndTables = daoimpl.fetchAreaAndTables();

        int tableNumCounter = 1; // Counter for naming tables
        for (Map.Entry<String, Integer> entry : areaAndTables.entrySet())
        {
            String areaName = entry.getKey();
            Integer tablesInArea = entry.getValue();

            // Create Title Pane
            TitledPane titledPane = new TitledPane();
            titledPane.setText(areaName);

            // Create Anchor Pane
            AnchorPane anchorpane = new AnchorPane();
            anchorpane.setPrefSize(730, 620);
            anchorpane.setMinSize(500,620);
            anchorpane.setStyle("-fx-background-color: white;");

            // Create GridPane
            GridPane gridPane = new GridPane();
            gridPane.setLayoutX(15);
            gridPane.setLayoutY(21);
            gridPane.setPrefSize(710, 620);
            gridPane.setStyle("-fx-background-color: white;");
            gridPane.setHgap(5);
            gridPane.setVgap(5);

            AnchorPane.setLeftAnchor(gridPane,15.0);
            AnchorPane.setRightAnchor(gridPane,15.0);


            //Adding Panes (tables) in the grid
            int row = 10;
            int col = 6;
            int temp = 1;
            for(int i=0;i<row;i++)
            {
                for(int j=0;j<col;j++)
                {
                    if(temp>tablesInArea) {
                        break;
                    }

                    Pane table = new Pane();
                    table.setCursor(Cursor.HAND); //Setting the cursor to "hand" when hovered on the pane

                    if(reservedTables.size() > 0 && reservedTables.contains("Table "+tableNumCounter))
                    {
                        table.setOnMouseClicked(this::viewTableBillItems);
                        table.getStyleClass().add("reserve-table");
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

                        Label reservedLabel = new Label();
                        reservedLabel.setText("RESERVED");
                        reservedLabel.setTextFill(Color.WHITE);
                        reservedLabel.setLayoutX(42);
                        reservedLabel.setLayoutY(46);
                        reservedLabel.layoutXProperty().bind(table.widthProperty().subtract(reservedLabel.widthProperty()).divide(2));  // TO center whole label inside Pane
                        reservedLabel.setId("reservedTableLabel");

                        table.getChildren().add(name);
                        table.getChildren().add(reservedLabel);
                    }
                    else if(!openTables.isEmpty() && openTables.containsKey("Table "+tableNumCounter))
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
            titledPane.setContent(anchorpane);
            accordion.getPanes().add(titledPane);
        }
    }


    public void setMainController(MainController mainController)
    {
        this.mainController = mainController;
    }


    /**
     * Populates the Menu Table when any food category is clicked.
     * @param e Mouse Event i.e Click
     */
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
        availCol.setPrefWidth(170);

        // Set the cell value factories for the table columns
        foodItemsTable.getColumns().setAll(nameCol, priceCol, availCol);

        foodItemsTable.setItems(menuTableItems);

        // Set the background color of the "Availability" cell based on its content
        availCol.setCellFactory(column -> new TableCell<>()
        {
            @Override
            protected void updateItem(String item, boolean empty)
            {
                super.updateItem(item, empty);

                if (item == null || empty || item.equals("NOT Available"))
                {
                    setText("");
                    setStyle("");
                    setGraphic(null);
                }
                else
                {
                    MenuItems selectedMenuItem = getTableRow().getItem();

                    Button addItemToBill = new Button();
                    addItemToBill.setText("Add ");
                    Image add_image = new Image(getClass().getResource("/images/white/outline_add_white_36pt_2x.png").toExternalForm());
                    ImageView add_icon = new ImageView(add_image);
                    add_icon.setFitHeight(20);
                    add_icon.setFitWidth(20);
                    addItemToBill.setGraphic(add_icon);
                    addItemToBill.setCursor(Cursor.HAND);
                    addItemToBill.setPrefSize(150, 25);
                    addItemToBill.getStyleClass().add("menu-add-button");
                    addItemToBill.setOnMouseClicked(event -> addMenuItemtoBill(selectedMenuItem));

                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER);
                    hBox.getChildren().add(addItemToBill);

                    setGraphic(hBox);
                    /*

                    setText(item);
                    if (item.equals("Available")) {
                        // Set the background color of the cell to green if the food item is available
                        setStyle("-fx-background-color: #c9f5c9;");
                    } else {
                        // Set the background color of the cell to red if the food item is not available
                        setStyle("-fx-background-color: #f5c9c9;");
                    }

                     */
                }
            }
        });
    }


    /**
     * Adds food items to the bill table when use selects/clicks in the menu
     */
    public void addMenuItemtoBill(MenuItems selectedFoodItem)
    {
        String tableNumber = tableNumberLabel.getText();

        //Checking if the table is Reserved or not
        if(reservedTables.contains(tableNumber))
            return;

        //Adding the selected table to 'openTables' list when first item is added
        if(!openTables.containsKey(tableNumber))
        {
            billTableItems = FXCollections.observableArrayList();
            openTables.put(tableNumber,billTableItems);

            //Setting "Reserve Table" button as disabled
            reserveTableButton.setDisable(true);
        }

        //MenuItems selectedFoodItem = foodItemsTable.getSelectionModel().getSelectedItem();
        if (selectedFoodItem == null || tableNumber.equals("_ : _"))
        {
            return;
        }

        String selectedFoodItemName = selectedFoodItem.getFoodItemName();
        int selectedFoodItemprice = selectedFoodItem.getFoodItemPrice();
        String selectedFoodItemAvailability = selectedFoodItem.getFoodItemAvailability();

        // Create a cell value factory for the Name column
        TableColumn<MenuItems, String> billTableNameCol = new TableColumn<>("Name");
        billTableNameCol.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));
        billTableNameCol.setPrefWidth(150);

        // Create a cell value factory for the Price column
        TableColumn<MenuItems, String> billTablePriceCol = new TableColumn<>("Price");
        billTablePriceCol.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));
        billTablePriceCol.setPrefWidth(110);

        // Create a cell value factory for the Quantity column
        TableColumn<BillItems, Integer> billTableQuantityCol = new TableColumn<>("Quantity");
        billTableQuantityCol.setMinWidth(60);
        billTableQuantityCol.setCellValueFactory(new PropertyValueFactory<>("foodItemQuantity"));
        billTableQuantityCol.setCellFactory(col -> {
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
                        HBox hbox = new HBox(18);
                        Text txtQuantity = new Text(quantity.toString());

                        Button btnMinus = new Button("-");
                        btnMinus.getStyleClass().add("minus-button");
                        btnMinus.setCursor(Cursor.HAND);

                        Button btnPlus = new Button("+");
                        btnPlus.getStyleClass().add("plus-button");
                        btnPlus.setCursor(Cursor.HAND);

                        btnMinus.setOnAction(event -> {

                            BillItems item = getTableView().getItems().get(getIndex());
                            int currentQuantity = item.getFoodItemQuantity();
                            if (currentQuantity > 1)
                            {
                                item.setFoodItemQuantity(currentQuantity - 1);
                                txtQuantity.setText(String.valueOf(currentQuantity - 1));
                                int index = billTableItems.indexOf(item);
                                billTableItems.set(index, item);

                                //Updating Open tables data
                                openTables.put(tableNumber,billTableItems);
                            }
                            else
                            {
                                if(billTableItems.size() == 1)
                                {
                                    //Close the table if user deleted last remaining item.
                                    clearBill();
                                    /*
                                    Alert tableCloseAlert = new Alert(Alert.AlertType.CONFIRMATION, "ARE YOU SURE ?", ButtonType.YES, ButtonType.NO);
                                    tableCloseAlert.setHeaderText("Deleting last item will close the table");
                                    tableCloseAlert.setTitle("Alert!");
                                    tableCloseAlert.showAndWait();

                                    if (tableCloseAlert.getResult() == ButtonType.YES)
                                    {
                                        openTables.remove(tableNumber);

                                        daoimpl.deleteOpenTableDetails(tableNumber, item.getFoodItemName());

                                        billTableItems.remove(item);
                                        billTable.setItems(billTableItems);
                                    }
                                     */
                                }
                                else
                                {
                                    //Deleting the removed item from the DB
                                    daoimpl.deleteOpenTableDetails(tableNumber, item.getFoodItemName());

                                    billTableItems.remove(item);
                                    billTable.setItems(billTableItems);
                                }
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
        billTableQuantityCol.setCellValueFactory(new PropertyValueFactory<>("foodItemQuantity"));

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
                billTable.getColumns().setAll(billTableNameCol, billTablePriceCol, billTableQuantityCol);
                billTable.setItems(billTableItems);
            }

        }

    }


    /**
     * Computes the discount and updates all necessary labels via 'updateTotals()' method
     * @param e key board event i.e key pressed
     */
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


    /**
     * Updates the labels (total, sub-total, grand-total, cgst, sgst, vat, service charge)
     *  and sets their values in the bill object of the selected table.
     * Also saves all the open table details in the Databasse
     * @param billTableItems Contains the items added in that tables bill
     */
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

        //Checking, If label belongs to reserved table ,then don't update
        if(tableGrandTotalLabel != null)
            tableGrandTotalLabel.setText(decimalFormat.format(grandTotal));

        //Displaying taxes
        cgstLabel.setText(decimalFormat.format(cgst));
        sgstLabel.setText(decimalFormat.format(sgst));
        serviceChargeLabel.setText(decimalFormat.format(servicecharge));


        //Saving open table Details
        daoimpl.saveOpenTableDetails(openTables);
    }


    /**
     * @param parent The JavaFX element in which Pane is contained
     * @param fxId fxID of Pane Element (Table)
     * @return Returns the Tables (Pane's Object) reference
     */
    private Pane getTableObjectById(Parent parent, String fxId)
    {
        if (parent != null) {
            for (Node node : parent.getChildrenUnmodifiable()) {
                if (node instanceof Pane && fxId.equals(node.getId())) {
                    return (Pane) node;
                } else if (node instanceof Parent) {
                    Pane pane = getTableObjectById((Parent) node, fxId);
                    if (pane != null) {
                        return pane;
                    }
                }
            }
        }
        return null;
    }


    /**
     * Removes all food items from the bill table and closes that Table
     * @param e Mouse Event i.e Click
     */
    @FXML
    public void clearBill(MouseEvent e)
    {
        if(billTableItems.isEmpty()) // CASE - Bill is empty
            return;

        if(reservedTables.contains(tableNumberLabel.getText())) // CASE - CLicked table is Reserved
            return;

        if(tableNumberLabel.getText().equals("_ : _")) // CASE - No table is selected , but user has added items to the bill table
        {
            billTableItems.clear();
            return;
        }

        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure ?", ButtonType.YES , ButtonType.NO);
        deleteAlert.setHeaderText("Table will be closed and bill items will be deleted");
        deleteAlert.setTitle("Alert!");
        deleteAlert.showAndWait();

        if(deleteAlert.getResult() != ButtonType.YES)
            return;

        String tableNumber = tableNumberLabel.getText();

        Pane table = getTableObjectById(accordion, tableNumber);
        table.getStyleClass().clear();
        table.getStyleClass().add("close-table");

        //Removing open-table from DB
        try
        {
            daoimpl.closeTable(tableNumber);
        }
        catch(SQLException ex)
        {
            Alert closeTableFailedAlert = new Alert(Alert.AlertType.ERROR, "Could not Close table - DB Exception", ButtonType.OK);
            closeTableFailedAlert.setHeaderText("Failed");
            closeTableFailedAlert.setTitle("Error!");
            closeTableFailedAlert.showAndWait();
        }

        //Removing open-table from openTables hashMap
        openTables.remove(tableNumber);

        //Clearing the items in bill table
        billTableItems.clear();

        //Clearing 'discount' label
        discountField.setText("");
        bill.setDiscount(0);

        //Clearing 'table number' label
        tableNumberLabel.setText("_ : _");

        //Clearing the grand-total label on the table(pane)
        tableGrandTotalLabel.setText("_:_");

        updateTotals(billTableItems);
    }

    /**
     * Removes all food items from the bill table and closes that Table (No Argument)
     */
    @FXML
    public void clearBill()
    {
        if(billTableItems.isEmpty()) // CASE - Bill is empty
            return;

        if(reservedTables.contains(tableNumberLabel.getText())) // CASE - CLicked table is Reserved
            return;

        if(tableNumberLabel.getText().equals("_ : _")) // CASE - No table is selected , but user has added items to the bill table
        {
            billTableItems.clear();
            return;
        }

        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure ?", ButtonType.YES , ButtonType.NO);
        deleteAlert.setHeaderText("Table will be closed and bill items will be deleted");
        deleteAlert.setTitle("Alert!");
        deleteAlert.showAndWait();

        if(deleteAlert.getResult() != ButtonType.YES)
            return;

        String tableNumber = tableNumberLabel.getText();

        Pane table = getTableObjectById(accordion, tableNumber);
        table.getStyleClass().clear();
        table.getStyleClass().add("close-table");

        //Removing open-table from DB
        try
        {
            daoimpl.closeTable(tableNumber);
        }
        catch(SQLException ex)
        {
            Alert closeTableFailedAlert = new Alert(Alert.AlertType.ERROR, "Could not Close table - DB Exception", ButtonType.OK);
            closeTableFailedAlert.setHeaderText("Failed");
            closeTableFailedAlert.setTitle("Error!");
            closeTableFailedAlert.showAndWait();
        }

        //Removing open-table from openTables collection
        openTables.remove(tableNumber);

        //Clearing the items in bill table
        billTableItems.clear();

        //Clearing 'discount' label
        discountField.setText("");
        bill.setDiscount(0);

        //Clearing 'table number' label
        tableNumberLabel.setText("_ : _");

        //Clearing the grand-total label on the table(pane)
        tableGrandTotalLabel.setText("_:_");

        updateTotals(billTableItems);
    }


    /**
     * Shows Invoice and gives option to print it
     * @param e Mouse Event i.e Click
     * @throws IOException throws IOException
     */
    @FXML
    public void printBillAndKOT(MouseEvent e) throws IOException
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


    /**
     *  -> Saves Invoice to Database
     *  -> Closes the table
     * @param e Mouse Event i.e Click
     */
    @FXML
    private void settleBill(MouseEvent e)
    {
        //Check if the bill table is empty
        if(billTableItems.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No Items Added. Invoice can not be generated", ButtonType.OK);
            alert.setHeaderText("Can't Generate Invoice");
            alert.setTitle("Sorry!");
            alert.showAndWait();
            return;
        }


        //IF BILL TABLE IS NOT EMPTY , PROCEED TO SAVING AND SETTLING BILL
        bill.setBillItems(billTableItems);
        String tableNumber = tableNumberLabel.getText();
        bill.setTableNumber(tableNumber);

        //Add bill details to DB
        int rowsUpdated = daoimpl.addBillToDB(bill);

        if(rowsUpdated>0)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Bill Settled & Added Successfully", ButtonType.OK);
            alert.setHeaderText("Bill Settled Successfully");
            alert.setTitle("Success!");
            alert.showAndWait();

            billTableItems.clear(); //Clearing the bill table

            openTables.remove(tableNumber); //Removing the table from openTables Collection

            try
            {
                daoimpl.closeTable(tableNumber); //Removing the Open Table num from DB
            }
            catch(SQLException ex)
            {
                Alert closeTableFailedAlert = new Alert(Alert.AlertType.ERROR, "Could not Close table DB Exception", ButtonType.OK);
                closeTableFailedAlert.setHeaderText("Failed");
                closeTableFailedAlert.setTitle("Error!");
                closeTableFailedAlert.showAndWait();
            }

            bill = new Bill(); //Generating new bill after old bill is saved

            discountField.setText(""); //Setting Discount field to blank

            updateTotals(billTableItems); // Updating total labels back to 0

            // Changing open-tables style class to close-table
            Pane table = getTableObjectById(accordion, tableNumber);
            table.getStyleClass().clear();
            table.getStyleClass().add("close-table");

            Label previousTableGrandTotalLabel = (Label) table.lookup("#tableGrandTotalLabel"); //Getting GRAND-TOTAL label of Table
            previousTableGrandTotalLabel.setText("_ : _");
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Bill Not Added", ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
        }
    }


    /**
     * Opens Single Billing Page
     * @param e Mouse Event i.e Click
     * @throws IOException  throws IOException
     */
    @FXML
    public void openSingleBillingPage(MouseEvent e) throws IOException
    {
        mainController.openSingleBillPageFlag=true;
        mainController.openSingleBillingPage(e);
    }


    /**
     * Populates the bill Table, when the Table (Pane) is clicked
     * @param e - Mouse Event i.e Click
     */
    @FXML
    public void viewTableBillItems(MouseEvent e)
    {
        String previousTableNumber = tableNumberLabel.getText();
        /*
        Changing the color of previously selected table (to red) ,if table is not Opened yet (No items are added yet)
        This is being verified by checking the 'openTables' list (Because as soon as a item is added toa table ,that
        table is added to 'openTables' list)
         */
        if(!previousTableNumber.equals("_ : _") && !openTables.containsKey(tableNumberLabel.getText()) && !reservedTables.contains(previousTableNumber))
        {
            Pane table = getTableObjectById(accordion, previousTableNumber);  //tableNumberLabel will still have the previously selected table number
            table.getStyleClass().clear();
            table.getStyleClass().add("close-table");

            Label previousTableGrandTotalLabel = (Label) table.lookup("#tableGrandTotalLabel"); //Getting GRAND-TOTAL label of Table
            previousTableGrandTotalLabel.setText("_ : _");
        }

        //Dealing with clicked Table
        Pane clickedTable = (Pane) e.getSource();

        Label clickedTableLabel = (Label) clickedTable.lookup("#tableNumber"); // Getting TABLE NUMBER label of Table
        String tableNumber = clickedTableLabel.getText();

        tableGrandTotalLabel = (Label) clickedTable.lookup("#tableGrandTotalLabel");


        // Checking if current table is reserved
        if(reservedTables.contains(tableNumber))
        {
            //Setting the table number label
            tableNumberLabel.setText(tableNumber);

            //Settings bill items list as empty
            billTableItems = FXCollections.observableArrayList();

            //Setting bill table as empty
            billTable.setItems(FXCollections.observableArrayList());

            //Updating all labels to Totals 0
            updateTotals(FXCollections.observableArrayList());

            //Setting text of "reserve-table" button as "Un-Reserve Table"
            reserveTableButton.setText("Un-Reserve Table");
            reserveTableButton.setDisable(false);

            return;
        }

        //If the table is Un-Reserved , set the buttons label to "Reserve Table" (DEFAULT SETTING)
        reserveTableButton.setText("Reserve Table");

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
            priceColB.setPrefWidth(120);

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
                            HBox hbox = new HBox(18);
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
                                if (currentQuantity > 1)
                                {
                                    item.setFoodItemQuantity(currentQuantity - 1);
                                    txtQuantity.setText(String.valueOf(currentQuantity - 1));
                                    int index = billTableItems.indexOf(item);
                                    billTableItems.set(index, item);

                                    //Updating Open tables data
                                    openTables.put(tableNumber,billTableItems);
                                }
                                else
                                {
                                    if(billTableItems.size() == 1)
                                    {
                                        //Close the table if user deleted last remaining item.
                                        clearBill(e);
                                        /*
                                        Alert tableCloseAlert = new Alert(Alert.AlertType.CONFIRMATION, "ARE YOU SURE ?", ButtonType.YES, ButtonType.NO);
                                        tableCloseAlert.setHeaderText("Deleting last item will close the table");
                                        tableCloseAlert.setTitle("Alert!");
                                        tableCloseAlert.showAndWait();

                                        if (tableCloseAlert.getResult() == ButtonType.YES)
                                        {
                                            openTables.remove(tableNumber);
                                            //******************* FIND A SOLUTION TO , not use this deleteOpenTableDetails method ***********
                                            daoimpl.deleteOpenTableDetails(tableNumber, item.getFoodItemName());
                                            //***********************************************************************************************
                                            billTableItems.remove(item);
                                            billTable.setItems(billTableItems);
                                        }
                                        */
                                    }
                                    else
                                    {
                                        //Deleting the removed food item from DB
                                        daoimpl.deleteOpenTableDetails(tableNumber, item.getFoodItemName());

                                        //Updating in billTableItems list
                                        billTableItems.remove(item);
                                        billTable.setItems(billTableItems);
                                    }
                                }

                                //Update Grand Total
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

            //Disabiling Reserve button for open table
            reserveTableButton.setDisable(true);

        }
        else
        {
            //Changing the color of table (pane) to green by setting css style to 'open-table'
            clickedTable.getStyleClass().clear();
            clickedTable.getStyleClass().add("open-table");

            //Setting the empty bill item list
            billTableItems = FXCollections.observableArrayList();

            //Setting Table Bill items in Bill Table
            billTable.setItems(billTableItems);

            //Updating Total labels
            updateTotals(billTableItems);

            //Updating Table no. in bill
            bill.setTableNumber(tableNumber);

            //Setting Table Number label for Identification of Open Table (Over Bill Items Table)
            tableNumberLabel.setText(tableNumber);

            //Enabling reserve button for Closed Tables
            reserveTableButton.setDisable(false);
        }
    }


    /**
     * Reserves a Table
     * @param e Mouse Event i.e Click
     */
    @FXML
    public void reserveTable(MouseEvent e)
    {
        String tableNumber = tableNumberLabel.getText();

        // CASE - If user tries to reserve a table when no table is selected
        if(tableNumber.equals("_ : _"))
        {
            Alert reserveAlert = new Alert(Alert.AlertType.WARNING, "Select a Table to Reserve it.", ButtonType.OK);
            reserveAlert.setHeaderText("No Table Selected");
            reserveAlert.setTitle("Alert!");
            reserveAlert.showAndWait();
        }
        //CASE - Un-Reserve Table
        else if(reservedTables.contains(tableNumber))
        {
            //IF Deletion success , then do UI work
            if(daoimpl.deleteReservedTable(tableNumber))
            {
                Pane table = getTableObjectById(accordion, tableNumber);
                table.getStyleClass().clear();
                table.getStyleClass().add("close-table");

                Label selectedTableReservedLabel = (Label) table.lookup("#reservedTableLabel");
                selectedTableReservedLabel.setId("tableGrandTotalLabel");
                selectedTableReservedLabel.setText("_ : _");

                //Setting "reserve table" button's text to "Un-Reserve Table"
                reserveTableButton.setText("Reserve Table");
                reserveTableButton.setDisable(true);

                //Deleting from reservedTables list
                reservedTables.remove(tableNumber);

                //Setting table number label to default
                tableNumberLabel.setText("_ : _");
            }
        }
        //CASE - Reserving table
        else
        {
            //If Saving success , then do UI Work
            if(daoimpl.saveReservedTableDetails(tableNumber))
            {
                Pane table = getTableObjectById(accordion, tableNumber);
                table.getStyleClass().clear();
                table.getStyleClass().add("reserve-table");

                Label selectedTableReservedLabel = (Label) table.lookup("#tableGrandTotalLabel");
                selectedTableReservedLabel.setId("reservedTableLabel");
                selectedTableReservedLabel.setText("RESERVED");

                //Setting "reserve Table" button's text to "Un-reserve"
                reserveTableButton.setText("Un-Reserve Table");
                reserveTableButton.setDisable(true);

                //Adding reserved table num to "reservedTables" list
                reservedTables.add(tableNumber);

                //Setting table number label (present a-top bill table) to default
                tableNumberLabel.setText("_ : _");


                Alert reserveSuccessAlert = new Alert(Alert.AlertType.INFORMATION, "Reservation Success", ButtonType.OK);
                reserveSuccessAlert.setHeaderText(tableNumber +" Reserved");
                reserveSuccessAlert.setTitle("Information");
                reserveSuccessAlert.showAndWait();
            }

        }

    }


}
