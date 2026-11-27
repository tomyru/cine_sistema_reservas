package cine_sistema_reservas;

import cine_sistema_reservas.controlador.VentanaPrincipalController;
import cine_sistema_reservas.excepciones.AlmacenamientoException;
import cine_sistema_reservas.persistencia.AlmacenamientoArchivo;
import cine_sistema_reservas.persistencia.EstadoCine;
import cine_sistema_reservas.servicios.FuncionService;
import cine_sistema_reservas.servicios.PeliculaService;
import cine_sistema_reservas.servicios.ReservaService;
import cine_sistema_reservas.servicios.SalaService;
import java.io.File;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Cine_sistema_reservas extends Application {

    @Override
    public void start(Stage stage) {
        try {
//            EstadoCine y AlmacenamientoArchivo; el resto los recibe. Así toda la aplicación comparte los mismos datos.
            AlmacenamientoArchivo alm = new AlmacenamientoArchivo(new File("datos/cine.dat"));
            EstadoCine estado = alm.cargar();

            PeliculaService peliculaService = new PeliculaService(estado, alm);
            SalaService salaService = new SalaService(estado, alm);
            FuncionService funcionService = new FuncionService(estado, alm);
            ReservaService reservaService = new ReservaService(estado, alm);

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cine_sistema_reservas/vista/VentanaPrincipal.fxml"));
            Parent raiz = loader.load();
            VentanaPrincipalController c = loader.getController();
            
            c.setServicios(peliculaService, salaService, funcionService, reservaService);

            stage.setScene(new Scene(raiz, 800, 500));
            stage.setTitle("Sistema de reservas");
            stage.show();
            
        } catch (AlmacenamientoException e) {
            mostrarErrorYSalir(e.getMessage());
        } catch (Exception e) {
            mostrarErrorYSalir("No se pudo iniciar la aplicación: " + e.getMessage());
        }
    }

    private void mostrarErrorYSalir(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
