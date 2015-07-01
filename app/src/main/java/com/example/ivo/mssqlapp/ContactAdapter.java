package com.example.ivo.mssqlapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ADMIN on 1.7.2015..
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private List<Contact> contacts;
    private int rowLayout;
    private Context mContext;

    public ContactAdapter(List<Contact> contacts, int rowLayout, Context context){
        this.contacts = contacts;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup));
    }

    @Override
    public void onBindViewHolder(ContactAdapter.ViewHolder viewHolder, int i) {
        Contact contact = contacts.get(i);
        viewHolder.firstName.setText(contact.firstName);
        viewHolder.lastName.setText(contact.lastName);
    }

    @Override
    public int getItemCount() {
        return contacts == null ? 0 : contacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView firstName, lastName;

        public ViewHolder(View itemView) {
            super(itemView);
            firstName = (TextView)itemView.findViewById(R.id.contactFirstName);
            lastName = (TextView)itemView.findViewById(R.id.contactLastName);
        }
    }
}
