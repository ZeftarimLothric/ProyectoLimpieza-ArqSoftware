package com.saltillo.limpieza.views.actividades;

import com.saltillo.limpieza.models.dto.ColoniaDTO;
import com.saltillo.limpieza.views.components.RoundedPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Diálogo para elegir las colonias atendidas (Integrante 3 - Issue #11).
 *
 * Arquitectura MVC Estricta: vista pasiva. Muestra lo que le entrega el
 * ColoniaSelectorController y le avisa de las acciones del usuario.
 */
public class ColoniaSelectorDialog extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JTextField txtBusqueda = new JTextField();
    private final DefaultListModel<ColoniaDTO> modeloResultados = new DefaultListModel<>();
    private final DefaultListModel<ColoniaDTO> modeloSeleccionadas = new DefaultListModel<>();
    private final JList<ColoniaDTO> lstResultados = new JList<>(modeloResultados);
    private final JList<ColoniaDTO> lstSeleccionadas = new JList<>(modeloSeleccionadas);
    private final JLabel lblResultados = new JLabel();
    private final JLabel lblSeleccionadas = new JLabel();

    private JButton btnBuscar;
    private JButton btnAgregar;
    private JButton btnQuitar;
    private JButton btnAceptar;
    private JButton btnCancelar;
    private JDialog dialogo;

    public ColoniaSelectorDialog() {
        setLayout(new BorderLayout(0, 12));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        add(crearBusqueda(), BorderLayout.NORTH);
        add(crearListas(), BorderLayout.CENTER);
        add(crearBotonesFinales(), BorderLayout.SOUTH);
    }

    private JPanel crearBusqueda() {
        JPanel pnlBusqueda = new JPanel(new BorderLayout(8, 4));
        pnlBusqueda.setOpaque(false);

        JLabel lblBusqueda = new JLabel("Buscar por nombre o código postal:");
        lblBusqueda.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBusqueda.setForeground(new Color(71, 85, 105));
        pnlBusqueda.add(lblBusqueda, BorderLayout.NORTH);

        txtBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pnlBusqueda.add(txtBusqueda, BorderLayout.CENTER);

        btnBuscar = crearBoton("Buscar", true);
        pnlBusqueda.add(btnBuscar, BorderLayout.EAST);
        return pnlBusqueda;
    }

    private JPanel crearListas() {
        JPanel pnlListas = new JPanel(new GridLayout(1, 2, 12, 0));
        pnlListas.setOpaque(false);

        btnAgregar = crearBoton("Agregar", true);
        btnQuitar = crearBoton("Quitar", false);
        pnlListas.add(crearTarjetaLista(lblResultados, lstResultados, btnAgregar));
        pnlListas.add(crearTarjetaLista(lblSeleccionadas, lstSeleccionadas, btnQuitar));
        return pnlListas;
    }

    private RoundedPanel crearTarjetaLista(JLabel titulo, JList<ColoniaDTO> lista, JButton boton) {
        RoundedPanel tarjeta = new RoundedPanel(16, Color.WHITE, new Color(226, 232, 240));
        tarjeta.setLayout(new BorderLayout(0, 8));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titulo.setForeground(new Color(15, 23, 42));
        tarjeta.add(titulo, BorderLayout.NORTH);

        lista.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lista.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        lista.setFixedCellHeight(26);
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        tarjeta.add(scroll, BorderLayout.CENTER);

        JPanel pnlBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlBoton.setOpaque(false);
        pnlBoton.add(boton);
        tarjeta.add(pnlBoton, BorderLayout.SOUTH);
        return tarjeta;
    }

    private JPanel crearBotonesFinales() {
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlBotones.setOpaque(false);
        btnCancelar = crearBoton("Cancelar", false);
        btnAceptar = crearBoton("Aceptar", true);
        pnlBotones.add(btnCancelar);
        pnlBotones.add(btnAceptar);
        return pnlBotones;
    }

    private JButton crearBoton(String texto, boolean principal) {
        Color fondo = principal ? new Color(2, 132, 199) : Color.WHITE;
        Color fondoHover = principal ? new Color(3, 105, 161) : new Color(241, 245, 249);
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? fondoHover : fondo);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                if (!principal) {
                    g2.setColor(new Color(203, 213, 225));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setForeground(principal ? Color.WHITE : new Color(71, 85, 105));
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(false);
        boton.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    // =========================================================================
    // MÉTODOS PÚBLICOS DE VISTA (STRICT MVC)
    // =========================================================================

    public void mostrarResultados(List<ColoniaDTO> colonias) {
        modeloResultados.clear();
        modeloResultados.addAll(colonias);
        lblResultados.setText(colonias.isEmpty() ? "Sin coincidencias" : "Resultados (" + colonias.size() + ")");
    }

    public void mostrarSeleccionadas(List<ColoniaDTO> colonias) {
        modeloSeleccionadas.clear();
        modeloSeleccionadas.addAll(colonias);
        lblSeleccionadas.setText("Colonias elegidas (" + colonias.size() + ")");
    }

    public void limpiarBusqueda() {
        txtBusqueda.setText("");
    }

    public String getTextoBusqueda() {
        return txtBusqueda.getText();
    }

    public List<ColoniaDTO> getResultadosMarcados() {
        return lstResultados.getSelectedValuesList();
    }

    public List<ColoniaDTO> getSeleccionadasMarcadas() {
        return lstSeleccionadas.getSelectedValuesList();
    }

    /**
     * Muestra el diálogo de forma modal sobre la ventana de padre; regresa cuando se cierra.
     */
    public void abrir(Component padre) {
        Window ventana = padre != null ? SwingUtilities.getWindowAncestor(padre) : null;
        dialogo = new JDialog(ventana, "Colonias atendidas", Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialogo.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                btnCancelar.doClick();
            }
        });
        dialogo.setContentPane(this);
        dialogo.setSize(760, 480);
        dialogo.setLocationRelativeTo(padre);
        dialogo.setVisible(true);
    }

    public void cerrar() {
        if (dialogo != null) {
            dialogo.dispose();
        }
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Colonias atendidas", JOptionPane.WARNING_MESSAGE);
    }

    public void setBuscarListener(ActionListener l) {
        btnBuscar.addActionListener(l);
        txtBusqueda.addActionListener(l);
    }

    public void setAgregarListener(ActionListener l) {
        btnAgregar.addActionListener(l);
    }

    public void setQuitarListener(ActionListener l) {
        btnQuitar.addActionListener(l);
    }

    public void setAceptarListener(ActionListener l) {
        btnAceptar.addActionListener(l);
    }

    public void setCancelarListener(ActionListener l) {
        btnCancelar.addActionListener(l);
    }
}
