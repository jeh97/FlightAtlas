package cs371m.jh54765.flightatlas;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Jacob on 4/3/18.
 */

public class AirportRouteAdapter extends CursorAdapter {
    MainActivity activity;

    TextView text_destination;
    TextView text_airline;
    View container;

    public AirportRouteAdapter(MainActivity act, Cursor c, boolean autoRequery) {
        super(act,c,autoRequery);

        activity = act;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.row_airport_route_info,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        text_destination = view.findViewById(R.id.text_destination);
        text_airline = view.findViewById(R.id.text_airline);
        container = view;
        String origin = cursor.getString(cursor.getColumnIndexOrThrow("origin"));
        final String destination = cursor.getString(cursor.getColumnIndexOrThrow("destination"));
        String destCity = cursor.getString(cursor.getColumnIndexOrThrow("city"));
        String destCountry = cursor.getString(cursor.getColumnIndexOrThrow("country"));


//        Log.d("AirportRouteAdapter",String.format("Origin: %s, Dest: %s",origin,destination));

        Cursor newCursor = activity.db.getOperators(origin,destination);
        String operators = "";
        while(newCursor.moveToNext()) {
            String name = newCursor.getString(newCursor.getColumnIndexOrThrow("name"));
            operators += "\n"+name;
//            Log.d("AirportRouteAdapter",String.format("Added operator: %s",name));
        }
        newCursor.close();
        operators = operators.substring(1);

        text_destination.setText(String.format("%s (%s, %s)",destination,destCity, destCountry));
        text_airline.setText(operators);

        text_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open this airport info
                launchAirportInfoFragment(destination);
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
