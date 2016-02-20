package com.example.ivo.mssqlapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Ivo on 6.7.2015..
 */
public class DatabaseConnection {

    @SuppressLint("NewAPI")
    public static Connection Connect(){
        String ipAddress = "192.168.2.16";
        //String ipaddress = "109.60.24.116";
        //String ipaddress = "192.168.43.162";
        String dataBase = "PivisDB_Prazna";
        String username = "admin";
        String password = "admin123";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionURL;

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
