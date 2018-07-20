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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static final String RECIPES_URL = "https://go.udacity.com/android-baking-app-json";

    /**
     * Create a private constructor because no one should ever create a {@link NetworkUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name NetworkUtils (and an object instance of NetworkUtils is not
     * needed).
     */
    private NetworkUtils() {
    }

    /**
     * Helper method to determine if there is an active network connection.
     *
     * @return true if we are connected to the internet, false otherwise.
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        try {
            activeNetwork = cm.getActiveNetworkInfo();
        } catch (NullPointerException e) {
            Log.e(TAG, "(isConnected) Error getting active network info: " + e);
        }
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Private helper method for building a Url from a given Uri.
     *
     * @param builtUri is the given Uri.
     * @return the Url built from the given Uri.
     */
    public static URL buildUrlFromUri(Uri builtUri) {
        URL url = null;
        try {
            url = new URL(builtUri.toString());
            Log.i(TAG, "(buildUrlFromUri) Built URL: " + url);
        } catch (MalformedURLException e) {
            Log.e(TAG, "(buildUrlFromUri) Error building URL: " + e);
        }
        return url;
    }

    /**
     * Retrieves a JSON document from an Uri.
     *
     * @param builtUri is the uniform resource identifier (uri) for retrieving the JSON document.
     * @return a String with the JSON document.
     * @throws java.io.IOException from url.openConnection().
     */
    public static String getJSONresponse(Uri builtUri) throws java.io.IOException {
        URL url = buildUrlFromUri(builtUri);
        //URL url = new URL(Uri.parse(RECIPES_URL).toString());
        Log.i(TAG, "(getJSONresponse) URL: " + url);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Helper method to get the String value of a key into a JSON object.
     *
     * @param jsonObject is the JSON object that is being parsed.
     * @param key        is the key
     *                   to search into the JSON object.
     * @return the String value associated to the given key, or an empty String if the key does not
     * exist.
     */
    static public String getStringFromJSON(JSONObject jsonObject, String key) {
        String value = "";
        if (!jsonObject.isNull(key)) try {
            value = jsonObject.getString(key);
        } catch (JSONException e) {
            // If an error is thrown when executing the "try" block, catch the exception here, so
            // the app doesn't crash. Print a log message with the message from the exception.
            Log.e(TAG, "(getStringFromJSON) Error parsing JSON response: ", e);
        }
        return value;
    }

    /**
     * Helper method to get the int value of a key into a JSON object.
     *
     * @param jsonObject is the JSON object that is being parsed.
     * @param key        is the key to search into the JSON object.
     * @return the int value associated to the given key, or 0 if the key does not exist.
     */
    static public int getIntFromJSON(JSONObject jsonObject, String key) {
        int value = 0;
        if (!jsonObject.isNull(key)) try {
            value = jsonObject.getInt(key);
        } catch (JSONException e) {
            // If an error is thrown when executing the "try" block, catch the exception here, so
            // the app doesn't crash. Print a log message with the message from the exception.
            Log.e(TAG, "(getIntFromJSON) Error parsing JSON response: ", e);
        }
        return value;
    }

    /**
     * Helper method to get the boolean value of a key into a JSON object.
     *
     * @param jsonObject is the JSON object that is being parsed.
     * @param key        is the key to search into the JSON object.
     * @return the boolean value associated to the given key, or false if the key does not exist.
     */
    static public boolean getBooleanFromJSON(JSONObject jsonObject, String key) {
        boolean value = false;
        if (!jsonObject.isNull(key)) try {
            value = jsonObject.getBoolean(key);
        } catch (JSONException e) {
            // If an error is thrown when executing the "try" block, catch the exception here, so
            // the app doesn't crash. Print a log message with the message from the exception.
            Log.e(TAG, "(getBooleanFromJSON) Error parsing JSON response: ", e);
        }
        return value;
    }

    /**
     * Helper method to get the Double value of a key into a JSON object.
     *
     * @param jsonObject is the JSON object that is being parsed.
     * @param key        is the key to search into the JSON object.
     * @return the Double value associated to the given key, or 0.0 if the key does not exist.
     */
    static public Double getDoubleFromJSON(JSONObject jsonObject, String key) {
        Double value = 0.0;
        if (!jsonObject.isNull(key)) try {
            value = jsonObject.getDouble(key);
        } catch (JSONException e) {
            // If an error is thrown when executing the "try" block, catch the exception here, so
            // the app doesn't crash. Print a log message with the message from the exception.
            Log.e(TAG, "(getBooleanFromJSON) Error parsing JSON response: ", e);
        }
        return value;
    }
}