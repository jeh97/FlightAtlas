package cs371m.jh54765.flightatlas;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class CityAirportAdapter extends CursorAdapter {
    MainActivity activity;

    TextView text_code;
    TextView text_name;
    TextView text_destinations;
    View container;

    public CityAirportAdapter(MainActivity act, Cursor c, boolean autoRequery) {
        super(act,c,autoRequery);

        activity = act;
        Log.d("CityAirportAdapter","Constructing");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        Log.d("CityAirportAdapter","New view");
        return LayoutInflater.from(context).inflate(R.layout.row_city_airport_info,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d("CityAirportAdapter","Binding view");
        text_destinations = view.findViewById(R.id.textView_cityAirportDestinationCount);
        text_name = view.findViewById(R.id.textView_cityAirportName);
        text_code = view.findViewById(R.id.textView_cityAirportCode);
        container = view;
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        final String code = cursor.getString(cursor.getColumnIndexOrThrow("IATA"));
        int destinations = cursor.getInt(cursor.getColumnIndexOrThrow("destinations"));


        text_destinations.setText(String.format("%d",destinations));
        text_name.setText(name);
        text_code.setText(code);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open this airport info
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
