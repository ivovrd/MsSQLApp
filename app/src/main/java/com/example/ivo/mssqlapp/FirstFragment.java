package com.example.ivo.mssqlapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Ivo on 1.7.2015..
 */
public class FirstFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private List<Contact> contacts;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        contacts = ContactManager.getInstance().getContacts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_fragment, container, false);
        view.setTag("RecyclerViewFragment");

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(new ContactAdapter(contacts, R.layout.recycler_item, getActivity()));

        return view;
    }
}
