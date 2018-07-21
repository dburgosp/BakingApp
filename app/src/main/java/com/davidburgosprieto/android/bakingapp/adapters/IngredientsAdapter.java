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
import com.davidburgosprieto.android.bakingapp.StepActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsAdapterViewHolder> {
    private Context mContext;
    private Cursor mCursor;

    public IngredientsAdapter(@NonNull Context context) {
        mContext = context;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public IngredientsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_ingredient, parent, false);
        view.setFocusable(true);
        return new IngredientsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientsAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        String ingredient = mCursor.getString(DetailActivity.INDEX_COLUMN_INGREDIENT);
        String measure = mCursor.getString(DetailActivity.INDEX_COLUMN_MEASURE);
        Double quantity = mCursor.getDouble(DetailActivity.INDEX_COLUMN_QUANTITY);

        holder.ingredient.setText(ingredient);
        holder.measure.setText(measure);
        holder.quantity.setText(Double.toString(quantity));
    }

    @Override
    public int getItemCount() {
        if (null == mCursor)
            return 0;
        return mCursor.getCount();
    }

    public class IngredientsAdapterViewHolder extends RecyclerView.ViewHolder  {
        @BindView(R.id.list_item_ingredient_name)
        TextView ingredient;
        @BindView(R.id.list_item_ingredient_quantity)
        TextView quantity;
        @BindView(R.id.list_item_ingredient_measure)
        TextView measure;

        IngredientsAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            // Butterknife fails for some reason...
            ingredient = view.findViewById(R.id.list_item_ingredient_name);
            quantity = view.findViewById(R.id.list_item_ingredient_quantity);
            measure = view.findViewById(R.id.list_item_ingredient_measure);
        }
    }
}
