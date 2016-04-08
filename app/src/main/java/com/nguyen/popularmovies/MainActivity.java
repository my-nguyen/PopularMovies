package com.nguyen.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;

public class MainActivity extends ContainerActivity implements MainFragment.Callbacks {
   @Override
   protected Fragment newFragment() {
      return new MainFragment();
   }

   @Override
   protected int getLayoutResId() {
      return R.layout.activity_masterdetail;
   }

   @Override
   public void onMovieSelected(CPMovie movie) {
      if (findViewById(R.id.detail_fragment_container) == null) {
         Intent intent = DetailActivity.newIntent(this, movie);
         startActivity(intent);
      } else {
         Fragment fragment = DetailFragment.newInstance(movie);
         getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container, fragment).commit();
      }
   }
}
