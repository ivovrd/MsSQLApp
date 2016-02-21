package com.example.ivo.mssqlapp;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
public class DocPrevAdapter extends RecyclerView.Adapter {
    protected List<DocPrev> docPrevList;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private static RecyclerViewClickListener recyclerViewClickListener;
    private static final String EMPTY_STRING = "";

    public DocPrevAdapter(List<DocPrev> docPrevList, RecyclerView recyclerView){
        this.docPrevList = docPrevList;

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
        return docPrevList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder holder;
        if(viewType == VIEW_ITEM){
            holder = new DocPrevViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item, viewGroup, false));
        }else{
            holder = new ProgressViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progressbar_item, viewGroup, false));
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        String napomena;
        if(viewHolder instanceof DocPrevViewHolder) {
            DocPrev docPrev = docPrevList.get(i);
            ((DocPrevViewHolder) viewHolder).sifra.setText("Šifra: " + docPrev.getSifra());
            ((DocPrevViewHolder) viewHolder).datum.setText("Datum kreiranja: " + docPrev.getDatum());
            ((DocPrevViewHolder) viewHolder).ovlastenik.setText("Ovlaštenik: " + docPrev.getOvlastenik());
            ((DocPrevViewHolder) viewHolder).raspon.setText("Trajanje: " + docPrev.getDatumOd() + " - " + docPrev.getDatumDo());
            napomena = (docPrev.getNapomena().equals(EMPTY_STRING)) ? "Nema napomene" : docPrev.getNapomena();
            ((DocPrevViewHolder) viewHolder).napomena.setText("Napomena: " + napomena);

        }else{
            ((ProgressViewHolder)viewHolder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return docPrevList == null ? 0 : docPrevList.size();
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener){
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setOnRecyclerViewClickListener(RecyclerViewClickListener recyclerViewClickListener){
        DocPrevAdapter.recyclerViewClickListener = recyclerViewClickListener;
    }

    public static class DocPrevViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView sifra, datum, ovlastenik, raspon, napomena;

        public DocPrevViewHolder(View itemView) {
            super(itemView);
            sifra = (TextView)itemView.findViewById(R.id.sifraDokumenta);
            datum = (TextView)itemView.findViewById(R.id.datumNastanka);
            ovlastenik = (TextView)itemView.findViewById(R.id.ovlastenik);
            raspon = (TextView)itemView.findViewById(R.id.datumRaspon);
            napomena = (TextView)itemView.findViewById(R.id.napomena);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recyclerViewClickListener.recyclerViewItemClicked(this.getLayoutPosition());
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
