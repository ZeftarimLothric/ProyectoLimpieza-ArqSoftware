package com.saltillo.limpieza.test;

import com.saltillo.limpieza.controllers.ActividadController;
import com.saltillo.limpieza.repositories.MockActividadRepository;
import com.saltillo.limpieza.repositories.MockDashboardRepository;
import com.saltillo.limpieza.repositories.MockEvidenciaRepository;
import com.saltillo.limpieza.services.EvidenciaStorageService;
import com.saltillo.limpieza.views.actividades.ActividadFormView;
import com.saltillo.limpieza.views.actividades.ActividadListView;

import javax.swing.*;

/**
 * Demo manual del CRUD de actividades (Integrante 3 - Issue #10).
 * Los datos viven en memoria y se pierden al cerrar la ventana.
 */
public class ActividadesDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ActividadesDemo::abrir);
    }

    private static void abrir() {
        ActividadListView lista = new ActividadListView();
        new ActividadController(lista, new ActividadFormView(), new MockActividadRepository(),
                new MockEvidenciaRepository(), new MockDashboardRepository(), new EvidenciaStorageService());

        JFrame ventana = new JFrame("Demo de Actividades - Issue #10");
        ventana.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ventana.setContentPane(lista);
        ventana.setSize(1100, 620);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}
