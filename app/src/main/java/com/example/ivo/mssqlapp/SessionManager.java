package com.example.ivo.mssqlapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

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
    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_FIRST_NAME = "name";
    public static final String KEY_LAST_NAME = "lastName";
    public static final String KEY_PARTNER_ID = "partnerId";
    public static final String KEY_USER_ID = "userId";

    public SessionManager(Context context){
        this.mContext = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void loginUser(String userName, String name, String lastName, String partnerId, String userId){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_FIRST_NAME, name);
        editor.putString(KEY_LAST_NAME, lastName);
        editor.putString(KEY_PARTNER_ID, partnerId);
        editor.putString(KEY_USER_ID, userId);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_USER_NAME, sharedPreferences.getString(KEY_USER_NAME, null));
        user.put(KEY_FIRST_NAME, sharedPreferences.getString(KEY_FIRST_NAME, null));
        user.put(KEY_LAST_NAME, sharedPreferences.getString(KEY_LAST_NAME, null));
        user.put(KEY_PARTNER_ID, sharedPreferences.getString(KEY_PARTNER_ID, null));
        user.put(KEY_USER_ID, sharedPreferences.getString(KEY_USER_ID, null));
        return user;
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
