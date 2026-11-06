package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.models.dto.ActividadEventDTO;
import com.saltillo.limpieza.models.dto.CuadrillaResumenDTO;
import com.saltillo.limpieza.models.dto.DashboardKpisDTO;
import com.saltillo.limpieza.patterns.observer.ActividadEventBus;

import java.time.LocalDateTime;
import java.util.*;

/**
 * =============================================================================
 * IMPLEMENTACIÓN MOCK EN MEMORIA (INTEGRANTE 4)
 * =============================================================================
 * Esta clase simula el comportamiento de la base de datos relacional para
 * permitir que el Dashboard Ejecutivo y el Menú Principal funcionen de inmediato,
 * sin bloquear el desarrollo a la espera del Integrante 1 y 2.
 * 
 * GUÍA DE INTEGRACIÓN PARA INTEGRANTES 1 Y 2:
 * 1. El Integrante 1 terminará la conexión Singleton a MySQL/PostgreSQL.
 * 2. El Integrante 2 creará los DAOs reales.
 * 3. Para conectar la BD real, crear 'SqlDashboardRepository' implementando
 *    'IDashboardRepository' con consultas SQL equivalentes a:
 *      - SELECT COUNT(*) FROM actividades;
 *      - SELECT cuadrilla_id, COUNT(*) FROM actividades GROUP BY cuadrilla_id;
 * 4. En 'Main.java', cambiar:
 *      IDashboardRepository repo = new MockDashboardRepository();
 *    por:
 *      IDashboardRepository repo = new SqlDashboardRepository();
 * =============================================================================
 */
public class MockDashboardRepository implements IDashboardRepository {

    // Clase interna para el estado mutable simulado en memoria
    private static class CuadrillaMock {
        int id;
        String nombre;
        String jefe;
        String zona;
        int actividadesRealizadas;
        int metaMensual;
        String estado;

        CuadrillaMock(int id, String nombre, String jefe, String zona, int actividades, int meta, String estado) {
            this.id = id;
            this.nombre = nombre;
            this.jefe = jefe;
            this.zona = zona;
            this.actividadesRealizadas = actividades;
            this.metaMensual = meta;
            this.estado = estado;
        }
    }

    private final Map<Integer, CuadrillaMock> cuadrillas = new LinkedHashMap<>();

    public MockDashboardRepository() {
        cargarDatosSemillaSaltillo();
    }

    /**
     * Carga de datos realistas para el Municipio de Saltillo, Coahuila.
     */
    private void cargarDatosSemillaSaltillo() {
        cuadrillas.put(1, new CuadrillaMock(1, "Cuadrilla Centro Histórico", "Arq. Elena Fuentes", "Zona Centro / Alameda", 42, 50, "EN OPERACION"));
        cuadrillas.put(2, new CuadrillaMock(2, "Cuadrilla Venustiano Carranza", "Ing. Carlos Mendoza", "Zona Norte / Corredor V. Carranza", 38, 45, "EN OPERACION"));
        cuadrillas.put(3, new CuadrillaMock(3, "Cuadrilla Mirasierra / Oriente", "Téc. Pedro Morales", "Zona Oriente / Mirasierra - Fundadores", 29, 40, "DISPONIBLE"));
        cuadrillas.put(4, new CuadrillaMock(4, "Cuadrilla República / Nazario", "Lic. Roberto Dávila", "Zona Poniente / República - San Patricio", 35, 45, "EN OPERACION"));
        cuadrillas.put(5, new CuadrillaMock(5, "Cuadrilla Ramos / Zona Industrial", "Ing. Claudia Salazar", "Límites Ramos Arizpe / Isidro López", 24, 35, "DISPONIBLE"));
        cuadrillas.put(6, new CuadrillaMock(6, "Cuadrilla Saltillo 2000", "Téc. Gabriel Treviño", "Zona Sur-Poniente / Saltillo 2000", 31, 40, "EN OPERACION"));
    }

    @Override
    public synchronized DashboardKpisDTO obtenerKpis() {
        int totalActividades = 0;
        int cuadrillasDisponibles = 0;
        String cuadrillaLider = "Ninguna";
        int maxActividades = -1;

        for (CuadrillaMock c : cuadrillas.values()) {
            totalActividades += c.actividadesRealizadas;
            if ("DISPONIBLE".equalsIgnoreCase(c.estado)) {
                cuadrillasDisponibles++;
            }
            if (c.actividadesRealizadas > maxActividades) {
                maxActividades = c.actividadesRealizadas;
                cuadrillaLider = c.nombre;
            }
        }

        int totalCuadrillas = cuadrillas.size();
        double promedio = totalCuadrillas > 0 ? (double) totalActividades / totalCuadrillas : 0.0;

        return new DashboardKpisDTO(
                totalActividades,
                cuadrillaLider,
                maxActividades,
                cuadrillasDisponibles,
                totalCuadrillas,
                promedio,
                LocalDateTime.now()
        );
    }

    @Override
    public synchronized List<CuadrillaResumenDTO> obtenerResumenPorCuadrillas() {
        List<CuadrillaResumenDTO> lista = new ArrayList<>();
        for (CuadrillaMock c : cuadrillas.values()) {
            lista.add(new CuadrillaResumenDTO(
                    c.id,
                    c.nombre,
                    c.jefe,
                    c.zona,
                    c.actividadesRealizadas,
                    c.metaMensual,
                    c.estado
            ));
        }
        return Collections.unmodifiableList(lista);
    }

    @Override
    public synchronized void registrarActividad(int idCuadrilla, String colonia, String tipoLimpieza, String observaciones) {
        CuadrillaMock c = cuadrillas.get(idCuadrilla);
        if (c != null) {
            c.actividadesRealizadas++;
            // Si estaba disponible, al asignarle actividad pasa a EN OPERACIÓN
            c.estado = "EN OPERACION";

            // Emitir evento mediante el Patrón Observer
            ActividadEventDTO evento = new ActividadEventDTO(
                    c.id,
                    c.nombre,
                    colonia,
                    tipoLimpieza,
                    observaciones
            );
            ActividadEventBus.getInstance().publicarNuevaActividad(evento);
        }
    }

    @Override
    public synchronized List<String> obtenerNombresCuadrillas() {
        List<String> nombres = new ArrayList<>();
        for (CuadrillaMock c : cuadrillas.values()) {
            nombres.add(c.nombre);
        }
        return nombres;
    }

    @Override
    public synchronized int obtenerIdPorNombre(String nombre) {
        for (CuadrillaMock c : cuadrillas.values()) {
            if (c.nombre.equalsIgnoreCase(nombre)) {
                return c.id;
            }
        }
        return 1;
    }
}
