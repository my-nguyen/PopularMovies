package com.nguyen.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by My on 3/26/2016.
 */
public class Movie {
   String originalTitle;
   String posterPath;
   String overview;
   double voteAverage;
   String releaseDate;

   public static Movie fromJSONObject(JSONObject jsonObject) {
      Movie movie = new Movie();
      try {
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

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("<title: ").append(originalTitle)
            .append("><poster path: ").append(posterPath)
            .append("><sypnosis: ").append(overview)
            .append("><user rating: ").append(voteAverage)
            .append("><release date: ").append(releaseDate).append(">\n");
      return builder.toString();
   }
}
