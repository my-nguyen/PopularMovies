package com.nguyen.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
   TMDBClient mClient = new TMDBClient();
   List<Movie> mMovies = null;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      mClient.getPopularMovies(new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            // construct the data source
            mMovies = Movie.fromJSONArray(response);
            for (Movie movie : mMovies)
               Log.d("NGUYEN", movie.toString());
            // create the adapter to convert the array to views
            MoviesAdapter adapter = new MoviesAdapter(MainActivity.this, mMovies);
            // attach the adapter to a ListView
            GridView listView = (GridView) findViewById(R.id.grid_view);
            listView.setAdapter(adapter);
         }
      });
   }
}
