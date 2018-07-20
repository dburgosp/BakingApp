package com.davidburgosprieto.android.bakingapp.classes;

public class RecipeIngredient {
    private int mRecipeId;
    private double mQuantity;
    private String mMeasure;
    private String mIngredient;

    public RecipeIngredient(int recipeId, double mQuantity, String mMeasure, String mIngredient) {
        this.mRecipeId = recipeId;
        this.mQuantity = mQuantity;
        this.mMeasure = mMeasure;
        this.mIngredient = mIngredient;
    }

    public int getmRecipeId() {
        return mRecipeId;
    }

    public void setmRecipeId(int mRecipeId) {
        this.mRecipeId = mRecipeId;
    }

    public double getmQuantity() {
        return mQuantity;
    }

    public void setmQuantity(int mQuantity) {
        this.mQuantity = mQuantity;
    }

    public String getmMeasure() {
        return mMeasure;
    }

    public void setmMeasure(String mMeasure) {
        this.mMeasure = mMeasure;
    }

    public String getmIngredient() {
        return mIngredient;
    }

    public void setmIngredient(String mIngredient) {
        this.mIngredient = mIngredient;
    }
}
