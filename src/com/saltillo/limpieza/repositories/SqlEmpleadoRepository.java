package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.models.entities.Empleado;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Implementación MySQL de IEmpleadoRepository (Issue #8). */
public class SqlEmpleadoRepository extends SqlRepositorioBase implements IEmpleadoRepository {

    private static final String COLUMNAS =
            "ID_EMPLEADO, NOMBRE, APELLIDO_PATERNO, APELLIDO_MATERNO, TELEFONO, PUESTO, ACTIVO";

    /** Convierte una fila (con las columnas de empleados) en Empleado. También la usa SqlCuadrillaRepository. */
    static Empleado mapear(ResultSet rs) throws SQLException {
        return new Empleado(
                rs.getInt("ID_EMPLEADO"),
                rs.getString("NOMBRE"),
                rs.getString("APELLIDO_PATERNO"),
                rs.getString("APELLIDO_MATERNO"),
                rs.getString("TELEFONO"),
                rs.getString("PUESTO"),
                rs.getInt("ACTIVO") != 0);
    }

    @Override
    public List<Empleado> listar() {
        return consultar("SELECT " + COLUMNAS + " FROM empleados ORDER BY APELLIDO_PATERNO, NOMBRE",
                SqlEmpleadoRepository::mapear);
    }

    @Override
    public Optional<Empleado> buscarPorId(int id) {
        return consultarUno("SELECT " + COLUMNAS + " FROM empleados WHERE ID_EMPLEADO = ?",
                SqlEmpleadoRepository::mapear, id);
    }

    @Override
    public boolean guardar(Empleado e) {
        int id = insertar("INSERT INTO empleados (NOMBRE, APELLIDO_PATERNO, APELLIDO_MATERNO, TELEFONO, PUESTO, ACTIVO) "
                + "VALUES (?, ?, ?, ?, ?, ?)",
                e.getNombre(), e.getApellidoPaterno(), e.getApellidoMaterno(), e.getTelefono(), e.getPuesto(),
                e.isActivo() ? 1 : 0);
        e.setIdEmpleado(id);
        return id > 0;
    }

    @Override
    public boolean actualizar(Empleado e) {
        return ejecutar("UPDATE empleados SET NOMBRE = ?, APELLIDO_PATERNO = ?, APELLIDO_MATERNO = ?, "
                + "TELEFONO = ?, PUESTO = ?, ACTIVO = ? WHERE ID_EMPLEADO = ?",
                e.getNombre(), e.getApellidoPaterno(), e.getApellidoMaterno(), e.getTelefono(), e.getPuesto(),
                e.isActivo() ? 1 : 0, e.getIdEmpleado()) > 0;
    }

    @Override
    public boolean eliminar(int id) {
        return ejecutar("DELETE FROM empleados WHERE ID_EMPLEADO = ?", id) > 0;
    }
}
