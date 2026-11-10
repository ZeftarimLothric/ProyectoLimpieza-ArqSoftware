package com.saltillo.limpieza.test;

import com.saltillo.limpieza.controllers.catalogos.ColoniaController;
import com.saltillo.limpieza.controllers.catalogos.EmpleadoController;
import com.saltillo.limpieza.controllers.catalogos.TipoActividadController;
import com.saltillo.limpieza.controllers.catalogos.UsuarioController;
import com.saltillo.limpieza.repositories.SqlColoniaRepository;
import com.saltillo.limpieza.repositories.SqlEmpleadoRepository;
import com.saltillo.limpieza.repositories.SqlTipoActividadRepository;
import com.saltillo.limpieza.repositories.SqlUsuarioRepository;
import com.saltillo.limpieza.views.catalogos.CatalogosView;
import com.saltillo.limpieza.views.catalogos.ColoniaView;
import com.saltillo.limpieza.views.catalogos.EmpleadoView;
import com.saltillo.limpieza.views.catalogos.TipoActividadView;
import com.saltillo.limpieza.views.catalogos.UsuarioView;

import javax.swing.*;

/**
 * Prueba manual del Issue #8: abre los 4 catálogos (usuarios, empleados, colonias, tipos de
 * actividad) conectados a MySQL. Requiere config/db.properties y la BD cargada.
 * Este es también el "Composition Root" de ejemplo para registrarlos en el Shell (Issue #19).
 */
public class CatalogosDemo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            UsuarioView usuarios = new UsuarioView();
            EmpleadoView empleados = new EmpleadoView();
            ColoniaView colonias = new ColoniaView();
            TipoActividadView tipos = new TipoActividadView();

            new UsuarioController(usuarios, new SqlUsuarioRepository());
            new EmpleadoController(empleados, new SqlEmpleadoRepository());
            new ColoniaController(colonias, new SqlColoniaRepository());
            new TipoActividadController(tipos, new SqlTipoActividadRepository());

            CatalogosView catalogos = new CatalogosView();
            catalogos.agregarCatalogo("Usuarios", usuarios);
            catalogos.agregarCatalogo("Empleados", empleados);
            catalogos.agregarCatalogo("Colonias", colonias);
            catalogos.agregarCatalogo("Tipos de actividad", tipos);

            JFrame ventana = new JFrame("Catálogos • Gestión de Actividades de Limpieza");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setContentPane(catalogos);
            ventana.setSize(1180, 720);
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}
