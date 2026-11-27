package cine_sistema_reservas.controlador;

import cine_sistema_reservas.modelo.Butaca;
import cine_sistema_reservas.modelo.Funcion;
import cine_sistema_reservas.modelo.Reserva;
import cine_sistema_reservas.servicios.FuncionService;
import cine_sistema_reservas.servicios.PeliculaService;
import cine_sistema_reservas.servicios.ReservaService;
import cine_sistema_reservas.servicios.SalaService;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class VentanaPrincipalController {

    @FXML
    private BorderPane raiz;

    @FXML
    private Label titulo;

    private PeliculaService peliculaService;
    private SalaService salaService;
    private FuncionService funcionService;
    private ReservaService reservaService;

    public void setServicios(PeliculaService peliculaService, SalaService salaService,
            FuncionService funcionService, ReservaService reservaService) {
        this.peliculaService = peliculaService;
        this.salaService = salaService;
        this.funcionService = funcionService;
        this.reservaService = reservaService;
    }

    @FXML
    private void mostrarCartelera() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/cine_sistema_reservas/vista/CarteleraView.fxml"));
        Parent vista = loader.load();
        CarteleraController controlador = loader.getController();
        controlador.setServicios(funcionService, peliculaService);
        controlador.setAlReservar(funcion -> mostrarButacas(funcion));
        raiz.setCenter(vista);
    }

    @FXML
    private void mostrarReservas() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cine_sistema_reservas/vista/ReservasView.fxml"));
            Parent vista = loader.load();
            ReservasController c = loader.getController();
            c.setServicio(reservaService);
            raiz.setCenter(vista);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void mostrarFunciones() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/cine_sistema_reservas/vista/FuncionesView.fxml"));
        Parent vista = loader.load();
        FuncionesController controlador = loader.getController();
        controlador.setServicios(funcionService, peliculaService, salaService);
        raiz.setCenter(vista);
    }
    
    @FXML
    private void mostrarSalas() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/cine_sistema_reservas/vista/SalasView.fxml"));
        Parent vista = loader.load();
        SalasController controlador = loader.getController();
        controlador.setServicio(salaService);
        raiz.setCenter(vista);
    }

    @FXML
    private void mostrarPeliculas() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/cine_sistema_reservas/vista/PeliculasView.fxml"));
        Parent vista = loader.load();
        PeliculasController controlador = loader.getController();
        controlador.setServicio(peliculaService);
        raiz.setCenter(vista);
    }

    private void mostrarButacas(Funcion funcion) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cine_sistema_reservas/vista/ButacasView.fxml"));
            Parent vista = loader.load();
            ButacasController c = loader.getController();
            c.setDatos(funcion, reservaService);
            c.setAlVolver(() -> {
                try {
                    mostrarCartelera();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            c.setAlContinuar(butacas -> mostrarCliente(funcion, butacas));
            raiz.setCenter(vista);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void mostrarCliente(Funcion funcion, List<Butaca> butacas) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cine_sistema_reservas/vista/ClienteView.fxml"));
            Parent vista = loader.load();
            ClienteController c = loader.getController();
            c.setDatos(funcion, butacas);
            c.setAlVolver(() -> mostrarButacas(funcion));
            c.setAlRevisar(datos -> mostrarRevision(funcion, datos));
            raiz.setCenter(vista);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarComprobante(Reserva reserva) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cine_sistema_reservas/vista/ComprobanteView.fxml"));
            Parent vista = loader.load();
            ComprobanteController c = loader.getController();
            c.setDatos(reserva);
            c.setAlNuevaReserva(() -> {
                try {
                    mostrarCartelera();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            c.setAlVerReservas(() -> mostrarReservas());
            raiz.setCenter(vista);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarRevision(Funcion funcion, ClienteController.DatosReserva datos) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cine_sistema_reservas/vista/RevisionView.fxml"));
            Parent vista = loader.load();
            RevisionController c = loader.getController();
            c.setDatos(reservaService, funcion, datos.cliente(), datos.butacas(), datos.tipos());
            c.setAlCorregir(() -> mostrarCliente(funcion, datos.butacas()));
            c.setAlConfirmar(reserva -> mostrarComprobante(reserva));
            raiz.setCenter(vista);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
