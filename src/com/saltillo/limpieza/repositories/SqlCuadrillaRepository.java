package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.models.entities.Cuadrilla;
import com.saltillo.limpieza.models.entities.Empleado;
import com.saltillo.limpieza.models.entities.IntegranteCuadrilla;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementación MySQL de ICuadrillaPersonalRepository (Issue #9).
 * Las operaciones que tocan varias tablas (guardar con jefe, eliminar, cambiar jefe) corren en una
 * transacción: si algo falla se deshace todo y la conexión compartida vuelve a su modo normal.
 */
public class SqlCuadrillaRepository extends SqlRepositorioBase implements ICuadrillaPersonalRepository {

    private static final String COLUMNAS = "ID_CUADRILLA, NOMBRE, ACTIVO";

    private static Cuadrilla mapear(ResultSet rs) throws SQLException {
        return new Cuadrilla(rs.getInt("ID_CUADRILLA"), rs.getString("NOMBRE"), rs.getInt("ACTIVO") != 0);
    }

    private static IntegranteCuadrilla mapearIntegrante(ResultSet rs) throws SQLException {
        Date fecha = rs.getDate("FECHA_ASIGNACION");
        return new IntegranteCuadrilla(
                rs.getInt("ID_CUADRILLA_EMPLEADO"),
                rs.getInt("ID_CUADRILLA"),
                SqlEmpleadoRepository.mapear(rs),
                fecha == null ? null : fecha.toLocalDate(),
                rs.getInt("ES_JEFE") != 0);
    }

    // =====================  ICrudRepository<Cuadrilla>  =====================

    @Override
    public List<Cuadrilla> listar() {
        return consultar("SELECT " + COLUMNAS + " FROM cuadrillas ORDER BY NOMBRE", SqlCuadrillaRepository::mapear);
    }

    @Override
    public Optional<Cuadrilla> buscarPorId(int id) {
        return consultarUno("SELECT " + COLUMNAS + " FROM cuadrillas WHERE ID_CUADRILLA = ?",
                SqlCuadrillaRepository::mapear, id);
    }

    @Override
    public boolean guardar(Cuadrilla c) {
        int id = insertar("INSERT INTO cuadrillas (NOMBRE, ACTIVO) VALUES (?, ?)", c.getNombre(), c.isActivo() ? 1 : 0);
        c.setIdCuadrilla(id);
        return id > 0;
    }

    @Override
    public boolean actualizar(Cuadrilla c) {
        return enTransaccion(con -> actualizarCuadrilla(con, c));
    }

    @Override
    public boolean eliminar(int id) {
        // Primero se borra al personal (FK RESTRICT); si la cuadrilla tiene actividades, MySQL rechaza
        // el DELETE final y la transacción se deshace completa.
        return enTransaccion(con -> {
            update(con, "DELETE FROM cuadrilla_empleado WHERE ID_CUADRILLA = ?", id);
            return update(con, "DELETE FROM cuadrillas WHERE ID_CUADRILLA = ?", id) > 0;
        });
    }

    // =====================  ICuadrillaPersonalRepository  =====================

    @Override
    public List<IntegranteCuadrilla> listarIntegrantes(int idCuadrilla) {
        return consultar("SELECT ce.ID_CUADRILLA_EMPLEADO, ce.ID_CUADRILLA, ce.FECHA_ASIGNACION, ce.ES_JEFE, "
                + "e.ID_EMPLEADO, e.NOMBRE, e.APELLIDO_PATERNO, e.APELLIDO_MATERNO, e.TELEFONO, e.PUESTO, e.ACTIVO "
                + "FROM cuadrilla_empleado ce JOIN empleados e ON e.ID_EMPLEADO = ce.ID_EMPLEADO "
                + "WHERE ce.ID_CUADRILLA = ? ORDER BY ce.ES_JEFE DESC, e.APELLIDO_PATERNO, e.NOMBRE",
                SqlCuadrillaRepository::mapearIntegrante, idCuadrilla);
    }

    @Override
    public List<Empleado> listarEmpleadosDisponibles(int idCuadrilla) {
        return consultar("SELECT e.ID_EMPLEADO, e.NOMBRE, e.APELLIDO_PATERNO, e.APELLIDO_MATERNO, e.TELEFONO, "
                + "e.PUESTO, e.ACTIVO FROM empleados e WHERE e.ACTIVO = 1 "
                + "AND NOT EXISTS (SELECT 1 FROM cuadrilla_empleado ce JOIN cuadrillas c ON c.ID_CUADRILLA = ce.ID_CUADRILLA "
                + "WHERE ce.ID_EMPLEADO = e.ID_EMPLEADO AND c.ACTIVO = 1 AND ce.ID_CUADRILLA <> ?) "
                + "AND NOT EXISTS (SELECT 1 FROM cuadrilla_empleado ce2 "
                + "WHERE ce2.ID_EMPLEADO = e.ID_EMPLEADO AND ce2.ID_CUADRILLA = ?) "
                + "ORDER BY e.APELLIDO_PATERNO, e.NOMBRE",
                SqlEmpleadoRepository::mapear, idCuadrilla, idCuadrilla);
    }

    @Override
    public boolean guardarConJefe(Cuadrilla c, int idEmpleadoJefe) {
        return enTransaccion(con -> {
            int id = insert(con, "INSERT INTO cuadrillas (NOMBRE, ACTIVO) VALUES (?, ?)",
                    c.getNombre(), c.isActivo() ? 1 : 0);
            if (id == 0) {
                return false;
            }
            c.setIdCuadrilla(id);
            asignarJefe(con, id, idEmpleadoJefe);
            return true;
        });
    }

    @Override
    public boolean actualizarConJefe(Cuadrilla c, int idEmpleadoJefe) {
        return enTransaccion(con -> {
            boolean ok = actualizarCuadrilla(con, c);
            asignarJefe(con, c.getIdCuadrilla(), idEmpleadoJefe);
            return ok;
        });
    }

    @Override
    public void agregarIntegrante(int idCuadrilla, int idEmpleado) {
        enTransaccion(con -> {
            if (esMiembro(con, idCuadrilla, idEmpleado)) {
                throw new ReglaNegocioException("Ese empleado ya forma parte de la cuadrilla.");
            }
            validarEmpleadoAsignable(con, idCuadrilla, idEmpleado);
            insert(con, "INSERT INTO cuadrilla_empleado (ID_CUADRILLA, ID_EMPLEADO, FECHA_ASIGNACION, ES_JEFE) "
                    + "VALUES (?, ?, ?, 0)", idCuadrilla, idEmpleado, Date.valueOf(LocalDate.now()));
            return true;
        });
    }

    @Override
    public boolean quitarIntegrante(int idCuadrilla, int idEmpleado) {
        return enTransaccion(con -> {
            List<Integer> esJefe = select(con, "SELECT ES_JEFE FROM cuadrilla_empleado "
                    + "WHERE ID_CUADRILLA = ? AND ID_EMPLEADO = ?", rs -> rs.getInt(1), idCuadrilla, idEmpleado);
            if (!esJefe.isEmpty() && esJefe.get(0) != 0) {
                throw new ReglaNegocioException("No se puede quitar al jefe de la cuadrilla. "
                        + "Primero asigna a otro jefe y guarda.");
            }
            return update(con, "DELETE FROM cuadrilla_empleado WHERE ID_CUADRILLA = ? AND ID_EMPLEADO = ?",
                    idCuadrilla, idEmpleado) > 0;
        });
    }

    // =====================  reglas y piezas internas  =====================

    private boolean actualizarCuadrilla(Connection con, Cuadrilla c) throws SQLException {
        boolean ok = update(con, "UPDATE cuadrillas SET NOMBRE = ?, ACTIVO = ? WHERE ID_CUADRILLA = ?",
                c.getNombre(), c.isActivo() ? 1 : 0, c.getIdCuadrilla()) > 0;
        if (c.isActivo()) {
            verificarPersonalSinConflicto(con, c.getIdCuadrilla());
        }
        return ok;
    }

    /** Deja a idEmpleado como único jefe de la cuadrilla (lo agrega como integrante si no lo era). */
    private void asignarJefe(Connection con, int idCuadrilla, int idEmpleado) throws SQLException {
        boolean yaEsMiembro = esMiembro(con, idCuadrilla, idEmpleado);
        if (!yaEsMiembro) {
            validarEmpleadoAsignable(con, idCuadrilla, idEmpleado);
        } else {
            validarSinOtraCuadrillaActiva(con, idCuadrilla, idEmpleado);
        }
        update(con, "UPDATE cuadrilla_empleado SET ES_JEFE = 0 WHERE ID_CUADRILLA = ?", idCuadrilla);
        if (yaEsMiembro) {
            update(con, "UPDATE cuadrilla_empleado SET ES_JEFE = 1 WHERE ID_CUADRILLA = ? AND ID_EMPLEADO = ?",
                    idCuadrilla, idEmpleado);
        } else {
            insert(con, "INSERT INTO cuadrilla_empleado (ID_CUADRILLA, ID_EMPLEADO, FECHA_ASIGNACION, ES_JEFE) "
                    + "VALUES (?, ?, ?, 1)", idCuadrilla, idEmpleado, Date.valueOf(LocalDate.now()));
        }
    }

    private boolean esMiembro(Connection con, int idCuadrilla, int idEmpleado) throws SQLException {
        return !select(con, "SELECT 1 FROM cuadrilla_empleado WHERE ID_CUADRILLA = ? AND ID_EMPLEADO = ?",
                rs -> rs.getInt(1), idCuadrilla, idEmpleado).isEmpty();
    }

    /** El empleado debe existir, estar activo y no pertenecer a otra cuadrilla activa. */
    private void validarEmpleadoAsignable(Connection con, int idCuadrilla, int idEmpleado) throws SQLException {
        List<Empleado> lista = select(con, "SELECT ID_EMPLEADO, NOMBRE, APELLIDO_PATERNO, APELLIDO_MATERNO, "
                + "TELEFONO, PUESTO, ACTIVO FROM empleados WHERE ID_EMPLEADO = ?",
                SqlEmpleadoRepository::mapear, idEmpleado);
        if (lista.isEmpty()) {
            throw new ReglaNegocioException("El empleado seleccionado ya no existe.");
        }
        Empleado e = lista.get(0);
        if (!e.isActivo()) {
            throw new ReglaNegocioException(nombre(e) + " está inactivo y no puede asignarse a una cuadrilla.");
        }
        validarSinOtraCuadrillaActiva(con, idCuadrilla, idEmpleado);
    }

    private void validarSinOtraCuadrillaActiva(Connection con, int idCuadrilla, int idEmpleado) throws SQLException {
        List<String> otras = select(con, "SELECT c.NOMBRE FROM cuadrilla_empleado ce "
                + "JOIN cuadrillas c ON c.ID_CUADRILLA = ce.ID_CUADRILLA "
                + "WHERE ce.ID_EMPLEADO = ? AND c.ACTIVO = 1 AND ce.ID_CUADRILLA <> ?",
                rs -> rs.getString(1), idEmpleado, idCuadrilla);
        if (!otras.isEmpty()) {
            List<Empleado> emp = select(con, "SELECT ID_EMPLEADO, NOMBRE, APELLIDO_PATERNO, APELLIDO_MATERNO, "
                    + "TELEFONO, PUESTO, ACTIVO FROM empleados WHERE ID_EMPLEADO = ?",
                    SqlEmpleadoRepository::mapear, idEmpleado);
            String quien = emp.isEmpty() ? "El empleado" : nombre(emp.get(0));
            throw new ReglaNegocioException(quien + " ya pertenece a la cuadrilla activa \"" + otras.get(0)
                    + "\". Un empleado no puede estar en dos cuadrillas activas.");
        }
    }

    /** Al activar una cuadrilla, ninguno de sus integrantes puede estar en otra cuadrilla activa. */
    private void verificarPersonalSinConflicto(Connection con, int idCuadrilla) throws SQLException {
        List<String> conflictos = select(con, "SELECT e.NOMBRE, e.APELLIDO_PATERNO, c2.NOMBRE "
                + "FROM cuadrilla_empleado ce JOIN empleados e ON e.ID_EMPLEADO = ce.ID_EMPLEADO "
                + "JOIN cuadrilla_empleado ce2 ON ce2.ID_EMPLEADO = ce.ID_EMPLEADO AND ce2.ID_CUADRILLA <> ce.ID_CUADRILLA "
                + "JOIN cuadrillas c2 ON c2.ID_CUADRILLA = ce2.ID_CUADRILLA AND c2.ACTIVO = 1 "
                + "WHERE ce.ID_CUADRILLA = ?",
                rs -> rs.getString(1) + " " + rs.getString(2) + " ya pertenece a la cuadrilla activa \""
                        + rs.getString(3) + "\"", idCuadrilla);
        if (!conflictos.isEmpty()) {
            throw new ReglaNegocioException("No se puede activar la cuadrilla: " + conflictos.get(0)
                    + ". Quítalo de una de las dos primero.");
        }
    }

    private static String nombre(Empleado e) {
        return e.getNombre() + " " + e.getApellidoPaterno();
    }

    // =====================  transacciones  =====================

    @FunctionalInterface
    private interface Transaccion {
        boolean ejecutar(Connection con) throws SQLException;
    }

    /**
     * Ejecuta el bloque en una transacción sobre la conexión compartida. Si algo falla hace rollback;
     * al terminar siempre devuelve la conexión a autocommit para no afectar a los demás repositorios.
     */
    private boolean enTransaccion(Transaccion bloque) {
        Connection con = null;
        boolean autoCommitPrevio = true;
        try {
            con = con();
            autoCommitPrevio = con.getAutoCommit();
            con.setAutoCommit(false);
            boolean resultado = bloque.ejecutar(con);
            con.commit();
            return resultado;
        } catch (ReglaNegocioException e) {
            rollback(con);
            throw e;
        } catch (SQLException | RuntimeException e) {
            rollback(con);
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RepositorioException("Error al guardar los cambios de la cuadrilla.", e);
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(autoCommitPrevio);
                } catch (SQLException ignorada) {
                    // si la conexión murió, ConexionBD abrirá otra en la siguiente consulta
                }
            }
        }
    }

    private static void rollback(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException ignorada) {
                // nada más que hacer
            }
        }
    }
}
