package cs371m.jh54765.flightatlas;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.Set;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity implements Fetch.FetchCallback {

    private boolean highlight_dest = true;
    private boolean draw_all_routes = true;

    private SupportMapFragment mapFragment;
    private MapHolder mapHolder;

    public TreeMap<String,Metro> cities;
    public TreeMap<String,Airport> airports;
    public TreeMap<String,Route> routes;
    public TreeMap<String,Airline> airlines;
    public ArrayList<String> routeCodes;

    public SQLiteDatabase flightDb;
    private DatabaseHelper dbHelper;

    public Globals globals;


    public void fetchStart() {
        System.out.println("Fetch started");
    }

    public void fetchComplete(TreeMap<String,Metro> cities,
                              TreeMap<String,Airport> airports,
                              TreeMap<String,Route> routes,
                              TreeMap<String,Airline> airlines,
                              ArrayList<String> routeCodes) {

        // for each airline, create airline entry
        ArrayList<String> airlineCodes = new ArrayList<String>(airlines.keySet());
//        for (int i = 0; i < airlineCodes.size(); i++) {
//            flightDb.
//        }
        // for each city, make city entry
        // for each airport, make airport entry
        // for each route, make route entry



        System.out.println("Fetch complete");
        this.cities = cities;
        this.airports = airports;
        this.routes = routes;
        this.airlines = airlines;
        this.routeCodes = routeCodes;
        System.out.printf("There are %d cities and %d airports\n",this.cities.size(),this.airports.size());

        launchListFragment();
    }
    public void fetchFailed() {
        System.out.println("Fetch Failed");
    }

    public void updateDatabase() {
        new Fetch(MainActivity.this,getApplicationContext());
    }

    private void initFlightDB() {
        dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.createDatabase();
        } catch (IOException e) {
            Log.e("DB","Fail to create database");
        }
        flightDb = dbHelper.getReadableDatabase();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globals = Globals.getInstance();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        initFlightDB();

        mapHolder = new MapHolder(getApplicationContext());
        mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(mapHolder);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_fragment, mapFragment)
                .hide(mapFragment)
                .commit();

        launchLoadingFragment();


        System.out.println("Fetching");
        new Fetch(MainActivity.this,getApplicationContext());
//        launchListActivity();


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
        intent.putExtra("highlight_dest",this.highlight_dest);
        intent.putExtra("draw_all_routes",this.draw_all_routes);
        startActivityForResult(intent,1);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        this.highlight_dest = data.getBooleanExtra("highlight_dest",true);
        this.draw_all_routes = data.getBooleanExtra("draw_all_routes",true);
    }



    public void launchListFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentManager.popBackStack();

        ListFragment listFragment = new ListFragment();
        fragmentTransaction.add(R.id.main_fragment,listFragment);
        fragmentTransaction.addToBackStack("list");
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

    public void toMapFragment(Airport airport) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.main_fragment,mapFragment);
        fragmentTransaction.show(mapFragment);
        fragmentTransaction.addToBackStack("mapFragment");
        fragmentTransaction.commit();
        mapHolder.showAirport(airport);
    }
}
