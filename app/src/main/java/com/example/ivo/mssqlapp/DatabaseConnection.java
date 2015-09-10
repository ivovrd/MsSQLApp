package com.example.ivo.mssqlapp;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Ivo on 6.7.2015..
 */
public class DatabaseConnection {
    private static String ipaddress = "192.168.2.14";
    //private static String ipaddress = "94.253.224.86";
    private static String database = "PivisDB_Prazna";
    private static String username = "admin";
    private static String password = "admin123";

    @SuppressLint("NewAPI")
    public static Connection Connect(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionURL;

        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionURL = "jdbc:jtds:sqlserver://" + ipaddress + "/" + database + ";user=" + username + ";password=" + password + ";";
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
