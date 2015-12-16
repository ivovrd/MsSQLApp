package com.example.ivo.mssqlapp;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ivo on 4.7.2015..
 */
public class AsyncDbConnection extends AsyncTask<Void, Void, ResultSet> {
    Connection connect;
    Statement statement;
    DocPrevAdapter mAdapter;
    private List<DocPrev> docPrevList;
    int start;

    public AsyncDbConnection(DocPrevAdapter adapter, List<DocPrev> docPrevList){
        this.mAdapter = adapter;
        this.docPrevList = docPrevList;
    }

    @Override
    protected void onPreExecute() {
        start = docPrevList.size();
        super.onPreExecute();
    }

    @Override
    protected ResultSet doInBackground(Void... params) {
        ResultSet result = null;

        try{
            connect = DatabaseConnection.Connect();
            statement = connect.createStatement();
            result = statement.executeQuery("select Sifra, Datum, Napomena from UpravljanjeLjudskimResursima.Dokument ORDER BY Id OFFSET " + String.valueOf(start) + " ROWS FETCH NEXT 10 ROWS ONLY");
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
        docPrevList.remove(docPrevList.size() - 1);
        mAdapter.notifyItemRemoved(docPrevList.size());

        try {
            while(result.next()){
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy.", Locale.ENGLISH);
                DocPrevManager.getInstance().setDocPrevs(result.getString("Sifra"), String.valueOf(dateFormat.format(result.getDate("Datum"))), result.getString("Napomena"));
                mAdapter.notifyDataSetChanged();
            }
        } catch (SQLException e) {
            Log.e("SQL error", e.getMessage());
        }
        mAdapter.setLoaded();
        super.onPostExecute(result);
    }
}
