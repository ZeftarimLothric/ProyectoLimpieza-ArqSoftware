package com.saltillo.limpieza.views;

import com.saltillo.limpieza.models.dto.ActividadEventDTO;
import com.saltillo.limpieza.patterns.observer.ActividadEventBus;
import com.saltillo.limpieza.repositories.IDashboardRepository;
import com.saltillo.limpieza.views.components.RoundedPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Vista contenedora modular tipo "Placeholder" para los módulos de los
 * Integrantes 1, 2 y 3.
 * 
 * Permite que la aplicación sea 100% navegable y demostrable antes de que los
 * compañeros
 * integren su código final en Git. Además, el módulo de "Registro de
 * Actividades (Integrante 3)"
 * incluye un simulador de formulario que dispara eventos en el
 * ActividadEventBus,
 * comprobando el patrón Observer entre módulos independientes.
 */
public class PlaceholderModuleView extends JPanel {
    private static final long serialVersionUID = 1L;

    private final String claveModulo;
    private final String tituloModulo;
    private final String integranteResponsable;
    private final String descripcionTareas;
    private final IDashboardRepository repository;

    // Componentes del simulador de registro (para Integrante 3)
    private JComboBox<String> cmbCuadrillas;
    private JComboBox<String> cmbColonias;
    private JComboBox<String> cmbTipoLimpieza;
    private JTextField txtObservaciones;
    private JButton btnRegistrarActividad;

    public PlaceholderModuleView(String claveModulo,
            String tituloModulo,
            String integranteResponsable,
            String descripcionTareas,
            IDashboardRepository repository) {
        this.claveModulo = claveModulo;
        this.tituloModulo = tituloModulo;
        this.integranteResponsable = integranteResponsable;
        this.descripcionTareas = descripcionTareas;
        this.repository = repository;

        setLayout(new BorderLayout(0, 20));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        initHeader();
        initContent();
    }

