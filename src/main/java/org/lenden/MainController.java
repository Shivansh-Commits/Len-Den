package org.lenden;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class MainController implements Initializable {

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
    BorderPane mainPane;

    Boolean openHomePageFlag=true;
    Boolean openBillPageFlag=true;
    Boolean openSalesPageFlag=true;
    Boolean openMenuPageFlag=true;
    Boolean openSettingsPageFlag=true;



    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        AnchorPane home = null;
        try {
            home = FXMLLoader.load(getClass().getResource("home.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mainPane.setCenter(home);
    }

    @FXML
    public void openBillingPage(MouseEvent e) throws IOException
    {
        openHomePageFlag=true;
        openSalesPageFlag=true;
        openMenuPageFlag=true;
        openSettingsPageFlag=true;
        if(openBillPageFlag == true)
        {
            AnchorPane billing = FXMLLoader.load(getClass().getResource("billing.fxml"));
            mainPane.setCenter(billing);

            openBillPageFlag=false;
        }

    }
    @FXML
    public void openHomePage(MouseEvent e) throws IOException
    {
        openBillPageFlag=true;
        openSalesPageFlag=true;
        openMenuPageFlag=true;
        openSettingsPageFlag=true;
        if(openHomePageFlag == true)
        {
            AnchorPane home = FXMLLoader.load(getClass().getResource("home.fxml"));
            mainPane.setCenter(home);

            openHomePageFlag=false;
        }

    }

    @FXML
    public void logout(MouseEvent e)
    {
        final Node source = (Node) e.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }



    @FXML
    public void changeHomeIcon(MouseEvent e)
    {

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
    public void changeSalesIcon(MouseEvent e)
    {

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
    public void changeMenuIcon(MouseEvent e)
    {

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
    public void changeBillingIcon(MouseEvent e)
    {

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
    public void changeSettingsIcon(MouseEvent e)
    {

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
    public void changeLogoutIcon(MouseEvent e)
    {
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
