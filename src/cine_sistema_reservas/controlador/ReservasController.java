package cine_sistema_reservas.controlador;

import cine_sistema_reservas.excepciones.AlmacenamientoException;
import cine_sistema_reservas.excepciones.NegocioException;
import cine_sistema_reservas.modelo.Entrada;
import cine_sistema_reservas.modelo.EstadoReserva;
import cine_sistema_reservas.modelo.Reserva;
import cine_sistema_reservas.servicios.ReservaService;
import java.io.File;
import java.util.List;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

public class ReservasController {

    @FXML private TextField campoBusqueda;
    @FXML private TableView<Reserva> tabla;
    @FXML private TableColumn<Reserva, String> colCodigo;
    @FXML private TableColumn<Reserva, String> colCliente;
    @FXML private TableColumn<Reserva, String> colFuncion;
    @FXML private TableColumn<Reserva, String> colEstado;
    @FXML private Button botonCancelar;
    @FXML private Label detalle;

    private ReservaService servicio;
    private Reserva seleccionada;

    public void setServicio(ReservaService servicio) {
        this.servicio = servicio;
        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colCliente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCliente().getNombre()));
        colFuncion.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFuncion().getPelicula() + " / "
                + c.getValue().getFuncion().getInicio().toLocalDate() + " "
                + c.getValue().getFuncion().getInicio().toLocalTime()));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado().toString()));
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        mostrar(servicio.listar());
    }

    private void mostrar(List<Reserva> reservas) {
        tabla.setItems(FXCollections.observableArrayList(reservas));
        tabla.refresh();
    }

    @FXML
    private void buscar() {
        String texto = campoBusqueda.getText();
        try {
            if (texto != null && texto.trim().toUpperCase().startsWith("CIN-")) {
                mostrar(List.of(servicio.buscarPorCodigo(texto)));
            } else {
                mostrar(servicio.buscarPorIdentificacion(texto));
            }
        } catch (NegocioException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void limpiar() {
        campoBusqueda.clear();
        detalle.setText("");
        botonCancelar.setDisable(true);
        seleccionada = null;
        mostrar(servicio.listar());
    }

    @FXML
    private void verDetalle() {
        seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Seleccioná una reserva de la tabla.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(seleccionada.getCodigo()).append(" · ").append(seleccionada.getEstado()).append("\n");
        sb.append(seleccionada.getCliente()).append(" · ")
          .append(seleccionada.getFuncion()).append("\n");
        for (Entrada e : seleccionada.getEntradas()) {
            sb.append(e.getButaca()).append(" · ").append(e.getTipo())
              .append(" · $").append(e.getPrecioConfirmado()).append("\n");
        }
        sb.append("Importe confirmado: $").append(seleccionada.getTotal());
        detalle.setText(sb.toString());
        botonCancelar.setDisable(seleccionada.getEstado() == EstadoReserva.CANCELADA);
    }

    @FXML
    private void cancelar() {
        if (seleccionada == null) {
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Cancelar la reserva " + seleccionada.getCodigo()
                + "? Se liberarán las butacas.");
        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }
        try {
            servicio.cancelar(seleccionada);
            tabla.refresh();
            verDetalle();
        } catch (NegocioException | AlmacenamientoException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void exportar() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName("reservas.csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File destino = chooser.showSaveDialog(tabla.getScene().getWindow());
        if (destino == null) {
            return;
        }
        try {
            servicio.exportarCSV(destino);
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setHeaderText(null);
            ok.setContentText("Reservas exportadas a " + destino.getName());
            ok.showAndWait();
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
}