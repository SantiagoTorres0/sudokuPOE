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
 * Controlador principal del juego de Sudoku 6x6.
 *
 * <p>Actúa como intermediario entre la vista ({@code sudoku-view.fxml})
 * y el modelo ({@link SudokuBoard}), siguiendo el patrón de diseño MVC.</p>
 *
 * @author Equipo de desarrollo
 * @version 1.0
 */
public class SudokuController implements Initializable {

    // -------------------------------------------------------
    // Grosor de bordes para separar bloques visualmente
    // -------------------------------------------------------

    /** Borde delgado entre celdas del mismo bloque (px). */
    private static final int BORDER_THIN = 1;

    /** Borde grueso en los límites entre bloques (px). */
    private static final int BORDER_THICK = 3;

    /** Color del borde delgado (gris claro). */
    private static final String COLOR_THIN = "#aaaaaa";

    /** Color del borde grueso (negro). */
    private static final String COLOR_THICK = "#222222";

    @FXML
    private GridPane sudokuGrid;

    private SudokuBoard sudokuBoard;

    /**
     * Referencias a todos los {@link TextField} del tablero organizados por
     * {@code [fila][columna]}, para actualizarlos directamente sin recorrer el GridPane.
     */
    private TextField[][] cells = new TextField[6][6];

    // -------------------------------------------------------
    // Inicialización
    // -------------------------------------------------------

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sudokuBoard = new SudokuBoard();
        buildBoard();
    }

    // -------------------------------------------------------
    // Construcción del tablero visual
    // -------------------------------------------------------

    /**
     * Construye la cuadrícula visual leyendo el estado actual del modelo.
     *
     * <p>Crea un {@link TextField} por celda, aplica el estilo de color
     * según si es fija o editable, y aplica los bordes de bloque con
     * {@link #blockBorderStyle(int, int)}.</p>
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

    // -------------------------------------------------------
    // Bordes de bloque
    // -------------------------------------------------------

    /**
     * Genera el estilo CSS de bordes para una celda según su posición en el bloque.
     *
     * <p>Cada bloque es de 2 filas × 3 columnas. Los bordes en los límites
     * entre bloques son más gruesos ({@value #BORDER_THICK}px) y oscuros,
     * mientras que los bordes internos son delgados ({@value #BORDER_THIN}px).</p>
     *
     * <pre>
     * Bloques:
     * +===+===+===+===+===+===+
     * | B1        | B2        |  fila 0
     * +   +   +   +   +   +   +  fila 1
     * +===+===+===+===+===+===+
     * | B3        | B4        |  fila 2
     * +   +   +   +   +   +   +  fila 3
     * +===+===+===+===+===+===+
     * | B5        | B6        |  fila 4
     * +   +   +   +   +   +   +  fila 5
     * +===+===+===+===+===+===+
     * </pre>
     *
     * @param row fila de la celda (0–5)
     * @param col columna de la celda (0–5)
     * @return cadena CSS con los cuatro bordes de la celda
     */
    private String blockBorderStyle(int row, int col) {
        // Borde superior: grueso si es inicio de bloque de fila (0, 2, 4)
        int top    = (row % 2 == 0) ? BORDER_THICK : BORDER_THIN;

        // Borde inferior: grueso siempre en la última fila del tablero
        int bottom = (row == 5)     ? BORDER_THICK : BORDER_THIN;

        // Borde izquierdo: grueso si es inicio de bloque de columna (0, 3)
        int left   = (col % 3 == 0) ? BORDER_THICK : BORDER_THIN;

        // Borde derecho: grueso siempre en la última columna del tablero
        int right  = (col == 5)     ? BORDER_THICK : BORDER_THIN;

        String ct = COLOR_THICK;
        String cn = COLOR_THIN;

        return String.format(
            "-fx-border-color: %s %s %s %s;" +
            "-fx-border-width: %d %d %d %d;",
            (top    == BORDER_THICK ? ct : cn),  // top color
            (right  == BORDER_THICK ? ct : cn),  // right color
            (bottom == BORDER_THICK ? ct : cn),  // bottom color
            (left   == BORDER_THICK ? ct : cn),  // left color
            top, right, bottom, left
        );
    }

    // -------------------------------------------------------
    // Manejo de entrada del usuario
    // -------------------------------------------------------

    /**
     * Asocia un listener de teclado a una celda editable.
     *
     * <p>Valida que la entrada sea un dígito entre 1 y 6, la envía al modelo
     * y actualiza el color de fondo manteniendo los bordes de bloque.</p>
     *
     * <p>Convención de colores de fondo:</p>
     * <ul>
     *     <li>Blanco: movimiento válido.</li>
     *     <li>Rojo claro ({@code #ffcccc}): entrada inválida.</li>
     *     <li>Azul claro ({@code #cce5ff}): celda revelada por pista.</li>
     * </ul>
     *
     * @param txt la celda {@link TextField}
     * @param row la fila de la celda
     * @param col la columna de la celda
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
                    showAlert("¡Ganaste!", "¡Felicitaciones! Completaste el Sudoku correctamente.");
                }
            } else {
                txt.setStyle("-fx-background-color: #ffcccc; -fx-font-size: 18px;" + border);
            }
        });
    }

    // -------------------------------------------------------
    // Manejadores de botones
    // -------------------------------------------------------

    /**
     * Revela la solución de la primera celda vacía encontrada (pista).
     *
     * @param event evento del botón
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
                        showAlert("¡Ganaste!", "¡Felicitaciones! Completaste el Sudoku.");
                    }
                    return;
                }
            }
        }
        showAlert("Sin celdas vacías", "No hay celdas vacías para revelar.");
    }

    /**
     * Genera una nueva partida y reconstruye el tablero visual.
     *
     * @param event evento del botón
     */
    @FXML
    void onHandleNewGame(ActionEvent event) {
        sudokuBoard.generateNewGame();
        buildBoard();
    }

    // -------------------------------------------------------
    // Utilidades
    // -------------------------------------------------------

    /**
     * Muestra un cuadro de diálogo informativo al usuario.
     *
     * @param title   título del diálogo
     * @param message mensaje a mostrar
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
