package com.example.ivo.mssqlapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Ivo on 1.7.2015..
 */
public class ThirdFragment extends Fragment implements View.OnClickListener{
    private EditText dateFrom, dateTo;
    private DatePickerDialog dateFromPickerDialog, dateToPickerDialog;
    private SimpleDateFormat dateFormat;
    private Connection connect;
    private Statement statement;
    private Spinner spinner;
    private ArrayList<String> ovlastenici;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ovlastenici = new ArrayList<>();
        try{
            connect = DatabaseConnection.Connect();
            statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery("select Id, Naziv from Sifrarnici.Partner");
            while (resultSet.next()){
                ovlastenici.add(resultSet.getString("Naziv"));
            }

        }catch (SQLException e){
            Log.e("SQL error", e.getMessage());
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.third_fragment, container, false);

        spinner = (Spinner)view.findViewById(R.id.spinner);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, ovlastenici);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        dateFrom = (EditText)view.findViewById(R.id.editDateFrom);
        dateTo = (EditText)view.findViewById(R.id.editDateTo);
        setDateFields();
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view == dateFrom){
           dateFromPickerDialog.show();
        } else if(view == dateTo){
            dateToPickerDialog.show();
        }
    }

    private void setDateFields(){
        dateFrom.setOnClickListener(this);
        dateTo.setOnClickListener(this);
        final Calendar calendar = Calendar.getInstance();
        dateFromPickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateFrom.setText(dateFormat.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        dateToPickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateTo.setText(dateFormat.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }
}
