package com.example.aljaztilen.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;


public class NapovedActivity extends AppCompatActivity { // activity za 3-urno napoved
    //spremenljivke za generiranje url naslov
    private static final String url1 = "http://api.openweathermap.org/data/2.5/forecast?q=";
    private static final String url2 = "&mode=xml&appid=4246747296c49960a49a577a3022a1d5";

    private String mesto; //spremenljivka za pridobljeno mesto
    private Toolbar toolbar; // definira se orodno vrstica

    private String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.napoved_activity); //izbere se layout

        Bundle extras = getIntent().getExtras();
        toolbar = (Toolbar) findViewById(R.id.tool_bar); //uporabimo izbran toolbar '' tool_bar''
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//v orodni vrstici se omogči ''home button''

        DownloadXMLHour.activity = this;//pridobimo activity DownloadXMLHoour
        NapovedFragment fragment = (NapovedFragment) getSupportFragmentManager().findFragmentById(R.id.napoved_Fragment); //nastavimo support fragment

        if (extras != null) {
            value = extras.getString("DAN");
            mesto = extras.getString("MESTO");
            String dat[] = value.split("-");
            String datum = dat[2] + "." + dat[1] + "." + dat[0]; //pretvorba za dtum
            setTitle(mesto + ", " + datum);//ime v orodni vrstici se spremeni v ime kraja in datum
            DownloadXMLHour.day = value;
            fragment.setDan(value); //nastavimo dan
            fragment.naloziXML(url1+mesto+url2); // tvori se url
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

}





