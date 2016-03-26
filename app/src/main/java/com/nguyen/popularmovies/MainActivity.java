package com.nguyen.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
            /*
            // create the adapter to convert the array to views
            MoviesAdapter adapter = new MoviesAdapter(MainActivity.this, mMovies);
            // attach the adapter to a ListView
            GridView listView = (GridView) findViewById(R.id.grid_view);
            listView.setAdapter(adapter);
            */
            // look up the RecyclerView in activity layout
            RecyclerView listView = (RecyclerView)findViewById(R.id.recycler_view);
            // create adapter passing in the sample data
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(mMovies);
            // attach the adapter to the RecyclerView to populate items
            listView.setAdapter(adapter);
            // set layout manager to position the items
            listView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
         }
      });
   }
}
