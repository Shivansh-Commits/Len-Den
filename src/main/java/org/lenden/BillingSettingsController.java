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
    TextField defaultDiscount;
    @FXML
    TextField maxDiscount;
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


    DaoImpl daoimpl = new DaoImpl();

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        try
        {
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

    }

}
