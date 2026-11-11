package com.saltillo.limpieza.controllers.catalogos;

import com.saltillo.limpieza.models.entities.Usuario;
import com.saltillo.limpieza.repositories.IUsuarioRepository;
import com.saltillo.limpieza.security.PasswordHasher;
import com.saltillo.limpieza.session.SesionUsuario;
import com.saltillo.limpieza.views.catalogos.UsuarioView;

import java.util.ArrayList;
import java.util.List;

/**
 * CRUD de usuarios (Issue #8). Las contraseñas se guardan siempre con PasswordHasher (SHA-256).
 * Al editar, si la contraseña se deja vacía se conserva la actual.
 */
public class UsuarioController extends CatalogoController<Usuario> {

    static final int MIN_CONTRASENA = 6;

    private final UsuarioView v;
    private final IUsuarioRepository repo;

    public UsuarioController(UsuarioView vista, IUsuarioRepository repositorio) {
        super(vista, repositorio);
        this.v = vista;
        this.repo = repositorio;
        iniciar();
    }

    @Override
    protected Object[] aFila(Usuario u) {
        return new Object[]{u.getIdUsuario(), u.getUsuario(), u.getNombre(), u.isActivo() ? "ACTIVO" : "INACTIVO"};
    }

    @Override
    protected int idDe(Usuario u) {
        return u.getIdUsuario();
    }

    @Override
    protected String descripcion(Usuario u) {
        return u.getUsuario();
    }

    @Override
    protected String nombreEntidad() {
        return "usuario";
    }

    @Override
    protected void mostrarEnFormulario(Usuario u) {
        v.setUsuario(u.getUsuario());
        v.setNombre(u.getNombre());
        v.limpiarContrasena(); // el hash nunca se muestra
        v.setActivo(u.isActivo());
    }

    @Override
    protected List<String> validar(boolean esNuevo) {
        List<String> e = new ArrayList<>();
        String usuario = v.getUsuario().trim();
        String nombre = v.getNombre().trim();
        String contrasena = v.getContrasena();

        if (usuario.isEmpty()) {
            e.add("• El usuario es obligatorio.");
        } else if (usuario.length() > 50) {
            e.add("• El usuario no puede pasar de 50 caracteres.");
        } else if (usuario.contains(" ")) {
            e.add("• El usuario no puede llevar espacios.");
        }
        if (nombre.isEmpty()) {
            e.add("• El nombre completo es obligatorio.");
        } else if (nombre.length() > 100) {
            e.add("• El nombre no puede pasar de 100 caracteres.");
        }
        if (esNuevo && contrasena.isEmpty()) {
            e.add("• La contraseña es obligatoria.");
        } else if (!contrasena.isEmpty() && contrasena.length() < MIN_CONTRASENA) {
            e.add("• La contraseña debe tener al menos " + MIN_CONTRASENA + " caracteres.");
        }
        if (!esNuevo && !v.isActivo() && esCuentaEnUso(getSeleccionado())) {
            e.add("• No puedes desactivar tu propia cuenta mientras tienes la sesión abierta.");
        }
        return e;
    }

    @Override
    protected Usuario leerFormulario(Usuario existente) {
        String contrasena = v.getContrasena();
        return new Usuario(
                existente == null ? 0 : existente.getIdUsuario(),
                v.getNombre().trim(),
                v.getUsuario().trim(),
                contrasena.isEmpty() ? null : PasswordHasher.hash(contrasena), // null = conservar la actual
                "ADMINISTRADOR",
                v.isActivo());
    }

    @Override
    protected String mensajeDuplicado() {
        return "Ese nombre de usuario ya está en uso. Elige otro.";
    }

    @Override
    protected String motivoNoEliminable(Usuario u) {
        if (esCuentaEnUso(u)) {
            return "No puedes eliminar tu propia cuenta mientras tienes la sesión abierta.";
        }
        if (repo.listar().size() <= 1) {
            return "No se puede eliminar al único usuario: nadie podría volver a entrar al sistema.";
        }
        return null;
    }

    @Override
    protected void alCambiarModo(boolean editando) {
        v.setAyudaContrasena(editando);
    }

    private boolean esCuentaEnUso(Usuario u) {
        Usuario actual = SesionUsuario.getInstance().getUsuarioActual();
        return u != null && actual != null && actual.getIdUsuario() == u.getIdUsuario();
    }
}
