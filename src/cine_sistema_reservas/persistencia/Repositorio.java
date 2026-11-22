package cine_sistema_reservas.persistencia;

import cine_sistema_reservas.modelo.EntidadConId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Repositorio<T extends EntidadConId> {

    private final List<T> elementos;

    public Repositorio(List<T> elementos) {
        this.elementos = elementos;
    }

    public void agregar(T elemento) {
        elementos.add(elemento);
    }

    public void quitar(T elemento) {
        elementos.remove(elemento);
    }

    public Optional<T> buscarPorId(String id) {
        for (T e : elementos) {
            if (e.getId().equals(id)) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }

    public List<T> listar() {
        return Collections.unmodifiableList(elementos);
    }
}