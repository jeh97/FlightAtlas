package cs371m.jh54765.flightatlas;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jacob on 4/3/18.
 */

public class CityAdapter extends CursorAdapter {
    MainActivity activity;

    public CityAdapter(MainActivity act, Cursor c, boolean autoRequery) {
        super(act,c,autoRequery);

        activity = act;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.row_city_info,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView text_name = view.findViewById(R.id.text_cityRowName);
        TextView text_airports = view.findViewById(R.id.text_cityRowAirports);
        View container = view;

        final String city = cursor.getString(cursor.getColumnIndexOrThrow("city"));
        final String country = cursor.getString(cursor.getColumnIndexOrThrow("country"));
        Cursor cur = activity.db.getCityAirports(city,country);
//        cur.moveToFirst();
        String airports = "";
        while (cur.moveToNext()) {
            String airCode = cur.getString(cur.getColumnIndexOrThrow("IATA"));
            String name = cur.getString(cur.getColumnIndexOrThrow("name"));
            airports = String.format("%s\n%s (%s)",airports,airCode,name);
        }
        if (airports.length()>0) airports = airports.substring(1);
        text_airports.setText(airports);
        text_name.setText(String.format("%s, %s",city,country));

        text_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CityAdapter","Clicked city");
                launchCityInfoFragment(city,country);
            }
        });
    }
    public void launchCityInfoFragment(String city,String country) {
//        Log.d("AirportAdapter",String.format("launching info for %s",codeIATA));
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

//        fragmentManager.popBackStack();
        Bundle args = new Bundle();
        args.putString("city",city);
        args.putString("country",country);


        CityInfoFragment infoFragment = new CityInfoFragment();
        infoFragment.setArguments(args);
        fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.main_fragment));
        fragmentTransaction.add(R.id.main_fragment,infoFragment);
        fragmentTransaction.addToBackStack("city_info");
        fragmentTransaction.commit();

    }
}
