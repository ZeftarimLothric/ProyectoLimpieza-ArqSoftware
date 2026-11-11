package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.models.entities.Usuario;
import java.util.Optional;

/**
 * Contrato de acceso a datos de Usuario (Issue #3).
 * Hereda listar, buscarPorId, guardar, actualizar y eliminar de ICrudRepository.
 */
public interface IUsuarioRepository extends ICrudRepository<Usuario> {

    /**
     * Busca un usuario por su nombre de usuario (USERNAME). Lo usa el login (Issue #7).
     * La comparación de la contraseña la hace el controlador con PasswordHasher.
     */
    Optional<Usuario> buscarPorUsername(String username);
}
