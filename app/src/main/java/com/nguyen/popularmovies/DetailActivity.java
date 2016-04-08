package com.nguyen.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import org.parceler.Parcels;

/**
 * Created by My on 3/26/2016.
 */
public class DetailActivity extends ContainerActivity implements DetailFragment.Callbacks {
   @Override
   protected Fragment newFragment() {
      CPMovie movie = (CPMovie)Parcels.unwrap(getIntent().getParcelableExtra("MOVIE_IN"));
      return DetailFragment.newInstance(movie);
   }

   public static Intent newIntent(Context context, CPMovie movie) {
      Intent intent = new Intent(context, DetailActivity.class);
      intent.putExtra("MOVIE_IN", Parcels.wrap(movie));
      return intent;
   }

   // empty implementation for DetailFragment.Callbacks, since DetailActivity is also a host for
   // DetailFragment
   @Override
   public void onMovieUpdated() {
   }
}
