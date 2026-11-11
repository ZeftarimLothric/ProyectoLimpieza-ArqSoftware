package com.saltillo.limpieza.views.actividades;

import com.saltillo.limpieza.models.entities.Colonia;

import javax.swing.*;
import java.awt.*;

/**
 * Muestra una colonia en las listas como "Nombre  (CP 25000)" (Integrante 3 - Issue #11).
 * Las colonias recuperadas solo por nombre no tienen código postal y se muestran sin él.
 */
public class ColoniaCellRenderer extends DefaultListCellRenderer {
    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                  boolean seleccionada, boolean foco) {
        super.getListCellRendererComponent(lista, valor, indice, seleccionada, foco);
        if (valor instanceof Colonia colonia) {
            setText(colonia.getCodigoPostal() != null
                    ? colonia.getNombre() + "  (CP " + colonia.getCodigoPostal() + ")"
                    : colonia.getNombre());
        }
        return this;
    }
}
