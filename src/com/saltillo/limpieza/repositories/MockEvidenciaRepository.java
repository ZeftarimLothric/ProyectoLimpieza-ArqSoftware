package com.saltillo.limpieza.repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación en memoria de IEvidenciaRepository (Integrante 3 - Issues #10 y #12).
 */
public class MockEvidenciaRepository implements IEvidenciaRepository {

    private final Map<Integer, List<String>> evidenciasPorActividad = new HashMap<>();

    @Override
    public List<String> listarPorActividad(int idActividad) {
        return new ArrayList<>(evidenciasPorActividad.getOrDefault(idActividad, List.of()));
    }

    @Override
    public void reemplazar(int idActividad, List<String> nombres) {
        evidenciasPorActividad.put(idActividad, new ArrayList<>(nombres));
    }

    @Override
    public void eliminarPorActividad(int idActividad) {
        evidenciasPorActividad.remove(idActividad);
    }
}
