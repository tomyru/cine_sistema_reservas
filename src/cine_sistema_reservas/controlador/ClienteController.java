package cine_sistema_reservas.controlador;

import cine_sistema_reservas.modelo.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class ClienteController {

    @FXML
    private Label encabezado;
    @FXML
    private TextField campoNombre;
    @FXML
    private TextField campoIdentificacion;
    @FXML
    private GridPane filas;
    @FXML
    private Label total;

    private Funcion funcion;
    private List<Butaca> butacas;
    private final List<ComboBox<TipoEntrada>> combos = new ArrayList<>();
    private Runnable alVolver;
    private Consumer<DatosReserva> alRevisar;

    // Datos que viajan a la pantalla de revisión
    public record DatosReserva(Cliente cliente, List<Butaca> butacas, List<TipoEntrada> tipos) {

    }

    public void setDatos(Funcion funcion, List<Butaca> butacas) {
        this.funcion = funcion;
        this.butacas = butacas;
        encabezado.setText(funcion.getPelicula() + " · " + funcion.getSala()
                + " · " + funcion.getInicio().toLocalTime());
        int fila = 0;
        for (Butaca b : butacas) {
            ComboBox<TipoEntrada> combo = new ComboBox<>();
            combo.getItems().addAll(new TipoGeneral(), new TipoEstudiante());
            combo.setValue(combo.getItems().get(0));
            combo.setOnAction(e -> actualizarTotal());
            combos.add(combo);
            filas.add(new Label(b.toString()), 0, fila);
            filas.add(combo, 1, fila);
            fila++;
        }
        actualizarTotal();
    }

    private void actualizarTotal() {
        double suma = 0;
        for (ComboBox<TipoEntrada> c : combos) {
            suma += c.getValue().calcularPrecio(funcion.getPrecio());
        }
        total.setText("Total: $" + suma);
    }

    @FXML
    private void revisar() {
        try {
            Cliente cliente = new Cliente(campoNombre.getText(), campoIdentificacion.getText());
            List<TipoEntrada> tipos = new ArrayList<>();
            for (ComboBox<TipoEntrada> c : combos) {
                tipos.add(c.getValue());
            }
            alRevisar.accept(new DatosReserva(cliente, butacas, tipos));
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText(null);
            a.setContentText(e.getMessage());
            a.showAndWait();
        }
    }

    @FXML
    private void volver() {
        alVolver.run();
    }

    public void setAlVolver(Runnable alVolver) {
        this.alVolver = alVolver;
    }

    public void setAlRevisar(Consumer<DatosReserva> alRevisar) {
        this.alRevisar = alRevisar;
    }
}
