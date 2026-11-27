package cine_sistema_reservas.controlador;

import cine_sistema_reservas.modelo.Butaca;
import cine_sistema_reservas.modelo.Funcion;
import cine_sistema_reservas.servicios.ReservaService;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;

public class ButacasController {

    @FXML
    private Label encabezado;
    @FXML
    private Label resumen;
    @FXML
    private GridPane grilla;
    @FXML
    private Button botonContinuar;

    private Funcion funcion;
    private final Set<Butaca> seleccionadas = new LinkedHashSet<>();
    private Runnable alVolver;
    private Consumer<List<Butaca>> alContinuar;

    public void setDatos(Funcion funcion, ReservaService reservas) {
        this.funcion = funcion;
        encabezado.setText(funcion.getPelicula() + " · " + funcion.getSala()
                + " · " + funcion.getInicio().toLocalDate()
                + " · " + funcion.getInicio().toLocalTime());
        Set<Butaca> ocupadas = reservas.butacasOcupadas(funcion);
        grilla.getChildren().clear();
        int columnas = funcion.getSala().getButacasPorFila();
        int i = 0;
        for (Butaca b : funcion.getSala().getButacas()) {
            ToggleButton boton = new ToggleButton(b.toString());
            boton.setPrefWidth(50);
            if (ocupadas.contains(b)) {
                boton.setDisable(true);
                boton.setStyle("-fx-background-color: #bbbbbb; -fx-strikethrough: true;");
            } else {
                boton.setOnAction(e -> {
                    if (boton.isSelected()) {
                        seleccionadas.add(b);
                    } else {
                        seleccionadas.remove(b);
                    }
                    actualizarResumen();
                });
            }
            grilla.add(boton, i % columnas, i / columnas);
            i++;
        }
        actualizarResumen();
    }

    private void actualizarResumen() {
        resumen.setText("Butacas: " + (seleccionadas.isEmpty() ? "-" : seleccionadas)
                + " · Cantidad: " + seleccionadas.size()
                + " · Precio general: $" + funcion.getPrecio());
        botonContinuar.setDisable(seleccionadas.isEmpty());
    }

    public void setAlVolver(Runnable alVolver) {
        this.alVolver = alVolver;
    }

    public void setAlContinuar(Consumer<List<Butaca>> alContinuar) {
        this.alContinuar = alContinuar;
    }

    @FXML
    private void volver() {
        alVolver.run();
    }

    @FXML
    private void continuar() {
        alContinuar.accept(new ArrayList<>(seleccionadas));
    }
}