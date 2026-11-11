package com.saltillo.limpieza.test;

import com.saltillo.limpieza.controllers.catalogos.CuadrillaController;
import com.saltillo.limpieza.repositories.SqlCuadrillaRepository;
import com.saltillo.limpieza.views.catalogos.CuadrillaView;

import javax.swing.*;

/**
 * Prueba manual del Issue #9: abre la pantalla de cuadrillas (jefe e integrantes) conectada a MySQL.
 * Requiere config/db.properties, la BD cargada y empleados dados de alta (Issue #8 o db/seed_catalogos.sql).
 */
public class CuadrillasDemo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            CuadrillaView vista = new CuadrillaView();
            new CuadrillaController(vista, new SqlCuadrillaRepository());

            JFrame ventana = new JFrame("Cuadrillas • Gestión de Actividades de Limpieza");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setContentPane(vista);
            ventana.setSize(1280, 760);
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}
