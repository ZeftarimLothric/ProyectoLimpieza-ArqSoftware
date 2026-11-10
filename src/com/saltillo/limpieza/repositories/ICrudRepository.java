package com.saltillo.limpieza.repositories;

import java.util.List;
import java.util.Optional;

/**
 * Contrato CRUD común para los repositorios de catálogos (Issue #3).
 * Todas las interfaces de catálogo lo extienden; los CRUD de los demás issues
 * (#8 catálogos, #10 actividades) se programan contra estas interfaces, no contra MySQL.
 *
 * Los errores de base de datos se lanzan como RepositorioException.
 *
 * @param <T> entidad que maneja el repositorio.
 */
public interface ICrudRepository<T> {

    /** Todos los registros. */
    List<T> listar();

    /** Registro con ese id, o Optional.empty() si no existe. */
    Optional<T> buscarPorId(int id);

    /** Inserta un registro nuevo y deja el id generado en la entidad. true si se insertó. */
    boolean guardar(T entidad);

    /** Actualiza un registro existente (identificado por su id). true si se modificó. */
    boolean actualizar(T entidad);

    /** Elimina el registro con ese id. true si se eliminó. */
    boolean eliminar(int id);
}
