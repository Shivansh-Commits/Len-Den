package org.lenden;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.lenden.dao.DaoImpl;
import org.lenden.model.BillItems;
import org.lenden.model.FoodItems;


import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;


public class BillingController implements Initializable
{
    @FXML
    AnchorPane navMenu;
    @FXML
    Button homeMenuButton;
    @FXML
    ImageView homeIcon;
    @FXML
    Button salesMenuButton;
    @FXML
    ImageView salesIcon;
    @FXML
    Button menuMenuButton;
    @FXML
    ImageView menuIcon;
    @FXML
    Button billingMenuButton;
    @FXML
    ImageView billingIcon;
    @FXML
    Button settingsMenuButton;
    @FXML
    ImageView settingsIcon;
    @FXML
    Button logoutMenuButton;
    @FXML
    ImageView logoutIcon;
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

        FoodItems selectedFoodItem = foodItemsTable.getSelectionModel().getSelectedItem();
        String foodItemName = selectedFoodItem.getFoodItemName();
        int foodItemprice = selectedFoodItem.getFoodItemPrice();
        String foodItemAvailability = selectedFoodItem.getFoodItemAvailability();

        BillItems billItems = new BillItems();

        if(foodItemAvailability.equals("NOT AVAILABLE"))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Selected Item Not Available", ButtonType.OK);
            alert.setHeaderText("Item Not Available");
            alert.setTitle("Sorry!");
            alert.showAndWait();
        }
        else
        {
            billItems.setFoodItemName(foodItemName);
            billItems.setFoodItemPrice(foodItemprice);
            billItems.setFoodItemQuantity("1");

            billTableItems.add(billItems);
            billTable.getColumns().setAll(nameColB, priceColB, quantColB);
            billTable.setItems(billTableItems);
        }



    }

    @FXML
    public void changeHomeIcon(MouseEvent e) throws IOException {

        //getting white logo and setting white logo
        Path logoPath = Paths.get("src","main", "resources","icons","white", "outline_home_white_24.png");
        homeIcon.setImage(new Image(logoPath.toUri().toString()));

        //getting black logo
        logoPath = Paths.get("src","main", "resources","icons","black", "home_FILL0_wght400_GRAD0_opsz48.png");
        Image black_home_icon = new Image(logoPath.toUri().toString());
        //setting black logo on mouse exit
        homeMenuButton.setOnMouseExited(event -> {
            homeIcon.setImage(black_home_icon);
        });
    }

    @FXML
    public void changeSalesIcon(MouseEvent e) throws IOException {

        //getting white logo and setting white logo
        Path logoPath = Paths.get("src","main", "resources","icons","white", "outline_sales_white_24.png");
        salesIcon.setImage(new Image(logoPath.toUri().toString()));

        //getting black logo
        logoPath = Paths.get("src","main", "resources","icons","black", "outline_sales_black_24.png");
        Image black_sales_icon = new Image(logoPath.toUri().toString());
        //setting black logo on mouse exit
        salesMenuButton.setOnMouseExited(event -> {
            salesIcon.setImage(black_sales_icon);
        });

    }

    @FXML
    public void changeMenuIcon(MouseEvent e) throws IOException {

        //getting white logo and setting white logo
        Path logoPath = Paths.get("src","main", "resources","icons","white", "outline_restaurant_menu_white_24.png");
        menuIcon.setImage(new Image(logoPath.toUri().toString()));

        //getting black logo
        logoPath = Paths.get("src","main", "resources","icons","black", "outline_restaurant_menu_black_24.png");
        Image black_menu_icon = new Image(logoPath.toUri().toString());
        //setting black logo on mouse exit
        menuMenuButton.setOnMouseExited(event -> {
            menuIcon.setImage(black_menu_icon);
        });
    }

    @FXML
    public void changeBillingIcon(MouseEvent e) throws IOException {

        //getting white logo and setting white logo
        Path logoPath = Paths.get("src","main", "resources","icons","white", "outline_currency_rupee_white_24.png");
        billingIcon.setImage(new Image(logoPath.toUri().toString()));

        //getting black logo
        logoPath = Paths.get("src","main", "resources","icons","black", "currency_rupee_FILL0_wght400_GRAD0_opsz48.png");
        Image black_currency_icon = new Image(logoPath.toUri().toString());
        //setting black logo on mouse exit
        billingMenuButton.setOnMouseExited(event -> {
            billingIcon.setImage(black_currency_icon);
        });
    }

    @FXML
    public void changeSettingsIcon(MouseEvent e) throws IOException {

        //getting white logo and setting white logo
        Path logoPath = Paths.get("src","main", "resources","icons","white", "outline_settings_white_24.png");
        settingsIcon.setImage(new Image(logoPath.toUri().toString()));

        //getting black logo
        logoPath = Paths.get("src","main", "resources","icons","black", "outline_settings_black_24.png");
        Image black_setting_icon = new Image(logoPath.toUri().toString());
        //setting black logo on mouse exit
        settingsMenuButton.setOnMouseExited(event -> {
            settingsIcon.setImage(black_setting_icon);
        });
    }

    @FXML
    public void changeLogoutIcon(MouseEvent e) throws IOException {

        //getting white logo and setting white logo
        Path logoPath = Paths.get("src","main", "resources","icons","white", "outline_logout_white_24.png");
        logoutIcon.setImage(new Image(logoPath.toUri().toString()));

        //getting black logo
        logoPath = Paths.get("src","main", "resources","icons","black", "logout_FILL0_wght400_GRAD0_opsz48.png");
        Image black_logout_icon = new Image(logoPath.toUri().toString());
        //setting black logo on mouse exit
        logoutMenuButton.setOnMouseExited(event -> {
            logoutIcon.setImage(black_logout_icon);
        });
    }



}
