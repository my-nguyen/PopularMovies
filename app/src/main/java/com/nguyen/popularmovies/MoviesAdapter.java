package com.nguyen.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by My on 3/26/2016.
 */
public class MoviesAdapter extends ArrayAdapter<CPMovie> {
   public MoviesAdapter(Context context, List<CPMovie> movies) {
      super(context, 0, movies);
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
      // get the data item for this position
      CPMovie movie = getItem(position);
      // check if an existing view is being reused, otherwise inflate the view
      if (convertView == null)
         convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_movie, parent, false);
      // look up view for data population
      ImageView imageView = (ImageView)convertView.findViewById(R.id.image);
      // populate the data into the template view using the data object
      String imageUrl = "http://image.tmdb.org/t/p/w185/" + movie.posterPath;
      int widthPixels = getContext().getResources().getDisplayMetrics().widthPixels;
      Picasso.with(getContext()).load(imageUrl).resize(widthPixels/2, 0).into(imageView);
      // return the completed view to render on screen
      return convertView;
   }
}
