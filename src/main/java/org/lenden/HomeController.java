package org.lenden;


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
import org.lenden.model.MenuItems;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.ResourceBundle;


public class HomeController implements Initializable
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
    TableView<MenuItems> menuItemsTable;
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

    ObservableList<MenuItems> items;

    DaoImpl daoimpl = new DaoImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        items = daoimpl.getCategoryItems("Bevereges");

        TableColumn<MenuItems, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("foodItem"));

        // Create a cell value factory for the Price column
        TableColumn<MenuItems, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Create a cell value factory for the Availability column
        TableColumn<MenuItems, Boolean> availCol = new TableColumn<>("Availability");
        availCol.setCellValueFactory(new PropertyValueFactory<>("availability"));

        // Set the cell value factories for the table columns
        menuItemsTable.getColumns().setAll(nameCol, priceCol, availCol);

        menuItemsTable.setItems(items);

    }

    @FXML
    public void displayMenuItems(MouseEvent e)
    {
        Button clickedButton = (Button) e.getSource();
        String category = clickedButton.getText();

        items = daoimpl.getCategoryItems(category);

        TableColumn<MenuItems, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("foodItem"));

        // Create a cell value factory for the Price column
        TableColumn<MenuItems, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Create a cell value factory for the Availability column
        TableColumn<MenuItems, Boolean> availCol = new TableColumn<>("Availability");
        availCol.setCellValueFactory(new PropertyValueFactory<>("availability"));

        // Set the cell value factories for the table columns
        menuItemsTable.getColumns().setAll(nameCol, priceCol, availCol);

        menuItemsTable.setItems(items);


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
