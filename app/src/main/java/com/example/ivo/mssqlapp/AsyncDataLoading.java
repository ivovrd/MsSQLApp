package com.example.ivo.mssqlapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Ivo on 8.9.2015..
 */
public class AsyncDataLoading extends AsyncTask<Void, Void, ResultSet> {
    private ArrayList<Ovlastenik> ovlastenici;
    private Context mContext;
    private Spinner spinner;
    private int selectedOvlastenikId;

    public AsyncDataLoading(ArrayList<Ovlastenik> ovlastenici, Context context, Spinner spinner, int selectedOvlastenikId){
        this.ovlastenici = ovlastenici;
        this.mContext = context;
        this.spinner = spinner;
        this.selectedOvlastenikId = selectedOvlastenikId;
    }

    @Override
    protected ResultSet doInBackground(Void... params) {
        ResultSet resultSet = null;
        try{
            Connection connect = DatabaseConnection.Connect();
            Statement statement = connect.createStatement();
            resultSet = statement.executeQuery("SELECT Id, Naziv FROM Sifrarnici.Partner WHERE Vrsta & 64 != 0");
        }catch (SQLException e){
            Log.e("SQL error", e.getMessage());
        }
        return resultSet;
    }

    @Override
    protected void onPostExecute(ResultSet resultSet) {
        super.onPostExecute(resultSet);
        try{
            while (resultSet.next()){
                Ovlastenik ovlastenik = new Ovlastenik(resultSet.getInt("Id"), resultSet.getString("Naziv"));
                ovlastenici.add(ovlastenik);
            }

        }catch (SQLException e){
            Log.e("SQL error", e.getMessage());
        }
        fillSpinner();
        if(selectedOvlastenikId != 0) {
            int i = 0, ovlastenikPosititon = 0;
            while (ovlastenici.get(i).getId() != selectedOvlastenikId) {
                i++;
                ovlastenikPosititon = i;
            }
            spinner.setSelection(ovlastenikPosititon);
        }
    }

    private void fillSpinner(){
        ArrayList<String> labels = new ArrayList<>();

        for(int i = 0; i < ovlastenici.size(); i++){
            labels.add(ovlastenici.get(i).getNaziv());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}