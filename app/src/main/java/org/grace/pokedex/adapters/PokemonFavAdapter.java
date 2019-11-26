package org.grace.pokedex.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.grace.pokedex.R;
import org.grace.pokedex.adapters.vh.PokemonFavViewHolder;
import org.grace.pokedex.data.Pokemon;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PokemonFavAdapter extends RecyclerView.Adapter<PokemonFavViewHolder> {

    private List<Pokemon> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;

    public PokemonFavAdapter(Context context, List<Pokemon> data) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    @NonNull
    public PokemonFavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.pokemon_item3_fav, parent, false);
        return new PokemonFavViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonFavViewHolder holder, int position) {
        Pokemon pokemon = mData.get(position);
        Glide.with(mContext).load(pokemon.getImage()).into(holder.pokemonImage);
        holder.pokemonName.setText(pokemon.getName());
    }

    @Override
    public int getItemCount() {
        return mData.size();
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