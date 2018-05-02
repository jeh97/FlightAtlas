package cs371m.jh54765.flightatlas;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jacob on 4/3/18.
 */

public class AirlineAdapter extends CursorAdapter {
    MainActivity activity;

    public AirlineAdapter(MainActivity act, Cursor c, boolean autoRequery) {
        super(act,c,autoRequery);

        activity = act;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.row_airline_info,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView text_name = view.findViewById(R.id.text_name);
        TextView text_country = view.findViewById(R.id.text_country);
        CheckBox checkBox_enabled = view.findViewById(R.id.checkBox_airlineEnabled);
        View container = view;
        final String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        final String country = cursor.getString(cursor.getColumnIndexOrThrow("country"));
        final String code = cursor.getString(cursor.getColumnIndexOrThrow("IATA"));
        final boolean enabled = (cursor.getInt(cursor.getColumnIndexOrThrow("enabled")) != 0)?true:false;


        checkBox_enabled.setChecked(enabled);

        checkBox_enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                activity.db.setAirlineEnabled(code,b);
            }
        });

        text_name.setTextColor(Color.BLACK);
        text_name.setText(String.format("%s (%s)",name,code));
        text_country.setTextColor(Color.BLACK);
        text_country.setText(country);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("UI Interaction","Clicked airline");
                activity.toMapFragmentAirline(code);
                Log.d("UI Interaction","Loaded Airline");
//                launchAirlineInfoFragment(code);
            }
        });
    }

    public void launchAirlineInfoFragment(String codeIATA) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

//        fragmentManager.popBackStack();
        Bundle args = new Bundle();
        args.putString("airline",codeIATA);

//        AirlineInfoFragment infoFragment = new AirportInfoFragment();
//        infoFragment.setArguments(args);
//        fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.main_fragment));
//        fragmentTransaction.add(R.id.main_fragment,infoFragment);
//        fragmentTransaction.addToBackStack("airport_info");
//        fragmentTransaction.commit();
    }

}

