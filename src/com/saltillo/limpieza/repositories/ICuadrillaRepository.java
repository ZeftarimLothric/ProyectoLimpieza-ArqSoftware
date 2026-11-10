package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.models.entities.Cuadrilla;

/**
 * Contrato de acceso a datos de Cuadrilla (Issue #3).
 * Hereda listar, buscarPorId, guardar, actualizar y eliminar de ICrudRepository.
 */
public interface ICuadrillaRepository extends ICrudRepository<Cuadrilla> {
}
