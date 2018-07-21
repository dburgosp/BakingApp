package com.davidburgosprieto.android.bakingapp.asynctaskloaders;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.os.OperationCanceledException;
import android.text.format.DateUtils;
import android.util.Log;

import com.davidburgosprieto.android.bakingapp.classes.Recipe;
import com.davidburgosprieto.android.bakingapp.classes.RecipeIngredient;
import com.davidburgosprieto.android.bakingapp.classes.RecipeStep;
import com.davidburgosprieto.android.bakingapp.data.RecipesContract;
import com.davidburgosprieto.android.bakingapp.utilities.RecipesJsonUtils;

import java.util.ArrayList;

public class RecipesAsyncTaskLoader extends AsyncTaskLoader<ArrayList<Recipe>> {
    private final String TAG = RecipesAsyncTaskLoader.class.getSimpleName();
    private ArrayList<Recipe> recipeArrayList;
    private Context mContext;

    /**
     * Constructor for this class.
     *
     * @param context is the context of the activity.
     */
    public RecipesAsyncTaskLoader(Context context) {
        super(context);
        mContext = context;
    }

    /**
     * Subclasses must implement this to take care of loading their data,
     * as per {@link #startLoading()}. This is not called by clients directly,
     * but as a result of a call to {@link #startLoading()}.
     */
    @Override
    protected void onStartLoading() {
        if (recipeArrayList != null) {
            Log.i(TAG, "(onStartLoading) Reload existing results.");
            deliverResult(recipeArrayList);
        } else {
            Log.i(TAG, "(onStartLoading) Load new results.");
            forceLoad();
        }
    }

    /**
     * Called on a worker thread to perform the actual load and to return
     * the result of the load operation.
     * <p>
     * Implementations should not deliver the result directly, but should return them
     * from this method, which will eventually end up calling {@link #deliverResult} on
     * the UI thread.  If implementations need to process the results on the UI thread
     * they may override {@link #deliverResult} and do so there.
     * <p>
     * To support cancellation, this method should periodically check the values of
     * {@link #isLoadInBackgroundCanceled} and terminate when it returns true.
     * Subclasses may also override {@link #cancelLoadInBackground} to interrupt the load
     * directly instead of polling {@link #isLoadInBackgroundCanceled}.
     * <p>
     * When the load is canceled, this method may either return normally or throw
     * {@link OperationCanceledException}.  In either case, the {@link Loader} will
     * call {@link #onCanceled} to perform post-cancellation cleanup and to dispose of the
     * result object, if any.
     *
     * @return The result of the load operation.
     * @throws OperationCanceledException if the load is canceled during execution.
     * @see #isLoadInBackgroundCanceled
     * @see #cancelLoadInBackground
     * @see #onCanceled
     */
    @Override
    public ArrayList<Recipe> loadInBackground() {
        return RecipesJsonUtils.getRecipes();
    }

