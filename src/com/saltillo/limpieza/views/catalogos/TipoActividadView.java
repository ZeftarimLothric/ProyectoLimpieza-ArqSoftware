package com.saltillo.limpieza.views.catalogos;

import com.saltillo.limpieza.views.components.EstiloUi;

import javax.swing.*;

/** Catálogo de tipos de actividad de limpieza (Issue #8). */
public class TipoActividadView extends CatalogoBaseView {
    private static final long serialVersionUID = 1L;

    private final JTextField txtNombre = nuevoCampoTexto();
    private final JTextArea txtDescripcion = EstiloUi.estilizarCampo(new JTextArea(4, 20));

    public TipoActividadView() {
        super("Tipos de actividad", "Clasificación de los servicios de limpieza urbana",
                new String[]{"ID", "Nombre", "Descripción"}, "Busca por nombre o descripción");
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        agregarCampo("Nombre *", txtNombre);
        agregarCampo("Descripción", txtDescripcion);
    }

    @Override
    protected void limpiarCampos() {
        txtNombre.setText("");
        txtDescripcion.setText("");
    }

    public String getNombre() {
        return txtNombre.getText();
    }

    public void setNombre(String v) {
        txtNombre.setText(v);
    }

    public String getDescripcion() {
        return txtDescripcion.getText();
    }

    public void setDescripcion(String v) {
        txtDescripcion.setText(v);
    }
}
