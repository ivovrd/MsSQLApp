package com.example.ivo.mssqlapp;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Created by Ivo on 4.7.2015..
 */
public class AsyncDbConnection extends AsyncTask<Void, Void, ResultSet> {
    Connection connect;
    Statement statement;
    ContactAdapter mAdapter;
    private List<Contact> contacts;
    int start, end;

    public AsyncDbConnection(ContactAdapter adapter, List<Contact> contacts){
        this.mAdapter = adapter;
        this.contacts = contacts;
    }

    @Override
    protected void onPreExecute() {
        start = contacts.size();
        end = start + 9;
        super.onPreExecute();
    }

    @Override
    protected ResultSet doInBackground(Void... params) {
        ResultSet result = null;

        try{
            connect = DatabaseConnection.Connect();
            statement = connect.createStatement();
            result = statement.executeQuery("select Sifra, Naziv, Opis from Sifrarnici.Artikl where ID between " + String.valueOf(start) + " and " + String.valueOf(end));
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
    protected void onPostExecute(ResultSet result) {
        contacts.remove(contacts.size() - 1);
        mAdapter.notifyItemRemoved(contacts.size());

        try {
            while(result.next()){
                ContactManager.getInstance().setContacts(result.getString("Sifra"), result.getString("Naziv"), result.getString("Opis"));
                mAdapter.notifyDataSetChanged();
            }
        } catch (SQLException e) {
            Log.e("SQL error", e.getMessage());
        }
        mAdapter.setLoaded();
        super.onPostExecute(result);
    }
}
