package com.saltillo.limpieza.views;

import com.saltillo.limpieza.models.dto.ActividadDetalleDTO;
import com.saltillo.limpieza.models.dto.FiltroActividadDTO;
import com.saltillo.limpieza.views.components.RoundedPanel;
import com.saltillo.limpieza.views.components.StatusBadgeRenderer;
import com.saltillo.limpieza.views.components.UiIcons;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

/**
 * Vista de Consulta y Filtrado de Actividades (Integrante 4 - Issue #16).
 * 
 * Cumplimiento del Requisito 11 de la Rúbrica:
 * "Desean consultar la información y un dashboard ejecutivo..."
 * 
 * Arquitectura MVC Estricta:
 * Esta vista es puramente de presentación. Recibe DTOs y emite eventos hacia
 * el ConsultaActividadesController.
 */
public class ConsultaActividadesView extends JPanel {
    private static final long serialVersionUID = 1L;

    // Controles de filtrado
    private JComboBox<String> cmbCuadrilla;
    private JComboBox<String> cmbColonia;
    private JComboBox<String> cmbEstado;
    private JComboBox<String> cmbRangoFecha;
    private JButton btnBuscar;
    private JButton btnLimpiar;

    // Tabla de resultados
    private JTable tblResultados;
    private DefaultTableModel tableModel;
    private JLabel lblConteoResultados;

    // Panel inferior de detalles de la actividad seleccionada
    private JLabel lblDetalleSeleccionado;

