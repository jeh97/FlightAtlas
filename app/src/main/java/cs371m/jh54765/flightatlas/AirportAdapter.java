package cs371m.jh54765.flightatlas;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jacob on 4/2/18.
 */

//public class AirportAdapter extends ArrayAdapter<Airport> {
//    private LayoutInflater theInflater = null;
//    public AirportAdapter(Context context) {
//        super(context,R.layout.row_airport_info);
//        theInflater = LayoutInflater.from(getContext());
//    }
//    private View BindView(int position, View theView) {
//        System.out.println("Binding view");
//        Airport airport = getItem(position);
//        TextView text_name = (TextView) theView.findViewById(R.id.text_name);
//        text_name.setText(airport.getName());
//        text_name.setTextColor(Color.BLACK);
//
//        System.out.printf("BV for %s pos %d\n",airport.getCodeIATA(),position);
//
//        TextView text_code = (TextView) theView.findViewById(R.id.text_code);
//        text_code.setText(airport.getCodeIATA());
//        text_code.setTextColor(Color.BLACK);
//
//        return theView;
//    }
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = theInflater.inflate(R.layout.row_airport_info,parent,false);
//        }
//        convertView = BindView(position,convertView);
//        return convertView;
//    }
//}


public class AirportAdapter extends RecyclerView.Adapter<AirportAdapter.AirportViewHolder> {
    private ArrayList<Airport> mData = new ArrayList<Airport>();
    private Context context;

    public class AirportViewHolder extends RecyclerView.ViewHolder {
        TextView text_name;
        TextView text_code;
        View container;

        public AirportViewHolder(View theView) {
            super(theView);
            theView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //open airport info fragment
                    System.out.printf("Clicked %s\n",mData.get(getAdapterPosition()).getName());
                    launchAirportInfoFragment(mData.get(getAdapterPosition()).getCodeIATA());
                }
            });
            container = theView;
            text_name = theView.findViewById(R.id.text_name);
            text_code = theView.findViewById(R.id.text_code);

        }

    }



    public AirportAdapter(RecyclerView rv, Context _context) {
        // More code
        rv.setAdapter(this);



        context = _context;
    }

    // XXX A bunch more functions, like add, removeItem, etc.
    // Also important functions like onCreateViewHolder, onBindViewHolder

    public void add(Airport airport) {
        mData.add(airport);
        notifyItemInserted(mData.size()-1);



    }

    public void removeItem(int position) {
        mData.remove(position);
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return mData.size();
    }

    public void onBindViewHolder(AirportAdapter.AirportViewHolder holder, int position) {
        try {
            holder.text_name.setTextColor(Color.BLACK);
            holder.text_name.setText(mData.get(position).getName());
            holder.text_code.setTextColor(Color.BLACK);
            holder.text_code.setText(mData.get(position).getCodeIATA());


        } catch (Exception e) {
            System.out.print("onBindViewHolder: ");
            System.out.println(e);
        }
    }

    public AirportAdapter.AirportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.row_airport_info,parent,false);
        AirportViewHolder vh = new AirportViewHolder(v);
        return vh;
    }


    // This one is important and not obvious
    @Override
    public long getItemId(int position) {
        return mData.get(position).hashCode();
    }

    public void launchAirportInfoFragment(String codeIATA) {
        MainActivity mainActivity = (MainActivity) context;
        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

//        fragmentManager.popBackStack();
        Bundle args = new Bundle();
        args.putString("airport",codeIATA);

        AirportInfoFragment infoFragment = new AirportInfoFragment();
        infoFragment.setArguments(args);
        fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.main_fragment));
        fragmentTransaction.add(R.id.main_fragment,infoFragment);
        fragmentTransaction.addToBackStack("airport_info");
        fragmentTransaction.commit();

    }

}

