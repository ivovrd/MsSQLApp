package com.example.ivo.mssqlapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Ivo on 21.6.2015..
 */
public class Login extends AppCompatActivity {
    private EditText editName, editPass;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        Button loginBtn = (Button)findViewById(R.id.btnlogin);
        editName = (EditText)findViewById(R.id.txtname);
        editPass = (EditText)findViewById(R.id.txtpassword);
        session = new SessionManager(getApplicationContext());
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                editName.setError(null);
                editPass.setError(null);

                if(TextUtils.isEmpty(editName.getText().toString())){
                    editName.setError("Nije upisano korisničko ime!");
                }
                else if(TextUtils.isEmpty(editPass.getText().toString())){
                    editPass.setError("Nije upisana lozinka!");
                }else {
                    String name = editName.getText().toString();
                    String pass = editPass.getText().toString();
                    new AsyncLogin(name, pass).execute();
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
        if (id == R.id.action_logout) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class AsyncLogin extends AsyncTask<Void, Void, ResultSet>{
        private String name, pass;
        private Connection connect;
        private ProgressDialog progressDialog;

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
                    Statement statement = connect.createStatement();
                    result = statement.executeQuery("SELECT Sifrarnici.Partner.Id, Korisnik.Id, Korisnik.KorisnickoIme, Korisnik.Lozinka, Korisnik.Ime, Korisnik.Prezime, Korisnik.Email FROM Korisnik INNER JOIN Sifrarnici.Partner ON Korisnik.OIB=Sifrarnici.Partner.OIB WHERE Korisnik.KorisnickoIme='" + name + "' and Korisnik.Lozinka='" + pass + "'");
                }
            }catch(SQLException e){
                Log.e("SQL error", e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(Login.this, "", "Prijavljivanje...");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ResultSet resultSet) {
            progressDialog.dismiss();
            if(connect == null) {
                showServerInactiveDialog();
            }else {
                try {
                    if (resultSet != null && resultSet.next()) {
                        session.loginUser(resultSet.getString("KorisnickoIme"), resultSet.getString("Lozinka"), resultSet.getString("Ime"), resultSet.getString("Prezime"), resultSet.getString("Email"), Integer.toString(resultSet.getInt("Id")), Integer.toString(resultSet.getInt(2)));
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        showWrongPasswordDialog();
                    }
                } catch (SQLException e) {
                    Log.e("SQL error", e.getMessage());
                }
            }
            super.onPostExecute(resultSet);
        }
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editPass.getWindowToken(), 0);
    }

    private void showServerInactiveDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Login.this);
        alertDialogBuilder.setTitle("Greška u povezivanju");
        alertDialogBuilder.setMessage("Server nije aktivan, pritisnite OK za izlazak iz aplikacije").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showWrongPasswordDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Login.this);
        alertDialogBuilder.setTitle("Greška pri unosu podataka");
        alertDialogBuilder.setMessage("Pogrešno korisničko ime ili lozinka, pokušajte ponovo").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
