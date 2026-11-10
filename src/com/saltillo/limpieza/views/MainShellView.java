package com.saltillo.limpieza.views;

import com.saltillo.limpieza.views.components.UiIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Shell Principal de la Aplicación (Integrante 4) - Rediseño Moderno Enterprise.
 * 
 * Correcciones y Mejoras:
 * 1. Sustitución de caracteres emoji (que generaban cuadros '▯') por iconos vectoriales Java2D.
 * 2. Barra lateral estilo GitHub / Linear / Tailwind con avatar de usuario y estado 'En Línea'.
 * 3. Botones con indicador activo lateral e iluminación al pasar el cursor (hover).
 * 4. Botón de Cierre de Sesión con acento de seguridad destacado.
 */
public class MainShellView extends JFrame {
    private static final long serialVersionUID = 1L;

    // Paleta Enterprise Dark Slate
    private static final Color COLOR_SIDEBAR = new Color(15, 23, 42);          // Slate-900 profundo
    private static final Color COLOR_SIDEBAR_CARD = new Color(30, 41, 59);     // Slate-800
    private static final Color COLOR_NAV_ACTIVE = new Color(2, 132, 199);       // Sky-600
    private static final Color COLOR_NAV_HOVER = new Color(30, 41, 59);        // Slate-800
    private static final Color COLOR_TEXT_MUTED = new Color(148, 163, 184);    // Slate-400
    private static final Color COLOR_TEXT_NORMAL = new Color(226, 232, 240);   // Slate-200

    // Content Host dinámico basado en CardLayout
    private final CardLayout cardLayout;
    private final JPanel pnlContentHost;

    // Control de estado de botones
    private final Map<String, JButton> navButtons = new HashMap<>();
    private final Map<String, Icon> navIconsNormal = new HashMap<>();
    private final Map<String, Icon> navIconsActive = new HashMap<>();
    private String moduloActivoActual = "";

    // Acciones de navegación
    private JButton btnNavDashboard;
    private JButton btnNavConsulta;
    private JButton btnNavActividades;
    private JButton btnNavCuadrillas;
    private JButton btnNavColonias;
    private JButton btnCerrarSesion;

    private JLabel lblUsuarioSesion;

