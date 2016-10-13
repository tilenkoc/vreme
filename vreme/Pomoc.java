package com.example.aljaztilen.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

public class Pomoc extends AppCompatActivity {
    private Toolbar toolbar; //activity za pomoč

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomoc);
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // izberemo toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); // omogočimo ''home button''
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Pomoč"); // izberemo naslov orodne vrstice
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // delovanje home buttona
        finish();// ko kliknemo home button se odpre main
        return true;
    }




}
