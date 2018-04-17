package cs371m.jh54765.flightatlas;

import android.content.Context;
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
    private TextView text_flights;
    private TextView text_city;
    private Airport airport;
    private RecyclerView recyclerView;
    protected LinearLayoutManager rv_layout_mgr;
    protected AirportInfoFragment fragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the root view and cache references to vital UI elements
        View v = inflater.inflate(R.layout.airportinfo_fragment, container, false);
        mainActivity = (MainActivity) getActivity();
        Bundle args = getArguments();
        String code = args.getString("airport");
        context = getContext();
        mainActivity = (MainActivity) context;
        airport = mainActivity.airports.get(code);
        fragment = this;
        return v;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.recyclerView = getView().findViewById(R.id.recyclerView_routes);
        rv_layout_mgr = new LinearLayoutManager(getContext());
        this.recyclerView.setLayoutManager(rv_layout_mgr);
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());

        this.text_name = getView().findViewById(R.id.textView_name);
        this.text_code = getView().findViewById(R.id.textView_code);
        this.text_coords = getView().findViewById(R.id.textView_coords);
        this.text_flights = getView().findViewById(R.id.textView_flights);
        this.text_city = getView().findViewById(R.id.textView_city);

        this.text_coords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.hide(fragment).commit();
                mainActivity.toMapFragment(airport);
            }
        });

        this.text_name.setText(airport.getName());
        this.text_code.setText(airport.getCodeIATA());
        this.text_city.setText(String.format("%s, %s", airport.getCity().getName(),airport.getCity().getCountry()));
        this.text_coords.setText(String.format("%5.2f, %5.2f",airport.getLatitude(),airport.getLongitude()));
        this.listView = getView().findViewById(R.id.recyclerView);
        initRecyclerView();

    }

    private void initRecyclerView() {
        loadRoutes();

    }

    private void loadRoutes() {
        RouteAdapter adapter = new RouteAdapter(recyclerView,getContext());
        ArrayList<Route> routes = airport.getRoutes();
        for (int i = 0; i < routes.size(); i++) {
            Route next = routes.get(i);
            adapter.add(next);
        }
        this.recyclerView.setAdapter(adapter);

    }

}
