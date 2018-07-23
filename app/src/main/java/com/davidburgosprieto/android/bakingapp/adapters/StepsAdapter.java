package com.davidburgosprieto.android.bakingapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.davidburgosprieto.android.bakingapp.DetailActivity;
import com.davidburgosprieto.android.bakingapp.R;
import com.davidburgosprieto.android.bakingapp.classes.RecipeStep;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsAdapterViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    private String mRecipeName;
    private OnItemClickListener mListener;
    private RecipeStep mRecipeStep;

    public interface OnItemClickListener {
        void onItemClick(RecipeStep step, View clickedView);
    }

    public StepsAdapter(@NonNull Context context, String recipeName, OnItemClickListener listener) {
        mContext = context;
        mRecipeName = recipeName;
        mListener = listener;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public StepsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_step, parent, false);
        view.setFocusable(true);
        return new StepsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepsAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        // Get step data from cursor.
        int mStepId = mCursor.getInt(DetailActivity.INDEX_COLUMN_STEP_ID);
        String mStepDescription = mCursor.getString(DetailActivity.INDEX_COLUMN_DESCRIPTION);
        String mStepShortDescription = mCursor.getString(DetailActivity.INDEX_COLUMN_SHORT_DESCRIPTION);
        String mStepVideoUrl = mCursor.getString(DetailActivity.INDEX_COLUMN_VIDEO_URL);

        mRecipeStep = new RecipeStep(1, mStepId, mStepShortDescription, mStepDescription, mStepVideoUrl, "");
        // Set step data to view holder elements.
        holder.mRecipeName = mRecipeName;
        holder.mStepId = mStepId;
        holder.mStepDescription = mStepDescription;
        holder.mStepShortDescription = mStepShortDescription;
        holder.mStepVideoUrl = mStepVideoUrl;
        holder.stepNumber.setText("Step #" + mStepId);
        holder.stepShortDescription.setText(mStepShortDescription);

        holder.bind(mRecipeStep, mListener);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor)
            return 0;
        return mCursor.getCount();
    }

    public class StepsAdapterViewHolder extends RecyclerView.ViewHolder
            //implements View.OnClickListener
    {
        @BindView(R.id.list_item_step_number_textview)
        TextView stepNumber;
        @BindView(R.id.list_item_step_short_description_textview)
        TextView stepShortDescription;

        public int mStepId;
        public String mRecipeName, mStepDescription, mStepShortDescription, mStepVideoUrl;

        StepsAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            // Butterknife fails for some reason...
            stepNumber = view.findViewById(R.id.list_item_step_number_textview);
            stepShortDescription = view.findViewById(R.id.list_item_step_short_description_textview);

            //view.setOnClickListener(this);
        }

        public void bind(final RecipeStep step, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(step, v);
                }
            });
        }

        /**
         * This gets called by the child views during a click. We fetch the date that has been
         * selected, and then call the onClick handler registered with this adapter, passing that
         * date.
         *
         * @param v the View that was clicked
         */
/*        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, StepActivity.class);
            intent.putExtra(StepActivity.RECIPE_NAME_EXTRA, mRecipeName);
            intent.putExtra(StepActivity.VIDEO_URL_EXTRA, mStepVideoUrl);
            intent.putExtra(StepActivity.DESCRIPTION_EXTRA, mStepDescription);
            intent.putExtra(StepActivity.SHORT_DESCRIPTION_EXTRA, mStepShortDescription);
            intent.putExtra(StepActivity.STEP_ID_EXTRA, Integer.toString(mStepId));
            mContext.startActivity(intent);
        }
        */
    }
}
