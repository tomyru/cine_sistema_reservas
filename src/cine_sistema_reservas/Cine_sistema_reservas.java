package cine_sistema_reservas;

import cine_sistema_reservas.excepciones.NegocioException;
import cine_sistema_reservas.modelo.*;
import java.time.LocalDateTime;

public class Cine_sistema_reservas {

    public static void main(String[] args) throws NegocioException {



//        Cliente jose = new Cliente("josesito", "44451874");
//        System.out.println(jose);


        Sala salauno = new Sala("sala B5", 5, 5);
        System.out.println(salauno);
        System.out.println(salauno.getCapacidad());
        

        Pelicula interestelar = new Pelicula("Interestelar", 120);
        System.out.println(interestelar);
        

        
        
        
        Butaca butaca = new Butaca('f', 7);
        System.out.println(butaca);
        
        TipoEntrada tipoEstudiante = new TipoEstudiante();
        TipoEntrada tipoGeneral = new TipoGeneral();
        
        Funcion funcionprueba = new Funcion(
                interestelar,
                salauno,
                LocalDateTime.of(2026, 12, 17, 16, 30),
                12000.50);
        System.out.println(funcionprueba);
        
        
        
        
        Entrada entrada = new Entrada(
                butaca,
                tipoEstudiante,
                funcionprueba.getPrecio());
        
        System.out.println(entrada);
        
        
                

        
        
        
        
        
        
        
        
        

    }
}
