package com.example.ivo.mssqlapp;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ivo on 1.7.2015..
 */
public class FirstFragment extends Fragment{
    private ContactAdapter mAdapter;
    private List<Contact> contacts;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Connection connect;
        Statement statement;

        try{
            connect = DatabaseConnection.Connect();
            statement = connect.createStatement();
            ResultSet result = statement.executeQuery("select top 10 Sifra, Datum, Napomena from UpravljanjeLjudskimResursima.Dokument");

            while(result.next()){
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy.", Locale.ENGLISH);
                ContactManager.getInstance().setContacts(result.getString("Sifra"), String.valueOf(dateFormat.format(result.getDate("Datum"))), result.getString("Napomena"));
            }

        }catch(SQLException e){
            Log.e("SQL error", e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_fragment, container, false);

        RecyclerView mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerList);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new ContactAdapter(ContactManager.getInstance().getContacts(), mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                contacts = ContactManager.getInstance().getContacts();
                contacts.add(null);
                mAdapter.notifyItemInserted(contacts.size() - 1);
                new AsyncDbConnection(mAdapter, contacts).execute();
            }
        });

        mAdapter.setOnRecyclerViewClickListener(new RecyclerViewClickListener() {
            @Override
            public void recyclerViewItemClicked(View view, int position) {
                Snackbar.make(view, "Item number " + (position + 1) + " clicked!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
                Fragment fragment = new NewFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack("FRAGMENT_TAG").commit();
            }
        });

        return view;
    }

    /*@Override
    public void onDestroyView() {
        ContactManager.getInstance().getContacts().clear();
        super.onDestroyView();
    }*/
}
