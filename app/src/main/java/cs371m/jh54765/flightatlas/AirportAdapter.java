package cs371m.jh54765.flightatlas;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jacob on 4/2/18.
 */

public class AirportAdapter extends CursorAdapter {
    MainActivity activity;

//    TextView text_name;
//    TextView text_code;
//    View container;
//    String name;
//    String code;

    public AirportAdapter(MainActivity act, Cursor c, boolean autoRequery) {
        super(act, c, autoRequery);

        activity = act;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.row_airport_info, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView text_name = view.findViewById(R.id.text_airportRowName);
        TextView text_code = view.findViewById(R.id.text_airportRowCode);
        TextView text_city = view.findViewById(R.id.text_airportRowCity);
        View container = view;
        final String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        final String code = cursor.getString(cursor.getColumnIndexOrThrow("IATA"));
        String city = cursor.getString(cursor.getColumnIndexOrThrow("city"));
        String country = cursor.getString(cursor.getColumnIndexOrThrow("country"));

//        Log.d("AirportAdapter",String.format("name is %s, code is %s",name,code));
        text_name.setText(name);
        text_code.setText(code);
        text_city.setText(String.format("(%s, %s)",city,country));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("AirportAdapter",String.format("Clicked %s",code));
                launchAirportInfoFragment(code);
            }
        });
    }
    public void launchAirportInfoFragment(String codeIATA) {
//        Log.d("AirportAdapter",String.format("launching info for %s",codeIATA));
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

//        fragmentManager.popBackStack();
        Bundle args = new Bundle();
        args.putString("airport",codeIATA);

        AirportInfoFragment infoFragment = new AirportInfoFragment();
        infoFragment.setArguments(args);
        fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.main_fragment));
        fragmentTransaction.add(R.id.main_fragment,infoFragment);
        fragmentTransaction.addToBackStack("airport_info");
        fragmentTransaction.commit();

    }


}