    public ConsultaActividadesView() {
        setLayout(new BorderLayout(0, 16));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 26, 20, 26));

        initHeader();
        initFiltrosSection();
        initTablaSection();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel pnlTitulos = new JPanel();
        pnlTitulos.setLayout(new BoxLayout(pnlTitulos, BoxLayout.Y_AXIS));
        pnlTitulos.setOpaque(false);

        JPanel pnlTitleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlTitleRow.setOpaque(false);

        JLabel lblTitulo = new JLabel("Consulta Histórica de Actividades");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(15, 23, 42));
        pnlTitleRow.add(lblTitulo);

        JLabel lblBadge = new JLabel("REQUISITO 11") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(224, 242, 254)); // Azul suave
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblBadge.setForeground(new Color(3, 105, 161));
        lblBadge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        pnlTitleRow.add(lblBadge);

        JLabel lblSubtitulo = new JLabel("Búsqueda y auditoría de servicios de limpieza urbana en Saltillo por cuadrilla, colonia, estado y fecha.");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(100, 116, 139));

        pnlTitulos.add(pnlTitleRow);
        pnlTitulos.add(Box.createVerticalStrut(4));
        pnlTitulos.add(lblSubtitulo);

        headerPanel.add(pnlTitulos, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void initFiltrosSection() {
        RoundedPanel cardFiltros = new RoundedPanel(16, Color.WHITE, new Color(226, 232, 240));
        cardFiltros.setLayout(new GridBagLayout());
        cardFiltros.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 8, 4, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: Etiquetas
        g.gridy = 0;

        g.gridx = 0; g.weightx = 0.25;
        JLabel lblFiltroC = new JLabel("Cuadrilla Asignada:");
        lblFiltroC.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblFiltroC.setForeground(new Color(71, 85, 105));
        cardFiltros.add(lblFiltroC, g);

        g.gridx = 1; g.weightx = 0.25;
        JLabel lblFiltroCol = new JLabel("Colonia Atendida:");
        lblFiltroCol.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblFiltroCol.setForeground(new Color(71, 85, 105));
        cardFiltros.add(lblFiltroCol, g);

        g.gridx = 2; g.weightx = 0.20;
        JLabel lblFiltroEst = new JLabel("Estado Operativo:");
        lblFiltroEst.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblFiltroEst.setForeground(new Color(71, 85, 105));
        cardFiltros.add(lblFiltroEst, g);

        g.gridx = 3; g.weightx = 0.20;
        JLabel lblFiltroFec = new JLabel("Rango de Fechas:");
        lblFiltroFec.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblFiltroFec.setForeground(new Color(71, 85, 105));
        cardFiltros.add(lblFiltroFec, g);

        g.gridx = 4; g.weightx = 0.10;
        cardFiltros.add(new JLabel(""), g);

        // Fila 1: Controles de selección
        g.gridy = 1;

        // 1. Combo Cuadrilla
        g.gridx = 0;
        cmbCuadrilla = new JComboBox<>();
        cmbCuadrilla.addItem("Todas las cuadrillas");
        cmbCuadrilla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cardFiltros.add(cmbCuadrilla, g);

        // 2. Combo Colonia
        g.gridx = 1;
        cmbColonia = new JComboBox<>();
        cmbColonia.addItem("Todas las colonias");
        String[] colsSaltillo = {
                "Centro Histórico", "República Poniente", "República Oriente",
                "Mirasierra", "Saltillo 2000", "San Patricio", "Bellavista",
                "Doctores", "Cumbres", "Zona Industrial", "Zaragoza"
        };
        for (String c : colsSaltillo) {
            cmbColonia.addItem(c);
        }
        cmbColonia.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cardFiltros.add(cmbColonia, g);

        // 3. Combo Estado
        g.gridx = 2;
        cmbEstado = new JComboBox<>(new String[]{"Todos los estados", "COMPLETADA", "EN PROCESO", "PROGRAMADA"});
        cmbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cardFiltros.add(cmbEstado, g);

        // 4. Combo Rango de Fecha
        g.gridx = 3;
        cmbRangoFecha = new JComboBox<>(new String[]{"Histórico completo", "Hoy", "Últimos 7 días", "Últimos 30 días"});
        cmbRangoFecha.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cardFiltros.add(cmbRangoFecha, g);

        // 5. Botones de Acción (Buscar + Limpiar)
        g.gridx = 4;
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlBotones.setOpaque(false);

        btnLimpiar = new JButton("Limpiar") {
            @Override
            protected void paintComponent(Graphics g2d) {
                Graphics2D g2 = (Graphics2D) g2d.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.setColor(new Color(203, 213, 225));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g2d);
            }
        };
        btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnLimpiar.setForeground(new Color(71, 85, 105));
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setContentAreaFilled(false);
        btnLimpiar.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnBuscar = new JButton("Buscar", UiIcons.getSearchIcon(Color.WHITE, 13)) {
            @Override
            protected void paintComponent(Graphics g2d) {
                Graphics2D g2 = (Graphics2D) g2d.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(3, 105, 161) : new Color(2, 132, 199));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g2d);
            }
        };
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setContentAreaFilled(false);
        btnBuscar.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBuscar.setIconTextGap(6);

        pnlBotones.add(btnLimpiar);
        pnlBotones.add(btnBuscar);
        cardFiltros.add(pnlBotones, g);

        // Contenedor intermedio
        JPanel pnlCentro = new JPanel(new BorderLayout(0, 14));
        pnlCentro.setOpaque(false);
        pnlCentro.add(cardFiltros, BorderLayout.NORTH);
        add(pnlCentro, BorderLayout.CENTER);
    }

    private void initTablaSection() {
        RoundedPanel cardResultados = new RoundedPanel(16, Color.WHITE, new Color(226, 232, 240));
        cardResultados.setLayout(new BorderLayout(0, 12));
        cardResultados.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        // Barra superior de la tabla con contador de resultados
        JPanel pnlTopTabla = new JPanel(new BorderLayout());
        pnlTopTabla.setOpaque(false);

        lblConteoResultados = new JLabel("Cargando actividades...");
        lblConteoResultados.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblConteoResultados.setForeground(new Color(15, 23, 42));
        pnlTopTabla.add(lblConteoResultados, BorderLayout.WEST);

        JLabel lblTip = new JLabel("Haz clic en una fila para ver el detalle de supervisión");
        lblTip.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblTip.setForeground(new Color(100, 116, 139));
        pnlTopTabla.add(lblTip, BorderLayout.EAST);

        cardResultados.add(pnlTopTabla, BorderLayout.NORTH);

        // Columnas completas de auditoría
        String[] columnas = {"ID", "Fecha/Hora", "Cuadrilla Asignada", "Colonias Atendidas", "Tipo de Servicio", "Lugar Específico", "Estado", "Observaciones"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblResultados = new JTable(tableModel);
        tblResultados.setRowHeight(36);
        tblResultados.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblResultados.setShowVerticalLines(false);
        tblResultados.setGridColor(new Color(241, 245, 249));
        tblResultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblResultados.setFillsViewportHeight(true);

        tblResultados.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblResultados.getTableHeader().setPreferredSize(new Dimension(0, 36));
        tblResultados.getTableHeader().setBackground(new Color(241, 245, 249));
        tblResultados.getTableHeader().setForeground(new Color(51, 65, 85));
        tblResultados.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(203, 213, 225)));

        // Distribución de anchos de columna
        tblResultados.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        tblResultados.getColumnModel().getColumn(1).setPreferredWidth(110); // Fecha/Hora
        tblResultados.getColumnModel().getColumn(2).setPreferredWidth(160); // Cuadrilla
        tblResultados.getColumnModel().getColumn(3).setPreferredWidth(165); // Colonias
        tblResultados.getColumnModel().getColumn(4).setPreferredWidth(170); // Tipo
        tblResultados.getColumnModel().getColumn(5).setPreferredWidth(150); // Lugar
        tblResultados.getColumnModel().getColumn(6).setPreferredWidth(105); // Estado
        tblResultados.getColumnModel().getColumn(7).setPreferredWidth(180); // Observaciones

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tblResultados.getColumnModel().getColumn(0).setCellRenderer(center);
        tblResultados.getColumnModel().getColumn(1).setCellRenderer(center);
        tblResultados.getColumnModel().getColumn(6).setCellRenderer(new StatusBadgeRenderer());

        JScrollPane scroll = new JScrollPane(tblResultados);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        cardResultados.add(scroll, BorderLayout.CENTER);

        // Panel de detalle inferior al seleccionar una fila
        JPanel pnlBottomDetalle = new JPanel(new BorderLayout());
        pnlBottomDetalle.setOpaque(false);
        pnlBottomDetalle.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(241, 245, 249)));

        lblDetalleSeleccionado = new JLabel("Seleccione una actividad de la tabla para ver notas de campo.");
        lblDetalleSeleccionado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDetalleSeleccionado.setForeground(new Color(71, 85, 105));
        lblDetalleSeleccionado.setBorder(BorderFactory.createEmptyBorder(8, 4, 2, 4));
        pnlBottomDetalle.add(lblDetalleSeleccionado, BorderLayout.CENTER);

        cardResultados.add(pnlBottomDetalle, BorderLayout.SOUTH);

        // Listener de selección de fila
        tblResultados.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblResultados.getSelectedRow();
                if (row >= 0) {
                    String cuadrilla = String.valueOf(tableModel.getValueAt(row, 2));
                    String colonias = String.valueOf(tableModel.getValueAt(row, 3));
                    String lugar = String.valueOf(tableModel.getValueAt(row, 5));
                    String notas = String.valueOf(tableModel.getValueAt(row, 7));
                    lblDetalleSeleccionado.setText("<html><b>Detalle:</b> " + cuadrilla + " en <i>" + colonias + "</i> (" + lugar + "). <b>Notas:</b> " + notas + "</html>");
                }
            }
        });

        JPanel pnlCentro = (JPanel) getComponent(1);
        pnlCentro.add(cardResultados, BorderLayout.CENTER);
    }

    // =========================================================================
    // MÉTODOS PÚBLICOS DE VISTA (STRICT MVC)
    // =========================================================================

    public void poblarCuadrillas(List<String> nombresCuadrillas) {
        cmbCuadrilla.removeAllItems();
        cmbCuadrilla.addItem("Todas las cuadrillas");
        if (nombresCuadrillas != null) {
            for (String n : nombresCuadrillas) {
                cmbCuadrilla.addItem(n);
            }
        }
    }

    public void mostrarResultados(List<ActividadDetalleDTO> actividades) {
        tableModel.setRowCount(0);
        int total = (actividades != null) ? actividades.size() : 0;
        lblConteoResultados.setText("Se encontraron " + total + " actividad" + (total != 1 ? "es" : ""));

        if (actividades == null || actividades.isEmpty()) {
            lblDetalleSeleccionado.setText("No se encontraron actividades que coincidan con los filtros seleccionados.");
            return;
        }

        for (ActividadDetalleDTO a : actividades) {
            tableModel.addRow(new Object[]{
                    a.getIdActividad(),
                    a.getFechaHoraFormateada(),
                    a.getNombreCuadrilla(),
                    a.getColoniasFormateadas(),
                    a.getTipoLimpieza(),
                    a.getLugarEspecifico(),
                    a.getEstado(),
                    a.getObservaciones()
            });
        }

        // Seleccionar primera fila por defecto
        if (tblResultados.getRowCount() > 0) {
            tblResultados.setRowSelectionInterval(0, 0);
        }
    }

    /**
     * Construye y retorna el DTO con los criterios seleccionados en los controles.
     */
    public FiltroActividadDTO obtenerFiltroActual(java.util.function.Function<String, Integer> cuadrillaIdResolver) {
        FiltroActividadDTO filtro = new FiltroActividadDTO();

        // 1. Cuadrilla
        int indexC = cmbCuadrilla.getSelectedIndex();
        if (indexC > 0) {
            String nombreC = (String) cmbCuadrilla.getSelectedItem();
            if (cuadrillaIdResolver != null) {
                filtro.setIdCuadrilla(cuadrillaIdResolver.apply(nombreC));
            }
        }

        // 2. Colonia
        int indexCol = cmbColonia.getSelectedIndex();
        if (indexCol > 0) {
            filtro.setColonia((String) cmbColonia.getSelectedItem());
        }

        // 3. Estado
        int indexEst = cmbEstado.getSelectedIndex();
        if (indexEst > 0) {
            filtro.setEstado((String) cmbEstado.getSelectedItem());
        }

        // 4. Fechas
        int indexFec = cmbRangoFecha.getSelectedIndex();
        LocalDate hoy = LocalDate.now();
        if (indexFec == 1) { // Hoy
            filtro.setFechaInicio(hoy);
            filtro.setFechaFin(hoy);
        } else if (indexFec == 2) { // Últimos 7 días
            filtro.setFechaInicio(hoy.minusDays(7));
            filtro.setFechaFin(hoy);
        } else if (indexFec == 3) { // Últimos 30 días
            filtro.setFechaInicio(hoy.minusDays(30));
            filtro.setFechaFin(hoy);
        }

        return filtro;
    }

    public void limpiarFiltros() {
        cmbCuadrilla.setSelectedIndex(0);
        cmbColonia.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        cmbRangoFecha.setSelectedIndex(0);
    }

    public void setBuscarListener(ActionListener l) {
        btnBuscar.addActionListener(l);
    }

    public void setLimpiarListener(ActionListener l) {
        btnLimpiar.addActionListener(l);
    }
}
