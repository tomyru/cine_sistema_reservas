package cine_sistema_reservas.modelo;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public abstract class EntidadConId implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;

    protected EntidadConId() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        EntidadConId otra = (EntidadConId) obj;
        return Objects.equals(id, otra.id);
    }
}