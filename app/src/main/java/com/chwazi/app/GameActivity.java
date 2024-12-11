package com.chwazi.app;

// Import necessary Android and Java classes
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.chwazi.app.databinding.ActivityGameBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

// This activity manages the main game screen where participants interact
public class GameActivity extends AppCompatActivity {

    // Binding object to access views defined in the layout
    protected ActivityGameBinding binding;

    // Number of participants in the game
    private int participantCount;
    // Selected category for the game
    private String category;

    // Constant for the size of participant circles in density-independent pixels (dp)
    private final int CIRCLE_SIZE_DP = 80;
    // List of color pairs (hex code and color name) for participant circles
    private final ArrayList<Pair<String, String>> colors = new ArrayList<>();

    // List to store all questions for the selected category
    private List<Question> questionsList = new ArrayList<>();

    // Map to store participant names with their index
    private HashMap<Integer, String> participantNames = new HashMap<>();
    // Map to store scores for each participant by name
    private Map<String, Integer> scores = new HashMap<>();

    // Index to track the current participant
    private int currentParticipantIndex;

    // Dialog to show various prompts
    private AlertDialog dialog;

    // Set to keep track of participants who have been touched
    private Set<Integer> touchedParticipants = new HashSet<>();

    // List of color names for reference (not used in the provided code snippet)
    private ArrayList<String> colorsName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout using ViewBinding
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the toolbar at the top of the screen
        setupToolbar();

        // Get the number of participants from the intent that started this activity
        participantCount = getIntent().getIntExtra("PARTICIPANT_COUNT", 2);
        // Get the selected category from the intent
        category = getIntent().getStringExtra("SELECTED_CATEGORY");

        // Add color pairs (hex code and name) to the colors list
        colors.add(new Pair("#FF0000", "Red"));
        colors.add(new Pair("#00FF00", "Green"));
        colors.add(new Pair("#0000FF", "Blue"));
        colors.add(new Pair("#ff0066", "Pink"));
        colors.add(new Pair("#FF00FF", "Magenta"));

        // Load questions based on the selected category
        loadQuestions(category);
        // Generate circle views for each participant
        generateCircles(participantCount);

        // Set click listener for the Scoreboard button to show the scoreboard
        binding.btnScoreBoard.setOnClickListener(v -> showScoreboard(false));

        // Set click listener for the Reset Game button to prompt resetting participants
        binding.btnResetGame.setOnClickListener(v -> {
            showParticipantsPrompt();
        });