    public MainShellView() {
        super("Municipio de Saltillo • Sistema de Gestión de Actividades de Limpieza Urbana");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1100, 720));
        setLocationRelativeTo(null);

        // Content Host central
        cardLayout = new CardLayout();
        pnlContentHost = new JPanel(cardLayout);
        pnlContentHost.setBackground(new Color(248, 250, 252));

        // Layout general
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(construirSidebar(), BorderLayout.WEST);
        getContentPane().add(pnlContentHost, BorderLayout.CENTER);
    }

    /**
     * Construye la barra lateral de navegación con estilo profesional y moderno.
     */
    private JPanel construirSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setBorder(BorderFactory.createEmptyBorder(22, 16, 22, 16));

        // --- 1. CABECERA: Logotipo y Marca Municipal ---
        JPanel pnlHeader = new JPanel();
        pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.Y_AXIS));
        pnlHeader.setOpaque(false);

        // Insignia municipal con icono
        JPanel pnlLogoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlLogoRow.setOpaque(false);

        // Icono circular de la ciudad
        JPanel pnlEmblema = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(2, 132, 199)); // Sky-600
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                String txt = "S";
                int x = (getWidth() - fm.stringWidth(txt)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(txt, x, y);
                g2.dispose();
            }
        };
        pnlEmblema.setPreferredSize(new Dimension(30, 30));
        pnlEmblema.setOpaque(false);
        pnlLogoRow.add(pnlEmblema);

        JPanel pnlLogoTextos = new JPanel();
        pnlLogoTextos.setLayout(new BoxLayout(pnlLogoTextos, BoxLayout.Y_AXIS));
        pnlLogoTextos.setOpaque(false);

        JLabel lblLogo = new JLabel("SALTILLO LIMPIO");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLogo.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Servicios Primarios • V2.0");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(COLOR_TEXT_MUTED);

        pnlLogoTextos.add(lblLogo);
        pnlLogoTextos.add(lblSub);
        pnlLogoRow.add(pnlLogoTextos);

        pnlHeader.add(pnlLogoRow);
        pnlHeader.add(Box.createVerticalStrut(20));

        // --- 2. TARJETA DE USUARIO: Sesión activa y rol con Avatar ---
        JPanel pnlUserCard = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_SIDEBAR_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pnlUserCard.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        pnlUserCard.setOpaque(false);

        // Avatar circular con iniciales
        JPanel pnlAvatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(16, 185, 129)); // Verde activo
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                String txt = "AG";
                int x = (getWidth() - fm.stringWidth(txt)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(txt, x, y);
                g2.dispose();
            }
        };
        pnlAvatar.setPreferredSize(new Dimension(28, 28));
        pnlAvatar.setOpaque(false);
        pnlUserCard.add(pnlAvatar, BorderLayout.WEST);

        JPanel pnlUserInfo = new JPanel();
        pnlUserInfo.setLayout(new BoxLayout(pnlUserInfo, BoxLayout.Y_AXIS));
        pnlUserInfo.setOpaque(false);

        JLabel lblRol = new JLabel("ADMIN MUNICIPAL");
        lblRol.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblRol.setForeground(new Color(56, 189, 248)); // Celeste

        lblUsuarioSesion = new JLabel("admin.saltillo@municipio.gob");
        lblUsuarioSesion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblUsuarioSesion.setForeground(COLOR_TEXT_NORMAL);

        pnlUserInfo.add(lblRol);
        pnlUserInfo.add(Box.createVerticalStrut(2));
        pnlUserInfo.add(lblUsuarioSesion);
        pnlUserCard.add(pnlUserInfo, BorderLayout.CENTER);

        pnlHeader.add(pnlUserCard);
        pnlHeader.add(Box.createVerticalStrut(24));

        sidebar.add(pnlHeader, BorderLayout.NORTH);

        // --- 3. MENÚ DE NAVEGACIÓN MODULAR ---
        JPanel pnlMenu = new JPanel();
        pnlMenu.setLayout(new BoxLayout(pnlMenu, BoxLayout.Y_AXIS));
        pnlMenu.setOpaque(false);

        JLabel lblMenuTag = new JLabel("MÓDULOS DEL SISTEMA");
        lblMenuTag.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblMenuTag.setForeground(new Color(100, 116, 139));
        lblMenuTag.setBorder(BorderFactory.createEmptyBorder(0, 10, 8, 0));
        pnlMenu.add(lblMenuTag);

        btnNavDashboard = crearBotonNavegacion("Dashboard Ejecutivo", "DASHBOARD",
                UiIcons.getDashboardIcon(COLOR_TEXT_MUTED, 16),
                UiIcons.getDashboardIcon(Color.WHITE, 16));

        btnNavConsulta = crearBotonNavegacion("Consulta Actividades", "CONSULTA",
                UiIcons.getSearchIcon(COLOR_TEXT_MUTED, 16),
                UiIcons.getSearchIcon(Color.WHITE, 16));

        btnNavActividades = crearBotonNavegacion("Registro Actividades", "ACTIVIDADES",
                UiIcons.getActividadesIcon(COLOR_TEXT_MUTED, 16),
                UiIcons.getActividadesIcon(Color.WHITE, 16));

        btnNavCuadrillas = crearBotonNavegacion("Catálogo Cuadrillas", "CUADRILLAS",
                UiIcons.getCuadrillasIcon(COLOR_TEXT_MUTED, 16),
                UiIcons.getCuadrillasIcon(Color.WHITE, 16));

        btnNavColonias = crearBotonNavegacion("Colonias (Sepomex)", "COLONIAS",
                UiIcons.getColoniasIcon(COLOR_TEXT_MUTED, 16),
                UiIcons.getColoniasIcon(Color.WHITE, 16));

        pnlMenu.add(btnNavDashboard);
        pnlMenu.add(Box.createVerticalStrut(6));
        pnlMenu.add(btnNavConsulta);
        pnlMenu.add(Box.createVerticalStrut(6));
        pnlMenu.add(btnNavActividades);
        pnlMenu.add(Box.createVerticalStrut(6));
        pnlMenu.add(btnNavCuadrillas);
        pnlMenu.add(Box.createVerticalStrut(6));
        pnlMenu.add(btnNavColonias);

        sidebar.add(pnlMenu, BorderLayout.CENTER);

        // --- 4. PIE DE BARRA LATERAL: Cerrar Sesión Segura ---
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setOpaque(false);

        btnCerrarSesion = new JButton("Cerrar Sesión Segura", UiIcons.getLogoutIcon(new Color(248, 113, 113), 16)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(69, 10, 10) : COLOR_SIDEBAR_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCerrarSesion.setForeground(new Color(248, 113, 113)); // Rojo coral
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        btnCerrarSesion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrarSesion.setHorizontalAlignment(SwingConstants.LEFT);
        btnCerrarSesion.setIconTextGap(10);
        btnCerrarSesion.setContentAreaFilled(false);

        pnlFooter.add(btnCerrarSesion, BorderLayout.SOUTH);
        sidebar.add(pnlFooter, BorderLayout.SOUTH);

        return sidebar;
    }

    /**
     * Construye un botón de navegación con soporte para iconos vectoriales y estados activos.
     */
    private JButton crearBotonNavegacion(String texto, String claveModulo, Icon iconNormal, Icon iconActive) {
        JButton btn = new JButton(texto, iconNormal) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean isActive = claveModulo.equalsIgnoreCase(moduloActivoActual);
                boolean isHover = getModel().isRollover();

                if (isActive) {
                    g2.setColor(COLOR_NAV_ACTIVE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                } else if (isHover) {
                    g2.setColor(COLOR_NAV_HOVER);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(COLOR_TEXT_MUTED);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(12);
        btn.setContentAreaFilled(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        navButtons.put(claveModulo, btn);
        navIconsNormal.put(claveModulo, iconNormal);
        navIconsActive.put(claveModulo, iconActive);

        return btn;
    }

    public void registrarModulo(String clave, JComponent vista) {
        pnlContentHost.add(vista, clave);
    }

    public void mostrarModulo(String clave) {
        cardLayout.show(pnlContentHost, clave);
        this.moduloActivoActual = clave;

        // Actualizar iconos y colores según módulo seleccionado
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            String k = entry.getKey();
            JButton b = entry.getValue();
            if (k.equalsIgnoreCase(clave)) {
                b.setForeground(Color.WHITE);
                b.setIcon(navIconsActive.get(k));
            } else {
                b.setForeground(COLOR_TEXT_MUTED);
                b.setIcon(navIconsNormal.get(k));
            }
        }
        repaint();
    }

    public String getModuloActivoActual() {
        return moduloActivoActual;
    }

    public void setUsuarioSesion(String textoUsuario) {
        lblUsuarioSesion.setText(textoUsuario);
    }

    public void setNavDashboardListener(ActionListener al) {
        btnNavDashboard.addActionListener(al);
    }

    public void setNavConsultaListener(ActionListener al) {
        btnNavConsulta.addActionListener(al);
    }

    public void setNavActividadesListener(ActionListener al) {
        btnNavActividades.addActionListener(al);
    }

    public void setNavCuadrillasListener(ActionListener al) {
        btnNavCuadrillas.addActionListener(al);
    }

    public void setNavColoniasListener(ActionListener al) {
        btnNavColonias.addActionListener(al);
    }

    public void setCerrarSesionListener(ActionListener al) {
        btnCerrarSesion.addActionListener(al);
    }

    public void setCerrarVentanaListener(WindowListener wl) {
        addWindowListener(wl);
    }
}
