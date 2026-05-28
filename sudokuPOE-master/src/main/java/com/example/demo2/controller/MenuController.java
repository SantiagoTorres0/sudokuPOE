package com.example.demo2.controller;

import com.example.demo2.view.SudokuView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Button;

/**
 * Controller for the main menu of the Sudoku application.
 * Handles user interactions on the menu screen.
 */
public class MenuController {

    /**
     * Button that starts the Sudoku game.
     */
    @FXML
    private Button startButton;

    /**
     * Handles the start button click event.
     * Opens the Sudoku game view in the current stage.
     *
     * @param event the action event triggered by the button click
     */
    @FXML
    void onHandleStart(ActionEvent event) {
        Stage stage = (Stage) startButton.getScene().getWindow();
        new SudokuView(stage);
    }
}