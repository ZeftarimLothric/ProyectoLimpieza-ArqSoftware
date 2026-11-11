package com.saltillo.limpieza.test;

import com.saltillo.limpieza.controllers.ActividadController;
import com.saltillo.limpieza.controllers.ColoniaSelectorController;
import com.saltillo.limpieza.repositories.IEvidenciaRepository;
import com.saltillo.limpieza.repositories.MockActividadRepository;
import com.saltillo.limpieza.repositories.MockColoniaRepository;
import com.saltillo.limpieza.repositories.MockCuadrillaRepository;
import com.saltillo.limpieza.repositories.MockEvidenciaRepository;
import com.saltillo.limpieza.services.EvidenciaStorageService;
import com.saltillo.limpieza.views.actividades.ActividadDetalleView;
import com.saltillo.limpieza.views.actividades.ActividadFormView;
import com.saltillo.limpieza.views.actividades.ActividadListView;
import com.saltillo.limpieza.views.actividades.ColoniaSelectorDialog;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Demo manual del detalle de actividad (Integrante 3 - Issue #13), sin login ni MySQL.
 * Abre la lista con datos de ejemplo y muestra el detalle de la actividad #1 con una evidencia generada.
 * Después se puede abrir el detalle de otras actividades con doble clic o con "Ver detalle".
 */
public class ActividadDetalleDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ActividadDetalleDemo::abrir);
    }

    private static void abrir() {
        try {
            Path carpeta = Files.createTempDirectory("evidencias-demo");
            EvidenciaStorageService servicio = new EvidenciaStorageService(carpeta);
            IEvidenciaRepository evidencias = new MockEvidenciaRepository();
            evidencias.reemplazar(1, List.of(servicio.guardar(crearImagenDeEjemplo(carpeta))));

            ActividadListView lista = new ActividadListView();
            ActividadController controller = new ActividadController(lista, new ActividadFormView(), new ActividadDetalleView(),
                    new MockActividadRepository(), evidencias, new MockCuadrillaRepository(), servicio,
                    new ColoniaSelectorController(new ColoniaSelectorDialog(), new MockColoniaRepository()));

            JFrame ventana = new JFrame("Demo de Detalle de Actividad - Issue #13");
            ventana.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            ventana.setContentPane(lista);
            ventana.setSize(1100, 620);
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);

            controller.verDetalle(1);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "No se pudo preparar la demo: " + ex.getMessage());
        }
    }

    private static Path crearImagenDeEjemplo(Path carpeta) throws Exception {
        BufferedImage imagen = new BufferedImage(640, 420, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagen.createGraphics();
        g.setColor(new Color(186, 230, 253));
        g.fillRect(0, 0, 640, 420);
        g.setColor(new Color(3, 105, 161));
        g.setFont(new Font("Segoe UI", Font.BOLD, 32));
        g.drawString("Evidencia de ejemplo", 150, 220);
        g.dispose();
        Path archivo = carpeta.resolve("ejemplo.png");
        ImageIO.write(imagen, "png", archivo.toFile());
        return archivo;
    }
}
