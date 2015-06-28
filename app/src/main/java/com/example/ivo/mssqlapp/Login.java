package com.example.ivo.mssqlapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Ivo on 21.6.2015..
 */
public class Login extends Activity {
    Button loginBtn;
    TextView errorLbl;
    EditText editName, editPass;
    Connection connect;
    PreparedStatement preparedStatement;
    Statement statement;
    String ipaddress, db, username, password;

    @SuppressLint("NewAPI")
    private Connection ConnectionHelper(String user, String password, String database, String server){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionURL = null;

        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionURL = "jdbc:jtds:sqlserver://" + server + database + ";user=" + user + ";password=" + password + ";";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        loginBtn = (Button)findViewById(R.id.btnlogin);
        errorLbl = (TextView)findViewById(R.id.lblerror);
        editName = (EditText)findViewById(R.id.txtname);
        editPass = (EditText)findViewById(R.id.txtpassword);
        ipaddress = "192.168.2.14:1433/";
        db = "MyDatabase";
        username = "admin";
        password = "admin123";
        connect = ConnectionHelper(username, password,db, ipaddress);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    connect = ConnectionHelper(username, password, db, ipaddress);
                    statement = connect.createStatement();
                    ResultSet resultSet = statement.executeQuery("select * from login where userid='" + editName.getText().toString() + "' and password='" + editPass.getText().toString() + "'");

                    if(resultSet != null && resultSet.next()){
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                    }else{
                        errorLbl.setText("Sorry, wrong credidentials!");
                    }
                }catch(SQLException e){
                    errorLbl.setText(e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
