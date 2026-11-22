package cine_sistema_reservas.servicios;

import cine_sistema_reservas.excepciones.AlmacenamientoException;
import cine_sistema_reservas.excepciones.NegocioException;
import cine_sistema_reservas.modelo.Sala;
import cine_sistema_reservas.persistencia.AlmacenamientoArchivo;
import cine_sistema_reservas.persistencia.EstadoCine;
import cine_sistema_reservas.persistencia.Repositorio;
import java.util.List;

public class SalaService {

    private final EstadoCine estado;
    private final AlmacenamientoArchivo almacenamiento;
    private final Repositorio<Sala> repo;

    public SalaService(EstadoCine estado, AlmacenamientoArchivo almacenamiento) {
        this.estado = estado;
        this.almacenamiento = almacenamiento;
        this.repo = new Repositorio<>(estado.getSalas());
    }

    public Sala registrar(String nombre, int filas, int butacasPorFila)
            throws NegocioException, AlmacenamientoException {
        Sala nueva = new Sala(nombre, filas, butacasPorFila);
        for (Sala sala : repo.listar()) {
            if (sala.getNombre().equalsIgnoreCase(nueva.getNombre())) {
                throw new NegocioException("Ya existe una sala con ese nombre.");
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

    public List<Sala> listar() {
        return repo.listar();
    }
}