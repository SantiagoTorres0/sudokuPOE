package com.example.demo2.model;

import javafx.fxml.FXML;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Logic model for a dynamic 6x6 Sudoku game.
 *
 * <p>This class contains no view, controller, graphical interface,
 * keyboard listeners, buttons, or Swing/JavaFX components.
 * It only manages the internal Sudoku data and its validation rules.</p>
 *
 * <p>The board structure is as follows:</p>
 * <ul>
 *     <li>6 rows</li>
 *     <li>6 columns</li>
 *     <li>6 blocks of 2 rows × 3 columns</li>
 *     <li>Valid numbers: 1 through 6</li>
 * </ul>
 *
 * <p>Game data is stored in {@link HashMap} and {@link HashSet} structures:</p>
 * <ul>
 *     <li>{@code board}: map {@code "row,column"} → current number</li>
 *     <li>{@code solution}: map {@code "row,column"} → solution number</li>
 *     <li>{@code fixedCells}: set of keys for non-editable cells</li>
 * </ul>
 *
 * <p>An empty cell simply <strong>does not exist</strong> in the {@code board} map,
 * eliminating the need for a sentinel value of {@code 0}.</p>
 *
 * @author Santiago Torres
 * @version 2.0
 */
public class SudokuBoard {

    /** Board size (6 rows and 6 columns). */
    private static final int SIZE = 6;

    /** Number of rows per block. */
    private static final int BLOCK_ROWS = 2;

    /** Number of columns per block. */
    private static final int BLOCK_COLS = 3;

    /**
     * Current state of the board.
     *
     * <p>Key: {@code "row,column"} — Value: placed number (1–6).
     * If a key does not exist, the cell is empty.</p>
     */
    private Map<String, Integer> board;

    /**
     * Complete and valid solution for the Sudoku.
     *
     * <p>Key: {@code "row,column"} — Value: correct number (1–6).
     * Used to generate the initial numbers and to provide hints.</p>
     */
    private Map<String, Integer> solution;

    /**
     * Set of keys for fixed cells (not editable by the user).
     *
     * <p>A key present in this set indicates that the cell was
     * generated as an initial number and cannot be modified.</p>
     */
    private Set<String> fixedCells;

    /** Random number generator used to shuffle positions and numbers. */
    private Random random;

    /**
     * Creates a new 6x6 Sudoku model and generates a new game.
     */
    public SudokuBoard() {
        board = new HashMap<>();
        solution = new HashMap<>();
        fixedCells = new HashSet<>();
        random = new Random();
        generateNewGame();
    }

    // -------------------------------------------------------
    // Game generation
    // -------------------------------------------------------

    /**
     * Generates a new Sudoku game.
     *
     * <p>Clears all structures, generates a complete valid solution
     * using backtracking, and places exactly two visible numbers
     * in each 2×3 block.</p>
     */
    public void generateNewGame() {
        board.clear();
        solution.clear();
        fixedCells.clear();
        generateSolution(0, 0);
        generateInitialNumbers();
    }

    /**
     * Generates a complete valid solution using recursive backtracking.
     *
     * <p>Tries numbers 1 through 6 in random order for each cell.
     * If a number can be placed according to the rules, it continues
     * to the next cell. If none are valid, it backtracks.</p>
     *
     * @param row    current row being processed
     * @param column current column being processed
     * @return {@code true} if the solution was successfully generated
     */
    private boolean generateSolution(int row, int column) {
        if (row == SIZE) return true;

        int nextRow = row;
        int nextCol = column + 1;
        if (nextCol == SIZE) {
            nextCol = 0;
            nextRow++;
        }

        int[] numbers = createShuffledNumbers();

        for (int number : numbers) {
            if (canPlaceInMap(solution, row, column, number)) {
                solution.put(key(row, column), number);

                if (generateSolution(nextRow, nextCol)) return true;

                solution.remove(key(row, column));
            }
        }

        return false;
    }

    /**
     * Creates an array with numbers 1 through 6 in random order
     * using the Fisher-Yates algorithm.
     *
     * @return shuffled array with numbers 1 through 6
     */
    private int[] createShuffledNumbers() {
        int[] numbers = {1, 2, 3, 4, 5, 6};

        for (int i = SIZE - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = numbers[i];
            numbers[i] = numbers[j];
            numbers[j] = temp;
        }

        return numbers;
    }

