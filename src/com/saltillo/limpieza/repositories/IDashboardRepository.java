package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.models.dto.CuadrillaResumenDTO;
import com.saltillo.limpieza.models.dto.DashboardKpisDTO;
import java.util.List;

/**
 * Contrato de acceso a datos para las métricas del Dashboard Ejecutivo.
 * 
 * Implementa el principio de Inversión de Dependencias (DIP de SOLID).
 * Permite que el Dashboard funcione sin depender de la base de datos real
 * que aún están construyendo el Integrante 1 (Esquema/Conexión) e Integrante 2 (DAOs).
 * 
 * =========================================================================
 * CONTRATO PARA INTEGRANTES 1 Y 2:
 * Cuando la base de datos MySQL/SQL esté lista, basta con crear una clase:
 * public class SqlDashboardRepository implements IDashboardRepository { ... }
 * e implementarla realizando las consultas agregadas (COUNT, GROUP BY cuadrilla).
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
     * Registra una nueva actividad en el almacén de datos y dispara el evento
     * correspondiente para los observadores.
     * 
     * @param idCuadrilla Identificador numérico de la cuadrilla asignada.
     * @param colonia Nombre de la colonia intervenida (catálogo Saltillo / Sepomex).
     * @param tipoLimpieza Tipo de servicio (Barrido, Deshierbe, Retiro de escombros, etc.).
     * @param observaciones Notas u observaciones de la cuadrilla.
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
