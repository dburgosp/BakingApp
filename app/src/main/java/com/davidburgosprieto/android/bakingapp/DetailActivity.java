package com.davidburgosprieto.android.bakingapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.davidburgosprieto.android.bakingapp.adapters.IngredientsAdapter;
import com.davidburgosprieto.android.bakingapp.adapters.RecipesAdapter;
import com.davidburgosprieto.android.bakingapp.data.RecipesContract;
import com.davidburgosprieto.android.bakingapp.utilities.LoadersUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String TAG = DetailActivity.class.getSimpleName();

    public final static String EXTRA_RECIPE_ID = "EXTRA_RECIPE_ID";
    public final static String EXTRA_RECIPE_NAME = "EXTRA_RECIPE_NAME";
    public final static String EXTRA_RECIPE_SERVINGS = "EXTRA_RECIPE_SERVINGS";

    // Data binding.
    @BindView(R.id.detail_ingredients_recyclerview)
    RecyclerView mIngredientsRecyclerView;
    @BindView(R.id.detail_steps_recyclerview)
    RecyclerView mStepsRecyclerView;
    @BindView(R.id.detail_servings)
    TextView mServingsTextView;

    // Columns of data that we need to be displayed in the recipes list.
    public static final String[] MAIN_INGREDIENTS_PROJECTION = {
            RecipesContract.IngredientsEntry.COLUMN_INGREDIENT,
            RecipesContract.IngredientsEntry.COLUMN_QUANTITY,
            RecipesContract.IngredientsEntry.COLUMN_MEASURE,
    };

    // We store the indices of the values in the array of Strings above to more quickly be able to
    // access the data from our query. If the order of the Strings above changes, these indices
    // must be adjusted to match the order of the Strings.
    public static final int INDEX_COLUMN_INGREDIENT = 0;
    public static final int INDEX_COLUMN_QUANTITY = 1;
    public static final int INDEX_COLUMN_MEASURE = 2;

    private IngredientsAdapter mIngredientsAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    private int mRecipeId;
    private String mRecipeName;
    private int mServings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        // ButterKnife fails for some reason...
        mIngredientsRecyclerView = findViewById(R.id.detail_ingredients_recyclerview);
        mServingsTextView = findViewById(R.id.detail_servings);

        // Get parameters.
        Intent intent = getIntent();
        mRecipeId = intent.getIntExtra(EXTRA_RECIPE_ID, 1);
        mRecipeName = intent.getStringExtra(EXTRA_RECIPE_NAME);
        mServings = intent.getIntExtra(EXTRA_RECIPE_SERVINGS, 1);
        setTitle(mRecipeName);
        mServingsTextView.setText(Integer.toString(mServings));

        // Set adapters and recycler views.
        final LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mIngredientsRecyclerView.setLayoutManager(layoutManager);
        mIngredientsRecyclerView.setHasFixedSize(true);
        mIngredientsAdapter = new IngredientsAdapter(this);
        mIngredientsRecyclerView.setAdapter(mIngredientsAdapter);
        getSupportLoaderManager().initLoader(LoadersUtils.ID_INGREDIENTS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LoadersUtils.ID_INGREDIENTS_LOADER:
                Uri uri = RecipesContract.IngredientsEntry.CONTENT_URI;
                return new CursorLoader(this,
                        uri,
                        MAIN_INGREDIENTS_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mIngredientsAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION)
            mPosition = 0;
        mIngredientsRecyclerView.smoothScrollToPosition(mPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mIngredientsAdapter.swapCursor(null);
    }
}
