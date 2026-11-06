package com.saltillo.limpieza.views.components;

import com.saltillo.limpieza.models.dto.ActividadEventDTO;

import javax.swing.*;
import java.awt.*;

/**
 * Renderer visual para los eventos del Patrón Observer en la lista en vivo.
 * Transforma un elemento de texto plano en una tarjeta interactiva con insignias y colores.
 */
public class EventCardRenderer extends JPanel implements ListCellRenderer<ActividadEventDTO> {
    private static final long serialVersionUID = 1L;

    private final JLabel lblHora;
    private final JLabel lblCuadrilla;
    private final JLabel lblTipo;
    private final JLabel lblColonia;
    private final JPanel pnlIndicador;

    public EventCardRenderer() {
        setLayout(new BorderLayout(8, 4));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 4, 4, 4),
                BorderFactory.createCompoundBorder(
                        new javax.swing.border.LineBorder(new Color(226, 232, 240), 1, true),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                )
        ));
        setOpaque(true);

        // Barra indicadora lateral izquierda (Verde esmeralda para actividad nueva)
        pnlIndicador = new JPanel();
        pnlIndicador.setPreferredSize(new Dimension(4, 38));
        pnlIndicador.setBackground(new Color(16, 185, 129));
        add(pnlIndicador, BorderLayout.WEST);

        // Contenedor principal de textos
        JPanel pnlContenido = new JPanel();
        pnlContenido.setLayout(new BoxLayout(pnlContenido, BoxLayout.Y_AXIS));
        pnlContenido.setOpaque(false);

        // Fila 1: Hora + Nombre Cuadrilla
        JPanel pnlFila1 = new JPanel(new BorderLayout());
        pnlFila1.setOpaque(false);

        lblCuadrilla = new JLabel();
        lblCuadrilla.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCuadrilla.setForeground(new Color(15, 23, 42));

        lblHora = new JLabel();
        lblHora.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblHora.setForeground(new Color(100, 116, 139));

        pnlFila1.add(lblCuadrilla, BorderLayout.WEST);
        pnlFila1.add(lblHora, BorderLayout.EAST);
        pnlContenido.add(pnlFila1);

        pnlContenido.add(Box.createVerticalStrut(2));

        // Fila 2: Tipo de Servicio
        lblTipo = new JLabel();
        lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTipo.setForeground(new Color(71, 85, 105));
        pnlContenido.add(lblTipo);

        pnlContenido.add(Box.createVerticalStrut(2));

        // Fila 3: Colonia
        lblColonia = new JLabel();
        lblColonia.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblColonia.setForeground(new Color(2, 132, 199)); // Azul cielo Saltillo
        pnlContenido.add(lblColonia);

        add(pnlContenido, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends ActividadEventDTO> list,
                                                   ActividadEventDTO value,
                                                   int index,
                                                   boolean isSelected,
                                                   boolean cellHasFocus) {
        if (value != null) {
            lblCuadrilla.setText(value.getNombreCuadrilla());
            lblHora.setText(value.getFechaHoraFormateada());
            lblTipo.setText("• " + value.getTipoLimpieza());
            lblColonia.setText("Ubicación: Col. " + value.getColonia());
        }

        if (isSelected) {
            setBackground(new Color(241, 245, 249));
        } else {
            setBackground(Color.WHITE);
        }

        return this;
    }
}
