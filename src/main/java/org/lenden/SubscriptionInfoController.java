package org.lenden;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.lenden.dao.DaoImpl;

import java.net.URL;
import java.util.ResourceBundle;

import static org.lenden.LoginController.getTenant;

public class SubscriptionInfoController implements Initializable {

    DaoImpl daoimpl = new DaoImpl();
    @FXML
    Label userLabel;



    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        String user = getTenant();
        userLabel.setText(userLabel.getText().replaceAll("User", user));


    }
}
