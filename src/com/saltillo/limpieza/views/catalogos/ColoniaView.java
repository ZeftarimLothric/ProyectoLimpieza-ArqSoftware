package com.saltillo.limpieza.views.catalogos;

import javax.swing.*;

/** Catálogo de colonias SEPOMEX de Saltillo (Issue #8). */
public class ColoniaView extends CatalogoBaseView {
    private static final long serialVersionUID = 1L;

    public static final String MUNICIPIO_INICIAL = "Saltillo";
    public static final String ESTADO_INICIAL = "Coahuila de Zaragoza";

    private final JTextField txtNombre = nuevoCampoTexto();
    private final JTextField txtCodigoPostal = nuevoCampoTexto();
    private final JTextField txtMunicipio = nuevoCampoTexto();
    private final JTextField txtEstado = nuevoCampoTexto();

    public ColoniaView() {
        super("Colonias", "Catálogo de colonias y códigos postales (SEPOMEX)",
                new String[]{"ID", "Colonia", "C.P.", "Municipio", "Estado"}, "Busca por colonia o código postal");
        agregarCampo("Colonia *", txtNombre);
        agregarCampo("Código postal * (5 dígitos)", txtCodigoPostal);
        agregarCampo("Municipio *", txtMunicipio);
        agregarCampo("Estado *", txtEstado);
        limpiarCampos();
    }

    @Override
    protected void limpiarCampos() {
        txtNombre.setText("");
        txtCodigoPostal.setText("");
        txtMunicipio.setText(MUNICIPIO_INICIAL);
        txtEstado.setText(ESTADO_INICIAL);
    }

    public String getNombre() {
        return txtNombre.getText();
    }

    public void setNombre(String v) {
        txtNombre.setText(v);
    }

    public String getCodigoPostal() {
        return txtCodigoPostal.getText();
    }

    public void setCodigoPostal(String v) {
        txtCodigoPostal.setText(v);
    }

    public String getMunicipio() {
        return txtMunicipio.getText();
    }

    public void setMunicipio(String v) {
        txtMunicipio.setText(v);
    }

    public String getEstado() {
        return txtEstado.getText();
    }

    public void setEstado(String v) {
        txtEstado.setText(v);
    }
}
