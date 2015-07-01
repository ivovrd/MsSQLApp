package com.example.ivo.mssqlapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Ivo on 1.7.2015..
 */
public class FirstFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_fragment, container, false);
        //Setup handlers to view objects here
        //etFoo = (EditText)view.findViewById(R.id.etFoo);
        return view;
    }
}
