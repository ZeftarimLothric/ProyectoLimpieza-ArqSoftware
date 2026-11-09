package com.saltillo.limpieza.test;

import com.saltillo.limpieza.controllers.EvidenciaController;
import com.saltillo.limpieza.services.EvidenciaStorageService;
import com.saltillo.limpieza.views.actividades.EvidenciaPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Demo manual del panel de evidencias (Integrante 3 - Issue #12).
 * Las imágenes se guardan en la carpeta "evidencias"; cerrar la ventana equivale a cancelar.
 */
public class EvidenciaPanelDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EvidenciaPanelDemo::abrir);
    }

    private static void abrir() {
        EvidenciaPanel panel = new EvidenciaPanel();
        EvidenciaController controller = new EvidenciaController(panel, new EvidenciaStorageService());
        controller.cargar(List.of());

        JCheckBox chkSoloLectura = new JCheckBox("Solo lectura");
        chkSoloLectura.addActionListener(e -> panel.setSoloLectura(chkSoloLectura.isSelected()));

        JButton btnConfirmar = new JButton("Confirmar cambios");
        btnConfirmar.addActionListener(e -> controller.confirmarCambios());

        JButton btnDescartar = new JButton("Descartar cambios");
        btnDescartar.addActionListener(e -> controller.descartarCambios());

        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlAcciones.setOpaque(false);
        pnlAcciones.add(chkSoloLectura);
        pnlAcciones.add(btnDescartar);
        pnlAcciones.add(btnConfirmar);

        JPanel contenido = new JPanel(new BorderLayout(0, 12));
        contenido.setBackground(new Color(248, 250, 252));
        contenido.setBorder(BorderFactory.createEmptyBorder(20, 26, 20, 26));
        contenido.add(panel, BorderLayout.CENTER);
        contenido.add(pnlAcciones, BorderLayout.SOUTH);

        JFrame ventana = new JFrame("Demo de Evidencias - Issue #12");
        ventana.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ventana.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.descartarCambios();
            }
        });
        ventana.setContentPane(contenido);
        ventana.setSize(760, 300);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}
