package cs371m.jh54765.flightatlas;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.SupportMapFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity implements Fetch.FetchCallback {

    private SupportMapFragment mapFragment;
    private MapHolder mapHolder;

    public TreeMap<String,Metro> cities;
    public TreeMap<String,Airport> airports;
    public TreeMap<String,Route> routes;
    public TreeMap<String,Airline> airlines;
    public ArrayList<String> routeCodes;

    public DBManager db;


    public Globals globals;


    public void fetchStart() {
        Log.v("MainActivity","Fetch started");
    }

    public void fetchComplete(TreeMap<String,Metro> cities,
                              TreeMap<String,Airport> airports,
                              TreeMap<String,Route> routes,
                              TreeMap<String,Airline> airlines,
                              ArrayList<String> routeCodes) {

        // for each airline, create airline entry
        ArrayList<String> airlineCodes = new ArrayList<>(airlines.keySet());
        ArrayList<String> cityCodes = new ArrayList<>(airlines.keySet());
        ArrayList<String> airportCodes = new ArrayList<>(airlines.keySet());
        ArrayList<String> routeKeys = new ArrayList<>(routes.keySet());

        for (Map.Entry<String,Airline> entry: airlines.entrySet()) {
            Airline airline = entry.getValue();
            if (airline.getRoutes().size() > 0) {
                try {
                    db.addAirline(airline);
                } catch (Exception e) {
                    Log.e("MainActivity",e.getMessage());
                }
            }
        }

        // for each city, make city entry
        for (Map.Entry<String,Metro> entry: cities.entrySet()) {
            Metro city = entry.getValue();
            boolean hasCommercialAirport = false;
            for (int k = 0; k < city.getAirports().size(); k++) {
                hasCommercialAirport = hasCommercialAirport || city.getAirports().get(k).hasRoutes();
            }
            if (hasCommercialAirport) {
                db.addMetro(city);
            }
        }
        // for each airport, make airport entry
        for (Map.Entry<String,Airport> entry: airports.entrySet()) {
            Airport airport = entry.getValue();
            if (airport.hasRoutes()) {
                try {
                    db.addAirport(airport);
                } catch (Exception e) {
                    Log.e("MainActivity",e.getMessage());
                }
            }
        }
        // for each route, make route entry
        for (Map.Entry<String,Route> entry: routes.entrySet()) {
            db.addRoute(entry.getValue());
        }


        Log.d("MainActivity","Fetch complete");
        this.cities = cities;
        this.airports = airports;
        this.routes = routes;
        this.airlines = airlines;
        this.routeCodes = routeCodes;
        Log.d("MainActivity",String.format("There are %d cities and %d airports\n",this.cities.size(),this.airports.size()));

        launchListFragment();
    }
    public void fetchFailed() {
        Log.d("MainActivity","Fetch Failed");
    }

    public void updateDatabase() {
        new Fetch(MainActivity.this,getApplicationContext());
    }

    private void initFlightDB() {
        db = DBManager.getInstance(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globals = Globals.getInstance();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mapHolder = new MapHolder(this);
        mapFragment = SupportMapFragment.newInstance();
//        mapFragment = new MapFragment();
        mapFragment.getMapAsync(mapHolder);


        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_fragment, mapFragment)
                .hide(mapFragment)
                .commit();

//        launchLoadingFragment();

        initFlightDB();

        launchListFragment();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            // open settings
            settingsButtonIsPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void settingsButtonIsPressed() {
        Intent intent = new Intent(this,TheSettings.class);
        intent.putExtra("show_markers",globals.SHOW_MARKERS);
        intent.putExtra("show_routes",globals.SHOW_ROUTES);
        startActivityForResult(intent,1);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        globals.SHOW_MARKERS = data.getBooleanExtra("show_markers",true);
        globals.SHOW_ROUTES = data.getBooleanExtra("show_routes",true);
        try {
            ListFragment frag = (ListFragment)getSupportFragmentManager().findFragmentById(R.id.main_fragment);
            frag.updateList();
        } catch (ClassCastException e) {
            Log.e("MainActivity",e.getMessage());
        }
    }



    public void launchListFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentManager.popBackStack();

        ListFragment listFragment = new ListFragment();
        fragmentTransaction.add(R.id.main_fragment,listFragment);
//        fragmentTransaction.addToBackStack("list");
        fragmentTransaction.commit();
    }
    public void launchLoadingFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        LoadingFragment loadingFragment = new LoadingFragment();
        fragmentTransaction.add(R.id.main_fragment,loadingFragment);
        fragmentTransaction.addToBackStack("Loading");
        fragmentTransaction.commit();
    }

    public void toMapFragment(String codeIATA) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.main_fragment,mapFragment);
        fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.main_fragment));
        fragmentTransaction.show(mapFragment);
        fragmentTransaction.addToBackStack("mapFragment");
        fragmentTransaction.commit();
        mapHolder.showAirport(codeIATA,true);
    }

    public void toMapFragmentAirline(String codeIATA) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.main_fragment,mapFragment);
        fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.main_fragment));
        fragmentTransaction.show(mapFragment);
        fragmentTransaction.addToBackStack("mapFragment");
        fragmentTransaction.commit();
        mapHolder.showAirline(codeIATA,true);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            Log.i("MainActivity","popping backstack");
            fragmentManager.popBackStack();
        } else {
            Log.i("MainActivity","nothing on backstack, calling super");
            super.onBackPressed();
        }
    }
}
