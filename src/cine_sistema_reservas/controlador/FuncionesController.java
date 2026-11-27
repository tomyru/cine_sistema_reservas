package cine_sistema_reservas.controlador;

import cine_sistema_reservas.excepciones.AlmacenamientoException;
import cine_sistema_reservas.excepciones.NegocioException;
import cine_sistema_reservas.modelo.Funcion;
import cine_sistema_reservas.modelo.Pelicula;
import cine_sistema_reservas.modelo.Sala;
import cine_sistema_reservas.servicios.FuncionService;
import cine_sistema_reservas.servicios.PeliculaService;
import cine_sistema_reservas.servicios.SalaService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class FuncionesController {

    @FXML private ComboBox<Pelicula> comboPelicula;
    @FXML private ComboBox<Sala> comboSala;
    @FXML private DatePicker campoFecha;
    @FXML private TextField campoHora;
    @FXML private TextField campoPrecio;
    @FXML private TableView<Funcion> tabla;
    @FXML private TableColumn<Funcion, String> colPelicula;
    @FXML private TableColumn<Funcion, String> colSala;
    @FXML private TableColumn<Funcion, String> colFecha;
    @FXML private TableColumn<Funcion, String> colHora;
    @FXML private TableColumn<Funcion, Number> colPrecio;

    private FuncionService servicio;

    public void setServicios(FuncionService servicio, PeliculaService peliculas, SalaService salas) {
        this.servicio = servicio;
        comboPelicula.setItems(FXCollections.observableArrayList(
                peliculas.listar().stream().filter(Pelicula::isActiva).toList()));
        comboSala.setItems(FXCollections.observableArrayList(salas.listar()));
        colPelicula.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPelicula().getTitulo()));
        colSala.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSala().getNombre()));
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getInicio().toLocalDate().toString()));
        colHora.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getInicio().toLocalTime().toString()));
        colPrecio.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrecio()));
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
            Pelicula pelicula = comboPelicula.getValue();
            Sala sala = comboSala.getValue();
            LocalDate fecha = campoFecha.getValue();
            if (pelicula == null || sala == null || fecha == null) {
                mostrarError("Elegí película, sala y fecha.");
                return;
            }
            LocalTime hora = LocalTime.parse(campoHora.getText().trim());
            double precio = Double.parseDouble(campoPrecio.getText().trim().replace(',', '.'));
            servicio.registrar(pelicula, sala, LocalDateTime.of(fecha, hora), precio);
            cargarTabla();
            campoHora.clear();
            campoPrecio.clear();
        } catch (DateTimeParseException e) {
            mostrarError("La hora debe tener formato HH:mm, por ejemplo 18:00.");
        } catch (NumberFormatException e) {
            mostrarError("El precio debe ser un número.");
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