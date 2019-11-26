package org.grace.pokedex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.grace.pokedex.adapters.DamageRelationAdapter;
import org.grace.pokedex.adapters.PokemonAdapter2;
import org.grace.pokedex.data.Pokemon;
import org.grace.pokedex.data.PokemonType;
import org.grace.pokedex.interfaces.AsyncTaskHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.grace.pokedex.utils.PokemonUtils.createUrl;
import static org.grace.pokedex.utils.PokemonUtils.makeHttpRequest;


public class PokemonTypeActivity extends AppCompatActivity implements AsyncTaskHandler, PokemonAdapter2.ItemClickListener {

    TextView name;
    RecyclerView damageRelations;
    RecyclerView pokemons;
    PokemonAdapter2 adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_type);

        name = findViewById(R.id.type_name);
        damageRelations = findViewById(R.id.type_damage_relations);
        pokemons = findViewById(R.id.type_pokemons);

        String type = getIntent().getStringExtra("TYPE");
        String url = "https://pokeapi.co/api/v2/type/" + type;

        TypeUtils pokemonTypeAsyncTask = new TypeUtils();
        pokemonTypeAsyncTask.handler = this;
        pokemonTypeAsyncTask.execute(url);

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
    public void onTaskEnd(Object result) {
        PokemonType pokemonType = (PokemonType) result;

        name.setText(pokemonType.getName());

        damageRelations.setLayoutManager(new LinearLayoutManager(this));
        damageRelations.setAdapter(new DamageRelationAdapter(this, pokemonType));

        pokemons.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new PokemonAdapter2(this, pokemonType.getPokemons());
        adapter.setClickListener(this);
        pokemons.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Pokemon pokemon = adapter.getPokemon(position);

        Intent intent = new Intent(this, PokemonDetailsActivity.class);
        intent.putExtra("URL", pokemon.getUrl());
        startActivity(intent);
    }

    public class TypeUtils extends AsyncTask<String, Void, PokemonType> {

        public AsyncTaskHandler handler;

        @Override
        protected PokemonType doInBackground(String... urls) {
            URL url = createUrl(urls[0]);
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
                return parsePokemonType(jsonResponse);
            } catch (IOException e) {
                Log.e("Download error", "Problem making the HTTP request.", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(PokemonType pokemonType) {
            super.onPostExecute(pokemonType);
            if (handler != null) {
                handler.onTaskEnd(pokemonType);
            }
        }

        private PokemonType parsePokemonType(String jsonStr) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                String name = jsonObj.getString("name");
                JSONObject damageRelationsJson = jsonObj.getJSONObject("damage_relations");
                Map<String, List<String>> damageRelations = new HashMap<>();
                for (int i = 0; i < org.grace.pokedex.utils.TypeUtils.relationNames.length; i++) {
                    List<String> typeNameList = new ArrayList<>();
                    JSONArray jsonArray = damageRelationsJson.getJSONArray(org.grace.pokedex.utils.TypeUtils.relationNames[i]);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        String typeName = jsonArray.getJSONObject(j).getString("name");
                        typeNameList.add(typeName);
                    }
                    damageRelations.put(org.grace.pokedex.utils.TypeUtils.relationNames[i], typeNameList);
                }

                List<Pokemon> pokemons = new ArrayList<>();
                JSONArray pokemonsArray = jsonObj.getJSONArray("pokemon");
                int iterations = pokemonsArray.length() < 5 ? pokemonsArray.length() : 5;
                for (int i = 0; i < iterations; i++) {
                    JSONObject pokemonJson = pokemonsArray.getJSONObject(i).getJSONObject("pokemon");
                    String url = pokemonJson.getString("url");
                    String pokemonName = pokemonJson.getString("name");
                    pokemons.add(new Pokemon(pokemonName, url));
                }
                PokemonType pokemonType = new PokemonType(name, damageRelations, pokemons);
                return pokemonType;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}