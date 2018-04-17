package cs371m.jh54765.flightatlas;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {
    private ArrayList<Route> mData = new ArrayList<Route>();
    private Context context;

    public class RouteViewHolder extends RecyclerView.ViewHolder {
        TextView text_destination;
        TextView text_airline;
        View container;

        public RouteViewHolder(View theView) {
            super(theView);
            theView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            container = theView;
            text_destination = theView.findViewById(R.id.text_destination);
            text_airline = theView.findViewById(R.id.text_airline);

        }

    }



    public RouteAdapter(RecyclerView rv, Context _context) {
        // More code
        rv.setAdapter(this);



        context = _context;
    }

    // XXX A bunch more functions, like add, removeItem, etc.
    // Also important functions like onCreateViewHolder, onBindViewHolder

    public void add(Route route) {
        mData.add(route);
        notifyItemInserted(mData.size()-1);



    }

    public void removeItem(int position) {
        mData.remove(position);
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return mData.size();
    }

    public void onBindViewHolder(RouteAdapter.RouteViewHolder holder, int position) {
        try {
            Route route = mData.get(position);
            holder.text_destination.setTextColor(Color.BLACK);
            holder.text_destination.setText(String.format("%s (%s)",route.getDestination().getCodeIATA(),route.getDestination().getCity().getCity()));
            holder.text_airline.setTextColor(Color.BLACK);
            String operators = "";
            for (int i = 0; i < route.getOperators().size(); i++) {
                operators += "\n"+route.getOperators().get(i).getName();
            }
            operators = operators.substring(1);
            holder.text_airline.setText(operators);


        } catch (Exception e) {
            System.out.print("onBindViewHolder: ");
            System.out.println(e);
        }
    }

    public RouteAdapter.RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.row_route_info,parent,false);
        RouteViewHolder vh = new RouteViewHolder(v);
        return vh;
    }


    // This one is important and not obvious
    @Override
    public long getItemId(int position) {
        return mData.get(position).hashCode();
    }


}
