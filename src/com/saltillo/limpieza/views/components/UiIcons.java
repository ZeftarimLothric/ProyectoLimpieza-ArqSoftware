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
     * Icono para el Dashboard Ejecutivo (Estilo moderno de analítica y métricas: columnas y KPI).
     */
    public static Icon getDashboardIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            float pad = w * 0.12f;
            float barW = (w - pad * 2) / 3.4f;
            float botY = h - pad;

            // Tres barras de métricas redondeadas con diferentes alturas de rendimiento
            g2.fill(new RoundRectangle2D.Float(pad, botY - (h * 0.42f), barW, h * 0.42f, 2.5f, 2.5f));
            g2.fill(new RoundRectangle2D.Float(pad + barW * 1.2f, botY - (h * 0.70f), barW, h * 0.70f, 2.5f, 2.5f));
            g2.fill(new RoundRectangle2D.Float(pad + barW * 2.4f, botY - (h * 0.52f), barW, h * 0.52f, 2.5f, 2.5f));

            // Línea base suave
            g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine((int) pad, (int) botY, (int) (w - pad), (int) botY);
        });
    }

    /**
     * Icono para Registro de Actividades (Escoba / Herramienta de Limpieza Urbana moderna).
     */
    public static Icon getActividadesIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Mango inclinado
            float x1 = w * 0.78f;
            float y1 = h * 0.14f;
            float x2 = w * 0.40f;
            float y2 = h * 0.58f;
            g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);

            // Cabeza del cepillo / escoba
            Path2D.Float broom = new Path2D.Float();
            broom.moveTo(w * 0.22f, h * 0.52f);
            broom.lineTo(w * 0.54f, h * 0.68f);
            broom.lineTo(w * 0.44f, h * 0.88f);
            broom.lineTo(w * 0.12f, h * 0.72f);
            broom.closePath();
            g2.draw(broom);

            // Cerdas / textura de limpieza
            g2.drawLine((int) (w * 0.24f), (int) (h * 0.76f), (int) (w * 0.36f), (int) (h * 0.82f));
        });
    }

    /**
     * Icono para Catálogo de Cuadrillas (Equipo de 2 personas con siluetas estilizadas).
     */
    public static Icon getCuadrillasIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Integrante 1 (Jefe / Central)
            int headR1 = (int) (w * 0.18f);
            int cx1 = (int) (w * 0.42f);
            int cy1 = (int) (h * 0.28f);
            g2.drawOval(cx1 - headR1, cy1 - headR1, headR1 * 2, headR1 * 2);

            // Torso integrante 1
            Path2D.Float torso1 = new Path2D.Float();
            torso1.moveTo(w * 0.16f, h * 0.88f);
            torso1.curveTo(w * 0.18f, h * 0.56f, w * 0.66f, h * 0.56f, w * 0.68f, h * 0.88f);
            g2.draw(torso1);

            // Integrante 2 (Compañero lateral)
            int headR2 = (int) (w * 0.13f);
            int cx2 = (int) (w * 0.74f);
            int cy2 = (int) (h * 0.36f);
            g2.drawOval(cx2 - headR2, cy2 - headR2, headR2 * 2, headR2 * 2);

            Path2D.Float torso2 = new Path2D.Float();
            torso2.moveTo(w * 0.62f, h * 0.88f);
            torso2.curveTo(w * 0.62f, h * 0.62f, w * 0.88f, h * 0.62f, w * 0.90f, h * 0.88f);
            g2.draw(torso2);
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
     * Icono para Catálogos del Sistema (Caja de herramientas / Catálogos maestros modular).
     */
    public static Icon getCatalogosIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Maletín / Caja maestra
            float pad = w * 0.14f;
            float topY = h * 0.34f;
            float boxW = w - pad * 2;
            float boxH = h * 0.54f;
            g2.draw(new RoundRectangle2D.Float(pad, topY, boxW, boxH, 3.5f, 3.5f));

            // Asa superior
            Path2D.Float handle = new Path2D.Float();
            handle.moveTo(w * 0.36f, topY);
            handle.lineTo(w * 0.36f, h * 0.18f);
            handle.lineTo(w * 0.64f, h * 0.18f);
            handle.lineTo(w * 0.64f, topY);
            g2.draw(handle);

            // Broche central
            g2.drawLine((int) pad, (int) (topY + boxH * 0.38f), (int) (w - pad), (int) (topY + boxH * 0.38f));
            g2.fillRect((int) (w * 0.44f), (int) (topY + boxH * 0.26f), (int) (w * 0.12f), (int) (boxH * 0.26f));
        });
    }

    /**
     * Icono de Búsqueda / Consulta (Lupa moderna y estilizada).
     */
    public static Icon getSearchIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            float r = w * 0.28f;
            float cx = w * 0.40f;
            float cy = h * 0.40f;
            g2.drawOval((int) (cx - r), (int) (cy - r), (int) (r * 2), (int) (r * 2));
            // Mango con buen grosor y redondeo
            float x1 = cx + r * 0.70f;
            float y1 = cy + r * 0.70f;
            g2.drawLine((int) x1, (int) y1, (int) (w - w * 0.12f), (int) (h - h * 0.12f));
        });
    }

    /**
     * Icono de Filtro (Embudo).
     */
    public static Icon getFilterIcon(Color color, int size) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D.Float funnel = new Path2D.Float();
            funnel.moveTo(2, 3);
            funnel.lineTo(w - 3, 3);
            funnel.lineTo(w * 0.58f, h * 0.52f);
            funnel.lineTo(w * 0.58f, h - 2);
            funnel.lineTo(w * 0.42f, h - 4);
            funnel.lineTo(w * 0.42f, h * 0.52f);
            funnel.closePath();
            g2.draw(funnel);
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
