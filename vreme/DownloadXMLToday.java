package com.example.aljaztilen.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DownloadXMLToday extends AsyncTask<String, Void, String> {
    //spremenljivke za izpis vremena
    private String country = "country";
    private String city;
    private String temperature = "temperature";
    private String humidity;
    private String urlString;
    private String lastUpdate;
    private String windSpeed;
    private String curr_Weather;
    private String sunRise;
    private String sunSet;
    public static String okMesto;
    public static ComponentName widget;
    private ImageView ikona;
    private TextView temperatura;
    private TextView naz_posodobljeno;
    private TextView mesto;
    public static View v;
    public static String error = null;
    public static Activity activity;
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;
    private ProgressDialog pgDialog;
    private String lon;
    private String lat;

    //getter za državo
    public String getCountry(){
        return country;
    }

    //getter za mesto
    public String getCity() {
        return city;
    }

    //getter za temperaturo
    public String getTemperature(){
        if(temperature.contains(".")) {
            String tmp[] = String.valueOf(temperature).split("\\.");
            temperature = tmp[0] + "." + tmp[1];
        }
        return temperature;
    }

    //getter za zadnjo posodobitev vremena
    public String getLastUpdate() {
        return lastUpdate;
    }

    //getter za trenutno vreme
    public String getCurr_Weather(){
        return curr_Weather;
    }

    public DownloadXMLToday(String urlString) {
        this.urlString = urlString;
    }

    @Override
    protected void onPreExecute() {
        // v primeru čakanja, da aplikacija pridobi podatke
        pgDialog = ProgressDialog.show(activity, "Prosimo počakajte", "Pridobivam podatke...", true);
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        error = null;
        try {
            //vzpostavitev povezave
            URL url = new URL(urlString);//url pridobimo iz main
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();// povezava se vzpostavi

            InputStream stream = conn.getInputStream(); //ustvari se tok vhodnih podatkov
            //implementira se Xml Pull parser, branje xml
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myparser = xmlFactoryObject.newPullParser();//novi pullparser
            myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);//kliče se generalno vedenje parserja, kot je npr. procesiranje namespace-ov
            myparser.setInput(stream, null);//nastavimo vhod
            parseXMLAndStoreIt(myparser);
            stream.close(); //zapremo stream
        }
        catch (IOException e ) {
            e.printStackTrace();
            error = "napaka";
        } catch (XmlPullParserException e ){
            error = "napaka";
        }
        return error;
    }

    protected void onPostExecute(String error){
        pgDialog.dismiss();
        Context applicationContext = Main.getContextOfApplication();
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        String izbiraEnote = SP.getString("enote", "1"); //pridobimo kontekst iz maina in vrednost shared preferences , za izbiro enote pri temperaturi

        if(error == null) { //će ni napake se naloži to
            naloziView();
            //izpišemo mesto in državo
            mesto.setText(this.getCity() + ", " + this.getCountry());
            //pretvorba iz fahrenheitov ali celzijev, odvisno od shared preferences
            String tempp1 = getTemperature();

            if(izbiraEnote.equals("1")){
                Double x = Double.parseDouble(tempp1);
                String tem1 = String.valueOf(x);
                tem1 = String.format("%.1f", x);
                temperatura.setText(tem1 + "°C");
            }else if(izbiraEnote.equals("2")) {
                Double d = Double.parseDouble(tempp1);
                d = d * 1.8 + 32;
                String tem2 = String.valueOf(d);
                tem2 = String.format("%.1f", d);
                temperatura.setText(tem2 + "°F");
            }

            //pretvorba datuma in ure v format h:MM in dd.MM.yyyy
            String temp = this.getLastUpdate().split("T")[1];
            SimpleDateFormat format3 = new SimpleDateFormat("hh:MM:ss");
            Date dt2 = null;
            try {
                dt2 = format3.parse(temp);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            DateFormat format4 = new SimpleDateFormat("h:MM");
            String zadnje = format4.format(dt2);
            naz_posodobljeno.setText("Posodobljeno: " + zadnje); //izpis nazadnje pridobljenih podatkov
            changeIcon(this.getCurr_Weather()); //glede na to se spremeni ikona
        } else {
            PreferenceManager.getDefaultSharedPreferences(activity).edit().putString("mesto", this.okMesto).commit();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setTitle("Napaka"); // v primeru, da ni internetne povezave se izpiše dialog
            dialog.setMessage("To mesto ne obstaja!");
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }
    }

    public void parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        try {
            event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT)//dokler ne pridemo do konca xml
            {
                String name=myParser.getName(); //pridobi ime značke
               //pridobili smo parser
                switch (event){
                    case XmlPullParser.TEXT:
                        country = myParser.getText();
                        break;
                    case XmlPullParser.START_TAG:
                        //kličemo ime značke in beremo izbrane vrednosti
                        if(name.equals("temperature")){ //če je ime značke temperature
                            temperature =  myParser.getAttributeValue(null,"value");
                            //pridobi vrednost atributa value značke temperature
                        } else if(name.equals("coord")) {
                            lat = myParser.getAttributeValue(null, "lat");
                            lon = myParser.getAttributeValue(null, "lon");

                        } else if(name.equals("sun")){
                            //pridobi vrednost atributa value sun
                            String tempRise = myParser.getAttributeValue(null, "rise");
                            String tempSet = myParser.getAttributeValue(null, "set");
                            sunRise = tempRise.split("T")[1];
                            sunSet = tempSet.split("T")[1];
                        } else if(name.equals("humidity")) {
                            //pridobi vrednost atributa value značke humidity
                            humidity = myParser.getAttributeValue(null, "value");
                        } else if(name.equals("lastupdate")) {
                            //pridobi vrednost atributa value značke lastupdate
                            lastUpdate = myParser.getAttributeValue(null, "value");
                        } else if(name.equals("speed")) {
                            //pridobi vrednost atributa value značke speed
                            windSpeed = myParser.getAttributeValue(null, "value");
                        } else if(name.equals("city")) {
                            //pridobi vrednost atributa value značke city
                            city = myParser.getAttributeValue(null, "name");
                        } else if(name.equals("weather")) {
                            //pridobi vrednost atributa value značke weather
                            curr_Weather = myParser.getAttributeValue(null, "number");
                        }
                        break;
                }
                event = myParser.next();
            }
            parsingComplete = false;
        }
        catch (Exception  e) {
            this.error = "napaka";
        }
    }

    public void naloziView() {
        //inicializiramo textview-e in imagevieve, kjer se izpišejo podatki
        temperatura = (TextView) v.findViewById(R.id.temperatura);
        naz_posodobljeno = (TextView) v.findViewById(R.id.naz_posodobljeno);
        mesto = (TextView) v.findViewById(R.id.mesto);
        ikona = (ImageView) v.findViewById(R.id.ikona);
    }


    public void changeIcon(String value) {
        System.out.println("vreme " + value);
        //spreminjanje ikone, glede na id vremene , ki ga pridobimo
        switch(value){
            case "800":
                ikona.setImageResource(R.drawable.clear); //ikone izbiramo iz mape drawable
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
            case "701":
            case "711":
            case "721":
            case "731":
            case "741":
            case "751":
            case "761":
            case "762":
            case "771":
            case "781":
                ikona.setImageResource(R.drawable.megla);
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
        }
    }
}
