package org.grace.pokedex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import org.grace.pokedex.adapters.PokemonAdapter;
import org.grace.pokedex.data.Pokemon;
import org.grace.pokedex.interfaces.AsyncTaskHandler;
import org.grace.pokedex.utils.PokemonUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.grace.pokedex.utils.PokemonUtils.createUrl;
import static org.grace.pokedex.utils.PokemonUtils.makeHttpRequest;

public class HomeActivity extends AppCompatActivity implements AsyncTaskHandler, PokemonAdapter.ItemClickListener {

    PokemonAdapter adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rv_pokemon);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        HomeActivity.PokemonAsyncTask pokemonAsyncTask = new HomeActivity.PokemonAsyncTask();
        pokemonAsyncTask.handler = this;
        pokemonAsyncTask.execute();

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
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTaskEnd(Object pokemons) {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new PokemonAdapter(this, (List<Pokemon>) pokemons);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(View view, int position) {
        Pokemon pokemon = adapter.getPokemon(position);
        Intent intent = new Intent(this, PokemonDetailsActivity.class);
        intent.putExtra("URL", pokemon.getUrl());
        startActivity(intent);
    }

    public static class PokemonAsyncTask extends AsyncTask<Void, Void, List<Pokemon>> {

        public AsyncTaskHandler handler;

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Pokemon> doInBackground(Void... voids) {

            URL url = createUrl("https://pokeapi.co/api/v2/pokemon?offset=0&limit=151");
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
                return listapokemon(jsonResponse);
            } catch (IOException e) {
                Log.e("Download error", "Problem making the HTTP request.", e);
            }
            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<Pokemon> pokemonList) {
            super.onPostExecute(pokemonList);
            if (handler != null) {
                handler.onTaskEnd(pokemonList);
            }
        }

        private List<Pokemon> listapokemon(String jsonStr) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray jsonArray = jsonObj.getJSONArray("results");
                ArrayList<Pokemon> pokemonShortList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    String url = jsonArray.getJSONObject(i).getString("url");
                    String name = jsonArray.getJSONObject(i).getString("name");
                    pokemonShortList.add(new Pokemon(name, url));
                }
                return pokemonShortList;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}