package com.saltillo.limpieza.test;

import com.saltillo.limpieza.Main;
import com.saltillo.limpieza.patterns.observer.ActividadEventBus;
import com.saltillo.limpieza.models.dto.ActividadEventDTO;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Utilidad de prueba para capturar el render visual de la ventana y verificar
 * que no existan traslapes, cajas de texto vacías o caracteres corruptos.
 */
public class VisualCaptureTest {

    public static void main(String[] args) throws Exception {
        System.out.println("Iniciando prueba de captura visual...");
        Main.main(new String[0]);

        Thread.sleep(1200);

        // Buscar la ventana principal
        JFrame shellFrame = null;
        for (Window w : Window.getWindows()) {
            if (w instanceof JFrame && w.isVisible()) {
                shellFrame = (JFrame) w;
                break;
            }
        }

        if (shellFrame != null) {
            // Emite 2 eventos simulados para ver el estado activo del feed
            ActividadEventBus.getInstance().publicarNuevaActividad(new ActividadEventDTO(
                    1, "Cuadrilla Centro Histórico", "Centro Histórico",
                    "Barrido Manual y Desazolve", "Operativo plaza de armas"
            ));
            ActividadEventBus.getInstance().publicarNuevaActividad(new ActividadEventDTO(
                    2, "Cuadrilla Venustiano Carranza", "República Poniente",
                    "Deshierbe y Poda de Camellones", "Corredor comercial"
            ));

            Thread.sleep(600);

            // Capturar la imagen de la ventana
            BufferedImage img = new BufferedImage(shellFrame.getWidth(), shellFrame.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            shellFrame.paint(g2);
            g2.dispose();

            File out = new File("visual_capture.png");
            ImageIO.write(img, "png", out);
            System.out.println("Captura guardada en: " + out.getAbsolutePath());

            // Cerrar ventana
            shellFrame.dispose();
            System.exit(0);
        } else {
            System.err.println("No se encontró el JFrame.");
            System.exit(1);
        }
    }
}
