package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.models.entities.TipoActividad;

/**
 * Contrato de acceso a datos de TipoActividad (Issue #3).
 * Hereda listar, buscarPorId, guardar, actualizar y eliminar de ICrudRepository.
 */
public interface ITipoActividadRepository extends ICrudRepository<TipoActividad> {
}
