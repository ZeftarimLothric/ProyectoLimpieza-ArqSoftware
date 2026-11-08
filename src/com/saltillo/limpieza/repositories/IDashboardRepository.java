package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.models.dto.ActividadDetalleDTO;
import com.saltillo.limpieza.models.dto.CuadrillaResumenDTO;
import com.saltillo.limpieza.models.dto.DashboardKpisDTO;
import com.saltillo.limpieza.models.dto.FiltroActividadDTO;
import java.util.List;

/**
 * Contrato de acceso a datos para las métricas del Dashboard Ejecutivo y consultas.
 * 
 * Implementa el principio de Inversión de Dependencias (DIP de SOLID).
 * 
 * =========================================================================
 * CONTRATO PARA XIMENA (Issue #17: SqlDashboardRepository):
 * Para implementar este repositorio con MySQL, crear 'SqlDashboardRepository'
 * implementando estos métodos:
 * - obtenerKpis(): Métricas agregadas (COUNT, AVG).
 * - obtenerResumenPorCuadrillas(): Resumen con GROUP BY cuadrilla_id.
 * - buscarActividades(FiltroActividadDTO): Consulta dinámica WHERE con los filtros.
 * =========================================================================
 */
public interface IDashboardRepository {

    /**
     * Obtiene los indicadores ejecutivos consolidados (KPIs globales).
     * 
     * @return DTO con totales, promedios y cuadrilla líder.
     */
    DashboardKpisDTO obtenerKpis();

    /**
     * Obtiene el listado desglosado del desempeño de cada cuadrilla.
     * 
     * @return Lista inmutable de DTOs con estadísticas individuales.
     */
    List<CuadrillaResumenDTO> obtenerResumenPorCuadrillas();

    /**
     * Busca y filtra actividades de limpieza según los criterios especificados.
     * 
     * @param filtro DTO con criterios opcionales de cuadrilla, colonia, estado y fechas.
     * @return Lista de actividades que cumplen con los criterios.
     */
    List<ActividadDetalleDTO> buscarActividades(FiltroActividadDTO filtro);

    /**
     * Registra una nueva actividad en el almacén de datos (soporta múltiples colonias).
     * 
     * @param idCuadrilla Identificador de la cuadrilla asignada.
     * @param colonias Lista de colonias intervenidas (Requisito 8 rúbrica).
     * @param tipoLimpieza Tipo de servicio de limpieza.
     * @param observaciones Notas u observaciones operativas.
     */
    void registrarActividad(int idCuadrilla, List<String> colonias, String tipoLimpieza, String observaciones);

    /**
     * Sobrecarga de conveniencia para registrar actividad en una sola colonia.
     */
    void registrarActividad(int idCuadrilla, String colonia, String tipoLimpieza, String observaciones);

    /**
     * Lista de nombres de las cuadrillas registradas para poblar combos y simuladores.
     * 
     * @return Lista de nombres legibles de cuadrillas.
     */
    List<String> obtenerNombresCuadrillas();

    /**
     * Obtiene el ID numérico correspondiente a un nombre de cuadrilla.
     * 
     * @param nombre Nombre de la cuadrilla.
     * @return ID numérico.
     */
    int obtenerIdPorNombre(String nombre);
}
