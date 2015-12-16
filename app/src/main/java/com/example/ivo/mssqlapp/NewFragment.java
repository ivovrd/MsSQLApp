package com.example.ivo.mssqlapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Ivo on 15.9.2015..
 */
public class NewFragment extends Fragment {
    TextView docSifra, docOvlastenik, docDate, docDateFrom, docDateTo, docDani, docRadniDani;
    LinearLayout loadingCircle;
    String dokumentSifra;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dokumentSifra = getArguments().getString("DOC_SIFRA");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_fragment, container, false);
        docSifra = (TextView)view.findViewById(R.id.docSifra);
        docOvlastenik = (TextView)view.findViewById(R.id.docOvlastenik);
        docDate = (TextView)view.findViewById(R.id.docDate);
        docDateFrom = (TextView)view.findViewById(R.id.docDateFrom);
        docDateTo = (TextView)view.findViewById(R.id.docDateTo);
        docDani = (TextView)view.findViewById(R.id.docDani);
        docRadniDani = (TextView)view.findViewById(R.id.docRadniDani);
        loadingCircle = (LinearLayout)view.findViewById(R.id.linProgBarr);
        new AsyncDocumentLoading().execute(dokumentSifra);
        return view;
    }

    public class AsyncDocumentLoading extends AsyncTask<String, Void, ResultSet> {
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
            loadingCircle.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ResultSet resultSet) {
            super.onPostExecute(resultSet);
            loadingCircle.setVisibility(View.GONE);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy.", Locale.ENGLISH);

            try{
                while (resultSet.next()){
                    docSifra.setText("Šifra dokumenta: " + resultSet.getString(1));
                    docOvlastenik.setText("Ovlaštenik: " + resultSet.getString(2));
                    docDate.setText("Datum stvaranja dokumenta: " + String.valueOf(dateFormat.format(resultSet.getDate(3))));
                    docDateFrom.setText("Početak godišnjeg odmora: " + String.valueOf(dateFormat.format(resultSet.getDate(4))));
                    docDateTo.setText("Kraj godišnjeg odmora: " + String.valueOf(dateFormat.format(resultSet.getDate(5))));
                    docDani.setText("Trajanje dana: " + String.valueOf(resultSet.getInt(6)));
                    docRadniDani.setText("Trajanje radnih dana: " + String.valueOf(resultSet.getInt(7)));
                }

            }catch (SQLException e){
                Log.e("SQL error", e.getMessage());
            }
        }
    }
}
