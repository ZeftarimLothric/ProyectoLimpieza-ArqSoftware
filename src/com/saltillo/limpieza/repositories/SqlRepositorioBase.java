package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.database.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Utilidades JDBC comunes para los repositorios MySQL (Issues #8 y #9).
 * Evita repetir try/catch/cierre de recursos en cada repositorio. Nunca cierra la Connection
 * (es la conexión única de ConexionBD); solo cierra sus PreparedStatement y ResultSet.
 */
abstract class SqlRepositorioBase {

    /** Convierte la fila actual de un ResultSet en un objeto. */
    @FunctionalInterface
    interface Mapeador<T> {
        T mapear(ResultSet rs) throws SQLException;
    }

    protected Connection con() throws SQLException {
        return ConexionBD.getInstance().getConexion();
    }

    // ---------- versiones con Connection explícita (sirven dentro de transacciones) ----------

    static <T> List<T> select(Connection c, String sql, Mapeador<T> mapeador, Object... params) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            asignar(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(mapeador.mapear(rs));
                }
                return lista;
            }
        }
    }

    static int update(Connection c, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            asignar(ps, params);
            return ps.executeUpdate();
        }
    }

    /** Ejecuta un INSERT y devuelve el id autogenerado (0 si no hubo). */
    static int insert(Connection c, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignar(ps, params);
            if (ps.executeUpdate() == 0) {
                return 0;
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    private static void asignar(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    // ---------- versiones de conveniencia (usan la conexión única y traducen errores) ----------

    protected <T> List<T> consultar(String sql, Mapeador<T> mapeador, Object... params) {
        try {
            return select(con(), sql, mapeador, params);
        } catch (SQLException e) {
            throw new RepositorioException("Error al consultar la base de datos.", e);
        }
    }

    protected <T> Optional<T> consultarUno(String sql, Mapeador<T> mapeador, Object... params) {
        List<T> lista = consultar(sql, mapeador, params);
        return lista.isEmpty() ? Optional.<T>empty() : Optional.of(lista.get(0));
    }

    protected int ejecutar(String sql, Object... params) {
        try {
            return update(con(), sql, params);
        } catch (SQLException e) {
            throw new RepositorioException("Error al modificar la base de datos.", e);
        }
    }

    protected int insertar(String sql, Object... params) {
        try {
            return insert(con(), sql, params);
        } catch (SQLException e) {
            throw new RepositorioException("Error al insertar en la base de datos.", e);
        }
    }

    /** Escapa % _ y \ para usar el texto del usuario dentro de un LIKE. */
    protected static String escaparLike(String texto) {
        return texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
