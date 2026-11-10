package com.saltillo.limpieza.views.actividades;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Formulario de actividad (Integrante 3 - Issue #10).
 *
 * Arquitectura MVC Estricta: entrega el texto capturado tal cual; las
 * validaciones las hace el ActividadController.
 */
public class ActividadFormView extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final String OPCION_CUADRILLA = "Seleccione una cuadrilla";
    private static final String OPCION_TIPO = "Seleccione un tipo";

    private final JComboBox<String> cmbCuadrilla = new JComboBox<>();
    private final JComboBox<String> cmbTipo = new JComboBox<>();
    private final JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"PROGRAMADA", "EN PROCESO", "COMPLETADA"});
    private final JTextField txtLugar = new JTextField();
    private final JTextField txtFechaProgramada = new JTextField();
    private final JTextField txtFechaRealizacion = new JTextField();
    private final JTextArea txtDescripcion = new JTextArea(3, 20);
    private final EvidenciaPanel evidenciaPanel = new EvidenciaPanel();
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnCancelar = new JButton("Cancelar");

    private JDialog dialogo;

    public ActividadFormView() {
        setLayout(new BorderLayout(0, 12));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        add(crearCampos(), BorderLayout.NORTH);
        add(evidenciaPanel, BorderLayout.CENTER);
        add(crearBotones(), BorderLayout.SOUTH);
        limpiar();
    }

    private JPanel crearCampos() {
        JPanel pnlCampos = new JPanel(new GridBagLayout());
        pnlCampos.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 0.5;

        agregarCampo(pnlCampos, g, 0, 0, 1, "Cuadrilla:", cmbCuadrilla);
        agregarCampo(pnlCampos, g, 1, 0, 1, "Tipo de actividad:", cmbTipo);
        agregarCampo(pnlCampos, g, 0, 2, 2, "Lugar específico:", txtLugar);
        agregarCampo(pnlCampos, g, 0, 4, 1, "Fecha programada (dd/mm/aaaa hh:mm):", txtFechaProgramada);
        agregarCampo(pnlCampos, g, 1, 4, 1, "Fecha de realización (dd/mm/aaaa hh:mm):", txtFechaRealizacion);
        agregarCampo(pnlCampos, g, 0, 6, 2, "Estado:", cmbEstado);

        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        agregarCampo(pnlCampos, g, 0, 8, 2, "Descripción:", new JScrollPane(txtDescripcion));
        return pnlCampos;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints g, int columna, int fila, int ancho, String etiqueta, JComponent campo) {
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblEtiqueta.setForeground(new Color(71, 85, 105));
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        g.gridx = columna;
        g.gridwidth = ancho;
        g.gridy = fila;
        panel.add(lblEtiqueta, g);
        g.gridy = fila + 1;
        panel.add(campo, g);
    }

    private JPanel crearBotones() {
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlBotones.setOpaque(false);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlBotones.add(btnCancelar);
        pnlBotones.add(btnGuardar);
        return pnlBotones;
    }

    // =========================================================================
    // MÉTODOS PÚBLICOS DE VISTA (STRICT MVC)
    // =========================================================================

    public void poblarCuadrillas(List<String> cuadrillas) {
        poblar(cmbCuadrilla, OPCION_CUADRILLA, cuadrillas);
    }

    public void poblarTipos(List<String> tipos) {
        poblar(cmbTipo, OPCION_TIPO, tipos);
    }

    private void poblar(JComboBox<String> combo, String opcionInicial, List<String> valores) {
        combo.removeAllItems();
        combo.addItem(opcionInicial);
        valores.forEach(combo::addItem);
    }

    public void limpiar() {
        cmbCuadrilla.setSelectedIndex(cmbCuadrilla.getItemCount() > 0 ? 0 : -1);
        cmbTipo.setSelectedIndex(cmbTipo.getItemCount() > 0 ? 0 : -1);
        cmbEstado.setSelectedIndex(0);
        txtLugar.setText("");
        txtFechaProgramada.setText("");
        txtFechaRealizacion.setText("");
        txtDescripcion.setText("");
    }

    public void mostrarDatos(String cuadrilla, String tipo, String lugar, String fechaProgramada,
                             String fechaRealizacion, String estado, String descripcion) {
        cmbCuadrilla.setSelectedItem(cuadrilla);
        cmbTipo.setSelectedItem(tipo);
        txtLugar.setText(lugar);
        txtFechaProgramada.setText(fechaProgramada);
        txtFechaRealizacion.setText(fechaRealizacion);
        cmbEstado.setSelectedItem(estado);
        txtDescripcion.setText(descripcion);
    }

    /**
     * Muestra el formulario en un diálogo modal; regresa cuando este se cierra.
     */
    public void abrir(Component padre, String titulo) {
        dialogo = new JDialog(SwingUtilities.getWindowAncestor(padre), titulo, Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialogo.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                btnCancelar.doClick();
            }
        });
        dialogo.setContentPane(this);
        dialogo.setSize(720, 640);
        dialogo.setLocationRelativeTo(padre);
        dialogo.setVisible(true);
    }

    public void cerrar() {
        if (dialogo != null) {
            dialogo.dispose();
        }
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Revisa los datos", JOptionPane.ERROR_MESSAGE);
    }

    public EvidenciaPanel getEvidenciaPanel() {
        return evidenciaPanel;
    }

    public String getCuadrilla() {
        return cmbCuadrilla.getSelectedIndex() > 0 ? (String) cmbCuadrilla.getSelectedItem() : null;
    }

    public String getTipo() {
        return cmbTipo.getSelectedIndex() > 0 ? (String) cmbTipo.getSelectedItem() : null;
    }

    public String getEstado() {
        return (String) cmbEstado.getSelectedItem();
    }

    public String getLugar() {
        return txtLugar.getText();
    }

    public String getFechaProgramada() {
        return txtFechaProgramada.getText();
    }

    public String getFechaRealizacion() {
        return txtFechaRealizacion.getText();
    }

    public String getDescripcion() {
        return txtDescripcion.getText();
    }

    public void setGuardarListener(ActionListener l) {
        btnGuardar.addActionListener(l);
    }

    public void setCancelarListener(ActionListener l) {
        btnCancelar.addActionListener(l);
    }
}
