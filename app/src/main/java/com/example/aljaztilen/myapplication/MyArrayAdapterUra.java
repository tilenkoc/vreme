package com.example.aljaztilen.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;


public class MyArrayAdapterUra extends ArrayAdapter<Vreme_napoved>{ //adapter za Vreme_napoved, 3-urna napoved
    private ArrayList<Vreme_napoved> napoved;
    private Context context;
    private ImageView ikona;

    //konstruktor, ki pridobi vrednosti za adapter
    public MyArrayAdapterUra(Context context, ArrayList<Vreme_napoved> napoved) {
        super(context, 0, napoved);
        this.napoved = napoved;//3-urna napoved
        this.context = context;  // kontekst 3-urne napovedi
    }

    public ArrayList<Vreme_napoved> napoved(){
        return this.napoved;
    }

    public void setData(ArrayList<Vreme_napoved> napoved) {
        this.napoved.clear();
        this.napoved = napoved;
        this.notifyDataSetChanged();//da se je dataset spremenil
    }

    public Vreme_napoved getItem(int position) {
        return napoved.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Vreme_napoved vreme = (Vreme_napoved) getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.napoved_layout, parent, false);
        }
        TextView textDan = (TextView) convertView.findViewById(R.id.dan);
        textDan.setText(vreme.getUra());
        TextView textTemperatura = (TextView) convertView.findViewById(R.id.temperatura);// izpis za temepraturo

        Context applicationContext = Main.getContextOfApplication();
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        String izbiraEnote = SP.getString("enote", "1");// pridobi se vrednost iz shared preferences za spreminajne enot
        String temp = vreme.getTemperatura(); //pridobi temperaturo

        if(izbiraEnote.equals("1")) {// izbira enot, pretvorba med fahrnehieti in celziji
            Double x = Double.parseDouble(temp);
            String tem1 = String.valueOf(x);
            tem1 = String.format("%.1f", x);
            textTemperatura.setText(tem1 + "°C");
        }else if(izbiraEnote.equals("2")){
            Double d = Double.parseDouble(temp);
            d = d * 1.8 + 32;
            String tem2 = String.valueOf(d);
            tem2 = String.format("%.1f", d);
            textTemperatura.setText(tem2 + "°F");
        }

        ikona = (ImageView) convertView.findViewById(R.id.vreme);// definiramo izpis za ikono
        String value = napoved.get(position).getVreme();
        changeIcon(value);
        return convertView;
    }


    public void changeIcon(String value) { //spreminjanje ikone glede na pridobljen id stanje vremena
        switch(value){
            case "800":
                ikona.setImageResource(R.drawable.clear);
                break;
            case "801":
            case "802":
            case "803":
            case "804":
                ikona.setImageResource(R.drawable.cloudy);
                break;
            case "600":
            case "601":
            case "602":
            case "611":
            case "612":
            case "615":
            case "616":
            case "620":
            case "621":
            case "622":
                ikona.setImageResource(R.drawable.blowingsnow);
                break;
            case "500":
            case "501":
            case "502":
            case "503":
            case "504":
            case "511":
            case "520":
            case "521":
            case "522":
            case "531":
                ikona.setImageResource(R.drawable.rain);
                break;
            case "200":
            case "201":
            case "202":
            case "212":
            case "210":
            case "211":
            case "232":
            case "221":
            case "230":
            case "231":
                ikona.setImageResource(R.drawable.storm);
                break;
            default:
                ikona.setImageResource(R.drawable.compass);

        }

    }
}
