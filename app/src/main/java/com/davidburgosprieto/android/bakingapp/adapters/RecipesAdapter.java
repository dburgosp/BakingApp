package com.davidburgosprieto.android.bakingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        int mRecipeId = mCursor.getInt(RecipesActivity.INDEX_COLUMN_RECIPE_ID);
        int mServings = mCursor.getInt(RecipesActivity.INDEX_COLUMN_SERVINGS);
        String mRecipeName = mCursor.getString(RecipesActivity.INDEX_COLUMN_NAME);

        holder.mRecipeId = mRecipeId;
        holder.mServings = mServings;
        holder.mRecipeName = mRecipeName;

        holder.mRecipeNameTextView.setText(mRecipeName);
        holder.mRecipeNumberTextView.setText(Integer.toString(mRecipeId));
        holder.mRecipeServingsTextView.setText(Integer.toString(mServings));
    }

    @Override
    public int getItemCount() {
        if (null == mCursor)
            return 0;
        return mCursor.getCount();
    }

    public class RecipesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.list_item_recipe_name_textview)
        TextView mRecipeNameTextView;
        @BindView(R.id.list_item_recipe_number_textview)
        TextView mRecipeNumberTextView;
        @BindView(R.id.list_item_recipe_servings_textview)
        TextView mRecipeServingsTextView;

        int mRecipeId, mServings;
        String mRecipeName;

        RecipesAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            // Butterknife fails for some reason...
            mRecipeNameTextView = view.findViewById(R.id.list_item_recipe_name_textview);
            mRecipeNumberTextView = view.findViewById(R.id.list_item_recipe_number_textview);
            mRecipeServingsTextView = view.findViewById(R.id.list_item_recipe_servings_textview);

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
            Intent intent = new Intent(mContext, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_RECIPE_ID, Integer.toString(mRecipeId));
            intent.putExtra(DetailActivity.EXTRA_RECIPE_NAME, mRecipeName);
            intent.putExtra(DetailActivity.EXTRA_RECIPE_SERVINGS, Integer.toString(mServings));
            mContext.startActivity(intent);
        }
    }
}
