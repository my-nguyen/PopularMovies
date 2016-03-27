package com.nguyen.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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

      // set up Spinner on screen by populating the drop-down list
      Spinner sortOrder = (Spinner)findViewById(R.id.sort_order);
      // create an ArrayAdapter using sort_order_array and a default spinner layout
      ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.sort_order_array, android.R.layout.simple_spinner_item);
      // specify the layout to use when the list of choices appears
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      // apply the adapter to the spinner
      sortOrder.setAdapter(adapter);
      // set the Spinner to the pre-selected Sort Order
      // sortOrder.setSelection(adapter.getPosition(mSettings.sortOrder));
      sortOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // mSettings.sortOrder = position == 0 ? null : parent.getItemAtPosition(position).toString();
            if (position == 0)
               getPopularMovies();
            else
               getTopRatedMovies();
         }
         @Override
         public void onNothingSelected(AdapterView<?> parent) {
         }
      });
   }

   private void getPopularMovies() {
      mClient.getPopularMovies(new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            reloadList(response);
         }
      });
   }

   private void getTopRatedMovies() {
      mClient.getTopRatedMovies(new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            reloadList(response);
         }
      });
   }

   private void reloadList(JSONObject response) {
      // empty the current list
      int size = mAdapter.getItemCount();
      mMovies.clear();
      mAdapter.notifyItemRangeRemoved(0, size);
      // add all new movies to the empty list
      mMovies.addAll(Movie.fromJSONArray(response));
      mAdapter.notifyItemRangeInserted(0, mMovies.size());
   }
}
