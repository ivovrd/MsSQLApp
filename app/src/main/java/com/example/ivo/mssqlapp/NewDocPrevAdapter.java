package com.example.ivo.mssqlapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Ivo on 6.7.2015..
 */
public class NewDocPrevAdapter extends DocPrevAdapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private static final String EMPTY_STRING = "";

    public NewDocPrevAdapter(List<DocPrev> docPrevList, RecyclerView recyclerView) {
        super(docPrevList, recyclerView);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if(viewType == VIEW_ITEM){
            viewHolder = new DocPrevViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_recycler_item, viewGroup, false));
        }else{
            viewHolder = new ProgressViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progressbar_item, viewGroup, false));
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        String napomena;
        if(viewHolder instanceof DocPrevViewHolder) {
            DocPrev docPrev = docPrevList.get(i);
            ((DocPrevViewHolder) viewHolder).sifra.setText("Zahtjev broj " + docPrev.getSifra());
            ((DocPrevViewHolder) viewHolder).datum.setText("Datum slanja zahtjeva: " + docPrev.getDatum());
            napomena = (docPrev.getNapomena().equals(EMPTY_STRING)) ? "Nema napomene" : docPrev.getNapomena();
            ((DocPrevViewHolder) viewHolder).napomena.setText("Napomena: " + napomena);
        }else{
            ((ProgressViewHolder)viewHolder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void setLoaded() {
        super.setLoaded();
    }

    @Override
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        super.setOnLoadMoreListener(onLoadMoreListener);
    }
}
