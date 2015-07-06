package com.example.ivo.mssqlapp;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ivo on 1.7.2015..
 */
public class ContactAdapter extends RecyclerView.Adapter {
    private List<Contact> contacts;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public ContactAdapter(List<Contact> contacts, RecyclerView recyclerView){
        this.contacts = contacts;

        if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                private boolean userScrolled;

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (userScrolled && !loading && totalItemCount <= (lastVisibleItem + 1)) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if(newState == NumberPicker.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                        userScrolled = true;
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position){
        return contacts.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder holder;
        if(viewType == VIEW_ITEM){
            holder = new ContactViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item, viewGroup, false));
        }else{
            holder = new ProgressViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progressbar_item, viewGroup, false));
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof ContactViewHolder) {
            Contact contact = contacts.get(i);
            ((ContactViewHolder) viewHolder).firstName.setText(contact.firstName);
            ((ContactViewHolder) viewHolder).lastName.setText(contact.lastName);
            ((ContactViewHolder) viewHolder).eMail.setText(contact.eMail);
            ((ContactViewHolder) viewHolder).phoneNum.setText(contact.phoneNumber);
        }else{
            ((ProgressViewHolder)viewHolder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return contacts == null ? 0 : contacts.size();
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener){
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder{
        public TextView firstName, lastName, eMail, phoneNum;

        public ContactViewHolder(View itemView) {
            super(itemView);
            firstName = (TextView)itemView.findViewById(R.id.contactFirstName);
            lastName = (TextView)itemView.findViewById(R.id.contactLastName);
            eMail = (TextView)itemView.findViewById(R.id.contactEmail);
            phoneNum = (TextView)itemView.findViewById(R.id.contactPhoneNum);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder{
        public ProgressBar progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar);
        }
    }
}
