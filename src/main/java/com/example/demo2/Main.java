package com.example.demo2;

import com.example.demo2.view.SudokuView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Punto de entrada principal de la aplicación Sudoku 6x6.
 *
 * <p>Esta clase extiende {@link Application} de JavaFX y actúa como
 * punto de arranque del ciclo de vida de la interfaz gráfica.
 * Su única responsabilidad es crear la vista principal ({@link SudokuView})
 * y pasarle el {@link Stage} proporcionado por JavaFX.</p>
 *
 * <p>El flujo de inicialización completo es el siguiente:</p>
 * <ol>
 *     <li>{@link #main(String[])} invoca {@code launch(args)}, que arranca el hilo de JavaFX.</li>
 *     <li>JavaFX llama a {@link #start(Stage)} con la ventana principal.</li>
 *     <li>{@code start} crea una instancia de {@link SudokuView}, que carga el FXML.</li>
 *     <li>JavaFX instancia automáticamente el controlador declarado en el FXML.</li>
 *     <li>El controlador inicializa el modelo ({@code SudokuBoard}) y construye el tablero.</li>
 * </ol>
 *
 * @author Equipo de desarrollo
 * @version 1.0
 * @see SudokuView
 */
public class Main extends Application {

    /**
     * Punto de entrada del ciclo de vida de JavaFX.
     *
     * <p>JavaFX invoca este método automáticamente en el hilo de la interfaz
     * gráfica (JavaFX Application Thread) después de que {@link #main(String[])}
     * llama a {@code launch()}. Aquí se crea la vista principal del juego.</p>
     *
     * @param primaryStage la ventana principal proporcionada por el framework JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        new SudokuView(primaryStage);
    }

    /**
     * Punto de entrada de la aplicación Java.
     *
     * <p>Llama a {@code launch(args)}, que inicializa el framework JavaFX,
     * crea el {@link Stage} principal e invoca {@link #start(Stage)}.</p>
     *
     * @param args argumentos de línea de comandos (no se usan en esta aplicación)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
