package com.saltillo.limpieza.test;

import com.saltillo.limpieza.controllers.ActividadController;
import com.saltillo.limpieza.controllers.ColoniaSelectorController;
import com.saltillo.limpieza.repositories.MockActividadRepository;
import com.saltillo.limpieza.repositories.MockColoniaRepository;
import com.saltillo.limpieza.repositories.MockCuadrillaRepository;
import com.saltillo.limpieza.repositories.MockEvidenciaRepository;
import com.saltillo.limpieza.services.EvidenciaStorageService;
import com.saltillo.limpieza.views.actividades.ActividadDetalleView;
import com.saltillo.limpieza.views.actividades.ActividadFormView;
import com.saltillo.limpieza.views.actividades.ActividadListView;
import com.saltillo.limpieza.views.actividades.ColoniaSelectorDialog;

import javax.swing.*;

/**
 * Demo manual del CRUD de actividades (Integrante 3 - Issues #10 y #11).
 * Los datos viven en memoria y se pierden al cerrar la ventana.
 */
public class ActividadesDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ActividadesDemo::abrir);
    }

    private static void abrir() {
        ActividadListView lista = new ActividadListView();
        ColoniaSelectorController coloniaSelector = new ColoniaSelectorController(new ColoniaSelectorDialog(),
                new MockColoniaRepository());
        new ActividadController(lista, new ActividadFormView(), new ActividadDetalleView(), new MockActividadRepository(),
                new MockEvidenciaRepository(), new MockCuadrillaRepository(), new EvidenciaStorageService(), coloniaSelector);

        JFrame ventana = new JFrame("Demo de Actividades - Issues #10 y #11");
        ventana.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ventana.setContentPane(lista);
        ventana.setSize(1100, 620);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}
