package com.davidburgosprieto.android.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.davidburgosprieto.android.bakingapp.asynctaskloaders.RecipesAsyncTaskLoader;
import com.davidburgosprieto.android.bakingapp.classes.Recipe;
import com.davidburgosprieto.android.bakingapp.data.RecipesContract;
import com.davidburgosprieto.android.bakingapp.utilities.LoadersUtils;
import com.davidburgosprieto.android.bakingapp.utilities.NetworkUtils;
import com.davidburgosprieto.android.bakingapp.utilities.RecipesJsonUtils;

import java.text.NumberFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<Recipe>> {

    private static final String TAG = MainActivity.class.getSimpleName();
    ArrayList<Recipe> recipeArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportLoaderManager().initLoader(LoadersUtils.RECIPES_FROM_INTERNET_LOADER_ID, null, this);
    }

    @Override
    public Loader<ArrayList<Recipe>> onCreateLoader(int id, Bundle args) {
        if (NetworkUtils.isConnected(MainActivity.this)) {
            return new RecipesAsyncTaskLoader(MainActivity.this);
        } else {
            // There is no connection. Show error message.
            Log.i(TAG, "(onCreateLoader) No internet connection.");
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Recipe>> loader, ArrayList<Recipe> data) {
        // Insert recipes in database.
        recipeArrayList = data;
        new recipesToDatabase(data);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Recipe>> loader) {

    }

    // Inner class for managing db inserts.
    class recipesToDatabase implements LoaderManager.LoaderCallbacks<Cursor> {
        private ArrayList<Recipe> mRecipeArrayList;
        public final String[] MAIN_RECIPES_PROJECTION = {
                RecipesContract.RecipesEntry.COLUMN_RECIPE_ID,
                RecipesContract.RecipesEntry.COLUMN_NAME,
                RecipesContract.RecipesEntry.COLUMN_SERVINGS,
                RecipesContract.RecipesEntry.COLUMN_IMAGE,
        };

        public recipesToDatabase(ArrayList<Recipe> recipeArrayList) {
            mRecipeArrayList = recipeArrayList;
            getSupportLoaderManager().initLoader(LoadersUtils.RECIPES_TO_DATABASE_LOADER_ID, null, this);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Uri recipesQueryUri = RecipesContract.RecipesEntry.CONTENT_URI;
            String sortOrder = RecipesContract.RecipesEntry.COLUMN_RECIPE_ID + " ASC";
            Loader<Cursor> cursorLoader = new CursorLoader(MainActivity.this,
                    recipesQueryUri,
                    MAIN_RECIPES_PROJECTION,
                    null,
                    null,
                    sortOrder);
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Intent intent = new Intent(MainActivity.this, RecipesActivity.class);
            startActivity(intent);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}
