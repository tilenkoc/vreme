package com.example.aljaztilen.myapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class MyArrayAdapter extends ArrayAdapter<Vreme_napoved> {

    //adapter za Vreme_napoved , 5-dnevna napoved
    NotificationCompat.Builder notification;
    private ArrayList<Vreme_napoved> napoved;
    private Context context;
    private ImageView ikona;
    //konstruktor, ki pridobi vrednosti za adapter
    public MyArrayAdapter(Context context, ArrayList<Vreme_napoved> napoved) {
        super(context, 0, napoved);
        this.napoved = napoved; //5-dnevna napoved
        this.context = context; // kontekst 5-dnevne napovedi
    }

    public void setData(ArrayList<Vreme_napoved> napoved) {
        this.napoved.clear();
        this.napoved = napoved;
        this.notifyDataSetChanged();//da se je dataset spremenil
    }

    public Vreme_napoved getItem(int position) {
        return napoved.get(position);
    } // getter tipa Vreme_napoved za pridobitev vrednosti iz arraylist

    public String getDatum(int pos) {
        return napoved.get(pos).getDatum();
    } // getter tipa Vreme_napoved za pridobitev vrednosti za datum

    public String getCity(int pos) {
        return napoved.get(pos).getMesto();
    }// getter tipa Vreme_napoved za pridobitev vrednosti za mesto

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // pridobi item za to poticijo
        Vreme_napoved vreme = (Vreme_napoved) getItem(position);

        //preveri če je obstoječi view ponovno uporabljen
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.napoved_layout, parent, false); //pridobimo layout kjer se izpiše 5-dnevna napoved
        }
        
        TextView textDan = (TextView) convertView.findViewById(R.id.dan);//izpis za dan
        textDan.setText(vreme.getDay());
        
        Context applicationContext = Main.getContextOfApplication(); //pridobi se konetkst maina
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        String izbiraEnote = SP.getString("enote", "1"); // pridobi se vrednost iz shared preferences za spreminajne enot

        String tempmin = vreme.getTemperaturamin(); // pridobi se maksimalna in minimalna temperatura
        String tempmax = vreme.getTemperaturamax();


        TextView textTemperatura = (TextView) convertView.findViewById(R.id.temperatura); // izpis za temepraturo

        if(izbiraEnote.equals("1")) { // izbira enot, pretvorba med fahrnehieti in celziji
            Double x = Double.parseDouble(tempmin);
            Double y = Double.parseDouble(tempmax);

            String temmin = String.valueOf(x);
            String temmax = String.valueOf(y);

            temmin = String.format("%.1f", x);
            temmax = String.format("%.1f",y);

            textTemperatura.setText(temmin + "/" + temmax + "°C");

        } else if(izbiraEnote.equals("2")) {

            Double d = Double.parseDouble(tempmin);
            Double f = Double.parseDouble(tempmax);

            d = d * 1.8 + 32;
            f = f*1.8+32;

            String tem3 = String.valueOf(d);
            tem3 = String.format("%.1f", d);

            String tem4 = String.valueOf(f);
            tem4 = String.format("%.1f", f);
            textTemperatura.setText(tem3 + "/" + tem4 + "°F");
        }

        ikona = (ImageView) convertView.findViewById(R.id.vreme); // definiramo izpis za ikono
        String value = napoved.get(position).getVreme();
        changeIcon(value); // id za ikono pošljemo v to funkcijo

        return convertView;
    }

    public void changeIcon(String value) { //spreminjanje ikone glede na pridobljen id stanje vremena
        switch(value){
            case "800":
                ikona.setImageResource(R.drawable.blustery);
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
            case "900":
            case "901":
            case "902":
            case "903":
            case "904":
                //če je vreme ekstrem (orkan, hudo neurje, cunami, itd) se prikaže notification
                ikona.setImageResource(R.drawable.blustery);
                notification = new NotificationCompat.Builder(context);
                notification.setAutoCancel(true);
                notification.setSmallIcon(R.drawable.ikona);
                notification.setTicker("To je ticker");
                notification.setWhen(System.currentTimeMillis());
                notification.setContentTitle("Ekstrem!");
                notification.setContentText("Ekstrem v tem tednu!");
                Intent intent = new Intent(context, Main.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                notification.setContentIntent(pendingIntent);
                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                nm.notify(1, notification.build());

            default:
                ikona.setImageResource(R.drawable.compass);
        }

    }
}
