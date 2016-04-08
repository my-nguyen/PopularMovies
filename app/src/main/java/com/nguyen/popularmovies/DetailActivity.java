package com.nguyen.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import org.parceler.Parcels;

/**
 * Created by My on 3/26/2016.
 */
public class DetailActivity extends FragmentActivity {
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_detail);

      FragmentManager manager = getSupportFragmentManager();
      Fragment fragment = manager.findFragmentById(R.id.fragment_container);
      if (fragment == null) {
         CPMovie movie = (CPMovie)Parcels.unwrap(getIntent().getParcelableExtra("MOVIE_IN"));
         fragment = DetailFragment.newInstance(movie);
         manager.beginTransaction().add(R.id.fragment_container, fragment).commit();
      }
   }

   public static Intent newIntent(Context context, CPMovie movie) {
      Intent intent = new Intent(context, DetailActivity.class);
      intent.putExtra("MOVIE_IN", Parcels.wrap(movie));
      return intent;
   }
}
