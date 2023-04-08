package org.lenden.dao;

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
            InputStream inputStream = new FileInputStream("C:\\Users\\shiva\\IdeaProjects\\LenDen\\src\\main\\java\\org\\lenden\\config.properties");
            props.load(inputStream);

            String url = props.getProperty("DBurl");
            String user = props.getProperty("DBusername");
            String password = props.getProperty("DBpassword");

            conn = DriverManager.getConnection(url, user, password);
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
