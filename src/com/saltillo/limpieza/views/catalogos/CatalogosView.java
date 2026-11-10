package com.saltillo.limpieza.views.catalogos;

import com.saltillo.limpieza.views.components.EstiloUi;

import javax.swing.*;
import java.awt.*;

/**
 * Contenedor con pestañas para los catálogos del Issue #8 (usuarios, empleados, colonias y
 * tipos de actividad). Se puede registrar tal cual como un módulo del Shell.
 */
public class CatalogosView extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JTabbedPane pestanas = new JTabbedPane();

    public CatalogosView() {
        super(new BorderLayout());
        setBackground(EstiloUi.FONDO);
        pestanas.setFont(EstiloUi.fuente(Font.BOLD, 13));
        add(pestanas, BorderLayout.CENTER);
    }

    public void agregarCatalogo(String titulo, JComponent vista) {
        pestanas.addTab(titulo, vista);
    }
}
