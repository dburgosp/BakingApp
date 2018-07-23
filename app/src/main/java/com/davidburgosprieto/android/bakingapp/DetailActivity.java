package com.davidburgosprieto.android.bakingapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.davidburgosprieto.android.bakingapp.adapters.IngredientsAdapter;
import com.davidburgosprieto.android.bakingapp.adapters.StepsAdapter;
import com.davidburgosprieto.android.bakingapp.classes.RecipeStep;
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

    // Columns of data that we need to be displayed in the steps list.
    public static final String[] MAIN_STEPS_PROJECTION = {
            RecipesContract.StepsEntry.COLUMN_STEP_ID,
            RecipesContract.StepsEntry.COLUMN_VIDEO_URL,
            RecipesContract.StepsEntry.COLUMN_DESCRIPTION,
            RecipesContract.StepsEntry.COLUMN_SHORT_DESCRIPTION,
    };

    // We store the indices of the values in the array of Strings above to more quickly be able to
    // access the data from our query. If the order of the Strings above changes, these indices
    // must be adjusted to match the order of the Strings.
    public static final int INDEX_COLUMN_STEP_ID = 0;
    public static final int INDEX_COLUMN_VIDEO_URL = 1;
    public static final int INDEX_COLUMN_DESCRIPTION = 2;
    public static final int INDEX_COLUMN_SHORT_DESCRIPTION = 3;

    private IngredientsAdapter mIngredientsAdapter;
    private StepsAdapter mStepsAdapter;
    private int mIngredientsPosition = RecyclerView.NO_POSITION;
    private int mStepsPosition = RecyclerView.NO_POSITION;

    private int mRecipeId, mServings, mStepsNumber;
    private String mRecipeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        // ButterKnife fails for some reason...
        mServingsTextView = findViewById(R.id.detail_servings);
        mIngredientsRecyclerView = findViewById(R.id.detail_ingredients_recyclerview);
        mStepsRecyclerView = findViewById(R.id.detail_steps_recyclerview);

        if (savedInstanceState == null) {
            // Get parameters from calling intent.
            Intent intent = getIntent();
            mRecipeId = intent.getIntExtra(EXTRA_RECIPE_ID, 1);
            mRecipeName = intent.getStringExtra(EXTRA_RECIPE_NAME);
            mServings = intent.getIntExtra(EXTRA_RECIPE_SERVINGS, 1);
        } else {
            // Get parameters from savedInstanceState.
            mRecipeId = savedInstanceState.getInt(EXTRA_RECIPE_ID);
            mRecipeName = savedInstanceState.getString(EXTRA_RECIPE_NAME);
            mServings = savedInstanceState.getInt(EXTRA_RECIPE_SERVINGS);
        }

        setTitle(mRecipeName);
        mServingsTextView.setText(Integer.toString(mServings));

        // Set adapters and recycler views.
        final LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mIngredientsRecyclerView.setLayoutManager(layoutManager);
        mIngredientsRecyclerView.setHasFixedSize(true);
        mIngredientsAdapter = new IngredientsAdapter(this);
        mIngredientsRecyclerView.setAdapter(mIngredientsAdapter);

        // Get ingredients list in a separate thread.
        getSupportLoaderManager().initLoader(LoadersUtils.ID_INGREDIENTS_LOADER, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXTRA_RECIPE_ID, mRecipeId);
        outState.putString(EXTRA_RECIPE_NAME, mRecipeName);
        outState.putInt(EXTRA_RECIPE_SERVINGS, mServings);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Get parameters from savedInstanceState.
        mRecipeId = savedInstanceState.getInt(EXTRA_RECIPE_ID);
        mRecipeName = savedInstanceState.getString(EXTRA_RECIPE_NAME);
        mServings = savedInstanceState.getInt(EXTRA_RECIPE_SERVINGS);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LoadersUtils.ID_INGREDIENTS_LOADER:
                Uri uri = RecipesContract.IngredientsEntry.CONTENT_URI;
                String[] selectionArgs = {Integer.toString(mRecipeId)};
                return new CursorLoader(this,
                        uri,
                        MAIN_INGREDIENTS_PROJECTION,
                        RecipesContract.IngredientsEntry.COLUMN_RECIPE_ID + "=?",
                        selectionArgs,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mIngredientsAdapter.swapCursor(data);
        if (mIngredientsPosition == RecyclerView.NO_POSITION)
            mIngredientsPosition = 0;
        mIngredientsRecyclerView.smoothScrollToPosition(mIngredientsPosition);

        // Get steps list in a separate thread.
        new RecipeStepsList(data.getCount());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mIngredientsAdapter.swapCursor(null);
    }

    // Inner class for retrieving recipe steps in a separate thread.
    class RecipeStepsList implements LoaderManager.LoaderCallbacks<Cursor> {
        public RecipeStepsList(int stepsNumber) {
            mStepsNumber = stepsNumber;

            // Get steps list in a separate thread.
            getSupportLoaderManager().initLoader(LoadersUtils.ID_STEPS_LOADER, null, this);
        }

        /**
         * Instantiate and return a new Loader for the given ID.
         *
         * @param id   The ID whose loader is to be created.
         * @param args Any arguments supplied by the caller.
         * @return Return a new Loader instance that is ready to start loading.
         */
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case LoadersUtils.ID_STEPS_LOADER:
                    Uri uri = RecipesContract.StepsEntry.CONTENT_URI;
                    String[] selectionArgs = {Integer.toString(mRecipeId)};
                    return new CursorLoader(DetailActivity.this,
                            uri,
                            MAIN_STEPS_PROJECTION,
                            RecipesContract.StepsEntry.COLUMN_RECIPE_ID + "=?",
                            selectionArgs,
                            null);

                default:
                    throw new RuntimeException("Loader Not Implemented: " + id);
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            final LinearLayoutManager layoutManager2 =
                    new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.VERTICAL, false);
            mStepsRecyclerView.setLayoutManager(layoutManager2);
            mStepsRecyclerView.setHasFixedSize(true);
            mStepsNumber = data.getCount();
            StepsAdapter.OnItemClickListener onItemClickListener = new StepsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(RecipeStep step, View clickedView) {
                    Intent intent = new Intent(DetailActivity.this, StepActivity.class);
                    intent.putExtra(StepActivity.RECIPE_ID_EXTRA, mRecipeId);
                    intent.putExtra(StepActivity.RECIPE_NAME_EXTRA, mRecipeName);
                    intent.putExtra(StepActivity.VIDEO_URL_EXTRA, step.getmVideoURL());
                    intent.putExtra(StepActivity.DESCRIPTION_EXTRA, step.getmDescription());
                    intent.putExtra(StepActivity.SHORT_DESCRIPTION_EXTRA, step.getmShortDescription());
                    intent.putExtra(StepActivity.STEP_ID_EXTRA, step.getmId());
                    intent.putExtra(StepActivity.STEPS_NUMBER_EXTRA, mStepsNumber);
                    startActivityForResult(intent, 1);
                }
            };
            mStepsAdapter = new StepsAdapter(DetailActivity.this, mRecipeName, onItemClickListener);
            mStepsRecyclerView.setAdapter(mStepsAdapter);

            mStepsAdapter.swapCursor(data);
            if (mStepsPosition == RecyclerView.NO_POSITION)
                mStepsPosition = 0;
            mStepsRecyclerView.smoothScrollToPosition(mStepsPosition);
        }

        /**
         * Called when a previously created loader is being reset, and thus
         * making its data unavailable.  The application should at this point
         * remove any references it has to the Loader's data.
         *
         * @param loader The Loader that is being reset.
         */
        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mStepsAdapter.swapCursor(null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // Get parameters from savedInstanceState.
            mRecipeName = data.getStringExtra(StepActivity.RECIPE_NAME_EXTRA);
        }
    }
}
