package com.example.ivo.mssqlapp;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.nio.charset.Charset;
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
    private EditText year, dateFrom, dateTo, daysCount, workDaysCount, remark, memo;
    private DatePickerDialog dateFromPickerDialog, dateToPickerDialog;
    private SimpleDateFormat dateFormat;
    private Connection connect;
    private Statement statement;
    private Spinner spinner;
    private Switch switchLock;
    private Button buttonSave;
    private ArrayList<Ovlastenik> ovlastenici;
    private int ovlastenikPickedIndex, isLocked;
    private long from, to, diff = 0;
    private boolean yearSet = false, dateFromSet = false, dateToSet = false;
    private DocumentData document;
    private SharedPreferences sharedPreferences;
    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "UserLoginData";
    public static final String KEY_PARTNER_ID = "partnerId";
    public static final String KEY_USER_ID = "userId";
    private static final String TIP_DOKUMENTA = "103";

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
        year = (EditText)view.findViewById(R.id.editYear);
        dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        dateFrom = (EditText)view.findViewById(R.id.editDateFrom);
        dateTo = (EditText)view.findViewById(R.id.editDateTo);
        daysCount = (EditText)view.findViewById(R.id.daysCount);
        workDaysCount = (EditText)view.findViewById(R.id.workDaysCount);
        remark = (EditText)view.findViewById(R.id.editRemark);
        memo = (EditText)view.findViewById(R.id.editMemo);
        switchLock = (Switch)view.findViewById(R.id.switchLock);
        buttonSave = (Button)view.findViewById(R.id.buttonSave);

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

        year.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && year.getText().toString().trim().length() == 4) {
                    yearSet = true;
                }
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int daysWork = Integer.valueOf(workDaysCount.getText().toString());
                int days = (int)diff;


                if (!yearSet) {
                    makeWarningSnackbar(view, "Nije unesena točna godina!");
                } else if (!dateFromSet) {
                    makeWarningSnackbar(view, "Nije unesen datum početka godišnjeg!");
                } else if (!dateToSet) {
                    makeWarningSnackbar(view, "Nije unesen datum završetka godišnjeg!");
                } else if (days - daysWork < 0) {
                    makeWarningSnackbar(view, "Nije unesen točan broj radnih dana!");
                } else {
                    sharedPreferences = getActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
                    int userId = Integer.valueOf(sharedPreferences.getString(KEY_USER_ID, null));
                    int partnerId = Integer.valueOf(sharedPreferences.getString(KEY_PARTNER_ID, null));
                    document = new DocumentData("1031511", TIP_DOKUMENTA, partnerId, partnerId, userId, isLocked, Integer.valueOf(daysCount.getText().toString()), Integer.valueOf(workDaysCount.getText().toString()), clearDateString(dateFrom.getText().toString()), clearDateString(dateTo.getText().toString()), addApostrophe(remark.getText().toString()), addApostrophe(memo.getText().toString()));
                    new AsyncSavingDocument(document, view).execute();
                }
            }
        });

        switchLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isLocked = 1;
                } else {
                    isLocked = 0;
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
                dateFrom.setText(dateFormat.format(newDate.getTime()) + ".");
                dateFromSet = true;
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
                dateTo.setText(dateFormat.format(newDate.getTime()) + ".");
                dateToSet = true;
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

    private void makeWarningSnackbar(View view,String warning){
        Snackbar.make(view, warning, Snackbar.LENGTH_LONG).show();
    }

    private String addApostrophe(String string){
        return "'" + string + "'";
    }

    private String clearDateString(String dateString){
        String first, second, third;
        first = dateString.substring(0,2);
        second = dateString.substring(3,5);
        third = dateString.substring(6,10);
        return "'" + third + second + first + "'";
    }
}
