package com.example.ivo.mssqlapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Created by Ivo on 1.7.2015..
 */
public class FirstFragment extends Fragment{

    private RecyclerView mRecyclerView;
    String ipaddress, db, username, password;
    Connection connect;
    Statement statement;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        ipaddress = "192.168.2.14/";
        db = "MyDatabase";
        username = "admin";
        password = "admin123";

        try{
            connect = Login.ConnectionHelper(username, password, db, ipaddress);
            statement = connect.createStatement();
            ResultSet result = statement.executeQuery("select top 10 FirstName, LastName, Email, PhoneNum from Contacts");

            while(result.next()){
                ContactManager.getInstance().setContacts(result.getString("FirstName"), result.getString("LastName"), result.getString("Email"), result.getString("PhoneNum"));
            }
        }catch(SQLException e){
            Log.e("SQL error", e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_fragment, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());



        mRecyclerView.setAdapter(new ContactAdapter(ContactManager.getInstance().getContacts(), R.layout.recycler_item, getActivity().getApplicationContext()));
        return view;
    }
}
