package com.davidburgosprieto.android.bakingapp.classes;

import java.util.ArrayList;

public class Recipe {
    private int mId;
    private String mName;
    private ArrayList<RecipeIngredient> mIngredients;
    private ArrayList<RecipeStep> mSteps;
    private int mServings;
    private String mImage;

    public Recipe(int mId, String mName, ArrayList<RecipeIngredient> mIngredients,
                  ArrayList<RecipeStep> mSteps, int mServings, String mImage) {
        this.mId = mId;
        this.mName = mName;
        this.mIngredients = mIngredients;
        this.mSteps = mSteps;
        this.mServings = mServings;
        this.mImage = mImage;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public ArrayList<RecipeIngredient> getmIngredients() {
        return mIngredients;
    }

    public void setmIngredients(ArrayList<RecipeIngredient> mIngredients) {
        this.mIngredients = mIngredients;
    }

    public ArrayList<RecipeStep> getmSteps() {
        return mSteps;
    }

    public void setmSteps(ArrayList<RecipeStep> mSteps) {
        this.mSteps = mSteps;
    }

    public int getmServings() {
        return mServings;
    }

    public void setmServings(int mServings) {
        this.mServings = mServings;
    }

    public String getmImage() {
        return mImage;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }
}
