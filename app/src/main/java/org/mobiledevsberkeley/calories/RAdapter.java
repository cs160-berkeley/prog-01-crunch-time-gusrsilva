package org.mobiledevsberkeley.calories;

import android.media.Image;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by GusSilva on 2/2/16.
 */
public class RAdapter extends RecyclerView.Adapter<RAdapter.ViewHolder> {

    public RAdapter(){}

    @Override
    public RAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 12;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView amount, unit;

        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView)itemView.findViewById(R.id.grid_pic);
            amount = (TextView)itemView.findViewById(R.id.grid_amount);
            unit = (TextView)itemView.findViewById(R.id.grid_unit);
        }

    }
}
