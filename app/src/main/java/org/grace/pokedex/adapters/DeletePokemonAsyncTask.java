package org.grace.pokedex.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.grace.pokedex.data.AppDatabase;
import org.grace.pokedex.data.Pokemon;
import org.grace.pokedex.interfaces.AsyncTaskHandler;

public class DeletePokemonAsyncTask extends AsyncTask<Pokemon, Void, Integer> {

    private Context context;
    AsyncTaskHandler handler;

    public DeletePokemonAsyncTask(Context context, AsyncTaskHandler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    protected Integer doInBackground(Pokemon... pokemonShorts) {
        if (pokemonShorts.length == 0) return 0;
        AppDatabase db = AppDataBaseSingleton.getInstance(context).appDatabase;
        db.pokemonDao().delete(pokemonShorts[0]);
        return 1;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        String message = (integer > 0) ? "Pokemon was deleted" : "Something went wrong";
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        if (handler != null) handler.onTaskEnd(null);
    }

}
