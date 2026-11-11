package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.models.entities.Colonia;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Implementación MySQL de IColoniaRepository (Issue #8). */
public class SqlColoniaRepository extends SqlRepositorioBase implements IColoniaRepository {

    private static final String COLUMNAS = "ID_COLONIA, NOMBRE, CODIGO_POSTAL, MUNICIPIO, ESTADO";

    private static Colonia mapear(ResultSet rs) throws SQLException {
        return new Colonia(
                rs.getInt("ID_COLONIA"),
                rs.getString("NOMBRE"),
                rs.getString("CODIGO_POSTAL"),
                rs.getString("MUNICIPIO"),
                rs.getString("ESTADO"));
    }

    @Override
    public List<Colonia> listar() {
        return consultar("SELECT " + COLUMNAS + " FROM colonias ORDER BY NOMBRE", SqlColoniaRepository::mapear);
    }

    @Override
    public Optional<Colonia> buscarPorId(int id) {
        return consultarUno("SELECT " + COLUMNAS + " FROM colonias WHERE ID_COLONIA = ?",
                SqlColoniaRepository::mapear, id);
    }

    @Override
    public List<Colonia> buscarPorNombreOCodigoPostal(String texto) {
        String patron = "%" + escaparLike(texto.trim()) + "%";
        return consultar("SELECT " + COLUMNAS + " FROM colonias WHERE NOMBRE LIKE ? OR CODIGO_POSTAL LIKE ? "
                + "ORDER BY NOMBRE", SqlColoniaRepository::mapear, patron, patron);
    }

    @Override
    public boolean guardar(Colonia c) {
        int id = insertar("INSERT INTO colonias (NOMBRE, CODIGO_POSTAL, MUNICIPIO, ESTADO) VALUES (?, ?, ?, ?)",
                c.getNombre(), c.getCodigoPostal(), c.getMunicipio(), c.getEstado());
        c.setIdColonia(id);
        return id > 0;
    }

    @Override
    public boolean actualizar(Colonia c) {
        return ejecutar("UPDATE colonias SET NOMBRE = ?, CODIGO_POSTAL = ?, MUNICIPIO = ?, ESTADO = ? "
                + "WHERE ID_COLONIA = ?",
                c.getNombre(), c.getCodigoPostal(), c.getMunicipio(), c.getEstado(), c.getIdColonia()) > 0;
    }

    @Override
    public boolean eliminar(int id) {
        return ejecutar("DELETE FROM colonias WHERE ID_COLONIA = ?", id) > 0;
    }
}
