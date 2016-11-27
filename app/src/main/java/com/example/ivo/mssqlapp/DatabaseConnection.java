package com.example.ivo.mssqlapp;

import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Ivo on 6.7.2015..
 */
public class DatabaseConnection {

    public static Connection Connect(){
        String ipAddress = "192.168.43.162";
        String dataBase = "PivisDB_Prazna";
        String username = "admin";
        String password = "admin123";
        String connectionURL;
        Connection connection = null;

        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionURL = "jdbc:jtds:sqlserver://" + ipAddress + "/" + dataBase + ";user=" + username + ";password=" + password + ";";
            connection = DriverManager.getConnection(connectionURL);
        }catch(SQLException se){
            Log.e("ERROR1", se.getMessage());
        }catch(ClassNotFoundException e){
            Log.e("ERROR2", e.getMessage());
        }catch(Exception e){
            Log.e("ERROR3", e.getMessage());
        }

        return connection;
    }
}
