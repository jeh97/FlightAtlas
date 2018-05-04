package cs371m.jh54765.flightatlas;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class CityInfoFragment extends Fragment {
    private MainActivity mainActivity;
    private ListView listView;
    private Context context;
    private TextView text_city;
    private TextView text_country;
    private TextView text_coords;

    private String city;
    private String country;
    private LatLng coords;
    protected CityInfoFragment fragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the root view and cache references to vital UI elements
        View v = inflater.inflate(R.layout.cityinfo_fragment, container, false);
        mainActivity = (MainActivity) getActivity();
        Bundle args = getArguments();
        this.city = args.getString("city");
        this.country = args.getString("country");
        context = getContext();
        mainActivity = (MainActivity) context;

        fragment = this;

        return v;
    }


    private void loadCityInfo() {
        Cursor cursor = mainActivity.db.getCityInfo(city,country);

        cursor.moveToFirst();
        this.city = cursor.getString(cursor.getColumnIndexOrThrow("city"));
        double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
        double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
        this.coords = new LatLng(latitude,longitude);
        String city = cursor.getString(cursor.getColumnIndexOrThrow("city"));
        String country = cursor.getString(cursor.getColumnIndexOrThrow("country"));

        this.text_city.setText(city);
        this.text_coords.setText(String.format("%f, %f",latitude,longitude));
        this.text_country.setText(country);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.listView = getView().findViewById(R.id.listView_airports);

        this.text_city = getView().findViewById(R.id.textView_cityCity);
        this.text_country = getView().findViewById(R.id.textView_cityCountry);
        this.text_coords = getView().findViewById(R.id.textView_cityCoords);

        loadCityInfo();

        this.text_coords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("UI Click",String.format("Clicked city %s, %s",city,country));
                mainActivity.toMapFragmentCity(city,country);
            }
        });


        loadAirports();


    }

    private void loadAirports() {
        Cursor cursor = mainActivity.db.getCityAirportsAndDestCount(city,country);

        CityAirportAdapter adapter = new CityAirportAdapter(mainActivity,cursor,false);
        this.listView.setAdapter(adapter);

    }

}
