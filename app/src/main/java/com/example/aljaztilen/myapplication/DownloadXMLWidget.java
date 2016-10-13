package com.example.aljaztilen.myapplication;


import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.AsyncTask;
import android.widget.RemoteViews;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class DownloadXMLWidget  extends AsyncTask<String, Void, String> {
    private String country = "country";
    private String city;
    private String temperature = "temperature";
    private String urlString;
    private String lastUpdate;
    private String lat;
    private String lon;
    private String curr_Weather;
    public static RemoteViews remoteViews;
    public static ComponentName widget;
    public static AppWidgetManager appWidgetManager;
    private String error = "";
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;

    public String getCity() {
        return city;
    }

    public String getTemperature(){
        if(temperature.contains(".")) {
            String tmp[] = String.valueOf(temperature).split("\\.");
            temperature = tmp[0] + "." + tmp[1];
        }
        return temperature;
    }

    public String getCurr_Weather(){
        return curr_Weather;
    }

    public DownloadXMLWidget(String urlString) {
        this.urlString = urlString;
    }

    @Override
    protected String doInBackground(String... params) {
        error = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            InputStream stream = conn.getInputStream();

            xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myparser = xmlFactoryObject.newPullParser();
            myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            myparser.setInput(stream, null);

            parseXMLAndStoreIt(myparser);
            stream.close();
        } catch (IOException e ) {
            e.printStackTrace();
            error = "Napaka";
            } catch (XmlPullParserException e ){
                error = "napaka";
            }
        return error;
    }

    protected void onPostExecute(String error){
        if(error == null && this.appWidgetManager != null) {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
            Date currentLocalTime = cal.getTime();
            DateFormat date = new SimpleDateFormat("HH:mm a");
            date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));
            String localTime = date.format(currentLocalTime);
            remoteViews.setTextViewText(R.id.textViewWidgetCity, this.getCity());
            this.changeIcon(this.getCurr_Weather());
            remoteViews.setTextViewText(R.id.stopinjeWidget, this.getTemperature() + "°C");
            remoteViews.setTextViewText(R.id.textViewApplikacija, localTime);
            appWidgetManager.updateAppWidget(widget, remoteViews); //updejtamo widget z vrednostmi
        }
    }

    public void parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        try {
            event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {//dokler ne pridemo do konca xml{
                String name=myParser.getName(); //pridobi ime značke
                switch (event){
                    case XmlPullParser.TEXT:
                        country = myParser.getText();
                        break;
                    case XmlPullParser.START_TAG:
                        if(name.equals("temperature")){ //če je ime značke temperature
                            temperature =  myParser.getAttributeValue(null,"value");
                        } else if(name.equals("coord")) {
                            lat = myParser.getAttributeValue(null, "lat");
                            lon = myParser.getAttributeValue(null, "lon");
                        }else if(name.equals("lastupdate")) {
                            lastUpdate = myParser.getAttributeValue(null, "value");
                        }  else if(name.equals("city")) {
                            city = myParser.getAttributeValue(null, "name");
                        } else if(name.equals("weather")) {
                            curr_Weather = myParser.getAttributeValue(null, "number");
                        }
                        break;
                }
                event = myParser.next();
            }
            parsingComplete = false;
        } catch (Exception  e) {
            error = "napaka";
        }
    }

    public void changeIcon(String value) {
        switch(value){
            case "800":
                remoteViews.setImageViewResource(R.id.vremeImageView, R.drawable.clear);
                break;
            case "801":
            case "802":
            case "803":
            case "804":
                remoteViews.setImageViewResource(R.id.vremeImageView, R.drawable.cloudy);
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
                remoteViews.setImageViewResource(R.id.vremeImageView, R.drawable.blowingsnow);
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
                remoteViews.setImageViewResource(R.id.vremeImageView, R.drawable.megla);
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
                remoteViews.setImageViewResource(R.id.vremeImageView, R.drawable.rain);
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
                remoteViews.setImageViewResource(R.id.vremeImageView, R.drawable.storm);
                break;

        }

    }
}
