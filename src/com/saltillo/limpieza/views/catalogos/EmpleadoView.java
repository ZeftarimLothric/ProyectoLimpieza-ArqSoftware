package com.saltillo.limpieza.views.catalogos;

import javax.swing.*;

/** Catálogo de empleados (Issue #8). */
public class EmpleadoView extends CatalogoBaseView {
    private static final long serialVersionUID = 1L;

    private final JTextField txtNombre = nuevoCampoTexto();
    private final JTextField txtApellidoPaterno = nuevoCampoTexto();
    private final JTextField txtApellidoMaterno = nuevoCampoTexto();
    private final JTextField txtTelefono = nuevoCampoTexto();
    private final JTextField txtPuesto = nuevoCampoTexto();
    private final JCheckBox chkActivo = nuevoCheck("Empleado activo");

    public EmpleadoView() {
        super("Empleados", "Personal operativo que forma las cuadrillas de limpieza",
                new String[]{"ID", "Nombre", "Teléfono", "Puesto", "Estado"}, "Busca por nombre, puesto o teléfono");
        agregarCampo("Nombre(s) *", txtNombre);
        agregarCampo("Apellido paterno *", txtApellidoPaterno);
        agregarCampo("Apellido materno", txtApellidoMaterno);
        agregarCampo("Teléfono", txtTelefono);
        agregarCampo("Puesto *", txtPuesto);
        agregarCampo(null, chkActivo);
    }

    @Override
    protected void limpiarCampos() {
        txtNombre.setText("");
        txtApellidoPaterno.setText("");
        txtApellidoMaterno.setText("");
        txtTelefono.setText("");
        txtPuesto.setText("");
        chkActivo.setSelected(true);
    }

    public String getNombre() {
        return txtNombre.getText();
    }

    public void setNombre(String v) {
        txtNombre.setText(v);
    }

    public String getApellidoPaterno() {
        return txtApellidoPaterno.getText();
    }

    public void setApellidoPaterno(String v) {
        txtApellidoPaterno.setText(v);
    }

    public String getApellidoMaterno() {
        return txtApellidoMaterno.getText();
    }

    public void setApellidoMaterno(String v) {
        txtApellidoMaterno.setText(v);
    }

    public String getTelefono() {
        return txtTelefono.getText();
    }

    public void setTelefono(String v) {
        txtTelefono.setText(v);
    }

    public String getPuesto() {
        return txtPuesto.getText();
    }

    public void setPuesto(String v) {
        txtPuesto.setText(v);
    }

    public boolean isActivo() {
        return chkActivo.isSelected();
    }

    public void setActivo(boolean v) {
        chkActivo.setSelected(v);
    }
}
