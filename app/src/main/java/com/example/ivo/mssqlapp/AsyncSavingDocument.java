package com.example.ivo.mssqlapp;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

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
    int task;

    public AsyncSavingDocument(DocumentData document, View view, int task){
        this.newDocument = document;
        this.view = view;
        this.task = task;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try{
            connect = DatabaseConnection.Connect();
            statement = connect.createStatement();
            if(task == 0){
                String queryPartOne, queryPartTwo;
                queryPartOne = "INSERT INTO UpravljanjeLjudskimResursima.Dokument (Sifra, TipDokumentaSifra, ZaposlenikId, OvlastenikId, Datum, Napomena, Memo, DatumOd, DatumDo, KorisnikId, KorisnikLastId, Status, TrajanjeDana, TrajanjeRadnihDana, GodinaGodisnjegOdmora) VALUES ";
                queryPartTwo = "(" + newDocument.Sifra + ", " + newDocument.TipDokumenta + ", " + newDocument.ZaposlenikId +  ", " + newDocument.OvlastenikId + ", CURRENT_TIMESTAMP, " + newDocument.Napomena + ", " + newDocument.Memo + ", " + newDocument.DatumOd + ", " + newDocument.DatumDo + ", " + newDocument.KorisnikId + ", " + newDocument.KorisnikId + ", " + newDocument.Status + ", " + newDocument.Dani + ", " + newDocument.RadniDani + ", " + newDocument.Godina + ")";
                statement.executeUpdate(queryPartOne + queryPartTwo);
            } else if (task == 1){
                String queryUpdate = "UPDATE UpravljanjeLjudskimResursima.Dokument SET OvlastenikId=" + newDocument.OvlastenikId + ", Napomena=" + newDocument.Napomena + ", Memo=" + newDocument.Memo + ", DatumOd=" + newDocument.DatumOd + ", DatumDo=" + newDocument.DatumDo +  ", Status=" + newDocument.Status + ", TrajanjeDana=" + newDocument.Dani + ", TrajanjeRadnihDana=" + newDocument.RadniDani + ", GodinaGodisnjegOdmora=" + newDocument.Godina + " WHERE Sifra=" + newDocument.Sifra;
                statement.executeUpdate(queryUpdate);
            } else if (task == 2){
                String queryLock = "UPDATE UpravljanjeLjudskimResursima.Dokument SET Status=" + newDocument.Status + " WHERE Sifra=" + newDocument.Sifra;
                statement.executeUpdate(queryLock);
            }

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
        Button saveButton = (Button)view.findViewById(R.id.buttonSave);
        Switch lock = (Switch)view.findViewById(R.id.switchLock);
        String snackBarText = "";

        if(task == 0){
            snackBarText = "Dokument spremljen!";
            saveButton.setEnabled(false);
            lock.setEnabled(true);
        } else if (task == 1){
            snackBarText = "Dokument zaključen!";
        }
        else if (task == 2){
            snackBarText = "Dokument otključen!";
        }

        Snackbar.make(view, snackBarText, Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).show();
    }
}
