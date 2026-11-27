package cine_sistema_reservas.servicios;

import cine_sistema_reservas.excepciones.AlmacenamientoException;
import cine_sistema_reservas.excepciones.NegocioException;
import cine_sistema_reservas.modelo.Butaca;
import cine_sistema_reservas.modelo.Cliente;
import cine_sistema_reservas.modelo.Entrada;
import cine_sistema_reservas.modelo.Funcion;
import cine_sistema_reservas.modelo.Reserva;
import cine_sistema_reservas.modelo.TipoEntrada;
import cine_sistema_reservas.persistencia.AlmacenamientoArchivo;
import cine_sistema_reservas.persistencia.EstadoCine;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReservaService {

    private final EstadoCine estado;
    private final AlmacenamientoArchivo almacenamiento;

    public ReservaService(EstadoCine estado, AlmacenamientoArchivo almacenamiento) {
        this.estado = estado;
        this.almacenamiento = almacenamiento;
    }

    public Set<Butaca> butacasOcupadas(Funcion funcion) {
        Set<Butaca> butacas = new HashSet<>();
        for (Reserva r : estado.getReservas()) {
            if (!r.getFuncion().equals(funcion)) {
                continue;
            }
            for (Butaca b : funcion.getSala().getButacas()) {
                if (r.ocupa(b)) {
                    butacas.add(b);
                }
            }
        }
        return butacas;
    }

    public Reserva confirmar(Funcion funcion, Cliente cliente,
            List<Butaca> butacas, List<TipoEntrada> tipos)
            throws NegocioException, AlmacenamientoException {
        if (funcion == null || cliente == null || butacas == null || tipos == null) {
            throw new NegocioException("Faltan datos para confirmar la reserva.");
        }
        if (butacas.size() != tipos.size()) {
            throw new NegocioException("Datos de entradas incompletos.");
        }
        if (!estado.getFunciones().contains(funcion)) {
            throw new NegocioException("La función no existe.");
        }
        if (!funcion.isActiva()) {
            throw new NegocioException("La función no está activa.");
        }
        if (!funcion.getPelicula().isActiva()) {
            throw new NegocioException("La película de esta función está desactivada.");
        }
        if (funcion.haComenzado()) {
            throw new NegocioException("La función ya comenzó.");
        }
        Set<Butaca> ocupadas = butacasOcupadas(funcion);
        for (Butaca b : butacas) {
            if (ocupadas.contains(b)) {
                throw new NegocioException("La butaca " + b + " ya está ocupada.");
            }
        }
        List<Entrada> entradas = new ArrayList<>();
        for (int i = 0; i < butacas.size(); i++) {
            entradas.add(new Entrada(butacas.get(i), tipos.get(i), funcion.getPrecio()));
        }
        String codigo = estado.siguienteCodigoReserva();
        Reserva nueva;
        try {
            nueva = new Reserva(codigo, cliente, funcion, entradas);
        } catch (NegocioException e) {
            estado.devolverCodigo();
            throw e;
        }
        estado.getReservas().add(nueva);
        try {
            almacenamiento.guardar(estado);
        } catch (AlmacenamientoException e) {
            estado.getReservas().remove(nueva);
            estado.devolverCodigo();
            throw e;
        }
        return nueva;
    }

    public void cancelar(Reserva reserva) throws NegocioException, AlmacenamientoException {
        if (reserva == null || !estado.getReservas().contains(reserva)) {
            throw new NegocioException("La reserva no existe.");
        }
        if (reserva.getFuncion().haComenzado()) {
            throw new NegocioException("La función ya comenzó, no se puede cancelar la reserva.");
        }
        reserva.cancelar();
        try {
            almacenamiento.guardar(estado);
        } catch (AlmacenamientoException e) {
            reserva.restaurarConfirmada();
            throw e;
        }
    }

    public Reserva buscarPorCodigo(String codigo) throws NegocioException {
        if (codigo == null || codigo.isBlank()) {
            throw new NegocioException("Ingresá un código de reserva.");
        }
        String buscado = codigo.trim();
        for (Reserva r : estado.getReservas()) {
            if (r.getCodigo().equalsIgnoreCase(buscado)) {
                return r;
            }
        }
        throw new NegocioException("No existe una reserva con el código " + buscado + ".");
    }

    public List<Reserva> buscarPorIdentificacion(String identificacion) throws NegocioException {
        if (identificacion == null || identificacion.isBlank()) {
            throw new NegocioException("Ingresá la identificación del cliente.");
        }
        String buscada = identificacion.trim();
        List<Reserva> encontradas = new ArrayList<>();
        for (Reserva r : estado.getReservas()) {
            if (r.getCliente().getIdentificacion().equals(buscada)) {
                encontradas.add(r);
            }
        }
        return encontradas;
    }

    public void exportarCSV(File destino) throws AlmacenamientoException {
        try (PrintWriter out = new PrintWriter(destino, StandardCharsets.UTF_8)) {
            out.println("codigo;cliente;identificacion;pelicula;sala;inicio;butacas;total;estado");
            for (Reserva r : estado.getReservas()) {
                StringBuilder butacas = new StringBuilder();
                for (Entrada e : r.getEntradas()) {
                    if (butacas.length() > 0) {
                        butacas.append(" ");
                    }
                    butacas.append(e.getButaca());
                }
                out.println(r.getCodigo() + ";"
                        + r.getCliente().getNombre() + ";"
                        + r.getCliente().getIdentificacion() + ";"
                        + r.getFuncion().getPelicula().getTitulo() + ";"
                        + r.getFuncion().getSala().getNombre() + ";"
                        + r.getFuncion().getInicio() + ";"
                        + butacas + ";"
                        + r.getTotal() + ";"
                        + r.getEstado());
            }
        } catch (IOException e) {
            throw new AlmacenamientoException("No se pudo exportar el archivo CSV.", e);
        }
    }

    public List<Reserva> listar() {
        return Collections.unmodifiableList(estado.getReservas());
    }
}
