package com.nguyen.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by My on 3/26/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
   // provide a direct reference to each of the views within a data item used to cache the views
   // within the item layout for fast access
   public class ViewHolder extends RecyclerView.ViewHolder {
      // your holder should contain a member variable for any view that will be set as you render a row
      public ImageView imageView;
      // we also create a constructor that accepts the entire row and does the view lookups to find
      // each subview
      public ViewHolder(View itemView) {
         // stores the itemView in a public final member variable that can be used to access the
         // context from any ViewHolder instance
         super(itemView);
         imageView = (ImageView)itemView.findViewById(R.id.image);
         itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               CPMovie movie = mMovies.get(getLayoutPosition());
               Intent intent = DetailActivity.newIntent(mContext, movie);
               mContext.startActivity(intent);
            }
         });
      }
   }

   // store a member variable for the movies
   private List<CPMovie> mMovies;
   private Context mContext;

   // pass in the movie array into the constructor
   public RecyclerViewAdapter(List<CPMovie> movies) {
      mMovies = movies;
   }

   // usually involves inflating a layout from XML and returning the holder
   @Override
   public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      mContext = parent.getContext();
      LayoutInflater inflater = LayoutInflater.from(mContext);
      // inflate the custom layout
      View contactView = inflater.inflate(R.layout.item_movie, parent, false);
      // return a new holder instance
      ViewHolder viewHolder = new ViewHolder(contactView);
      return viewHolder;
   }

   // involves populating data into the item through holder
   @Override
   public void onBindViewHolder(ViewHolder holder, int position) {
      // get the data model based on position
      CPMovie movie = mMovies.get(position);
      // set item views based on the data model
      ImageView imageView = holder.imageView;
      String imageUrl = "http://image.tmdb.org/t/p/w185/" + movie.posterPath;
      int widthPixels = mContext.getResources().getDisplayMetrics().widthPixels;
      Picasso.with(mContext).load(imageUrl).resize(widthPixels/2, 0).into(imageView);
   }

   // return the total count of items
   @Override
   public int getItemCount() {
      return mMovies.size();
   }
}
