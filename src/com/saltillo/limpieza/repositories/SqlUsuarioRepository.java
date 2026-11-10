package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.database.ConexionBD;
import com.saltillo.limpieza.models.entities.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación MySQL de IUsuarioRepository (Issue #7).
 * Toda la SQL del login vive aquí: LoginView no tiene SQL y LoginController solo usa el contrato.
 *
 * Nota: la tabla usuarios no tiene columna de rol (la app solo tiene administradores),
 * por eso todo usuario leído se crea con el rol ADMINISTRADOR.
 * Las contraseñas que llegan a guardar/actualizar ya deben venir hasheadas (PasswordHasher).
 */
public class SqlUsuarioRepository implements IUsuarioRepository {

    private static final String ROL_POR_DEFECTO = "ADMINISTRADOR";
    private static final String COLUMNAS = "ID_USUARIO, USERNAME, CONTRASENA, NOMBRE, ACTIVO";

    private Connection con() throws SQLException {
        return ConexionBD.getInstance().getConexion();
    }

    @Override
    public List<Usuario> listar() {
        String sql = "SELECT " + COLUMNAS + " FROM usuarios ORDER BY NOMBRE";
        try (PreparedStatement ps = con().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            List<Usuario> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new RepositorioException("No se pudo listar los usuarios.", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(int id) {
        String sql = "SELECT " + COLUMNAS + " FROM usuarios WHERE ID_USUARIO = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, id);
            return primero(ps);
        } catch (SQLException e) {
            throw new RepositorioException("No se pudo consultar el usuario.", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        String sql = "SELECT " + COLUMNAS + " FROM usuarios WHERE USERNAME = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, username);
            return primero(ps);
        } catch (SQLException e) {
            throw new RepositorioException("No se pudo consultar el usuario.", e);
        }
    }

    private Optional<Usuario> primero(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? Optional.of(mapear(rs)) : Optional.<Usuario>empty();
        }
    }

    @Override
    public boolean guardar(Usuario u) {
        String sql = "INSERT INTO usuarios (USERNAME, CONTRASENA, NOMBRE, ACTIVO) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getUsuario());
            ps.setString(2, u.getContrasenaHash());
            ps.setString(3, u.getNombre());
            ps.setInt(4, u.isActivo() ? 1 : 0);
            boolean insertado = ps.executeUpdate() > 0;
            if (insertado) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        u.setIdUsuario(keys.getInt(1));
                    }
                }
            }
            return insertado;
        } catch (SQLException e) {
            throw new RepositorioException("No se pudo guardar el usuario.", e);
        }
    }

    @Override
    public boolean actualizar(Usuario u) {
        // Si contrasenaHash viene null se conserva la contraseña actual.
        String sql = "UPDATE usuarios SET USERNAME = ?, NOMBRE = ?, ACTIVO = ?, "
                + "CONTRASENA = COALESCE(?, CONTRASENA) WHERE ID_USUARIO = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, u.getUsuario());
            ps.setString(2, u.getNombre());
            ps.setInt(3, u.isActivo() ? 1 : 0);
            ps.setString(4, u.getContrasenaHash());
            ps.setInt(5, u.getIdUsuario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositorioException("No se pudo actualizar el usuario.", e);
        }
    }

    @Override
    public boolean eliminar(int id) {
        try (PreparedStatement ps = con().prepareStatement("DELETE FROM usuarios WHERE ID_USUARIO = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositorioException("No se pudo eliminar el usuario.", e);
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("ID_USUARIO"),
                rs.getString("NOMBRE"),
                rs.getString("USERNAME"),
                rs.getString("CONTRASENA"),
                ROL_POR_DEFECTO,
                rs.getInt("ACTIVO") != 0);
    }
}
