package com.example.ivo.mssqlapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
    private DocPrevAdapter mAdapter;
    private List<DocPrev> docPrevList;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Connection connect;
        Statement statement;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy.", Locale.ENGLISH);

        try{
            connect = DatabaseConnection.Connect();
            statement = connect.createStatement();
            ResultSet result = statement.executeQuery("select top 10 Sifra, Datum, Napomena from UpravljanjeLjudskimResursima.Dokument");

            while(result.next()){
                DocPrevManager.getInstance().setDocPrevs(result.getString("Sifra"), String.valueOf(dateFormat.format(result.getDate("Datum"))), result.getString("Napomena"));
            }

        }catch(SQLException e){
            Log.e("SQL error", e.getMessage());
        }

        View view = inflater.inflate(R.layout.first_fragment, container, false);

        RecyclerView mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerList);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new DocPrevAdapter(DocPrevManager.getInstance().getDocPrevs(), mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                docPrevList = DocPrevManager.getInstance().getDocPrevs();
                docPrevList.add(null);
                mAdapter.notifyItemInserted(docPrevList.size() - 1);
                new AsyncDbConnection(mAdapter, docPrevList).execute();
            }
        });

        mAdapter.setOnRecyclerViewClickListener(new RecyclerViewClickListener() {
            @Override
            public void recyclerViewItemClicked(View view, int position) {

                docPrevList = DocPrevManager.getInstance().getDocPrevs();
                Bundle args = new Bundle();
                args.putString("DOC_SIFRA", docPrevList.get(position).sifra);

                Fragment fragment = new NewFragment();
                fragment.setArguments(args);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack("firstFragment").commit();
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Pregled dokumenta");
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        if(DocPrevManager.getInstance().getDocPrevs() != null)
            DocPrevManager.getInstance().getDocPrevs().clear();
        super.onDestroyView();
    }
}
