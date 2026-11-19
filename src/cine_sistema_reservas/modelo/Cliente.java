package cine_sistema_reservas.modelo;

import cine_sistema_reservas.excepciones.NegocioException;
import java.io.Serializable;
import java.util.Objects;

public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private String identificacion;

    public Cliente(String nombre, String identificacion) throws NegocioException {
        setNombre(nombre);
        setIdentificacion(identificacion);
    }

    public String getNombre() {
        return nombre;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setNombre(String nombre) throws NegocioException {
        if (nombre == null || nombre.isBlank()) {
            throw new NegocioException("ERROR Cliente; El nombre esta vacio.");
        }
        this.nombre = nombre.trim();
    }

    public void setIdentificacion(String identificacion) throws NegocioException {
        if (identificacion == null || identificacion.isBlank()) {
            throw new NegocioException("ERROR Cliente; La identificacion esta vacia.");
        }
        this.identificacion = identificacion.trim();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.identificacion);
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
        final Cliente other = (Cliente) obj;
        return Objects.equals(this.identificacion, other.identificacion);
    }

    @Override
    public String toString() {
        return nombre;
    }

}
