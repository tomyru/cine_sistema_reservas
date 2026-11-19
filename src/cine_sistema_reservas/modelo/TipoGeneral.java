package cine_sistema_reservas.modelo;

public class TipoGeneral implements TipoEntrada {

    @Override
    public double calcularPrecio(double precioBase) {
        return precioBase;
    }

    @Override
    public String getNombre() {
        return "General";
    }

    @Override
    public String toString() {
        return getNombre();
    }

}
