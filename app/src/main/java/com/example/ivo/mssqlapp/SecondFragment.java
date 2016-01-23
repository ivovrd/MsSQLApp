package com.example.ivo.mssqlapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Ivo on 1.7.2015..
 */
public class SecondFragment extends Fragment {
    private static final int DATA_TYPE = 1;
    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    LinearLayout loadingCircle;

    @Override
    public void onCreate(Bundle savedInstanceState){ super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.second_fragment, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerList2);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        loadingCircle = (LinearLayout)view.findViewById(R.id.linProgressBarr);

        new AsyncInitialDataLoading(recyclerView, DATA_TYPE, getActivity(), loadingCircle).execute();
        return view;
    }

    @Override
    public void onDestroyView() {
        if(DocPrevManager.getInstance().getDocPrevs() != null)
            DocPrevManager.getInstance().getDocPrevs().clear();
        super.onDestroyView();
    }
}
