package com.saltillo.limpieza.test;

import com.saltillo.limpieza.Main;
import com.saltillo.limpieza.controllers.LoginController;
import com.saltillo.limpieza.repositories.SqlUsuarioRepository;
import com.saltillo.limpieza.views.LoginView;

import javax.swing.*;

/**
 * Prueba manual del Issue #7: abre el login contra MySQL y, si las credenciales son correctas,
 * abre el Shell con el usuario real en SesionUsuario.
 * Requiere config/db.properties, la BD cargada y db/seed_admin.sql ejecutado.
 */
public class LoginDemo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            LoginView vista = new LoginView();
            new LoginController(vista, new SqlUsuarioRepository(), () -> Main.main(new String[0]));
            vista.setVisible(true);
        });
    }
}
