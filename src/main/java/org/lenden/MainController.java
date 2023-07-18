package org.lenden;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
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
    @FXML
    AnchorPane home = null;

    Boolean openHomePageFlag=true;
    Boolean openSingleBillPageFlag=true;
    Boolean openTableBillPageFlag=true;
    Boolean openSalesPageFlag=true;
    Boolean openMenuPageFlag=true;
    Boolean openSettingsPageFlag=true;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        try {
            home = FXMLLoader.load(getClass().getResource("home.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mainPane.setCenter(home);
    }
    @FXML
    public void openHomePage(MouseEvent e) throws IOException
    {
        openTableBillPageFlag=true;
        openSingleBillPageFlag=true;
        openSalesPageFlag=true;
        openMenuPageFlag=true;
        openSettingsPageFlag=true;
        if(openHomePageFlag)
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
            AnchorPane home = loader.load();
            mainPane.setCenter(home);

            openHomePageFlag=false;
        }

    }
    @FXML
    public void openSingleBillingPage(MouseEvent e) throws IOException
    {
        openHomePageFlag=true;
        openSalesPageFlag=true;
        openMenuPageFlag=true;
        openSettingsPageFlag=true;
        openTableBillPageFlag=true;

        if(openSingleBillPageFlag == true)
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("single_billing.fxml"));
            AnchorPane Singlebilling = loader.load();
            mainPane.setCenter(Singlebilling);

            Scene scene = homeMenuButton.getScene();
            scene.getStylesheets().add("billingStyleSheet.css");

            SingleBillingController singleBillingController = loader.getController();
            singleBillingController.setMainController(this);

            openSingleBillPageFlag=false;
        }

    }
    @FXML
    public void openTableBillingPage(MouseEvent e) throws IOException
    {
        openHomePageFlag=true;
        openSalesPageFlag=true;
        openMenuPageFlag=true;
        openSettingsPageFlag=true;
        openSingleBillPageFlag=true;

        if(openTableBillPageFlag == true)
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("table_billing.fxml"));
            AnchorPane Tablebilling = loader.load();

            Scene scene = homeMenuButton.getScene();
            scene.getStylesheets().add("billingStyleSheet.css");

            mainPane.setCenter(Tablebilling);

            TableBillingController tableBillingController = loader.getController();
            tableBillingController.setMainController(this);

            openTableBillPageFlag=false;
        }

    }
    public void openMenuPage(MouseEvent e) throws IOException
    {
        openTableBillPageFlag=true;
        openSingleBillPageFlag=true;
        openHomePageFlag=true;
        openSalesPageFlag=true;
        openSettingsPageFlag=true;
        if(openMenuPageFlag)
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
            AnchorPane home = loader.load();

            Scene scene = homeMenuButton.getScene();
            scene.getStylesheets().add("menuStyleSheet.css");

            mainPane.setCenter(home);
            openMenuPageFlag=false;
        }
    }
    @FXML
    public void openSettingsPage(MouseEvent e) throws IOException
    {
        openTableBillPageFlag=true;
        openSingleBillPageFlag=true;
        openHomePageFlag=true;
        openSalesPageFlag=true;
        openMenuPageFlag=true;
        if(openSettingsPageFlag)
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("settings.fxml"));
            AnchorPane home = loader.load();

            Scene scene = homeMenuButton.getScene();
            scene.getStylesheets().add("settingsStyleSheet.css");

            mainPane.setCenter(home);
            openSettingsPageFlag=false;
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
        Image white_logo = new Image(getClass().getResource("/images/white/outline_home_white_24.png").toExternalForm());
        homeIcon.setImage(white_logo);

        //getting black logo
        Image black_logo = new Image(getClass().getResource("/images/black/home_FILL0_wght400_GRAD0_opsz48.png").toExternalForm());
        //setting black logo on mouse exit
        homeMenuButton.setOnMouseExited(event -> {
            homeIcon.setImage(black_logo);
        });
    }
    @FXML
    public void changeSalesIcon(MouseEvent e)
    {
        //getting white logo and setting white logo
        Image white_logo = new Image(getClass().getResource("/images/white/outline_sales_white_24.png").toExternalForm());
        salesIcon.setImage(white_logo);

        //getting black logo
        Image black_logo = new Image(getClass().getResource("/images/black/outline_sales_black_24.png").toExternalForm());
        //setting black logo on mouse exit
        salesMenuButton.setOnMouseExited(event -> {
            salesIcon.setImage(black_logo);
        });
    }
    @FXML
    public void changeMenuIcon(MouseEvent e)
    {
        //getting white logo and setting white logo
        Image white_logo = new Image(getClass().getResource("/images/white/outline_restaurant_menu_white_24.png").toExternalForm());
        menuIcon.setImage(white_logo);

        //getting black logo
        Image black_logo = new Image(getClass().getResource("/images/black/outline_restaurant_menu_black_24.png").toExternalForm());
        //setting black logo on mouse exit
        menuMenuButton.setOnMouseExited(event -> {
            menuIcon.setImage(black_logo);
        });
    }
    @FXML
    public void changeBillingIcon(MouseEvent e)
    {
        //getting white logo and setting white logo
        Image white_logo = new Image(getClass().getResource("/images/white/outline_currency_rupee_white_24.png").toExternalForm());
        billingIcon.setImage(white_logo);

        //getting black logo
        Image black_logo = new Image(getClass().getResource("/images/black/currency_rupee_FILL0_wght400_GRAD0_opsz48.png").toExternalForm());
        //setting black logo on mouse exit
        billingMenuButton.setOnMouseExited(event -> {
            billingIcon.setImage(black_logo);
        });
    }
    @FXML
    public void changeSettingsIcon(MouseEvent e)
    {

        //getting white logo and setting white logo
        Image white_logo = new Image(getClass().getResource("/images/white/outline_settings_white_24.png").toExternalForm());
        settingsIcon.setImage(white_logo);

        //getting black logo
        Image black_logo = new Image(getClass().getResource("/images/black/outline_settings_black_24.png").toExternalForm());
        //setting black logo on mouse exit
        settingsMenuButton.setOnMouseExited(event -> {
            settingsIcon.setImage(black_logo);
        });
    }
    @FXML
    public void changeLogoutIcon(MouseEvent e)
    {

        //getting white logo and setting white logo
        Image white_logo = new Image(getClass().getResource("/images/white/outline_logout_white_24.png").toExternalForm());
        logoutIcon.setImage(white_logo);

        //getting black logo
        Image black_logo = new Image(getClass().getResource("/images/black/logout_FILL0_wght400_GRAD0_opsz48.png").toExternalForm());
        //setting black logo on mouse exit
        logoutMenuButton.setOnMouseExited(event -> {
            logoutIcon.setImage(black_logo);
        });
    }



}
