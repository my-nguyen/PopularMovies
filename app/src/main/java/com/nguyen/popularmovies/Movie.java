package com.nguyen.popularmovies;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by My on 3/26/2016.
 */
@Parcel(analyze = {Movie.class})
@Table(name = "Movies")
public class Movie extends Model {
   @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
   public long id;
   @Column(name = "original_title")
   public String originalTitle;
   @Column(name = "poster_path")
   public String posterPath;
   @Column(name = "overview")
   public String overview;
   @Column(name = "vote_average")
   public double voteAverage;
   @Column(name = "release_date")
   public String releaseDate;

   // empty constructor required by the Parceler library and the ActiveAndroid model
   public Movie() {
   }

   public static Movie fromJSONObject(JSONObject jsonObject) {
      Movie movie = new Movie();
      try {
         movie.id = jsonObject.getLong("id");
         movie.originalTitle = jsonObject.getString("original_title");
         movie.posterPath = jsonObject.getString("poster_path");
         movie.overview = jsonObject.getString("overview");
         movie.voteAverage = jsonObject.getDouble("vote_average");
         movie.releaseDate = jsonObject.getString("release_date");
         movie.save();
      } catch (JSONException e) {
         e.printStackTrace();
      }
      return movie;
   }

   public static List<Movie> fromJSONArray(JSONObject response) {
      List<Movie> movies = new ArrayList<>();
      try {
         JSONArray jsonArray = response.getJSONArray("results");
         for (int i = 0; i < jsonArray.length(); i++) {
            try {
               JSONObject jsonObject = jsonArray.getJSONObject(i);
               Movie movie = fromJSONObject(jsonObject);
               if (movie != null)
                  movies.add(movie);
            } catch (JSONException e) {
               e.printStackTrace();
               // keep deserializing the next JSONObject in the JSONArray even if the deserialization
               // of the current JSONObject fails
               continue;
            }
         }
      } catch (JSONException e) {
         e.printStackTrace();
      }

      return movies;
   }

   /*
   public static Movie query(long id) {
      List<Movie> movies = new Select().from(Movie.class).where("remote_id = ?", id).execute();
      return movies != null ? movies.get(0) : null;
   }
   */

   public static List<Movie> query() {
      return new Select().from(Movie.class).orderBy("original_title ASC").execute();
   }

   public static void createDummy() {
      Movie movie = new Movie();
      movie.id = 1;
      movie.originalTitle = "Dummy";
      movie.posterPath = "";
      movie.overview = "";
      movie.voteAverage = 0.0;
      movie.releaseDate = "";
      movie.save();
      Log.d("NGUYEN", "createDummy()");
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder
            .append("<id: ").append(id)
            .append("><title: ").append(originalTitle)
            .append("><poster path: ").append(posterPath)
            .append("><sypnosis: ").append(overview)
            .append("><user rating: ").append(voteAverage)
            .append("><release date: ").append(releaseDate)
            .append(">\n");
      return builder.toString();
   }
}
