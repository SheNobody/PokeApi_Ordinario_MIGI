package org.grace.pokedex;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.grace.pokedex.adapters.AppDataBaseSingleton;
import org.grace.pokedex.adapters.DeletePokemonAsyncTask;
import org.grace.pokedex.adapters.RowTypesAdapter;
import org.grace.pokedex.data.AppDatabase;
import org.grace.pokedex.data.Pokemon;
import org.grace.pokedex.data.PokemonDetails;
import org.grace.pokedex.interfaces.AsyncTaskHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static org.grace.pokedex.utils.PokemonUtils.createUrl;
import static org.grace.pokedex.utils.PokemonUtils.makeHttpRequest;

public class PokemonDetailsActivity extends AppCompatActivity implements AsyncTaskHandler {

    ImageView image, favorite;
    TextView name, types, height, experience, id, typeText;
    RecyclerView rvDetailsTypes;
    String url;
    String pokemonName;
    Pokemon favoritePokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_details);
        image = findViewById(R.id.details_image);
        favorite = findViewById(R.id.details_favorite);
        name = findViewById(R.id.details_name);
        types = findViewById(R.id.detatils_type);
        height = findViewById(R.id.detatils_height);
        experience = findViewById(R.id.detatils_experience);
        id = findViewById(R.id.details_id);
        typeText = findViewById(R.id.detatils_typessText);
        rvDetailsTypes = findViewById(R.id.rv_details_types);
        url = getIntent().getStringExtra("URL");
        PokemonDetailsAsyncTask pokemonDetailsAsyncTask = new PokemonDetailsAsyncTask();
        pokemonDetailsAsyncTask.handler = this;
        pokemonDetailsAsyncTask.execute(url);
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
        PokemonDetails details = (PokemonDetails) result;
        pokemonName = details.getName().toUpperCase();
        Glide.with(this).load(details.getImage()).into(image);
        name.setText(details.getName());
        height.setText("Altura: " + details.getHeight() + "0 cm");
        experience.setText("Experiencia: " + details.getBaseExperience() + " xp");
        id.setText("ID: #" + details.getId());
        String typesString = "";

        for (int i = 0; i < details.getTypes().length; i++) {
            typesString += details.getTypes()[i] + " ";
        }

        rvDetailsTypes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvDetailsTypes.setAdapter(new RowTypesAdapter(this, Arrays.asList(details.getTypes())));
        types.setText("Tipo/s " + System.lineSeparator() + typesString);
        GetPokemonByName task = new GetPokemonByName();
        task.execute(details.getName());
    }

    public void onClickType(View view) {
        Intent intent = new Intent(this, PokemonTypeActivity.class);
        startActivity(intent);
    }

    public void onClickFavorite(View view) {
        if (favoritePokemon != null) {
            showAlert(this);
        } else {
            AddPokemonAsyncTask task = new AddPokemonAsyncTask();
            Pokemon pokemon = new Pokemon(pokemonName, url);
            task.execute(pokemon);
        }
    }

    private void showAlert(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Seguro que quieres eliminar a " + pokemonName + " de favoritos?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DeletePokemonAsyncTask task = new DeletePokemonAsyncTask(getApplicationContext(), new AsyncTaskHandler() {
                    @Override
                    public void onTaskEnd(Object result) {
                        favoritePokemon = null;
                        Glide.with(context).load(R.drawable.fav_empty).into(favorite);
                    }
                });
                task.execute(favoritePokemon);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class PokemonDetailsAsyncTask extends AsyncTask<String, Void, PokemonDetails> {

        public AsyncTaskHandler handler;

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected PokemonDetails doInBackground(String... urls) {

            URL url = createUrl(urls[0]);
            // Hacemos la petición. Ésta puede tirar una exepción.
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
                return pokemonDetails(jsonResponse);
            } catch (IOException e) {
                Log.e("Download error", "Problem making the HTTP request.", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(PokemonDetails pokemonDetails) {
            super.onPostExecute(pokemonDetails);
            if (handler != null) {
                handler.onTaskEnd(pokemonDetails);
            }
        }

        private PokemonDetails pokemonDetails(String jsonStr) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                int baseExperience = jsonObj.getInt("base_experience");
                String name = jsonObj.getString("name");
                int height = jsonObj.getInt("height");
                int id = jsonObj.getInt("id");

                JSONArray jsonArray = jsonObj.getJSONArray("types");
                String[] types = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    String type = jsonArray.getJSONObject(i).getJSONObject("type").getString("name");
                    types[i] = type;
                }
                PokemonDetails pokemonDetails = new PokemonDetails(name, id, baseExperience, height, types);
                return pokemonDetails;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class AddPokemonAsyncTask extends AsyncTask<Pokemon, Void, Pokemon> {
        @Override
        protected Pokemon doInBackground(Pokemon... pokemonShorts) {
            if (pokemonShorts.length == 0) return null;
            AppDatabase db = AppDataBaseSingleton.getInstance(getApplicationContext()).appDatabase;
            db.pokemonDao().insertAll(pokemonShorts);
            return pokemonShorts[0];
        }

        @Override
        protected void onPostExecute(Pokemon pokemon) {
            super.onPostExecute(pokemon);
            favoritePokemon = pokemon;
            Glide.with(getApplicationContext()).load(R.drawable.fav_filled).into(favorite);
        }
    }

    private class GetPokemonByName extends AsyncTask<String, Void, Pokemon> {
        @Override
        protected Pokemon doInBackground(String... pokemonShorts) {
            if (pokemonShorts.length == 0) return null;
            AppDatabase db = AppDataBaseSingleton.getInstance(getApplicationContext()).appDatabase;
            return db.pokemonDao().findByName(pokemonShorts[0]);
        }

        @Override
        protected void onPostExecute(Pokemon pokemon) {
            super.onPostExecute(pokemon);
            if (pokemon != null) {
                favoritePokemon = pokemon;
                Glide.with(getApplicationContext()).load(R.drawable.fav_filled).into(favorite);
            }
        }
    }
}