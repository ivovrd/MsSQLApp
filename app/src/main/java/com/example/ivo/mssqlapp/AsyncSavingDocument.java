package com.example.ivo.mssqlapp;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.sql.Connection;
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

    public AsyncSavingDocument(DocumentData document, View view){
        this.newDocument = document;
        this.view = view;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try{
            connect = DatabaseConnection.Connect();
            statement = connect.createStatement();
            statement.executeQuery("insert into UpravljanjeLjudskimResursima.Dokument (Sifra, TipDokumentaSifra, ZaposlenikId, OvlastenikId, Datum, Napomena, Memo, DatumOd, DatumDo, KorisnikId, KorisnikLastId, Status, TrajanjeDana, TrajanjeRadnihDana) values (" + newDocument.Sifra + ", " + newDocument.TipDokumenta + ", " + newDocument.ZaposlenikId + ", " + newDocument.OvlastenikId + ", " + "convert(datetime, " + newDocument.Datum + ", 5)" + ", " + newDocument.Napomena + ", " + newDocument.Memo + ", " + "convert(datetime, " + newDocument.DatumOd + ", 5)" + ", " + "convert(datetime, " + newDocument.DatumDo + ", 5)" + ", " + newDocument.KorisnikId + ", " + newDocument.KorisnikId + ", " + newDocument.Status + ", " + newDocument.Dani + ", " + newDocument.RadniDani + ")");
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
