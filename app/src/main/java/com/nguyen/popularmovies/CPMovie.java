package com.nguyen.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by My on 4/6/2016.
 */
@Parcel
// CPMovie = ContentProviderMovie; Movie class whose database interaction is done thru ContentProvider 
public class CPMovie {
   long id;
   String originalTitle;
   String posterPath;
   String overview;
   double voteAverage;
   String releaseDate;

   static final Uri CONTENT_URI = Uri.parse("content://com.nguyen.popularmovies.MoviesProvider/favorites");
   static ContentResolver sResolver;

   // empty constructor required by Parceler
   public CPMovie() {
   }
      
   public static void setContentResolver(ContentResolver resolver) {
      sResolver = resolver;
   }

   public static CPMovie query(long remoteId) {
      String[] projection = new String[] { "id", "original_title", "poster_path",
            "overview", "vote_average", "release_date" };
      String selection = "id = ?";
      String[] selectionArgs = new String[]{"" + remoteId};
      Cursor cursor = sResolver.query(CONTENT_URI, projection, selection, selectionArgs, null);
      if (cursor != null && cursor.moveToFirst())
         return fromCursor(cursor);
      else
         return null;
   }

   public static List<CPMovie> query() {
      List<CPMovie> movies = new ArrayList<>();
      String[] projection = new String[] { "id", "original_title", "poster_path",
            "overview", "vote_average", "release_date" };
      Cursor cursor = sResolver.query(CONTENT_URI, projection, null, null, null);
      if (cursor != null && cursor.moveToFirst()) {
         do {
            movies.add(fromCursor(cursor));
         } while (cursor.moveToNext());
      }
      return movies;
   }

   public void save() {
      ContentValues values = new ContentValues();
      values.put("id", id);
      values.put("original_title", originalTitle);
      values.put("poster_path", posterPath);
      values.put("overview", overview);
      values.put("vote_average", voteAverage);
      values.put("release_date", releaseDate);
      sResolver.insert(MoviesProvider.CONTENT_URI, values);
   }

   public void delete() {
      String selection = "id = ?";
      String[] selectionArgs = new String[]{ ""+id };
      sResolver.delete(CONTENT_URI, selection, selectionArgs);
   }

   private static CPMovie fromCursor(Cursor cursor) {
      CPMovie movie = new CPMovie();
      movie.id = cursor.getInt(cursor.getColumnIndex("id"));
      movie.originalTitle = cursor.getString(cursor.getColumnIndex("original_title"));
      movie.posterPath = cursor.getString(cursor.getColumnIndex("poster_path"));
      movie.overview = cursor.getString(cursor.getColumnIndex("overview"));
      movie.voteAverage = cursor.getDouble(cursor.getColumnIndex("vote_average"));
      movie.releaseDate = cursor.getString(cursor.getColumnIndex("release_date"));
      return movie;
   }

   public static CPMovie fromJSONObject(JSONObject jsonObject) {
      CPMovie movie = new CPMovie();
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

   public static List<CPMovie> fromJSONArray(JSONObject response) {
      List<CPMovie> movies = new ArrayList<>();
      try {
         JSONArray jsonArray = response.getJSONArray("results");
         for (int i = 0; i < jsonArray.length(); i++) {
            try {
               JSONObject jsonObject = jsonArray.getJSONObject(i);
               CPMovie movie = fromJSONObject(jsonObject);
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
      builder.append("<id: ").append(id)
            .append("><title: ").append(originalTitle)
            .append("><poster path: ").append(posterPath)
            .append("><sypnosis: ").append(overview)
            .append("><user rating: ").append(voteAverage)
            .append("><release date: ").append(releaseDate)
            .append(">\n");
      return builder.toString();
   }
}
