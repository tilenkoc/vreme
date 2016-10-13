package com.example.aljaztilen.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class Trend extends AppCompatActivity {


    Toolbar toolbar; // inicializiram orodno vrstico
    ImageView img1,img2,img3; // inicializiram ikona za trend
    View v; // inicializiram view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend);

        img1 = (ImageView) findViewById(R.id.zjutraj);// definitam ikone
        img2 = (ImageView)findViewById(R.id.opoldan);
        img3 = (ImageView)findViewById(R.id.zvečer);

        DownloadXMLForecast night = new DownloadXMLForecast(); //narejeni objekti za pridobivanje vrednosti iz DownloadXMLForecat
        DownloadXMLForecast eve = new DownloadXMLForecast();
        DownloadXMLForecast day = new DownloadXMLForecast();
        DownloadXMLForecast morn= new DownloadXMLForecast();

        //vrednosti za izačun enačbe linearne regresije
        double sumpr1, sumpr2, sumpr3;

        double a1,a2,b1,b2,c1,c2,d1,d2;



        double sum1,sum2,sum3,sum4;

        double kvadratn,kvadratd,kvadrate,kvadratm;

        // enačba za linearno regresijo

        sum1 = night.sum1;
        sum2 = day.sum2;
        sum3 = morn.sum3;
        sum4 = eve.sum4;

        sumpr1 = night.sumpr1;
        sumpr2 =night.sumpr2;
        sumpr3 = night.sumpr3;

        kvadratd = night.kvadratd;
        kvadratn = night.kvadratn;
        kvadrate = night.kvadrate;
        kvadratm = night.kvadratm;

        double enacba1,enacba2,enacba3;

        System.out.println(sum1 +" ss");


        a1 = ((sum2*kvadratm)-(sum3*sumpr1))/((5*kvadratm)-(sum1*sum1));
        a2 = (5*(sumpr1)-(sum1*sum2))/((5*kvadratm)-(sum1*sum1));

        b1=((sum3*kvadratd)-(sum2*sumpr2))/(5*kvadratd-(sum2*sum2));
        b2=((5*sumpr2)-(sum2*sum3))/((5*kvadratd)-(sum2*sum2));

        c1=((sum4*kvadrate)-(sum3*sumpr3))/((5*kvadrate)-(sum3*sum3));
        c2=((5*sumpr3)-(sum3*sum4))/((5*kvadrate)-(sum3*sum3));



        enacba1 = a1+(6*a2);

        enacba2 = b1+(6*b2);

        enacba3 = c1 +(6*c2);

        System.out.println("enacba1 " + enacba1 + "enacba2 " + enacba2 + "enacba3" + enacba3);//testni izpis

//spreminjanje ikone glede na vrednosti trenda, narašča je pozitiven , pada če je negativen
        if(enacba1>0){
            img1.setImageResource(R.drawable.up);
        }else{
            img1.setImageResource(R.drawable.down);
        }

        if(enacba2>0){
            img2.setImageResource(R.drawable.up);
        }else{
            img2.setImageResource(R.drawable.down);
        }

        if(enacba3>0){
            img3.setImageResource(R.drawable.up);
        }else{
            img3.setImageResource(R.drawable.down);
        }

        toolbar = (Toolbar) findViewById(R.id.tool_bar); //izbremo orodno vrstico
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);//omogočimo gumb nazaj
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Trend"); //naslov je trend v activity trend


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent myIntent = new Intent(getApplicationContext(), Main.class); //z gumbom nazaj se vrnemo na activity Main.class
        startActivityForResult(myIntent, 0);

        return true;
    }
}
