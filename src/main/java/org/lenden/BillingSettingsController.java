package org.lenden;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.net.URL;
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

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

    }

}