        // Set click listener for the End Game button to show the scoreboard
        binding.btnEndGame.setOnClickListener(v -> {
            showScoreboard(false);
        });
    }

    // Method to set up the toolbar with a back button and title
    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            // Enable the back button in the toolbar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Set the title of the toolbar
            getSupportActionBar().setTitle(getString(R.string.game_screen));
            // Set the icon for the back button
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        }
        // Set the toolbar title text color to white
        binding.toolbar.setTitleTextColor(Color.WHITE);
    }

    // Handle toolbar item clicks, such as the back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // If the back button is clicked, close this activity and go back
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to generate circle views representing each participant
    private void generateCircles(int numberOfParticipants) {
        // Remove any existing circles from the container
        binding.circleContainer.removeAllViews();
        // Add a listener to perform actions before the view is drawn
        binding.circleContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                // Remove this listener to avoid multiple calls
                binding.circleContainer.getViewTreeObserver().removeOnPreDrawListener(this);

                // Get the width and height of the container
                int containerWidth = binding.circleContainer.getWidth();
                int containerHeight = binding.circleContainer.getHeight();

                // If container size is not yet determined, exit early
                if (containerWidth == 0 || containerHeight == 0) {
                    return true;
                }

                // Get display metrics to convert dp to pixels
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int circleSizePx = (int) (CIRCLE_SIZE_DP * metrics.density);

                // Calculate the center of the container
                int centerX = containerWidth / 2;
                int centerY = containerHeight / 2;

                // Calculate the radius for positioning circles in a circular layout
                int radius = Math.min(containerWidth, containerHeight) / 3 - circleSizePx;
                radius += circleSizePx;

                // Loop to create each participant's circle
                for (int i = 0; i < numberOfParticipants; i++) {
                    // Calculate the angle for the current circle
                    double angle = 2 * Math.PI * i / numberOfParticipants;
                    // Calculate the x and y position based on the angle and radius
                    int x = (int) (centerX + radius * Math.cos(angle)) - circleSizePx / 2;
                    int y = (int) (centerY + radius * Math.sin(angle)) - circleSizePx / 2;

                    // Ensure the circle stays within the container bounds
                    x = Math.max(0, Math.min(x, containerWidth - circleSizePx));
                    y = Math.max(0, Math.min(y, containerHeight - circleSizePx));

                    // Create a new View for the circle
                    View circle = new View(GameActivity.this);
                    // Set layout parameters for size and position
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(circleSizePx, circleSizePx);
                    params.leftMargin = x;
                    params.topMargin = y;
                    circle.setLayoutParams(params);

                    // Set the background with a ripple effect and the participant's color
                    circle.setBackground(createRippleDrawable(Color.parseColor(colors.get(i % colors.size()).first)));

                    final int participantIndex = i;

                    // Set a touch listener to handle participant interactions
                    circle.setOnTouchListener((v, event) -> {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            // Add participant to the touched set when pressed
                            touchedParticipants.add(participantIndex);
                            // Check if all participants have been touched
                            checkAllParticipantsReady(numberOfParticipants);
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            // Remove participant from the touched set when released
                            touchedParticipants.remove(participantIndex);
                        }
                        return true;
                    });

                    // Initialize participant name as empty
                    participantNames.put(i, "");

                    // Set initial scale and transparency for animation
                    circle.setScaleX(0f);
                    circle.setScaleY(0f);
                    circle.setAlpha(0f);

                    // Add the circle to the container
                    binding.circleContainer.addView(circle);

                    // Animate the circle to appear with scaling and fading in
                    circle.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .alpha(1f)
                            .setDuration(500)
                            .setStartDelay(i * 100L) // Stagger the animations
                            .start();
                }
                return true;
            }
        });
    }

    // Method to check if all participants have been touched
    private void checkAllParticipantsReady(int numberOfParticipants) {
        // If the number of touched participants equals the total number of participants
        if (touchedParticipants.size() == numberOfParticipants) {
            // Choose a random color for the next action
            chooseRandomColor();
        }
    }

    // Method to show a prompt for setting the number of participants
    private void showParticipantsPrompt() {

        // Inflate the dialog layout
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

        // Set the background of the dialog to transparent for better UI appearance
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Set click listener for the Cancel button
        btnCancel.setOnClickListener(v -> {
            dialog.dismiss(); // Dismiss the dialog
            resetGame(); // Reset the game
        });

        // Set click listener for the Next button
        btnNext.setOnClickListener(v -> {
            // Get the selected number of participants from the NumberPicker
            participantCount = numberPicker.getValue();
            dialog.dismiss(); // Dismiss the dialog
            resetGame(); // Reset the game with the new participant count
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

    // Method to choose a random participant from touched participants
    private void chooseRandomColor() {
        // Convert the set of touched participants to a list
        List<Integer> touchedList = new ArrayList<>(touchedParticipants);
        // Choose a random index from the list
        int randomIndex = new Random().nextInt(touchedList.size());

        // Show a toast message with the chosen color name
        Toast.makeText(this, getString(R.string.choosen_color) + ": " + colors.get(randomIndex).second, Toast.LENGTH_SHORT).show();
        // Set the current participant index to the chosen participant
        currentParticipantIndex = touchedList.get(randomIndex);
        // Delay for 500 milliseconds before prompting for the participant's name
        new Handler().postDelayed(() -> promptParticipantName(), 500);

        // Clear the set of touched participants
        touchedParticipants.clear();
    }

    // Method to create a RippleDrawable with a specified color
    private Drawable createRippleDrawable(int color) {
        // Create a ColorStateList for the ripple effect color
        ColorStateList colorStateList = ColorStateList.valueOf(Color.WHITE); // Ripple color
        // Create a ShapeDrawable with an oval shape and set its color
        ShapeDrawable circleShape = new ShapeDrawable(new OvalShape());
        circleShape.getPaint().setColor(color);

        // Create and return a RippleDrawable with the ripple color and circle shape
        return new RippleDrawable(colorStateList, circleShape, null);
    }


    // Method to prompt the current participant to enter their name
    private void promptParticipantName() {
        // Get the current participant's name by index
        String value = getValueByIndex(participantNames, currentParticipantIndex);
        // If the participant already has a name, show the next question
        if (!value.isEmpty()) {
            showNextQuestion();
            return;
        }

        // Inflate the dialog layout for adding a participant's name
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_participant_prompt, null);

        // Find views in the dialog layout
        TextInputEditText inputBox = dialogView.findViewById(R.id.input_box);
        AppCompatButton btnCancel = dialogView.findViewById(R.id.btnCancel);
        AppCompatButton btnSave = dialogView.findViewById(R.id.btnSave);

        // Create an AlertDialog with the dialog view
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView); // Set the custom view
        builder.setCancelable(false); // Make the dialog not cancelable by clicking outside

        AlertDialog alertDialog = builder.create();

        // Set the background of the dialog to transparent for better UI appearance
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        // Show the dialog to the user
        alertDialog.show();


        // Set click listener for the Cancel button
        btnCancel.setOnClickListener(v -> {
            alertDialog.dismiss(); // Dismiss the dialog
        });


        // Set click listener for the Save button
        btnSave.setOnClickListener(v -> {
            // Get the entered name and trim any extra spaces
            String name = inputBox.getText().toString().trim();
            if (!name.isEmpty()) {
                // Save the participant's name in the map
                participantNames.put(currentParticipantIndex, name);
                // Hide the keyboard
                hideKeyboard(inputBox);
                // Dismiss the dialog
                alertDialog.dismiss();
                // Show the next question
                showNextQuestion();
            } else {
                // Show an error message if the name is empty
                Toast.makeText(this, getString(R.string.empty_name_error), Toast.LENGTH_SHORT).show();
            }
        });

    }


    // Method to reset the game by clearing scores and participant names
    private void resetGame() {
        // Delay the reset by 1 second for a smooth transition
        new Handler().postDelayed(() -> {
            scores.clear(); // Clear all scores
            participantNames.clear(); // Clear all participant names
            questionsList.clear(); // Clear the list of questions
            loadQuestions(category); // Reload questions based on the category
            generateCircles(participantCount); // Regenerate participant circles
        }, 1000);
    }

    // Method to show the next question in the game
    private void showNextQuestion() {
        // If there are no more questions, show the scoreboard
        if (questionsList.isEmpty()) {
            showScoreboard(false);
            return;
        }
        // Get the current question (first in the list)
        Question currentQuestion = questionsList.get(0);

        // Inflate the dialog layout for question options
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_question_options, null);

        // Find views in the dialog layout
        TextView questionTextView = dialogView.findViewById(R.id.tvQuestion);
        LinearLayout buttonWrapper = dialogView.findViewById(R.id.question_option_buttons_wrapper);

        // Set the question text
        questionTextView.setText(currentQuestion.getQuestion());

        // Get the options for the current question
        String[] options = currentQuestion.getOptions().toArray(new String[0]);
        Button[] optionButtons = new Button[options.length];

        // Loop to create buttons for each option
        for (int i = 0; i < options.length; i++) {
            AppCompatButton optionButton = new AppCompatButton(this);
            optionButton.setText(options[i]); // Set the option text
            optionButton.setTag(i); // Tag the button with its index
            // Set layout parameters for the button
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 10); // Set bottom margin
            optionButton.setLayoutParams(params);
            optionButton.setPadding(12, 12, 12, 12); // Set padding
            optionButton.setBackgroundResource(R.drawable.button_gradient); // Set background
            optionButton.setTextColor(Color.WHITE); // Set text color
            buttonWrapper.addView(optionButton); // Add button to the wrapper

            optionButtons[i] = optionButton;

            // Set click listener for the option button
            optionButton.setOnClickListener(v -> {
                // Get the selected option index from the tag
                int selectedOptionIndex = (int) v.getTag();
                // Get the current participant's name
                String participantName = participantNames.get(currentParticipantIndex);

                // Check if the selected option is correct
                if (selectedOptionIndex == currentQuestion.getCorrectAnswer()) {
                    // Change button background to green and text color to green
                    v.setBackgroundResource(R.drawable.button_gradient_green);
                    ((AppCompatButton) v).setTextColor(Color.GREEN);
                    // Remove the question from the list as it's answered
                    questionsList.remove(0);
                    // Increment the participant's score
                    scores.put(participantName, scores.getOrDefault(participantName, 0) + 1);
                } else {
                    // Change button background to red and text color to red
                    v.setBackgroundResource(R.drawable.button_gradient_red);
                    ((AppCompatButton) v).setTextColor(Color.RED);
                }

                // Disable all option buttons after selection to prevent multiple answers
                for (Button button : optionButtons) {
                    button.setEnabled(false);
                }

                // Delay to show the result before moving to the next question or scoreboard
                new Handler().postDelayed(() -> {
                    dialog.dismiss(); // Dismiss the question dialog
                    // Move to the next participant in a round-robin fashion
                    currentParticipantIndex = (currentParticipantIndex + 1) % participantNames.size();
                    // Show the scoreboard if there are no more questions, else continue the game
                    showScoreboard(!questionsList.isEmpty());
                }, 1000);
            });
        }

        // Create and show the AlertDialog for the question
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView); // Set the custom view
        builder.setCancelable(false); // Make the dialog not cancelable by clicking outside
        dialog = builder.create();
        dialog.show(); // Show the dialog to the user
    }


    // Method to show the scoreboard in a bottom sheet dialog
    private void showScoreboard(Boolean isTimerSet) {

        // Create a BottomSheetDialog with a custom theme
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setCancelable(false); // Make it not cancelable by clicking outside


        // Inflate the scoreboard layout
        View view = getLayoutInflater().inflate(R.layout.scoreboard_bottom_sheet, null);

        // Find views in the scoreboard layout
        AppCompatButton returnToGameBtn = view.findViewById(R.id.btnReturnGame);
        AppCompatButton resetGameBtn = view.findViewById(R.id.bsBtnResetGame);
        LinearLayout scoreboardContainer = view.findViewById(R.id.scoreboardContainer);
        TextView winnerTv = view.findViewById(R.id.tvWinner); // TextView to display the winner

        // If there are no more questions, determine and display the winner
        if (questionsList.isEmpty()){
            returnToGameBtn.setVisibility(View.GONE); // Hide the "Return to Game" button

            int firstScore = -1;
            boolean isTie = true;
            String winnerName = null;
            int highestScore = 0;

            // Loop through the scores to find the participant with the highest score
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                int score = entry.getValue();
                if (firstScore == -1) {
                    firstScore = score;
                    winnerName = entry.getKey();
                    highestScore = score;
                } else if (score != firstScore) {
                    isTie = false;
                    if (score > highestScore) {
                        highestScore = score;
                        winnerName = entry.getKey();
                    }
                }
            }

            if (isTie) {
                // If all participants have the same score, declare a tie
                winnerTv.setText(getString(R.string.tie_message));
            } else {
                // Otherwise, announce the participant with the highest score
                winnerTv.setText(getString(R.string.winner_announcement, winnerName, highestScore));
            }
        }

        // Loop through the scores map and add each participant's score to the scoreboard
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            // Inflate the scoreboard item layout
            View itemView = getLayoutInflater().inflate(R.layout.scoreboard_item, scoreboardContainer, false);


            // Find TextViews in the scoreboard item layout
            TextView tvParticipantName = itemView.findViewById(R.id.tvParticipantName);
            TextView tvParticipantScore = itemView.findViewById(R.id.tvParticipantScore);

            // Set the participant's name and score
            tvParticipantName.setText(entry.getKey());
            tvParticipantScore.setText(entry.getValue() + " " + getString(R.string.score));

            // Add the item to the scoreboard container
            scoreboardContainer.addView(itemView);
        }

        // Set the content view of the BottomSheetDialog
        bottomSheetDialog.setContentView(view);

        // Ensure the bottom sheet is expanded and takes up full height
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<?> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED); // Expand the bottom sheet
                bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT; // Set height to match parent
                bottomSheet.requestLayout(); // Request layout to apply changes
            }
        });

        // Set click listener for the Return to Game button to dismiss the scoreboard
        returnToGameBtn.setOnClickListener(v -> bottomSheetDialog.dismiss());

        // Set click listener for the Reset Game button to prompt resetting participants
        resetGameBtn.setOnClickListener(v -> {
            bottomSheetDialog.dismiss(); // Dismiss the scoreboard
            showParticipantsPrompt(); // Show the participants prompt to reset the game
        });


        // If a timer is set, automatically dismiss the scoreboard after 5 seconds
        if (isTimerSet) {
            new Handler().postDelayed(bottomSheetDialog::dismiss, 5000);
        }

        // Show the BottomSheetDialog to the user
        bottomSheetDialog.show();
    }

    // Method to load questions based on the selected category
    private void loadQuestions(String category) {
        // Load categories from JSON using the JsonLoader helper class
        List<CategoryData> jsonCategories = JsonLoader.loadCategories(this);

        // Loop through each category data to find the matching category
        for (CategoryData categoryData : jsonCategories) {
            if (categoryData.getCategory().equals(category)) {
                // Set the questions list to the questions of the matched category
                questionsList = categoryData.getQuestions();
                break; // Exit the loop once the category is found
            }
        }
    }

    // Method to hide the keyboard from a specific view
    private void hideKeyboard(View view) {
        // Get the InputMethodManager service
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            // Hide the soft keyboard from the window token of the view
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // Method to create a GradientDrawable for a circle background
    private GradientDrawable getCircleBackground(String colorHex) {
        // Create a new GradientDrawable
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL); // Set shape to oval
        drawable.setColor(Color.parseColor(colorHex)); // Set the color from hex code
        drawable.setSize(CIRCLE_SIZE_DP, CIRCLE_SIZE_DP); // Set the size of the circle
        return drawable;
    }

    // Static method to get a value from a map by index
    public static String getValueByIndex(HashMap<Integer, String> map, int index) {
        // Convert the map values to a list
        List<String> values = new ArrayList<>(map.values());

        // Check if the index is within the list bounds
        if (index >= 0 && index < values.size()) {
            return values.get(index); // Return the value at the specified index
        } else {
            return null; // Return null if index is out of bounds
        }
    }
}