    /**
     * Sends the result of the load to the registered listener. Should only be called by subclasses.
     * <p>
     * Must be called from the process's main_menu thread.
     *
     * @param data the result of the load
     */
    @Override
    public void deliverResult(ArrayList<Recipe> data) {
        if (data == null) {
            Log.i(TAG, "(deliverResult) No results to deliver.");
        } else {
            Log.i(TAG, "(deliverResult) " + data.size() + " element(s) delivered.");

            // Insert recipes into database.
            ContentValues[] recipeContentValues = getRecipeContentValues(data);
            if (recipeContentValues != null && recipeContentValues.length != 0) {
                /* Get a handle on the ContentResolver to delete and insert data */
                ContentResolver contentResolver = mContext.getContentResolver();

                /* Delete old weather data because we don't need to keep multiple days' data */
                contentResolver.delete(
                        RecipesContract.RecipesEntry.CONTENT_URI,
                        null,
                        null);

                /* Delete old weather data because we don't need to keep multiple days' data */
                contentResolver.delete(
                        RecipesContract.IngredientsEntry.CONTENT_URI,
                        null,
                        null);

                /* Delete old weather data because we don't need to keep multiple days' data */
                contentResolver.delete(
                        RecipesContract.StepsEntry.CONTENT_URI,
                        null,
                        null);

                /* Insert our new weather data into Sunshine's ContentProvider */
                contentResolver.bulkInsert(
                        RecipesContract.RecipesEntry.CONTENT_URI,
                        recipeContentValues);

                for (int i = 0; i < data.size(); i++) {
                    // Insert ingredients into database.
                    ContentValues[] ingredientContentValues =
                            getIngredientContentValues(data.get(i).getmIngredients(), data.get(i).getmId());
                    if (ingredientContentValues != null && ingredientContentValues.length != 0) {
                        /* Insert our new weather data into Sunshine's ContentProvider */
                        contentResolver.bulkInsert(
                                RecipesContract.IngredientsEntry.CONTENT_URI,
                                ingredientContentValues);
                    }

                    // Insert steps into database.
                    ContentValues[] stepContentValues =
                            getStepContentValues(data.get(i).getmSteps(), data.get(i).getmId());
                    if (stepContentValues != null && stepContentValues.length != 0) {
                        /* Insert our new weather data into Sunshine's ContentProvider */
                        contentResolver.bulkInsert(
                                RecipesContract.StepsEntry.CONTENT_URI,
                                stepContentValues);
                    }
                }
            }
        }

        recipeArrayList = data;
        super.deliverResult(data);
    }

    private static ContentValues[] getRecipeContentValues(ArrayList<Recipe> data) {
        ContentValues[] recipeContentValues = new ContentValues[data.size()];
        for (int i = 0; i < data.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(RecipesContract.RecipesEntry.COLUMN_RECIPE_ID, data.get(i).getmId());
            contentValues.put(RecipesContract.RecipesEntry.COLUMN_IMAGE, data.get(i).getmImage());
            contentValues.put(RecipesContract.RecipesEntry.COLUMN_NAME, data.get(i).getmName());
            contentValues.put(RecipesContract.RecipesEntry.COLUMN_SERVINGS, data.get(i).getmServings());
            recipeContentValues[i] = contentValues;
        }
        return recipeContentValues;
    }

    private static ContentValues[] getIngredientContentValues(ArrayList<RecipeIngredient> data, int recipeId) {
        ContentValues[] ingredientContentValues = new ContentValues[data.size()];
        for (int i = 0; i < data.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(RecipesContract.IngredientsEntry.COLUMN_RECIPE_ID, recipeId);
            contentValues.put(RecipesContract.IngredientsEntry.COLUMN_INGREDIENT, data.get(i).getmIngredient());
            contentValues.put(RecipesContract.IngredientsEntry.COLUMN_MEASURE, data.get(i).getmMeasure());
            contentValues.put(RecipesContract.IngredientsEntry.COLUMN_QUANTITY, data.get(i).getmQuantity());
            ingredientContentValues[i] = contentValues;
        }
        return ingredientContentValues;
    }

    private static ContentValues[] getStepContentValues(ArrayList<RecipeStep> data, int recipeId) {
        ContentValues[] stepContentValues = new ContentValues[data.size()];
        for (int i = 0; i < data.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(RecipesContract.StepsEntry.COLUMN_STEP_ID, data.get(i).getmId());
            contentValues.put(RecipesContract.StepsEntry.COLUMN_RECIPE_ID, recipeId);
            contentValues.put(RecipesContract.StepsEntry.COLUMN_DESCRIPTION, data.get(i).getmDescription());
            contentValues.put(RecipesContract.StepsEntry.COLUMN_SHORT_DESCRIPTION, data.get(i).getmShortDescription());
            contentValues.put(RecipesContract.StepsEntry.COLUMN_THUMBNAIL_URL, data.get(i).getmThumbnailURL());
            contentValues.put(RecipesContract.StepsEntry.COLUMN_VIDEO_URL, data.get(i).getmVideoURL());
            stepContentValues[i] = contentValues;
        }
        return stepContentValues;
    }
}
