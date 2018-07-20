/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.davidburgosprieto.android.bakingapp.utilities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import com.davidburgosprieto.android.bakingapp.classes.Recipe;
import com.davidburgosprieto.android.bakingapp.classes.RecipeIngredient;
import com.davidburgosprieto.android.bakingapp.classes.RecipeStep;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Utility functions to handle recipes JSON data.
 */
public final class RecipesJsonUtils {

    private static final String TAG = RecipesJsonUtils.class.getSimpleName();

    public static ArrayList<Recipe> getRecipes() {
        /* ------------ */
        /* Get the JSON */
        /* ------------ */

        Uri builtUri = Uri.parse(NetworkUtils.RECIPES_URL).buildUpon().build();

        // Use the built Uri to get the JSON document with the results of the query.
        String JSONresponse;
        try {
            JSONresponse = NetworkUtils.getJSONresponse(builtUri);
        } catch (java.io.IOException e) {
            // If getJSONresponse has thrown an exception, exit returning null.
            Log.e(TAG, "(getRecipes) Error retrieving JSON response: ", e);
            return null;
        }

        /* -------------- */
        /* Parse the JSON */
        /* -------------- */

        // If the JSON string is empty or null, then return null.
        if (TextUtils.isEmpty(JSONresponse)) {
            Log.i(TAG, "(getRecipes) The JSON string is empty.");
            return null;
        }

        // Create an empty array of Recipe objects to append data.
        ArrayList<Recipe> recipes = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON is
        // formatted, a JSONException exception object will be thrown.
        try {
            // Create a JSONObject from the JSON response string.
            JSONArray jsonArray = new JSONArray(JSONresponse);

            // Get results array.
            JSONObject jsonObject;
            for (int n = 0; n < jsonArray.length(); n++) {
                // Get a single result at position n within the list of results, extract the movie
                // info and append it to the movies_menu array.
                jsonObject = jsonArray.getJSONObject(n);

                int id = NetworkUtils.getIntFromJSON(jsonObject, "id");
                String name = NetworkUtils.getStringFromJSON(jsonObject, "name");
                int servings = NetworkUtils.getIntFromJSON(jsonObject, "servings");
                String image = NetworkUtils.getStringFromJSON(jsonObject, "image");

                // Get ingredients.
                ArrayList<RecipeIngredient> ingredientArrayList = new ArrayList<>();
                JSONArray ingredientsJsonArray = jsonObject.getJSONArray("ingredients");
                JSONObject ingredientsJsonObject;
                for (int i = 0; i < ingredientsJsonArray.length(); i++) {
                    ingredientsJsonObject = ingredientsJsonArray.getJSONObject(i);
                    double quantity = NetworkUtils.getDoubleFromJSON(ingredientsJsonObject, "quantity");
                    String measure = NetworkUtils.getStringFromJSON(ingredientsJsonObject, "measure");
                    String ingredient = NetworkUtils.getStringFromJSON(ingredientsJsonObject, "ingredient");
                    RecipeIngredient recipeIngredient = new RecipeIngredient(id, quantity, measure, ingredient);
                    ingredientArrayList.add(recipeIngredient);
                }

                // Get steps.
                ArrayList<RecipeStep> stepArrayList = new ArrayList<>();
                JSONArray stepsJsonArray = jsonObject.getJSONArray("steps");
                JSONObject stepsJsonObject;
                for (int i = 0; i < stepsJsonArray.length(); i++) {
                    stepsJsonObject = stepsJsonArray.getJSONObject(i);
                    int stepId = NetworkUtils.getIntFromJSON(stepsJsonObject, "id");
                    String shortDescription = NetworkUtils.getStringFromJSON(stepsJsonObject, "shortDescription");
                    String description = NetworkUtils.getStringFromJSON(stepsJsonObject, "description");
                    String videoURL = NetworkUtils.getStringFromJSON(stepsJsonObject, "videoURL");
                    String thumbnailURL = NetworkUtils.getStringFromJSON(stepsJsonObject, "thumbnailURL");
                    RecipeStep recipeStep = new RecipeStep(id, stepId, shortDescription, description, videoURL, thumbnailURL);
                    stepArrayList.add(recipeStep);
                }

                Recipe recipe = new Recipe(id, name, ingredientArrayList, stepArrayList, servings, image);
                recipes.add(recipe);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash.
            Log.e(TAG, "(getRecipes) Error parsing the JSON response: ", e);
        }

        // Return the movies_menu array.
        return recipes;
    }
}