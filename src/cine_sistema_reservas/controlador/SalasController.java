package cine_sistema_reservas.controlador;

import cine_sistema_reservas.excepciones.AlmacenamientoException;
import cine_sistema_reservas.excepciones.NegocioException;
import cine_sistema_reservas.modelo.Sala;
import cine_sistema_reservas.servicios.SalaService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SalasController {

    @FXML private TextField campoNombre;
    @FXML private TextField campoFilas;
    @FXML private TextField campoButacas;
    @FXML private TableView<Sala> tabla;
    @FXML private TableColumn<Sala, String> colNombre;
    @FXML private TableColumn<Sala, Number> colFilas;
    @FXML private TableColumn<Sala, Number> colButacas;
    @FXML private TableColumn<Sala, Number> colCapacidad;

    private SalaService servicio;

    public void setServicio(SalaService servicio) {
        this.servicio = servicio;
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colFilas.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCantidadFilas()));
        colButacas.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getButacasPorFila()));
        colCapacidad.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCapacidad()));
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        cargarTabla();
    }

    private void cargarTabla() {
        tabla.setItems(FXCollections.observableArrayList(servicio.listar()));
        tabla.refresh();
    }

    @FXML
    private void registrar() {
        try {
            String nombre = campoNombre.getText();
            int filas = Integer.parseInt(campoFilas.getText().trim());
            int butacas = Integer.parseInt(campoButacas.getText().trim());
            servicio.registrar(nombre, filas, butacas);
            cargarTabla();
            campoNombre.clear();
            campoFilas.clear();
            campoButacas.clear();
        } catch (NumberFormatException e) {
            mostrarError("Filas y butacas por fila deben ser números enteros.");
        } catch (NegocioException | AlmacenamientoException e) {
            mostrarError(e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}