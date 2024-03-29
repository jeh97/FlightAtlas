package cs371m.jh54765.flightatlas;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Jacob on 4/3/18.
 */

public class AirportInfoFragment extends Fragment {
    private MainActivity mainActivity;
    private ListView listView;
    private Context context;
    private TextView text_name;
    private TextView text_code;
    private TextView text_coords;

    private TextView text_city;
    private String code;
    private String name;
    private LatLng coords;
    private String city;
    protected AirportInfoFragment fragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the root view and cache references to vital UI elements
        View v = inflater.inflate(R.layout.airportinfo_fragment, container, false);
        mainActivity = (MainActivity) getActivity();
        Bundle args = getArguments();
        this.code = args.getString("airport");
        context = getContext();
        mainActivity = (MainActivity) context;

        fragment = this;

        return v;
    }

    private void loadAirportInfo() {
        Cursor cursor = mainActivity.db.getAirportInfo(code);

        cursor.moveToFirst();
        this.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
        double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
        this.coords = new LatLng(latitude,longitude);
        String city = cursor.getString(cursor.getColumnIndexOrThrow("city"));
        String country = cursor.getString(cursor.getColumnIndexOrThrow("country"));
        this.city = String.format("%s, %s",city,country);

        this.text_name.setText(name);
        this.text_code.setText(code);
        this.text_coords.setText(String.format("%f, %f",latitude,longitude));
        this.text_city.setText(this.city);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.listView = getView().findViewById(R.id.listView_routes);

        this.text_name = getView().findViewById(R.id.textView_name);
        this.text_code = getView().findViewById(R.id.textView_code);
        this.text_coords = getView().findViewById(R.id.textView_coords);
        this.text_city = getView().findViewById(R.id.textView_city);

        loadAirportInfo();

        this.text_coords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mainActivity.toMapFragment(code);
            }
        });


        loadRoutes();


    }

    private void loadRoutes() {
        Cursor cursor = mainActivity.db.getAirportRoutes(code);

        AirportRouteAdapter adapter = new AirportRouteAdapter(mainActivity,cursor,false);
        this.listView.setAdapter(adapter);

    }

}
