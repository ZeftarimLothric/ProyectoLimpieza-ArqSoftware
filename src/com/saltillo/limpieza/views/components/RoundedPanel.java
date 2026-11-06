package com.saltillo.limpieza.views.components;

import javax.swing.*;
import java.awt.*;

/**
 * Componente visual de soporte: Panel con esquinas redondeadas y borde sutil.
 * Proporciona una estética moderna y limpia (estilo card) sin librerías externas.
 */
public class RoundedPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private int cornerRadius = 14;
    private Color backgroundColor = Color.WHITE;
    private Color borderColor = new Color(226, 232, 240); // Slate-200
    private boolean drawBorder = true;

    public RoundedPanel() {
        super();
        setOpaque(false);
    }

    public RoundedPanel(int radius, Color bg) {
        super();
        this.cornerRadius = radius;
        this.backgroundColor = bg;
        setOpaque(false);
    }

    public RoundedPanel(int radius, Color bg, Color border) {
        super();
        this.cornerRadius = radius;
        this.backgroundColor = bg;
        this.borderColor = border;
        this.drawBorder = border != null;
        setOpaque(false);
    }

    public void setBackgroundColor(Color bg) {
        this.backgroundColor = bg;
        repaint();
    }

    public void setBorderColor(Color border) {
        this.borderColor = border;
        this.drawBorder = border != null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo redondeado
        g2.setColor(backgroundColor != null ? backgroundColor : getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

        // Borde redondeado sutil
        if (drawBorder && borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }

        g2.dispose();
    }
}
