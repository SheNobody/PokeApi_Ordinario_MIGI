package org.grace.pokedex.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.grace.pokedex.R;
import org.grace.pokedex.data.Pokemon;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PokemonAdapter3 extends RecyclerView.Adapter<PokemonAdapter3.ViewHolder> {

    private List<Pokemon> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;

    public PokemonAdapter3(Context context, List<Pokemon> data) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.pokemon_item3_fav, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pokemon pokemon = mData.get(position);

        Glide.with(mContext).load(pokemon.getImage()).into(holder.pokemonImage);
        holder.pokemonName.setText(pokemon.getName());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView pokemonName;
        ImageView pokemonImage, fav;

        ViewHolder(View itemView) {
            super(itemView);
            pokemonName = itemView.findViewById(R.id.tv_pokemon_name);
            pokemonImage = itemView.findViewById(R.id.iv_pokemon_image);
            fav = itemView.findViewById(R.id.details_favorite);
            itemView.setOnClickListener(this);
            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) mClickListener.onDeleteItem(getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public Pokemon getPokemon(int id) {
        return mData.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
        void onDeleteItem(int position);
    }

    public void setItems(List<Pokemon> items) {
        this.mData = items;
        notifyDataSetChanged();
    }
}
