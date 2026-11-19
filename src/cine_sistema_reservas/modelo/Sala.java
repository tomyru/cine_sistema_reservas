package cine_sistema_reservas.modelo;

import cine_sistema_reservas.excepciones.NegocioException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Sala implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private int cantidadFilas;
    private int butacasPorFila;
    private List<Butaca> butacas;

    public Sala(String nombre, int cantidadFilas, int butacasPorFila) throws NegocioException {
        this.id = UUID.randomUUID().toString();
        setNombre(nombre);
        validarDistribucion(cantidadFilas, butacasPorFila);
        this.cantidadFilas = cantidadFilas;
        this.butacasPorFila = butacasPorFila;
        this.butacas = new ArrayList<>();
        generarButacas();
    }

    public String getId() {
        return id;
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
            throw new NegocioException("ERROR Sala; El nombre esta vacio.");
        }
        this.nombre = nombre.trim();
    }

    private void validarDistribucion(int cantidadFilas, int butacasPorFila) throws NegocioException {
        if (cantidadFilas < 1 || cantidadFilas > 26) {
            throw new NegocioException("ERROR Sala; Las filas deben estar entre 1 y 26.");
        }
        if (butacasPorFila < 1 || butacasPorFila > 40) {
            throw new NegocioException("ERROR Sala; Las butacas por fila deben ser al menos 1 y menor que 40.");
        }
    }

    private void generarButacas() throws NegocioException {
        for (int i = 0; i < cantidadFilas; i++) {
            char letra = (char) ('A' + i);
            for (int j = 1; j <= butacasPorFila; j++) {
                Butaca butaca = new Butaca(letra, j);
                butacas.add(butaca);
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Sala other = (Sala) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return nombre;
    }

}
