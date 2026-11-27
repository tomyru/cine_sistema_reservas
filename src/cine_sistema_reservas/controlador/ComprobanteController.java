package cine_sistema_reservas.controlador;

import cine_sistema_reservas.modelo.Entrada;
import cine_sistema_reservas.modelo.Reserva;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ComprobanteController {

    @FXML private Label titulo;
    @FXML private Label detalle;

    private Runnable alVerReservas;
    private Runnable alNuevaReserva;

    public void setDatos(Reserva r) {
        titulo.setText("Reserva " + r.getEstado().name().toLowerCase() + " · " + r.getCodigo());
        StringBuilder sb = new StringBuilder();
        sb.append("Película: ").append(r.getFuncion().getPelicula()).append("\n");
        sb.append("Función: ").append(r.getFuncion().getSala())
          .append(" · ").append(r.getFuncion().getInicio().toLocalDate())
          .append(" · ").append(r.getFuncion().getInicio().toLocalTime()).append("\n");
        sb.append("Cliente: ").append(r.getCliente())
          .append(" · ID: ").append(r.getCliente().getIdentificacion()).append("\n\n");
        for (Entrada e : r.getEntradas()) {
            sb.append(e.getButaca()).append(" · ").append(e.getTipo())
              .append(" · $").append(e.getPrecioConfirmado()).append("\n");
        }
        sb.append("\nImporte confirmado: $").append(r.getTotal());
        detalle.setText(sb.toString());
    }

    @FXML private void verReservas() { alVerReservas.run(); }
    @FXML private void nuevaReserva() { alNuevaReserva.run(); }

    public void setAlVerReservas(Runnable r) { this.alVerReservas = r; }
    public void setAlNuevaReserva(Runnable r) { this.alNuevaReserva = r; }
}