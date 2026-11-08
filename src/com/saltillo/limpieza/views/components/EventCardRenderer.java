package com.saltillo.limpieza.views.components;

import com.saltillo.limpieza.models.dto.ActividadEventDTO;
import com.saltillo.limpieza.models.dto.TipoEventoActividad;

import javax.swing.*;
import java.awt.*;

/**
 * Renderer visual para los eventos del Patrón Observer en la lista en vivo.
 * Soporta múltiples colonias y coloración según TipoEventoActividad (CREADA, ACTUALIZADA, ELIMINADA).
 */
public class EventCardRenderer extends JPanel implements ListCellRenderer<ActividadEventDTO> {
    private static final long serialVersionUID = 1L;

    private final JLabel lblHora;
    private final JLabel lblCuadrilla;
    private final JLabel lblTipo;
    private final JLabel lblColonia;
    private final JLabel lblBadgeEvento;
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

        pnlIndicador = new JPanel();
        pnlIndicador.setPreferredSize(new Dimension(4, 40));
        pnlIndicador.setBackground(new Color(16, 185, 129));
        add(pnlIndicador, BorderLayout.WEST);

        JPanel pnlContenido = new JPanel();
        pnlContenido.setLayout(new BoxLayout(pnlContenido, BoxLayout.Y_AXIS));
        pnlContenido.setOpaque(false);

        // Fila 1: Nombre Cuadrilla + Badge Evento + Hora
        JPanel pnlFila1 = new JPanel(new BorderLayout());
        pnlFila1.setOpaque(false);

        lblCuadrilla = new JLabel();
        lblCuadrilla.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCuadrilla.setForeground(new Color(15, 23, 42));

        JPanel pnlDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlDer.setOpaque(false);

        lblBadgeEvento = new JLabel();
        lblBadgeEvento.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblBadgeEvento.setBorder(BorderFactory.createEmptyBorder(1, 6, 1, 6));

        lblHora = new JLabel();
        lblHora.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblHora.setForeground(new Color(100, 116, 139));

        pnlDer.add(lblBadgeEvento);
        pnlDer.add(lblHora);

        pnlFila1.add(lblCuadrilla, BorderLayout.WEST);
        pnlFila1.add(pnlDer, BorderLayout.EAST);
        pnlContenido.add(pnlFila1);

        pnlContenido.add(Box.createVerticalStrut(2));

        // Fila 2: Tipo de Servicio
        lblTipo = new JLabel();
        lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTipo.setForeground(new Color(71, 85, 105));
        pnlContenido.add(lblTipo);

        pnlContenido.add(Box.createVerticalStrut(2));

        // Fila 3: Colonias (Soporte para múltiples colonias - Requisito 8 rúbrica)
        lblColonia = new JLabel();
        lblColonia.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblColonia.setForeground(new Color(2, 132, 199));
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
            lblColonia.setText("Ubicación: " + value.getColoniasFormateadas());

            TipoEventoActividad tipo = value.getTipoEvento();
            if (tipo == TipoEventoActividad.ELIMINADA) {
                pnlIndicador.setBackground(new Color(239, 68, 68)); // Rojo
                lblBadgeEvento.setText("ELIMINADA");
                lblBadgeEvento.setForeground(new Color(185, 28, 28));
            } else if (tipo == TipoEventoActividad.ACTUALIZADA) {
                pnlIndicador.setBackground(new Color(2, 132, 199)); // Azul
                lblBadgeEvento.setText("ACTUALIZADA");
                lblBadgeEvento.setForeground(new Color(3, 105, 161));
            } else {
                pnlIndicador.setBackground(new Color(16, 185, 129)); // Verde
                lblBadgeEvento.setText("NUEVA");
                lblBadgeEvento.setForeground(new Color(21, 128, 61));
            }
        }

        if (isSelected) {
            setBackground(new Color(241, 245, 249));
        } else {
            setBackground(Color.WHITE);
        }

        return this;
    }
}
