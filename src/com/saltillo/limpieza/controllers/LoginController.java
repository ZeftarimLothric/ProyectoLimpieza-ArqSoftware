package com.saltillo.limpieza.controllers;

import com.saltillo.limpieza.models.entities.Usuario;
import com.saltillo.limpieza.repositories.IUsuarioRepository;
import com.saltillo.limpieza.security.PasswordHasher;
import com.saltillo.limpieza.session.SesionUsuario;
import com.saltillo.limpieza.views.LoginView;

import java.util.Arrays;
import java.util.Optional;

/**
 * Controlador del login (Issue #7).
 *
 * - Valida el usuario y compara la contraseña contra su hash SHA-256 (nunca texto plano).
 * - Si es correcto: SesionUsuario.getInstance().iniciar(usuario) y ejecuta alAutenticar
 *   (que abre el Shell; el cableado final es el Issue #19).
 * - Si no: muestra un mensaje claro en la vista.
 * Depende del contrato IUsuarioRepository, no de MySQL (inversión de dependencias).
 */
public class LoginController {

    static final String MSG_CAMPOS_VACIOS = "Escribe tu usuario y tu contraseña.";
    static final String MSG_CREDENCIALES = "Usuario o contraseña incorrectos.";
    static final String MSG_ERROR_BD = "No se pudo conectar con la base de datos. Revisa config/db.properties y que MySQL esté encendido.";

    private final LoginView view;
    private final IUsuarioRepository repositorio;
    private final Runnable alAutenticar;

    public LoginController(LoginView view, IUsuarioRepository repositorio, Runnable alAutenticar) {
        this.view = view;
        this.repositorio = repositorio;
        this.alAutenticar = alAutenticar;
        this.view.setLoginListener(e -> intentarLogin());
    }

    void intentarLogin() {
        String username = view.getUsuario().trim();
        char[] contrasena = view.getContrasena();
        try {
            if (username.isEmpty() || contrasena.length == 0) {
                view.mostrarError(MSG_CAMPOS_VACIOS);
                return;
            }

            Optional<Usuario> encontrado = repositorio.buscarPorUsername(username);
            if (!encontrado.isPresent()
                    || !encontrado.get().isActivo()
                    || !PasswordHasher.verificar(new String(contrasena), encontrado.get().getContrasenaHash())) {
                view.mostrarError(MSG_CREDENCIALES); // mismo mensaje: no revela si el usuario existe
                view.limpiarContrasena();
                return;
            }

            Usuario autenticado = encontrado.get();
            autenticado.setContrasenaHash(null); // el hash no debe viajar en la sesión
            view.mostrarError(" ");
            SesionUsuario.getInstance().iniciar(autenticado);
            alAutenticar.run();   // primero se abre el Shell...
            view.dispose();       // ...y después se cierra el login
        } catch (RuntimeException ex) {
            System.err.println("[Login] Error de acceso a datos: " + ex);
            view.mostrarError(MSG_ERROR_BD);
        } finally {
            Arrays.fill(contrasena, '\0');
        }
    }
}
