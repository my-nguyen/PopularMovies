package com.nguyen.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

/**
 * Created by My on 3/26/2016.
 */
public class DetailActivity extends AppCompatActivity {
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_detail);

      final Movie movie = (Movie)Parcels.unwrap(getIntent().getParcelableExtra("MOVIE_IN"));
      TextView title = (TextView)findViewById(R.id.title);
      title.setText(movie.originalTitle);
      ImageView poster = (ImageView)findViewById(R.id.poster);
      String imageUrl = "http://image.tmdb.org/t/p/w185/" + movie.posterPath;
      Picasso.with(this).load(imageUrl).into(poster);
      TextView year = (TextView)findViewById(R.id.year);
      String yearText = movie.releaseDate.split("-")[0];
      year.setText(yearText);
      TextView average = (TextView)findViewById(R.id.average);
      String averageText = movie.voteAverage + "/10";
      average.setText(averageText);
      TextView synopsis = (TextView)findViewById(R.id.synopsis);
      synopsis.setText(movie.overview);
   }

   public static Intent newIntent(Context context, Movie movie) {
      Intent intent = new Intent(context, DetailActivity.class);
      intent.putExtra("MOVIE_IN", Parcels.wrap(movie));
      return intent;
   }
}
