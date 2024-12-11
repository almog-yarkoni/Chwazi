package com.chwazi.app;

// Import necessary Android and Java classes
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.chwazi.app.databinding.ActivityMainBinding;

import java.lang.reflect.Field;

// This activity is the main screen of the app where the user can start the game
public class MainActivity extends AppCompatActivity {

    // Binding object to access views defined in the layout
    protected ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout using ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set a click listener for the Start Game button
        binding.btnStartGame.setOnClickListener(v -> {
            showParticipantsPrompt(); // Show a prompt to select number of participants
        });
    }

    // Method to show a prompt for selecting the number of participants
    private void showParticipantsPrompt() {

        // Inflate the dialog layout from XML
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_participants_prompt, null);

        // Find views in the dialog layout
        NumberPicker numberPicker = dialogView.findViewById(R.id.numberPicker);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnNext = dialogView.findViewById(R.id.btnNext);

        // Set minimum and maximum values for the NumberPicker
        numberPicker.setMinValue(2); // Minimum 2 participants
        numberPicker.setMaxValue(5); // Maximum 5 participants
        numberPicker.setWrapSelectorWheel(false); // Prevent the NumberPicker from looping

        // Set the text color of the NumberPicker to white
        setNumberPickerTextColor(numberPicker, Color.WHITE);

        // Create an AlertDialog with the dialog view
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView) // Set the custom view
                .setCancelable(false) // Make the dialog not cancelable by clicking outside
                .create();

        // Set the background of the dialog to transparent
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Set a click listener for the Cancel button
        btnCancel.setOnClickListener(v -> dialog.dismiss()); // Dismiss the dialog when Cancel is clicked

        // Set a click listener for the Next button
        btnNext.setOnClickListener(v -> {
            int participantCount = numberPicker.getValue(); // Get the selected number of participants
            dialog.dismiss(); // Dismiss the dialog
            proceedToCategorySelection(participantCount); // Move to the next screen with the selected count
        });

        // Show the dialog to the user
        dialog.show();
    }

    // Method to set the text color of a NumberPicker using reflection
    private void setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        try {
            // Access all declared fields of the NumberPicker class
            Field[] pickerFields = NumberPicker.class.getDeclaredFields();
            for (Field field : pickerFields) {
                // Look for the field named "mSelectorWheelPaint" which controls the text paint
                if (field.getName().equals("mSelectorWheelPaint")) {
                    field.setAccessible(true); // Make the field accessible
                    Paint paint = (Paint) field.get(numberPicker); // Get the Paint object
                    paint.setColor(color); // Set the desired text color
                    break; // Exit the loop once the field is found and modified
                }
            }
            numberPicker.invalidate(); // Refresh the NumberPicker to apply the color change
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace if an error occurs
        }
    }

    // Method to move to the Category Selection screen with the selected number of participants
    private void proceedToCategorySelection(int participantCount) {
        // Create an Intent to start the CategorySelectionActivity
        Intent intent = new Intent(MainActivity.this, CategorySelectionActivity.class);
        // Put the number of participants as an extra to pass to the next activity
        intent.putExtra("PARTICIPANT_COUNT", participantCount);
        // Start the CategorySelectionActivity
        startActivity(intent);
    }
}
