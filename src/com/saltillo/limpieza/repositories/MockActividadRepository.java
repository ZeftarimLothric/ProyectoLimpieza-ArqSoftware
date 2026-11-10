package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.models.dto.ActividadDetalleDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación en memoria de IActividadRepository (Integrante 3 - Issue #10).
 */
public class MockActividadRepository implements IActividadRepository {

    private final List<ActividadDetalleDTO> actividades = new ArrayList<>();
    private int ultimoId = 0;

    public MockActividadRepository() {
        cargarDatosDeEjemplo();
    }

    private void cargarDatosDeEjemplo() {
        LocalDate hoy = LocalDate.now();
        guardar(crearEjemplo(1, "Cuadrilla Centro Histórico", List.of("Centro Histórico"), "Barrido Manual y Desazolve",
                "Plaza de Armas", "COMPLETADA", hoy.minusDays(1).atTime(8, 0), hoy.minusDays(1).atTime(10, 30), "Retiro de basura en la plaza."));
        guardar(crearEjemplo(3, "Cuadrilla Mirasierra / Oriente", List.of("Mirasierra"), "Recolección de Escombros y Enseres",
                "Entronque Fundadores", "EN PROCESO", hoy.atTime(9, 0), null, "Apoyo con camión de volteo."));
        guardar(crearEjemplo(6, "Cuadrilla Saltillo 2000", List.of("Saltillo 2000"), "Deshierbe y Poda de Camellones",
                "Av. Javier García", "PROGRAMADA", hoy.plusDays(2).atTime(7, 30), null, "Mantenimiento de camellón."));
    }

    private ActividadDetalleDTO crearEjemplo(int idCuadrilla, String cuadrilla, List<String> colonias, String tipo, String lugar,
                                             String estado, LocalDateTime programada, LocalDateTime realizada, String descripcion) {
        return new ActividadDetalleDTO(0, idCuadrilla, cuadrilla, colonias, tipo, lugar, estado, programada, descripcion, realizada);
    }

    @Override
    public List<ActividadDetalleDTO> listar(String estado) {
        List<ActividadDetalleDTO> resultado = new ArrayList<>();
        for (ActividadDetalleDTO a : actividades) {
            if (estado == null || estado.equalsIgnoreCase(a.getEstado())) {
                resultado.add(a);
            }
        }
        return resultado;
    }

    @Override
    public ActividadDetalleDTO buscarPorId(int idActividad) {
        for (ActividadDetalleDTO a : actividades) {
            if (a.getIdActividad() == idActividad) {
                return a;
            }
        }
        return null;
    }

    @Override
    public int guardar(ActividadDetalleDTO a) {
        ultimoId++;
        actividades.add(conId(a, ultimoId));
        return ultimoId;
    }

    @Override
    public void actualizar(ActividadDetalleDTO actividad) {
        for (int i = 0; i < actividades.size(); i++) {
            if (actividades.get(i).getIdActividad() == actividad.getIdActividad()) {
                actividades.set(i, actividad);
                return;
            }
        }
    }

    @Override
    public void eliminar(int idActividad) {
        actividades.removeIf(a -> a.getIdActividad() == idActividad);
    }

    @Override
    public List<String> listarTiposActividad() {
        return List.of("Barrido Manual y Desazolve", "Deshierbe y Poda de Camellones", "Recolección de Escombros y Enseres",
                "Lavado de Espacios Públicos", "Limpieza de Canales Pluviales");
    }

    private ActividadDetalleDTO conId(ActividadDetalleDTO a, int id) {
        return new ActividadDetalleDTO(id, a.getIdCuadrilla(), a.getNombreCuadrilla(), a.getColonias(), a.getTipoLimpieza(),
                a.getLugarEspecifico(), a.getEstado(), a.getFechaHora(), a.getObservaciones(), a.getFechaRealizacion());
    }
}
