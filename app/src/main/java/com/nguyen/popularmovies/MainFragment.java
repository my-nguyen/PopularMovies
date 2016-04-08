package com.nguyen.popularmovies;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainFragment extends Fragment {
   TMDBClient mClient = TMDBClient.getInstance();
   // data source, which needs to be an empty ArrayList and not NULL
   List<CPMovie> mMovies = new ArrayList<>();
   // bind data source to adapter
   // RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(mMovies);
   RecyclerViewAdapter mAdapter;
   int mPage = 1;
   enum SortCriteria { NOW_PLAYING, POPULAR, TOP_RATED, UPCOMING, FAVORITE }
   SortCriteria mCriteria = SortCriteria.POPULAR;
   private Callbacks mCallbacks;

   // required interface for hosting activities
   public interface Callbacks {
      void onMovieSelected(CPMovie movie);
   }

   @Override
   public void onAttach(Activity activity) {
      super.onAttach(activity);
      mCallbacks = (Callbacks)activity;
   }

   @Override
   public void onDetach() {
      super.onDetach();
      mCallbacks = null;
   }

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_main, container, false);

      /*
      // unsuccessful attempt at using GridView and custom ArrayAdapter (MoviesAdapter)
      // create the adapter to convert the array to views
      MoviesAdapter adapter = new MoviesAdapter(MainActivity.this, mMovies);
      // attach the adapter to a ListView
      GridView listView = (GridView) findViewById(R.id.grid_view);
      listView.setAdapter(adapter);
      */
      // look up the RecyclerView in activity layout
      RecyclerView listView = (RecyclerView)view.findViewById(R.id.recycler_view);
      // attach the adapter to the RecyclerView to populate items
      mAdapter = new RecyclerViewAdapter(mMovies, mCallbacks);
      listView.setAdapter(mAdapter);
      GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
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
               case NOW_PLAYING:
                  getNowPlayingMovies(mPage);
                  break;
               case POPULAR:
                  getPopularMovies(mPage);
                  break;
               case TOP_RATED:
                  getTopRatedMovies(mPage);
                  break;
               case UPCOMING:
                  getUpcomingMovies(mPage);
                  break;
               case FAVORITE:
                  getFavoriteMovies(mPage);
                  break;
            }
         }
      });

      CPMovie.setContentResolver(getActivity().getContentResolver());

      // set up Spinner on screen by populating the drop-down list
      Spinner sortOrder = (Spinner)view.findViewById(R.id.sort_order);
      // create an ArrayAdapter using sort_order_array and a default spinner layout
      ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
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
                  mCriteria = SortCriteria.NOW_PLAYING;
                  getNowPlayingMovies(mPage);
                  break;
               case 1:
                  mCriteria = SortCriteria.POPULAR;
                  getPopularMovies(mPage);
                  break;
               case 2:
                  mCriteria = SortCriteria.TOP_RATED;
                  getTopRatedMovies(mPage);
                  break;
               case 3:
                  mCriteria = SortCriteria.UPCOMING;
                  getUpcomingMovies(mPage);
                  break;
               case 4:
                  mCriteria = SortCriteria.FAVORITE;
                  getFavoriteMovies(mPage);
                  break;
            }
         }
         @Override
         public void onNothingSelected(AdapterView<?> parent) {
         }
      });

      return view;
   }

   private void getNowPlayingMovies(final int page) {
      mClient.getNowPlayingMovies(page, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            reloadList(page, response);
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

   private void getUpcomingMovies(final int page) {
      mClient.getUpcomingMovies(page, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            reloadList(page, response);
         }
      });
   }

   // load favorite movies from local database
   private void getFavoriteMovies(int page) {
      reloadList(page, CPMovie.query());
   }

   // load movies from TMDB.org
   private void reloadList(int page, JSONObject response) {
      reloadList(page, CPMovie.fromJSONArray(response));
   }

   private void reloadList(int page, List<CPMovie> movies) {
      int size = mAdapter.getItemCount();
      // if this is a new request, empty the current list
      if (page == 1 && size != 0) {
         mMovies.clear();
         mAdapter.notifyItemRangeRemoved(0, size - 1);
         size = 0;
         // need to call notifyDataSetChanged() for the case of empty favorite list
         mAdapter.notifyDataSetChanged();
      }
      // add all new movies to the current list
      // movies.size() is zero when it's the favorite list
      if (movies.size() != 0) {
         mMovies.addAll(movies);
         mAdapter.notifyItemRangeInserted(size, mMovies.size() - 1);
      }
   }
}
