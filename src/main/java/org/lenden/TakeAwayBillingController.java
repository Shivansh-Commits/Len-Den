package org.lenden;


import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.lenden.dao.DaoImpl;
import org.lenden.model.Bill;
import org.lenden.model.BillItems;
import org.lenden.model.Menu;
import org.lenden.model.Variant;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TakeAwayBillingController implements Initializable
{

    @FXML
    HBox categoryHbox;
    @FXML
    AnchorPane categoryAnchorPane;
    @FXML
    ScrollPane categoryScrollPane;
    ObservableList<Menu> menuTableItems;
    ObservableList<BillItems> billTableItems = FXCollections.observableArrayList();
    @FXML
    TableView<Menu> foodItemsTable;
    @FXML
    TableView billTable;
    @FXML
    Label grandTotalLabel;
    @FXML
    Label gstLabel;
    @FXML
    Label serviceChargeLabel;
    @FXML
    Label totalLabel;
    @FXML
    Label subTotalLabel;
    @FXML
    TextField discountField;
    @FXML
    TilePane takeAwayOrdersTilePane;
    @FXML
    ComboBox<String> modeOfPaymentComboBox;
    @FXML
    Button placeOrderButton;
    @FXML
    Button clearBillButton;


    HashMap<String,ObservableList<BillItems>> openOrders = new HashMap<>();
    Bill bill = new Bill();
    DaoImpl daoimpl = new DaoImpl();
    MainController mainController = new MainController();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //--------------------------------------------------------------------------------------------------------------
        //Setting Tips for Different Buttons - Will be Visible on Hover

        placeOrderButton.setTooltip(new javafx.scene.control.Tooltip("Create order"));

        clearBillButton.setTooltip(new javafx.scene.control.Tooltip("Clear all items from Bill"));

        //--------------------------------------------------------------------------------------------------------------
        //Setting Category Buttons
        List<String> categories = null;
        try
        {
            categories = daoimpl.fetchCategories();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+e.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
        }

        for(String category:categories)
        {
            Button button = new Button(category); // Create a new button

            button.getStyleClass().add("categoryButtons");
            button.setPrefWidth(122);
            button.setPrefHeight(109);
            button.setOnMouseClicked(this::displayMenuItems);
            categoryHbox.getChildren().add(button);
        }

        try {
            //FOR MENU TABLE
            menuTableItems = daoimpl.fetchCategoryItems("Main Course");
        }
        catch (Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
        }

        TableColumn<Menu, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));
        nameCol.setPrefWidth(150);
        nameCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Price column
        TableColumn<Menu, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));
        priceCol.setPrefWidth(200);
        priceCol.setStyle("-fx-alignment: CENTER;");
        //Displaying the price value in col, only if variant is not added
        priceCol.setCellValueFactory(cellData -> {
            Menu menuItem = cellData.getValue();
            if (menuItem.getVariantData() == null || menuItem.getVariantData().isEmpty()) {
                return new SimpleStringProperty(String.valueOf(menuItem.getFoodItemPrice()));
            } else {
                return new SimpleStringProperty("Depends On Variant");
            }
        });


        // Create a cell value factory for the Price column
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

        // Set the cell value factories for the table columns
        foodItemsTable.getColumns().setAll(nameCol, priceCol, variantCol);
        foodItemsTable.setItems(menuTableItems);



        //--------------------------------------------------------------------------------------------------------------
        //Adding Values to Mode Of payment Combo Box

        try
        {
            ArrayList<String> modeofpayments = daoimpl.fetchModeOfPayment();
            modeOfPaymentComboBox.getItems().addAll(modeofpayments);
            String defaultmodeofpayment = daoimpl.fetchDefaultModeOfPayment();
            if(defaultmodeofpayment!=null)
            {
                modeOfPaymentComboBox.setValue(defaultmodeofpayment);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        //--------------------------------------------------------------------------------------------------------------
        //Setting Default Discount

        try{
            Double defaultDiscount = daoimpl.fetchDefaultDiscount();

            bill.setDiscount(defaultDiscount);
            discountField.setText(Double.toString(defaultDiscount));
        }
        catch(Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
        }

        //--------------------------------------------------------------------------------------------------------------
        //Displaying pending Take-away orders
        displayTakeAwayOrdersInitially();

        //--------------------------------------------------------------------------------------------------------------

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

        try {
            menuTableItems = daoimpl.fetchCategoryItems(category);
        }
        catch (Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
        }

        TableColumn<Menu, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));
        nameCol.setPrefWidth(200);
        nameCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Price column
        TableColumn<Menu, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));
        priceCol.setPrefWidth(200);
        priceCol.setStyle("-fx-alignment: CENTER;");
        //Displaying the price value in col, only if variant is not added
        priceCol.setCellValueFactory(cellData -> {
            Menu menuItem = cellData.getValue();
            if (menuItem.getVariantData() == null || menuItem.getVariantData().isEmpty()) {
                return new SimpleStringProperty(String.valueOf(menuItem.getFoodItemPrice()));
            } else {
                return new SimpleStringProperty("Depends On Variant");
            }
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

        // Set the cell value factories for the table columns
        foodItemsTable.getColumns().setAll(nameCol, priceCol, variantCol);

        foodItemsTable.setItems(menuTableItems);

    }
    public void addMenuItemtoBill()
    {
        Menu selectedMenuFoodItem = foodItemsTable.getSelectionModel().getSelectedItem();

        BillItems selectedFoodItem = new BillItems();
        selectedFoodItem.setId(selectedMenuFoodItem.getId());
        selectedFoodItem.setFoodItemName(selectedMenuFoodItem.getFoodItemName());
        //selectedFoodItem.setFoodItemQuantity(selectedMenuFoodItem.getStockQuantity());
        selectedFoodItem.setFoodItemPrice(selectedMenuFoodItem.getFoodItemPrice());

        //MenuItems selectedFoodItem = foodItemsTable.getSelectionModel().getSelectedItem();
        if(selectedFoodItem == null)
        {
            return;
        }

        //Setting Default Discount
        try{
            Double defaultDiscount = daoimpl.fetchDefaultDiscount();
            bill.setDiscount(defaultDiscount);
            discountField.setText(Double.toString(defaultDiscount));
        }
        catch(Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
        }

        // Variant Selection
        if (selectedMenuFoodItem.getVariantData() != null) {
            Dialog<String> variantDialog = new Dialog<>();
            variantDialog.setTitle("Variant Selection");
            variantDialog.setHeaderText("Select Variant");

            // Create a flow pane to hold the variant buttons
            FlowPane variantPane = new FlowPane();
            variantPane.setHgap(10);
            variantPane.setVgap(5);

            // Add a button for each variant
            for (Variant variant : selectedMenuFoodItem.getVariantData()) {
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

                // Process selected variant here
                System.out.println("Selected Variant: " + variantName + ", Price: " + variantPrice);

                selectedFoodItem.setVariant(new HashMap<String,Double>(){{put(variantName,variantPrice);}});
                selectedFoodItem.setFoodItemPrice(variantPrice);
                selectedFoodItem.setFoodItemName(selectedFoodItem.getFoodItemName()+" (" + variantName + ")");
            });
        }

        // Create a cell value factory for the Name column
        TableColumn<Menu, String> nameColB = new TableColumn<>("Name");
        nameColB.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));
        nameColB.setMinWidth(150);

        // Create a cell value factory for the Price column
        TableColumn<Menu, String> priceColB = new TableColumn<>("Price");
        priceColB.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));
        priceColB.setMinWidth(150);

        // Create a cell value factory for the Quantity column
        TableColumn<BillItems, Integer> quantColB = new TableColumn<>("Quantity");
        quantColB.setMinWidth(110);
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

        int selectedFoodItemId = selectedFoodItem.getId();
        String selectedFoodItemName = selectedFoodItem.getFoodItemName();
        Double selectedFoodItemprice = selectedFoodItem.getFoodItemPrice();

        HashMap selectedVariant = selectedFoodItem.getVariant();
        String selectedVariantName="";
        Double selectedVariantPrice;
        if(selectedVariant != null)
        {
            selectedVariantName = (String) selectedVariant.keySet().iterator().next();
            selectedVariantPrice = (Double) selectedVariant.get(selectedVariantName);
        }


        //Adding only if the Item in available in Menu
        if(selectedMenuFoodItem.getFoodItemAvailability().equals("NOT Available"))
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
                BillItems newItem = new BillItems(selectedFoodItemId,selectedFoodItemName,selectedFoodItemprice,1,selectedVariant );

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

    public void computeDiscount(KeyEvent ignoredEvent) throws SQLException {
        if(discountField.getText().isEmpty())
        {
            discountField.setText("");
            bill.setDiscount(0);
            updateTotals(billTableItems);
        }
        else
        {
            //Checking if the discount value is more than 0 and less than Max Discount Value
            Double maxDiscount = daoimpl.fetchMaxDiscount();
            if( discountField.getText().matches("[0-9]*\\.?[0-9]*") && Double.parseDouble(discountField.getText()) >= 0 && Double.parseDouble(discountField.getText()) < maxDiscount )
            {
                double newDiscount = Double.parseDouble(discountField.getText());
                bill.setDiscount(newDiscount);
                updateTotals(billTableItems);
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Discount Out of Limits", ButtonType.OK);
                alert.setHeaderText("Discount should be more than 0.0 & less than "+maxDiscount);
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

        for(BillItems item : billTableItems)
        {
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

        gstLabel.setText(Double.toString(sgst+cgst));
        serviceChargeLabel.setText(Double.toString(servicecharge));



    }

    @FXML
    public void clearBill(MouseEvent ignoredEvent)
    {
        billTableItems.clear();

        discountField.setText("");
        bill.setDiscount(0);

        updateTotals(billTableItems);
    }

    @FXML
    public void placeOrder(MouseEvent ignoredEvent) throws IOException, SQLException {
        if(billTableItems.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No Items Added. Invoice can not be generated", ButtonType.OK);
            alert.setHeaderText("Can't Generate Invoice");
            alert.setTitle("Sorry!");
            alert.showAndWait();
            return;
        }

        //Checking for Mode of payment
        if( modeOfPaymentComboBox.getValue() == null)
        {

            Alert alert = new Alert(Alert.AlertType.ERROR, "Select a valid mode of payment", ButtonType.OK);
            alert.setHeaderText("Mode of Payment not Selected");
            alert.setTitle("Alert!");
            alert.showAndWait();
            modeOfPaymentComboBox.requestFocus();
            return;
        }
        else
        {
            addBill(ignoredEvent);

            //Display Take Away Orders in Take-Away order Tile Pane
            displayTakeAwayOrders();

            billTableItems.clear(); //Clearing the bill table

            bill = new Bill(); //Generating new bill after bill is saved

            discountField.setText(""); //Setting Discount field to blank

            updateTotals(billTableItems);// Updating total labels back to 0
        }


    }

    @FXML
    private void addBill(MouseEvent ignoredEvent)
    {
        //Check if bill is not empty
        if(billTableItems.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No Items Added. Invoice can not be generated", ButtonType.OK);
            alert.setHeaderText("Can't Generate Invoice");
            alert.setTitle("Sorry!");
            alert.showAndWait();
            return;
        }

        //IF BILL TABLE IS NOT EMPTY AND MODE OF PAYMENT IS SELECTED, PROCEED TO SAVING AND SETTLING BILL

        //Setting bill details
        try
        {
            bill.setBillnumber(daoimpl.fetchNextBillNumber());
        }
        catch (Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
        }

        String modeOfPayment = modeOfPaymentComboBox.getValue();

        bill.setModeOfpayment(modeOfPayment);

        bill.setTableNumber("TAKE AWAY");

        bill.setBillItems(billTableItems);

        bill.setStatus("IN PROGRESS");

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        bill.setDate(dateFormat.format( new Date()));

        //ADD BILL Details to DB (Bill Table)
        DaoImpl daoimpl = new DaoImpl();

        try {
            int rowsUpdated = daoimpl.addBillToDB(bill);

            if (rowsUpdated > 0) {
                //Displaying Bill
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

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Order Placed Successfully", ButtonType.OK);
                alert.setHeaderText("Saved");
                alert.setTitle("Success!");
                alert.showAndWait();
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Bill Not Added", ButtonType.OK);
                alert.setHeaderText("Failed");
                alert.setTitle("Error!");
                alert.showAndWait();
                return;
            }


            //ADD order DETAILS TO DB openOrderDetails Table
            daoimpl.saveTakeAwayOrderDetails(bill.getBillnumber(), billTableItems, "In Process");

        }
        catch (Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
        }

    }

    public void displayTakeAwayOrders()
    {
        //Adding Order in Take-Away Order Window

        takeAwayOrdersTilePane.setPadding(new Insets(15, 15, 15, 15));

        Label orderNumberLabel = new Label();
        orderNumberLabel.setText("Order No. "+ bill.getBillnumber());
        orderNumberLabel.setAlignment(Pos.CENTER);
        orderNumberLabel.setId("orderNumber");
        orderNumberLabel.getStyleClass().add("common-text-font");

        Label grandTotal = new Label();
        grandTotal.setText("Grand Total : "+ bill.getGrandTotal());
        grandTotal.getStyleClass().add("common-text-font");

        Button servedButton = new Button();
        servedButton.setTooltip(new javafx.scene.control.Tooltip("Order will be Saved"));
        servedButton.setText("Order Served");
        servedButton.setMaxWidth(Integer.MAX_VALUE);
        servedButton.getStyleClass().add("billngButtons");
        servedButton.setOnAction(actionEvent -> {

            Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure ?", ButtonType.YES , ButtonType.NO);
            deleteAlert.setHeaderText("Please make sure the order is Served");
            deleteAlert.setTitle("Alert!");
            deleteAlert.showAndWait();

            if(deleteAlert.getResult() != ButtonType.YES)
                return;

            //Removing the ORDER ticket from Take Away Order Tile Pane
            BorderPane order = (BorderPane) servedButton.getParent().getParent();
            TilePane tilePane = (TilePane) order.getParent();
            tilePane.getChildren().remove(order);

            //Changing the status of order
            Label ordernumLabel = (Label)order.lookup("#orderNumber");
            Pattern pattern = Pattern.compile("\\d+"); // Matches one or more digits
            Matcher matcher = pattern.matcher(ordernumLabel.getText());
            // Check if a match is found
            if (matcher.find()) {
                // Extract and parse the matched digits as an integer
                String match = matcher.group();
                int ordernumber = Integer.parseInt(match);
                try
                {
                    daoimpl.closeTakeAwayOrder(ordernumber);
                    daoimpl.changeBillStatus(ordernumber, "SUCCESS");
                }
                catch (Exception ex)
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
                    alert.setHeaderText("Failed");
                    alert.setTitle("Error!");
                    alert.showAndWait();
                }
            }

        });

        Button cancelButton = new Button();
        cancelButton.setTooltip(new javafx.scene.control.Tooltip("Bill will be Cancelled"));
        cancelButton.setText("Cancel Order");
        cancelButton.setMaxWidth(Integer.MAX_VALUE);
        cancelButton.getStyleClass().add("billngButtons");
        cancelButton.setOnAction(actionEvent -> {

            Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure ?", ButtonType.YES , ButtonType.NO);
            deleteAlert.setHeaderText("This order will be cancelled");
            deleteAlert.setTitle("Alert!");
            deleteAlert.showAndWait();

            if(deleteAlert.getResult() != ButtonType.YES)
                return;

            BorderPane order = (BorderPane) cancelButton.getParent().getParent();
            TilePane tilePane = (TilePane) order.getParent();
            tilePane.getChildren().remove(order);

            Label ordernumLabel = (Label)order.lookup("#orderNumber");
            Pattern pattern = Pattern.compile("\\d+"); // Matches one or more digits
            Matcher matcher = pattern.matcher(ordernumLabel.getText());
            // Check if a match is found
            if (matcher.find()) {
                // Extract and parse the matched digits as an integer
                String match = matcher.group();
                int ordernumber = Integer.parseInt(match);
                try {
                    daoimpl.closeTakeAwayOrder(ordernumber);
                    daoimpl.changeBillStatus(ordernumber, "CANCELLED");
                }
                catch (Exception ex)
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
                    alert.setHeaderText("Failed");
                    alert.setTitle("Error!");
                    alert.showAndWait();
                }
            }

        });

        TextArea orderItems = new TextArea();
        orderItems.setEditable(false);
        orderItems.getStyleClass().add("common-text-font");
        String itemsList="";
        for(BillItems items:billTableItems)
        {
            itemsList += (items.getFoodItemName()).concat(" X ").concat( String.valueOf(items.getFoodItemQuantity()) ).concat("\n");
        }
        orderItems.setText(itemsList);

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.getChildren().add(orderItems);
        vbox.getChildren().add(servedButton);
        vbox.getChildren().add(cancelButton);
        vbox.getChildren().add(grandTotal);

        BorderPane order = new BorderPane();
        order.getStyleClass().add("new-order-border-pane");
        order.setPadding(new Insets(8,8,8,8));
        BorderPane.setAlignment(orderNumberLabel, javafx.geometry.Pos.CENTER);
        order.setPrefHeight(300);
        order.setPrefWidth(200);
        order.setTop(orderNumberLabel);
        order.setCenter(vbox);


        takeAwayOrdersTilePane.getChildren().add(order);
    }

    public void displayTakeAwayOrdersInitially()
    {
        try {
            openOrders = daoimpl.fetchOpenTakeAwayOrders();

            takeAwayOrdersTilePane.setPadding(new Insets(15, 15, 15, 15));


            for (Map.Entry<String, ObservableList<BillItems>> openTakeAwayOrder : openOrders.entrySet()) {
                String orderNum = openTakeAwayOrder.getKey();

                Label orderNumberLabel = new Label();
                orderNumberLabel.setText("Order No. " + orderNum);
                orderNumberLabel.setAlignment(Pos.CENTER);
                orderNumberLabel.setId("orderNumber");
                orderNumberLabel.getStyleClass().add("common-text-font");

                Bill fetchedBill = daoimpl.fetchBill(Integer.parseInt(orderNum));
                Label grandTotal = new Label();
                grandTotal.setText("Grand Total : " + fetchedBill.getGrandTotal());
                grandTotal.getStyleClass().add("common-text-font");

                Button servedButton = new Button();
                servedButton.setText("Order Served");
                servedButton.setMaxWidth(Integer.MAX_VALUE);
                servedButton.getStyleClass().add("billngButtons");
                servedButton.setOnAction(actionEvent -> {

                    Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure ?", ButtonType.YES, ButtonType.NO);
                    deleteAlert.setHeaderText("Please make sure the order is Served");
                    deleteAlert.setTitle("Alert!");
                    deleteAlert.showAndWait();

                    if (deleteAlert.getResult() != ButtonType.YES)
                        return;

                    //Removing the ORDER ticket (GUI) from Take Away Order Tile Pane
                    BorderPane order = (BorderPane) servedButton.getParent().getParent();
                    TilePane tilePane = (TilePane) order.getParent();
                    tilePane.getChildren().remove(order);

                    //Changing the status of order
                    Label ordernumLabel = (Label) order.lookup("#orderNumber");
                    Pattern pattern = Pattern.compile("\\d+"); // Matches one or more digits
                    Matcher matcher = pattern.matcher(ordernumLabel.getText());
                    // Check if a match is found
                    if (matcher.find()) {
                        // Extract and parse the matched digits as an integer
                        String match = matcher.group();
                        int ordernumber = Integer.parseInt(match);
                        try {
                            daoimpl.closeTakeAwayOrder(ordernumber);
                            daoimpl.changeBillStatus(ordernumber, "SUCCESS");
                        } catch (Exception ex) {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - " + ex.getMessage(), ButtonType.OK);
                            alert.setHeaderText("Failed");
                            alert.setTitle("Error!");
                            alert.showAndWait();
                        }
                    }

                });

                Button cancelButton = new Button();
                cancelButton.setText("Cancel Order");
                cancelButton.setMaxWidth(Integer.MAX_VALUE);
                cancelButton.getStyleClass().add("billngButtons");
                cancelButton.setOnAction(actionEvent -> {

                    Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure ?", ButtonType.YES, ButtonType.NO);
                    deleteAlert.setHeaderText("This order will be cancelled");
                    deleteAlert.setTitle("Alert!");
                    deleteAlert.showAndWait();

                    if (deleteAlert.getResult() != ButtonType.YES)
                        return;

                    BorderPane order = (BorderPane) cancelButton.getParent().getParent();
                    TilePane tilePane = (TilePane) order.getParent();
                    tilePane.getChildren().remove(order);

                    Label ordernumLabel = (Label) order.lookup("#orderNumber");
                    Pattern pattern = Pattern.compile("\\d+"); // Matches one or more digits
                    Matcher matcher = pattern.matcher(ordernumLabel.getText());
                    // Check if a match is found
                    if (matcher.find()) {
                        // Extract and parse the matched digits as an integer
                        String match = matcher.group();
                        int ordernumber = Integer.parseInt(match);
                        try {
                            daoimpl.closeTakeAwayOrder(ordernumber);
                            daoimpl.changeBillStatus(ordernumber, "CANCELLED");
                        } catch (Exception ex) {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - " + ex.getMessage(), ButtonType.OK);
                            alert.setHeaderText("Failed");
                            alert.setTitle("Error!");
                            alert.showAndWait();
                        }
                    }

                });

                ObservableList<BillItems> billitems = openTakeAwayOrder.getValue();

                TextArea orderItems = new TextArea();
                orderItems.setEditable(false);
                orderItems.getStyleClass().add("common-text-font");
                String itemsList = "";
                for (BillItems items : billitems) {
                    itemsList += (items.getFoodItemName()).concat(" X ").concat(String.valueOf(items.getFoodItemQuantity())).concat("\n");
                }
                orderItems.setText(itemsList);

                VBox vbox = new VBox();
                vbox.setSpacing(10);
                vbox.getChildren().add(orderItems);
                vbox.getChildren().add(servedButton);
                vbox.getChildren().add(cancelButton);
                vbox.getChildren().add(grandTotal);

                BorderPane order = new BorderPane();
                order.getStyleClass().add("new-order-border-pane");
                order.setPadding(new Insets(8, 8, 8, 8));
                BorderPane.setAlignment(orderNumberLabel, javafx.geometry.Pos.CENTER);
                order.setPrefHeight(300);
                order.setPrefWidth(200);
                order.setTop(orderNumberLabel);
                order.setCenter(vbox);


                takeAwayOrdersTilePane.getChildren().add(order);
            }
        }
        catch(Exception e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database Operation Exception - "+e.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
        }

    }

    public void openTableBillingPage(MouseEvent e) throws IOException
    {
        mainController.openSingleBillPageFlag=true;
        mainController.openTableBillingPage(e);
    }

}


