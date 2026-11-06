package com.saltillo.limpieza.views;

import com.saltillo.limpieza.models.dto.ActividadEventDTO;
import com.saltillo.limpieza.models.dto.CuadrillaResumenDTO;
import com.saltillo.limpieza.models.dto.DashboardKpisDTO;
import com.saltillo.limpieza.views.components.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Vista del Dashboard Ejecutivo (Integrante 4) - Rediseño Perfeccionado.
 */
public class DashboardView extends JPanel {
    private static final long serialVersionUID = 1L;

    // Componentes de Indicadores Rápidos (KPIs)
    private KpiCardComponent cardTotalActividades;
    private KpiCardComponent cardCuadrillaLider;
    private KpiCardComponent cardCuadrillasDisponibles;
    private KpiCardComponent cardPromedioActividades;

    private JLabel lblUltimaActualizacion;

    // Tabla de desempeño
    private JTable tblCuadrillas;
    private DefaultTableModel tableModel;

    // Feed / Log de eventos Observer
    private DefaultListModel<ActividadEventDTO> listModelEventos;
    private JList<ActividadEventDTO> listEventos;
    private JPanel pnlFeedContainer;
    private CardLayout feedCardLayout;
    private JPanel pnlEmptyState;

    // Botones de acción
    private JButton btnSimularActividad;
    private JButton btnRefrescar;
    private JButton btnSimularDesdeVacio;

