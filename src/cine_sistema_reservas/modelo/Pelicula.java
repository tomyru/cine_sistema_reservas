package cine_sistema_reservas.modelo;

import cine_sistema_reservas.excepciones.NegocioException;

public class Pelicula extends EntidadConId {

    private static final long serialVersionUID = 1L;

    private String titulo;
    private int duracionMinutos;
    private boolean activa;

    public Pelicula(String titulo, int duracionMinutos) throws NegocioException {
        super();
        setTitulo(titulo);
        setDuracionMinutos(duracionMinutos);
        this.activa = true;
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
            throw new NegocioException("ERROR Pelicula; El titulo es obligatorio.");
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

    public void reactivar() {
        this.activa = true;
    }

    @Override
    public String toString() {
        return titulo;
    }
}