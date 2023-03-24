package org.lenden;

import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import service.service;

import java.security.Provider;

public class LoginController {

    public TextField username;

    public PasswordField password;

    public void onSignIn(ActionEvent event)
    {
        String user = username.getText();
        String pass = password.getText();

        System.out.println("USERNAME ->"+user+"PASSWORD ->"+pass);
        service obj = new service();
        obj.login(user,pass);
    }
}
