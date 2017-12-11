package com.example.amank.locationtracker;

/**
 * Created by amank on 06-12-2017.
 */

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import android.support.v7.widget.RecyclerView;


import java.util.ArrayList;

/**
 * Created by amank on 26-11-2017.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Location> locations;

    public ListAdapter(Context context, ArrayList<Location> list){
        this.context = context;
        this.locations = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.locations_list_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {


        holder.date.setText(locations.get(position).getDate());
        holder.time.setText(locations.get(position).getTime());
        holder.address.setText(locations.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        if(locations != null){
            return locations.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tv_date);
            time = itemView.findViewById(R.id.tv_time);
            address = itemView.findViewById(R.id.tv_address);
        }

        TextView date;
        TextView time;
        TextView address;
    }
}

