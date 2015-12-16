package com.example.ivo.mssqlapp;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
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
public class SecondFragment extends Fragment {
    private DocPrevAdapter mAdapter;
    private List<DocPrev> docPrevList;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SessionManager session = new SessionManager(getActivity());
        Connection connect;
        Statement statement;

        try{
            connect = DatabaseConnection.Connect();
            statement = connect.createStatement();
            ResultSet result = statement.executeQuery("SELECT TOP 10 Sifra, Datum, Napomena FROM UpravljanjeLjudskimResursima.Dokument WHERE KorisnikId=" + session.getUserId());

            while(result.next()){
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy.", Locale.ENGLISH);
                DocPrevManager.getInstance().setDocPrevs(result.getString("Sifra"), String.valueOf(dateFormat.format(result.getDate("Datum"))), result.getString("Napomena"));
            }
        }catch(SQLException e){
            Log.e("SQL error", e.getMessage());
        }

        View view =  inflater.inflate(R.layout.second_fragment, container, false);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recyclerList2);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        mAdapter = new NewContactAdapter(DocPrevManager.getInstance().getDocPrevs(),recyclerView);
        recyclerView.setAdapter(mAdapter);

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


                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack("FRAGMENT_TAG").commit();
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Pregled dokumeta");
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
