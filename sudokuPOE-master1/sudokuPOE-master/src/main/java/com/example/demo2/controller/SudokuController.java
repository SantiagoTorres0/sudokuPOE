package com.example.demo2.controller;

import com.example.demo2.model.SudokuBoard;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main controller for the 6x6 Sudoku game.
 *
 * <p>Acts as an intermediary between the view ({@code sudoku-view.fxml})
 * and the model ({@link SudokuBoard}), following the MVC design pattern.</p>
 *
 * @author Development team
 * @version 1.0
 */
public class SudokuController implements Initializable {

    /** Thin border between cells within the same block (px). */
    private static final int BORDER_THIN = 1;

    /** Thick border at block boundaries (px). */
    private static final int BORDER_THICK = 3;

    /** Thin border color (light gray). */
    private static final String COLOR_THIN = "#aaaaaa";

    /** Thick border color (black). */
    private static final String COLOR_THICK = "#222222";

    @FXML
    private GridPane sudokuGrid;

    private SudokuBoard sudokuBoard;

    /**
     * References to all {@link TextField} elements on the board, organized by
     * {@code [row][column]}, for direct updates without traversing the GridPane.
     */
    private TextField[][] cells = new TextField[6][6];

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sudokuBoard = new SudokuBoard();
        buildBoard();
    }

    /**
     * Builds the visual grid by reading the current state of the model.
     *
     * <p>Creates a {@link TextField} per cell, applies color styling
     * depending on whether the cell is fixed or editable, and applies
     * block borders using {@link #blockBorderStyle(int, int)}.</p>
     */
    private void buildBoard() {
        sudokuGrid.getChildren().clear();

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                TextField txt = new TextField();
                txt.setAlignment(Pos.CENTER);
                txt.setPrefWidth(60);
                txt.setPrefHeight(60);

                int value = sudokuBoard.getValue(row, col);
                String borderStyle = blockBorderStyle(row, col);

                if (value != 0) {
                    txt.setText(String.valueOf(value));
                    txt.setEditable(false);
                    txt.setStyle(
                            "-fx-background-color: #c0c0c0;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-font-size: 18px;" +
                                    borderStyle
                    );
                } else {
                    txt.setStyle(
                            "-fx-background-color: white;" +
                                    "-fx-font-size: 18px;" +
                                    borderStyle
                    );
                    attachInputHandler(txt, row, col);
                }

                cells[row][col] = txt;
                sudokuGrid.add(txt, col, row);
            }
        }
    }

    /**
     * Generates the CSS border style for a cell based on its position within the block.
     *
     * <p>Each block is 2 rows × 3 columns. Borders at block boundaries are
     * thicker ({@value #BORDER_THICK}px) and darker, while internal borders
     * are thin ({@value #BORDER_THIN}px).</p>
     *
     * <pre>
     * Blocks:
     * +===+===+===+===+===+===+
     * | B1        | B2        |  row 0
     * +   +   +   +   +   +   +  row 1
     * +===+===+===+===+===+===+
     * | B3        | B4        |  row 2
     * +   +   +   +   +   +   +  row 3
     * +===+===+===+===+===+===+
     * | B5        | B6        |  row 4
     * +   +   +   +   +   +   +  row 5
     * +===+===+===+===+===+===+
     * </pre>
     *
     * @param row the cell's row (0–5)
     * @param col the cell's column (0–5)
     * @return CSS string with the four borders of the cell
     */
    private String blockBorderStyle(int row, int col) {
        // Top border: thick if start of row block (0, 2, 4)
        int top    = (row % 2 == 0) ? BORDER_THICK : BORDER_THIN;

        // Bottom border: always thick on the last row of the board
        int bottom = (row == 5)     ? BORDER_THICK : BORDER_THIN;

        // Left border: thick if start of column block (0, 3)
        int left   = (col % 3 == 0) ? BORDER_THICK : BORDER_THIN;

        // Right border: always thick on the last column of the board
        int right  = (col == 5)     ? BORDER_THICK : BORDER_THIN;

        String ct = COLOR_THICK;
        String cn = COLOR_THIN;

        return String.format(
                "-fx-border-color: %s %s %s %s;" +
                        "-fx-border-width: %d %d %d %d;",
                (top    == BORDER_THICK ? ct : cn),
                (right  == BORDER_THICK ? ct : cn),
                (bottom == BORDER_THICK ? ct : cn),
                (left   == BORDER_THICK ? ct : cn),
                top, right, bottom, left
        );
    }

    /**
     * Attaches a keyboard listener to an editable cell.
     *
     * <p>Validates that the input is a digit between 1 and 6, sends it to
     * the model, and updates the background color while preserving block borders.</p>
     *
     * <p>Background color conventions:</p>
     * <ul>
     *     <li>White: valid move.</li>
     *     <li>Light red ({@code #ffcccc}): invalid input.</li>
     *     <li>Light blue ({@code #cce5ff}): cell revealed by hint.</li>
     * </ul>
     *
     * @param txt the {@link TextField} cell
     * @param row the cell's row
     * @param col the cell's column
     */
    private void attachInputHandler(TextField txt, int row, int col) {
        txt.setOnKeyReleased(keyEvent -> {
            String text = txt.getText();
            String border = blockBorderStyle(row, col);

            if (text.isEmpty()) {
                sudokuBoard.clearCell(row, col);
                txt.setStyle("-fx-background-color: white; -fx-font-size: 18px;" + border);
                return;
            }

            if (!text.matches("[1-6]")) {
                txt.clear();
                txt.setStyle("-fx-background-color: #ffcccc; -fx-font-size: 18px;" + border);
                return;
            }

            int number = Integer.parseInt(text);
            boolean placed = sudokuBoard.placeNumber(row, col, number);

            if (placed) {
                txt.setStyle("-fx-background-color: white; -fx-font-size: 18px;" + border);

                if (sudokuBoard.isGameWon()) {
                    showAlert("You won!", "Congratulations! You completed the Sudoku correctly.");
                }
            } else {
                txt.setStyle("-fx-background-color: #ffcccc; -fx-font-size: 18px;" + border);
                showAlert("Invalid move", "That number already exists in the same row, column, or block.");
            }
        });
    }

    /**
     * Reveals the solution for the first empty cell found (hint).
     *
     * @param event the button event
     */
    @FXML
    void onHandleHelp(ActionEvent event) {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                if (sudokuBoard.getValue(row, col) == 0) {
                    int hint = sudokuBoard.getSolutionValue(row, col);
                    sudokuBoard.placeNumber(row, col, hint);
                    cells[row][col].setText(String.valueOf(hint));
                    cells[row][col].setStyle(
                            "-fx-background-color: #cce5ff;" +
                                    "-fx-font-size: 18px;" +
                                    blockBorderStyle(row, col)
                    );

                    if (sudokuBoard.isGameWon()) {
                        showAlert("You won!", "Congratulations! You completed the Sudoku.");
                    }
                    return;
                }
            }
        }
        showAlert("No empty cells", "There are no empty cells to reveal.");
    }

    /**
     * Generates a new game and rebuilds the visual board.
     *
     * @param event the button event
     */
    @FXML
    void onHandleNewGame(ActionEvent event) {
        sudokuBoard.generateNewGame();
        buildBoard();
    }

    /**
     * Displays an informational dialog to the user.
     *
     * @param title   the dialog title
     * @param message the message to display
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}