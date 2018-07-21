package com.davidburgosprieto.android.bakingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.davidburgosprieto.android.bakingapp.DetailActivity;
import com.davidburgosprieto.android.bakingapp.R;
import com.davidburgosprieto.android.bakingapp.RecipesActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipesAdapterViewHolder> {
    private Context mContext;
    private Cursor mCursor;

    public RecipesAdapter(@NonNull Context context) {
        mContext = context;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public RecipesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_recipe, parent, false);
        view.setFocusable(true);
        return new RecipesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipesAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        int recipeId = mCursor.getInt(RecipesActivity.INDEX_COLUMN_RECIPE_ID);
        int servings = mCursor.getInt(RecipesActivity.INDEX_COLUMN_SERVINGS);
        String recipeName = mCursor.getString(RecipesActivity.INDEX_COLUMN_NAME);

        holder.recipeName.setText(recipeName);
        holder.recipeNumber.setText(Integer.toString(recipeId));
        holder.recipeServings.setText(Integer.toString(servings));
    }

    @Override
    public int getItemCount() {
        if (null == mCursor)
            return 0;
        return mCursor.getCount();
    }

    public class RecipesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.list_item_recipe_name_textview)
        TextView recipeName;
        @BindView(R.id.list_item_recipe_number_textview)
        TextView recipeNumber;
        @BindView(R.id.list_item_recipe_servings_textview)
        TextView recipeServings;

        RecipesAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            // Butterknife fails for some reason...
            recipeName = view.findViewById(R.id.list_item_recipe_name_textview);
            recipeNumber = view.findViewById(R.id.list_item_recipe_number_textview);
            recipeServings = view.findViewById(R.id.list_item_recipe_servings_textview);

            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click. We fetch the date that has been
         * selected, and then call the onClick handler registered with this adapter, passing that
         * date.
         *
         * @param v the View that was clicked
         */
        @Override
        public void onClick(View v) {
 /*           int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            mClickHandler.onClick(dateInMillis);*/
            Intent intent = new Intent(mContext, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_RECIPE_ID, Integer.valueOf(recipeNumber.getText().toString()));
            intent.putExtra(DetailActivity.EXTRA_RECIPE_NAME, recipeName.getText());
            intent.putExtra(DetailActivity.EXTRA_RECIPE_SERVINGS, Integer.valueOf(recipeServings.getText().toString()));
            mContext.startActivity(intent);
        }
    }
}
