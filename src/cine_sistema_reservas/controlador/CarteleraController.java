package cine_sistema_reservas.controlador;

import cine_sistema_reservas.modelo.Funcion;
import cine_sistema_reservas.modelo.Pelicula;
import cine_sistema_reservas.servicios.FuncionService;
import cine_sistema_reservas.servicios.PeliculaService;
import java.util.function.Consumer;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CarteleraController {

    @FXML
    private ComboBox<Pelicula> comboPelicula;
    @FXML
    private DatePicker campoFecha;
    @FXML
    private TableView<Funcion> tabla;
    @FXML
    private TableColumn<Funcion, String> colPelicula;
    @FXML
    private TableColumn<Funcion, String> colSala;
    @FXML
    private TableColumn<Funcion, String> colFecha;
    @FXML
    private TableColumn<Funcion, String> colHora;
    @FXML
    private TableColumn<Funcion, Number> colPrecio;

    private FuncionService funciones;

    public void setServicios(FuncionService funciones, PeliculaService peliculas) {
        this.funciones = funciones;
        comboPelicula.setItems(FXCollections.observableArrayList(
                peliculas.listar().stream().filter(Pelicula::isActiva).toList()));
        colPelicula.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPelicula().getTitulo()));
        colSala.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSala().getNombre()));
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getInicio().toLocalDate().toString()));
        colHora.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getInicio().toLocalTime().toString()));
        colPrecio.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrecio()));
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        buscar();
    }

    @FXML
    private void buscar() {
        tabla.setItems(FXCollections.observableArrayList(
                funciones.cartelera(comboPelicula.getValue(), campoFecha.getValue())));
        tabla.refresh();
    }

    @FXML
    private void limpiar() {
        comboPelicula.setValue(null);
        campoFecha.setValue(null);
        buscar();
    }
    private Consumer<Funcion> alReservar;

    public void setAlReservar(Consumer<Funcion> alReservar) {
        this.alReservar = alReservar;
    }

    @FXML
    private void reservar() {
        Funcion elegida = tabla.getSelectionModel().getSelectedItem();
        if (elegida == null) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText(null);
            a.setContentText("Seleccioná una función de la tabla.");
            a.showAndWait();
            return;
        }
        alReservar.accept(elegida);
    }
}
