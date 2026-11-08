package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.models.dto.*;
import com.saltillo.limpieza.patterns.observer.ActividadEventBus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * =============================================================================
 * IMPLEMENTACIÓN MOCK EN MEMORIA (INTEGRANTE 4)
 * =============================================================================
 * Cumplimiento de Contrato Issue #4:
 * - Implementa 'buscarActividades(FiltroActividadDTO)' con filtrado dinámico en memoria.
 * - Soporta actividades con múltiples colonias (Requisito 8 rúbrica).
 * - Emite eventos tipificados (CREADA) mediante el ActividadEventBus.
 * 
 * GUÍA DE INTEGRACIÓN PARA XIMENA (Issue #17: SqlDashboardRepository):
 * Al implementar la versión MySQL real, sustituir la lógica de filtrado en memoria
 * por consultas preparadas con cláusula WHERE dinámica basada en FiltroActividadDTO.
 * =============================================================================
 */
public class MockDashboardRepository implements IDashboardRepository {

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
    private final List<ActividadDetalleDTO> actividadesSimuladas = new ArrayList<>();
    private final AtomicInteger generadorIdActividad = new AtomicInteger(100);

    public MockDashboardRepository() {
        cargarDatosSemillaSaltillo();
    }

    private void cargarDatosSemillaSaltillo() {
        cuadrillas.put(1, new CuadrillaMock(1, "Cuadrilla Centro Histórico", "Arq. Elena Fuentes", "Zona Centro / Alameda", 42, 50, "EN OPERACION"));
        cuadrillas.put(2, new CuadrillaMock(2, "Cuadrilla Venustiano Carranza", "Ing. Carlos Mendoza", "Zona Norte / Corredor V. Carranza", 38, 45, "EN OPERACION"));
        cuadrillas.put(3, new CuadrillaMock(3, "Cuadrilla Mirasierra / Oriente", "Téc. Pedro Morales", "Zona Oriente / Mirasierra - Fundadores", 29, 40, "DISPONIBLE"));
        cuadrillas.put(4, new CuadrillaMock(4, "Cuadrilla República / Nazario", "Lic. Roberto Dávila", "Zona Poniente / República - San Patricio", 35, 45, "EN OPERACION"));
        cuadrillas.put(5, new CuadrillaMock(5, "Cuadrilla Ramos / Zona Industrial", "Ing. Claudia Salazar", "Límites Ramos Arizpe / Isidro López", 24, 35, "DISPONIBLE"));
        cuadrillas.put(6, new CuadrillaMock(6, "Cuadrilla Saltillo 2000", "Téc. Gabriel Treviño", "Zona Sur-Poniente / Saltillo 2000", 31, 40, "EN OPERACION"));

        // Carga de actividades históricas simuladas para búsqueda y filtros
        LocalDate hoy = LocalDate.now();
        agregarActividadSemilla(1, List.of("Centro Histórico", "Bellavista"), "Barrido Manual y Desazolve", "Plaza de Armas y calle Victoria", "COMPLETADA", hoy.atTime(8, 30), "Retiro de 1.2 toneladas de basura.");
        agregarActividadSemilla(2, List.of("República Poniente"), "Deshierbe y Poda de Camellones", "Blvd. V. Carranza y Canadá", "COMPLETADA", hoy.atTime(9, 15), "Poda y perfilado de áreas verdes.");
        agregarActividadSemilla(3, List.of("Mirasierra", "Zaragoza"), "Recolección de Escombros y Enseres", "Entronque Fundadores", "EN PROCESO", hoy.atTime(10, 0), "Apoyo con camión de volteo.");
        agregarActividadSemilla(4, List.of("San Patricio"), "Lavado de Espacios Públicos", "Parque los Pinos", "COMPLETADA", hoy.minusDays(1).atTime(11, 20), "Desinfección con hidrolavadora.");
        agregarActividadSemilla(5, List.of("Zona Industrial"), "Limpieza de Canales Pluviales", "Arroyo del Pueblo / Isidro López", "COMPLETADA", hoy.minusDays(2).atTime(7, 45), "Desazolve preventivo.");
        agregarActividadSemilla(6, List.of("Saltillo 2000"), "Barrido Manual y Desazolve", "Av. Javier García", "EN PROCESO", hoy.minusDays(3).atTime(12, 10), "Cuadrilla operando en sector sur.");
        agregarActividadSemilla(1, List.of("Centro Histórico"), "Lavado de Espacios Públicos", "Alameda Zaragoza", "COMPLETADA", hoy.minusDays(4).atTime(6, 0), "Lavado matutino de andadores.");
        agregarActividadSemilla(2, List.of("Doctores", "Cumbres"), "Deshierbe y Poda de Camellones", "Blvd. Nazario Ortiz Garza", "COMPLETADA", hoy.minusDays(5).atTime(14, 0), "Mantenimiento general de camellón.");
    }

    private void agregarActividadSemilla(int idCuadrilla, List<String> colonias, String tipo, String lugar, String estado, LocalDateTime fecha, String obs) {
        CuadrillaMock c = cuadrillas.get(idCuadrilla);
        String nombreC = (c != null) ? c.nombre : "Cuadrilla #" + idCuadrilla;
        actividadesSimuladas.add(new ActividadDetalleDTO(
                generadorIdActividad.incrementAndGet(),
                idCuadrilla,
                nombreC,
                colonias,
                tipo,
                lugar,
                estado,
                fecha,
                obs
        ));
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

    /**
     * Búsqueda y filtrado dinámico en memoria según FiltroActividadDTO.
     */
    @Override
    public synchronized List<ActividadDetalleDTO> buscarActividades(FiltroActividadDTO filtro) {
        if (filtro == null) {
            return Collections.unmodifiableList(new ArrayList<>(actividadesSimuladas));
        }

        List<ActividadDetalleDTO> resultados = new ArrayList<>();

        for (ActividadDetalleDTO act : actividadesSimuladas) {
            // Filtro por cuadrilla
            if (filtro.tieneFiltroCuadrilla() && act.getIdCuadrilla() != filtro.getIdCuadrilla()) {
                continue;
            }

            // Filtro por colonia (coincidencia en cualquiera de las colonias asignadas)
            if (filtro.tieneFiltroColonia()) {
                String colBuscada = filtro.getColonia().toLowerCase();
                boolean coincideColonia = act.getColonias().stream()
                        .anyMatch(c -> c.toLowerCase().contains(colBuscada));
                if (!coincideColonia) {
                    continue;
                }
            }

            // Filtro por estado ("COMPLETADA", "EN PROCESO", etc.)
            if (filtro.tieneFiltroEstado() && !act.getEstado().equalsIgnoreCase(filtro.getEstado())) {
                continue;
            }

            // Filtro por rango de fechas
            LocalDate fechaAct = act.getFechaHora().toLocalDate();
            if (filtro.tieneFiltroFechaInicio() && fechaAct.isBefore(filtro.getFechaInicio())) {
                continue;
            }
            if (filtro.tieneFiltroFechaFin() && fechaAct.isAfter(filtro.getFechaFin())) {
                continue;
            }

            resultados.add(act);
        }

        return Collections.unmodifiableList(resultados);
    }

    @Override
    public synchronized void registrarActividad(int idCuadrilla, List<String> colonias, String tipoLimpieza, String observaciones) {
        CuadrillaMock c = cuadrillas.get(idCuadrilla);
        if (c != null) {
            c.actividadesRealizadas++;
            c.estado = "EN OPERACION";

            // Guardar en la lista interna para consultas y filtros (Issue #16)
            int nuevoId = generadorIdActividad.incrementAndGet();
            String nombreCuadrilla = c.nombre;
            String lugar = (colonias != null && !colonias.isEmpty()) ? colonias.get(0) : "Saltillo";

            actividadesSimuladas.add(0, new ActividadDetalleDTO(
                    nuevoId,
                    idCuadrilla,
                    nombreCuadrilla,
                    colonias,
                    tipoLimpieza,
                    lugar,
                    "COMPLETADA",
                    LocalDateTime.now(),
                    observaciones
            ));

            // Emitir evento tipificado mediante el Patrón Observer
            ActividadEventDTO evento = new ActividadEventDTO(
                    c.id,
                    c.nombre,
                    colonias,
                    tipoLimpieza,
                    observaciones,
                    TipoEventoActividad.CREADA
            );
            ActividadEventBus.getInstance().publicarEvento(evento);
        }
    }

    @Override
    public synchronized void registrarActividad(int idCuadrilla, String colonia, String tipoLimpieza, String observaciones) {
        List<String> colonias = (colonia != null && !colonia.isBlank()) ? List.of(colonia) : List.of();
        registrarActividad(idCuadrilla, colonias, tipoLimpieza, observaciones);
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