    /**
     * Generates the initial visible numbers for the puzzle.
     *
     * <p>Randomly selects 2 positions from each 2×3 block,
     * copies the solution value to the board, and marks them as fixed
     * in {@code fixedCells}.</p>
     */
    private void generateInitialNumbers() {
        for (int blockRow = 0; blockRow < SIZE; blockRow += BLOCK_ROWS) {
            for (int blockCol = 0; blockCol < SIZE; blockCol += BLOCK_COLS) {

                int[][] positions = new int[BLOCK_ROWS * BLOCK_COLS][2];
                int index = 0;

                for (int row = blockRow; row < blockRow + BLOCK_ROWS; row++) {
                    for (int col = blockCol; col < blockCol + BLOCK_COLS; col++) {
                        positions[index][0] = row;
                        positions[index][1] = col;
                        index++;
                    }
                }

                shufflePositions(positions);

                for (int i = 0; i < 2; i++) {
                    int row = positions[i][0];
                    int col = positions[i][1];
                    String k = key(row, col);

                    board.put(k, solution.get(k));
                    fixedCells.add(k);
                }
            }
        }
    }

    /**
     * Shuffles an array of {@code [row, column]} positions using Fisher-Yates.
     *
     * @param positions array of positions to shuffle
     */
    private void shufflePositions(int[][] positions) {
        for (int i = positions.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int[] temp = positions[i];
            positions[i] = positions[j];
            positions[j] = temp;
        }
    }

    // -------------------------------------------------------
    // Validation
    // -------------------------------------------------------

    /**
     * Checks whether a number can be placed in a cell of a given map.
     *
     * <p>Validates the three Sudoku rules:</p>
     * <ul>
     *     <li>The number must not already exist in the same row.</li>
     *     <li>The number must not already exist in the same column.</li>
     *     <li>The number must not already exist in the same 2×3 block.</li>
     * </ul>
     *
     * @param map    the map where the number will be checked
     * @param row    row where the number will be placed
     * @param column column where the number will be placed
     * @param number number to validate
     * @return {@code true} if the number can be placed
     */
    public boolean canPlaceInMap(Map<String, Integer> map, int row, int column, int number) {
        if (!isValidPosition(row, column)) return false;
        if (!isValidNumber(number)) return false;

        // Validate row
        for (int col = 0; col < SIZE; col++) {
            if (map.getOrDefault(key(row, col), 0) == number) return false;
        }

        // Validate column
        for (int r = 0; r < SIZE; r++) {
            if (map.getOrDefault(key(r, column), 0) == number) return false;
        }

        // Validate 2×3 block
        int startRow = (row / BLOCK_ROWS) * BLOCK_ROWS;
        int startCol = (column / BLOCK_COLS) * BLOCK_COLS;

        for (int r = startRow; r < startRow + BLOCK_ROWS; r++) {
            for (int c = startCol; c < startCol + BLOCK_COLS; c++) {
                if (map.getOrDefault(key(r, c), 0) == number) return false;
            }
        }

        return true;
    }

    /**
     * Checks whether the user can place a number in a specific cell.
     *
     * <p>The current cell value is temporarily removed from the map before
     * validation, allowing an already-entered value to be replaced.</p>
     *
     * @param row    row where the user wants to place the number
     * @param column column where the user wants to place the number
     * @param number number the user wants to place
     * @return {@code true} if the move is valid
     */
    public boolean verifyMove(int row, int column, int number) {
        if (!isValidPosition(row, column)) return false;
        if (!isValidNumber(number)) return false;
        if (isFixedCell(row, column)) return false;

        String k = key(row, column);
        Integer current = board.remove(k); // temporarily remove

        boolean valid = canPlaceInMap(board, row, column, number);

        if (current != null) board.put(k, current); // restore

        return valid;
    }

    /**
     * Places a number on the board if the move is valid.
     *
     * <p>Internally calls {@link #verifyMove(int, int, int)}.
     * If the move is valid, the number is added to the {@code board} map.</p>
     *
     * @param row    row where the number will be placed
     * @param column column where the number will be placed
     * @param number number to place
     * @return {@code true} if the number was placed
     */
    public boolean placeNumber(int row, int column, int number) {
        if (!verifyMove(row, column, number)) return false;
        board.put(key(row, column), number);
        return true;
    }

