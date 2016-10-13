package com.example.aljaztilen.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Preferences extends PreferenceActivity {

    private Context applicationContext = Main.getContextOfApplication(); //konekst aplikacija maina
    private SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(applicationContext); ////pridobimo shared preferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences); //nastavimo datoteko, kjer so shared preferences
        final String zacEnote = SP.getString("enote", "1");
        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent(); // dobimo layout, za shared preferences, kjer je list

        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.app_bar2, root, false); // izberemo orodno vrstico

        bar.setTitle("Nastavitve");
        bar.setTitleTextColor(Color.WHITE); // izberemo naslov za orodno vrstico in barvo črk

        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 //delovanje home buttona
                finish();
                String curr = SP.getString("enote","1");
                if(!zacEnote.equals(curr)){
                startActivity(new Intent(Main.contextOfApplication, Main.class));}
            }
        });
    }
}
