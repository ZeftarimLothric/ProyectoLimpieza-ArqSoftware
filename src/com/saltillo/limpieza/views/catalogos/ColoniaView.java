package com.saltillo.limpieza.views.catalogos;

import com.saltillo.limpieza.views.components.EstiloUi;

import javax.swing.*;
import java.awt.Font;

/** Catálogo de colonias SEPOMEX de Saltillo y municipios de Coahuila (Issue #8). */
public class ColoniaView extends CatalogoBaseView {
    private static final long serialVersionUID = 1L;

    public static final String MUNICIPIO_INICIAL = "Saltillo";
    public static final String ESTADO_INICIAL = "Coahuila de Zaragoza";

    public static final String[] MUNICIPIOS_COAHUILA = {
        "Abasolo", "Acuña", "Allende", "Arteaga", "Candela", "Castaños",
        "Cuatro Ciénegas", "Escobedo", "Francisco I. Madero", "Frontera",
        "General Cepeda", "Guerrero", "Hidalgo", "Jiménez", "Juárez",
        "Lamadrid", "Matamoros", "Monclova", "Morelos", "Múzquiz",
        "Nadadores", "Nava", "Ocampo", "Parras", "Piedras Negras",
        "Progreso", "Ramos Arizpe", "Sabinas", "Sacramento", "Saltillo",
        "San Buenaventura", "San Juan de Sabinas", "San Pedro", "Sierra Mojada",
        "Torreón", "Viesca", "Villa Unión", "Zaragoza"
    };

    private final JTextField txtNombre = nuevoCampoTexto();
    private final JTextField txtCodigoPostal = nuevoCampoTexto();
    private final JComboBox<String> cmbMunicipio = new JComboBox<>(MUNICIPIOS_COAHUILA);
    private final JTextField txtEstado = nuevoCampoTexto();

    public ColoniaView() {
        super("Colonias", "Catálogo de colonias y códigos postales (SEPOMEX)",
                new String[]{"ID", "Colonia", "C.P.", "Municipio", "Estado"}, "Busca por colonia o código postal");
        cmbMunicipio.setFont(EstiloUi.fuente(Font.PLAIN, 13));
        agregarCampo("Colonia *", txtNombre);
        agregarCampo("Código postal * (5 dígitos)", txtCodigoPostal);
        agregarCampo("Municipio *", cmbMunicipio);
        agregarCampo("Estado *", txtEstado);
        limpiarCampos();
    }

    @Override
    protected void limpiarCampos() {
        txtNombre.setText("");
        txtCodigoPostal.setText("");
        setMunicipio(MUNICIPIO_INICIAL);
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
        Object item = cmbMunicipio.getSelectedItem();
        return item != null ? item.toString() : "";
    }

    public void setMunicipio(String v) {
        if (v == null || v.isBlank()) {
            cmbMunicipio.setSelectedItem(MUNICIPIO_INICIAL);
            return;
        }
        for (int i = 0; i < cmbMunicipio.getItemCount(); i++) {
            if (cmbMunicipio.getItemAt(i).equalsIgnoreCase(v.trim())) {
                cmbMunicipio.setSelectedIndex(i);
                return;
            }
        }
        // Si no está en la lista inicial (ej. datos históricos), se añade y selecciona
        cmbMunicipio.addItem(v);
        cmbMunicipio.setSelectedItem(v);
    }

    public String getEstado() {
        return txtEstado.getText();
    }

    public void setEstado(String v) {
        txtEstado.setText(v);
    }
}
