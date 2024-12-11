package com.chwazi.app;

// Import the List class from Java's utility package
import java.util.List;

// This class represents a single question in the game
public class Question {
    // Variable to store the text of the question
    private String question;
    // List to store the possible answer options for the question
    private List<String> options;
    // Variable to store the index of the correct answer in the options list
    private int correctAnswer;

    // Getter method to retrieve the question text
    public String getQuestion() {
        return question;
    }

    // Getter method to retrieve the list of answer options
    public List<String> getOptions() {
        return options;
    }

    // Getter method to retrieve the index of the correct answer
    public int getCorrectAnswer() {
        return correctAnswer;
    }
}
