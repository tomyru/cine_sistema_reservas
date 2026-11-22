package cine_sistema_reservas.persistencia;

import cine_sistema_reservas.modelo.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EstadoCine implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Pelicula> peliculas = new ArrayList<>();
    private List<Sala> salas = new ArrayList<>();
    private List<Funcion> funciones = new ArrayList<>();
    private List<Reserva> reservas = new ArrayList<>();
    private int ultimoNumeroReserva = 0;

    public List<Pelicula> getPeliculas() {
        return peliculas;
    }

    public List<Sala> getSalas() {
        return salas;
    }

    public List<Funcion> getFunciones() {
        return funciones;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public String siguienteCodigoReserva() {
        ultimoNumeroReserva++;
        return String.format("CIN-%03d", ultimoNumeroReserva);
    }

    /**
     * Solo para rollback de ReservaService: deshace el último código pedido.
     */
    public void devolverCodigo() {
        if (ultimoNumeroReserva > 0) {
            ultimoNumeroReserva--;
        }
    }

}
