package cine_sistema_reservas.persistencia;

import cine_sistema_reservas.excepciones.AlmacenamientoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AlmacenamientoArchivo {

    private final File archivo;

    public AlmacenamientoArchivo(File archivo) {
        this.archivo = archivo;
    }

    public void guardar(EstadoCine estado) throws AlmacenamientoException {
        File carpeta = archivo.getParentFile();
        if (carpeta != null) {
            carpeta.mkdirs();
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(archivo))) {
            out.writeObject(estado);
        } catch (IOException e) {
            throw new AlmacenamientoException("ERROR AlmacenamientoArchivo; No se pudo guardar el archivo de datos", e);
        }
    }

    public EstadoCine cargar() throws AlmacenamientoException {
        if (!archivo.exists()) {
            return new EstadoCine();   // primer uso: estado inicial válido
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            return (EstadoCine) in.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new AlmacenamientoException("ERROR AlmacenamientoArchivo; No se pudo leer el archivo de datos", e);
        }
    }

}
