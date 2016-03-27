package com.nguyen.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
   int mPage = 1;
   enum SortCriteria { LATEST, NOW_PLAYING, POPULAR, TOP_RATED, UPCOMING }
   SortCriteria mCriteria = SortCriteria.POPULAR;

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
      GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2);
      // set layout manager to position the items
      listView.setLayoutManager(layoutManager);
      // add scroll listener
      listView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
         @Override
         public void onLoadMore(int page, int totalItemsCount) {
            // triggered only when new data needs to be appended to the list
            // add whatever code is needed to append new items to the bottom of the list
            mPage++;
            switch (mCriteria) {
               case LATEST:
                  break;
               case NOW_PLAYING:
                  break;
               case POPULAR:
                  getPopularMovies(mPage);
                  break;
               case TOP_RATED:
                  getTopRatedMovies(mPage);
                  break;
               case UPCOMING:
                  break;
            }
         }
      });
      // load data into view
      // getPopularMovies(mPage);

      // set up Spinner on screen by populating the drop-down list
      Spinner sortOrder = (Spinner)findViewById(R.id.sort_order);
      // create an ArrayAdapter using sort_order_array and a default spinner layout
      ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.sort_order_array, android.R.layout.simple_spinner_item);
      // specify the layout to use when the list of choices appears
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      // apply the adapter to the spinner
      sortOrder.setAdapter(adapter);
      // set the Spinner to the "Popular" sort criteria by default
      sortOrder.setSelection(adapter.getPosition("Popular"));
      sortOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mPage = 1;
            switch (position) {
               case 0:
                  mCriteria = SortCriteria.LATEST;
                  // getLatestMovies(mPage);
                  break;
               case 1:
                  mCriteria = SortCriteria.NOW_PLAYING;
                  // getNowPlayingMovies(mPage);
                  break;
               case 2:
                  mCriteria = SortCriteria.POPULAR;
                  getPopularMovies(mPage);
                  break;
               case 3:
                  mCriteria = SortCriteria.TOP_RATED;
                  getTopRatedMovies(mPage);
                  break;
               case 4:
                  mCriteria = SortCriteria.UPCOMING;
                  // getUpcomingMovies(mPage);
                  break;
            }
         }
         @Override
         public void onNothingSelected(AdapterView<?> parent) {
         }
      });
   }

   private void getPopularMovies(final int page) {
      mClient.getPopularMovies(page, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            reloadList(page, response);
         }
      });
   }

   private void getTopRatedMovies(final int page) {
      mClient.getTopRatedMovies(page, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            reloadList(page, response);
         }
      });
   }

   private void reloadList(int page, JSONObject response) {
      // if this is a new request, empty the current list
      int size = mAdapter.getItemCount();
      if (page == 1 && size != 0) {
         mMovies.clear();
         mAdapter.notifyItemRangeRemoved(0, size-1);
      }
      // add all new movies to the current list
      size = mAdapter.getItemCount();
      mMovies.addAll(Movie.fromJSONArray(response));
      mAdapter.notifyItemRangeInserted(size, mMovies.size()-1);
   }
}
