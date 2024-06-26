package org.lenden;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import org.lenden.model.Menu;
import org.lenden.model.Variant;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TableBillingController implements Initializable {

    @FXML
    HBox categoryHbox;
    @FXML
    AnchorPane categoryAnchorPane;
    @FXML
    ScrollPane categoryScrollPane;
    ObservableList<Menu> menuTableItems;
    ObservableList<BillItems> billTableItems = FXCollections.observableArrayList();

    @FXML
    Button reserveTableButton;
    @FXML
    TableView<Menu> foodItemsTable;
    @FXML
    Label tableNumberLabel;
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
    Label tableGrandTotalLabel;
    @FXML
    ComboBox<String> modeofpayment;
    @FXML
    Button shiftTableButton;
    @FXML
    Button generateInvoiceButton;
    @FXML
    Button saveInvoiceButton;
    @FXML
    Button clearBillButton;

    @FXML
    Accordion accordion;
    Bill bill = new Bill();
    DaoImpl daoimpl = new DaoImpl();
    HashMap<String,ObservableList<BillItems>> openTables = new HashMap<>();
    ArrayList<String> reservedTables = new ArrayList<>();
    MainController mainController = new MainController();

    Stage currentStage;

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
        //Setting Tips for Different Buttons - Will be Visible on Hover
        shiftTableButton.setTooltip(new javafx.scene.control.Tooltip("Shift Current Items to another Table in same Area"));

        reserveTableButton.setTooltip(new javafx.scene.control.Tooltip("Reserve Table (Cannot be used for Billing"));

        generateInvoiceButton.setTooltip(new javafx.scene.control.Tooltip("Print Bill"));

        saveInvoiceButton.setTooltip(new javafx.scene.control.Tooltip("Save Invoice Details to System"));

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

        //--------------------------------------------------------------------------------------------------------------
        try
        {
            //Getting Default Category Items FOR MENU TABLE
            menuTableItems = daoimpl.fetchCategoryItems(categories.get(0));
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
                return new SimpleStringProperty("N/A");
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
        //Setting mode of payments & Default Payment

        try
        {
            ArrayList<String> modeofpayments = daoimpl.fetchModeOfPayment();
            modeofpayment.getItems().addAll(modeofpayments);

            String defaultmodeofpayment = daoimpl.fetchDefaultModeOfPayment();
            if(defaultmodeofpayment!=null)
            {
                modeofpayment.setValue(defaultmodeofpayment);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        //--------------------------------------------------------------------------------------------------------------
        //Setting Open Table & Reserved Table Details

        try 
        {
            openTables = daoimpl.fetchOpenTableDetails();

            reservedTables = daoimpl.fetchReservedTables();
        }
        catch(Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
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


        //Display Areas & Tables
        LinkedHashMap<String, Integer> areaAndTables = null;
        try
        {
            areaAndTables = daoimpl.fetchAreaAndTables();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+e.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
        }

        int tableNumCounter = 1; // Counter for naming tables
        for (Map.Entry<String, Integer> entry : areaAndTables.entrySet())
        {
            String areaName = entry.getKey();
            Integer tablesInArea = entry.getValue();

            // Create Title Pane
            TitledPane titledPane = new TitledPane();
            titledPane.setStyle("-fx-background-color: white;");
            titledPane.setText(areaName);

            //Creating Scroll Pane
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            scrollPane.setStyle("-fx-background-color: white;");

            // Create Anchor Pane
            AnchorPane anchorpane = new AnchorPane();
            anchorpane.setStyle("-fx-background-color: white;");
            anchorpane.setPrefSize(Region.USE_COMPUTED_SIZE,Region.USE_COMPUTED_SIZE);

            // Create TilePane
            TilePane tilePane = new TilePane();
            tilePane.setLayoutX(15);
            tilePane.setLayoutY(21);
            tilePane.setStyle("-fx-background-color: white;");
            tilePane.setHgap(5);
            tilePane.setVgap(5);
            tilePane.setPrefSize(Region.USE_COMPUTED_SIZE,Region.USE_COMPUTED_SIZE);

            AnchorPane.setLeftAnchor(tilePane,15.0);
            AnchorPane.setRightAnchor(tilePane,15.0);


            //Adding Panes (tables) in the Tile pane
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
                    tilePane.getChildren().add(table);
                    temp++;
                    tableNumCounter++;
                }
            }


            anchorpane.getChildren().add(tilePane);

            scrollPane.setContent(anchorpane);

            titledPane.setContent(scrollPane);

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

        try {
            menuTableItems = daoimpl.fetchCategoryItems(category);
        }
        catch (Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
            alert.initOwner(currentStage);
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
                return new SimpleStringProperty("N/A");
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


    /**
     * Adds food items to the bill table when user selects/clicks in the menu
     */
    public void addMenuItemtoBill()
    {

        Menu selectedMenuFoodItem = foodItemsTable.getSelectionModel().getSelectedItem();

        BillItems selectedFoodItem = new BillItems();
        selectedFoodItem.setId(selectedMenuFoodItem.getId());
        selectedFoodItem.setFoodItemName(selectedMenuFoodItem.getFoodItemName());
        selectedFoodItem.setFoodItemPrice(selectedMenuFoodItem.getFoodItemPrice());
        selectedFoodItem.setVariant(new HashMap<>());

        String tableNumber = tableNumberLabel.getText();

        //Checking if the table is Reserved or not
        if(reservedTables.contains(tableNumber))
            return;

        //Enabling the shift Table Button when first item is added
        shiftTableButton.setDisable(false);

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
            currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
            alert.initOwner(currentStage);
            alert.showAndWait();
        }

        // Variant Selection
        if (selectedMenuFoodItem.getVariantData() != null) {
            Dialog<String> variantDialog = new Dialog<>();
            variantDialog.setTitle("Variant Selection");
            variantDialog.setHeaderText("Select Variant");
            currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
            variantDialog.initOwner(currentStage);

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

                selectedFoodItem.setVariant(new HashMap<String,Double>(){{put(variantName,variantPrice);}});
                selectedFoodItem.setFoodItemPrice(variantPrice);
                selectedFoodItem.setFoodItemName(selectedFoodItem.getFoodItemName()+" (" + variantName + ")");
            });
        }


        // Create a cell value factory for the Name column
        TableColumn<BillItems, String> billTableNameCol = new TableColumn<>("Name");
        billTableNameCol.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));
        billTableNameCol.setPrefWidth(150);

        // Create a cell value factory for the Price column
        TableColumn<BillItems, String> billTablePriceCol = new TableColumn<>("Price");
        billTablePriceCol.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));
        billTablePriceCol.setPrefWidth(110);

        // Create a cell value factory for the Quantity column
        TableColumn<BillItems, Integer> billTableQuantityCol = new TableColumn<>("Quantity");
        billTableQuantityCol.setPrefWidth(80);
        billTableQuantityCol.setMinWidth(60);
        billTableQuantityCol.setCellValueFactory(new PropertyValueFactory<>("foodItemQuantity"));
        billTableQuantityCol.setCellFactory(col ->
        {
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
                        HBox hbox = new HBox(10);

                        Text txtQuantity = new Text(quantity.toString());
                        
                        Button btnMinus = new Button("-");
                        btnMinus.getStyleClass().add("minus-button");
                        btnMinus.setCursor(Cursor.HAND);
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
                                }
                                else
                                {
                                    //Deleting the removed item from the DB
                                    try
                                    {
                                        daoimpl.deleteOpenTableDetails(tableNumber, item.getFoodItemName());
                                    }
                                    catch(Exception e)
                                    {
                                        e.printStackTrace();
                                        Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+e.getMessage(), ButtonType.OK);
                                        alert.setHeaderText("Failed");
                                        alert.setTitle("Error!");
                                        currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
                                        alert.initOwner(currentStage);
                                        alert.showAndWait();
                                    }

                                    billTableItems.remove(item);
                                    billTable.setItems(billTableItems);
                                }
                            }

                            //update Grand Total
                            updateTotals(billTableItems);

                        });

                        Button btnPlus = new Button("+");
                        btnPlus.getStyleClass().add("plus-button");
                        btnPlus.setCursor(Cursor.HAND);
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


                        hbox.setAlignment(Pos.CENTER);
                        hbox.getChildren().addAll(btnMinus, txtQuantity, btnPlus);

                        setGraphic(hbox);
                        setText(null);
                    }
                }
            };
            return cell;
        });

        //----------------------------------------------------------------------------------------------------------------------
       
        HashMap selectedVariant = selectedFoodItem.getVariant();
        
        String selectedVariantName="";
        Double selectedVariantPrice;
        if(!selectedVariant.isEmpty() )
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
            currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
            alert.initOwner(currentStage);
            alert.showAndWait();
        }
        else
        {
            boolean itemFound = false;

            for (BillItems item : billTableItems) 
            {
              
                if( (item.getFoodItemName().equals(selectedFoodItem.getFoodItemName()) && ( item.getVariant().isEmpty() )) || ( (item.getVariant().size() > 0 && item.getVariant().containsKey(selectedVariantName)) )) 
                {
                    item.setFoodItemQuantity(item.getFoodItemQuantity() + 1);
                    updateTotals(billTableItems);
                    int index = billTableItems.indexOf(item);
                    billTableItems.set(index, item);
                    itemFound = true;
                    break;
                }
            }


            //IF ITEM BEING ADDED IS NEW i.e. NOT PREVIOUSLY PRESENT IN BILL TABLE
            if (!itemFound)
            {
                BillItems newItem = new BillItems(selectedFoodItem.getId(),selectedFoodItem.getFoodItemName(),selectedFoodItem.getFoodItemPrice(),1,selectedFoodItem.getVariant());

                //add the item to the bill items list
                billTableItems.add(newItem);

                //openTables.put(tableNumberLabel.getText(), billTableItems);

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
     * @param ignoredEvent key board event i.e key pressed
     */
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
            if(maxDiscount==0)
                return;

            if( discountField.getText().matches("[0-9]*\\.?[0-9]*") && Double.parseDouble(discountField.getText()) >= 0 && Double.parseDouble(discountField.getText()) < maxDiscount )
            {
                double newDiscount = Double.parseDouble(discountField.getText());
                bill.setDiscount(newDiscount);
                updateTotals(billTableItems);
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid Discount Value", ButtonType.OK);
                alert.setHeaderText("Discount should be more than/equal to 0.0 & less than "+maxDiscount);
                alert.setTitle("Attention!");
                currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
                alert.initOwner(currentStage);
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
    public void updateTotals(ObservableList<BillItems> billTableItems) {
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");

        double subTotal = 0;
        double discount = bill.getDiscount();

        if (billTableItems == null)
            billTableItems = FXCollections.observableArrayList();

        for (BillItems item : billTableItems) {
            subTotal += item.getFoodItemPrice() * item.getFoodItemQuantity();
        }
        //Setting SubTotal
        bill.setSubTotal(subTotal);

        //Displaying Subtotal
        subTotalLabel.setText(decimalFormat.format(subTotal));

        //Setting Discounted Total
        double total = subTotal - (subTotal * ((discount) / 100));
        bill.setTotal(total);
        //Displaying subtotal
        totalLabel.setText(decimalFormat.format(total));

        double cgst = bill.getCgst();
        double sgst = bill.getSgst();
        double servicecharge = bill.getServiceCharge();
        double tax = total * ((cgst + sgst + servicecharge) / 100);
        double grandTotal = total + tax;

        //Setting Grand Total in bill
        bill.setGrandTotal(grandTotal);

        //Diplaying Grandtotal
        grandTotalLabel.setText(decimalFormat.format(grandTotal));

        //Checking, If label belongs to reserved table ,then don't update
        if (tableGrandTotalLabel != null)
            tableGrandTotalLabel.setText(decimalFormat.format(grandTotal));

        //Displaying taxes
        gstLabel.setText(decimalFormat.format(sgst+cgst));
        serviceChargeLabel.setText(decimalFormat.format(servicecharge));

        try {
            //Saving open table Details
            daoimpl.saveOpenTableDetails(openTables);
        }
        catch(Exception e)
        {
            Alert updateAlert = new Alert(Alert.AlertType.WARNING, "Not able to update Totals, Check you network Connection. If this keeps occurring Contact Customer Support"+e.getMessage(), ButtonType.OK);
            updateAlert.setHeaderText("Totals Not updated");
            updateAlert.setTitle("Alert!");
            currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
            updateAlert.initOwner(currentStage);
            updateAlert.showAndWait();
        }
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
     * @param ignoredEvent Mouse Event i.e Click
     */
    @FXML
    public void clearBill(MouseEvent ignoredEvent)
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
        currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
        deleteAlert.initOwner(currentStage);
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
            currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
            closeTableFailedAlert.initOwner(currentStage);
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
        currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
        deleteAlert.initOwner(currentStage);
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
            currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
            closeTableFailedAlert.initOwner(currentStage);
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
     * @param ignoredEvent Mouse Event i.e Click
     * @throws IOException throws IOException
     */
    @FXML
    public void printBillAndKOT(MouseEvent ignoredEvent) throws IOException {
        if(billTableItems.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No Items Added. Invoice can not be generated", ButtonType.OK);
            alert.setHeaderText("Can't Generate Invoice");
            alert.setTitle("Sorry!");
            currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
            alert.initOwner(currentStage);
            alert.showAndWait();
            return;
        }

        try {
            bill.setBillnumber(daoimpl.fetchNextBillNumber());
        }
        catch (Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
            alert.initOwner(currentStage);
            alert.showAndWait();
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
     * @param ignoredEvent Mouse Event i.e Click
     */
    @FXML
    private void settleBill(MouseEvent ignoredEvent)
    {
        //Check if the bill table is empty
        if(billTableItems.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No Items Added. Invoice can not be generated", ButtonType.OK);
            alert.setHeaderText("Can't Generate Invoice");
            alert.setTitle("Alert!");
            currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
            alert.initOwner(currentStage);
            alert.showAndWait();
            return;
        }

        //Check if mode of payment in selected
        if( modeofpayment.getValue() == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Select a valid mode of payment", ButtonType.OK);
            alert.setHeaderText("Mode of Payment not Selected");
            alert.setTitle("Alert!");
            currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
            alert.initOwner(currentStage);
            alert.showAndWait();
            modeofpayment.requestFocus();
            return;
        }

        String modeOfPayment = modeofpayment.getValue();

        //IF BILL TABLE IS NOT EMPTY AND MODE OF PAYMENT IS SELECTED, PROCEED TO SAVING AND SETTLING BILL

        if(bill.getBillnumber() == 0)
        {
            try
            {
                bill.setBillnumber(daoimpl.fetchNextBillNumber());
            }
            catch (Exception ex)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
                alert.setHeaderText("Failed");
                alert.setTitle("Error!");
                currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
                alert.initOwner(currentStage);
                alert.showAndWait();
            }
        }

        bill.setBillItems(billTableItems);

        String tableNumber = tableNumberLabel.getText();
        bill.setTableNumber(tableNumber);

        bill.setModeOfpayment(modeOfPayment);

        bill.setStatus("SUCCESS");

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        bill.setDate(dateFormat.format( new Date()));

        //Add bill details to DB
        int rowsUpdated=0;
        try
        {
            rowsUpdated = daoimpl.addBillToDB(bill);
        }
        catch (Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
            alert.initOwner(currentStage);
            alert.showAndWait();
        }

        if(rowsUpdated>0)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Bill Settled & Added Successfully", ButtonType.OK);
            alert.setHeaderText("Bill Settled Successfully");
            alert.setTitle("Success!");
            currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
            alert.initOwner(currentStage);
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
                currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
                alert.initOwner(currentStage);
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

            tableNumberLabel.setText("_ : _");
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Bill Not Added", ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
            alert.initOwner(currentStage);
            alert.showAndWait();
        }
    }


    /**
     * Opens Take Away Billing Page
     * @param e Mouse Event i.e Click
     * @throws IOException  throws IOException
     */
    @FXML
    public void openTakeAwayBillingPage(MouseEvent e) throws IOException
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
            //Making ShiftTable disabled Hidden for shifting table
            shiftTableButton.setDisable(true);

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
            TableColumn<Menu, String> nameColB = new TableColumn<>("Name");
            nameColB.setCellValueFactory(new PropertyValueFactory<>("foodItemName"));
            nameColB.setPrefWidth(150);

            // Create a cell value factory for the Price column
            TableColumn<Menu, String> priceColB = new TableColumn<>("Price");
            priceColB.setCellValueFactory(new PropertyValueFactory<>("foodItemPrice"));
            priceColB.setPrefWidth(120);

            // Create a cell value factory for the Quantity column
            TableColumn<BillItems, Integer> quantColB = new TableColumn<>("Quantity");
            quantColB.setPrefWidth(80);
            quantColB.setMinWidth(60);
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
                            HBox hbox = new HBox(10);
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
                                    }
                                    else
                                    {
                                        //Deleting the removed food item from DB
                                        try {
                                            daoimpl.deleteOpenTableDetails(tableNumber, item.getFoodItemName());
                                        }
                                        catch(Exception e)
                                        {
                                            e.printStackTrace();
                                            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+e.getMessage(), ButtonType.OK);
                                            alert.setHeaderText("Failed");
                                            alert.setTitle("Error!");
                                            currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
                                            alert.initOwner(currentStage);
                                            alert.showAndWait();
                                        }

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
                            hbox.setAlignment(Pos.CENTER);
                            setGraphic(hbox);
                            setText(null);
                        }
                    }
                };
                return cell;
            });


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

            //Making shift table button Enabled for shifting table
            shiftTableButton.setDisable(false);

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

            //Making shift table button enabled for shifting table
            shiftTableButton.setDisable(true);
        }
    }


    /**
     * Reserves a Table
     * @param ignoredEvent Mouse Event i.e Click
     */
    @FXML
    public void reserveTable(MouseEvent ignoredEvent)
    {
        String tableNumber = tableNumberLabel.getText();

        try {
            // CASE - If user tries to reserve a table when no table is selected
            if (tableNumber.equals("_ : _")) {
                Alert reserveAlert = new Alert(Alert.AlertType.WARNING, "Select a Table to Reserve it.", ButtonType.OK);
                reserveAlert.setHeaderText("No Table Selected");
                reserveAlert.setTitle("Alert!");
                currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
                reserveAlert.initOwner(currentStage);
                reserveAlert.showAndWait();
            }
            //CASE - Un-Reserve Table
            else if (reservedTables.contains(tableNumber)) {
                //IF Deletion success , then do UI work
                try {
                    if (daoimpl.deleteReservedTable(tableNumber)) {
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
                catch(Exception e)
                {
                    Alert shiftTableAlert = new Alert(Alert.AlertType.ERROR, "Could not un-reserve Table" + e, ButtonType.OK);
                    shiftTableAlert.setHeaderText("Error");
                    shiftTableAlert.setTitle("Alert!");
                    currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
                    shiftTableAlert.initOwner(currentStage);
                    shiftTableAlert.showAndWait();
                }
            }
            //CASE - Reserving table
            else {
                try {
                    //If Saving success , then do UI Work
                    if (daoimpl.saveReservedTableDetails(tableNumber)) {
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
                        reserveSuccessAlert.setHeaderText(tableNumber + " Reserved");
                        reserveSuccessAlert.setTitle("Information");
                        currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
                        reserveSuccessAlert.initOwner(currentStage);
                        reserveSuccessAlert.showAndWait();
                    }
                }
                catch(Exception e)
                {
                    Alert shiftTableAlert = new Alert(Alert.AlertType.ERROR, "Could not Reserve Table" + e, ButtonType.OK);
                    shiftTableAlert.setHeaderText("Error");
                    shiftTableAlert.setTitle("Alert!");
                    currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
                    shiftTableAlert.initOwner(currentStage);
                    shiftTableAlert.showAndWait();
                }
            }
        }
        catch (Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
            alert.initOwner(currentStage);
            alert.showAndWait();
        }

    }


    public ObservableList<String> getExpandedTitledPanesTables() {
        ObservableList<String> destinationTables = FXCollections.observableArrayList();

        TitledPane expandedPane = accordion.getExpandedPane();

        if (expandedPane != null) {
            ScrollPane scrollPane = (ScrollPane) expandedPane.getContent();
            AnchorPane anchorPane = (AnchorPane) scrollPane.getContent();
            TilePane tilePane = (TilePane) anchorPane.getChildren().get(0); // Assuming the TilePane is the first child

            for (Node cellNode : tilePane.getChildren()) {
                if (cellNode instanceof Pane) {
                    Pane tablePane = (Pane) cellNode;
                    Label nameLabel = (Label) tablePane.lookup("#tableNumber");

                    if (nameLabel != null) {
                        String tableName = nameLabel.getText();
                        destinationTables.add(tableName);
                    }
                }
            }
        } else {
            // Handle the case when no pane is expanded
            Alert noExpandedPaneAlert = new Alert(Alert.AlertType.WARNING, "Select an area to shift the table from", ButtonType.OK);
            noExpandedPaneAlert.setHeaderText("No area is selected");
            noExpandedPaneAlert.setTitle("Alert!");
            currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
            noExpandedPaneAlert.initOwner(currentStage);
            noExpandedPaneAlert.showAndWait();
        }

        return destinationTables;
    }


    @FXML
    public void shiftTable(MouseEvent ignoredEvent)
    {

        //CASE : If no table is selected or bill is empty
        if(billTableItems.isEmpty())
        {
            Alert shiftTableAlert = new Alert(Alert.AlertType.WARNING, "No Items Present in bill", ButtonType.OK);
            shiftTableAlert.setHeaderText("Can not Shift Table");
            shiftTableAlert.setTitle("Alert!");
            currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
            shiftTableAlert.initOwner(currentStage);
            shiftTableAlert.showAndWait();
            return;
        }

        TextInputDialog destinationTableDialog = new TextInputDialog();
        destinationTableDialog.setTitle("Destination Table");
        destinationTableDialog.setHeaderText("Enter the Destination Table");
        destinationTableDialog.setContentText("Value:");

        //Function call to get all tables in currently expanded area
        ObservableList<String> destinationTables = getExpandedTitledPanesTables();

        ComboBox<String> destinationTableComboBox = new ComboBox<>(destinationTables);
        destinationTableComboBox.setPrefSize(50,15);
        destinationTableComboBox.setPadding(new Insets(5,5,5,5));
        destinationTableComboBox.setStyle("-fx-background-color:white");
        destinationTableComboBox.setPromptText("Select Destination Table");
        destinationTableDialog.getDialogPane().setPadding(new Insets(10,10,10,10));
        destinationTableDialog.getDialogPane().setContent(destinationTableComboBox);

        // Show the dialog and wait for a response
        destinationTableDialog.showAndWait().ifPresent(result -> {

            String destinationTableName = destinationTableComboBox.getValue();
            String sourceTableName = tableNumberLabel.getText();

            ObservableList<BillItems> billItems = openTables.get(sourceTableName);

            try {
                //Checking if the source and destination tables are different or not
                if(destinationTableName == null || destinationTableName.equals(sourceTableName))
                {
                    return;
                }

                //Checking if destination table is not and open-table or a reserved table
                if(!openTables.containsKey(destinationTableName) && !reservedTables.contains(destinationTableName))
                {
                    //Updating DB and in-memory collections
                    openTables.remove(sourceTableName);
                    daoimpl.closeTable(sourceTableName);
                    openTables.put(destinationTableName, billItems);
                    daoimpl.saveOpenTableDetails(openTables);

                    //Getting source and destination Table objects
                    Pane destTable = getTableObjectById(accordion,destinationTableName);
                    Pane sourceTable = getTableObjectById(accordion,sourceTableName);

                    //Setting respective style classes
                    destTable.getStyleClass().clear();
                    destTable.getStyleClass().add("open-table");

                    sourceTable.getStyleClass().clear();
                    sourceTable.getStyleClass().add("close-table");

                    //Setting respective grand total labels
                    Label sourceTableGrandTotalLabel = (Label) sourceTable.lookup("#tableGrandTotalLabel");
                    Label destinationTableGrandTotalLabel = (Label) destTable.lookup("#tableGrandTotalLabel");

                    destinationTableGrandTotalLabel.setText(sourceTableGrandTotalLabel.getText());
                    tableGrandTotalLabel = destinationTableGrandTotalLabel;

                    sourceTableGrandTotalLabel.setText("_ : _");

                    //Setting the table number label
                    tableNumberLabel.setText(destinationTableName);

                }
                else
                {
                    Alert shiftTableAlert = new Alert(Alert.AlertType.WARNING, "Select a Closed Table for Shifting", ButtonType.OK);
                    shiftTableAlert.setHeaderText("Invalid Destination Table Selected");
                    shiftTableAlert.setTitle("Alert!");
                    currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
                    shiftTableAlert.initOwner(currentStage);
                    shiftTableAlert.showAndWait();
                }
            }
            catch (SQLException | JsonProcessingException ex)
            {
                Alert shiftTableAlert = new Alert(Alert.AlertType.WARNING, "Table could not be Shifted, Check you network Connection. If this keeps occurring Contact Customer Support"+ex.getMessage(), ButtonType.OK);
                shiftTableAlert.setHeaderText("Could not Shift Table");
                shiftTableAlert.setTitle("Alert!");
                currentStage = (Stage) foodItemsTable.getScene().getWindow(); // For displaying alerts on top of current window.
                shiftTableAlert.initOwner(currentStage);
                shiftTableAlert.showAndWait();
            }
        });
    }


}
