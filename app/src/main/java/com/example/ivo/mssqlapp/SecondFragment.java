package com.example.ivo.mssqlapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
public class SecondFragment extends Fragment {
    private Connection connect;
    private Statement statement;
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private List<Contact> contacts;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        try{
            connect = DatabaseConnection.Connect();
            statement = connect.createStatement();
            ResultSet result = statement.executeQuery("select top 10 Sifra, Naziv, Opis from Sifrarnici.Artikl");

            while(result.next()){
                ContactManager.getInstance().setContacts(result.getString("Sifra"), result.getString("Naziv"), result.getString("Opis"));
            }
        }catch(SQLException e){
            Log.e("SQL error", e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.second_fragment, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerList2);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        contactAdapter = new NewContactAdapter(ContactManager.getInstance().getContacts(),recyclerView);
        recyclerView.setAdapter(contactAdapter);

        contactAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                contacts = ContactManager.getInstance().getContacts();
                contacts.add(null);
                contactAdapter.notifyItemInserted(contacts.size() - 1);
                new AsyncDbConnection(contactAdapter, contacts).execute();
            }
        });
        return view;
    }
}
