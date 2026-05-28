package com.example.demo2;

import com.example.demo2.view.MenuView;
import com.example.demo2.view.SudokuView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main entry point of the 6x6 Sudoku application.
 *
 * <p>This class extends JavaFX's {@link Application} and acts as the
 * startup point for the graphical interface lifecycle.
 * Its sole responsibility is to create the main view ({@link SudokuView})
 * and pass it the {@link Stage} provided by JavaFX.</p>
 *
 * <p>The full initialization flow is as follows:</p>
 * <ol>
 *     <li>{@link #main(String[])} calls {@code launch(args)}, which starts the JavaFX thread.</li>
 *     <li>JavaFX calls {@link #start(Stage)} with the primary window.</li>
 *     <li>{@code start} creates an instance of {@link SudokuView}, which loads the FXML.</li>
 *     <li>JavaFX automatically instantiates the controller declared in the FXML.</li>
 *     <li>The controller initializes the model ({@code SudokuBoard}) and builds the board.</li>
 * </ol>
 *
 * @author Development team
 * @version 1.0
 * @see SudokuView
 */
public class Main extends Application {

    /**
     * Entry point of the JavaFX lifecycle.
     *
     * <p>JavaFX calls this method automatically on the JavaFX Application Thread
     * after {@link #main(String[])} calls {@code launch()}. The main game view
     * is created here.</p>
     *
     * @param primaryStage the primary window provided by the JavaFX framework
     */
    @Override
    public void start(Stage primaryStage) {
        new MenuView(primaryStage);
    }

    /**
     * Java application entry point.
     *
     * <p>Calls {@code launch(args)}, which initializes the JavaFX framework,
     * creates the primary {@link Stage}, and invokes {@link #start(Stage)}.</p>
     *
     * @param args command-line arguments (not used in this application)
     */
    public static void main(String[] args) {
        launch(args);
    }
}