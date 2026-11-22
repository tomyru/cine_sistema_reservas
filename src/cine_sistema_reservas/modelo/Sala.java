package cine_sistema_reservas.modelo;

import cine_sistema_reservas.excepciones.NegocioException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sala extends EntidadConId {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private int cantidadFilas;
    private int butacasPorFila;
    private List<Butaca> butacas;

    public Sala(String nombre, int cantidadFilas, int butacasPorFila) throws NegocioException {
        super();
        setNombre(nombre);
        validarDistribucion(cantidadFilas, butacasPorFila);
        this.cantidadFilas = cantidadFilas;
        this.butacasPorFila = butacasPorFila;
        this.butacas = new ArrayList<>();
        generarButacas();
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidadFilas() {
        return cantidadFilas;
    }

    public int getButacasPorFila() {
        return butacasPorFila;
    }

    public List<Butaca> getButacas() {
        return Collections.unmodifiableList(butacas);
    }

    public int getCapacidad() {
        return cantidadFilas * butacasPorFila;
    }

    public void setNombre(String nombre) throws NegocioException {
        if (nombre == null || nombre.isBlank()) {
            throw new NegocioException("ERROR Sala; El nombre de la sala está vacío.");
        }
        this.nombre = nombre.trim();
    }

    private void validarDistribucion(int cantidadFilas, int butacasPorFila) throws NegocioException {
        if (cantidadFilas < 1 || cantidadFilas > 26) {
            throw new NegocioException("ERROR Sala; Las filas deben estar entre 1 y 26.");
        }
        if (butacasPorFila < 1 || butacasPorFila > 40) {
            throw new NegocioException("ERROR Sala; Las butacas por fila deben estar entre 1 y 40.");
        }
    }

    private void generarButacas() throws NegocioException {
        for (int i = 0; i < cantidadFilas; i++) {
            char letra = (char) ('A' + i);
            for (int j = 1; j <= butacasPorFila; j++) {
                butacas.add(new Butaca(letra, j));
            }
        }
    }

    @Override
    public String toString() {
        return nombre;
    }
}