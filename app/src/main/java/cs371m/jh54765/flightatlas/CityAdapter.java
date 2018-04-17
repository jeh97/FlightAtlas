package cs371m.jh54765.flightatlas;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jacob on 4/3/18.
 */

//public class CityAdapter extends ArrayAdapter<Metro> {
//    private LayoutInflater theInflater = null;
//    private Context context;
//    private Globals globals;
//    public CityAdapter(Context context) {
//        super(context,R.layout.row_city_info);
//        globals = Globals.getInstance();
//        this.context = context;
//        theInflater = LayoutInflater.from(getContext());
//    }
//    private View BindView(int position, View theView) {
////        System.out.println("Binding view");
//        Metro metro = getItem(position);
//        TextView text_name = (TextView) theView.findViewById(R.id.text_name);
//        text_name.setText(metro.getName()+", "+metro.getCountry());
//        text_name.setTextColor(Color.BLACK);
//
////        System.out.printf("BV for %s pos %d\n",metro.getName(),position);
//
//        TextView text_airports = (TextView) theView.findViewById(R.id.text_airports);
//        String codes = "";
//        if (globals.ONLY_SHOW_COMMERCIAL_AIRPORTS) {
//            int numAirports = 0;
//            for (int i = 0; i < metro.getAirports().size(); i++) {
//                Airport airport = metro.getAirports().get(i);
//                if (airport.hasRoutes()) {
//                    codes += ", "+airport.getCodeIATA();
//                    numAirports++;
//                }
//            }
//            if (numAirports > 1) {
//                System.out.printf("%s, %s has %d airports: %s\n",metro.getName(),metro.getCountry(),numAirports,codes);
//            }
//            codes = codes.substring(2);
//        } else {
//            codes = metro.getAirports().get(0).getCodeIATA();
//            for (int i = 1; i < metro.getAirports().size(); i++) {
//                codes += ", " + metro.getAirports().get(i).getCodeIATA();
//            }
//        }
//        text_airports.setText(codes);
//        text_airports.setTextColor(Color.BLACK);
//
//        return theView;
//    }
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = theInflater.inflate(R.layout.row_city_info,parent,false);
//        }
//        convertView = BindView(position,convertView);
//        return convertView;
//    }
//}

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {
    private ArrayList<Metro> mData = new ArrayList<Metro>();
    private Context context;
    private Globals globals;

    public class CityViewHolder extends RecyclerView.ViewHolder {
        TextView text_name;
        TextView text_airports;
        View container;

        public CityViewHolder(View theView) {
            super(theView);
            theView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //open city info fragment

                }
            });
            container = theView;
            text_name = theView.findViewById(R.id.text_name);
            text_airports = theView.findViewById(R.id.text_airports);

        }

    }



    public CityAdapter(RecyclerView rv, Context _context) {
        // More code
        rv.setAdapter(this);
        globals = Globals.getInstance();



        context = _context;
    }

    // XXX A bunch more functions, like add, removeItem, etc.
    // Also important functions like onCreateViewHolder, onBindViewHolder

    public void add(Metro metro) {
        mData.add(metro);
        notifyItemInserted(mData.size()-1);



    }

    public void removeItem(int position) {
        mData.remove(position);
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return mData.size();
    }

    public void onBindViewHolder(CityAdapter.CityViewHolder holder, int position) {
        try {
            holder.text_name.setTextColor(Color.BLACK);
            holder.text_name.setText(mData.get(position).getCity());
            holder.text_airports.setTextColor(Color.BLACK);
            Metro metro = mData.get(position);
            String codes = "";
            if (globals.ONLY_SHOW_COMMERCIAL_AIRPORTS) {
                int numAirports = 0;
                for (int i = 0; i < metro.getAirports().size(); i++) {
                    Airport airport = metro.getAirports().get(i);
                    if (airport.hasRoutes()) {
                        codes += ", "+airport.getCodeIATA();
                        numAirports++;
                    }
                }
                if (numAirports > 1) {
                    System.out.printf("%s, %s has %d airports: %s\n",metro.getCity(),metro.getCountry(),numAirports,codes);
                }
                codes = codes.substring(2);
            } else {
                codes = metro.getAirports().get(0).getCodeIATA();
                for (int i = 1; i < metro.getAirports().size(); i++) {
                    codes += ", " + metro.getAirports().get(i).getCodeIATA();
                }
            }
            holder.text_airports.setText(codes);


        } catch (Exception e) {
            System.out.print("onBindViewHolder: ");
            System.out.println(e);
        }
    }

    public CityAdapter.CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.row_city_info,parent,false);
        CityViewHolder vh = new CityViewHolder(v);
        return vh;
    }


    // This one is important and not obvious
    @Override
    public long getItemId(int position) {
        return mData.get(position).hashCode();
    }

}

