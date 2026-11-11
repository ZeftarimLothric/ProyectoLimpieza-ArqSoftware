package com.saltillo.limpieza.views.catalogos;

import com.saltillo.limpieza.views.components.EstiloUi;

import javax.swing.*;
import java.awt.*;

/** Catálogo de usuarios administradores (Issue #8). Solo dibuja; la lógica está en UsuarioController. */
public class UsuarioView extends CatalogoBaseView {
    private static final long serialVersionUID = 1L;

    private final JTextField txtUsuario = nuevoCampoTexto();
    private final JTextField txtNombre = nuevoCampoTexto();
    private final JPasswordField txtContrasena = EstiloUi.estilizarCampo(new JPasswordField());
    private final JLabel lblAyudaContrasena = EstiloUi.subtitulo(" ");
    private final JCheckBox chkActivo = nuevoCheck("Usuario activo (puede iniciar sesión)");

    public UsuarioView() {
        super("Usuarios", "Administradores con acceso al sistema",
                new String[]{"ID", "Usuario", "Nombre", "Estado"}, "Busca por usuario o nombre");
        agregarCampo("Usuario *", txtUsuario);
        agregarCampo("Nombre completo *", txtNombre);
        agregarCampo("Contraseña *", txtContrasena);
        lblAyudaContrasena.setFont(EstiloUi.fuente(Font.PLAIN, 11));
        agregarCampo(null, lblAyudaContrasena);
        agregarCampo(null, chkActivo);
        setAyudaContrasena(false);
    }

    @Override
    protected void limpiarCampos() {
        txtUsuario.setText("");
        txtNombre.setText("");
        txtContrasena.setText("");
        chkActivo.setSelected(true);
    }

    /** Texto de ayuda bajo la contraseña: obligatoria al crear, opcional al editar. */
    public void setAyudaContrasena(boolean editando) {
        lblAyudaContrasena.setText(editando
                ? "Déjala vacía para conservar la contraseña actual."
                : "Mínimo 6 caracteres. Se guarda cifrada (SHA-256).");
    }

    public String getUsuario() {
        return txtUsuario.getText();
    }

    public void setUsuario(String v) {
        txtUsuario.setText(v);
    }

    public String getNombre() {
        return txtNombre.getText();
    }

    public void setNombre(String v) {
        txtNombre.setText(v);
    }

    public String getContrasena() {
        return new String(txtContrasena.getPassword());
    }

    public void limpiarContrasena() {
        txtContrasena.setText("");
    }

    public boolean isActivo() {
        return chkActivo.isSelected();
    }

    public void setActivo(boolean v) {
        chkActivo.setSelected(v);
    }
}
