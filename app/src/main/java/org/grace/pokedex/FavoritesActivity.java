package org.grace.pokedex;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.grace.pokedex.adapters.AppDataBaseSingleton;
import org.grace.pokedex.adapters.DeletePokemonAsyncTask;
import org.grace.pokedex.adapters.PokemonAdapter3;
import org.grace.pokedex.data.AppDatabase;
import org.grace.pokedex.data.Pokemon;
import org.grace.pokedex.interfaces.AsyncTaskHandler;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


public class FavoritesActivity extends AppCompatActivity implements PokemonAdapter3.ItemClickListener, AsyncTaskHandler {

    PokemonAdapter3 adapter;
    RecyclerView recyclerView;
    ImageView favorite;
    RecyclerView rvDetailsTypes;
    Pokemon pokemon;

    List<Pokemon> favoritePokemons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rv_pokemon);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new PokemonAdapter3(this, favoritePokemons);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        favorite = findViewById(R.id.details_favorite);
        rvDetailsTypes = findViewById(R.id.rv_details_types);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_top, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_favorites:
                Intent intent = new Intent(this, FavoritesActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                // app icon in action bar clicked; go home
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetPokemonsAsyncTask task = new GetPokemonsAsyncTask();
        task.execute();
    }

    @Override
    public void onItemClick(View view, int position) {
        Pokemon selectedPokemon = adapter.getPokemon(position);
        Intent intent = new Intent(this, PokemonDetailsActivity.class);
        intent.putExtra("URL", selectedPokemon.getUrl());
        startActivity(intent);
    }

    private void showAlert(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Seguro que quieres eliminar a " + pokemon.getName()+ " de favoritos?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DeletePokemonAsyncTask taskDelete = new DeletePokemonAsyncTask(FavoritesActivity.this, FavoritesActivity.this);
                taskDelete.execute(pokemon);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onDeleteItem(int position) {
        pokemon = adapter.getPokemon(position);
        showAlert(this);
    }

    private void updatePokemonList(List<Pokemon> pokemonShortList) {
        adapter.setItems(pokemonShortList);
    }

    @Override
    public void onTaskEnd(Object result) {
        pokemon = null;
        GetPokemonsAsyncTask task = new GetPokemonsAsyncTask();
        task.execute();
    }


    public class GetPokemonsAsyncTask extends AsyncTask<Void, Void, List<Pokemon>> {

        @Override
        protected List<Pokemon> doInBackground(Void... voids) {
            AppDatabase db = AppDataBaseSingleton.getInstance(getApplicationContext()).appDatabase;
            return db.pokemonDao().getAll();
        }

        @Override
        protected void onPostExecute(List<Pokemon> pokemons) {
            super.onPostExecute(pokemons);
            favoritePokemons.clear();
            favoritePokemons.addAll(pokemons);
            adapter.notifyDataSetChanged();
        }
    }
}
