
package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.database.ConexionBD;
import com.saltillo.limpieza.models.dto.*;
import com.saltillo.limpieza.patterns.observer.ActividadEventBus;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Issue #17 - Repositorio MySQL del Dashboard.
 *
 * Utiliza la conexión Singleton y las utilidades de
 * SqlRepositorioBase. No cierra la conexión compartida.
 */
public class SqlDashboardRepository extends SqlRepositorioBase
        implements IDashboardRepository {

    // Meta provisional para demostración.
    // El esquema actual no almacena metas por cuadrilla.
    private static final int META_DEMOSTRACION = 10;

    /**
     * Calcula los indicadores globales utilizando los
     * registros reales de las cuadrillas y actividades.
     */
    @Override
    public DashboardKpisDTO obtenerKpis() {

        List<CuadrillaResumenDTO> resumen =
                obtenerResumenPorCuadrillas();

        int totalActividades = 0;
        int disponibles = 0;
        int maxActividades = 0;
        String cuadrillaLider = "Ninguna";

        for (CuadrillaResumenDTO cuadrilla : resumen) {

            int realizadas = cuadrilla.getTotalActividades();
            totalActividades += realizadas;

            if ("DISPONIBLE".equalsIgnoreCase(
                    cuadrilla.getEstado())) {
                disponibles++;
            }

            if (realizadas > maxActividades) {
                maxActividades = realizadas;
                cuadrillaLider = cuadrilla.getNombreCuadrilla();
            }
        }

        int totalCuadrillas = resumen.size();

        double promedio = totalCuadrillas == 0
                ? 0.0
                : (double) totalActividades / totalCuadrillas;

        return new DashboardKpisDTO(
                totalActividades,
                cuadrillaLider,
                maxActividades,
                disponibles,
                totalCuadrillas,
                promedio,
                LocalDateTime.now()
        );
    }

    /**
     * Obtiene el desempeño de cada cuadrilla.
     *
     * Cuenta únicamente actividades COMPLETADAS.
     * La zona se aproxima mediante colonias atendidas.
     */
    @Override
    public List<CuadrillaResumenDTO>
            obtenerResumenPorCuadrillas() {

        String sql = """
            SELECT
                c.ID_CUADRILLA,
                c.NOMBRE AS NOMBRE_CUADRILLA,
                c.ACTIVO,

                COALESCE((
                    SELECT CONCAT_WS(' ',
                        emp.NOMBRE,
                        emp.APELLIDO_PATERNO,
                        emp.APELLIDO_MATERNO)
                    FROM cuadrilla_empleado ce
                    INNER JOIN empleados emp
                        ON emp.ID_EMPLEADO = ce.ID_EMPLEADO
                    WHERE ce.ID_CUADRILLA = c.ID_CUADRILLA
                      AND ce.ES_JEFE = 1
                    ORDER BY ce.ID_CUADRILLA_EMPLEADO
                    LIMIT 1
                ), 'Sin jefe asignado') AS JEFE,

                COALESCE((
                    SELECT GROUP_CONCAT(
                        DISTINCT col.NOMBRE
                        ORDER BY col.NOMBRE
                        SEPARATOR ', ')
                    FROM actividades act
                    INNER JOIN actividad_colonia ac
                        ON ac.ID_ACTIVIDAD = act.ID_ACTIVIDAD
                    INNER JOIN colonias col
                        ON col.ID_COLONIA = ac.ID_COLONIA
                    WHERE act.ID_CUADRILLA = c.ID_CUADRILLA
                ), 'Sin colonias registradas') AS ZONA,

                COALESCE((
                    SELECT COUNT(*)
                    FROM actividades act
                    INNER JOIN estados_actividad est
                        ON est.ID_ESTADO_ACTIVIDAD =
                           act.ID_ESTADO_ACTIVIDAD
                    WHERE act.ID_CUADRILLA = c.ID_CUADRILLA
                      AND UPPER(est.NOMBRE_ESTADO) = 'COMPLETADA'
                ), 0) AS REALIZADAS,

                COALESCE((
                    SELECT COUNT(*)
                    FROM actividades act
                    INNER JOIN estados_actividad est
                        ON est.ID_ESTADO_ACTIVIDAD =
                           act.ID_ESTADO_ACTIVIDAD
                    WHERE act.ID_CUADRILLA = c.ID_CUADRILLA
                      AND UPPER(est.NOMBRE_ESTADO) = 'EN PROCESO'
                ), 0) AS EN_PROCESO

            FROM cuadrillas c
            ORDER BY c.ID_CUADRILLA
            """;

        List<CuadrillaResumenDTO> resultado =
                consultar(sql, rs -> {

                    String estado;

                    if (rs.getInt("ACTIVO") != 1) {
                        estado = "EN ESPERA";
                    } else if (rs.getInt("EN_PROCESO") > 0) {
                        estado = "EN OPERACION";
                    } else {
                        estado = "DISPONIBLE";
                    }

                    return new CuadrillaResumenDTO(
                            rs.getInt("ID_CUADRILLA"),
                            rs.getString("NOMBRE_CUADRILLA"),
                            rs.getString("JEFE"),
                            rs.getString("ZONA"),
                            rs.getInt("REALIZADAS"),
                            META_DEMOSTRACION,
                            estado
                    );
                });

        return Collections.unmodifiableList(resultado);
    }

    /**
     * Consulta actividades aplicando filtros opcionales.
     * EXISTS evita duplicar actividades al filtrar colonias.
     */
    @Override
    public List<ActividadDetalleDTO>
            buscarActividades(FiltroActividadDTO filtro) {

        StringBuilder sql = new StringBuilder("""
            SELECT
                a.ID_ACTIVIDAD,
                a.ID_CUADRILLA,
                c.NOMBRE AS CUADRILLA,
                t.NOMBRE AS TIPO,
                e.NOMBRE_ESTADO AS ESTADO,
                a.TITULO,
                a.LUGAR_ESPECIFICO,
                a.OBSERVACIONES,
                COALESCE(
                    a.FECHA_PROGRAMADA,
                    TIMESTAMP(a.FECHA_ACTIVIDAD, a.HORA_INICIO),
                    CAST(a.FECHA_ACTIVIDAD AS DATETIME),
                    a.FECHA_REALIZACION
                ) AS FECHA_HORA,
                a.FECHA_REALIZACION,

                (
                    SELECT GROUP_CONCAT(
                        DISTINCT col.NOMBRE
                        ORDER BY col.NOMBRE
                        SEPARATOR '|||'
                    )
                    FROM actividad_colonia ac
                    INNER JOIN colonias col
                        ON col.ID_COLONIA = ac.ID_COLONIA
                    WHERE ac.ID_ACTIVIDAD = a.ID_ACTIVIDAD
                ) AS COLONIAS

            FROM actividades a

            INNER JOIN cuadrillas c
                ON c.ID_CUADRILLA = a.ID_CUADRILLA

            INNER JOIN tipo_actividad t
                ON t.ID_TIPO_ACTIVIDAD = a.ID_TIPO_ACTIVIDAD

            INNER JOIN estados_actividad e
                ON e.ID_ESTADO_ACTIVIDAD = a.ID_ESTADO_ACTIVIDAD

            WHERE 1 = 1
            """);

        List<Object> parametros = new ArrayList<>();

        if (filtro != null) {

            if (filtro.tieneFiltroCuadrilla()) {
                sql.append(" AND a.ID_CUADRILLA = ?");
                parametros.add(filtro.getIdCuadrilla());
            }

            if (filtro.tieneFiltroColonia()) {
                sql.append("""
                     AND EXISTS (
                         SELECT 1
                         FROM actividad_colonia acf
                         INNER JOIN colonias cf
                             ON cf.ID_COLONIA = acf.ID_COLONIA
                         WHERE acf.ID_ACTIVIDAD = a.ID_ACTIVIDAD
                           AND LOWER(cf.NOMBRE) LIKE LOWER(?)
                     )
                    """);

                parametros.add("%"
                        + escaparLike(filtro.getColonia())
                        + "%");
            }

            if (filtro.tieneFiltroEstado()) {
                sql.append(
                        " AND UPPER(e.NOMBRE_ESTADO) = UPPER(?)");
                parametros.add(filtro.getEstado());
            }

            String fechaSql = """
                DATE(COALESCE(
                    a.FECHA_PROGRAMADA,
                    CAST(a.FECHA_ACTIVIDAD AS DATETIME),
                    a.FECHA_REALIZACION
                ))
                """;

            if (filtro.tieneFiltroFechaInicio()) {
                sql.append(" AND ")
                   .append(fechaSql)
                   .append(" >= ?");
                parametros.add(filtro.getFechaInicio());
            }

            if (filtro.tieneFiltroFechaFin()) {
                sql.append(" AND ")
                   .append(fechaSql)
                   .append(" <= ?");
                parametros.add(filtro.getFechaFin());
            }
        }

        sql.append(" ORDER BY FECHA_HORA DESC, a.ID_ACTIVIDAD DESC");

        List<ActividadDetalleDTO> resultado = consultar(
                sql.toString(),
                rs -> {

                    List<String> colonias = new ArrayList<>();
                    String textoColonias = rs.getString("COLONIAS");

                    if (textoColonias != null
                            && !textoColonias.isBlank()) {

                        for (String nombre :
                                textoColonias.split("\\|\\|\\|")) {
                            colonias.add(nombre);
                        }
                    }

                    Timestamp fecha =
                            rs.getTimestamp("FECHA_HORA");

                    Timestamp realizacion =
                            rs.getTimestamp("FECHA_REALIZACION");

                    LocalDateTime fechaHora = fecha == null
                            ? null
                            : fecha.toLocalDateTime();

                    LocalDateTime fechaRealizacion =
                            realizacion == null
                                    ? null
                                    : realizacion.toLocalDateTime();

                    return new ActividadDetalleDTO(
                            rs.getInt("ID_ACTIVIDAD"),
                            rs.getInt("ID_CUADRILLA"),
                            rs.getString("CUADRILLA"),
                            colonias,
                            rs.getString("TIPO"),
                            rs.getString("LUGAR_ESPECIFICO"),
                            rs.getString("ESTADO"),
                            fechaHora,
                            rs.getString("OBSERVACIONES"),
                            fechaRealizacion,
                            rs.getString("TITULO")
                    );
                },
                parametros.toArray()
        );

        return Collections.unmodifiableList(resultado);
    }

    /**
     * Registra una actividad y sus colonias en una transacción.
     * El evento Observer se publica después del COMMIT.
     */
    @Override
    public void registrarActividad(
            int idCuadrilla,
            List<String> colonias,
            String tipoLimpieza,
            String observaciones) {

        if (idCuadrilla <= 0) {
            throw new IllegalArgumentException(
                    "Debes seleccionar una cuadrilla válida.");
        }

        if (tipoLimpieza == null || tipoLimpieza.isBlank()) {
            throw new IllegalArgumentException(
                    "Debes indicar el tipo de limpieza.");
        }

        if (colonias == null || colonias.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debes seleccionar al menos una colonia.");
        }

        Set<String> coloniasUnicas = new LinkedHashSet<>();

        for (String colonia : colonias) {
            if (colonia != null && !colonia.isBlank()) {
                coloniasUnicas.add(colonia.trim());
            }
        }

        if (coloniasUnicas.isEmpty()) {
            throw new IllegalArgumentException(
                    "Las colonias no pueden estar vacías.");
        }

        List<String> nombresColonias =
                new ArrayList<>(coloniasUnicas);

        Connection conexion;
        boolean autoCommitOriginal = true;
        boolean transaccionIniciada = false;
        String nombreCuadrilla;

        try {
            conexion = con();
            autoCommitOriginal = conexion.getAutoCommit();

            conexion.setAutoCommit(false);
            transaccionIniciada = true;

            try {
                nombreCuadrilla = buscarNombreCuadrilla(
                        conexion, idCuadrilla);

                int idTipo = buscarIdUnico(
                        conexion,
                        """
                        SELECT ID_TIPO_ACTIVIDAD
                        FROM tipo_actividad
                        WHERE LOWER(NOMBRE) = LOWER(?)
                        LIMIT 2
                        """,
                        tipoLimpieza.trim(),
                        "tipo de actividad"
                );

                int idEstado = buscarIdUnico(
                        conexion,
                        """
                        SELECT ID_ESTADO_ACTIVIDAD
                        FROM estados_actividad
                        WHERE UPPER(NOMBRE_ESTADO) = 'COMPLETADA'
                        LIMIT 2
                        """,
                        null,
                        "estado COMPLETADA"
                );

                List<Integer> idsColonias = new ArrayList<>();

                for (String colonia : nombresColonias) {
                    idsColonias.add(buscarIdUnico(
                            conexion,
                            """
                            SELECT ID_COLONIA
                            FROM colonias
                            WHERE LOWER(NOMBRE) = LOWER(?)
                            LIMIT 2
                            """,
                            colonia,
                            "colonia " + colonia
                    ));
                }

                LocalDateTime ahora = LocalDateTime.now();
                String lugar = nombresColonias.get(0);
                String titulo = tipoLimpieza.trim();

                if (titulo.length() > 100) {
                    titulo = titulo.substring(0, 100);
                }

                int idActividad = SqlRepositorioBase.insert(
                        conexion,
                        """
                        INSERT INTO actividades (
                            ID_TIPO_ACTIVIDAD,
                            ID_ESTADO_ACTIVIDAD,
                            ID_CUADRILLA,
                            TITULO,
                            DESCRIPCION,
                            FECHA_ACTIVIDAD,
                            HORA_INICIO,
                            FECHA_REALIZACION,
                            LUGAR_ESPECIFICO,
                            OBSERVACIONES
                        )
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                        idTipo,
                        idEstado,
                        idCuadrilla,
                        titulo,
                        "Actividad registrada desde el dashboard",
                        java.sql.Date.valueOf(ahora.toLocalDate()),
                        java.sql.Time.valueOf(ahora.toLocalTime()),
                        Timestamp.valueOf(ahora),
                        lugar,
                        observaciones
                );

                if (idActividad <= 0) {
                    throw new SQLException(
                            "No se obtuvo el ID de la actividad.");
                }

                for (Integer idColonia : idsColonias) {
                    SqlRepositorioBase.insert(
                            conexion,
                            """
                            INSERT INTO actividad_colonia (
                                ID_ACTIVIDAD,
                                ID_COLONIA
                            )
                            VALUES (?, ?)
                            """,
                            idActividad,
                            idColonia
                    );
                }

                conexion.commit();

            } catch (SQLException | RuntimeException e) {
                try {
                    conexion.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            } finally {
                if (transaccionIniciada) {
                    conexion.setAutoCommit(autoCommitOriginal);
                }
            }

        } catch (SQLException e) {
            throw new RepositorioException(
                    "Error al registrar la actividad en MySQL.", e);
        }

        ActividadEventDTO evento = new ActividadEventDTO(
                idCuadrilla,
                nombreCuadrilla,
                nombresColonias,
                tipoLimpieza.trim(),
                observaciones,
                TipoEventoActividad.CREADA
        );

        ActividadEventBus.getInstance().publicarEvento(evento);
    }

    /**
     * Sobrecarga para actividades en una sola colonia.
     */
    @Override
    public void registrarActividad(
            int idCuadrilla,
            String colonia,
            String tipoLimpieza,
            String observaciones) {

        List<String> colonias = new ArrayList<>();

        if (colonia != null && !colonia.isBlank()) {
            colonias.add(colonia.trim());
        }

        registrarActividad(
                idCuadrilla,
                colonias,
                tipoLimpieza,
                observaciones
        );
    }

    /**
     * Recupera los nombres de cuadrillas registrados en MySQL.
     */
    @Override
    public List<String> obtenerNombresCuadrillas() {

        List<String> nombres = consultar(
                """
                SELECT NOMBRE
                FROM cuadrillas
                ORDER BY NOMBRE
                """,
                rs -> rs.getString("NOMBRE")
        );

        return Collections.unmodifiableList(nombres);
    }

    /**
     * Obtiene el ID de una cuadrilla a partir de su nombre.
     * Devuelve 0 cuando no existe.
     */
    @Override
    public int obtenerIdPorNombre(String nombre) {

        if (nombre == null || nombre.isBlank()) {
            return 0;
        }

        return consultarUno(
                """
                SELECT ID_CUADRILLA
                FROM cuadrillas
                WHERE LOWER(NOMBRE) = LOWER(?)
                ORDER BY ID_CUADRILLA
                LIMIT 1
                """,
                rs -> rs.getInt("ID_CUADRILLA"),
                nombre.trim()
        ).orElse(0);
    }

    /**
     * Busca el nombre de una cuadrilla existente.
     */
    private String buscarNombreCuadrilla(
            Connection conexion,
            int idCuadrilla) throws SQLException {

        List<String> nombres = SqlRepositorioBase.select(
                conexion,
                """
                SELECT NOMBRE
                FROM cuadrillas
                WHERE ID_CUADRILLA = ?
                """,
                rs -> rs.getString("NOMBRE"),
                idCuadrilla
        );

        if (nombres.isEmpty()) {
            throw new IllegalArgumentException(
                    "La cuadrilla seleccionada no existe.");
        }

        return nombres.get(0);
    }

    /**
     * Busca un identificador y evita elegir silenciosamente
     * entre registros con nombres duplicados.
     */
    private int buscarIdUnico(
            Connection conexion,
            String sql,
            String parametro,
            String descripcion) throws SQLException {

        List<Integer> ids;

        if (parametro == null) {
            ids = SqlRepositorioBase.select(
                    conexion,
                    sql,
                    rs -> rs.getInt(1)
            );
        } else {
            ids = SqlRepositorioBase.select(
                    conexion,
                    sql,
                    rs -> rs.getInt(1),
                    parametro
            );
        }

        if (ids.isEmpty()) {
            throw new IllegalArgumentException(
                    "No se encontró: " + descripcion);
        }

        if (ids.size() > 1) {
            throw new IllegalArgumentException(
                    "Existen varios registros para: "
                            + descripcion
                            + ". Se requiere una identificación única.");
        }

        return ids.get(0);
    }
}