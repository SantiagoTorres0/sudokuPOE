package com.example.demo2.model;

import javafx.fxml.FXML;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Modelo de lógica para un juego de Sudoku dinámico de 6x6.
 *
 * <p>Esta clase no contiene ninguna vista, controlador, interfaz gráfica,
 * listener de teclado, botón ni componente de Swing o JavaFX.
 * Solo gestiona los datos internos del Sudoku y sus reglas de validación.</p>
 *
 * <p>La estructura del tablero es la siguiente:</p>
 * <ul>
 *     <li>6 filas</li>
 *     <li>6 columnas</li>
 *     <li>6 bloques de 2 filas × 3 columnas</li>
 *     <li>Números válidos: del 1 al 6</li>
 * </ul>
 *
 * <p>Los datos del juego se almacenan en estructuras {@link HashMap} y {@link HashSet}:</p>
 * <ul>
 *     <li>{@code board}: mapa {@code "fila,columna"} → número actual</li>
 *     <li>{@code solution}: mapa {@code "fila,columna"} → número solución</li>
 *     <li>{@code fixedCells}: conjunto de claves de celdas no editables</li>
 * </ul>
 *
 * <p>Una celda vacía simplemente <strong>no existe</strong> en el mapa {@code board},
 * eliminando la necesidad del valor centinela {@code 0}.</p>
 *
 * @author Santiago Torres
 * @version 2.0
 */
public class SudokuBoard {

    /** Tamaño del tablero (6 filas y 6 columnas). */
    private static final int SIZE = 6;

    /** Número de filas por bloque. */
    private static final int BLOCK_ROWS = 2;

    /** Número de columnas por bloque. */
    private static final int BLOCK_COLS = 3;

    /**
     * Estado actual del tablero.
     *
     * <p>Clave: {@code "fila,columna"} — Valor: número colocado (1–6).
     * Si una clave no existe, la celda está vacía.</p>
     */
    private Map<String, Integer> board;

    /**
     * Solución completa y válida del Sudoku.
     *
     * <p>Clave: {@code "fila,columna"} — Valor: número correcto (1–6).
     * Se usa para generar los números iniciales y para dar pistas.</p>
     */
    private Map<String, Integer> solution;

    /**
     * Conjunto de claves de celdas fijas (no editables por el usuario).
     *
     * <p>Una clave presente en este conjunto indica que la celda fue
     * generada como número inicial y no puede modificarse.</p>
     */
    private Set<String> fixedCells;

    /** Generador de números aleatorios para mezclar posiciones y números. */
    private Random random;

    /**
     * Crea un nuevo modelo de Sudoku 6x6 y genera una partida nueva.
     */
    public SudokuBoard() {
        board = new HashMap<>();
        solution = new HashMap<>();
        fixedCells = new HashSet<>();
        random = new Random();
        generateNewGame();
    }

    // -------------------------------------------------------
    // Generación del juego
    // -------------------------------------------------------

    /**
     * Genera una nueva partida de Sudoku.
     *
     * <p>Limpia todas las estructuras, genera una solución completa válida
     * mediante backtracking y coloca exactamente dos números visibles
     * en cada bloque de 2×3.</p>
     */
    public void generateNewGame() {
        board.clear();
        solution.clear();
        fixedCells.clear();
        generateSolution(0, 0);
        generateInitialNumbers();
    }

