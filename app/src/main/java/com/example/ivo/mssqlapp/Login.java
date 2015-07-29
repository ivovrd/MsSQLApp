package com.example.ivo.mssqlapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Ivo on 21.6.2015..
 */
public class Login extends AppCompatActivity {
    Button loginBtn;
    TextView errorLbl;
    EditText editName, editPass;
    Connection connect;
    Statement statement;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginBtn = (Button)findViewById(R.id.btnlogin);
        errorLbl = (TextView)findViewById(R.id.lblerror);
        editName = (EditText)findViewById(R.id.txtname);
        editPass = (EditText)findViewById(R.id.txtpassword);
        connect = DatabaseConnection.Connect();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    connect = DatabaseConnection.Connect();
                    statement = connect.createStatement();
                    ResultSet resultSet = statement.executeQuery("select * from Korisnik where KorisnickoIme='" + editName.getText().toString() + "' and Lozinka='" + editPass.getText().toString() + "'");

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
        //getMenuInflater().inflate(R.menu.menu_main, menu);
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
