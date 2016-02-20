package com.example.ivo.mssqlapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Ivo on 28.8.2015..
 */
public class AsyncSavingDocument extends AsyncTask<Void, Void, Void> {
    private DocumentData newDocument;
    private ProgressDialog progressDialog;
    private Context context;
    private View view;
    private int task;

    public AsyncSavingDocument(DocumentData document, View view, int task, Context context){
        this.newDocument = document;
        this.view = view;
        this.task = task;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Connection connect;
        Statement statement;
        try{
            connect = DatabaseConnection.Connect();
            statement = connect.createStatement();
            if(task == 0){
                String queryPartOne, queryPartTwo;
                queryPartOne = "INSERT INTO UpravljanjeLjudskimResursima.Dokument (Sifra, TipDokumentaSifra, ZaposlenikId, OvlastenikId, Datum, Napomena, Memo, DatumOd, DatumDo, KorisnikId, KorisnikLastId, Status, TrajanjeDana, TrajanjeRadnihDana, GodinaGodisnjegOdmora) VALUES ";
                queryPartTwo = "(" + newDocument.getSifra() + ", " + newDocument.getTipDokumenta() + ", " + newDocument.getZaposlenikId() +  ", " + newDocument.getOvlastenikId() + ", CURRENT_TIMESTAMP, " + newDocument.getNapomena() + ", " + newDocument.getMemo() + ", " + newDocument.getDatumOd() + ", " + newDocument.getDatumDo() + ", " + newDocument.getKorisnikId() + ", " + newDocument.getKorisnikId() + ", " + newDocument.getStatus() + ", " + newDocument.getDani() + ", " + newDocument.getRadniDani() + ", " + newDocument.getGodina() + ")";
                statement.executeUpdate(queryPartOne + queryPartTwo);
            } else if (task == 1){
                String queryUpdate = "UPDATE UpravljanjeLjudskimResursima.Dokument SET OvlastenikId=" + newDocument.getOvlastenikId() + ", Napomena=" + newDocument.getNapomena() + ", Memo=" + newDocument.getMemo() + ", DatumOd=" + newDocument.getDatumOd() + ", DatumDo=" + newDocument.getDatumDo() +  ", Status=" + newDocument.getStatus() + ", TrajanjeDana=" + newDocument.getDani() + ", TrajanjeRadnihDana=" + newDocument.getRadniDani() + ", GodinaGodisnjegOdmora=" + newDocument.getGodina() + " WHERE Sifra=" + newDocument.getSifra();
                statement.executeUpdate(queryUpdate);
            } else if (task == 2){
                String queryLock = "UPDATE UpravljanjeLjudskimResursima.Dokument SET Status=" + newDocument.getStatus() + " WHERE Sifra=" + newDocument.getSifra();
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
        progressDialog = ProgressDialog.show(context, "", "Processing...");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
        Switch lock = (Switch)view.findViewById(R.id.switchLock);
        String snackBarText = "";

        if(task == 0){
            snackBarText = "Dokument spremljen!";
            lock.setEnabled(true);
        } else if (task == 1){
            snackBarText = "Dokument zaključen!";
        } else if (task == 2){
            snackBarText = "Dokument otključen!";
        }

        Snackbar.make(view, snackBarText, Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).show();
    }
}
