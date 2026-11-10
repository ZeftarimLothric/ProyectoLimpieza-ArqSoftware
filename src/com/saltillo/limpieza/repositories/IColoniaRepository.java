package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.models.entities.Colonia;
import java.util.List;

/**
 * Contrato de acceso a datos de Colonia (Issue #3).
 * Hereda listar, buscarPorId, guardar, actualizar y eliminar de ICrudRepository.
 */
public interface IColoniaRepository extends ICrudRepository<Colonia> {

    /**
     * Busca colonias cuyo nombre o código postal contengan el texto indicado
     * (para el combo / buscador de colonias SEPOMEX).
     */
    List<Colonia> buscarPorNombreOCodigoPostal(String texto);
}
