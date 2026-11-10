package com.saltillo.limpieza.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Conexión única a MySQL para toda la aplicación (patrón Singleton).
 *
 * Issue #3 - ConexionBD Singleton e interfaces de catálogos.
 *
 * - getInstance() devuelve siempre la misma instancia.
 * - Lee la configuración desde config/db.properties (relativa a la carpeta del proyecto).
 * - cerrar() libera la conexión; la siguiente llamada a getConexion() la vuelve a abrir
 *   (por ejemplo, al iniciar sesión otra vez).
 *
 * Uso desde un repositorio:
 *     Connection con = ConexionBD.getInstance().getConexion();
 * Los repositorios NO deben cerrar la Connection; solo sus PreparedStatement / ResultSet.
 */
public final class ConexionBD {

    /** Ruta del archivo real de configuración (no se sube a Git). */
    public static final String RUTA_CONFIG = "config/db.properties";

    private static ConexionBD instance;

    private final String url;
    private final String usuario;
    private final String password;
    private Connection conexion;

    private ConexionBD() {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(RUTA_CONFIG)) {
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + RUTA_CONFIG
                    + ". Copia config/db.properties.example como " + RUTA_CONFIG
                    + " y captura tus datos de MySQL.", e);
        }
        this.url = requerida(props, "db.url");
        this.usuario = requerida(props, "db.user");
        this.password = props.getProperty("db.password", "");
    }

    private static String requerida(Properties props, String clave) {
        String valor = props.getProperty(clave);
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalStateException("Falta la propiedad '" + clave + "' en " + RUTA_CONFIG);
        }
        return valor.trim();
    }

    /** Acceso Singleton sincronizado: siempre devuelve la misma instancia. */
    public static synchronized ConexionBD getInstance() {
        if (instance == null) {
            instance = new ConexionBD();
        }
        return instance;
    }

    /**
     * Devuelve la conexión activa; si no existe, está cerrada o dejó de ser válida,
     * abre una nueva con los datos de db.properties.
     */
    public synchronized Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed() || !conexion.isValid(2)) {
            conexion = DriverManager.getConnection(url, usuario, password);
        }
        return conexion;
    }

    /** Libera la conexión. Se puede volver a abrir llamando de nuevo a getConexion(). */
    public synchronized void cerrar() {
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
                System.err.println("[ConexionBD] Error al cerrar la conexión: " + e.getMessage());
            } finally {
                conexion = null;
            }
        }
    }
}
