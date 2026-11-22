package cine_sistema_reservas.servicios;

import cine_sistema_reservas.excepciones.AlmacenamientoException;
import cine_sistema_reservas.excepciones.NegocioException;
import cine_sistema_reservas.modelo.Funcion;
import cine_sistema_reservas.modelo.Pelicula;
import cine_sistema_reservas.modelo.Sala;
import cine_sistema_reservas.persistencia.AlmacenamientoArchivo;
import cine_sistema_reservas.persistencia.EstadoCine;
import cine_sistema_reservas.persistencia.Repositorio;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FuncionService {

    private final EstadoCine estado;
    private final AlmacenamientoArchivo almacenamiento;
    private final Repositorio<Funcion> repo;

    public FuncionService(EstadoCine estado, AlmacenamientoArchivo almacenamiento) {
        this.estado = estado;
        this.almacenamiento = almacenamiento;
        this.repo = new Repositorio<>(estado.getFunciones());
    }

    public Funcion registrar(Pelicula pelicula, Sala sala, LocalDateTime inicio, double precio)
            throws NegocioException, AlmacenamientoException {
        Funcion nueva = new Funcion(pelicula, sala, inicio, precio);
        if (!pelicula.isActiva()) {
            throw new NegocioException("La película está desactivada.");
        }
        if (!estado.getPeliculas().contains(pelicula) || !estado.getSalas().contains(sala)) {
            throw new NegocioException("La película o la sala no existen.");
        }
        for (Funcion existente : repo.listar()) {
            if (existente.isActiva() && nueva.seSuperponeCon(existente)) {
                throw new NegocioException("La sala ya tiene una función entre las "
                        + existente.getInicio().toLocalTime() + " y "
                        + existente.getFin().toLocalTime() + ".");
            }
        }
        repo.agregar(nueva);
        try {
            almacenamiento.guardar(estado);
        } catch (AlmacenamientoException e) {
            repo.quitar(nueva);
            throw e;
        }
        return nueva;
    }

    public List<Funcion> listar() {
        return repo.listar();
    }

    public List<Funcion> cartelera(Pelicula pelicula, LocalDate fecha) {
        List<Funcion> resultado = new ArrayList<>();
        for (Funcion f : repo.listar()) {
            if (!f.isActiva() || f.haComenzado() || !f.getPelicula().isActiva()) {
                continue;
            }
            if (pelicula != null && !f.getPelicula().equals(pelicula)) {
                continue;
            }
            if (fecha != null && !f.getInicio().toLocalDate().equals(fecha)) {
                continue;
            }
            resultado.add(f);
        }
        resultado.sort(Comparator.comparing(Funcion::getInicio));
        return resultado;
    }
}