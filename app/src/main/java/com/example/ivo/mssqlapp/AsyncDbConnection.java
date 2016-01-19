package com.example.ivo.mssqlapp;

import android.content.Context;
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
    int dataType;
    Context context;

    public AsyncDbConnection(DocPrevAdapter adapter, List<DocPrev> docPrevList, int dataType, Context context){
        this.mAdapter = adapter;
        this.docPrevList = docPrevList;
        this.dataType = dataType;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        start = docPrevList.size();
        super.onPreExecute();
    }

    @Override
    protected ResultSet doInBackground(Void... params) {
        ResultSet result = null;
        String queryPart;
        SessionManager session = new SessionManager(context);

        try{
            connect = DatabaseConnection.Connect();
            statement = connect.createStatement();
            if(dataType == 1)
                queryPart = " AND UpravljanjeLjudskimResursima.Dokument.Status=0";
            else
                queryPart = " AND UpravljanjeLjudskimResursima.Dokument.Status=1";
            result = statement.executeQuery("SELECT UpravljanjeLjudskimResursima.Dokument.Sifra, UpravljanjeLjudskimResursima.Dokument.Datum, UpravljanjeLjudskimResursima.Dokument.DatumOd, UpravljanjeLjudskimResursima.Dokument.DatumDo, UpravljanjeLjudskimResursima.Dokument.Napomena, UpravljanjeLjudskimResursima.Dokument.Memo, Sifrarnici.Partner.Naziv, UpravljanjeLjudskimResursima.Dokument.OvlastenikId, UpravljanjeLjudskimResursima.Dokument.TrajanjeDana, UpravljanjeLjudskimResursima.Dokument.TrajanjeRadnihDana, UpravljanjeLjudskimResursima.Dokument.GodinaGodisnjegOdmora FROM UpravljanjeLjudskimResursima.Dokument INNER JOIN Sifrarnici.Partner ON UpravljanjeLjudskimResursima.Dokument.OvlastenikId=Sifrarnici.Partner.Id  WHERE UpravljanjeLjudskimResursima.Dokument.KorisnikId=" + session.getUserId() + queryPart + " ORDER BY UpravljanjeLjudskimResursima.Dokument.Id OFFSET " + String.valueOf(start) + " ROWS FETCH NEXT 10 ROWS ONLY");
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
            while(result != null && result.next()){
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy.", Locale.ENGLISH);
                DocPrevManager.getInstance().setDocPrevs(result.getString(1), String.valueOf(dateFormat.format(result.getDate(2))), String.valueOf(dateFormat.format(result.getDate(3))), String.valueOf(dateFormat.format(result.getDate(4))), result.getString(5), result.getString(6), result.getString(7),  result.getInt(8), result.getInt(9), result.getInt(10), result.getInt(11));
                mAdapter.notifyDataSetChanged();
            }
        } catch (SQLException e) {
            Log.e("SQL error", e.getMessage());
        }
        mAdapter.setLoaded();
        super.onPostExecute(result);
    }
}
