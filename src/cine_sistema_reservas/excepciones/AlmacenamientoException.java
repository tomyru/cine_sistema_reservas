package cine_sistema_reservas.excepciones;

public class AlmacenamientoException extends Exception {

    public AlmacenamientoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

}
