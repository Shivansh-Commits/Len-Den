package org.lenden.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import org.postgresql.util.PSQLException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {
    private static HikariDataSource dataSource;

    public static Connection getConnection() throws SQLException
    {
        try
        {
            if(dataSource == null)
            {
                HikariConfig config = new HikariConfig();

                Properties props = new Properties();
                InputStream inputStream = ConnectionManager.class.getResourceAsStream("/config.properties");
                props.load(inputStream);

                String url = props.getProperty("DBurl");
                String user = props.getProperty("DBusername");
                String password = props.getProperty("DBpassword");

                config.setJdbcUrl(url);
                config.setUsername(user);
                config.setPassword(password);

                config.setMaximumPoolSize(10); // Set the maximum number of connections in the pool
                config.setAutoCommit(true); // Set auto-commit to true (if required)

                dataSource = new HikariDataSource(config);
            }

            return dataSource.getConnection();
        }
        catch (SQLException  | HikariPool.PoolInitializationException e)
        {
            //e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }
}
