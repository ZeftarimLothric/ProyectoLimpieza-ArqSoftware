package com.saltillo.limpieza.views.actividades;

import com.saltillo.limpieza.models.dto.ActividadDetalleDTO;
import com.saltillo.limpieza.views.components.RoundedPanel;
import com.saltillo.limpieza.views.components.StatusBadgeRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Vista de la lista de actividades (Integrante 3 - Issue #10).
 *
 * Arquitectura MVC Estricta: muestra los DTOs que recibe y avisa de las
 * acciones al ActividadController.
 */
public class ActividadListView extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final int COLUMNA_ID = 0;
    private static final int COLUMNA_ESTADO = 6;

    private JComboBox<String> cmbEstado;
    private JButton btnNueva;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JTable tblActividades;
    private DefaultTableModel tableModel;
    private JLabel lblConteo;

    public ActividadListView() {
        setLayout(new BorderLayout(0, 16));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 26, 20, 26));

        initHeader();
        initTabla();
    }

    private void initHeader() {
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JPanel pnlTitulos = new JPanel();
        pnlTitulos.setLayout(new BoxLayout(pnlTitulos, BoxLayout.Y_AXIS));
        pnlTitulos.setOpaque(false);

        JLabel lblTitulo = new JLabel("Registro de Actividades");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(15, 23, 42));

        JLabel lblSubtitulo = new JLabel("Asigna cuadrillas a lugares específicos y da seguimiento a cada servicio de limpieza.");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(100, 116, 139));

        pnlTitulos.add(lblTitulo);
        pnlTitulos.add(Box.createVerticalStrut(4));
        pnlTitulos.add(lblSubtitulo);
        pnlHeader.add(pnlTitulos, BorderLayout.WEST);

        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlAcciones.setOpaque(false);
        btnEliminar = crearBoton("Eliminar", Color.WHITE, new Color(241, 245, 249), new Color(185, 28, 28), true);
        btnEditar = crearBoton("Editar", Color.WHITE, new Color(241, 245, 249), new Color(71, 85, 105), true);
        btnNueva = crearBoton("Nueva actividad", new Color(2, 132, 199), new Color(3, 105, 161), Color.WHITE, false);
        pnlAcciones.add(btnEliminar);
        pnlAcciones.add(btnEditar);
        pnlAcciones.add(btnNueva);
        pnlHeader.add(pnlAcciones, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);
    }

    private void initTabla() {
        RoundedPanel cardTabla = new RoundedPanel(16, Color.WHITE, new Color(226, 232, 240));
        cardTabla.setLayout(new BorderLayout(0, 12));
        cardTabla.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JPanel pnlSuperior = new JPanel(new BorderLayout());
        pnlSuperior.setOpaque(false);

        lblConteo = new JLabel();
        lblConteo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblConteo.setForeground(new Color(15, 23, 42));
        pnlSuperior.add(lblConteo, BorderLayout.WEST);

        cmbEstado = new JComboBox<>(new String[]{"Todos los estados", "PROGRAMADA", "EN PROCESO", "COMPLETADA"});
        cmbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pnlSuperior.add(cmbEstado, BorderLayout.EAST);
        cardTabla.add(pnlSuperior, BorderLayout.NORTH);

        String[] columnas = {"ID", "Programada", "Realizada", "Cuadrilla", "Tipo de Servicio", "Lugar Específico", "Estado", "Descripción"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblActividades = new JTable(tableModel);
        tblActividades.setRowHeight(36);
        tblActividades.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblActividades.setShowVerticalLines(false);
        tblActividades.setGridColor(new Color(241, 245, 249));
        tblActividades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblActividades.setFillsViewportHeight(true);
        tblActividades.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblActividades.getTableHeader().setPreferredSize(new Dimension(0, 36));
        tblActividades.getColumnModel().getColumn(COLUMNA_ID).setPreferredWidth(40);
        tblActividades.getColumnModel().getColumn(COLUMNA_ESTADO).setCellRenderer(new StatusBadgeRenderer());

        JScrollPane scroll = new JScrollPane(tblActividades);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        cardTabla.add(scroll, BorderLayout.CENTER);

        add(cardTabla, BorderLayout.CENTER);
    }

    private JButton crearBoton(String texto, Color fondo, Color fondoHover, Color colorTexto, boolean conBorde) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? fondoHover : fondo);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                if (conBorde) {
                    g2.setColor(new Color(203, 213, 225));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setForeground(colorTexto);
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(false);
        boton.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    // =========================================================================
    // MÉTODOS PÚBLICOS DE VISTA (STRICT MVC)
    // =========================================================================

    public void mostrarActividades(List<ActividadDetalleDTO> actividades) {
        tableModel.setRowCount(0);
        lblConteo.setText(actividades.size() + (actividades.size() == 1 ? " actividad" : " actividades"));
        for (ActividadDetalleDTO a : actividades) {
            tableModel.addRow(new Object[]{
                    a.getIdActividad(),
                    a.getFechaHoraFormateada(),
                    a.getFechaRealizacionFormateada(),
                    a.getNombreCuadrilla(),
                    a.getTipoLimpieza(),
                    a.getLugarEspecifico(),
                    a.getEstado(),
                    a.getObservaciones()
            });
        }
    }

    /**
     * @return el ID de la fila seleccionada, o null si no hay selección
     */
    public Integer getIdSeleccionado() {
        int fila = tblActividades.getSelectedRow();
        return fila >= 0 ? (Integer) tableModel.getValueAt(fila, COLUMNA_ID) : null;
    }

    /**
     * @return el estado elegido, o null si se muestran todos
     */
    public String getFiltroEstado() {
        return cmbEstado.getSelectedIndex() > 0 ? (String) cmbEstado.getSelectedItem() : null;
    }

    public boolean confirmarEliminacion(String mensaje) {
        return JOptionPane.showConfirmDialog(this, mensaje, "Eliminar actividad",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Actividades", JOptionPane.ERROR_MESSAGE);
    }

    public void setNuevoListener(ActionListener l) {
        btnNueva.addActionListener(l);
    }

    public void setEditarListener(ActionListener l) {
        btnEditar.addActionListener(l);
    }

    public void setEliminarListener(ActionListener l) {
        btnEliminar.addActionListener(l);
    }

    public void setFiltroEstadoListener(ActionListener l) {
        cmbEstado.addActionListener(l);
    }
}
