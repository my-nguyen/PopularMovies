package com.nguyen.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by My on 3/28/2016.
 */
public class Review {
   String id;
   String author;
   String content;

   public static Review fromJSONObject(JSONObject jsonObject) {
      Review review = new Review();
      try {
         review.id = jsonObject.getString("id");
         review.author = jsonObject.getString("author");
         review.content = jsonObject.getString("content");
      } catch (JSONException e) {
         e.printStackTrace();
      }

      return review;
   }

   public static List<Review> fromJSONArray(JSONObject response) {
      List<Review> reviews = new ArrayList<>();
      try {
         JSONArray jsonArray = response.getJSONArray("results");
         for (int i = 0; i < jsonArray.length(); i++) {
            try {
               JSONObject jsonObject = jsonArray.getJSONObject(i);
               Review review = fromJSONObject(jsonObject);
               if (review != null)
                  reviews.add(review);
            } catch (JSONException e) {
               e.printStackTrace();
               // keep deserializing the next JSONObject in the JSONArray even if the deserialization
               // of the current JSONObject fails
               continue;
            }
         }
      } catch (JSONException e) {
         e.printStackTrace();
      }

      return reviews;
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("<id: ").append(id)
            .append("><author: ").append(author)
            .append("><content: ").append(content).append(">\n");
      return builder.toString();
   }
}