    public DashboardView() {
        setLayout(new BorderLayout(0, 16));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 26, 20, 26));

        initHeader();
        initKpisSection();
        initDataSection();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 6));
        headerPanel.setOpaque(false);

        // --- FILA 1: Título + Badge + Botones con renderizado vectorial ---
        JPanel row1 = new JPanel(new BorderLayout(12, 0));
        row1.setOpaque(false);

        JPanel pnlTitleBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlTitleBadge.setOpaque(false);

        JLabel lblTitulo = new JLabel("Dashboard Ejecutivo de Desempeño");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(15, 23, 42));
        pnlTitleBadge.add(lblTitulo);

        JLabel lblBadgeLive = new JLabel("● EN LÍNEA • OBSERVER ACTIVO") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(220, 252, 231));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblBadgeLive.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblBadgeLive.setForeground(new Color(22, 101, 52));
        lblBadgeLive.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        pnlTitleBadge.add(lblBadgeLive);

        row1.add(pnlTitleBadge, BorderLayout.WEST);

        // Botones de acción
        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlAcciones.setOpaque(false);

        // Botón Refrescar con renderizado custom
        btnRefrescar = new JButton("Refrescar", UiIcons.getRefreshIcon(new Color(51, 65, 85), 14)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(new Color(203, 213, 225));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnRefrescar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefrescar.setForeground(new Color(51, 65, 85));
        btnRefrescar.setFocusPainted(false);
        btnRefrescar.setContentAreaFilled(false);
        btnRefrescar.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnRefrescar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefrescar.setIconTextGap(8);

        // Botón Simular con renderizado vibrante Sky-600 y texto 100% blanco (SIN caracteres emoji)
        btnSimularActividad = new JButton("Simular Actividad (Observer)", UiIcons.getLightningIcon(Color.WHITE, 14)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isRollover() ? new Color(3, 105, 161) : new Color(2, 132, 199);
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSimularActividad.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSimularActividad.setForeground(Color.WHITE);
        btnSimularActividad.setFocusPainted(false);
        btnSimularActividad.setContentAreaFilled(false);
        btnSimularActividad.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btnSimularActividad.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSimularActividad.setIconTextGap(8);

        pnlAcciones.add(btnRefrescar);
        pnlAcciones.add(btnSimularActividad);
        row1.add(pnlAcciones, BorderLayout.EAST);

        // --- FILA 2: Subtítulo municipal + Sincronización ---
        JPanel row2 = new JPanel(new BorderLayout());
        row2.setOpaque(false);

        JLabel lblSubtitulo = new JLabel("Municipio de Saltillo • Supervisión de cuadrillas de limpieza urbana y evidencias en tiempo real");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(100, 116, 139));
        row2.add(lblSubtitulo, BorderLayout.WEST);

        lblUltimaActualizacion = new JLabel("Sincronización: --");
        lblUltimaActualizacion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblUltimaActualizacion.setForeground(new Color(100, 116, 139));
        row2.add(lblUltimaActualizacion, BorderLayout.EAST);

        headerPanel.add(row1);
        headerPanel.add(row2);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void initKpisSection() {
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        kpiGrid.setOpaque(false);

        cardTotalActividades = new KpiCardComponent(
                "Total Actividades",
                "0",
                "En colonias atendidas",
                new Color(2, 132, 199),
                UiIcons.getActividadesIcon(new Color(2, 132, 199), 16)
        );

        cardCuadrillaLider = new KpiCardComponent(
                "Cuadrilla Líder",
                "--",
                "Mayor volumen registrado",
                new Color(16, 185, 129),
                UiIcons.getCuadrillasIcon(new Color(16, 185, 129), 16)
        );

        cardCuadrillasDisponibles = new KpiCardComponent(
                "Disponibilidad",
                "0 / 0",
                "Listas para despacho",
                new Color(245, 158, 11),
                UiIcons.getDashboardIcon(new Color(245, 158, 11), 16)
        );

        cardPromedioActividades = new KpiCardComponent(
                "Promedio / Cuadrilla",
                "0.0",
                "Intervenciones por equipo",
                new Color(139, 92, 246),
                UiIcons.getLightningIcon(new Color(139, 92, 246), 16)
        );

        kpiGrid.add(cardTotalActividades);
        kpiGrid.add(cardCuadrillaLider);
        kpiGrid.add(cardCuadrillasDisponibles);
        kpiGrid.add(cardPromedioActividades);

        JPanel pnlCentro = new JPanel(new BorderLayout(0, 16));
        pnlCentro.setOpaque(false);
        pnlCentro.add(kpiGrid, BorderLayout.NORTH);

        add(pnlCentro, BorderLayout.CENTER);
    }

    private void initDataSection() {
        JPanel splitPanel = new JPanel(new GridBagLayout());
        splitPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // -------------------------------------------------------------
        // PANEL IZQUIERDO: TABLA CALIBRADA DE CUADRILLAS (68% peso)
        // -------------------------------------------------------------
        gbc.gridx = 0;
        gbc.weightx = 0.68;
        gbc.insets = new Insets(0, 0, 0, 14);

        RoundedPanel pnlTablaCard = new RoundedPanel(16, Color.WHITE, new Color(226, 232, 240));
        pnlTablaCard.setLayout(new BorderLayout(0, 12));
        pnlTablaCard.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel pnlTablaHeader = new JPanel(new BorderLayout());
        pnlTablaHeader.setOpaque(false);

        JLabel lblTituloTabla = new JLabel("Resumen Operativo por Cuadrilla de Limpieza");
        lblTituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTituloTabla.setForeground(new Color(15, 23, 42));

        JLabel lblSubTabla = new JLabel("Estadísticas consolidadas de servicios públicos en Saltillo");
        lblSubTabla.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSubTabla.setForeground(new Color(100, 116, 139));

        pnlTablaHeader.add(lblTituloTabla, BorderLayout.NORTH);
        pnlTablaHeader.add(lblSubTabla, BorderLayout.SOUTH);
        pnlTablaCard.add(pnlTablaHeader, BorderLayout.NORTH);

        // Encabezados limpios y directos para evitar cualquier truncamiento
        String[] columnas = {"ID", "Cuadrilla", "Jefe", "Zona", "Total", "Meta", "Estado"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblCuadrillas = new JTable(tableModel);
        tblCuadrillas.setRowHeight(38);
        tblCuadrillas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblCuadrillas.setShowVerticalLines(false);
        tblCuadrillas.setGridColor(new Color(241, 245, 249));
        tblCuadrillas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCuadrillas.setFillsViewportHeight(true);

        tblCuadrillas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblCuadrillas.getTableHeader().setPreferredSize(new Dimension(0, 36));
        tblCuadrillas.getTableHeader().setBackground(new Color(241, 245, 249));
        tblCuadrillas.getTableHeader().setForeground(new Color(51, 65, 85));
        tblCuadrillas.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(203, 213, 225)));

        // Anchos perfectamente balanceados
        tblCuadrillas.getColumnModel().getColumn(0).setPreferredWidth(30);  // ID
        tblCuadrillas.getColumnModel().getColumn(1).setPreferredWidth(160); // Cuadrilla
        tblCuadrillas.getColumnModel().getColumn(2).setPreferredWidth(130); // Jefe
        tblCuadrillas.getColumnModel().getColumn(3).setPreferredWidth(140); // Zona
        tblCuadrillas.getColumnModel().getColumn(4).setPreferredWidth(50);  // Total
        tblCuadrillas.getColumnModel().getColumn(5).setPreferredWidth(110); // Meta
        tblCuadrillas.getColumnModel().getColumn(6).setPreferredWidth(115); // Estado

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblCuadrillas.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblCuadrillas.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        tblCuadrillas.getColumnModel().getColumn(5).setCellRenderer(new ProgressBarRenderer());
        tblCuadrillas.getColumnModel().getColumn(6).setCellRenderer(new StatusBadgeRenderer());

        JScrollPane scrollTabla = new JScrollPane(tblCuadrillas);
        scrollTabla.setBorder(BorderFactory.createEmptyBorder());
        scrollTabla.getViewport().setBackground(Color.WHITE);
        pnlTablaCard.add(scrollTabla, BorderLayout.CENTER);

        splitPanel.add(pnlTablaCard, gbc);

        // -------------------------------------------------------------
        // PANEL DERECHO: FEED EN VIVO PATRÓN OBSERVER (32% peso)
        // -------------------------------------------------------------
        gbc.gridx = 1;
        gbc.weightx = 0.32;
        gbc.insets = new Insets(0, 0, 0, 0);

        RoundedPanel pnlFeedCard = new RoundedPanel(16, Color.WHITE, new Color(226, 232, 240));
        pnlFeedCard.setLayout(new BorderLayout(0, 12));
        pnlFeedCard.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel pnlFeedHeader = new JPanel(new BorderLayout());
        pnlFeedHeader.setOpaque(false);

        JLabel lblFeedTitulo = new JLabel("● Eventos en Vivo (Observer)");
        lblFeedTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFeedTitulo.setForeground(new Color(16, 185, 129));

        JLabel lblFeedInfo = new JLabel("<html><body style='width: 200px; color: #64748b; font-size: 11px;'>" +
                "Suscrito a <b>ActividadEventBus</b>. Recibe notificaciones automáticas sin polling.</body></html>");

        pnlFeedHeader.add(lblFeedTitulo, BorderLayout.NORTH);
        pnlFeedHeader.add(Box.createVerticalStrut(4), BorderLayout.CENTER);
        pnlFeedHeader.add(lblFeedInfo, BorderLayout.SOUTH);
        pnlFeedCard.add(pnlFeedHeader, BorderLayout.NORTH);

        feedCardLayout = new CardLayout();
        pnlFeedContainer = new JPanel(feedCardLayout);
        pnlFeedContainer.setOpaque(false);

        // Estado Vacío Ilustrado
        pnlEmptyState = new JPanel(new GridBagLayout());
        pnlEmptyState.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0; g.insets = new Insets(6, 6, 6, 6);

        JLabel lblEmptyIcon = new JLabel(UiIcons.getLightningIcon(new Color(148, 163, 184), 30));
        pnlEmptyState.add(lblEmptyIcon, g);

        g.gridy = 1;
        JLabel lblEmptyTxt = new JLabel("En espera de actividades...");
        lblEmptyTxt.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEmptyTxt.setForeground(new Color(100, 116, 139));
        pnlEmptyState.add(lblEmptyTxt, g);

        g.gridy = 2;
        JLabel lblEmptySub = new JLabel("<html><center style='width: 180px; color: #94a3b8; font-size: 11px;'>" +
                "Presiona el botón de simulación para observar el patrón Observer en tiempo real.</center></html>");
        pnlEmptyState.add(lblEmptySub, g);

        g.gridy = 3;
        btnSimularDesdeVacio = new JButton("Simular Primera Actividad", UiIcons.getLightningIcon(new Color(2, 132, 199), 12));
        btnSimularDesdeVacio.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnSimularDesdeVacio.setForeground(new Color(2, 132, 199));
        btnSimularDesdeVacio.setBackground(new Color(240, 249, 255));
        btnSimularDesdeVacio.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(186, 230, 253), 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btnSimularDesdeVacio.setFocusPainted(false);
        btnSimularDesdeVacio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlEmptyState.add(btnSimularDesdeVacio, g);

        pnlFeedContainer.add(pnlEmptyState, "EMPTY");

        // Lista de Tarjetas
        listModelEventos = new DefaultListModel<>();
        listEventos = new JList<>(listModelEventos);
        listEventos.setCellRenderer(new EventCardRenderer());
        listEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listEventos.setBackground(new Color(248, 250, 252));
        listEventos.setBorder(BorderFactory.createEmptyBorder());

        JScrollPane scrollFeed = new JScrollPane(listEventos);
        scrollFeed.setBorder(BorderFactory.createEmptyBorder());
        scrollFeed.getViewport().setBackground(new Color(248, 250, 252));

        pnlFeedContainer.add(scrollFeed, "LIST");
        feedCardLayout.show(pnlFeedContainer, "EMPTY");

        pnlFeedCard.add(pnlFeedContainer, BorderLayout.CENTER);
        splitPanel.add(pnlFeedCard, gbc);

        JPanel pnlCentro = (JPanel) getComponent(1);
        pnlCentro.add(splitPanel, BorderLayout.CENTER);
    }

    public void mostrarKpis(DashboardKpisDTO kpis) {
        if (kpis == null) return;

        cardTotalActividades.setValor(String.valueOf(kpis.getTotalActividadesEjecutadas()));
        cardCuadrillaLider.setValor(kpis.getCuadrillaMasActiva());
        cardCuadrillaLider.setSubtitulo(kpis.getActividadesCuadrillaMasActiva() + " intervenciones");
        cardCuadrillasDisponibles.setValor(kpis.getCuadrillasDisponibles() + " / " + kpis.getTotalCuadrillas());
        cardPromedioActividades.setValor(String.valueOf(kpis.getPromedioActividadesPorCuadrilla()));

        lblUltimaActualizacion.setText("Sincronización: " + kpis.getUltimaActualizacion());
    }

    public void mostrarResumenCuadrillas(List<CuadrillaResumenDTO> lista) {
        tableModel.setRowCount(0);
        if (lista == null) return;

        for (CuadrillaResumenDTO c : lista) {
            tableModel.addRow(new Object[]{
                    c.getIdCuadrilla(),
                    c.getNombreCuadrilla(),
                    c.getNombreJefe(),
                    c.getZonaAsignada(),
                    c.getTotalActividades(),
                    c.getPorcentajeCumplimiento(),
                    c.getEstado()
            });
        }
    }

    public void agregarEventoObserver(ActividadEventDTO evento) {
        if (listModelEventos.isEmpty()) {
            feedCardLayout.show(pnlFeedContainer, "LIST");
        }
        listModelEventos.add(0, evento);
        if (listModelEventos.size() > 40) {
            listModelEventos.remove(40);
        }
    }

    public void setSimularActividadListener(ActionListener listener) {
        btnSimularActividad.addActionListener(listener);
        btnSimularDesdeVacio.addActionListener(listener);
    }

    public void setRefrescarListener(ActionListener listener) {
        btnRefrescar.addActionListener(listener);
    }
}
