package com.example.demo2.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Vista principal del juego de Sudoku 6x6.
 *
 * <p>Implementa la interfaz {@link IView} y se encarga únicamente de cargar
 * la interfaz gráfica definida en el archivo FXML y mostrarla en pantalla.
 * Sigue el patrón de diseño MVC (Modelo-Vista-Controlador), por lo que
 * <strong>no contiene lógica de negocio ni acceso al modelo</strong>.</p>
 *
 * <p>El flujo de inicialización es el siguiente:</p>
 * <ol>
 *     <li>{@code Main} crea una instancia de {@code SudokuView} pasándole el {@link Stage}.</li>
 *     <li>El constructor llama a {@link #showView()}.</li>
 *     <li>{@link #showView()} carga el archivo {@code sudoku-view.fxml} con {@link FXMLLoader}.</li>
 *     <li>JavaFX instancia automáticamente el controlador declarado en el FXML
 *         e invoca su método {@code initialize()}.</li>
 *     <li>La escena se configura y la ventana se muestra al usuario.</li>
 * </ol>
 *
 * <p>El archivo FXML debe estar ubicado en:</p>
 * <pre>{@code /com/example/demo/sudoku-view.fxml}</pre>
 *
 * @author Equipo de desarrollo
 * @version 1.0
 * @see IView
 */
public class SudokuView implements IView {

    /**
     * Ventana principal de la aplicación JavaFX.
     *
     * <p>Recibida desde {@code Main.start(Stage)} y usada para configurar
     * y mostrar la escena del juego.</p>
     */
    private Stage primaryStage;

    /**
     * Crea la vista del Sudoku y la muestra inmediatamente en pantalla.
     *
     * <p>Recibe el {@link Stage} principal de la aplicación y llama a
     * {@link #showView()} para cargar el FXML y configurar la ventana.</p>
     *
     * @param primaryStage la ventana principal proporcionada por JavaFX en
     *                     {@code Application.start(Stage)}
     */
    public SudokuView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showView();
    }

    /**
     * Carga el archivo FXML y configura la ventana principal.
     *
     * <p>Usa {@link FXMLLoader} para cargar el layout definido en
     * {@code sudoku-view.fxml}. Durante la carga, JavaFX instancia
     * automáticamente el controlador especificado en el atributo
     * {@code fx:controller} del FXML e invoca su método {@code initialize()}.</p>
     *
     * <p>Si el archivo FXML no puede cargarse, se imprime la traza del error
     * en consola mediante {@code e.printStackTrace()} y la ventana no se muestra.</p>
     *
     * <p>Propiedades de la ventana configuradas:</p>
     * <ul>
     *     <li>Título: "Sudoku"</li>
     *     <li>No redimensionable ({@code setResizable(false)})</li>
     * </ul>
     */
    @Override
    public void showView() {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/demo2/sudoku-view.fxml")
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
     * Cierra la ventana principal del juego.
     *
     * <p>Llama a {@link Stage#close()} para ocultar y liberar la ventana.
     * Puede usarse para cerrar la vista desde el controlador si fuera necesario.</p>
     */
    @Override
    public void deleteView() {
        primaryStage.close();
    }
}
