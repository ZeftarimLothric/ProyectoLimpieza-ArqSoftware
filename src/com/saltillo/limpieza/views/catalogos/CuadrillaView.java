package com.saltillo.limpieza.views.catalogos;

import com.saltillo.limpieza.models.entities.Empleado;
import com.saltillo.limpieza.views.components.EstiloUi;
import com.saltillo.limpieza.views.components.RoundedPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * Pantalla de cuadrillas con su jefe y su personal (Issue #9).
 *
 * Izquierda: tabla de cuadrillas. Derecha: formulario de la cuadrilla (nombre, estado, jefe) y,
 * debajo, el personal de la cuadrilla seleccionada. Vista pura: no tiene SQL ni reglas.
 */
public class CuadrillaView extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final String SIN_SELECCION = "— Selecciona un empleado —";

    // cuadrillas
    private final DefaultTableModel modeloCuadrillas = modeloSoloLectura(
            new String[]{"ID", "Cuadrilla", "Jefe", "Personal", "Estado"});
    private final JTable tblCuadrillas = new JTable(modeloCuadrillas);
    private final JLabel lblConteo = new JLabel(" ");

    // formulario
    private final JLabel lblModo = new JLabel("Nueva cuadrilla");
    private final JTextField txtNombre = EstiloUi.estilizarCampo(new JTextField());
    private final JCheckBox chkActiva = new JCheckBox("Cuadrilla activa", true);
    private final JComboBox<Empleado> cmbJefe = new JComboBox<>();
    private final JButton btnNuevo = EstiloUi.botonSecundario("Nueva");
    private final JButton btnGuardar = EstiloUi.botonPrimario("Guardar");
    private final JButton btnEliminar = EstiloUi.botonPeligro("Eliminar");

    // personal
    private final DefaultTableModel modeloIntegrantes = modeloSoloLectura(
            new String[]{"Empleado", "Puesto", "Rol", "Asignado"});
    private final JTable tblIntegrantes = new JTable(modeloIntegrantes);
    private final JComboBox<Empleado> cmbDisponibles = new JComboBox<>();
    private final JButton btnAgregar = EstiloUi.botonPrimario("Agregar");
    private final JButton btnQuitar = EstiloUi.botonSecundario("Quitar seleccionado");
    private final JLabel lblAyudaPersonal = EstiloUi.subtitulo(" ");

    private final JLabel lblMensaje = new JLabel(" ");

    public CuadrillaView() {
        setLayout(new BorderLayout(0, 16));
        setBackground(EstiloUi.FONDO);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setOpaque(false);
        JLabel titulo = EstiloUi.titulo("Cuadrillas");
        JLabel subtitulo = EstiloUi.subtitulo("Cuadrillas de limpieza con su jefe y el personal a su cargo");
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        encabezado.add(titulo);
        encabezado.add(Box.createVerticalStrut(4));
        encabezado.add(subtitulo);
        add(encabezado, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(18, 0));
        centro.setOpaque(false);
        centro.add(construirListaCuadrillas(), BorderLayout.CENTER);
        centro.add(construirPanelDerecho(), BorderLayout.EAST);
        add(centro, BorderLayout.CENTER);

        configurarCombo(cmbJefe);
        configurarCombo(cmbDisponibles);
        setPersonalHabilitado(false);
        setModo("Nueva cuadrilla", false);
    }

    private static DefaultTableModel modeloSoloLectura(String[] columnas) {
        return new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private JComponent construirListaCuadrillas() {
        EstiloUi.estilizarTabla(tblCuadrillas);
        tblCuadrillas.getColumnModel().getColumn(0).setMaxWidth(60);
        tblCuadrillas.getColumnModel().getColumn(3).setMaxWidth(90);
        tblCuadrillas.getColumnModel().getColumn(4).setMaxWidth(100);
        JScrollPane scroll = new JScrollPane(tblCuadrillas);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        lblConteo.setFont(EstiloUi.fuente(Font.PLAIN, 11));
        lblConteo.setForeground(EstiloUi.TEXTO_SUAVE);
        lblConteo.setBorder(BorderFactory.createEmptyBorder(8, 2, 0, 0));

        RoundedPanel card = new RoundedPanel(16, Color.WHITE, EstiloUi.BORDE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));
        card.add(scroll, BorderLayout.CENTER);
        card.add(lblConteo, BorderLayout.SOUTH);
        return card;
    }

    private JComponent construirPanelDerecho() {
        // --- formulario de la cuadrilla ---
        lblModo.setFont(EstiloUi.fuente(Font.BOLD, 15));
        lblModo.setForeground(EstiloUi.TEXTO);
        chkActiva.setOpaque(false);
        chkActiva.setFont(EstiloUi.fuente(Font.PLAIN, 13));

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;
        g.gridy = 0;
        g.insets = new Insets(8, 0, 3, 0);
        campos.add(EstiloUi.etiqueta("Nombre de la cuadrilla *"), g);
        g.gridy = 1;
        g.insets = new Insets(0, 0, 0, 0);
        campos.add(txtNombre, g);
        g.gridy = 2;
        g.insets = new Insets(10, 0, 3, 0);
        campos.add(EstiloUi.etiqueta("Jefe de cuadrilla *"), g);
        g.gridy = 3;
        g.insets = new Insets(0, 0, 0, 0);
        campos.add(cmbJefe, g);
        g.gridy = 4;
        g.insets = new Insets(10, 0, 0, 0);
        campos.add(chkActiva, g);

        JPanel botones = new JPanel(new GridLayout(1, 3, 8, 0));
        botones.setOpaque(false);
        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnEliminar);

        lblMensaje.setFont(EstiloUi.fuente(Font.PLAIN, 12));
        JPanel sur = new JPanel(new BorderLayout(0, 8));
        sur.setOpaque(false);
        sur.add(lblMensaje, BorderLayout.NORTH);
        sur.add(botones, BorderLayout.SOUTH);

        RoundedPanel cardForm = new RoundedPanel(16, Color.WHITE, EstiloUi.BORDE);
        cardForm.setLayout(new BorderLayout(0, 6));
        cardForm.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        cardForm.add(lblModo, BorderLayout.NORTH);
        cardForm.add(campos, BorderLayout.CENTER);
        cardForm.add(sur, BorderLayout.SOUTH);

        // --- personal ---
        JLabel lblPersonal = new JLabel("Personal de la cuadrilla");
        lblPersonal.setFont(EstiloUi.fuente(Font.BOLD, 15));
        lblPersonal.setForeground(EstiloUi.TEXTO);
        lblAyudaPersonal.setFont(EstiloUi.fuente(Font.PLAIN, 11));
        JPanel encabezadoPersonal = new JPanel(new BorderLayout(0, 2));
        encabezadoPersonal.setOpaque(false);
        encabezadoPersonal.add(lblPersonal, BorderLayout.NORTH);
        encabezadoPersonal.add(lblAyudaPersonal, BorderLayout.SOUTH);

        EstiloUi.estilizarTabla(tblIntegrantes);
        tblIntegrantes.getColumnModel().getColumn(2).setMaxWidth(90);
        tblIntegrantes.getColumnModel().getColumn(3).setMaxWidth(100);
        JScrollPane scroll = new JScrollPane(tblIntegrantes);
        scroll.setBorder(BorderFactory.createLineBorder(EstiloUi.BORDE));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setPreferredSize(new Dimension(100, 150));

        JPanel agregar = new JPanel(new BorderLayout(8, 0));
        agregar.setOpaque(false);
        agregar.add(cmbDisponibles, BorderLayout.CENTER);
        agregar.add(btnAgregar, BorderLayout.EAST);
        JPanel pie = new JPanel(new BorderLayout(0, 8));
        pie.setOpaque(false);
        pie.add(agregar, BorderLayout.NORTH);
        pie.add(btnQuitar, BorderLayout.SOUTH);

        RoundedPanel cardPersonal = new RoundedPanel(16, Color.WHITE, EstiloUi.BORDE);
        cardPersonal.setLayout(new BorderLayout(0, 8));
        cardPersonal.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        cardPersonal.add(encabezadoPersonal, BorderLayout.NORTH);
        cardPersonal.add(scroll, BorderLayout.CENTER);
        cardPersonal.add(pie, BorderLayout.SOUTH);

        JPanel derecho = new JPanel(new BorderLayout(0, 14));
        derecho.setOpaque(false);
        derecho.setPreferredSize(new Dimension(440, 0));
        derecho.add(cardForm, BorderLayout.NORTH);
        derecho.add(cardPersonal, BorderLayout.CENTER);
        return derecho;
    }

    /** Muestra el nombre completo y el puesto de cada empleado; el valor nulo se ve como una indicación. */
    private static void configurarCombo(JComboBox<Empleado> combo) {
        combo.setFont(EstiloUi.fuente(Font.PLAIN, 13));
        combo.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                          boolean seleccionado, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, seleccionado, foco);
                if (valor instanceof Empleado) {
                    Empleado e = (Empleado) valor;
                    setText(nombreCompleto(e) + "  ·  " + e.getPuesto());
                } else {
                    setText(SIN_SELECCION);
                    setForeground(EstiloUi.TEXTO_SUAVE);
                }
                return this;
            }
        });
    }

    public static String nombreCompleto(Empleado e) {
        String materno = e.getApellidoMaterno() == null || e.getApellidoMaterno().trim().isEmpty()
                ? "" : " " + e.getApellidoMaterno();
        return e.getNombre() + " " + e.getApellidoPaterno() + materno;
    }

    // =====================  API para el controlador  =====================

    // ---- cuadrillas ----
    public void mostrarCuadrillas(List<Object[]> filas) {
        modeloCuadrillas.setRowCount(0);
        for (Object[] f : filas) {
            modeloCuadrillas.addRow(f);
        }
        lblConteo.setText(filas.size() == 1 ? "1 cuadrilla" : filas.size() + " cuadrillas");
    }

    public void seleccionarCuadrilla(int fila) {
        tblCuadrillas.getSelectionModel().setSelectionInterval(fila, fila);
    }

    public void limpiarSeleccionCuadrillas() {
        tblCuadrillas.clearSelection();
    }

    public void setSeleccionCuadrillaListener(IntConsumer listener) {
        tblCuadrillas.getSelectionModel().addListSelectionListener(e -> {
            int fila = tblCuadrillas.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                listener.accept(fila);
            }
        });
    }

    // ---- formulario ----
    public String getNombre() {
        return txtNombre.getText();
    }

    public void setNombre(String v) {
        txtNombre.setText(v);
    }

    public boolean isActiva() {
        return chkActiva.isSelected();
    }

    public void setActiva(boolean v) {
        chkActiva.setSelected(v);
    }

    /** Reemplaza las opciones del combo de jefe (la primera es "sin selección"). */
    public void setOpcionesJefe(List<Empleado> empleados, Integer idJefeActual) {
        llenarCombo(cmbJefe, empleados, idJefeActual);
    }

    /** Empleado elegido como jefe, o null si no hay selección. */
    public Empleado getJefeSeleccionado() {
        return (Empleado) cmbJefe.getSelectedItem();
    }

    public void setModo(String descripcion, boolean cuadrillaExistente) {
        lblModo.setText(descripcion);
        btnEliminar.setEnabled(cuadrillaExistente);
    }

    // ---- personal ----
    public void mostrarIntegrantes(List<Object[]> filas) {
        modeloIntegrantes.setRowCount(0);
        for (Object[] f : filas) {
            modeloIntegrantes.addRow(f);
        }
    }

    public int getFilaIntegranteSeleccionada() {
        return tblIntegrantes.getSelectedRow();
    }

    public void setEmpleadosDisponibles(List<Empleado> empleados) {
        llenarCombo(cmbDisponibles, empleados, null);
    }

    public Empleado getDisponibleSeleccionado() {
        return (Empleado) cmbDisponibles.getSelectedItem();
    }

    /** El personal solo se administra cuando la cuadrilla ya existe (está guardada). */
    public void setPersonalHabilitado(boolean habilitado) {
        cmbDisponibles.setEnabled(habilitado);
        btnAgregar.setEnabled(habilitado);
        btnQuitar.setEnabled(habilitado);
        tblIntegrantes.setEnabled(habilitado);
        lblAyudaPersonal.setText(habilitado
                ? "Agrega o quita personal. Un empleado solo puede estar en una cuadrilla activa."
                : "Guarda la cuadrilla para poder agregar a su personal.");
    }

    private static void llenarCombo(JComboBox<Empleado> combo, List<Empleado> empleados, Integer idASeleccionar) {
        combo.removeAllItems();
        combo.addItem(null);
        Empleado elegido = null;
        for (Empleado e : empleados) {
            combo.addItem(e);
            if (idASeleccionar != null && e.getIdEmpleado() == idASeleccionar) {
                elegido = e;
            }
        }
        combo.setSelectedItem(elegido); // null = "— Selecciona un empleado —"
    }

    // ---- mensajes ----
    public void mostrarError(String mensaje) {
        lblMensaje.setForeground(EstiloUi.ERROR);
        lblMensaje.setText(html(mensaje));
    }

    public void mostrarExito(String mensaje) {
        lblMensaje.setForeground(EstiloUi.EXITO);
        lblMensaje.setText(html(mensaje));
    }

    public void limpiarMensaje() {
        lblMensaje.setText(" ");
    }

    /** Pregunta Sí/No (se puede sobrescribir en pruebas automáticas). */
    public boolean confirmar(String mensaje) {
        int r = JOptionPane.showConfirmDialog(this, mensaje, "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return r == JOptionPane.YES_OPTION;
    }

    private static String html(String texto) {
        return "<html><body style='width:380px'>" + texto.replace("\n", "<br>") + "</body></html>";
    }

    // ---- listeners ----
    public void setNuevaListener(ActionListener l) {
        btnNuevo.addActionListener(l);
    }

    public void setGuardarListener(ActionListener l) {
        btnGuardar.addActionListener(l);
    }

    public void setEliminarListener(ActionListener l) {
        btnEliminar.addActionListener(l);
    }

    public void setAgregarIntegranteListener(ActionListener l) {
        btnAgregar.addActionListener(l);
    }

    public void setQuitarIntegranteListener(ActionListener l) {
        btnQuitar.addActionListener(l);
    }
}
