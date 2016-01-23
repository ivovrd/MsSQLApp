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
public class FirstFragment extends Fragment{
    private static final int DATA_TYPE = 0;
    LinearLayoutManager mLayoutManager;
    RecyclerView mRecyclerView;
    LinearLayout loadingCircle;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_fragment, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerList);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        loadingCircle = (LinearLayout)view.findViewById(R.id.linProgressBar);

        new AsyncInitialDataLoading(mRecyclerView, DATA_TYPE, getActivity(), loadingCircle).execute();
        return view;
    }

    @Override
    public void onDestroyView() {
        if(DocPrevManager.getInstance().getDocPrevs() != null)
            DocPrevManager.getInstance().getDocPrevs().clear();
        super.onDestroyView();
    }
}
