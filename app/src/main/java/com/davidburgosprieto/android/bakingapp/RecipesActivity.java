package com.davidburgosprieto.android.bakingapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.davidburgosprieto.android.bakingapp.adapters.RecipesAdapter;
import com.davidburgosprieto.android.bakingapp.classes.Recipe;
import com.davidburgosprieto.android.bakingapp.data.RecipesContract;
import com.davidburgosprieto.android.bakingapp.utilities.LoadersUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String TAG = RecipesActivity.class.getSimpleName();

    // Data binding.
    @BindView(R.id.recipes_reciclerview)
    RecyclerView mRecyclerView;

    // Columns of data that we need to be displayed in the recipes list.
    public static final String[] MAIN_RECIPES_PROJECTION = {
            RecipesContract.RecipesEntry.COLUMN_RECIPE_ID,
            RecipesContract.RecipesEntry.COLUMN_NAME,
            RecipesContract.RecipesEntry.COLUMN_SERVINGS,
    };

    // We store the indices of the values in the array of Strings above to more quickly be able to
    // access the data from our query. If the order of the Strings above changes, these indices
    // must be adjusted to match the order of the Strings.
    public static final int INDEX_COLUMN_RECIPE_ID = 0;
    public static final int INDEX_COLUMN_NAME = 1;
    public static final int INDEX_COLUMN_SERVINGS = 2;

    private RecipesAdapter mRecipesAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        ButterKnife.bind(this);

        final LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // ButterKnife fails for some reason...
        mRecyclerView = findViewById(R.id.recipes_reciclerview);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecipesAdapter = new RecipesAdapter(this);
        mRecyclerView.setAdapter(mRecipesAdapter);
        getSupportLoaderManager().initLoader(LoadersUtils.ID_RECIPES_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LoadersUtils.ID_RECIPES_LOADER:
                Uri uri = RecipesContract.RecipesEntry.CONTENT_URI;
                String sortOrder = RecipesContract.RecipesEntry.COLUMN_RECIPE_ID + " ASC";
                return new CursorLoader(this,
                        uri,
                        MAIN_RECIPES_PROJECTION,
                        null,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mRecipesAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION)
            mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecipesAdapter.swapCursor(null);
    }
}
