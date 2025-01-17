package cl.bebt.staffcore.sql;


import cl.bebt.staffcore.utils.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Mysql {

    private static Connection connection;

    public static boolean isConnected() {
        return (connection != null);
    }

    public static void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void connect() throws SQLException {
        String host = Utils.getString("mysql.host");
        String port = Utils.getString("mysql.port");
        String database = Utils.getString("mysql.database");
        String username = Utils.getString("mysql.username");
        String password = Utils.getString("mysql.password");
        if (!isConnected()) {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=TRUE&useSSL=FALSE", username, password);
        }
    }
}
