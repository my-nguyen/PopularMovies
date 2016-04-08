package com.nguyen.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by My on 3/26/2016.
 */
public class DetailFragment extends Fragment {
   private CPMovie dbMovie = null;
   private Callbacks mCallbacks;

   public interface Callbacks {
      void onMovieUpdated();
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
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      final View view = inflater.inflate(R.layout.fragment_detail, container, false);

      final CPMovie movie = (CPMovie)Parcels.unwrap(getArguments().getParcelable("MOVIE_IN"));
      TextView title = (TextView)view.findViewById(R.id.title);
      title.setText(movie.originalTitle);
      ImageView poster = (ImageView)view.findViewById(R.id.poster);
      String imageUrl = "http://image.tmdb.org/t/p/w185/" + movie.posterPath;
      Picasso.with(getActivity()).load(imageUrl).into(poster);
      TextView year = (TextView)view.findViewById(R.id.year);
      String yearText = movie.releaseDate.split("-")[0];
      year.setText(yearText);
      TextView average = (TextView)view.findViewById(R.id.average);
      String averageText = String.format("%.1f", movie.voteAverage) + "/10";
      final ImageButton favorite = (ImageButton)view.findViewById(R.id.favorite);
      final ColorFilter gray = favorite.getColorFilter();

      CPMovie.setContentResolver(getActivity().getContentResolver());
      dbMovie = CPMovie.query(movie.id);
      if (dbMovie != null)
         favorite.setColorFilter(Color.RED);
      favorite.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (dbMovie == null) {
               favorite.setColorFilter(Color.RED);
               movie.save();
               dbMovie = movie;
            } else {
               favorite.setColorFilter(gray);
               dbMovie.delete();
               dbMovie = null;
            }
            // if it's a tablet, update the Favorite list on the left panel
            mCallbacks.onMovieUpdated();
         }
      });
      average.setText(averageText);
      TextView synopsis = (TextView)view.findViewById(R.id.synopsis);
      synopsis.setText(movie.overview);

      TMDBClient client = TMDBClient.getInstance();
      client.getTrailers(movie.id, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            final List<Trailer> trailers = Trailer.fromJSONArray(response);
            LinearLayout XMLLayout = (LinearLayout) view.findViewById(R.id.trailers);
            for (final Trailer trailer : trailers) {
               RelativeLayout dynamicLayout = new RelativeLayout(getActivity());
               RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                     RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
               dynamicLayout.setLayoutParams(layoutParams);

               ImageButton button = new ImageButton(getActivity());
               button.setImageResource(R.drawable.trailer_play);
               button.setBackgroundColor(Color.TRANSPARENT);
               // button.setId() is necessary for nameParams.addRule() below
               button.setId(1);
               RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                     ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
               buttonParams.setMargins(20, 0, 20, 0);
               dynamicLayout.addView(button, buttonParams);
               button.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                     Uri uri = Uri.parse("http://www.youtube.com/watch?v=" + trailer.key);
                     startActivity(new Intent(Intent.ACTION_VIEW, uri));
                  }
               });

               TextView name = new TextView(getActivity());
               name.setText(trailer.name);
               name.setGravity(Gravity.CENTER);
               RelativeLayout.LayoutParams nameParams = new RelativeLayout.LayoutParams(
                     ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
               nameParams.addRule(RelativeLayout.RIGHT_OF, button.getId());
               nameParams.addRule(RelativeLayout.ALIGN_TOP, button.getId());
               nameParams.addRule(RelativeLayout.ALIGN_BOTTOM, button.getId());
               nameParams.setMarginStart(30);
               dynamicLayout.addView(name, nameParams);

               XMLLayout.addView(dynamicLayout);
            }
         }
      });

      client.getReviews(movie.id, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            List<Review> reviews = Review.fromJSONArray(response);
            LinearLayout XMLLayout = (LinearLayout) view.findViewById(R.id.reviews);
            for (Review review : reviews) {
               LinearLayout dynamicLayout = new LinearLayout(getActivity());
               dynamicLayout.setOrientation(LinearLayout.VERTICAL);
               dynamicLayout.setPadding(20, 0, 20, 0);

               TextView author = new TextView(getActivity());
               Spanned html = Html.fromHtml("<i>by <b>" + review.author + ":</b></i>");
               author.setText(html);
               dynamicLayout.addView(author);

               TextView content = new TextView(getActivity());
               content.setText(review.content);
               content.setPadding(0, 20, 0, 20);
               dynamicLayout.addView(content);

               View separator = new View(getActivity());
               separator.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
               separator.setBackgroundColor(Color.DKGRAY);
               dynamicLayout.addView(separator);

               XMLLayout.addView(dynamicLayout);
            }
         }
      });

      return view;
   }

   public static DetailFragment newInstance(CPMovie movie) {
      DetailFragment fragment = new DetailFragment();
      Bundle bundle = new Bundle();
      bundle.putParcelable("MOVIE_IN", Parcels.wrap(movie));
      fragment.setArguments(bundle);
      return fragment;
   }
}
