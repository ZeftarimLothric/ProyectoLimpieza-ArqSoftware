package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.models.dto.ActividadDetalleDTO;

import java.util.List;

/**
 * Contrato de acceso a datos de las actividades de limpieza (Integrante 3 - Issue #10).
 *
 * TODO(#2): cambiar ActividadDetalleDTO por la entidad Actividad cuando exista.
 * Implementaciones: MockActividadRepository (pruebas) y SqlActividadRepository (MySQL, Issue #18).
 */
public interface IActividadRepository {

    /**
     * @param estado estado a filtrar, o null para traer todas
     */
    List<ActividadDetalleDTO> listar(String estado);

    /**
     * @return la actividad, o null si no existe
     */
    ActividadDetalleDTO buscarPorId(int idActividad);

    /**
     * @return el ID asignado a la nueva actividad
     */
    int guardar(ActividadDetalleDTO actividad);

    void actualizar(ActividadDetalleDTO actividad);

    void eliminar(int idActividad);

    // TODO(#3): mover a ITipoActividadRepository cuando exista.
    List<String> listarTiposActividad();
}
