package org.grace.pokedex.interfaces;

import org.grace.pokedex.data.Pokemon;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PokemonDao {
    @Query("SELECT * FROM pokemon")
    List<Pokemon> getAll();

    @Query("SELECT * FROM pokemon WHERE id IN (:pokemonIds)")
    List<Pokemon> loadAllByIds(int[] pokemonIds);

    @Query("SELECT * FROM pokemon WHERE name LIKE :name LIMIT 1")
    Pokemon findByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Pokemon... pokemons);

    @Delete
    void delete(Pokemon pokemon);
}