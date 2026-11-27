package cine_sistema_reservas.controlador;

import cine_sistema_reservas.excepciones.*;
import cine_sistema_reservas.modelo.Pelicula;
import cine_sistema_reservas.servicios.PeliculaService;
import java.util.Optional;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PeliculasController {

    @FXML
    private TextField campoTitulo;
    @FXML
    private TextField campoDuracion;
    @FXML
    private TableView<Pelicula> tabla;
    @FXML
    private TableColumn<Pelicula, String> colTitulo;
    @FXML
    private TableColumn<Pelicula, Number> colDuracion;
    @FXML
    private TableColumn<Pelicula, String> colEstado;

    private PeliculaService servicio;

    public void setServicio(PeliculaService servicio) {
        this.servicio = servicio;
        colTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        colDuracion.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getDuracionMinutos()));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isActiva() ? "Activa" : "Inactiva"));
        cargarTabla();
    }

    private void cargarTabla() {
        tabla.setItems(FXCollections.observableArrayList(servicio.listar()));
        tabla.refresh();
    }

    @FXML
    private void registrar() {
        try {
            String titulo = campoTitulo.getText();
            int duracion = Integer.parseInt(campoDuracion.getText().trim());
            if (enEdicion != null) {
                servicio.modificar(enEdicion, titulo, duracion);
                enEdicion = null;
            } else {
                servicio.registrar(titulo, duracion);
            }
            cargarTabla();
            campoTitulo.clear();
            campoDuracion.clear();
        } catch (NumberFormatException e) {
            mostrarError("La duración debe ser un número entero.");
        } catch (NegocioException e) {
            mostrarError(e.getMessage());
        } catch (AlmacenamientoException e) {
            mostrarError(e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void cambiarEstado() {
        Pelicula seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Seleccioná una película de la tabla.");
            return;
        }
        try {
            if (seleccionada.isActiva()) {
                Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                confirmacion.setHeaderText(null);
                confirmacion.setContentText("¿Desactivar la película \"" + seleccionada.getTitulo() + "\"?");
                Optional<ButtonType> respuesta = confirmacion.showAndWait();
                if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
                    return;
                }
                servicio.desactivar(seleccionada);
            } else {
                servicio.reactivar(seleccionada);
            }
            cargarTabla();
        } catch (AlmacenamientoException e) {
            mostrarError(e.getMessage());
        }
    }
    private Pelicula enEdicion;

    @FXML
    private void editar() {
        Pelicula seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Seleccioná una película de la tabla.");
            return;
        }
        enEdicion = seleccionada;
        campoTitulo.setText(seleccionada.getTitulo());
        campoDuracion.setText(String.valueOf(seleccionada.getDuracionMinutos()));
    }
}
