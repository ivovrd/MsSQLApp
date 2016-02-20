package com.example.ivo.mssqlapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.app.AlertDialog;
import android.util.Log;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 * Created by Ivo on 30.7.2015..
 */
public class SessionManager {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context mContext;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "UserLoginData";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_FIRST_NAME = "name";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_EMAIL = "eMail";
    public static final String KEY_PARTNER_ID = "partnerId";
    public static final String KEY_USER_ID = "userId";

    public SessionManager(Context context){
        this.mContext = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void loginUser(String userName, String password, String name, String lastName, String eMail, String partnerId, String userId){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_FIRST_NAME, name);
        editor.putString(KEY_LAST_NAME, lastName);
        editor.putString(KEY_EMAIL, eMail);
        editor.putString(KEY_PARTNER_ID, partnerId);
        editor.putString(KEY_USER_ID, userId);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_USER_NAME, sharedPreferences.getString(KEY_USER_NAME, null));
        user.put(KEY_PASSWORD, sharedPreferences.getString(KEY_PASSWORD, null));
        user.put(KEY_FIRST_NAME, sharedPreferences.getString(KEY_FIRST_NAME, null));
        user.put(KEY_LAST_NAME, sharedPreferences.getString(KEY_LAST_NAME, null));
        user.put(KEY_EMAIL, sharedPreferences.getString(KEY_EMAIL, null));
        user.put(KEY_PARTNER_ID, sharedPreferences.getString(KEY_PARTNER_ID, null));
        user.put(KEY_USER_ID, sharedPreferences.getString(KEY_USER_ID, null));
        return user;
    }

    public void checkLogin(Activity mActivity){
        if(!this.isLoggedIn()){
            Intent i = new Intent(mContext, Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
            mActivity.finish();
        } else {
            new CheckUserLogin(sharedPreferences.getString(KEY_USER_NAME, null), sharedPreferences.getString(KEY_PASSWORD, null), mActivity).execute();
        }
    }

    public void logoutUser(Activity mActivity){
        editor.clear();
        editor.commit();

        Intent i = new Intent(mContext, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
        mActivity.finish();
    }

    public boolean isLoggedIn(){
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }

    public String getUserId(){
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public String getPartnerId(){
        return sharedPreferences.getString(KEY_PARTNER_ID, null);
    }

    public class CheckUserLogin extends AsyncTask<Void, Void, ResultSet> {
        Connection connect;
        Statement statement;
        String name, pass;
        Activity mActivity;

        public CheckUserLogin(String name, String pass, Activity mActivity){
            this.name = name;
            this.pass = pass;
            this.mActivity = mActivity;
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
                    result = statement.executeQuery("SELECT Sifrarnici.Partner.Id, Korisnik.Id, Korisnik.KorisnickoIme, Korisnik.Lozinka, Korisnik.Ime, Korisnik.Prezime FROM Korisnik INNER JOIN Sifrarnici.Partner ON Korisnik.OIB=Sifrarnici.Partner.OIB WHERE Korisnik.KorisnickoIme='" + this.name + "'");
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
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ResultSet resultSet) {
            if(connect == null) {
                showServerInactiveDialog();
            }else {
                try {
                    if (resultSet != null && resultSet.next()) {
                        String temp = resultSet.getString("Lozinka");
                        if (!temp.equals(this.pass)) {
                            showWrongPasswordDialog();
                        }
                    }
                } catch (SQLException e) {
                    Log.e("SQL error", e.getMessage());
                }
            }
            super.onPostExecute(resultSet);
        }

        private void showServerInactiveDialog(){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
            alertDialogBuilder.setTitle("Greška u povezivanju");
            alertDialogBuilder.setMessage("Server nije aktivan, pritisnite OK za izlazak iz aplikacije").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mActivity.finish();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        private void showWrongPasswordDialog(){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
            alertDialogBuilder.setTitle("Pogrešna lozinka");
            alertDialogBuilder.setMessage("Klikni OK za ponovni unos").
                    setCancelable(false).setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            logoutUser(mActivity);
                        }
                    }).setNegativeButton("Izlaz", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mActivity.finish();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
}
