package cine_sistema_reservas.modelo;

import cine_sistema_reservas.excepciones.NegocioException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Funcion implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private Pelicula pelicula;
    private Sala sala;
    private LocalDateTime inicio;
    private double precio;
    private boolean activa;

    public Funcion(Pelicula pelicula, Sala sala, LocalDateTime inicio, double precio) throws NegocioException {
        this.id = UUID.randomUUID().toString();
        if (pelicula == null) {
            throw new NegocioException("ERROR Funcion; La pelicula no puede estar vacia.");
        }
        if (sala == null) {
            throw new NegocioException("ERROR Funcion; La sala no puede estar vacia.");
        }
        this.pelicula = pelicula;
        this.sala = sala;
        setInicio(inicio);
        setPrecio(precio);
        this.activa = true;
    }

    public String getId() {
        return id;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public Sala getSala() {
        return sala;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFin() {
        return inicio.plusMinutes(pelicula.getDuracionMinutos());
    }

    public double getPrecio() {
        return precio;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setInicio(LocalDateTime inicio) throws NegocioException {
        if (inicio == null) {
            throw new NegocioException("ERROR Funcion; El inicio no puede estar vacio.");
        }
        this.inicio = inicio;
    }

    public void setPrecio(double precio) throws NegocioException {
        if (precio <= 0) {
            throw new NegocioException("ERROR Funcion; El precio debe ser mayor a 0.");
        }
        this.precio = precio;
    }

    public void desactivar() {
        this.activa = false;
    }

    public boolean seSuperponeCon(Funcion otra) {
        return this.sala.equals(otra.sala)
                && this.inicio.isBefore(otra.getFin())
                && otra.inicio.isBefore(this.getFin());
    }

    public boolean haComenzado() {
        return inicio.isBefore(LocalDateTime.now());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.id);
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
        final Funcion other = (Funcion) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return pelicula + " - " + sala + " - " + inicio;
    }

}
