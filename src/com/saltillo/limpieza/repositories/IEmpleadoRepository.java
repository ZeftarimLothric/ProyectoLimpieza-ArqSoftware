package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.models.entities.Empleado;

/**
 * Contrato de acceso a datos de Empleado (Issue #3).
 * Hereda listar, buscarPorId, guardar, actualizar y eliminar de ICrudRepository.
 */
public interface IEmpleadoRepository extends ICrudRepository<Empleado> {
}
