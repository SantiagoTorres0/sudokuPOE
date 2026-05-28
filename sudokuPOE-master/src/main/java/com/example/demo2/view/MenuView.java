package com.example.demo2.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * View class responsible for displaying the main menu screen.
 * Loads the menu FXML layout and binds it to the primary application stage.
 * Implements {@link IView} to follow a consistent view lifecycle contract.
 */
public class MenuView implements IView {

    /** The primary application window where the menu scene will be displayed. */
    private Stage primaryStage;

    /**
     * Constructs a new {@code MenuView} and immediately renders the menu screen.
     *
     * @param primaryStage the main {@link Stage} of the JavaFX application
     */
    public MenuView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showView();
    }

    /**
     * Loads the menu FXML file, builds the scene, and configures the primary stage.
     * Sets the window title to "Sudoku", disables resizing, and makes the stage visible.
     * If the FXML file cannot be loaded, the stack trace is printed to standard error.
     */
    @Override
    public void showView() {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/demo2/menu-view.fxml")
        );
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sudoku");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Closes the primary stage, effectively removing the menu view from the screen.
     */
    @Override
    public void deleteView() {
        primaryStage.close();
    }
}