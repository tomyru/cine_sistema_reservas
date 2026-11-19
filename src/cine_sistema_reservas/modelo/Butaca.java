package cine_sistema_reservas.modelo;

import cine_sistema_reservas.excepciones.NegocioException;
import java.io.Serializable;

public class Butaca implements Serializable {

    private static final long serialVersionUID = 1L;

    private final char fila;
    private final int numero;

    public Butaca(char fila, int numero) throws NegocioException {
        if (!Character.isLetter(fila)) {
            throw new NegocioException("ERROR Butaca; La fila debe ser una letra");
        }
        if (numero < 1) {
            throw new NegocioException("ERROR Butaca; El número debe ser mayor o igual a 1");
        }
        this.fila = Character.toUpperCase(fila);
        this.numero = numero;
    }

    public char getFila() {
        return fila;
    }

    public int getNumero() {
        return numero;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.fila;
        hash = 37 * hash + this.numero;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Butaca other = (Butaca) obj;
        if (this.fila != other.fila) {
            return false;
        }
        return this.numero == other.numero;
    }

    @Override
    public String toString() {
        return "" + fila + numero;
    }

}
