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
@Parcel(analyze = {AAMovie.class})
@Table(name = "AAMovies")
// AAMovie = ActiveAndroidMovie; Movie class whose database interaction is done thru ActiveAndroid
public class AAMovie extends Model {
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
   // currently AAMovie is not associated with Review or Trailer, so the latter two are not saved in
   // local database but AAMovie is. instead, Review and Trailer are retrieved directly from TMDB.org

   // empty constructor required by the Parceler library and the ActiveAndroid model
   public AAMovie() {
   }

   public static AAMovie fromJSONObject(JSONObject jsonObject) {
      AAMovie movie = new AAMovie();
      try {
         movie.id = jsonObject.getLong("id");
         movie.originalTitle = jsonObject.getString("original_title");
         movie.posterPath = jsonObject.getString("poster_path");
         movie.overview = jsonObject.getString("overview");
         movie.voteAverage = jsonObject.getDouble("vote_average");
         movie.releaseDate = jsonObject.getString("release_date");
      } catch (JSONException e) {
         e.printStackTrace();
      }
      return movie;
   }

   public static List<AAMovie> fromJSONArray(JSONObject response) {
      List<AAMovie> movies = new ArrayList<>();
      try {
         JSONArray jsonArray = response.getJSONArray("results");
         for (int i = 0; i < jsonArray.length(); i++) {
            try {
               JSONObject jsonObject = jsonArray.getJSONObject(i);
               AAMovie movie = fromJSONObject(jsonObject);
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

   public static AAMovie query(long id) {
      Log.d("NGUYEN", "query single");
      return new Select().from(AAMovie.class).where("remote_id = ?", id).executeSingle();
   }

   public static List<AAMovie> query() {
      return new Select().from(AAMovie.class).execute();
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
