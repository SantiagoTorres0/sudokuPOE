package com.example.demo2.view;

/**
 * Interfaz que define el contrato básico para las vistas de la aplicación.
 *
 * <p>Forma parte del patrón de diseño MVC (Modelo-Vista-Controlador) y
 * establece las operaciones mínimas que toda vista debe implementar:
 * mostrarse y cerrarse.</p>
 *
 * <p>Al programar contra esta interfaz en lugar de contra implementaciones
 * concretas, el controlador puede trabajar con cualquier tipo de vista
 * sin depender de sus detalles internos.</p>
 *
 * @author Equipo de desarrollo
 * @version 1.0
 * @see SudokuView
 */
public interface IView {

    /**
     * Muestra la vista al usuario.
     *
     * <p>Las implementaciones deben cargar y renderizar todos los componentes
     * gráficos necesarios. En el caso de {@link SudokuView}, este método
     * carga el archivo FXML y configura la ventana principal.</p>
     */
    void showView();

    /**
     * Cierra y elimina la vista.
     *
     * <p>Las implementaciones deben liberar los recursos gráficos asociados
     * y ocultar la ventana. En el caso de {@link SudokuView}, este método
     * llama a {@code Stage.close()}.</p>
     */
    void deleteView();
}
