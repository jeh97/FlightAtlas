package cs371m.jh54765.flightatlas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import java.util.Iterator;

/**
 * Created by Jacob on 4/3/18.
 */

public class ListFragment extends Fragment {
    MainActivity mainActivity;
    private RecyclerView recyclerView;
    private TextView title;
    private Button citiesButton;
    private Button airlinesButton;
    private Button airportsButton;
    protected LinearLayoutManager rv_layout_mgr;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the root view and cache references to vital UI elements
        View v = inflater.inflate(R.layout.list_fragment, container, false);
        mainActivity = (MainActivity) getActivity();


        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.recyclerView = getView().findViewById(R.id.recyclerView);
        rv_layout_mgr = new LinearLayoutManager(getContext());
        this.recyclerView.setLayoutManager(rv_layout_mgr);
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
        this.title = getView().findViewById(R.id.textView_title);
        initRecyclerView();
        citiesButton = getView().findViewById(R.id.button_cities);
        citiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCities();
            }
        });

        airlinesButton = getView().findViewById(R.id.button_airlines);
        airlinesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAirlines();
            }
        });
        airportsButton = getView().findViewById(R.id.button_airports);
        airportsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAirports();
            }
        });
    }

    private void initRecyclerView() {
        loadAirports();

    }

    private void loadAirports() {
        title.setText("Airports");
        AirportAdapter adapter = new AirportAdapter(recyclerView,getContext());

        Iterator<Airport> iter = mainActivity.airports.values().iterator();
        while (iter.hasNext()) {
            Airport next = iter.next();
            if (!mainActivity.globals.ONLY_SHOW_COMMERCIAL_AIRPORTS || next.hasRoutes()) adapter.add(next);
        }
        this.recyclerView.setAdapter(adapter);


    }

    private void loadCities() {
        title.setText("Cities");
        CityAdapter adapter = new CityAdapter(recyclerView,getContext());
        Iterator<Metro> iter = mainActivity.cities.values().iterator();
        while (iter.hasNext()) {
            Metro next = iter.next();
            if (!mainActivity.globals.ONLY_SHOW_COMMERCIAL_AIRPORTS || next.hasCommercialAirport()) adapter.add(next);
        }
        this.recyclerView.setAdapter(adapter);

    }

    private void loadAirlines() {
        title.setText("Airlines");
        AirlineAdapter adapter = new AirlineAdapter(recyclerView,getContext());
        Iterator<Airline> iter = mainActivity.airlines.values().iterator();
        while (iter.hasNext()) {
            Airline next = iter.next();
            adapter.add(next);
        }
        this.recyclerView.setAdapter(adapter);
    }
}
