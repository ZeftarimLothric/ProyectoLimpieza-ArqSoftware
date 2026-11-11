package com.saltillo.limpieza.test;

import com.saltillo.limpieza.controllers.ColoniaSelectorController;
import com.saltillo.limpieza.models.entities.Colonia;
import com.saltillo.limpieza.repositories.MockColoniaRepository;
import com.saltillo.limpieza.views.actividades.ColoniaSelectorDialog;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Demo manual del selector de colonias (Integrante 3 - Issue #11).
 */
public class ColoniaSelectorDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ColoniaSelectorDemo::abrir);
    }

    private static void abrir() {
        ColoniaSelectorController controller =
                new ColoniaSelectorController(new ColoniaSelectorDialog(), new MockColoniaRepository());
        List<Colonia> elegidas = new ArrayList<>();

        JLabel lblElegidas = new JLabel("Sin colonias elegidas");
        lblElegidas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton btnElegir = new JButton("Elegir colonias...");

        JPanel contenido = new JPanel(new BorderLayout(0, 12));
        contenido.setBorder(BorderFactory.createEmptyBorder(20, 26, 20, 26));
        contenido.add(lblElegidas, BorderLayout.CENTER);
        contenido.add(btnElegir, BorderLayout.SOUTH);

        btnElegir.addActionListener(e -> controller.seleccionar(contenido, elegidas).ifPresent(nuevas -> {
            elegidas.clear();
            elegidas.addAll(nuevas);
            lblElegidas.setText("<html>" + String.join("<br>", nuevas.stream().map(Colonia::toString).toList()) + "</html>");
        }));

        JFrame ventana = new JFrame("Demo de Colonias - Issue #11");
        ventana.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ventana.setContentPane(contenido);
        ventana.setSize(460, 280);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}
