package com.saltillo.limpieza.views.components;

import javax.swing.*;
import java.awt.*;

/**
 * Componente visual de tarjeta ejecutiva de KPI rediseñada.
 * 
 * Mejoras de diseño:
 * - Badge circular superior con icono de acento temático.
 * - Ajuste dinámico de tipografía (evita que nombres largos como 'Cuadrilla Centro...' se trunquen con puntos suspensivos).
 * - Borde suave con sombra simulada y esquinas redondeadas.
 */
public class KpiCardComponent extends RoundedPanel {
    private static final long serialVersionUID = 1L;

    private final JLabel lblTitulo;
    private final JLabel lblValor;
    private final JLabel lblSubtitulo;
    private final JPanel pnlIconBadge;
    private final Color colorAcento;

    public KpiCardComponent(String titulo, String valorInicial, String subtitulo, Color colorAcento, Icon icono) {
        super(16, Color.WHITE, new Color(226, 232, 240));
        this.colorAcento = colorAcento;
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        setPreferredSize(new Dimension(240, 130));

        // 1. FILA SUPERIOR: Título del KPI + Icon Badge circular
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);

        lblTitulo = new JLabel(titulo.toUpperCase());
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitulo.setForeground(new Color(100, 116, 139)); // Slate-500
        pnlTop.add(lblTitulo, BorderLayout.WEST);

        // Badge circular para el icono
        pnlIconBadge = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Fondo con opacidad suave del color de acento
                g2.setColor(new Color(colorAcento.getRed(), colorAcento.getGreen(), colorAcento.getBlue(), 35));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pnlIconBadge.setPreferredSize(new Dimension(32, 32));
        pnlIconBadge.setOpaque(false);

        if (icono != null) {
            JLabel lblIcono = new JLabel(icono);
            pnlIconBadge.add(lblIcono);
        }
        pnlTop.add(pnlIconBadge, BorderLayout.EAST);

        add(pnlTop, BorderLayout.NORTH);

        // 2. FILA CENTRAL: Valor métrico con tipografía inteligente
        lblValor = new JLabel(valorInicial);
        aplicarTipografiaValor(valorInicial);
        lblValor.setForeground(new Color(15, 23, 42)); // Slate-900

        // 3. FILA INFERIOR: Subtítulo contextual
        lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(71, 85, 105)); // Slate-600

        JPanel pnlCentro = new JPanel();
        pnlCentro.setLayout(new BoxLayout(pnlCentro, BoxLayout.Y_AXIS));
        pnlCentro.setOpaque(false);
        pnlCentro.add(lblValor);
        pnlCentro.add(Box.createVerticalStrut(4));
        pnlCentro.add(lblSubtitulo);

        add(pnlCentro, BorderLayout.CENTER);
    }

    /**
     * Ajusta el tamaño de la fuente para que cadenas largas (ej. 'Cuadrilla Centro Histórico')
     * quepan perfectamente sin truncarse con '...'.
     */
    private void aplicarTipografiaValor(String valor) {
        if (valor == null) {
            lblValor.setText("--");
            return;
        }

        // Si es un nombre de texto largo
        if (valor.length() > 10) {
            lblValor.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblValor.setText("<html><body style='width: 160px; line-height: 1.15;'>" + valor + "</body></html>");
        } else {
            lblValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
            lblValor.setText(valor);
        }
    }

    public void setValor(String valor) {
        aplicarTipografiaValor(valor);
        revalidate();
        repaint();
    }

    public void setSubtitulo(String subtitulo) {
        lblSubtitulo.setText(subtitulo);
        revalidate();
        repaint();
    }
}
