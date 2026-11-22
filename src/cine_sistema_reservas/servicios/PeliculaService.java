package cine_sistema_reservas.servicios;

import cine_sistema_reservas.excepciones.*;
import cine_sistema_reservas.persistencia.*;
import cine_sistema_reservas.modelo.Pelicula;
import cine_sistema_reservas.modelo.Funcion;
import java.util.List;

public class PeliculaService {

    private final EstadoCine estado;
    private final AlmacenamientoArchivo almacenamiento;
    private final Repositorio<Pelicula> repo;

    public PeliculaService(EstadoCine estado, AlmacenamientoArchivo almacenamiento) {
        this.estado = estado;
        this.almacenamiento = almacenamiento;
        this.repo = new Repositorio<>(estado.getPeliculas());
    }

    public Pelicula registrar(String titulo, int duracionMinutos) throws NegocioException, AlmacenamientoException {
        Pelicula nueva = new Pelicula(titulo, duracionMinutos);
        repo.agregar(nueva);
        try {
            almacenamiento.guardar(estado);
        } catch (AlmacenamientoException e) {
            repo.quitar(nueva);
            throw e;
        }
        return nueva;
    }

    public void modificar(Pelicula p, String titulo, int duracionMinutos)
            throws NegocioException, AlmacenamientoException {

        String tituloAnterior = p.getTitulo();
        int duracionAnterior = p.getDuracionMinutos();

        try {
            p.setTitulo(titulo);
            p.setDuracionMinutos(duracionMinutos);
            verificarSinSuperposiciones(p);
            almacenamiento.guardar(estado);
        } catch (NegocioException | AlmacenamientoException e) {
            p.setTitulo(tituloAnterior);
            p.setDuracionMinutos(duracionAnterior);
            throw e;
        }
    }

    private void verificarSinSuperposiciones(Pelicula p) throws NegocioException {
        for (Funcion f : estado.getFunciones()) {
            if (!f.isActiva() || !f.getPelicula().equals(p)) {
                continue;
            }
            for (Funcion otra : estado.getFunciones()) {
                if (otra != f && otra.isActiva() && f.seSuperponeCon(otra)) {
                    throw new NegocioException("Con esa duración, la función del "
                            + f.getInicio().toLocalDate() + " a las " + f.getInicio().toLocalTime()
                            + " se superpone con otra en la sala " + f.getSala().getNombre()
                            + " (" + otra.getInicio().toLocalTime() + " a " + otra.getFin().toLocalTime() + ").");
                }
            }
        }
    }

    public List<Pelicula> listar() {
        return repo.listar();
    }

    public void desactivar(Pelicula p) throws AlmacenamientoException {
        if (!p.isActiva()) {
            return;
        }
        p.desactivar();
        try {
            almacenamiento.guardar(estado);
        } catch (AlmacenamientoException e) {
            p.reactivar();
            throw e;
        }
    }

    public void reactivar(Pelicula p) throws AlmacenamientoException {
        if (p.isActiva()) {
            return;
        }
        p.reactivar();
        try {
            almacenamiento.guardar(estado);
        } catch (AlmacenamientoException e) {
            p.desactivar();
            throw e;
        }
    }
}