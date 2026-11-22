package cine_sistema_reservas;

import cine_sistema_reservas.excepciones.*;
import cine_sistema_reservas.modelo.*;
import cine_sistema_reservas.persistencia.*;
import cine_sistema_reservas.servicios.*;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

public class PruebaServicios {

    private static void check(String nombre, boolean condicion) {
        System.out.println((condicion ? "[OK]    " : "[FALLO] ") + nombre);
    }

    public static void main(String[] args) throws Exception {
        File archivo = new File(Files.createTempDirectory("cine_test").toFile(), "cine.dat");
        AlmacenamientoArchivo alm = new AlmacenamientoArchivo(archivo);
        EstadoCine estado = alm.cargar();

        PeliculaService peliculas = new PeliculaService(estado, alm);
        SalaService salas = new SalaService(estado, alm);
        FuncionService funciones = new FuncionService(estado, alm);
        ReservaService reservas = new ReservaService(estado, alm);

        Pelicula pA = peliculas.registrar("Pelicula A", 100);
        Pelicula pB = peliculas.registrar("Pelicula B", 60);
        Sala sala = salas.registrar("Sala 1", 5, 10);

        LocalDateTime base = LocalDateTime.now().plusDays(1)
                .withHour(18).withMinute(0).withSecond(0).withNano(0);
        Funcion fA = funciones.registrar(pA, sala, base, 1000);                 // 18:00-19:40
        Funcion fB = funciones.registrar(pB, sala, base.plusMinutes(110), 1000); // 19:50-20:50
        Funcion fC = funciones.registrar(pB, sala, base.plusHours(4), 1000);     // 22:00-23:00

        // --- Superposición al registrar
        try {
            funciones.registrar(pB, sala, base.plusMinutes(30), 1000);
            check("Rechaza funcion superpuesta", false);
        } catch (NegocioException e) {
            check("Rechaza funcion superpuesta", true);
        }

        // --- Modificar pelicula
        try {
            peliculas.modificar(pA, "Nuevo titulo", 130);   // terminaria 20:10, pisa a fB
            check("Modificar: rechaza duracion que superpone", false);
        } catch (NegocioException e) {
            check("Modificar: rechaza duracion que superpone", true);
        }
        check("Modificar: restaura duracion", pA.getDuracionMinutos() == 100);
        check("Modificar: restaura titulo", pA.getTitulo().equals("Pelicula A"));
        peliculas.modificar(pA, "Pelicula A2", 105);        // termina 19:45, ok
        check("Modificar valido", pA.getDuracionMinutos() == 105 && pA.getTitulo().equals("Pelicula A2"));

        // --- Reserva de varias butacas + polimorfismo
        List<Butaca> b = sala.getButacas();
        Cliente cli = new Cliente("Ana", "12345678");
        Reserva r1 = reservas.confirmar(fA, cli,
                List.of(b.get(0), b.get(1)),
                List.of(new TipoGeneral(), new TipoEstudiante()));
        check("Codigo CIN-001", r1.getCodigo().equals("CIN-001"));
        check("Total 1000 + 800 = 1800", Math.abs(r1.getTotal() - 1800) < 0.001);

        // --- Butaca ocupada
        try {
            reservas.confirmar(fA, cli, List.of(b.get(1)), List.of(new TipoGeneral()));
            check("Rechaza butaca ocupada", false);
        } catch (NegocioException e) {
            check("Rechaza butaca ocupada", true);
        }

        // --- Misma ubicacion en otra funcion
        Reserva r2 = reservas.confirmar(fC, cli, List.of(b.get(0)), List.of(new TipoGeneral()));
        check("Misma butaca en otra funcion", r2.getCodigo().equals("CIN-002"));

        // --- Precio congelado
        // (si tu Funcion no tiene setPrecio, borrá estas 2 lineas)
        // fA.setPrecio(5000);
        // check("Precio de reserva no cambia", Math.abs(r1.getTotal() - 1800) < 0.001);
        // --- Cancelar y reutilizar
        reservas.cancelar(r1);
        check("Reserva queda CANCELADA", r1.getEstado() == EstadoReserva.CANCELADA);
        check("Butaca liberada", !reservas.butacasOcupadas(fA).contains(b.get(0)));
        Reserva r3 = reservas.confirmar(fA, cli, List.of(b.get(0)), List.of(new TipoGeneral()));
        check("Butaca reutilizada tras cancelar", r3 != null);
        try {
            reservas.cancelar(r1);
            check("No cancela dos veces", false);
        } catch (NegocioException e) {
            check("No cancela dos veces", true);
        }

        // --- Busquedas
        check("Busca por codigo (minusculas)", reservas.buscarPorCodigo("cin-001") == r1);
        check("Busca por identificacion: 3 reservas", reservas.buscarPorIdentificacion("12345678").size() == 3);
        check("Identificacion sin reservas: lista vacia", reservas.buscarPorIdentificacion("999").isEmpty());
        try {
            reservas.buscarPorCodigo("CIN-999");
            check("Codigo inexistente lanza error", false);
        } catch (NegocioException e) {
            check("Codigo inexistente lanza error", true);
        }

        // --- Desactivar / reactivar
        peliculas.desactivar(pB);
        check("Desactivar pelicula", !pB.isActiva());
        peliculas.desactivar(pB);   // segunda vez: no debe fallar
        check("Desactivar dos veces no falla", !pB.isActiva());
        peliculas.reactivar(pB);
        check("Reactivar pelicula", pB.isActiva());

        check("Cartelera sin filtro: 3 funciones", funciones.cartelera(null, null).size() == 3);
        check("Cartelera por pelicula B: 2", funciones.cartelera(pB, null).size() == 2);
        check("Cartelera por fecha de fA: 3", funciones.cartelera(null, base.toLocalDate()).size() == 3);
        check("Cartelera ordenada", funciones.cartelera(null, null).get(0) == fA);
        peliculas.desactivar(pB);
        check("Pelicula desactivada sale de cartelera", funciones.cartelera(null, null).size() == 1);
        try {
            reservas.confirmar(fC, cli, List.of(b.get(5)), List.of(new TipoGeneral()));
            check("No reserva con pelicula desactivada", false);
        } catch (NegocioException e) {
            check("No reserva con pelicula desactivada", true);
        }
        peliculas.reactivar(pB);

        // --- Recuperacion tras reiniciar
        AlmacenamientoArchivo alm2 = new AlmacenamientoArchivo(archivo);
        EstadoCine recuperado = alm2.cargar();
        check("Recupera peliculas", recuperado.getPeliculas().size() == 2);
        check("Recupera salas", recuperado.getSalas().size() == 1);
        check("Recupera funciones", recuperado.getFunciones().size() == 3);
        check("Recupera reservas", recuperado.getReservas().size() == 3);
        check("Contador sigue: CIN-004", recuperado.siguienteCodigoReserva().equals("CIN-004"));
        File csv = new File(archivo.getParentFile(), "reservas.csv");
        reservas.exportarCSV(csv);
        check("CSV creado", csv.exists());
        check("CSV: encabezado + 3 reservas", Files.readAllLines(csv.toPath()).size() == 4);

    }
}
