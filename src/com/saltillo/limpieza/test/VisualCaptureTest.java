package com.saltillo.limpieza.test;

import com.saltillo.limpieza.Main;
import com.saltillo.limpieza.views.MainShellView;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class VisualCaptureTest {

    public static void main(String[] args) throws Exception {
        System.out.println("Iniciando prueba de captura visual para Issue #16...");
        Main.main(new String[0]);

        Thread.sleep(1200);

        MainShellView shellFrame = null;
        for (Window w : Window.getWindows()) {
            if (w instanceof MainShellView && w.isVisible()) {
                shellFrame = (MainShellView) w;
                break;
            }
        }

        if (shellFrame != null) {
            // Navegar al módulo de Consulta Actividades (Issue #16)
            shellFrame.mostrarModulo("CONSULTA");
            Thread.sleep(600);

            BufferedImage img = new BufferedImage(shellFrame.getWidth(), shellFrame.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            shellFrame.paint(g2);
            g2.dispose();

            File out = new File("visual_capture_consulta.png");
            ImageIO.write(img, "png", out);
            System.out.println("Captura guardada en: " + out.getAbsolutePath());

            shellFrame.dispose();
            System.exit(0);
        } else {
            System.err.println("No se encontró el JFrame.");
            System.exit(1);
        }
    }
}
