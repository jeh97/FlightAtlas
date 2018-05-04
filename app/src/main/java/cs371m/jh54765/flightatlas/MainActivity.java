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

public class MainActivity extends AppCompatActivity implements FetchCityCoords.FetchCityCoordsCallback {

    private SupportMapFragment mapFragment;
    private MapHolder mapHolder;


    public DBManager db;


    public Globals globals;


    public void fetchStart() {
        Log.v("MainActivity","Fetch started");
    }

    public void fetchComplete() {
        Log.v("MainActivity","Fetch Completed");
        launchListFragment();
    }
    public void fetchFailed() {
        Log.d("MainActivity","Fetch Failed");
    }

    public void updateCityCoords() {
        new FetchCityCoords(MainActivity.this,getApplicationContext());
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

        initFlightDB();
//        launchLoadingFragment();
//        updateCityCoords();


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
        fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.main_fragment));
        fragmentTransaction.show(mapFragment);
        fragmentTransaction.addToBackStack("mapFragment");
        fragmentTransaction.commit();
        mapHolder.showAirline(codeIATA,true);
    }

    public void toMapFragmentCity(String city, String country) {
        Log.d("MainActivity",String.format("Loading mapFragment for city %s, %s",city,country));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.main_fragment));
        fragmentTransaction.show(mapFragment);
        fragmentTransaction.addToBackStack("mapFragment");
        fragmentTransaction.commit();
        mapHolder.showCity(city,country,true);
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
