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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
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
    private Spinner spinner;
    private Switch switchLock;
    private ArrayList<Ovlastenik> ovlastenici;
    private int ovlastenikPickedIndex, isLocked;
    private long from, to, diff = 0;
    private boolean dateFromSet = false, dateToSet = false;
    private DocumentData document;
    private static final String TIP_DOKUMENTA = "103";
    private static final String FIXED_PART_SIFRA = "10315";
    private int docNum;
    private static final int TYPE_SAVE = 0;
    private static final int TYPE_LOCK = 1;
    private static final int TYPE_UNLOCK = 2;
    private SessionManager sessionManager;
    private Connection connect;
    private Statement statement;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.third_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinner = (Spinner)view.findViewById(R.id.spinner);
        year = (EditText)view.findViewById(R.id.editYear);
        dateFormat = new SimpleDateFormat("dd.MM.yyyy.", Locale.ENGLISH);
        dateFrom = (EditText)view.findViewById(R.id.editDateFrom);
        dateTo = (EditText)view.findViewById(R.id.editDateTo);
        daysCount = (EditText)view.findViewById(R.id.daysCount);
        workDaysCount = (EditText)view.findViewById(R.id.workDaysCount);
        remark = (EditText)view.findViewById(R.id.editRemark);
        memo = (EditText)view.findViewById(R.id.editMemo);
        switchLock = (Switch)view.findViewById(R.id.switchLock);
        Button buttonSave = (Button)view.findViewById(R.id.buttonSave);
        sessionManager = new SessionManager(getActivity());

        switchLock.setEnabled(false);
        daysCount.setEnabled(false);

        ovlastenici = new ArrayList<>();
        new AsyncDataLoading(ovlastenici, getActivity(), spinner).execute();

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

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFields(view, workDaysCount, dateFromSet, dateToSet)){
                    try{
                        connect = DatabaseConnection.Connect();
                        statement = connect.createStatement();
                        ResultSet resultSet = statement.executeQuery("SELECT Sifra FROM UpravljanjeLjudskimResursima.Dokument WHERE Id = (SELECT MAX(Id) FROM UpravljanjeLjudskimResursima.Dokument)");
                        if (resultSet != null && resultSet.next()){
                            String temp = resultSet.getString("Sifra").substring(5);
                            docNum = Integer.parseInt(temp);
                            docNum ++;
                        }
                    }catch(SQLException e){
                        Log.e("SQL error", e.getMessage());
                    }

                    int userId = Integer.valueOf(sessionManager.getUserId());
                    int partnerId = Integer.valueOf(sessionManager.getPartnerId());
                    document = new DocumentData(FIXED_PART_SIFRA + String.valueOf(docNum), TIP_DOKUMENTA, partnerId, ovlastenici.get(ovlastenikPickedIndex).Id, userId, isLocked, Integer.valueOf(daysCount.getText().toString()), Integer.valueOf(workDaysCount.getText().toString()), clearDateString(dateFrom.getText().toString()), clearDateString(dateTo.getText().toString()), addApostrophe(remark.getText().toString()), addApostrophe(memo.getText().toString()));
                    new AsyncSavingDocument(document, view, TYPE_SAVE).execute();
                }
            }
        });

        final EditText[] editTexts = {year, dateFrom, dateTo, workDaysCount, remark, memo};

        switchLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean checkValue = checkFields(view, workDaysCount, dateFromSet, dateToSet);

                if(isChecked && checkValue){
                    isLocked = 1;

                    int userId = Integer.valueOf(sessionManager.getUserId());
                    int partnerId = Integer.valueOf(sessionManager.getPartnerId());
                    document = new DocumentData(FIXED_PART_SIFRA + String.valueOf(docNum), TIP_DOKUMENTA, partnerId, ovlastenici.get(ovlastenikPickedIndex).Id, userId, isLocked, Integer.valueOf(daysCount.getText().toString()), Integer.valueOf(workDaysCount.getText().toString()), clearDateString(dateFrom.getText().toString()), clearDateString(dateTo.getText().toString()), addApostrophe(remark.getText().toString()), addApostrophe(memo.getText().toString()));
                    new AsyncSavingDocument(document, view, TYPE_LOCK).execute();

                    enableDisableViews(spinner, editTexts, false);
                } else if (isChecked){
                    switchLock.setChecked(false);
                }
                else {
                    isLocked = 0;

                    document = new DocumentData(FIXED_PART_SIFRA + String.valueOf(docNum), isLocked);
                    new AsyncSavingDocument(document, view, TYPE_UNLOCK).execute();

                    enableDisableViews(spinner, editTexts, true);
                }
            }
        });
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
                dateTo.setText(dateFormat.format(newDate.getTime()));
                dateToSet = true;
                to = newDate.getTimeInMillis();
                diff = (to - from)/(24*60*60*1000);
                if(diff >= 0)
                    daysCount.setText(String.valueOf(diff));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void makeWarningSnackbar(View view,String warning){
        Snackbar.make(view, warning, Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }).show();
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

    private void enableDisableViews(Spinner spinner, EditText[] editTexts, boolean enabled){
        spinner.setEnabled(enabled);
        for(int i = 0; i < editTexts.length; i++){
            editTexts[i].setEnabled(enabled);
        }
    }

    private boolean checkFields(View view, EditText workDaysCount, boolean dateFromSet, boolean dateToSet){
        int days, daysWork;
        boolean yearSet = false;
        boolean result = false;

        if (workDaysCount.getText().length() != 0) {
            daysWork = Integer.valueOf(workDaysCount.getText().toString());
            days = (int) diff;
        } else {
            days = 0;
            daysWork = 1;
        }

        if (year.getText().toString().trim().length() == 4) {
            yearSet = true;
        }

        if (!yearSet) {
            makeWarningSnackbar(view, "Nije unesena točna godina!");
        } else if (!dateFromSet) {
            makeWarningSnackbar(view, "Nije unesen datum početka godišnjeg!");
        } else if (!dateToSet) {
            makeWarningSnackbar(view, "Nije unesen datum završetka godišnjeg!");
        } else if (days - daysWork < 0) {
            makeWarningSnackbar(view, "Nije unesen točan broj radnih dana!");
        } else{
            result = true;
        }

        return result;
    }
}