    /**
     * Genera una solución completa válida usando backtracking recursivo.
     *
     * <p>Prueba números del 1 al 6 en orden aleatorio en cada celda.
     * Si un número puede colocarse según las reglas, continúa con la
     * siguiente celda. Si ninguno es válido, retrocede.</p>
     *
     * @param row    fila actual que se está procesando
     * @param column columna actual que se está procesando
     * @return {@code true} si la solución fue generada con éxito
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
     * Crea un arreglo con los números del 1 al 6 en orden aleatorio
     * usando el algoritmo Fisher-Yates.
     *
     * @return arreglo mezclado con los números del 1 al 6
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
     * Genera los números iniciales visibles del puzzle.
     *
     * <p>Selecciona aleatoriamente 2 posiciones de cada bloque 2×3,
     * copia el valor de la solución al tablero y las marca como fijas
     * en {@code fixedCells}.</p>
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
     * Mezcla un arreglo de posiciones {@code [fila, columna]} usando Fisher-Yates.
     *
     * @param positions arreglo de posiciones a mezclar
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
    // Validación
    // -------------------------------------------------------

    /**
     * Verifica si un número puede colocarse en una celda de un mapa dado.
     *
     * <p>Valida las tres reglas del Sudoku:</p>
     * <ul>
     *     <li>El número no debe existir en la misma fila.</li>
     *     <li>El número no debe existir en la misma columna.</li>
     *     <li>El número no debe existir en el mismo bloque 2×3.</li>
     * </ul>
     *
     * @param map    el mapa donde se verificará el número
     * @param row    fila donde se colocará el número
     * @param column columna donde se colocará el número
     * @param number número a validar
     * @return {@code true} si el número puede colocarse
     */
    public boolean canPlaceInMap(Map<String, Integer> map, int row, int column, int number) {
        if (!isValidPosition(row, column)) return false;
        if (!isValidNumber(number)) return false;

        // Validar fila
        for (int col = 0; col < SIZE; col++) {
            if (map.getOrDefault(key(row, col), 0) == number) return false;
        }

        // Validar columna
        for (int r = 0; r < SIZE; r++) {
            if (map.getOrDefault(key(r, column), 0) == number) return false;
        }

        // Validar bloque 2×3
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
     * Verifica si el usuario puede colocar un número en una celda específica.
     *
     * <p>El valor actual de la celda se elimina temporalmente del mapa antes
     * de la validación, permitiendo reemplazar un valor ya ingresado.</p>
     *
     * @param row    fila donde el usuario desea colocar el número
     * @param column columna donde el usuario desea colocar el número
     * @param number número que el usuario desea colocar
     * @return {@code true} si el movimiento es válido
     */
    public boolean verifyMove(int row, int column, int number) {
        if (!isValidPosition(row, column)) return false;
        if (!isValidNumber(number)) return false;
        if (isFixedCell(row, column)) return false;

        String k = key(row, column);
        Integer current = board.remove(k); // eliminar temporalmente

        boolean valid = canPlaceInMap(board, row, column, number);

        if (current != null) board.put(k, current); // restaurar

        return valid;
    }

    /**
     * Coloca un número en el tablero si el movimiento es válido.
     *
     * <p>Llama internamente a {@link #verifyMove(int, int, int)}.
     * Si el movimiento es válido, el número se agrega al mapa {@code board}.</p>
     *
     * @param row    fila donde se colocará el número
     * @param column columna donde se colocará el número
     * @param number número a colocar
     * @return {@code true} si el número fue colocado
     */
    public boolean placeNumber(int row, int column, int number) {
        if (!verifyMove(row, column, number)) return false;
        board.put(key(row, column), number);
        return true;
    }

    /**
     * Limpia una celda no fija eliminando su entrada del mapa {@code board}.
     *
     * @param row    fila de la celda a limpiar
     * @param column columna de la celda a limpiar
     * @return {@code true} si la celda fue limpiada
     */
    public boolean clearCell(int row, int column) {
        if (!isValidPosition(row, column)) return false;
        if (isFixedCell(row, column)) return false;
        board.remove(key(row, column));
        return true;
    }

    // -------------------------------------------------------
    // Estado del juego
    // -------------------------------------------------------

    /**
     * Verifica si el tablero está completo.
     *
     * <p>El tablero está completo cuando el mapa {@code board} contiene
     * exactamente 36 entradas (6×6).</p>
     *
     * @return {@code true} si todas las celdas tienen un valor
     */
    public boolean isComplete() {
        return board.size() == SIZE * SIZE;
    }

    /**
     * Verifica si el estado actual del tablero es válido según las reglas del Sudoku.
     *
     * <p>Cada valor se elimina temporalmente del mapa antes de validarlo
     * para evitar que la celda se detecte a sí misma como duplicado.</p>
     *
     * @return {@code true} si el tablero es válido
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
     * Verifica si el juego ha sido ganado (tablero completo y válido).
     *
     * @return {@code true} si el juego fue ganado
     */
    public boolean isGameWon() {
        return isComplete() && isBoardValid();
    }

    // -------------------------------------------------------
    // Getters
    // -------------------------------------------------------

    /**
     * Retorna el valor actual de una celda, o {@code 0} si está vacía.
     *
     * @param row    fila de la celda
     * @param column columna de la celda
     * @return valor almacenado, {@code 0} si vacía, {@code -1} si posición inválida
     */
    public int getValue(int row, int column) {
        if (!isValidPosition(row, column)) return -1;
        return board.getOrDefault(key(row, column), 0);
    }

    /**
     * Retorna el valor de la solución en una celda específica.
     *
     * @param row    fila de la celda
     * @param column columna de la celda
     * @return valor de la solución, o {@code -1} si la posición es inválida
     */
    public int getSolutionValue(int row, int column) {
        if (!isValidPosition(row, column)) return -1;
        return solution.getOrDefault(key(row, column), -1);
    }

    /**
     * Verifica si una celda es fija (no editable por el usuario).
     *
     * @param row    fila de la celda
     * @param column columna de la celda
     * @return {@code true} si la celda es fija
     */
    public boolean isFixedCell(int row, int column) {
        if (!isValidPosition(row, column)) return false;
        return fixedCells.contains(key(row, column));
    }

    /**
     * Verifica si una posición es válida dentro del tablero.
     *
     * @param row    fila a validar
     * @param column columna a validar
     * @return {@code true} si la posición está dentro del tablero
     */
    public boolean isValidPosition(int row, int column) {
        return row >= 0 && row < SIZE && column >= 0 && column < SIZE;
    }

    /**
     * Verifica si un número es válido para un Sudoku 6x6.
     *
     * @param number número a validar
     * @return {@code true} si el número está entre 1 y 6
     */
    public boolean isValidNumber(int number) {
        return number >= 1 && number <= SIZE;
    }

    /**
     * Retorna una copia del mapa del tablero actual.
     *
     * @return copia del tablero como {@link Map}
     */
    public Map<String, Integer> getBoard() {
        return new HashMap<>(board);
    }

    /**
     * Retorna una copia del conjunto de celdas fijas.
     *
     * @return copia del conjunto de claves fijas como {@link Set}
     */
    public Set<String> getFixedCells() {
        return new HashSet<>(fixedCells);
    }

    // -------------------------------------------------------
    // Utilidades internas
    // -------------------------------------------------------

    /**
     * Genera la clave {@code "fila,columna"} usada como identificador en los mapas.
     *
     * @param row    fila
     * @param column columna
     * @return cadena con formato {@code "fila,columna"}
     */
    private String key(int row, int column) {
        return row + "," + column;
    }

    /**
     * Parsea una clave {@code "fila,columna"} y retorna {@code [fila, columna]}.
     *
     * @param key clave a parsear
     * @return arreglo de dos enteros: {@code [fila, columna]}
     */
    private int[] parseKey(String key) {
        String[] parts = key.split(",");
        return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
    }

    /**
     * Imprime el estado actual del tablero en la consola.
     * Útil para depuración durante el desarrollo.
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
