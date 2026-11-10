package com.saltillo.limpieza.views.catalogos;

import com.saltillo.limpieza.views.components.EstiloUi;
import com.saltillo.limpieza.views.components.RoundedPanel;
import com.saltillo.limpieza.views.components.UiIcons;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * Plantilla visual de un catálogo (Issue #8): tabla con buscador a la izquierda y formulario
 * con los botones de las 4 operaciones (consultar, alta, edición, baja) a la derecha.
 *
 * Vista pura (MVC): no tiene SQL ni reglas. Cada catálogo hereda esta clase, agrega sus campos
 * con agregarCampo(...) en su constructor e implementa limpiarCampos().
 */
public abstract class CatalogoBaseView extends JPanel {
    private static final long serialVersionUID = 1L;

    private final DefaultTableModel modelo;
    private final JTable tabla;
    private final JTextField txtBuscar = EstiloUi.estilizarCampo(new JTextField());
    private final JButton btnBuscar = EstiloUi.botonSecundario("Buscar");
    private final JButton btnNuevo = EstiloUi.botonSecundario("Nuevo");
    private final JButton btnGuardar = EstiloUi.botonPrimario("Guardar");
    private final JButton btnEliminar = EstiloUi.botonPeligro("Eliminar");
    private final JLabel lblModo = new JLabel("Nuevo registro");
    private final JLabel lblMensaje = new JLabel(" ");
    private final JLabel lblConteo = new JLabel(" ");
    private final JPanel pnlCampos = new JPanel(new GridBagLayout());
    private int filaCampos = 0;

    protected CatalogoBaseView(String titulo, String subtitulo, String[] columnas, String ayudaBusqueda) {
        setLayout(new BorderLayout(0, 16));
        setBackground(EstiloUi.FONDO);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // --- encabezado ---
        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setOpaque(false);
        JLabel lblTitulo = EstiloUi.titulo(titulo);
        JLabel lblSubtitulo = EstiloUi.subtitulo(subtitulo);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        encabezado.add(lblTitulo);
        encabezado.add(Box.createVerticalStrut(4));
        encabezado.add(lblSubtitulo);
        add(encabezado, BorderLayout.NORTH);

        // --- tabla ---
        modelo = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        EstiloUi.estilizarTabla(tabla);
        tabla.getColumnModel().getColumn(0).setMaxWidth(70);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        txtBuscar.setToolTipText(ayudaBusqueda);
        btnBuscar.setIcon(UiIcons.getSearchIcon(EstiloUi.TEXTO_MEDIO, 13));
        JPanel pnlBusqueda = new JPanel(new BorderLayout(8, 0));
        pnlBusqueda.setOpaque(false);
        pnlBusqueda.add(txtBuscar, BorderLayout.CENTER);
        pnlBusqueda.add(btnBuscar, BorderLayout.EAST);
        pnlBusqueda.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        lblConteo.setFont(EstiloUi.fuente(Font.PLAIN, 11));
        lblConteo.setForeground(EstiloUi.TEXTO_SUAVE);
        lblConteo.setBorder(BorderFactory.createEmptyBorder(8, 2, 0, 0));

        RoundedPanel cardLista = new RoundedPanel(16, Color.WHITE, EstiloUi.BORDE);
        cardLista.setLayout(new BorderLayout());
        cardLista.setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));
        cardLista.add(pnlBusqueda, BorderLayout.NORTH);
        cardLista.add(scroll, BorderLayout.CENTER);
        cardLista.add(lblConteo, BorderLayout.SOUTH);

        // --- formulario ---
        lblModo.setFont(EstiloUi.fuente(Font.BOLD, 15));
        lblModo.setForeground(EstiloUi.TEXTO);
        lblModo.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        pnlCampos.setOpaque(false);
        JPanel contenedorCampos = new JPanel(new BorderLayout());
        contenedorCampos.setOpaque(false);
        contenedorCampos.add(pnlCampos, BorderLayout.NORTH);

        lblMensaje.setFont(EstiloUi.fuente(Font.PLAIN, 12));
        JPanel pnlBotones = new JPanel(new GridLayout(1, 3, 8, 0));
        pnlBotones.setOpaque(false);
        pnlBotones.add(btnNuevo);
        pnlBotones.add(btnGuardar);
        pnlBotones.add(btnEliminar);
        JPanel pnlSur = new JPanel(new BorderLayout(0, 10));
        pnlSur.setOpaque(false);
        pnlSur.add(lblMensaje, BorderLayout.CENTER);
        pnlSur.add(pnlBotones, BorderLayout.SOUTH);

        RoundedPanel cardForm = new RoundedPanel(16, Color.WHITE, EstiloUi.BORDE);
        cardForm.setLayout(new BorderLayout(0, 8));
        cardForm.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        cardForm.setPreferredSize(new Dimension(360, 0));
        cardForm.add(lblModo, BorderLayout.NORTH);
        cardForm.add(contenedorCampos, BorderLayout.CENTER);
        cardForm.add(pnlSur, BorderLayout.SOUTH);

        JPanel centro = new JPanel(new BorderLayout(18, 0));
        centro.setOpaque(false);
        centro.add(cardLista, BorderLayout.CENTER);
        centro.add(cardForm, BorderLayout.EAST);
        add(centro, BorderLayout.CENTER);

        txtBuscar.addActionListener(e -> btnBuscar.doClick());
        setModo("Nuevo registro", false);
    }

    // ---------- construcción del formulario (para las subclases) ----------

    /** Agrega una fila al formulario: etiqueta (opcional) y su campo. */
    protected void agregarCampo(String etiqueta, JComponent campo) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;
        if (etiqueta != null) {
            g.gridy = filaCampos++;
            g.insets = new Insets(10, 0, 3, 0);
            pnlCampos.add(EstiloUi.etiqueta(etiqueta), g);
        }
        g.gridy = filaCampos++;
        g.insets = new Insets(etiqueta == null ? 10 : 0, 0, 0, 0);
        pnlCampos.add(campo, g);
    }

    protected static JTextField nuevoCampoTexto() {
        return EstiloUi.estilizarCampo(new JTextField());
    }

    protected static JCheckBox nuevoCheck(String texto) {
        JCheckBox c = new JCheckBox(texto, true);
        c.setOpaque(false);
        c.setFont(EstiloUi.fuente(Font.PLAIN, 13));
        c.setForeground(EstiloUi.TEXTO);
        return c;
    }

    /** Deja los campos del formulario en su valor inicial. */
    protected abstract void limpiarCampos();

    // ---------- API para el controlador ----------

    public void limpiarFormulario() {
        limpiarCampos();
        limpiarMensaje();
    }

    public void mostrarFilas(List<Object[]> filas) {
        modelo.setRowCount(0);
        for (Object[] fila : filas) {
            modelo.addRow(fila);
        }
        lblConteo.setText(filas.size() == 1 ? "1 registro" : filas.size() + " registros");
    }

    public String getTextoBusqueda() {
        return txtBuscar.getText().trim();
    }

    public void limpiarSeleccionTabla() {
        tabla.clearSelection();
    }

    public void setModo(String descripcion, boolean registroExistente) {
        lblModo.setText(descripcion);
        btnEliminar.setEnabled(registroExistente);
    }

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

    /** Pregunta Sí/No al usuario (se puede sobrescribir en pruebas automáticas). */
    public boolean confirmar(String mensaje) {
        int r = JOptionPane.showConfirmDialog(this, mensaje, "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return r == JOptionPane.YES_OPTION;
    }

    private static String html(String texto) {
        return "<html><body style='width:270px'>" + texto.replace("\n", "<br>") + "</body></html>";
    }

    public void setNuevoListener(ActionListener l) {
        btnNuevo.addActionListener(l);
    }

    public void setGuardarListener(ActionListener l) {
        btnGuardar.addActionListener(l);
    }

    public void setEliminarListener(ActionListener l) {
        btnEliminar.addActionListener(l);
    }

    public void setBuscarListener(ActionListener l) {
        btnBuscar.addActionListener(l);
    }

    /** Avisa con el índice de la fila (base 0) cuando el usuario selecciona una fila de la tabla. */
    public void setSeleccionListener(IntConsumer listener) {
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                listener.accept(fila);
            }
        });
    }
}
