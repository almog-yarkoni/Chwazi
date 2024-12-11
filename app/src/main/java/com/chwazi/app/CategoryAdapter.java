package com.chwazi.app;

// Import necessary Android and Java classes
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// This is the adapter class for the RecyclerView that displays categories
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    // List to hold category names
    private List<String> categories;
    // Listener for item click events
    private OnItemClickListener listener;

    // Interface to handle click events on items
    public interface OnItemClickListener {
        void onItemClick(String category);
    }

    // Constructor for the adapter, takes a list of categories and a click listener
    public CategoryAdapter(List<String> categories, OnItemClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    // ViewHolder class that holds the view for each category item
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        // TextView to display the category name
        TextView categoryName;

        // Constructor for the ViewHolder
        public CategoryViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            // Find the TextView in the item layout
            categoryName = itemView.findViewById(R.id.categoryName);

            // Set a click listener on the entire item view
            itemView.setOnClickListener(v -> {
                // When clicked, get the text from the TextView and pass it to the listener
                listener.onItemClick(categoryName.getText().toString());
            });
        }
    }

    // Called when RecyclerView needs a new ViewHolder
    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item layout for a category
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        // Return a new ViewHolder instance
        return new CategoryViewHolder(view, listener);
    }

    // Called to display data at the specified position
    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        // Get the category name for the current position
        String categoryName = categories.get(position);
        // Set the category name to the TextView in the ViewHolder
        holder.categoryName.setText(categoryName);
    }

    // Returns the total number of items in the data set
    @Override
    public int getItemCount() {
        return categories.size();
    }
}
