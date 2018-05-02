package cs371m.jh54765.flightatlas;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;


import java.util.Iterator;

/**
 * Created by Jacob on 4/3/18.
 */

public class ListFragment extends Fragment {
    MainActivity mainActivity;
    private ListView listView;
    private TextView title;
    private Button citiesButton;
    private Button airlinesButton;
    private Button airportsButton;
    private String[] airportsSortOptions;
    private String[] airlinesSortOptions;
    private String[] citiesSortOptions;

    private Spinner sortSpinner;
    private ArrayAdapter<CharSequence> airportsSortAdapter;
    private ArrayAdapter<CharSequence> airlinesSortAdapter;
    private ArrayAdapter<CharSequence> citiesSortAdapter;
    private Switch switchAsc;

    private static final int AIRPORTS_ID = 0;
    private static final int AIRLINES_ID = 1;
    private static final int CITIES_ID = 2;
    private static final int DEFAULT_SELECTION = 0;
    private static final int DEFAULT_LIST = -1;
    private static final boolean DEFAULT_ASC = true;

    private int currentList;
    private int selection;
    private boolean asc;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the root view and cache references to vital UI elements
        View v = inflater.inflate(R.layout.list_fragment, container, false);

        this.listView = v.findViewById(R.id.listView);
        this.title = v.findViewById(R.id.textView_title);
        citiesButton = v.findViewById(R.id.button_cities);
        citiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selection = DEFAULT_SELECTION;
                loadCities();
            }
        });

        airlinesButton = v.findViewById(R.id.button_airlines);
        airlinesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selection = DEFAULT_SELECTION;
                loadAirlines();
            }
        });
        airportsButton = v.findViewById(R.id.button_airports);
        airportsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selection = DEFAULT_SELECTION;
                loadAirports();
            }
        });




        sortSpinner = v.findViewById(R.id.spinner_sort);



        switchAsc = v.findViewById(R.id.switch_asc);

        switchAsc.setChecked(asc);

        switchAsc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateList();
            }
        });


        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selection = i;
                updateList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selection = DEFAULT_SELECTION;
                sortSpinner.setSelection(selection);
            }
        });


        updateTitle();
        initListView();
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();

        currentList = DEFAULT_LIST;
        asc = DEFAULT_ASC;
        selection = DEFAULT_SELECTION;


        airportsSortOptions = getResources().getStringArray(R.array.airports_spinner);
        airlinesSortOptions = getResources().getStringArray(R.array.airlines_spinner);
        citiesSortOptions = getResources().getStringArray(R.array.cities_spinner);


        airportsSortAdapter = ArrayAdapter.createFromResource(mainActivity,R.array.airports_spinner,android.R.layout.simple_spinner_item);
        airlinesSortAdapter = ArrayAdapter.createFromResource(mainActivity,R.array.airlines_spinner,android.R.layout.simple_spinner_item);
        citiesSortAdapter = ArrayAdapter.createFromResource(mainActivity,R.array.cities_spinner,android.R.layout.simple_spinner_item);



        airportsSortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        airlinesSortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citiesSortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {









    }

    private void initListView() {
        if (currentList == DEFAULT_LIST) {
            currentList = AIRPORTS_ID;
        }
        setSortAdapter();
        updateList();

    }

    private void loadAirports() {
        currentList = AIRPORTS_ID;
        // Set title of list
        updateTitle();
        setSortAdapter();

        // Add sort options for airports

        airportsOptionsChanged();


    }

    public void airportsOptionsChanged() {
        boolean asc = switchAsc.isChecked();

        String orderBy = airportsSortOptions[sortSpinner.getSelectedItemPosition()];

        Cursor cursor = mainActivity.db.getAirports(orderBy,asc);
        AirportAdapter adapter = new AirportAdapter(mainActivity,cursor,false);

        this.listView.setAdapter(adapter);
    }

    private void loadCities() {
        currentList = CITIES_ID;
        updateTitle();
        setSortAdapter();
//        CityAdapter adapter = new CityAdapter(mainActivity,cursor,false);
//        CityAdapter adapter = new CityAdapter(mainActivity,cursor,false);
//        Iterator<Metro> iter = mainActivity.cities.values().iterator();
//        while (iter.hasNext()) {
//            Metro next = iter.next();
//            if (!mainActivity.globals.ONLY_SHOW_COMMERCIAL_AIRPORTS || next.hasCommercialAirport()) adapter.add(next);
//        }
//        this.listView.setAdapter(adapter);

    }

    public void citiesOptionsChanged() {
        boolean asc = switchAsc.isChecked();

        String orderBy = citiesSortOptions[sortSpinner.getSelectedItemPosition()];

        Cursor cursor = mainActivity.db.getCities(orderBy,asc);
        CityAdapter adapter = new CityAdapter(mainActivity,cursor,false);
//
        this.listView.setAdapter(adapter);
    }

    private void loadAirlines() {
        currentList = AIRLINES_ID;
        updateTitle();
        airlinesOptionsChanged();
        setSortAdapter();
    }

    public void airlinesOptionsChanged() {
        boolean asc = switchAsc.isChecked();

        String orderBy = airlinesSortOptions[sortSpinner.getSelectedItemPosition()];

        Cursor cursor = mainActivity.db.getAirlines(orderBy,asc);
        AirlineAdapter adapter = new AirlineAdapter(mainActivity,cursor,false);

        this.listView.setAdapter(adapter);
    }

    public void updateList() {
        switch (currentList) {
            case AIRPORTS_ID:
                airportsOptionsChanged();
                break;
            case AIRLINES_ID:
                airlinesOptionsChanged();
                break;
            case CITIES_ID:
                citiesOptionsChanged();
                break;
            default:

        }
    }

    public void setSortAdapter() {
        switch (currentList) {
            case AIRPORTS_ID:
                sortSpinner.setAdapter(airportsSortAdapter);
                sortSpinner.setSelection(selection);
                break;
            case AIRLINES_ID:
                sortSpinner.setAdapter(airlinesSortAdapter);
                sortSpinner.setSelection(selection);
                break;
            case CITIES_ID:
                sortSpinner.setAdapter(citiesSortAdapter);
                sortSpinner.setSelection(selection);
                break;
            default:

        }
    }

    public void updateTitle() {
        switch (currentList) {
            case AIRPORTS_ID:
                title.setText("Airports");
                break;
            case AIRLINES_ID:
                title.setText("Airlines");
                break;
            case CITIES_ID:
                title.setText("Cities");
                break;
            default:

        }
    }
}
