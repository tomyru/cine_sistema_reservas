package cine_sistema_reservas.modelo;

import cine_sistema_reservas.excepciones.NegocioException;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Pelicula implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String titulo;
    private int duracionMinutos;
    private boolean activa;

    public Pelicula(String titulo, int duracionMinutos) throws NegocioException {
        this.id = UUID.randomUUID().toString();
        setTitulo(titulo);
        setDuracionMinutos(duracionMinutos);
        this.activa = true;
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setTitulo(String titulo) throws NegocioException {
        if (titulo == null || titulo.isBlank()) {
            throw new NegocioException("ERROR Pelicula; El título es obligatorio.");
        }
        this.titulo = titulo.trim();
    }

    public void setDuracionMinutos(int duracionMinutos) throws NegocioException {
        if (duracionMinutos <= 0) {
            throw new NegocioException("ERROR Pelicula; La duración debe ser mayor a 0.");
        }
        this.duracionMinutos = duracionMinutos;
    }

    public void desactivar() {
        this.activa = false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.id);
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
        final Pelicula other = (Pelicula) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return titulo;
    }
}
