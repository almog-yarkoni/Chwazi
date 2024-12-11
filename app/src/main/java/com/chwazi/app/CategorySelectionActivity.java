package com.chwazi.app;

// Import necessary Android and Java classes
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chwazi.app.databinding.ActivityCategorySelectionBinding;

import java.util.ArrayList;
import java.util.List;

// This activity allows users to select a category from a list
public class CategorySelectionActivity extends AppCompatActivity implements CategoryAdapter.OnItemClickListener{

    // Binding object to access views defined in the layout
    protected ActivityCategorySelectionBinding binding;
    // List to store category names
    private ArrayList<String> categories = new ArrayList<>();

    // Number of participants, default is 2
    private int participantCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout using ViewBinding
        binding = ActivityCategorySelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the toolbar at the top of the screen
        setupToolbar();

        // Get the number of participants from the intent that started this activity
        participantCount = getIntent().getIntExtra("PARTICIPANT_COUNT", 2);
        // Load the list of categories from a JSON file
        loadCategoriesFromJSON();

        // Set the layout manager for the RecyclerView to display items in a grid with 2 columns
        binding.recyclerViewCategories.setLayoutManager(new GridLayoutManager(this, 2));
        // Set the adapter for the RecyclerView with the list of categories and the current activity as the click listener
        binding.recyclerViewCategories.setAdapter(new CategoryAdapter(categories, this));
    }

    // Method to set up the toolbar with a back button and title
    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null){
            // Enable the back button in the toolbar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Set the title of the toolbar
            getSupportActionBar().setTitle(getString(R.string.select_a_category));
            // Set the icon for the back button
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        }
        // Set the title text color to white
        binding.toolbar.setTitleTextColor(Color.WHITE);
    }

    // Handle toolbar item clicks, such as the back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            // If the back button is clicked, close this activity and go back
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to load categories from a JSON file
    private void loadCategoriesFromJSON() {
        try {
            // Load the list of categories from JSON using a helper class
            List<CategoryData> jsonCategories = JsonLoader.loadCategories(this);
            // Add each category name to the categories list
            for (CategoryData category : jsonCategories) {
                categories.add(category.getCategory());
            }
        } catch (Exception e) {
            // Log an error message if something goes wrong while reading JSON
            Log.e("CategorySelection", "Error reading JSON", e);
        }
    }

    // Handle click events on category items
    @Override
    public void onItemClick(String category) {
        // Create an intent to start the GameActivity
        Intent intent = new Intent(CategorySelectionActivity.this, GameActivity.class);
        // Pass the number of participants to the GameActivity
        intent.putExtra("PARTICIPANT_COUNT", participantCount);
        // Pass the selected category to the GameActivity
        intent.putExtra("SELECTED_CATEGORY", category);
        // Start the GameActivity
        startActivity(intent);
        // Close the current activity
        finish();
    }
}
