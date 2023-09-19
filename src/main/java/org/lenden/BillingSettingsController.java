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
    Button saveTaxSettingsButton;


    DaoImpl daoimpl = new DaoImpl();

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        try
        {
            double defaultGst = 2*daoimpl.getTax("sgst");
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
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        fivePercentGstRadioButton.setOnAction(event -> customGstTextField.setDisable(true));

        eighteenPercentGstRadioButton.setOnAction(event -> customGstTextField.setDisable(true));

        customGstRadioButton.setOnAction(event -> customGstTextField.setDisable(false));

        saveTaxSettingsButton.setOnMouseClicked(event -> {

            int defaultGst = 5;

            if(eighteenPercentGstRadioButton.isSelected())
                defaultGst = 18;
            if(customGstRadioButton.isSelected())
                defaultGst = Integer.parseInt(customGstTextField.getText());

            try
            {
                if(defaultGst < 30)
                {
                    daoimpl.saveTax(defaultGst);
                    Alert reserveSuccessAlert = new Alert(Alert.AlertType.INFORMATION, "Default Gst Value Changed Successfully", ButtonType.OK);
                    reserveSuccessAlert.setHeaderText("GST Saved Successfully");
                    reserveSuccessAlert.setTitle("INFORMATION");
                    reserveSuccessAlert.showAndWait();
                }
                else
                {
                    Alert reserveSuccessAlert = new Alert(Alert.AlertType.WARNING, "GST Value can not be more than 30%", ButtonType.OK);
                    reserveSuccessAlert.setHeaderText("Invalid GST Value");
                    reserveSuccessAlert.setTitle("Alert");
                    reserveSuccessAlert.showAndWait();
                }
            }
            catch (SQLException e)
            {
                Alert reserveSuccessAlert = new Alert(Alert.AlertType.ERROR, "Check your Internet Connection. If this error keeps occurring, contact customer support.", ButtonType.OK);
                reserveSuccessAlert.setHeaderText("Could not save to Database");
                reserveSuccessAlert.setTitle("Error!");
                reserveSuccessAlert.showAndWait();
                throw new RuntimeException(e);
            }
        } );

    }

}
