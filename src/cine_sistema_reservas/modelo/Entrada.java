package cine_sistema_reservas.modelo;

import cine_sistema_reservas.excepciones.NegocioException;
import java.io.Serializable;

public class Entrada implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Butaca butaca;
    private final TipoEntrada tipo;
    private final double precioConfirmado;

    public Entrada(Butaca butaca, TipoEntrada tipo, double precioBase) throws NegocioException {
        if (butaca == null) {
            throw new NegocioException("ERROR Entrada; Debe indicar una butaca.");
        }
        if (tipo == null) {
            throw new NegocioException("ERROR Entrada; Debe seleccionar un tipo de entrada.");
        }
        if (precioBase <= 0) {
            throw new NegocioException("ERROR Entrada; El precio base debe ser mayor a 0.");
        }
        this.butaca = butaca;
        this.tipo = tipo;
        this.precioConfirmado = tipo.calcularPrecio(precioBase);
    }

    public Butaca getButaca() {
        return butaca;
    }

    public TipoEntrada getTipo() {
        return tipo;
    }

    public double getPrecioConfirmado() {
        return precioConfirmado;
    }

    @Override
    public String toString() {
        return butaca + " - " + tipo.getNombre() + " - $" + precioConfirmado;
    }

}
