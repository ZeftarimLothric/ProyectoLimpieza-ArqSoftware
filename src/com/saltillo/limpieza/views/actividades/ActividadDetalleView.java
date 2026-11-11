package com.saltillo.limpieza.views.actividades;

import com.saltillo.limpieza.models.dto.ActividadDetalleDTO;
import com.saltillo.limpieza.views.components.RoundedPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Detalle de una actividad (Integrante 3 - Issue #13).
 *
 * Arquitectura MVC Estricta: vista pasiva. Muestra el ActividadDetalleDTO y el personal
 * que le entrega el ActividadController; las evidencias se ven con EvidenciaPanel en solo lectura.
 */
public class ActividadDetalleView extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color TEXTO_FUERTE = new Color(15, 23, 42);
    private static final Color TEXTO_ETIQUETA = new Color(71, 85, 105);
    private static final Color TEXTO_SUAVE = new Color(100, 116, 139);

    private final JLabel lblTitulo = new JLabel();
    private final JLabel lblSubtitulo = new JLabel();
    private final JLabel lblEstado = new JLabel() {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fondoEstado);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    };
    private Color fondoEstado = new Color(241, 245, 249);

    private final JTextArea txtTipo = crearValor();
    private final JTextArea txtLugar = crearValor();
    private final JTextArea txtColonias = crearValor();
    private final JTextArea txtFechaProgramada = crearValor();
    private final JTextArea txtFechaRealizacion = crearValor();
    private final JTextArea txtObservaciones = crearValor();
    private final JTextArea txtCuadrilla = crearValor();
    private final JTextArea txtJefe = crearValor();
    private final JTextArea txtIntegrantes = crearValor();
    private final EvidenciaPanel evidenciaPanel = new EvidenciaPanel();
    private final JButton btnCerrar = new JButton("Cerrar");

    private JDialog dialogo;

    public ActividadDetalleView() {
        setLayout(new BorderLayout(0, 12));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(18, 22, 16, 22));

        evidenciaPanel.setSoloLectura(true);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setOpaque(false);
        contenido.add(crearTarjeta("Información de la actividad", new String[]{
                "Tipo de servicio", "Lugar específico", "Colonias atendidas",
                "Fecha programada", "Fecha de realización", "Observaciones"},
                new JTextArea[]{txtTipo, txtLugar, txtColonias, txtFechaProgramada, txtFechaRealizacion, txtObservaciones}));
        contenido.add(Box.createVerticalStrut(12));
        contenido.add(crearTarjeta("Cuadrilla asignada", new String[]{"Cuadrilla", "Jefe", "Integrantes"},
                new JTextArea[]{txtCuadrilla, txtJefe, txtIntegrantes}));
        contenido.add(Box.createVerticalStrut(12));
        evidenciaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(evidenciaPanel);

        // Anclado arriba para que cada tarjeta tome su alto natural en lugar de estirarse.
        JPanel contenidoArriba = new JPanel(new BorderLayout());
        contenidoArriba.setOpaque(false);
        contenidoArriba.add(contenido, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(contenidoArriba);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(crearEncabezado(), BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(crearBotones(), BorderLayout.SOUTH);
    }

    private JPanel crearEncabezado() {
        JPanel pnlEncabezado = new JPanel();
        pnlEncabezado.setLayout(new BoxLayout(pnlEncabezado, BoxLayout.Y_AXIS));
        pnlEncabezado.setOpaque(false);

        JPanel pnlTitulo = new JPanel(new BorderLayout(10, 0));
        pnlTitulo.setOpaque(false);
        pnlTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(TEXTO_FUERTE);
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblEstado.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        JPanel pnlEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        pnlEstado.setOpaque(false);
        pnlEstado.add(lblEstado);
        pnlTitulo.add(lblTitulo, BorderLayout.CENTER);
        pnlTitulo.add(pnlEstado, BorderLayout.EAST);

        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(TEXTO_SUAVE);
        lblSubtitulo.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlEncabezado.add(pnlTitulo);
        pnlEncabezado.add(lblSubtitulo);
        return pnlEncabezado;
    }

    private RoundedPanel crearTarjeta(String titulo, String[] etiquetas, JTextArea[] valores) {
        RoundedPanel tarjeta = new RoundedPanel(16, Color.WHITE, new Color(226, 232, 240));
        tarjeta.setLayout(new GridBagLayout());
        tarjeta.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.NORTHWEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0;
        g.gridy = 0;
        g.gridwidth = 2;
        g.insets = new Insets(0, 0, 8, 0);
        JLabel lblTarjeta = new JLabel(titulo);
        lblTarjeta.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTarjeta.setForeground(TEXTO_FUERTE);
        tarjeta.add(lblTarjeta, g);

        g.gridwidth = 1;
        g.insets = new Insets(4, 0, 4, 12);
        for (int i = 0; i < etiquetas.length; i++) {
            g.gridy = i + 1;
            g.gridx = 0;
            g.weightx = 0;
            JLabel lblEtiqueta = new JLabel(etiquetas[i] + ":");
            lblEtiqueta.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblEtiqueta.setForeground(TEXTO_ETIQUETA);
            lblEtiqueta.setPreferredSize(new Dimension(140, 18));
            tarjeta.add(lblEtiqueta, g);

            g.gridx = 1;
            g.weightx = 1;
            tarjeta.add(valores[i], g);
        }
        return tarjeta;
    }

    private static JTextArea crearValor() {
        JTextArea valor = new JTextArea();
        valor.setEditable(false);
        valor.setLineWrap(true);
        valor.setWrapStyleWord(true);
        valor.setOpaque(false);
        valor.setBorder(BorderFactory.createEmptyBorder());
        valor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        valor.setForeground(TEXTO_FUERTE);
        return valor;
    }

    private JPanel crearBotones() {
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlBotones.setOpaque(false);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlBotones.add(btnCerrar);
        return pnlBotones;
    }

    private void pintarEstado(String estado) {
        lblEstado.setText(estado == null ? "SIN ESTADO" : estado.toUpperCase());
        if ("COMPLETADA".equalsIgnoreCase(estado)) {
            fondoEstado = new Color(220, 252, 231);
            lblEstado.setForeground(new Color(22, 101, 52));
        } else if ("EN PROCESO".equalsIgnoreCase(estado)) {
            fondoEstado = new Color(254, 243, 199);
            lblEstado.setForeground(new Color(146, 64, 14));
        } else {
            fondoEstado = new Color(224, 242, 254);
            lblEstado.setForeground(new Color(3, 105, 161));
        }
        lblEstado.repaint();
    }

    private static String valorOGuion(String texto) {
        return texto == null || texto.isBlank() ? "—" : texto;
    }

    // =========================================================================
    // MÉTODOS PÚBLICOS DE VISTA (STRICT MVC)
    // =========================================================================

    public void mostrarActividad(ActividadDetalleDTO actividad) {
        lblTitulo.setText(actividad.getTitulo());
        lblSubtitulo.setText("Actividad #" + actividad.getIdActividad());
        pintarEstado(actividad.getEstado());

        txtTipo.setText(valorOGuion(actividad.getTipoLimpieza()));
        txtLugar.setText(valorOGuion(actividad.getLugarEspecifico()));
        txtColonias.setText(actividad.getColoniasFormateadas());
        txtFechaProgramada.setText(actividad.getFechaHoraFormateada());
        String realizada = actividad.getFechaRealizacionFormateada();
        txtFechaRealizacion.setText(realizada.isEmpty() ? "Pendiente" : realizada);
        txtObservaciones.setText(valorOGuion(actividad.getObservaciones()));
        txtCuadrilla.setText(valorOGuion(actividad.getNombreCuadrilla()));
    }

    /**
     * @param jefe        nombre completo del jefe, o null si la cuadrilla no tiene
     * @param integrantes nombres del resto del personal
     */
    public void mostrarPersonal(String jefe, List<String> integrantes) {
        txtJefe.setText(jefe != null ? jefe : "Sin jefe asignado");
        txtIntegrantes.setText(integrantes.isEmpty() ? "Sin otros integrantes" : String.join("\n", integrantes));
    }

    public void mostrarPersonalNoDisponible(String mensaje) {
        txtJefe.setText(mensaje);
        txtIntegrantes.setText("—");
    }

    public EvidenciaPanel getEvidenciaPanel() {
        return evidenciaPanel;
    }

    /**
     * Muestra el detalle en un diálogo modal; regresa cuando se cierra.
     */
    public void abrir(Component padre, String titulo) {
        Window ventana = padre != null ? SwingUtilities.getWindowAncestor(padre) : null;
        dialogo = new JDialog(ventana, titulo, Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialogo.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                btnCerrar.doClick();
            }
        });
        dialogo.setContentPane(this);
        dialogo.setSize(760, 720);
        dialogo.setLocationRelativeTo(padre);
        dialogo.setVisible(true);
    }

    public void cerrar() {
        if (dialogo != null) {
            dialogo.dispose();
        }
    }

    public void setCerrarListener(ActionListener l) {
        btnCerrar.addActionListener(l);
    }
}
