package com.davidburgosprieto.android.bakingapp.classes;

public class RecipeStep {
    private int mRecipeId;
    private int mId;
    private String mShortDescription;
    private String mDescription;
    private String mVideoURL;
    private String mThumbnailURL;

    public RecipeStep(int recipeId, int id, String shortDescription, String description, String videoURL, String thumbnailURL) {
        this.mRecipeId = recipeId;
        this.mId = id;
        this.mShortDescription = shortDescription;
        this.mDescription = description;
        this.mVideoURL = videoURL;
        this.mThumbnailURL = thumbnailURL;
    }

    public int getmRecipeId() {
        return mRecipeId;
    }

    public void setmRecipeId(int mRecipeId) {
        this.mRecipeId = mRecipeId;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int id) {
        this.mId = id;
    }

    public String getmShortDescription() {
        return mShortDescription;
    }

    public void setmShortDescription(String shortDescription) {
        this.mShortDescription = shortDescription;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String description) {
        this.mDescription = description;
    }

    public String getmVideoURL() {
        return mVideoURL;
    }

    public void setmVideoURL(String videoURL) {
        this.mVideoURL = videoURL;
    }

    public String getmThumbnailURL() {
        return mThumbnailURL;
    }

    public void setmThumbnailURL(String thumbnailURL) {
        this.mThumbnailURL = thumbnailURL;
    }
}
