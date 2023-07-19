package org.lenden.dao;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class Dao
{
    public Connection getConnection()
    {
        Connection conn;
        try {
            Properties props = new Properties();
            InputStream inputStream = getClass().getResourceAsStream("/config.properties");
            props.load(inputStream);

            String url = props.getProperty("DBurl");
            String user = props.getProperty("DBusername");
            String password = props.getProperty("DBpassword");

            conn = DriverManager.getConnection(url, user, password);
            return conn;
        }
        catch (SQLException e)
        {
            System.out.println("COULD NOT CONNECT TO DB");
            Alert alert = new Alert(Alert.AlertType.ERROR, "DB CONNECTION ERROR", ButtonType.OK);
            alert.setHeaderText("DB Error");
            alert.setTitle("Alert!");
            alert.showAndWait();

            e.printStackTrace();
        } catch (FileNotFoundException e)
        {
            throw new RuntimeException(e);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }
}
