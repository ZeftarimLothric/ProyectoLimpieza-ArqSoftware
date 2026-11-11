package com.saltillo.limpieza.test;

import com.saltillo.limpieza.database.ConexionBD;
import com.saltillo.limpieza.models.dto.ActividadDetalleDTO;
import com.saltillo.limpieza.repositories.ReglaNegocioException;
import com.saltillo.limpieza.repositories.SqlActividadRepository;
import com.saltillo.limpieza.repositories.SqlEvidenciaRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Prueba de integración de actividades y evidencias contra MySQL (Integrante 3 - Issue #18).
 *
 * Necesita config/db.properties, el driver de MySQL en el classpath y la BD cargada con db/schema.sql
 * y los datos de prueba (cuadrillas, tipos y colonias). Si falta algo se OMITE en lugar de fallar.
 * Todo lo que crea lo borra al final.
 */
public class SqlActividadIntegracionTest {

    public static void main(String[] args) throws Exception {
        System.out.println("===================================================================");
        System.out.println("INTEGRACION MYSQL DE ACTIVIDADES Y EVIDENCIAS - INTEGRANTE 3 (ISSUE #18)");
        System.out.println("===================================================================");

        if (!Files.exists(Path.of(ConexionBD.RUTA_CONFIG))) {
            omitir("no existe " + ConexionBD.RUTA_CONFIG);
            return;
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            omitir("falta el driver de MySQL en el classpath");
            return;
        }

        Connection con = ConexionBD.getInstance().getConexion();
        List<String> cuadrillas = textos(con, "SELECT NOMBRE FROM cuadrillas ORDER BY ID_CUADRILLA LIMIT 1");
        List<String> colonias = textos(con, "SELECT NOMBRE FROM colonias GROUP BY NOMBRE HAVING COUNT(*) = 1 ORDER BY NOMBRE LIMIT 3");
        SqlActividadRepository repo = new SqlActividadRepository();
        SqlEvidenciaRepository evidencias = new SqlEvidenciaRepository();
        List<String> tipos = repo.listarTiposActividad();
        if (cuadrillas.isEmpty() || colonias.size() < 3 || tipos.isEmpty()) {
            omitir("la BD no tiene cuadrillas, tipos y al menos 3 colonias (carga los datos de prueba)");
            return;
        }

        String cuadrilla = cuadrillas.get(0);
        String tipo = tipos.get(0);
        String lugar = "Prueba integración #18 - " + System.currentTimeMillis();
        LocalDateTime programada = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.MINUTES);
        int id = 0;
        try {
            // 1. Crear: la actividad y sus dos colonias en una transacción
            System.out.print("[TEST 1] Crear actividad con colonias... ");
            id = repo.guardar(dto(0, cuadrilla, List.of(colonias.get(0), colonias.get(1)), tipo, lugar,
                    "PROGRAMADA", programada, null, "Alta desde la prueba"));
            ActividadDetalleDTO creada = repo.buscarPorId(id);
            assert creada != null && id > 0 : "La actividad debe existir en la BD";
            assert cuadrilla.equals(creada.getNombreCuadrilla()) && tipo.equals(creada.getTipoLimpieza()) : "Cuadrilla y tipo";
            assert "PROGRAMADA".equals(creada.getEstado()) && programada.equals(creada.getFechaHora()) : "Estado y fecha";
            assert "Alta desde la prueba".equals(creada.getObservaciones()) : "Descripción";
            assert creada.getColonias().equals(List.of(colonias.get(0), colonias.get(1))) : "Colonias: " + creada.getColonias();
            assert contar(con, "SELECT COUNT(*) FROM actividad_colonia WHERE ID_ACTIVIDAD = ?", id) == 2 : "Dos filas en actividad_colonia";
            assert con.getAutoCommit() : "La conexión debe regresar a autoCommit";
            System.out.println("PASO (actividad #" + id + " con 2 colonias)");

            // 2. Editar cambiando colonias: no deben quedar filas huérfanas
            System.out.print("[TEST 2] Editar cambiando colonias y estado... ");
            LocalDateTime realizada = programada.plusHours(3);
            repo.actualizar(dto(id, cuadrilla, List.of(colonias.get(1), colonias.get(2)), tipo, lugar,
                    "COMPLETADA", programada, realizada, "Terminada"));
            ActividadDetalleDTO editada = repo.buscarPorId(id);
            assert editada.getColonias().equals(List.of(colonias.get(1), colonias.get(2))) : "Colonias nuevas: " + editada.getColonias();
            assert contar(con, "SELECT COUNT(*) FROM actividad_colonia WHERE ID_ACTIVIDAD = ?", id) == 2 : "Sin filas huérfanas";
            assert "COMPLETADA".equals(editada.getEstado()) && realizada.equals(editada.getFechaRealizacion()) : "Estado y realización";
            assert creada.getTitulo().equals(editada.getTitulo()) : "El título se conserva al editar";
            System.out.println("PASO (colonias reemplazadas, 2 filas)");

            // 3. Listar con y sin filtro de estado
            System.out.print("[TEST 3] Listar y filtrar por estado... ");
            final int idCreada = id;
            assert repo.listar(null).stream().anyMatch(a -> a.getIdActividad() == idCreada) : "Debe aparecer sin filtro";
            assert repo.listar("COMPLETADA").stream().anyMatch(a -> a.getIdActividad() == idCreada) : "Debe aparecer en COMPLETADA";
            assert repo.listar("PROGRAMADA").stream().noneMatch(a -> a.getIdActividad() == idCreada) : "No debe aparecer en PROGRAMADA";
            System.out.println("PASO (" + repo.listar(null).size() + " actividades en la BD)");

            // 4. Evidencias: solo el nombre del archivo en RUTA_IMAGEN
            System.out.print("[TEST 4] Guardar y reemplazar evidencias... ");
            evidencias.reemplazar(id, List.of("a1b2.png", "c3d4.jpg"));
            assert evidencias.listarPorActividad(id).equals(List.of("a1b2.png", "c3d4.jpg")) : "Dos evidencias";
            evidencias.reemplazar(id, List.of("c3d4.jpg", "e5f6.png"));
            assert evidencias.listarPorActividad(id).equals(List.of("c3d4.jpg", "e5f6.png")) : "Se quita una y se agrega otra";
            assert con.getAutoCommit() : "La conexión debe regresar a autoCommit";
            System.out.println("PASO (nombres de archivo, sin rutas)");

            // 5. Rollback: si una colonia no existe no se guarda nada
            System.out.print("[TEST 5] Rollback con datos inexistentes... ");
            int antes = contar(con, "SELECT COUNT(*) FROM actividades");
            esperarError(() -> repo.guardar(dto(0, cuadrilla, List.of(colonias.get(0), "Colonia Inexistente XYZ"), tipo,
                    lugar + " (no debe guardarse)", "PROGRAMADA", programada, null, "")), "La colonia \"Colonia Inexistente XYZ\" no existe en el catálogo.");
            esperarError(() -> repo.guardar(dto(0, "Cuadrilla Inexistente XYZ", List.of(colonias.get(0)), tipo,
                    lugar + " (no debe guardarse)", "PROGRAMADA", programada, null, "")), "La cuadrilla \"Cuadrilla Inexistente XYZ\" no existe en la base de datos.");
            assert contar(con, "SELECT COUNT(*) FROM actividades") == antes : "El rollback no debe dejar actividades a medias";
            assert con.getAutoCommit() : "La conexión debe regresar a autoCommit tras el rollback";
            System.out.println("PASO (nada guardado y errores claros)");

            // 6. Eliminar: ON DELETE CASCADE borra colonias y evidencias
            System.out.print("[TEST 6] Eliminar con borrado en cascada... ");
            repo.eliminar(id);
            assert repo.buscarPorId(id) == null : "La actividad debe borrarse";
            assert contar(con, "SELECT COUNT(*) FROM actividad_colonia WHERE ID_ACTIVIDAD = ?", id) == 0 : "Sin colonias huérfanas";
            assert contar(con, "SELECT COUNT(*) FROM evidencia WHERE ID_ACTIVIDAD = ?", id) == 0 : "Sin evidencias huérfanas";
            id = 0;
            System.out.println("PASO (actividad_colonia y evidencia en 0)");
        } finally {
            if (id > 0) {
                repo.eliminar(id);
            }
            ConexionBD.getInstance().cerrar();
        }

        System.out.println("\n>>> TODAS LAS PRUEBAS DE INTEGRACION MYSQL PASARON CON EXITO <<<");
    }

    private static ActividadDetalleDTO dto(int id, String cuadrilla, List<String> colonias, String tipo, String lugar,
                                           String estado, LocalDateTime programada, LocalDateTime realizada, String descripcion) {
        return new ActividadDetalleDTO(id, 0, cuadrilla, colonias, tipo, lugar, estado, programada, descripcion, realizada);
    }

    private static void esperarError(Runnable accion, String mensaje) {
        try {
            accion.run();
            assert false : "Debió fallar: " + mensaje;
        } catch (ReglaNegocioException e) {
            assert mensaje.equals(e.getMessage()) : "Mensaje inesperado: " + e.getMessage();
        }
    }

    private static List<String> textos(Connection con, String sql) throws SQLException {
        List<String> lista = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(rs.getString(1));
            }
        }
        return lista;
    }

    private static int contar(Connection con, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private static void omitir(String motivo) {
        System.out.println("[OMITIDA] Prueba de integración MySQL: " + motivo + ".");
    }
}
