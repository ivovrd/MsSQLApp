package com.example.ivo.mssqlapp;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Ivo on 28.8.2015..
 */
public class AsyncSavingDocument extends AsyncTask<Void, Void, Void> {
    Connection connect;
    Statement statement;
    DocumentData newDocument;
    View view;
    ResultSet resultSet;

    public AsyncSavingDocument(DocumentData document, View view){
        this.newDocument = document;
        this.view = view;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try{
            connect = DatabaseConnection.Connect();
            statement = connect.createStatement();
            statement.executeUpdate("insert into UpravljanjeLjudskimResursima.Dokument (Sifra, TipDokumentaSifra, ZaposlenikId, OvlastenikId, Datum, DatumOd, DatumDo, KorisnikId, KorisnikLastId, Status, TrajanjeDana, TrajanjeRadnihDana) values ('103159', '103', 12491, 12492, '20150829', '20150829', '20150829', 83, 83, 0, 1, 1)");
        }catch(SQLException e){
            Log.e("SQL error", e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        //Snackbar.make(view, "Dokument spremljen", Snackbar.LENGTH_LONG).show();
    }
}