    private void initHeader() {
        JPanel pnlHeader = new JPanel();
        pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.Y_AXIS));
        pnlHeader.setOpaque(false);

        JLabel lblTitulo = new JLabel(tituloModulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(15, 23, 42));

        JLabel lblResponsable = new JLabel("Módulo a cargo de: " + integranteResponsable);
        lblResponsable.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblResponsable.setForeground(new Color(2, 132, 199)); // Azul cielo

        pnlHeader.add(lblTitulo);
        pnlHeader.add(Box.createVerticalStrut(4));
        pnlHeader.add(lblResponsable);

        add(pnlHeader, BorderLayout.NORTH);
    }

    private void initContent() {
        RoundedPanel card = new RoundedPanel(14, Color.WHITE, new Color(226, 232, 240));
        card.setLayout(new BorderLayout(0, 20));
        card.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Descripción técnica de la responsabilidad del integrante
        JLabel lblDesc = new JLabel("<html><body style='width: 600px; color: #475569; font-size: 13px;'>" +
                "<b>Especificación Arquitectónica:</b><br>" + descripcionTareas + "<br><br>" +
                "<i>Nota:</i> Este panel está montado sobre el contenedor " +
                "<b>MainShell</b>. Cuando el integrante responsable finalice su JPanel, simplemente se sustituye " +
                "en la inicialización del controlador de navegación sin alterar el Shell ni el Dashboard.</body></html>");
        card.add(lblDesc, BorderLayout.NORTH);

        // Si es el módulo de Registro de Actividades (Integrante 3), dotarlo de un
        // formulario funcional
        if ("ACTIVIDADES".equalsIgnoreCase(claveModulo)) {
            JPanel pnlForm = construirFormularioActividad();
            card.add(pnlForm, BorderLayout.CENTER);
        } else {
            // Panel informativo para los módulos 1 y 2
            JPanel pnlInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
            pnlInfo.setOpaque(false);
            JLabel lblEstado = new JLabel(
                    "✓ Contrato de interfaz e inyección de dependencias listo para recibir el módulo.");
            lblEstado.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            lblEstado.setForeground(new Color(16, 185, 129));
            pnlInfo.add(lblEstado);
            card.add(pnlInfo, BorderLayout.CENTER);
        }

        add(card, BorderLayout.CENTER);
    }

    /**
     * Construye un simulador del formulario de Integrante 3 para demostrar
     * la publicación de eventos al ActividadEventBus (Patrón Observer).
     */
    private JPanel construirFormularioActividad() {
        RoundedPanel pnlForm = new RoundedPanel(12, new Color(241, 245, 249), new Color(203, 213, 225));
        pnlForm.setLayout(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblBanner = new JLabel("Simulador de Registro de Actividad (Disparador de Evento Observer)");
        lblBanner.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblBanner.setForeground(new Color(30, 41, 59));
        g.gridx = 0;
        g.gridy = 0;
        g.gridwidth = 2;
        pnlForm.add(lblBanner, g);

        // Cuadrilla
        g.gridwidth = 1;
        g.gridy = 1;
        g.gridx = 0;
        pnlForm.add(new JLabel("Seleccionar Cuadrilla:"), g);

        cmbCuadrillas = new JComboBox<>();
        if (repository != null) {
            List<String> cuadrillas = repository.obtenerNombresCuadrillas();
            for (String c : cuadrillas) {
                cmbCuadrillas.addItem(c);
            }
        } else {
            cmbCuadrillas.addItem("Cuadrilla Centro Histórico");
            cmbCuadrillas.addItem("Cuadrilla Venustiano Carranza");
        }
        g.gridx = 1;
        pnlForm.add(cmbCuadrillas, g);

        // Colonia (Sepomex Saltillo)
        g.gridy = 2;
        g.gridx = 0;
        pnlForm.add(new JLabel("Colonia Atendida (Saltillo):"), g);

        String[] colonias = {
                "Centro Histórico", "República Oriente", "República Poniente",
                "Mirasierra", "Saltillo 2000", "Lomas de Lourdes", "San Patricio",
                "Zaragoza", "Bellavista", "Doctores", "Cumbres"
        };
        cmbColonias = new JComboBox<>(colonias);
        g.gridx = 1;
        pnlForm.add(cmbColonias, g);

        // Tipo de Limpieza
        g.gridy = 3;
        g.gridx = 0;
        pnlForm.add(new JLabel("Tipo de Limpieza:"), g);

        String[] tipos = {
                "Barrido Manual y Desazolve",
                "Deshierbe y Poda de Camellones",
                "Recolección de Escombros y Enseres",
                "Limpieza Profunda de Arroyos",
                "Lavado y Desinfección de Plazas Públicas"
        };
        cmbTipoLimpieza = new JComboBox<>(tipos);
        g.gridx = 1;
        pnlForm.add(cmbTipoLimpieza, g);

        // Observaciones
        g.gridy = 4;
        g.gridx = 0;
        pnlForm.add(new JLabel("Observaciones:"), g);

        txtObservaciones = new JTextField("Operativo matutino concluido con evidencia fotográfica.");
        g.gridx = 1;
        pnlForm.add(txtObservaciones, g);

        // Botón Guardar / Notificar
        g.gridy = 5;
        g.gridx = 0;
        g.gridwidth = 2;
        btnRegistrarActividad = new JButton("Registrar Actividad y Disparar Evento Observer",
                com.saltillo.limpieza.views.components.UiIcons.getActividadesIcon(Color.WHITE, 16));
        btnRegistrarActividad.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRegistrarActividad.setForeground(Color.WHITE);
        btnRegistrarActividad.setBackground(new Color(16, 185, 129)); // Verde
        btnRegistrarActividad.setFocusPainted(false);
        btnRegistrarActividad.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnRegistrarActividad.addActionListener(e -> {
            String cuadrillaNombre = (String) cmbCuadrillas.getSelectedItem();
            String colonia = (String) cmbColonias.getSelectedItem();
            String tipo = (String) cmbTipoLimpieza.getSelectedItem();
            String obs = txtObservaciones.getText().trim();

            int id = (repository != null) ? repository.obtenerIdPorNombre(cuadrillaNombre) : 1;

            if (repository != null) {
                repository.registrarActividad(id, colonia, tipo, obs);
            } else {
                ActividadEventBus.getInstance().publicarNuevaActividad(
                        new ActividadEventDTO(id, cuadrillaNombre, colonia, tipo, obs));
            }

            JOptionPane.showMessageDialog(this,
                    "¡Actividad registrada exitosamente!\n\n" +
                            "Se emitió el evento a través del 'ActividadEventBus'.\n" +
                            "Navega al 'Dashboard Ejecutivo' para observar la actualización en tiempo real (Patrón Observer).",
                    "Evento Publicado",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        pnlForm.add(btnRegistrarActividad, g);

        return pnlForm;
    }
}
