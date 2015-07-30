package com.example.ivo.mssqlapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by Ivo on 30.7.2015..
 */
public class SessionManager {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context mContext;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "UserLoginData";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "userName";

    public SessionManager(Context context){
        this.mContext = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void loginUser(String userName){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, userName);
        editor.commit();
    }

    public String getUserDetails(){
        return sharedPreferences.getString(KEY_NAME, null);
    }

    public void checkLogin(){
        if(!this.isLoggedIn()){
            Intent i = new Intent(mContext, Login.class);
            //closing all activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //add new flag to start activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
        }
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();

        Intent i = new Intent(mContext, Login.class);
        //closing all activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //add new flag to start activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }

    public boolean isLoggedIn(){
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }
}
