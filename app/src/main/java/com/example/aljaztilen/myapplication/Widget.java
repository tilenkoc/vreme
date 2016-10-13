package com.example.aljaztilen.myapplication;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

public class Widget extends AppWidgetProvider {
    public static String ACTION_UPDATE_CITY = "update_city";
    private static final String url1 = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static final String url2 = "&mode=xml&units=metric&appid=4246747296c49960a49a577a3022a1d5";

    @Override
    public void onUpdate(Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews;
        ComponentName widget;
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        widget = new ComponentName(context, Widget.class);

        Intent configIntent = new Intent(context, Main.class);
        configIntent.setAction(Intent.ACTION_MAIN);
        configIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widgetLayout, configPendingIntent);

        String mesto = PreferenceManager.getDefaultSharedPreferences(context).getString("mesto", "maribor");

        Intent intent = new Intent(context, Widget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //pending intent, da se widget refresha, ko kliknemo na textview(uro)
        remoteViews.setOnClickPendingIntent(R.id.textViewApplikacija, pendingIntent);

        String finalUrl = url1 + mesto + url2;
        DownloadXMLWidget xmlWidget = new DownloadXMLWidget(finalUrl);
        xmlWidget.remoteViews = remoteViews;
        xmlWidget.widget = widget;
        xmlWidget.appWidgetManager = appWidgetManager;
        xmlWidget.execute();
    }


}
