package com.saltillo.limpieza.views.components;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Renderer visual de alta estética para la barra de rendimiento en JTable.
 * Incluye bordes redondeados y colores adaptativos suaves.
 */
public class ProgressBarRenderer extends JPanel implements TableCellRenderer {
    private static final long serialVersionUID = 1L;

    private double porcentaje = 0.0;
    private final JProgressBar progressBar;

    public ProgressBarRenderer() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        setOpaque(true);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        progressBar.setPreferredSize(new Dimension(110, 20));
        progressBar.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true));
        add(progressBar, BorderLayout.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        if (value instanceof Number) {
            porcentaje = ((Number) value).doubleValue();
        } else if (value != null) {
            try {
                porcentaje = Double.parseDouble(value.toString().replace("%", "").trim());
            } catch (NumberFormatException e) {
                porcentaje = 0.0;
            }
        } else {
            porcentaje = 0.0;
        }

        int valInt = (int) Math.round(porcentaje);
        progressBar.setValue(valInt);
        progressBar.setString(String.format("%.1f%%", porcentaje));

        // Coloración moderna de rendimiento
        if (porcentaje >= 80.0) {
            progressBar.setForeground(new Color(16, 185, 129)); // Verde Esmeralda
        } else if (porcentaje >= 50.0) {
            progressBar.setForeground(new Color(2, 132, 199));  // Azul Sky
        } else {
            progressBar.setForeground(new Color(245, 158, 11)); // Ámbar
        }

        if (isSelected) {
            setBackground(new Color(241, 245, 249));
        } else {
            setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
        }

        return this;
    }
}
