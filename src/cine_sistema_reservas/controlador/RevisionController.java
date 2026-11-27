package cine_sistema_reservas.controlador;

import cine_sistema_reservas.excepciones.AlmacenamientoException;
import cine_sistema_reservas.excepciones.NegocioException;
import cine_sistema_reservas.modelo.Butaca;
import cine_sistema_reservas.modelo.Cliente;
import cine_sistema_reservas.modelo.Funcion;
import cine_sistema_reservas.modelo.Reserva;
import cine_sistema_reservas.modelo.TipoEntrada;
import cine_sistema_reservas.servicios.ReservaService;
import java.util.List;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public class RevisionController {

    @FXML private Label detalle;
    @FXML private Label total;

    private ReservaService reservas;
    private Funcion funcion;
    private Cliente cliente;
    private List<Butaca> butacas;
    private List<TipoEntrada> tipos;
    private Runnable alCorregir;
    private Consumer<Reserva> alConfirmar;

    public void setDatos(ReservaService reservas, Funcion funcion, Cliente cliente,
            List<Butaca> butacas, List<TipoEntrada> tipos) {
        this.reservas = reservas;
        this.funcion = funcion;
        this.cliente = cliente;
        this.butacas = butacas;
        this.tipos = tipos;

        StringBuilder sb = new StringBuilder();
        sb.append(funcion).append("\nCliente: ").append(cliente)
          .append(" · ID: ").append(cliente.getIdentificacion()).append("\n\n");
        double suma = 0;
        for (int i = 0; i < butacas.size(); i++) {
            double precio = tipos.get(i).calcularPrecio(funcion.getPrecio());
            suma += precio;
            sb.append(butacas.get(i)).append(" · ").append(tipos.get(i))
              .append(" · $").append(precio).append("\n");
        }
        detalle.setText(sb.toString());
        total.setText("Total: $" + suma);
    }

    @FXML
    private void confirmar() {
        try {
            Reserva r = reservas.confirmar(funcion, cliente, butacas, tipos);
            alConfirmar.accept(r);
        } catch (NegocioException | AlmacenamientoException e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText(null);
            a.setContentText(e.getMessage());
            a.showAndWait();
        }
    }

    @FXML
    private void corregir() {
        alCorregir.run();
    }

    public void setAlCorregir(Runnable alCorregir) {
        this.alCorregir = alCorregir;
    }

    public void setAlConfirmar(Consumer<Reserva> alConfirmar) {
        this.alConfirmar = alConfirmar;
    }
}