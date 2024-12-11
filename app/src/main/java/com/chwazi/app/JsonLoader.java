package com.chwazi.app;

// Import necessary Android and Java classes
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

// This class helps load JSON data from the assets folder and parse it into Java objects
public class JsonLoader {

    // Public method to load categories from a JSON file
    public static List<CategoryData> loadCategories(Context context) {

        // Load the JSON content from the "questions.json" file in the assets folder
        String json = loadJsonFromAssets(context, "questions.json");

        // Create a new Gson instance for parsing JSON
        Gson gson = new Gson();
        // Define the type of the data we expect (a list of CategoryData objects)
        Type listType = new TypeToken<List<CategoryData>>() {}.getType();
        // Parse the JSON string into a list of CategoryData objects and return it
        return gson.fromJson(json, listType);
    }

    // Private helper method to load a JSON file from the assets folder and return it as a String
    private static String loadJsonFromAssets(Context context, String fileName) {
        try {
            // Open the JSON file from the assets folder
            InputStream is = context.getAssets().open(fileName);
            // Create a buffer to hold the data from the file
            byte[] buffer = new byte[is.available()];
            // Read the data into the buffer
            is.read(buffer);
            // Close the InputStream to free resources
            is.close();
            // Convert the byte array to a String and return it
            return new String(buffer);
        } catch (Exception e) {
            // If an error occurs, print the stack trace for debugging
            e.printStackTrace();
            // Return null if there was an error loading the JSON
            return null;
        }
    }
}
