package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.models.entities.TipoActividad;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Implementación MySQL de ITipoActividadRepository (Issue #8). */
public class SqlTipoActividadRepository extends SqlRepositorioBase implements ITipoActividadRepository {

    private static final String COLUMNAS = "ID_TIPO_ACTIVIDAD, NOMBRE, DESCRIPCION";

    private static TipoActividad mapear(ResultSet rs) throws SQLException {
        return new TipoActividad(
                rs.getInt("ID_TIPO_ACTIVIDAD"),
                rs.getString("NOMBRE"),
                rs.getString("DESCRIPCION"));
    }

    @Override
    public List<TipoActividad> listar() {
        return consultar("SELECT " + COLUMNAS + " FROM tipo_actividad ORDER BY NOMBRE",
                SqlTipoActividadRepository::mapear);
    }

    @Override
    public Optional<TipoActividad> buscarPorId(int id) {
        return consultarUno("SELECT " + COLUMNAS + " FROM tipo_actividad WHERE ID_TIPO_ACTIVIDAD = ?",
                SqlTipoActividadRepository::mapear, id);
    }

    @Override
    public boolean guardar(TipoActividad t) {
        int id = insertar("INSERT INTO tipo_actividad (NOMBRE, DESCRIPCION) VALUES (?, ?)",
                t.getNombre(), t.getDescripcion());
        t.setIdTipoActividad(id);
        return id > 0;
    }

    @Override
    public boolean actualizar(TipoActividad t) {
        return ejecutar("UPDATE tipo_actividad SET NOMBRE = ?, DESCRIPCION = ? WHERE ID_TIPO_ACTIVIDAD = ?",
                t.getNombre(), t.getDescripcion(), t.getIdTipoActividad()) > 0;
    }

    @Override
    public boolean eliminar(int id) {
        return ejecutar("DELETE FROM tipo_actividad WHERE ID_TIPO_ACTIVIDAD = ?", id) > 0;
    }
}
