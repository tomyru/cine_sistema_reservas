package cine_sistema_reservas.modelo;

import java.io.Serializable;

public interface TipoEntrada extends Serializable {

    double calcularPrecio(double precioBase);

    String getNombre();

}
