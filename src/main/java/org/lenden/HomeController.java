package org.lenden;


import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
    Button currencyMenuButton;
    @FXML
    ImageView currencyIcon;
    @FXML
    Button settingsMenuButton;
    @FXML
    ImageView settingsIcon;
    @FXML
    Button logoutMenuButton;
    @FXML
    ImageView logoutIcon;

    @FXML
    ObservableList<MenuItems> items;

    @FXML
    ListView<MenuItems> menuItemsList;


    DaoImpl daoimpl = new DaoImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        items = daoimpl.getMenuItems("Main Course");
        menuItemsList.setItems(items);

    }

    @FXML
    public void changeHomeIcon(MouseEvent e) throws IOException {
        Properties props = new Properties();
        InputStream inputStream = new FileInputStream("C:\\Users\\shiva\\IdeaProjects\\LenDen\\src\\main\\java\\org\\lenden\\config.properties");
        props.load(inputStream);

        String white_home_icon = props.getProperty("white-home-icon");
        String black_home_icon = props.getProperty("black-home-icon");

        homeIcon.setImage(new Image(white_home_icon));

        homeMenuButton.setOnMouseExited(event -> {
            homeIcon.setImage(new Image(black_home_icon));
        });
    }

    @FXML
    public void changeSalesIcon(MouseEvent e) throws IOException {
        Properties props = new Properties();
        InputStream inputStream = new FileInputStream("C:\\Users\\shiva\\IdeaProjects\\LenDen\\src\\main\\java\\org\\lenden\\config.properties");
        props.load(inputStream);

        String white_sales_icon = props.getProperty("white-sales-icon");
        String black_sales_icon = props.getProperty("black-sales-icon");

        salesIcon.setImage(new Image(white_sales_icon));

        salesMenuButton.setOnMouseExited(event -> {
            salesIcon.setImage(new Image(black_sales_icon));
        });
    }

    @FXML
    public void changeMenuIcon(MouseEvent e) throws IOException {
        Properties props = new Properties();
        InputStream inputStream = new FileInputStream("C:\\Users\\shiva\\IdeaProjects\\LenDen\\src\\main\\java\\org\\lenden\\config.properties");
        props.load(inputStream);

        String white_menu_icon = props.getProperty("white-menu-icon");
        String black_menu_icon = props.getProperty("black-menu-icon");

        menuIcon.setImage(new Image(white_menu_icon));

        menuMenuButton.setOnMouseExited(event -> {
            menuIcon.setImage(new Image(black_menu_icon));
        });
    }

    @FXML
    public void changeCurrencyIcon(MouseEvent e) throws IOException {
        Properties props = new Properties();
        InputStream inputStream = new FileInputStream("C:\\Users\\shiva\\IdeaProjects\\LenDen\\src\\main\\java\\org\\lenden\\config.properties");
        props.load(inputStream);

        String white_currency_icon = props.getProperty("white-currency-icon");
        String black_currency_icon = props.getProperty("black-currency-icon");

        currencyIcon.setImage(new Image(white_currency_icon));

        currencyMenuButton.setOnMouseExited(event -> {
            currencyIcon.setImage(new Image(black_currency_icon));
        });
    }

    @FXML
    public void changeSettingsIcon(MouseEvent e) throws IOException {
        Properties props = new Properties();
        InputStream inputStream = new FileInputStream("C:\\Users\\shiva\\IdeaProjects\\LenDen\\src\\main\\java\\org\\lenden\\config.properties");
        props.load(inputStream);

        String white_settings_icon = props.getProperty("white-settings-icon");
        String black_settings_icon = props.getProperty("black-settings-icon");

        settingsIcon.setImage(new Image(white_settings_icon));

        settingsMenuButton.setOnMouseExited(event -> {
            settingsIcon.setImage(new Image(black_settings_icon));
        });
    }

    @FXML
    public void changeLogoutIcon(MouseEvent e) throws IOException {
        Properties props = new Properties();
        InputStream inputStream = new FileInputStream("C:\\Users\\shiva\\IdeaProjects\\LenDen\\src\\main\\java\\org\\lenden\\config.properties");
        props.load(inputStream);

        String white_logout_icon = props.getProperty("white-logout-icon");
        String black_logout_icon = props.getProperty("black-logout-icon");

        logoutIcon.setImage(new Image(white_logout_icon));

        logoutMenuButton.setOnMouseExited(event -> {
            logoutIcon.setImage(new Image(black_logout_icon));
        });
    }



}
