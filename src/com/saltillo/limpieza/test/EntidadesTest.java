
package com.saltillo.limpieza.test;

import com.saltillo.limpieza.models.entities.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

public class EntidadesTest {

    private static int aprobadas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {

        System.out.println(" PRUEBAS DE ENTIDADES - ISSUE #2");

        probarUsuario();
        probarEmpleado();
        probarCuadrilla();
        probarColonia();
        probarTipoActividad();
        probarEstadoActividad();
        probarEvidencia();
        probarActividad();

        System.out.println("PRUEBAS APROBADAS: " + aprobadas);
        System.out.println("PRUEBAS FALLIDAS: " + fallidas);
        if (fallidas > 0) {
            throw new AssertionError(
                "Existen pruebas de entidades fallidas."
            );
        }

        System.out.println("TODAS LAS PRUEBAS PASARON CON EXITO");
    }

    // Comprueba que una accion rechace datos invalidos
    private static void verificarRechazo(String nombre, Runnable accion) {
        try {
            accion.run();
            System.out.println("[ERROR] " + nombre);
            fallidas++;
        } catch (IllegalArgumentException e) {
            System.out.println("[EXITO] " + nombre);
            aprobadas++;
        } catch (Exception e) {
            System.out.println("[ERROR] " + nombre + ": " + e);
            fallidas++;
        }
    }

    // Comprueba que una condicion sea verdadera
    private static void verificar(String nombre, boolean condicion) {
        if (condicion) {
            System.out.println("[EXITO] " + nombre);
            aprobadas++;
        } else {
            System.out.println("[ERROR] " + nombre);
            fallidas++;
        }
    }

    // 1. USUARIO
    private static void probarUsuario() {
        System.out.println("\n[1] Usuario");

        Usuario usuario = new Usuario();

        verificarRechazo(
            "Rechazar nombre vacio",
            () -> usuario.setNombre("")
        );

        verificarRechazo(
            "Rechazar ID negativo",
            () -> usuario.setIdUsuario(-1)
        );

        usuario.setNombre("Carlos");

        verificar(
            "Aceptar nombre valido",
            "Carlos".equals(usuario.getNombre())
        );
    }

    // 2. EMPLEADO
    private static void probarEmpleado() {
        System.out.println("\n[2] Empleado");

        Empleado empleado = new Empleado();

        verificarRechazo(
            "Rechazar apellido paterno vacio",
            () -> empleado.setApellidoPaterno("")
        );

        verificarRechazo(
            "Rechazar ID negativo",
            () -> empleado.setIdEmpleado(-1)
        );

        empleado.setNombre("Ana");
        empleado.setApellidoPaterno("Lopez");

        verificar(
            "Generar nombre completo",
            "Ana Lopez".equals(empleado.getNombreCompleto())
        );
    }

    // 3. CUADRILLA
    private static void probarCuadrilla() {
        System.out.println("\n[3] Cuadrilla");

        Empleado jefe = new Empleado(
            1, "Ana", "Lopez", null,
            null, "Supervisor", true
        );

        Empleado integrante = new Empleado(
            2, "Luis", "Perez", null,
            null, "Operador", true
        );

        Cuadrilla cuadrilla = new Cuadrilla();

        cuadrilla.setNombre("Cuadrilla Centro");
        cuadrilla.agregarIntegrante(integrante);
        cuadrilla.setJefe(jefe);

        verificar(
            "Asignar jefe",
            cuadrilla.getJefe() == jefe
        );

        verificar(
            "Registrar integrantes",
            cuadrilla.getCantidadIntegrantes() == 2
        );

        verificarRechazo(
            "Rechazar integrante duplicado",
            () -> cuadrilla.agregarIntegrante(integrante)
        );
    }

    // 4. COLONIA
    private static void probarColonia() {
        System.out.println("\n[4] Colonia");

        Colonia colonia = new Colonia();

        verificarRechazo(
            "Rechazar codigo postal invalido",
            () -> colonia.setCodigoPostal("123")
        );

        colonia.setCodigoPostal("25000");

        verificar(
            "Aceptar codigo postal valido",
            "25000".equals(colonia.getCodigoPostal())
        );
    }

    // 5. TIPO DE ACTIVIDAD
    private static void probarTipoActividad() {
        System.out.println("\n[5] TipoActividad");

        TipoActividad tipo = new TipoActividad();

        verificarRechazo(
            "Rechazar nombre vacio",
            () -> tipo.setNombre("")
        );

        tipo.setNombre("Barrido");

        verificar(
            "Aceptar tipo valido",
            "Barrido".equals(tipo.getNombre())
        );
    }

    // 6. ESTADO DE ACTIVIDAD
    private static void probarEstadoActividad() {
        System.out.println("\n[6] EstadoActividad");

        EstadoActividad estado = new EstadoActividad();

        verificarRechazo(
            "Rechazar ID negativo",
            () -> estado.setIdEstadoActividad(-1)
        );

        estado.setNombreEstado("Programada");

        verificar(
            "Aceptar estado valido",
            "Programada".equals(estado.getNombreEstado())
        );
    }

    // 7. EVIDENCIA
    private static void probarEvidencia() {
        System.out.println("\n[7] Evidencia");

        Evidencia evidencia = new Evidencia();

        verificarRechazo(
            "Rechazar ruta vacia",
            () -> evidencia.setRutaImagen("")
        );

        evidencia.setRutaImagen("imagenes/foto.jpg");
        evidencia.setFechaCarga(LocalDateTime.now());

        verificar(
            "Registrar ruta y fecha",
            "imagenes/foto.jpg".equals(evidencia.getRutaImagen())
                && evidencia.getFechaCarga() != null
        );
    }

    // 8. ACTIVIDAD
    private static void probarActividad() {
        System.out.println("\n[8] Actividad");

        TipoActividad tipo = new TipoActividad(
            1, "Barrido", "Limpieza de calles"
        );

        EstadoActividad estado = new EstadoActividad(
            1, "Programada"
        );

        Cuadrilla cuadrilla = new Cuadrilla();
        cuadrilla.setIdCuadrilla(1);
        cuadrilla.setNombre("Cuadrilla Norte");
        cuadrilla.setActivo(true);

        Colonia colonia = new Colonia(
            1, "Centro", "25000", "Saltillo", "Coahuila"
        );

        Evidencia evidencia = new Evidencia(
            1, 1, "imagenes/limpieza.jpg",
            null, LocalDateTime.now()
        );

        Actividad actividad = new Actividad(
            1,
            tipo,
            estado,
            cuadrilla,
            "Limpieza de calles",
            "Barrido de espacios publicos",
            LocalDate.now(),
            LocalTime.of(8, 0),
            LocalTime.of(12, 0),
            "Plaza principal",
            null,
            Arrays.asList(colonia),
            Arrays.asList(evidencia)
        );

        verificar(
            "Relacion con cuadrilla",
            actividad.getCuadrilla() == cuadrilla
        );

        verificar(
            "Relacion con tipo y estado",
            actividad.getTipoActividad() == tipo
                && actividad.getEstadoActividad() == estado
        );

        verificar(
            "Relacion con colonia",
            actividad.getColonias().size() == 1
        );

        verificar(
            "Relacion con evidencia",
            actividad.getEvidencias().size() == 1
        );

        verificarRechazo(
            "Rechazar colonia duplicada",
            () -> actividad.agregarColonia(colonia)
        );

        verificarRechazo(
            "Rechazar hora final anterior al inicio",
            () -> actividad.setHoraFin(LocalTime.of(7, 0))
        );
    }
}
