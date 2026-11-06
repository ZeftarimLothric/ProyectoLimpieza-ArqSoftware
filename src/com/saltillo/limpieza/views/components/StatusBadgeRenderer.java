package com.saltillo.limpieza.views.components;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Renderer visual estilo Pill Badge para el estado de las cuadrillas.
 */
public class StatusBadgeRenderer extends JPanel implements TableCellRenderer {
    private static final long serialVersionUID = 1L;

    private final JLabel lblBadge;
    private Color badgeBackground = new Color(241, 245, 249);
    private Color badgeForeground = new Color(71, 85, 105);

    public StatusBadgeRenderer() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 6));
        setOpaque(true);

        lblBadge = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(badgeBackground);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblBadge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        add(lblBadge);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        String estado = (value != null) ? value.toString() : "DESCONOCIDO";
        lblBadge.setText(estado.toUpperCase());

        if ("EN OPERACION".equalsIgnoreCase(estado)) {
            badgeBackground = new Color(220, 252, 231); // Verde menta suave
            badgeForeground = new Color(22, 101, 52);   // Verde oscuro legible
        } else if ("DISPONIBLE".equalsIgnoreCase(estado)) {
            badgeBackground = new Color(224, 242, 254); // Azul suave
            badgeForeground = new Color(3, 105, 161);   // Azul oscuro legible
        } else {
            badgeBackground = new Color(241, 245, 249);
            badgeForeground = new Color(71, 85, 105);
        }

        lblBadge.setForeground(badgeForeground);

        if (isSelected) {
            setBackground(new Color(241, 245, 249));
        } else {
            setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
        }

        return this;
    }
}
