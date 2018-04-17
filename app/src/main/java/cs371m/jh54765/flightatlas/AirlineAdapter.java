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

//public class AirlineAdapter extends ArrayAdapter<Airline> {
//    private LayoutInflater theInflater = null;
//    public AirlineAdapter(Context context) {
//        super(context,R.layout.row_airline_info);
//        theInflater = LayoutInflater.from(getContext());
//    }
//    private View BindView(int position, View theView) {
//        System.out.println("Binding view");
//        Airline airline = getItem(position);
//        TextView text_name = (TextView) theView.findViewById(R.id.text_name);
//        text_name.setText(String.format("%s (%s)",airline.getName(),airline.getCodeIATA()));
//        text_name.setTextColor(Color.BLACK);
//
//        TextView text_country = (TextView) theView.findViewById(R.id.text_country);
//        text_country.setText(airline.getCountry());
//        text_country.setTextColor(Color.BLACK);
//
//
//        return theView;
//    }
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = theInflater.inflate(R.layout.row_airline_info,parent,false);
//        }
//        convertView = BindView(position,convertView);
//        return convertView;
//    }
//}

public class AirlineAdapter extends RecyclerView.Adapter<AirlineAdapter.AirlineViewHolder> {
    private ArrayList<Airline> mData = new ArrayList<Airline>();
    private Context context;

    public class AirlineViewHolder extends RecyclerView.ViewHolder {
        TextView text_name;
        TextView text_country;
        View container;

        public AirlineViewHolder(View theView) {
            super(theView);
            theView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //open airport info fragment

                }
            });
            container = theView;
            text_name = theView.findViewById(R.id.text_name);
            text_country = theView.findViewById(R.id.text_code);

        }

    }



    public AirlineAdapter(RecyclerView rv, Context _context) {
        // More code
        rv.setAdapter(this);



        context = _context;
    }

    // XXX A bunch more functions, like add, removeItem, etc.
    // Also important functions like onCreateViewHolder, onBindViewHolder

    public void add(Airline airline) {
        mData.add(airline);
        notifyItemInserted(mData.size()-1);



    }

    public void removeItem(int position) {
        mData.remove(position);
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return mData.size();
    }

    public void onBindViewHolder(AirlineAdapter.AirlineViewHolder holder, int position) {
        try {
            holder.text_name.setTextColor(Color.BLACK);
            holder.text_name.setText(String.format("%s (%s)",mData.get(position).getName(),mData.get(position).getCodeIATA()));
            holder.text_country.setTextColor(Color.BLACK);
            holder.text_country.setText(mData.get(position).getCountry());


        } catch (Exception e) {
            System.out.print("onBindViewHolder: ");
            System.out.println(e);
        }
    }

    public AirlineAdapter.AirlineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.row_airline_info,parent,false);
        AirlineViewHolder vh = new AirlineViewHolder(v);
        return vh;
    }


    // This one is important and not obvious
    @Override
    public long getItemId(int position) {
        return mData.get(position).hashCode();
    }

}

