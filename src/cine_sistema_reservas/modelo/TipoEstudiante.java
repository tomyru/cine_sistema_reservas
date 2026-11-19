package cine_sistema_reservas.modelo;

public class TipoEstudiante implements TipoEntrada {

    private static final long serialVersionUID = 1L;

    private static final double DESCUENTO = 0.20;

    @Override
    public double calcularPrecio(double precioBase) {
        return precioBase - (precioBase * DESCUENTO);
    }

    @Override
    public String getNombre() {
        return "Estudiante";
    }

    @Override
    public String toString() {
        return getNombre();
    }

}
