package com.example.ivo.mssqlapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private FragmentManager fragmentManager;
    private ActionBarDrawerToggle drawerToggle;
    private SessionManager session;
    private ActionBar actionBar;
    private boolean isConnected;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(!isConnected){
            showWarningDialog();
        }else {
            setContentView(R.layout.activity_main);

            session = new SessionManager(getApplicationContext());
            session.checkLogin(this);
            HashMap<String, String> user = session.getUserDetails();
            String userFirstName = user.get(SessionManager.KEY_FIRST_NAME);
            String userLastName = user.get(SessionManager.KEY_LAST_NAME);
            String userEmail = user.get(SessionManager.KEY_EMAIL);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            TextView userDetails = (TextView) findViewById(R.id.userName);
            TextView userDetailsEmail = (TextView) findViewById(R.id.userEmail);
            userDetails.setText(userFirstName + " " + userLastName);
            userDetailsEmail.setText(userEmail);
            drawerToggle = setupDrawerToggle();
            mDrawer.setDrawerListener(drawerToggle);

            navigationView = (NavigationView) findViewById(R.id.nvView);
            actionBar = getSupportActionBar();
            if(actionBar != null)
                actionBar.setDisplayHomeAsUpEnabled(true);

            fragmentManager = getSupportFragmentManager();
            fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                        if (navigationView.getMenu().getItem(0).isChecked())
                            actionBar.setTitle(navigationView.getMenu().getItem(0).getTitle());
                        else if (navigationView.getMenu().getItem(1).isChecked())
                            actionBar.setTitle(navigationView.getMenu().getItem(1).getTitle());
                            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
                    }
                }
            });

            FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), MakeDocActivity.class));
                }
            });
            setupDrawerContent(navigationView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()){
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                    onBackPressed();
                else
                    mDrawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_logout:
                session.logoutUser(this);
                return true;
        }
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstance){
        super.onPostCreate(savedInstance);
        if(isConnected)
            drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed()
    {
        if (mDrawer.isDrawerOpen(GravityCompat.START))
            mDrawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    public void selectDrawerItem(MenuItem menuItem){
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()){
            case R.id.nav_first_fragment:
                fragmentClass = FirstFragment.class;
                break;
            case R.id.nav_second_fragment:
                fragmentClass = SecondFragment.class;
                break;
            default:
                fragmentClass = FirstFragment.class;
        }

        try{
            fragment = (Fragment)fragmentClass.newInstance();
        } catch(Exception e){
            e.printStackTrace();
        }

        fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        menuItem.setChecked(true);
        actionBar.setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle(){
        return new ActionBarDrawerToggle(this, mDrawer, R.string.drawer_open, R.string.drawer_close);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isConnected) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                if (navigationView.getMenu().getItem(0).isChecked()) {
                    fragmentManager.beginTransaction().replace(R.id.flContent, new FirstFragment()).commit();
                    actionBar.setTitle(navigationView.getMenu().getItem(0).getTitle());
                } else {
                    fragmentManager.beginTransaction().replace(R.id.flContent, new SecondFragment()).commit();
                    actionBar.setTitle(navigationView.getMenu().getItem(1).getTitle());
                }
            }
        }
    }

    private void showWarningDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("Greška u povezivanju");
        alertDialogBuilder.setMessage("Provjerite podatkovnu vezu i pokušajte ponovo").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
