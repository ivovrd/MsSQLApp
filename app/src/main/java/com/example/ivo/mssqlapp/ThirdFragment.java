package com.example.ivo.mssqlapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private EditText daysCount, workDaysCount;
    private ArrayList<Ovlastenik> ovlastenici;
    private int ovlastenikPickedIndex;
    private long from, to, diff = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ovlastenici = new ArrayList<>();
        try{
            connect = DatabaseConnection.Connect();
            statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery("select Id, Naziv from Sifrarnici.Partner");
            while (resultSet.next()){
                Ovlastenik ovlastenik = new Ovlastenik(resultSet.getInt("Id"), resultSet.getString("Naziv"));
                ovlastenici.add(ovlastenik);
            }

        }catch (SQLException e){
            Log.e("SQL error", e.getMessage());
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.third_fragment, container, false);
        spinner = (Spinner)view.findViewById(R.id.spinner);
        dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        dateFrom = (EditText)view.findViewById(R.id.editDateFrom);
        dateTo = (EditText)view.findViewById(R.id.editDateTo);
        daysCount = (EditText)view.findViewById(R.id.daysCount);
        workDaysCount = (EditText)view.findViewById(R.id.workDaysCount);

        fillSpinner();
        setDateFields();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ovlastenikPickedIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        workDaysCount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    int daysWork = Integer.valueOf(workDaysCount.getText().toString());
                    int days = (int)diff;
                    if(days - daysWork < 0){
                        Snackbar.make(view, "Višak radnih dana!!!", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });
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
                from = newDate.getTimeInMillis();
                diff = (to - from)/(24*60*60*1000);
                if(diff >= 0)
                    daysCount.setText(String.valueOf(diff));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        dateToPickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateTo.setText(dateFormat.format(newDate.getTime()));
                to = newDate.getTimeInMillis();
                diff = (to - from)/(24*60*60*1000);
                if(diff >= 0)
                    daysCount.setText(String.valueOf(diff));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void fillSpinner(){
        ArrayList<String> labels = new ArrayList<>();

        for(int i = 0; i < ovlastenici.size(); i++){
            labels.add(ovlastenici.get(i).Naziv);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
