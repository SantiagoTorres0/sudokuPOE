package com.example.demo2.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MenuView implements IView {

    private Stage primaryStage;

    public MenuView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showView();
    }

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

    @Override
    public void deleteView() {
        primaryStage.close();
    }
}