package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.models.entities.Cuadrilla;
import com.saltillo.limpieza.models.entities.Empleado;
import com.saltillo.limpieza.models.entities.IntegranteCuadrilla;

import java.util.List;

/**
 * Contrato de la gestión de cuadrillas con su jefe y su personal (Issue #9).
 * Extiende ICuadrillaRepository (Issue #3) sin modificarlo.
 *
 * Reglas que garantiza la implementación (lanzan ReglaNegocioException con un mensaje legible):
 * - Cada cuadrilla tiene un solo jefe y el jefe también es integrante de la cuadrilla.
 * - Un empleado no puede estar en dos cuadrillas activas a la vez.
 * - Solo se asignan empleados activos.
 * - No se puede quitar al jefe: primero se asigna otro.
 */
public interface ICuadrillaPersonalRepository extends ICuadrillaRepository {

    /** Integrantes de la cuadrilla, el jefe primero. */
    List<IntegranteCuadrilla> listarIntegrantes(int idCuadrilla);

    /**
     * Empleados activos que se pueden asignar a esta cuadrilla: no están ya en ella ni
     * pertenecen a otra cuadrilla activa. Para una cuadrilla nueva se pasa 0.
     */
    List<Empleado> listarEmpleadosDisponibles(int idCuadrilla);

    /** Inserta la cuadrilla y su jefe en una sola transacción. Deja el id generado en la cuadrilla. */
    boolean guardarConJefe(Cuadrilla cuadrilla, int idEmpleadoJefe);

    /** Actualiza la cuadrilla y cambia su jefe (si es otro) en una sola transacción. */
    boolean actualizarConJefe(Cuadrilla cuadrilla, int idEmpleadoJefe);

    void agregarIntegrante(int idCuadrilla, int idEmpleado);

    /** true si se quitó; lanza ReglaNegocioException si es el jefe. */
    boolean quitarIntegrante(int idCuadrilla, int idEmpleado);
}
