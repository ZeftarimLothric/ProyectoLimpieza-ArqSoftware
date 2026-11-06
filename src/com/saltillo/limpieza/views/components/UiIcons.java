package com.saltillo.limpieza.views.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Generador de iconos vectoriales ligeros mediante Java2D.
 * 
 * Soluciona definitivamente los problemas de compatibilidad y los cuadros
 * vacíos (tofu glyphs '▯') que ocurren al usar emojis o caracteres especiales
 * en fuentes del sistema en Windows.
 */
public class UiIcons {

    /**
     * Icono para el Dashboard Ejecutivo (Cuadrícula 2x2 moderna).
     */
    public static Icon getDashboardIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            int pad = 2;
            int gap = 2;
            int cellW = (w - pad * 2 - gap) / 2;
            int cellH = (h - pad * 2 - gap) / 2;

            g2.fill(new RoundRectangle2D.Float(pad, pad, cellW, cellH, 3, 3));
            g2.fill(new RoundRectangle2D.Float(pad + cellW + gap, pad, cellW, cellH, 3, 3));
            g2.fill(new RoundRectangle2D.Float(pad, pad + cellH + gap, cellW, cellH, 3, 3));
            g2.fill(new RoundRectangle2D.Float(pad + cellW + gap, pad + cellH + gap, cellW, cellH, 3, 3));
        });
    }

    /**
     * Icono para Registro de Actividades (Portapapeles / Lista de tareas).
     */
    public static Icon getActividadesIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Tabla / Portapapeles
            g2.draw(new RoundRectangle2D.Float(2, 3, w - 5, h - 5, 3, 3));
            // Clip superior
            g2.drawLine(w / 2 - 3, 3, w / 2 + 3, 3);
            // Líneas de contenido
            g2.drawLine(5, 7, w - 6, 7);
            g2.drawLine(5, 10, w - 6, 10);
            g2.drawLine(5, 13, w - 9, 13);
        });
    }

    /**
     * Icono para Catálogo de Cuadrillas (Personas / Equipo).
     */
    public static Icon getCuadrillasIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f));

            // Cabeza central
            g2.drawOval(w / 2 - 3, 2, 6, 6);
            // Cuerpo central
            g2.drawArc(w / 2 - 6, 9, 12, 10, 0, 180);

            // Cabeza izquierda secundaria
            g2.drawOval(1, 4, 4, 4);
            g2.drawArc(0, 9, 8, 8, 20, 140);
        });
    }

    /**
     * Icono para Catálogo de Colonias (Pin de Mapa / Ubicación).
     */
    public static Icon getColoniasIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int cx = w / 2;
            Path2D.Float pin = new Path2D.Float();
            pin.moveTo(cx, h - 2);
            pin.curveTo(cx - 5, h - 7, cx - 6, 6, cx, 2);
            pin.curveTo(cx + 6, 6, cx + 5, h - 7, cx, h - 2);
            pin.closePath();
            g2.draw(pin);

            // Punto interior
            g2.fillOval(cx - 2, 5, 4, 4);
        });
    }

    /**
     * Icono para Cerrar Sesión (Puerta con flecha de salida).
     */
    public static Icon getLogoutIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Marco de puerta
            g2.drawPolyline(new int[]{w / 2, 2, 2, w / 2}, new int[]{2, 2, h - 2, h - 2}, 4);

            // Flecha de salida hacia la derecha
            int midY = h / 2;
            g2.drawLine(w / 2 - 2, midY, w - 2, midY);
            g2.drawLine(w - 5, midY - 3, w - 2, midY);
            g2.drawLine(w - 5, midY + 3, w - 2, midY);
        });
    }

    /**
     * Icono de Rayo / Simulación Observer.
     */
    public static Icon getLightningIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            Path2D.Float bolt = new Path2D.Float();
            bolt.moveTo(w * 0.55f, 1f);
            bolt.lineTo(w * 0.20f, h * 0.55f);
            bolt.lineTo(w * 0.48f, h * 0.55f);
            bolt.lineTo(w * 0.40f, h - 1f);
            bolt.lineTo(w * 0.80f, h * 0.42f);
            bolt.lineTo(w * 0.52f, h * 0.42f);
            bolt.closePath();
            g2.fill(bolt);
        });
    }

    /**
     * Icono de Refrescar (Flecha circular).
     */
    public static Icon getRefreshIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawArc(3, 3, w - 6, h - 6, 45, 270);
            // Punta de flecha
            g2.drawLine(w - 3, h / 2 - 1, w - 3, h / 2 + 4);
            g2.drawLine(w - 7, h / 2 + 4, w - 3, h / 2 + 4);
        });
    }

    /**
     * Clase interna para encapsular dibujo vectorial en un Icon estándar de Swing.
     */
    private static class VectorIcon implements Icon {
        private final int width;
        private final int height;
        private final IconPainter painter;

        VectorIcon(int width, int height, IconPainter painter) {
            this.width = width;
            this.height = height;
            this.painter = painter;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(x, y);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            painter.paint(g2, width, height);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }

    @FunctionalInterface
    public interface IconPainter {
        void paint(Graphics2D g2, int width, int height);
    }
}
