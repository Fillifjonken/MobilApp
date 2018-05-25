package com.example.marcusjohansson.homeexam;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences preferences;

    public Session(Context ctx) {
        preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
    }
    public void setDate (String date) {
        preferences.edit().putString("date", date).commit();
    }
    public void setEmail (String email) {
        preferences.edit().putString("email", email).commit();
    }
    public String getEmail(){
        String str = preferences.getString("email","");
        return str;
    }
    public String getDate(){
        String str = preferences.getString("date","");
        return str;
    }
}
