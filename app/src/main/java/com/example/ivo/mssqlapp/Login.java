package com.example.ivo.mssqlapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    Toolbar toolbar;
    SessionManager session;

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

        session = new SessionManager(getApplicationContext());

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editPass.getWindowToken(), 0);

                String name = editName.getText().toString();
                String pass = editPass.getText().toString();
                new AsyncLogin(name, pass).execute();
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
        if (id == R.id.action_logout) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class AsyncLogin extends AsyncTask<Void, Void, ResultSet>{
        Connection connect;
        Statement statement;
        String name, pass;
        LinearLayout linProgBar = (LinearLayout)findViewById(R.id.linProgBar);

        public AsyncLogin(String name, String pass){
            this.name = name;
            this.pass = pass;
        }

        @Override
        protected ResultSet doInBackground(Void... params) {
            ResultSet result = null;

            try{
                connect = DatabaseConnection.Connect();
                if(connect == null){
                    Log.e("SERVER_ERROR_MESSAGE", "Server not running");
                }else {
                    statement = connect.createStatement();
                    result = statement.executeQuery("SELECT Sifrarnici.Partner.Id, Korisnik.Id, Korisnik.KorisnickoIme, Korisnik.Lozinka, Korisnik.Ime, Korisnik.Prezime FROM Korisnik INNER JOIN Sifrarnici.Partner ON Korisnik.OIB=Sifrarnici.Partner.OIB WHERE Korisnik.KorisnickoIme='" + name + "' and Korisnik.Lozinka='" + pass + "'");
                }
            }catch(SQLException e){
                Log.e("SQL error", e.getMessage());
            }

            if(result != null) {
                return result;
            }else{
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            linProgBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ResultSet resultSet) {
            linProgBar.setVisibility(View.GONE);
            if(connect == null) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Login.this);
                alertDialogBuilder.setTitle("Server ne radi");
                alertDialogBuilder.setMessage("Klikni OK za izlazak iz aplikacije").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }else {
                try {
                    if (resultSet != null && resultSet.next()) {
                        session.loginUser(resultSet.getString("KorisnickoIme"), resultSet.getString("Lozinka"), resultSet.getString("Ime"), resultSet.getString("Prezime"), Integer.toString(resultSet.getInt("Id")), Integer.toString(resultSet.getInt(2)));
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        errorLbl.setText("Wrong username or password, please try again!");
                    }
                } catch (SQLException e) {
                    Log.e("SQL error", e.getMessage());
                }
            }

            super.onPostExecute(resultSet);
        }
    }
}
