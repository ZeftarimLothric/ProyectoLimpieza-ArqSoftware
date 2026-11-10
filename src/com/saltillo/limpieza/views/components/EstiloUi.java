package com.saltillo.limpieza.views.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Estilos y fábricas de componentes compartidos por las pantallas de catálogos (Issues #8 y #9),
 * con la misma paleta Slate / Sky del Shell y el Dashboard.
 */
public final class EstiloUi {

    public static final Color FONDO = new Color(248, 250, 252);        // Slate-50
    public static final Color TEXTO = new Color(15, 23, 42);           // Slate-900
    public static final Color TEXTO_MEDIO = new Color(71, 85, 105);    // Slate-600
    public static final Color TEXTO_SUAVE = new Color(100, 116, 139);  // Slate-500
    public static final Color BORDE = new Color(226, 232, 240);        // Slate-200
    public static final Color BORDE_CAMPO = new Color(203, 213, 225);  // Slate-300
    public static final Color ACENTO = new Color(2, 132, 199);         // Sky-600
    public static final Color ACENTO_HOVER = new Color(3, 105, 161);   // Sky-700
    public static final Color PELIGRO = new Color(220, 38, 38);        // Red-600
    public static final Color PELIGRO_HOVER = new Color(185, 28, 28);  // Red-700
    public static final Color EXITO = new Color(22, 101, 52);          // Green-800
    public static final Color ERROR = new Color(185, 28, 28);          // Red-700

    private EstiloUi() {
    }

    public static Font fuente(int estilo, int tamano) {
        return new Font("Segoe UI", estilo, tamano);
    }

    // ---------- botones ----------

    public static JButton botonPrimario(String texto) {
        return boton(texto, ACENTO, ACENTO_HOVER, Color.WHITE, null);
    }

    public static JButton botonSecundario(String texto) {
        return boton(texto, Color.WHITE, new Color(241, 245, 249), TEXTO_MEDIO, BORDE_CAMPO);
    }

    public static JButton botonPeligro(String texto) {
        return boton(texto, PELIGRO, PELIGRO_HOVER, Color.WHITE, null);
    }

    private static JButton boton(String texto, Color base, Color hover, Color letra, Color borde) {
        JButton b = new JButton(texto) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fondo = !isEnabled() ? new Color(226, 232, 240)
                        : (getModel().isRollover() ? hover : base);
                g2.setColor(fondo);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                if (borde != null && isEnabled()) {
                    g2.setColor(borde);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(fuente(Font.BOLD, 12));
        b.setForeground(letra);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setRolloverEnabled(true);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ---------- tablas ----------

    public static void estilizarTabla(JTable tabla) {
        tabla.setRowHeight(34);
        tabla.setFont(fuente(Font.PLAIN, 12));
        tabla.setForeground(TEXTO);
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(new Color(241, 245, 249));
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setFillsViewportHeight(true);
        tabla.setSelectionBackground(new Color(224, 242, 254));
        tabla.setSelectionForeground(TEXTO);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader h = tabla.getTableHeader();
        h.setFont(fuente(Font.BOLD, 12));
        h.setPreferredSize(new Dimension(100, 36));
        h.setBackground(new Color(241, 245, 249));
        h.setForeground(new Color(51, 65, 85));
        h.setReorderingAllowed(false);
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE_CAMPO));

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor, boolean seleccionada,
                                                           boolean foco, int fila, int columna) {
                super.getTableCellRendererComponent(t, valor, seleccionada, false, fila, columna);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
    }

    // ---------- etiquetas y campos ----------

    public static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(Font.BOLD, 11));
        l.setForeground(TEXTO_MEDIO);
        return l;
    }

    public static JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(Font.BOLD, 22));
        l.setForeground(TEXTO);
        return l;
    }

    public static JLabel subtitulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(Font.PLAIN, 12));
        l.setForeground(TEXTO_SUAVE);
        return l;
    }

    /** Fuente y borde de un campo de texto. */
    public static <T extends JComponent> T estilizarCampo(T campo) {
        campo.setFont(fuente(Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_CAMPO),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        return campo;
    }
}
