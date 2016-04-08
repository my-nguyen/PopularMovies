package com.nguyen.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;

public class MainActivity extends ContainerActivity implements MainFragment.Callbacks, DetailFragment.Callbacks {
   @Override
   protected Fragment newFragment() {
      return new MainFragment();
   }

   @Override
   protected int getLayoutResId() {
      return R.layout.activity_masterdetail;
   }

   // implementation of MainFragment.Callbacks interface
   @Override
   public void onMovieSelected(CPMovie movie) {
      if (findViewById(R.id.detail_fragment_container) == null) {
         // if phone interface, replace the current UI (MainFragment) with a new DetailActivity
         Intent intent = DetailActivity.newIntent(this, movie);
         startActivity(intent);
      } else {
         // if tablet interface, put a DetailFragment in detail_fragment_container layout while
         // keeping the fragment_container layout intact
         Fragment fragment = DetailFragment.newInstance(movie);
         getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container, fragment).commit();
      }
   }

   @Override
   public void onMovieUpdated() {
      MainFragment fragment = (MainFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
      fragment.getFavoriteMovies(1);
   }
}
