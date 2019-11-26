package org.grace.pokedex.adapters.vh;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.grace.pokedex.R;

public class DamageViewHolder {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView relation;
        public ImageView damageFrom;
        public RecyclerView rvTypes;

        public ViewHolder(View itemView) {
            super(itemView);
            relation = itemView.findViewById(R.id.relation);
            damageFrom = itemView.findViewById(R.id.damage_from);
            rvTypes = itemView.findViewById(R.id.rv_types);
        }
    }
}