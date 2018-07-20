/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.davidburgosprieto.android.bakingapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the recipes database. This class is not necessary, but keeps
 * the code organized.
 */
public class RecipesContract {

    /*
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website. A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * Play Store.
     */
    public static final String CONTENT_AUTHORITY = "com.davidburgosprieto.android.bakingapp";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for Sunshine.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_RECIPE = "recipe";
    public static final String PATH_STEP = "step";
    public static final String PATH_INGREDIENT = "ingredient";

    /* Inner class that defines the table contents of the recipe steps table */
    public static final class RecipesEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the recipes table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECIPE)
                .build();

        /* Used internally as the name of our recipes table. */
        public static final String TABLE_NAME = "recipe";

        // Table fields.
        public static final String COLUMN_RECIPE_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SERVINGS = "servings";
        public static final String COLUMN_IMAGE = "image";
    }

    /* Inner class that defines the table contents of the recipe ingredients table */
    public static final class IngredientsEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the recipe ingredients table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_INGREDIENT)
                .build();

        /* Used internally as the name of our recipe ingredients table. */
        public static final String TABLE_NAME = "step";

        // Table fields.
        public static final String COLUMN_RECIPE_ID = "recipeId";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_MEASURE = "measure";
        public static final String COLUMN_INGREDIENT = "ingredient";
    }

    /* Inner class that defines the table contents of the recipe steps table */
    public static final class StepsEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the recipe steps table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_STEP)
                .build();

        /* Used internally as the name of our recipe steps table. */
        public static final String TABLE_NAME = "step";

        // Table fields.
        public static final String COLUMN_STEP_ID = "id";
        public static final String COLUMN_RECIPE_ID = "recipeId";
        public static final String COLUMN_SHORT_DESCRIPTION = "shortDescription";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_VIDEO_URL = "videoURL";
        public static final String COLUMN_THUMBNAIL_URL = "thumbnailURL";
    }
}