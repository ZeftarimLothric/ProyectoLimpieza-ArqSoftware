package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.models.dto.ActividadDetalleDTO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación MySQL de IActividadRepository (Integrante 3 - Issue #18).
 *
 * La actividad y sus filas de actividad_colonia se guardan en una sola transacción: si falla
 * cualquier parte (por ejemplo, una colonia que no existe) no queda nada a medias.
 * Tipo, estado, cuadrilla y colonias llegan como nombres en el DTO y se traducen a sus ID.
 * Las fechas se escriben y leen como LocalDateTime (sin zona horaria) para que la BD guarde
 * exactamente la hora que captura el usuario.
 */
public class SqlActividadRepository extends SqlRepositorioBase implements IActividadRepository {

    private static final String CONSULTA_BASE = "SELECT a.ID_ACTIVIDAD, a.ID_CUADRILLA, c.NOMBRE AS CUADRILLA, "
            + "t.NOMBRE AS TIPO, e.NOMBRE_ESTADO AS ESTADO, a.TITULO, a.LUGAR_ESPECIFICO, a.DESCRIPCION, "
            + "COALESCE(a.FECHA_PROGRAMADA, TIMESTAMP(a.FECHA_ACTIVIDAD, COALESCE(a.HORA_INICIO, '00:00:00'))) AS PROGRAMADA, "
            + "a.FECHA_REALIZACION "
            + "FROM actividades a "
            + "JOIN cuadrillas c ON c.ID_CUADRILLA = a.ID_CUADRILLA "
            + "JOIN tipo_actividad t ON t.ID_TIPO_ACTIVIDAD = a.ID_TIPO_ACTIVIDAD "
            + "JOIN estados_actividad e ON e.ID_ESTADO_ACTIVIDAD = a.ID_ESTADO_ACTIVIDAD ";

    /** Fila de actividades sin sus colonias, que se agregan después. */
    private record FilaActividad(int id, int idCuadrilla, String cuadrilla, String tipo, String estado, String titulo,
                                 String lugar, String descripcion, LocalDateTime programada, LocalDateTime realizada) {
    }

    private static FilaActividad mapear(ResultSet rs) throws SQLException {
        return new FilaActividad(rs.getInt("ID_ACTIVIDAD"), rs.getInt("ID_CUADRILLA"), rs.getString("CUADRILLA"),
                rs.getString("TIPO"), rs.getString("ESTADO"), rs.getString("TITULO"), rs.getString("LUGAR_ESPECIFICO"),
                rs.getString("DESCRIPCION"), rs.getObject("PROGRAMADA", LocalDateTime.class),
                rs.getObject("FECHA_REALIZACION", LocalDateTime.class));
    }

    // =====================  consultas  =====================

    @Override
    public List<ActividadDetalleDTO> listar(String estado) {
        String sql = CONSULTA_BASE + (estado == null ? "" : "WHERE e.NOMBRE_ESTADO = ? ") + "ORDER BY a.ID_ACTIVIDAD";
        Object[] params = estado == null ? new Object[0] : new Object[]{estado};
        List<FilaActividad> filas = consultar(sql, SqlActividadRepository::mapear, params);
        Map<Integer, List<String>> colonias = coloniasPorActividad(null);
        return filas.stream().map(f -> aDto(f, colonias.getOrDefault(f.id(), List.of()))).toList();
    }

    @Override
    public ActividadDetalleDTO buscarPorId(int idActividad) {
        return consultarUno(CONSULTA_BASE + "WHERE a.ID_ACTIVIDAD = ?", SqlActividadRepository::mapear, idActividad)
                .map(f -> aDto(f, coloniasPorActividad(idActividad).getOrDefault(idActividad, List.of())))
                .orElse(null);
    }

    @Override
    public List<String> listarTiposActividad() {
        return consultar("SELECT NOMBRE FROM tipo_actividad ORDER BY NOMBRE", rs -> rs.getString(1));
    }

    /** Nombres de colonias por actividad; con idActividad null trae las de todas. */
    private Map<Integer, List<String>> coloniasPorActividad(Integer idActividad) {
        String sql = "SELECT ac.ID_ACTIVIDAD, co.NOMBRE FROM actividad_colonia ac "
                + "JOIN colonias co ON co.ID_COLONIA = ac.ID_COLONIA "
                + (idActividad == null ? "" : "WHERE ac.ID_ACTIVIDAD = ? ")
                + "ORDER BY ac.ID_ACTIVIDAD_COLONIA";
        Object[] params = idActividad == null ? new Object[0] : new Object[]{idActividad};
        Map<Integer, List<String>> resultado = new LinkedHashMap<>();
        for (Object[] fila : consultar(sql, rs -> new Object[]{rs.getInt(1), rs.getString(2)}, params)) {
            resultado.computeIfAbsent((Integer) fila[0], k -> new ArrayList<>()).add((String) fila[1]);
        }
        return resultado;
    }

    private static ActividadDetalleDTO aDto(FilaActividad f, List<String> colonias) {
        return new ActividadDetalleDTO(f.id(), f.idCuadrilla(), f.cuadrilla(), colonias, f.tipo(), f.lugar(), f.estado(),
                f.programada(), f.descripcion(), f.realizada(), f.titulo());
    }

    // =====================  escritura (en transacción)  =====================

    @Override
    public int guardar(ActividadDetalleDTO a) {
        return enTransaccion(con -> {
            Ids ids = resolverIds(con, a);
            int id = insert(con, "INSERT INTO actividades (ID_TIPO_ACTIVIDAD, ID_ESTADO_ACTIVIDAD, ID_CUADRILLA, TITULO, "
                            + "DESCRIPCION, FECHA_ACTIVIDAD, HORA_INICIO, HORA_FIN, FECHA_PROGRAMADA, FECHA_REALIZACION, "
                            + "LUGAR_ESPECIFICO) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    ids.tipo(), ids.estado(), ids.cuadrilla(), a.getTitulo(), a.getObservaciones(),
                    a.getFechaHora().toLocalDate(), a.getFechaHora().toLocalTime(), horaFin(a),
                    a.getFechaHora(), a.getFechaRealizacion(), a.getLugarEspecifico());
            insertarColonias(con, id, ids.colonias());
            return id;
        });
    }

    @Override
    public void actualizar(ActividadDetalleDTO a) {
        enTransaccion(con -> {
            Ids ids = resolverIds(con, a);
            // El título se conserva: se fija al crear la actividad (o viene de los datos de prueba).
            int filas = update(con, "UPDATE actividades SET ID_TIPO_ACTIVIDAD = ?, ID_ESTADO_ACTIVIDAD = ?, ID_CUADRILLA = ?, "
                            + "TITULO = COALESCE(TITULO, ?), DESCRIPCION = ?, FECHA_ACTIVIDAD = ?, HORA_INICIO = ?, "
                            + "HORA_FIN = ?, FECHA_PROGRAMADA = ?, FECHA_REALIZACION = ?, LUGAR_ESPECIFICO = ? "
                            + "WHERE ID_ACTIVIDAD = ?",
                    ids.tipo(), ids.estado(), ids.cuadrilla(), a.getTitulo(), a.getObservaciones(),
                    a.getFechaHora().toLocalDate(), a.getFechaHora().toLocalTime(), horaFin(a),
                    a.getFechaHora(), a.getFechaRealizacion(), a.getLugarEspecifico(), a.getIdActividad());
            if (filas == 0) {
                throw new ReglaNegocioException("La actividad que intentas modificar ya no existe.");
            }
            // Se reemplazan todas las colonias para no dejar filas huérfanas de la versión anterior.
            update(con, "DELETE FROM actividad_colonia WHERE ID_ACTIVIDAD = ?", a.getIdActividad());
            insertarColonias(con, a.getIdActividad(), ids.colonias());
            return filas;
        });
    }

    /**
     * Sus filas de actividad_colonia y evidencia se borran solas por ON DELETE CASCADE (db/schema.sql).
     */
    @Override
    public void eliminar(int idActividad) {
        ejecutar("DELETE FROM actividades WHERE ID_ACTIVIDAD = ?", idActividad);
    }

    private static Object horaFin(ActividadDetalleDTO a) {
        return a.getFechaRealizacion() == null ? null : a.getFechaRealizacion().toLocalTime();
    }

    private static void insertarColonias(Connection con, int idActividad, List<Integer> idsColonias) throws SQLException {
        for (int idColonia : idsColonias) {
            insert(con, "INSERT INTO actividad_colonia (ID_ACTIVIDAD, ID_COLONIA) VALUES (?, ?)", idActividad, idColonia);
        }
    }

    // =====================  traducción de nombres a ID  =====================

    private record Ids(int tipo, int estado, int cuadrilla, List<Integer> colonias) {
    }

    private static Ids resolverIds(Connection con, ActividadDetalleDTO a) throws SQLException {
        int tipo = idPorNombre(con, "SELECT ID_TIPO_ACTIVIDAD FROM tipo_actividad WHERE NOMBRE = ? ORDER BY ID_TIPO_ACTIVIDAD",
                a.getTipoLimpieza(), "El tipo de actividad \"" + a.getTipoLimpieza() + "\" no existe en el catálogo.");
        int estado = idPorNombre(con, "SELECT ID_ESTADO_ACTIVIDAD FROM estados_actividad WHERE NOMBRE_ESTADO = ? "
                + "ORDER BY ID_ESTADO_ACTIVIDAD", a.getEstado(), "El estado \"" + a.getEstado() + "\" no existe en el catálogo.");
        int cuadrilla = idPorNombre(con, "SELECT ID_CUADRILLA FROM cuadrillas WHERE NOMBRE = ? ORDER BY ID_CUADRILLA",
                a.getNombreCuadrilla(), "La cuadrilla \"" + a.getNombreCuadrilla() + "\" no existe en la base de datos.");

        // TODO(#18): si hay colonias homónimas (mismo nombre, distinto CP) se toma la de menor ID,
        // porque ActividadDetalleDTO solo trae nombres. Lo ideal es que el DTO traiga el ID de cada colonia.
        List<Integer> colonias = new ArrayList<>();
        for (String nombre : a.getColonias()) {
            int idColonia = idPorNombre(con, "SELECT ID_COLONIA FROM colonias WHERE NOMBRE = ? ORDER BY ID_COLONIA",
                    nombre, "La colonia \"" + nombre + "\" no existe en el catálogo.");
            if (!colonias.contains(idColonia)) {
                colonias.add(idColonia);
            }
        }
        return new Ids(tipo, estado, cuadrilla, colonias);
    }

    private static int idPorNombre(Connection con, String sql, String nombre, String mensajeSiNoExiste) throws SQLException {
        List<Integer> ids = select(con, sql, rs -> rs.getInt(1), nombre);
        if (ids.isEmpty()) {
            throw new ReglaNegocioException(mensajeSiNoExiste);
        }
        return ids.get(0);
    }

    // =====================  transacción  =====================

    @FunctionalInterface
    private interface Transaccion<T> {
        T ejecutar(Connection con) throws SQLException;
    }

    /**
     * Ejecuta el bloque en una transacción sobre la conexión compartida de ConexionBD: commit si todo sale bien,
     * rollback si algo falla. Siempre regresa la conexión a autoCommit y nunca la cierra.
     */
    private <T> T enTransaccion(Transaccion<T> bloque) {
        Connection con = null;
        try {
            con = con();
            con.setAutoCommit(false);
            T resultado = bloque.ejecutar(con);
            con.commit();
            return resultado;
        } catch (SQLException e) {
            rollback(con);
            throw new RepositorioException("No se pudo guardar la actividad en la base de datos.", e);
        } catch (RuntimeException e) {
            rollback(con);
            throw e;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
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
                // no hay nada más que deshacer
            }
        }
    }
}
