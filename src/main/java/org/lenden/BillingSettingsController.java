package org.lenden;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.lenden.dao.DaoImpl;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class BillingSettingsController implements Initializable {

    @FXML
    CheckBox setDefaultDiscountCheckbox;
    @FXML
    CheckBox setMaxDiscountCheckbox;
    @FXML
    TextField defaultDiscountTextBox;
    @FXML
    TextField maxDiscountTextBox;
    @FXML
    RadioButton eighteenPercentGstRadioButton;
    @FXML
    RadioButton fivePercentGstRadioButton;
    @FXML
    RadioButton customGstRadioButton;
    @FXML
    TextField customGstTextField;
    @FXML
    RadioButton fivePercentVatRadioButton;
    @FXML
    RadioButton customVatRadioButton;
    @FXML
    TextField customVatTextField;
    @FXML
    Button saveTaxSettingsButton;
    @FXML
    Button saveDiscountSettingsButton;


    DaoImpl daoimpl = new DaoImpl();

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        try
        {
            //Initially checking saved GST values
            double defaultGst = 2*daoimpl.getTax("sgst"); // sgst + cgst = GST  (sgst = cgst)
            if(defaultGst == 5)
            {
                fivePercentGstRadioButton.setSelected(true);
            }
            else if (defaultGst == 18)
            {
                eighteenPercentGstRadioButton.setSelected(true);
            }
            else
            {
                customGstRadioButton.setSelected(true);
                customGstTextField.setText(Double.toString(defaultGst));
                customGstTextField.setDisable(false);
            }

            //Initially checking saved VAT values
            double defaultVat = daoimpl.getTax("vat");
            if(defaultVat == 5)
            {
                fivePercentVatRadioButton.setSelected(true);
            }
            else
            {
                customVatRadioButton.setSelected(true);
                customVatTextField.setText(Double.toString(defaultVat));
                customVatTextField.setDisable(false);
            }

            //Initially checking & displaying saved Default Discount
            double defaultDiscount = daoimpl.fetchDefaultDiscount();
            if(defaultDiscount > 0)
            {
                setDefaultDiscountCheckbox.setSelected(true);
                defaultDiscountTextBox.setText(String.valueOf(defaultDiscount));
            }
            else
            {
                defaultDiscountTextBox.setDisable(true);
            }

            //Initially checking & displaying saved Max Discount
            double maxDiscount = daoimpl.fetchMaxDiscount();
            if(maxDiscount > 0)
            {
                setMaxDiscountCheckbox.setSelected(true);
                maxDiscountTextBox.setText(String.valueOf(maxDiscount));
            }
            else
            {
                maxDiscountTextBox.setDisable(true);
            }

        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        fivePercentGstRadioButton.setOnAction(event -> customGstTextField.setDisable(true));
        eighteenPercentGstRadioButton.setOnAction(event -> customGstTextField.setDisable(true));
        customGstRadioButton.setOnAction(event -> customGstTextField.setDisable(false));


        fivePercentVatRadioButton.setOnAction(event -> customVatTextField.setDisable(true));
        customVatRadioButton.setOnAction(event -> customVatTextField.setDisable(false));


        setDefaultDiscountCheckbox.setOnAction(event -> defaultDiscountTextBox.setDisable(false));
        setMaxDiscountCheckbox.setOnAction((event -> maxDiscountTextBox.setDisable(false)));

        saveTaxSettingsButton.setOnMouseClicked(event -> {

            try
            {
                //FOR GST
                double defaultGst = 5;
                if(eighteenPercentGstRadioButton.isSelected())
                    defaultGst = 18;
                if(customGstRadioButton.isSelected())
                    defaultGst = Double.parseDouble(customGstTextField.getText());

                if(defaultGst < 30 && defaultGst >0) // Max GST amount Limit < 30
                {
                    daoimpl.saveTax("gst",defaultGst);

                }
                else
                {
                    Alert reserveSuccessAlert = new Alert(Alert.AlertType.WARNING, "GST Value should be between 30% - 0%", ButtonType.OK);
                    reserveSuccessAlert.setHeaderText("Value out of Range");
                    reserveSuccessAlert.setTitle("Alert");
                    reserveSuccessAlert.showAndWait();
                    return;
                }

                //FOR VAT
                double defaultVat = 5;
                if(customVatRadioButton.isSelected())
                    defaultVat = Double.parseDouble(customVatTextField.getText());

                if(defaultVat < 30 && defaultVat > 0) // Max VAT amount Limit < 30
                {
                    daoimpl.saveTax("vat",defaultVat);
                }
                else
                {
                    Alert reserveSuccessAlert = new Alert(Alert.AlertType.WARNING, "VAT Value should be between 30% - 0%", ButtonType.OK);
                    reserveSuccessAlert.setHeaderText("Value out of Range");
                    reserveSuccessAlert.setTitle("Alert");
                    reserveSuccessAlert.showAndWait();
                    return;
                }
            }
            catch (SQLException e)
            {
                Alert reserveSuccessAlert = new Alert(Alert.AlertType.ERROR, "Check your Internet Connection. If this error keeps occurring, contact customer support."+e.getMessage(), ButtonType.OK);
                reserveSuccessAlert.setHeaderText("Could not save to Database");
                reserveSuccessAlert.setTitle("Error!");
                reserveSuccessAlert.showAndWait();
                throw new RuntimeException(e);
            }
            catch (NumberFormatException e)
            {
                Alert reserveSuccessAlert = new Alert(Alert.AlertType.WARNING, "Only Numeric Values are allowed in the Text box", ButtonType.OK);
                reserveSuccessAlert.setHeaderText("Invalid Values");
                reserveSuccessAlert.setTitle("Error!");
                reserveSuccessAlert.showAndWait();
                return;
            }

            Alert reserveSuccessAlert = new Alert(Alert.AlertType.INFORMATION, "Tax Values Changed Successfully", ButtonType.OK);
            reserveSuccessAlert.setHeaderText("Tax Saved Successfully");
            reserveSuccessAlert.setTitle("INFORMATION");
            reserveSuccessAlert.showAndWait();

        });

        saveDiscountSettingsButton.setOnMouseClicked(event -> {

            try{

                double defaultDiscount = 0.0;
                double maxDiscount = 0.0;

                if(setDefaultDiscountCheckbox.isSelected())
                    defaultDiscount = Double.parseDouble(defaultDiscountTextBox.getText());
                if(setMaxDiscountCheckbox.isSelected())
                    maxDiscount = Double.parseDouble(maxDiscountTextBox.getText());


                if(!setMaxDiscountCheckbox.isSelected() && !setDefaultDiscountCheckbox.isSelected())
                {
                    daoimpl.updateDefaultDiscount(defaultDiscount);
                    daoimpl.updateMaxDiscount(maxDiscount);
                }
                else if(!setMaxDiscountCheckbox.isSelected() && setDefaultDiscountCheckbox.isSelected())
                {
                    daoimpl.updateDefaultDiscount(defaultDiscount);
                    daoimpl.updateMaxDiscount(maxDiscount);
                }
                else if(setMaxDiscountCheckbox.isSelected() && !setDefaultDiscountCheckbox.isSelected() )
                {
                    daoimpl.updateMaxDiscount(maxDiscount);
                    daoimpl.updateDefaultDiscount(defaultDiscount);
                }
                else if(setMaxDiscountCheckbox.isSelected() && setDefaultDiscountCheckbox.isSelected() && (defaultDiscount <= maxDiscount))
                {
                    defaultDiscount = Double.parseDouble(defaultDiscountTextBox.getText());
                    maxDiscount = Double.parseDouble(maxDiscountTextBox.getText());

                    daoimpl.updateDefaultDiscount(defaultDiscount);
                    daoimpl.updateMaxDiscount(maxDiscount);
                }
                else if(setMaxDiscountCheckbox.isSelected() && setDefaultDiscountCheckbox.isSelected() && (defaultDiscount > maxDiscount))
                {
                        Alert reserveSuccessAlert = new Alert(Alert.AlertType.WARNING, "Default discount should be less than Max Discount Value", ButtonType.OK);
                        reserveSuccessAlert.setHeaderText("Default Discount > Max Discount");
                        reserveSuccessAlert.setTitle("Alert");
                        reserveSuccessAlert.showAndWait();
                        return;
                }

                defaultDiscountTextBox.setText(String.valueOf(defaultDiscount));
                maxDiscountTextBox.setText(String.valueOf(maxDiscount));
            }
            catch (SQLException e)
            {
                Alert reserveSuccessAlert = new Alert(Alert.AlertType.ERROR, "Check your Internet Connection. If this error keeps occurring, contact customer support."+e.getMessage(), ButtonType.OK);
                reserveSuccessAlert.setHeaderText("Could not save to Database");
                reserveSuccessAlert.setTitle("Error!");
                reserveSuccessAlert.showAndWait();
                throw new RuntimeException(e);
            }
            catch (NumberFormatException e)
            {
                Alert reserveSuccessAlert = new Alert(Alert.AlertType.WARNING, "Only Numeric Values are allowed in the Text box", ButtonType.OK);
                reserveSuccessAlert.setHeaderText("Invalid Values");
                reserveSuccessAlert.setTitle("Error!");
                reserveSuccessAlert.showAndWait();
                e.printStackTrace();
                return;
            }

            Alert reserveSuccessAlert = new Alert(Alert.AlertType.INFORMATION, "Discount Values Saved Successfully", ButtonType.OK);
            reserveSuccessAlert.setHeaderText("Discount Saved Successfully");
            reserveSuccessAlert.setTitle("INFORMATION");
            reserveSuccessAlert.showAndWait();

        });


    }

}
