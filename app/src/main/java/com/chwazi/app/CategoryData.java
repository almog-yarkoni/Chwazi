package com.chwazi.app;

// Import the List class from Java's utility package
import java.util.List;

// This class represents the data for a category, including its name and related questions
public class CategoryData {
    // Variable to store the name of the category
    private String category;
    // List to store questions related to this category
    private List<Question> questions;

    // Getter method to retrieve the category name
    public String getCategory() {
        return category;
    }

    // Getter method to retrieve the list of questions for this category
    public List<Question> getQuestions() {
        return questions;
    }
}
