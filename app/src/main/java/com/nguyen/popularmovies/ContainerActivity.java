package com.nguyen.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public abstract class ContainerActivity extends FragmentActivity {
   protected abstract Fragment newFragment();

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_container);

      /*
      // the following approach is advocated by Android Programming: The Big Nerd Ranch Guide, 2nd
      // edition by Bill Phillipos et al.
      FragmentManager manager = getSupportFragmentManager();
      Fragment fragment = manager.findFragmentById(R.id.fragment_container);
      if (fragment == null) {
         fragment = newFragment();
         manager.beginTransaction().add(R.id.fragment_container, fragment).commit();
      }
      */
      // the following approach is advocated by CodePath.com; either approach works fine.
      // begin the transaction
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      // replace the contents of the container with the new fragment
      transaction.replace(R.id.fragment_container, newFragment());
      // complete the changes added above
      transaction.commit();
   }
}
