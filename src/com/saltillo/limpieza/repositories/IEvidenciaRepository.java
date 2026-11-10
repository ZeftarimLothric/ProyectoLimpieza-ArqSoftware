package com.saltillo.limpieza.repositories;

import java.util.List;

/**
 * Contrato para asociar evidencias a una actividad (Integrante 3 - Issues #10 y #12).
 * Solo maneja los nombres de archivo que entrega EvidenciaStorageService.
 *
 * TODO(#2): cambiar los nombres por la entidad Evidencia cuando exista.
 * TODO(#18): crear SqlEvidenciaRepository con este mismo contrato.
 */
public interface IEvidenciaRepository {

    List<String> listarPorActividad(int idActividad);

    void reemplazar(int idActividad, List<String> nombres);

    void eliminarPorActividad(int idActividad);
}
