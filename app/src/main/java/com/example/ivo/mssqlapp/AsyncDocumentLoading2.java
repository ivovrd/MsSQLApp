package com.example.ivo.mssqlapp;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Ivo on 17.9.2015..
 */
public class AsyncDocumentLoading2 extends AsyncTask<String, Void, ResultSet> {
    Connection connect;
    Statement statement;

    @Override
    protected ResultSet doInBackground(String... params) {
        String docSifra = params[0];
        ResultSet resultSet = null;
        try{
            connect = DatabaseConnection.Connect();
            statement = connect.createStatement();
            resultSet = statement.executeQuery("SELECT UpravljanjeLjudskimResursima.Dokument.Sifra, Sifrarnici.Partner.Naziv, UpravljanjeLjudskimResursima.Dokument.Datum, UpravljanjeLjudskimResursima.Dokument.DatumOd, UpravljanjeLjudskimResursima.Dokument.DatumDo, UpravljanjeLjudskimResursima.Dokument.TrajanjeDana, UpravljanjeLjudskimResursima.Dokument.TrajanjeRadnihDana FROM UpravljanjeLjudskimResursima.Dokument INNER JOIN Sifrarnici.Partner ON UpravljanjeLjudskimResursima.Dokument.OvlastenikId=Sifrarnici.Partner.Id WHERE UpravljanjeLjudskimResursima.Dokument.Sifra='" + docSifra + "'");
        }catch (SQLException e){
            Log.e("SQL error", e.getMessage());
        }

        return resultSet;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(ResultSet resultSet) {
        super.onPostExecute(resultSet);
    }
}
