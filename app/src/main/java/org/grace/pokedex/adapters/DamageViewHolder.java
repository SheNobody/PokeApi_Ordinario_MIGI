package org.grace.pokedex.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.grace.pokedex.R;

public class DamageViewHolder {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView relation;
        ImageView damageFrom;
        RecyclerView rvTypes;

        public ViewHolder(View itemView) {
            super(itemView);
            relation = itemView.findViewById(R.id.relation);
            damageFrom = itemView.findViewById(R.id.damage_from);
            rvTypes = itemView.findViewById(R.id.rv_types);
        }
    }
}