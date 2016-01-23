package com.example.ivo.mssqlapp;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

public class MakeDocActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText year, dateFrom, dateTo, daysCount, workDaysCount, remark, memo;
    private Button buttonSave;
    private DatePickerDialog dateFromPickerDialog, dateToPickerDialog;
    private SimpleDateFormat dateFormat;
    private Spinner spinner;
    private Switch switchLock;
    private ArrayList<Ovlastenik> ovlastenici;
    private int ovlastenikPickedIndex, isLocked;
    private long from, to, diff = 0;
    private boolean dateFromSet = false, dateToSet = false, argsExists = false;
    private DocumentData document;
    private static final String TIP_DOKUMENTA = "103";
    private String docSifraYearPart = "";
    private String thisDocSifra;
    private int ovlastenikId = 0;
    private int docNum;
    private static final int TYPE_SAVE = 0;
    private static final int TYPE_LOCK = 1;
    private static final int TYPE_UNLOCK = 2;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_doc);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Novi zahtjev");

        Calendar rightNow = Calendar.getInstance();
        docSifraYearPart = Integer.toString(rightNow.get(Calendar.YEAR)).substring(2,4);

        spinner = (Spinner)findViewById(R.id.spinner);
        year = (EditText)findViewById(R.id.editYear);
        dateFormat = new SimpleDateFormat("dd.MM.yyyy.", Locale.ENGLISH);
        dateFrom = (EditText)findViewById(R.id.editDateFrom);
        dateTo = (EditText)findViewById(R.id.editDateTo);
        daysCount = (EditText)findViewById(R.id.daysCount);
        workDaysCount = (EditText)findViewById(R.id.workDaysCount);
        remark = (EditText)findViewById(R.id.editRemark);
        memo = (EditText)findViewById(R.id.editMemo);
        switchLock = (Switch)findViewById(R.id.switchLock);
        buttonSave = (Button)findViewById(R.id.buttonSave);
        sessionManager = new SessionManager(this);

        switchLock.setEnabled(false);
        daysCount.setEnabled(false);

        setDateFields();

        Bundle args = getIntent().getExtras();

        if(args != null){
            getSupportActionBar().setTitle("Uređivanje zahtjeva");
            year.setText(Integer.toString(args.getInt("DOC_GODINA")));
            dateFrom.setText(args.getString("DOC_DATUM_OD"));
            dateFromSet = true;
            dateTo.setText(args.getString("DOC_DATUM_DO"));
            dateToSet = true;
            daysCount.setText(Integer.toString(args.getInt("DOC_DANI")));
            diff = args.getInt("DOC_DANI");
            workDaysCount.setText(Integer.toString(args.getInt("DOC_RADNI_DANI")));
            remark.setText(args.getString("DOC_NAPOMENA"));
            memo.setText(args.getString("DOC_MEMO"));
            ovlastenikId = args.getInt("DOC_OVLASTENIK_ID");

            switchLock.setEnabled(true);
            buttonSave.setEnabled(false);
            thisDocSifra = args.getString("DOC_SIFRA");
            argsExists = true;
        }

        ovlastenici = new ArrayList<>();
        new AsyncDataLoading(ovlastenici, this, spinner, ovlastenikId).execute();

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
                if (checkFields(findViewById(R.id.content), workDaysCount, dateFromSet, dateToSet)){
                    buttonSave.setEnabled(false);
                    new AsyncDocCodeRetrieving().execute();
                }
            }
        });

        final EditText[] editTexts = {year, dateFrom, dateTo, workDaysCount, remark, memo};

        switchLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean checkValue = checkFields(findViewById(R.id.content), workDaysCount, dateFromSet, dateToSet);

                if(isChecked && checkValue){
                    isLocked = 1;
                    String docSifra;

                    if(argsExists)
                        docSifra = thisDocSifra;
                    else
                        docSifra = TIP_DOKUMENTA + docSifraYearPart + String.valueOf(docNum);

                    int userId = Integer.valueOf(sessionManager.getUserId());
                    int partnerId = Integer.valueOf(sessionManager.getPartnerId());
                    document = new DocumentData(docSifra, TIP_DOKUMENTA, partnerId, ovlastenici.get(ovlastenikPickedIndex).Id, userId, isLocked, Integer.valueOf(daysCount.getText().toString()), Integer.valueOf(workDaysCount.getText().toString()), Integer.valueOf(year.getText().toString()), clearDateString(dateFrom.getText().toString()), clearDateString(dateTo.getText().toString()), addApostrophe(remark.getText().toString()), addApostrophe(memo.getText().toString()));

                    new AsyncSavingDocument(document, findViewById(R.id.content), TYPE_LOCK).execute();
                    enableDisableViews(spinner, editTexts, false);
                } else if (isChecked){
                    switchLock.setChecked(false);
                }
                else {
                    isLocked = 0;
                    String docSifra;
                    if(argsExists)

                        docSifra = thisDocSifra;
                    else
                        docSifra = TIP_DOKUMENTA + docSifraYearPart + String.valueOf(docNum);

                    document = new DocumentData(docSifra, isLocked);
                    new AsyncSavingDocument(document, findViewById(R.id.content), TYPE_UNLOCK).execute();
                    enableDisableViews(spinner, editTexts, true);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        dateFrom.setText(dateFormat.format(calendar.getTime()));
        dateTo.setText(dateFormat.format(calendar.getTime()));

        dateFromPickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateFrom.setText(dateFormat.format(newDate.getTime()));
                dateFromSet = true;
                from = newDate.getTimeInMillis();
                diff = (to - from)/(24*60*60*1000) + 1;
                if(diff >= 1)
                    daysCount.setText(String.valueOf(diff));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        dateToPickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateTo.setText(dateFormat.format(newDate.getTime()));
                dateToSet = true;
                to = newDate.getTimeInMillis();
                diff = (to - from)/(24*60*60*1000) + 1;
                if(diff >= 1)
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
        for (EditText editText: editTexts) {
            editText.setEnabled(enabled);
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
            makeWarningSnackbar(view, "Nije unesena godina!");
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

    public class AsyncDocCodeRetrieving extends AsyncTask<Void, Void, ResultSet>{

        @Override
        protected ResultSet doInBackground(Void... voids) {
            ResultSet resultSet = null;
            Connection connect;
            Statement statement;

            try{
                connect = DatabaseConnection.Connect();
                statement = connect.createStatement();
                resultSet = statement.executeQuery("SELECT Sifra FROM UpravljanjeLjudskimResursima.Dokument WHERE Id = (SELECT MAX(Id) FROM UpravljanjeLjudskimResursima.Dokument)");
            }catch(SQLException e){
                Log.e("SQL error", e.getMessage());
            }

            if(resultSet != null) {
                return resultSet;
            }else{
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResultSet resultSet) {
            try {
                if (resultSet != null && resultSet.next()) {
                    String temp = resultSet.getString("Sifra").substring(5);
                    docNum = Integer.parseInt(temp);
                    docNum++;
                }
            } catch (SQLException e){
                Log.e("SQL error", e.getMessage());
            }

            int userId = Integer.valueOf(sessionManager.getUserId());
            int partnerId = Integer.valueOf(sessionManager.getPartnerId());
            document = new DocumentData(TIP_DOKUMENTA + docSifraYearPart + String.valueOf(docNum), TIP_DOKUMENTA, partnerId, ovlastenici.get(ovlastenikPickedIndex).Id, userId, isLocked, Integer.valueOf(daysCount.getText().toString()), Integer.valueOf(workDaysCount.getText().toString()), Integer.valueOf(year.getText().toString()), clearDateString(dateFrom.getText().toString()), clearDateString(dateTo.getText().toString()), addApostrophe(remark.getText().toString()), addApostrophe(memo.getText().toString()));
            new AsyncSavingDocument(document, findViewById(R.id.content), TYPE_SAVE).execute();

            super.onPostExecute(resultSet);
        }
    }
}
