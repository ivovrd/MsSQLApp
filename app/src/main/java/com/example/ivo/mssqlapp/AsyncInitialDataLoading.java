package com.example.ivo.mssqlapp;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ivo on 19.1.2016..
 */
public class AsyncInitialDataLoading extends AsyncTask<Void, Void, ResultSet>{
    Connection connect;
    Statement statement;
    DocPrevAdapter adapter;
    RecyclerView recyclerView;
    Context context;
    List<DocPrev> docPrevList;
    LinearLayout loadingCircle;
    int status;

    public AsyncInitialDataLoading (RecyclerView recyclerView, int status, Context context, LinearLayout loadingCircle){
        this.recyclerView = recyclerView;
        this.context = context;
        this.status = status;
        this.loadingCircle = loadingCircle;
    }

    @Override
    protected void onPreExecute() {
        loadingCircle.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected ResultSet doInBackground(Void... voids) {
        ResultSet result = null;
        SessionManager session = new SessionManager(context);

        try{
            connect = DatabaseConnection.Connect();
            if(connect == null){
                Log.e("FIRST_FRAGMENT_ERROR", "Can't load data from server");
            }else {
                statement = connect.createStatement();
                result = statement.executeQuery("SELECT TOP 10 UpravljanjeLjudskimResursima.Dokument.Sifra, UpravljanjeLjudskimResursima.Dokument.Datum, UpravljanjeLjudskimResursima.Dokument.DatumOd, UpravljanjeLjudskimResursima.Dokument.DatumDo, UpravljanjeLjudskimResursima.Dokument.Napomena, UpravljanjeLjudskimResursima.Dokument.Memo, Sifrarnici.Partner.Naziv, UpravljanjeLjudskimResursima.Dokument.OvlastenikId, UpravljanjeLjudskimResursima.Dokument.TrajanjeDana, UpravljanjeLjudskimResursima.Dokument.TrajanjeRadnihDana, UpravljanjeLjudskimResursima.Dokument.GodinaGodisnjegOdmora FROM UpravljanjeLjudskimResursima.Dokument INNER JOIN Sifrarnici.Partner ON UpravljanjeLjudskimResursima.Dokument.OvlastenikId=Sifrarnici.Partner.Id  WHERE UpravljanjeLjudskimResursima.Dokument.KorisnikId=" + session.getUserId() + " AND UpravljanjeLjudskimResursima.Dokument.Status=" + Integer.toString(status) + "ORDER BY UpravljanjeLjudskimResursima.Dokument.Id DESC");
            }
        }catch(SQLException e){
            Log.e("SQL error", e.getMessage());
        }

        if(result != null) {
            return result;
        }else{
            return null;
        }
    }

    @Override
    protected void onPostExecute(ResultSet result) {
        loadingCircle.setVisibility(View.GONE);
        try {
            while(result != null && result.next()){
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy.", Locale.ENGLISH);
                DocPrevManager.getInstance().setDocPrevs(result.getString(1), String.valueOf(dateFormat.format(result.getDate(2))), String.valueOf(dateFormat.format(result.getDate(3))), String.valueOf(dateFormat.format(result.getDate(4))), result.getString(5), result.getString(6), result.getString(7),  result.getInt(8), result.getInt(9), result.getInt(10), result.getInt(11));
            }

        } catch (SQLException e) {
            Log.e("SQL error", e.getMessage());
        }

        if(status == 0) {
            adapter = new DocPrevAdapter(DocPrevManager.getInstance().getDocPrevs(), recyclerView);
        } else{
            adapter = new NewDocPrevAdapter(DocPrevManager.getInstance().getDocPrevs(), recyclerView);
        }

        recyclerView.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                docPrevList = DocPrevManager.getInstance().getDocPrevs();
                docPrevList.add(null);
                adapter.notifyItemInserted(docPrevList.size() - 1);
                if (status == 0) {
                    new AsyncDbConnection(adapter, docPrevList, status, context).execute();
                } else {
                    new AsyncDbConnection(adapter, docPrevList, status, context).execute();
                }
            }
        });

        if(status == 0) {
            adapter.setOnRecyclerViewClickListener(new RecyclerViewClickListener() {
                @Override
                public void recyclerViewItemClicked(View view, int position) {
                    docPrevList = DocPrevManager.getInstance().getDocPrevs();
                    Bundle args = new Bundle();
                    Intent i = new Intent(context, MakeDocActivity.class);
                    i.putExtras(setBundleArguments(args, position));
                    context.startActivity(i);
                }
            });
        } else{
            adapter.setOnRecyclerViewClickListener(new RecyclerViewClickListener() {
                @Override
                public void recyclerViewItemClicked(View view, int position) {
                    docPrevList = DocPrevManager.getInstance().getDocPrevs();
                    Bundle args = new Bundle();
                    Fragment fragment = new NewFragment();
                    fragment.setArguments(setBundleArguments(args, position));
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack("FRAGMENT_TAG").commit();
                    ((AppCompatActivity) context).getSupportActionBar().setTitle("Pregled zahtjeva");
                }
            });
        }
        super.onPostExecute(result);
    }

    private Bundle setBundleArguments(Bundle args, int position){
        args.putString("DOC_SIFRA", docPrevList.get(position).sifra);
        args.putInt("DOC_OVLASTENIK_ID", docPrevList.get(position).ovlastenikId);
        args.putString("DOC_OVLASTENIK", docPrevList.get(position).ovlastenik);
        args.putInt("DOC_GODINA", docPrevList.get(position).godina);
        args.putString("DOC_DATUM", docPrevList.get(position).datum);
        args.putString("DOC_DATUM_OD", docPrevList.get(position).datumOd);
        args.putString("DOC_DATUM_DO", docPrevList.get(position).datumDo);
        args.putInt("DOC_DANI", docPrevList.get(position).dani);
        args.putInt("DOC_RADNI_DANI", docPrevList.get(position).radniDani);
        args.putString("DOC_NAPOMENA", docPrevList.get(position).napomena);
        args.putString("DOC_MEMO", docPrevList.get(position).memo);
        return args;
    }
}
