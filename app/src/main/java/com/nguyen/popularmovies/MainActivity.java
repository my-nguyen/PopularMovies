package com.nguyen.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.GridView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
   // should this be a singleton?
   TMDBClient mClient = new TMDBClient();
   // data source, which needs to be an empty ArrayList and not NULL
   List<Movie> mMovies = new ArrayList<>();
   // bind data source to adapter
   RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(mMovies);

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      /*
      // unsuccessful attempt at using GridView and custom ArrayAdapter (MoviesAdapter)
      // create the adapter to convert the array to views
      MoviesAdapter adapter = new MoviesAdapter(MainActivity.this, mMovies);
      // attach the adapter to a ListView
      GridView listView = (GridView) findViewById(R.id.grid_view);
      listView.setAdapter(adapter);
      */
      // look up the RecyclerView in activity layout
      RecyclerView listView = (RecyclerView)findViewById(R.id.recycler_view);
      // attach the adapter to the RecyclerView to populate items
      listView.setAdapter(mAdapter);
      // set layout manager to position the items
      listView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
      // load data into view
      getPopularMovies();
   }

   private void getPopularMovies() {
      mClient.getPopularMovies(new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            // empty the current list
            int size = mAdapter.getItemCount();
            mMovies.clear();
            mAdapter.notifyItemRangeRemoved(0, size);
            // add all new movies to the empty list
            mMovies.addAll(Movie.fromJSONArray(response));
            mAdapter.notifyItemRangeInserted(0, mMovies.size());
         }
      });
   }
}
