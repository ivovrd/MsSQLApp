package com.example.ivo.mssqlapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Ivo on 15.9.2015..
 */
public class NewFragment extends Fragment {
    private String dokumentSifra, ovlastenik, datum, datumOd, datumDo, napomena, memo;
    private int dani, radniDani;
    private static final String EMPTY_STRING = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar toolbar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if(toolbar != null) {
            toolbar.setHomeButtonEnabled(false);
            toolbar.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        }

        dokumentSifra = getArguments().getString("DOC_SIFRA");
        ovlastenik = getArguments().getString("DOC_OVLASTENIK");
        datum = getArguments().getString("DOC_DATUM");
        datumOd = getArguments().getString("DOC_DATUM_OD");
        datumDo = getArguments().getString("DOC_DATUM_DO");
        dani = getArguments().getInt("DOC_DANI");
        radniDani = getArguments().getInt("DOC_RADNI_DANI");
        napomena = getArguments().getString("DOC_NAPOMENA");
        memo = getArguments().getString("DOC_MEMO");
        napomena = (napomena.equals(EMPTY_STRING)) ? "Nema napomene" : napomena;
        memo = (memo.equals(EMPTY_STRING)) ? "Nema mema" : memo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView docSifra, docOvlastenik, docDate, docDateFrom, docDateTo, docDani, docRadniDani, docNapomena, docMemo;

        View view = inflater.inflate(R.layout.new_fragment, container, false);
        docSifra = (TextView)view.findViewById(R.id.docSifra);
        docOvlastenik = (TextView)view.findViewById(R.id.docOvlastenik);
        docDate = (TextView)view.findViewById(R.id.docDate);
        docDateFrom = (TextView)view.findViewById(R.id.docDateFrom);
        docDateTo = (TextView)view.findViewById(R.id.docDateTo);
        docDani = (TextView)view.findViewById(R.id.docDani);
        docRadniDani = (TextView)view.findViewById(R.id.docRadniDani);
        docNapomena = (TextView)view.findViewById(R.id.docNapomena);
        docMemo = (TextView)view.findViewById(R.id.docMemo);

        docSifra.setText("Šifra zahtjeva: " + dokumentSifra);
        docOvlastenik.setText("Ovlaštenik: " + ovlastenik);
        docDate.setText("Datum slanja zahtjeva: " + datum);
        docDateFrom.setText("Početak godišnjeg odmora: " + datumOd);
        docDateTo.setText("Kraj godišnjeg odmora: " + datumDo);
        docDani.setText("Trajanje dana: " + Integer.toString(dani));
        docRadniDani.setText("Trajanje radnih dana: " + Integer.toString(radniDani));
        docNapomena.setText("Napomena: " + napomena);
        docMemo.setText("Memo: " + memo);
        return view;
    }
}