    /**
     * Clears a non-fixed cell by removing its entry from the {@code board} map.
     *
     * @param row    row of the cell to clear
     * @param column column of the cell to clear
     * @return {@code true} if the cell was cleared
     */
    public boolean clearCell(int row, int column) {
        if (!isValidPosition(row, column)) return false;
        if (isFixedCell(row, column)) return false;
        board.remove(key(row, column));
        return true;
    }

    // -------------------------------------------------------
    // Game state
    // -------------------------------------------------------

    /**
     * Checks whether the board is complete.
     *
     * <p>The board is complete when the {@code board} map contains
     * exactly 36 entries (6×6).</p>
     *
     * @return {@code true} if all cells have a value
     */
    public boolean isComplete() {
        return board.size() == SIZE * SIZE;
    }

    /**
     * Checks whether the current board state is valid according to Sudoku rules.
     *
     * <p>Each value is temporarily removed from the map before validation
     * to prevent a cell from detecting itself as a duplicate.</p>
     *
     * @return {@code true} if the board is valid
     */
    public boolean isBoardValid() {
        for (Map.Entry<String, Integer> entry : board.entrySet()) {
            String k = entry.getKey();
            int number = entry.getValue();
            int[] pos = parseKey(k);

            board.remove(k);
            boolean valid = canPlaceInMap(board, pos[0], pos[1], number);
            board.put(k, number);

            if (!valid) return false;
        }
        return true;
    }

    /**
     * Checks whether the game has been won (board complete and valid).
     *
     * @return {@code true} if the game was won
     */
    public boolean isGameWon() {
        return isComplete() && isBoardValid();
    }

    // -------------------------------------------------------
    // Getters
    // -------------------------------------------------------

    /**
     * Returns the current value of a cell, or {@code 0} if empty.
     *
     * @param row    row of the cell
     * @param column column of the cell
     * @return stored value, {@code 0} if empty, {@code -1} if position is invalid
     */
    public int getValue(int row, int column) {
        if (!isValidPosition(row, column)) return -1;
        return board.getOrDefault(key(row, column), 0);
    }

    /**
     * Returns the solution value at a specific cell.
     *
     * @param row    row of the cell
     * @param column column of the cell
     * @return solution value, or {@code -1} if the position is invalid
     */
    public int getSolutionValue(int row, int column) {
        if (!isValidPosition(row, column)) return -1;
        return solution.getOrDefault(key(row, column), -1);
    }

    /**
     * Checks whether a cell is fixed (not editable by the user).
     *
     * @param row    row of the cell
     * @param column column of the cell
     * @return {@code true} if the cell is fixed
     */
    public boolean isFixedCell(int row, int column) {
        if (!isValidPosition(row, column)) return false;
        return fixedCells.contains(key(row, column));
    }

    /**
     * Checks whether a position is valid within the board.
     *
     * @param row    row to validate
     * @param column column to validate
     * @return {@code true} if the position is within the board
     */
    public boolean isValidPosition(int row, int column) {
        return row >= 0 && row < SIZE && column >= 0 && column < SIZE;
    }

    /**
     * Checks whether a number is valid for a 6x6 Sudoku.
     *
     * @param number number to validate
     * @return {@code true} if the number is between 1 and 6
     */
    public boolean isValidNumber(int number) {
        return number >= 1 && number <= SIZE;
    }

    /**
     * Returns a copy of the current board map.
     *
     * @return copy of the board as a {@link Map}
     */
    public Map<String, Integer> getBoard() {
        return new HashMap<>(board);
    }

    /**
     * Returns a copy of the fixed cells set.
     *
     * @return copy of the fixed keys set as a {@link Set}
     */
    public Set<String> getFixedCells() {
        return new HashSet<>(fixedCells);
    }

    // -------------------------------------------------------
    // Internal utilities
    // -------------------------------------------------------

    /**
     * Generates the {@code "row,column"} key used as an identifier in the maps.
     *
     * @param row    row
     * @param column column
     * @return string in the format {@code "row,column"}
     */
    private String key(int row, int column) {
        return row + "," + column;
    }

    /**
     * Parses a {@code "row,column"} key and returns {@code [row, column]}.
     *
     * @param key key to parse
     * @return integer array of two elements: {@code [row, column]}
     */
    private int[] parseKey(String key) {
        String[] parts = key.split(",");
        return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
    }

    /**
     * Prints the current state of the board to the console.
     * Useful for debugging during development.
     */
    public void showBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                System.out.print(board.getOrDefault(key(row, col), 0) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}