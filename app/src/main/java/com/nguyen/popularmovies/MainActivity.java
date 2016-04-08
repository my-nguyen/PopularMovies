package com.nguyen.popularmovies;

import android.support.v4.app.Fragment;

public class MainActivity extends ContainerActivity {
   @Override
   protected Fragment newFragment() {
      return new MainFragment();
   }
}
