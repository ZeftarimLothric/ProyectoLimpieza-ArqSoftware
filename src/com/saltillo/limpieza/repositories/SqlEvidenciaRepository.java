package com.saltillo.limpieza.repositories;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementación MySQL de IEvidenciaRepository (Integrante 3 - Issue #18).
 * En evidencia.RUTA_IMAGEN se guarda solo el nombre del archivo que genera EvidenciaStorageService.
 */
public class SqlEvidenciaRepository extends SqlRepositorioBase implements IEvidenciaRepository {

    @Override
    public List<String> listarPorActividad(int idActividad) {
        return consultar("SELECT RUTA_IMAGEN FROM evidencia WHERE ID_ACTIVIDAD = ? ORDER BY ID_EVIDENCIA",
                rs -> rs.getString(1), idActividad);
    }

    /**
     * Deja exactamente esos nombres: borra los que ya no están y agrega los nuevos.
     * Las evidencias que se conservan mantienen su FECHA_CARGA original.
     */
    @Override
    public void reemplazar(int idActividad, List<String> nombres) {
        Connection con = null;
        try {
            con = con();
            con.setAutoCommit(false);
            List<String> actuales = select(con, "SELECT RUTA_IMAGEN FROM evidencia WHERE ID_ACTIVIDAD = ?",
                    rs -> rs.getString(1), idActividad);
            for (String actual : actuales) {
                if (!nombres.contains(actual)) {
                    update(con, "DELETE FROM evidencia WHERE ID_ACTIVIDAD = ? AND RUTA_IMAGEN = ?", idActividad, actual);
                }
            }
            for (String nombre : nombres) {
                if (!actuales.contains(nombre)) {
                    insert(con, "INSERT INTO evidencia (ID_ACTIVIDAD, RUTA_IMAGEN) VALUES (?, ?)", idActividad, nombre);
                }
            }
            con.commit();
        } catch (SQLException e) {
            rollback(con);
            throw new RepositorioException("No se pudieron guardar las evidencias de la actividad.", e);
        } finally {
            restaurarAutoCommit(con);
        }
    }

    @Override
    public void eliminarPorActividad(int idActividad) {
        ejecutar("DELETE FROM evidencia WHERE ID_ACTIVIDAD = ?", idActividad);
    }

    private static void rollback(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException ignorada) {
                // no hay nada más que deshacer
            }
        }
    }

    private static void restaurarAutoCommit(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ignorada) {
                // si la conexión murió, ConexionBD abrirá otra en la siguiente consulta
            }
        }
    }
}
