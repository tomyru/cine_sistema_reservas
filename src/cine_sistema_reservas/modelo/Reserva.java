package cine_sistema_reservas.modelo;

import cine_sistema_reservas.excepciones.NegocioException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Reserva implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String codigo;
    private final Cliente cliente;
    private final Funcion funcion;
    private final List<Entrada> entradas;
    private EstadoReserva estado;
    private final LocalDateTime fechaCreacion;

    public Reserva(String codigo, Cliente cliente, Funcion funcion, List<Entrada> entradas) throws NegocioException {
        if (codigo == null || codigo.isBlank()) {
            throw new NegocioException("ERROR Reserva; El codigo no puede estar vacio.");
        }
        if (cliente == null) {
            throw new NegocioException("ERROR Reserva; Falta el cliente.");
        }
        if (funcion == null) {
            throw new NegocioException("ERROR Reserva; Falta la función.");
        }
        if (entradas == null || entradas.isEmpty()) {
            throw new NegocioException("ERROR Reserva; Debe tener al menos una entrada.");
        }
        Set<Butaca> vistas = new HashSet<>();
        for (Entrada e : entradas) {
            if (!funcion.getSala().getButacas().contains(e.getButaca())) {
                throw new NegocioException("ERROR Reserva; La butaca " + e.getButaca() + " no pertenece a la sala.");
            }
            if (!vistas.add(e.getButaca())) {
                throw new NegocioException("ERROR Reserva; La butaca " + e.getButaca() + " está repetida.");
            }
        }

        this.codigo = codigo;
        this.cliente = cliente;
        this.funcion = funcion;
        this.entradas = new ArrayList<>(entradas);
        this.estado = EstadoReserva.CONFIRMADA;
        this.fechaCreacion = LocalDateTime.now();
    }

    public String getCodigo() {
        return codigo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public List<Entrada> getEntradas() {
        return Collections.unmodifiableList(entradas);
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public double getTotal() {
        double total = 0;
        for (Entrada e : entradas) {
            total += e.getPrecioConfirmado();
        }
        return total;
    }

    public void cancelar() throws NegocioException {
        if (estado == EstadoReserva.CANCELADA) {
            throw new NegocioException("ERROR Reserva; La reserva ya está cancelada.");
        }
        this.estado = EstadoReserva.CANCELADA;
    }

    public boolean ocupa(Butaca b) {
        if (estado != EstadoReserva.CONFIRMADA) {
            return false;
        }
        for (Entrada e : entradas) {
            if (e.getButaca().equals(b)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Solo para el rollback de ReservaService: deja la reserva CONFIRMADA si
     * falla el guardado de una cancelación. No es una operación de negocio.
     */
    public void restaurarConfirmada() {
        this.estado = EstadoReserva.CONFIRMADA;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return Objects.equals(this.codigo, ((Reserva) obj).codigo);
    }

    @Override
    public String toString() {
        return codigo + " - " + cliente + " - " + estado;
    }
}