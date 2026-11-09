package com.saltillo.limpieza.views.actividades;

import com.saltillo.limpieza.views.components.RoundedPanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Panel de Evidencias (Integrante 3 - Issue #12).
 *
 * Arquitectura MVC Estricta:
 * Vista pasiva. Muestra las imágenes que recibe del EvidenciaController y le
 * avisa de las acciones del usuario; no valida ni accede a la carpeta de evidencias.
 * Se reutiliza en solo lectura desde el detalle de actividad (Issue #13).
 */
public class EvidenciaPanel extends RoundedPanel {
    private static final long serialVersionUID = 1L;

    private static final int ANCHO_MINIATURA = 120;
    private static final int ALTO_MINIATURA = 90;
    private static final String TARJETA_MINIATURAS = "miniaturas";
    private static final String TARJETA_VACIO = "vacio";

    private final DefaultListModel<String> modeloMiniaturas = new DefaultListModel<>();
    private final Map<String, Icon> iconos = new HashMap<>();
    private final CardLayout layoutContenido = new CardLayout();
    private final JPanel pnlContenido = new JPanel(layoutContenido);
    private final JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
    private final JLabel lblVacio = new JLabel("", SwingConstants.CENTER);

    private JList<String> lstMiniaturas;
    private JButton btnAgregar;
    private JButton btnQuitar;
    private boolean soloLectura;
    private Consumer<String> abrirImagenListener;

    public EvidenciaPanel() {
        super(16, Color.WHITE, new Color(226, 232, 240));
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        initEncabezado();
        initContenido();
        mostrarEvidencias(Map.of());
    }

    private void initEncabezado() {
        JPanel pnlEncabezado = new JPanel(new BorderLayout());
        pnlEncabezado.setOpaque(false);

        JLabel lblTitulo = new JLabel("Evidencias");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(new Color(15, 23, 42));
        pnlEncabezado.add(lblTitulo, BorderLayout.WEST);

        pnlBotones.setOpaque(false);
        btnQuitar = crearBoton("Quitar", Color.WHITE, new Color(241, 245, 249), new Color(71, 85, 105), true);
        btnAgregar = crearBoton("Agregar imagen", new Color(2, 132, 199), new Color(3, 105, 161), Color.WHITE, false);
        pnlBotones.add(btnQuitar);
        pnlBotones.add(btnAgregar);
        pnlEncabezado.add(pnlBotones, BorderLayout.EAST);

        add(pnlEncabezado, BorderLayout.NORTH);
    }

    private void initContenido() {
        lstMiniaturas = new JList<>(modeloMiniaturas);
        lstMiniaturas.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        lstMiniaturas.setVisibleRowCount(1);
        lstMiniaturas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstMiniaturas.setFixedCellWidth(ANCHO_MINIATURA + 12);
        lstMiniaturas.setFixedCellHeight(ALTO_MINIATURA + 12);
        lstMiniaturas.setCellRenderer(this::crearCeldaMiniatura);
        lstMiniaturas.setToolTipText("Doble clic para ver la imagen completa");
        lstMiniaturas.addListSelectionListener(e -> actualizarBotonQuitar());
        lstMiniaturas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    notificarAbrirImagen(e.getPoint());
                }
            }
        });

        JScrollPane scroll = new JScrollPane(lstMiniaturas,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        lblVacio.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblVacio.setForeground(new Color(100, 116, 139));

        pnlContenido.setOpaque(false);
        pnlContenido.setPreferredSize(new Dimension(ANCHO_MINIATURA * 4, ALTO_MINIATURA + 34));
        pnlContenido.add(scroll, TARJETA_MINIATURAS);
        pnlContenido.add(lblVacio, TARJETA_VACIO);
        add(pnlContenido, BorderLayout.CENTER);
    }

    private JButton crearBoton(String texto, Color fondo, Color fondoHover, Color colorTexto, boolean conBorde) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() && isEnabled() ? fondoHover : fondo);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                if (conBorde) {
                    g2.setColor(new Color(203, 213, 225));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        boton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        boton.setForeground(colorTexto);
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(false);
        boton.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private Component crearCeldaMiniatura(JList<? extends String> lista, String nombre, int indice,
                                          boolean seleccionada, boolean foco) {
        Icon icono = iconos.get(nombre);
        JLabel celda = new JLabel(icono == null ? "No disponible" : "", icono, SwingConstants.CENTER);
        celda.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        celda.setForeground(new Color(100, 116, 139));
        celda.setOpaque(true);
        celda.setBackground(seleccionada ? new Color(224, 242, 254) : new Color(241, 245, 249));
        celda.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, 2, 2, 2),
                BorderFactory.createLineBorder(seleccionada ? new Color(2, 132, 199) : new Color(226, 232, 240), 2)));
        return celda;
    }

    private Icon crearMiniatura(BufferedImage imagen) {
        double escala = Math.min((double) ANCHO_MINIATURA / imagen.getWidth(), (double) ALTO_MINIATURA / imagen.getHeight());
        int ancho = Math.max(1, (int) (imagen.getWidth() * escala));
        int alto = Math.max(1, (int) (imagen.getHeight() * escala));

        BufferedImage miniatura = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = miniatura.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(imagen, 0, 0, ancho, alto, null);
        g2.dispose();
        return new ImageIcon(miniatura);
    }

    private void notificarAbrirImagen(Point punto) {
        int indice = lstMiniaturas.locationToIndex(punto);
        boolean sobreCelda = indice >= 0 && lstMiniaturas.getCellBounds(indice, indice).contains(punto);
        if (sobreCelda && abrirImagenListener != null) {
            abrirImagenListener.accept(modeloMiniaturas.get(indice));
        }
    }

    private void actualizarBotonQuitar() {
        btnQuitar.setEnabled(lstMiniaturas.getSelectedIndex() >= 0);
    }

    private void actualizarMensajeVacio() {
        lblVacio.setText(soloLectura
                ? "Esta actividad no tiene evidencias registradas."
                : "Aún no hay imágenes. Usa \"Agregar imagen\" para adjuntar evidencias.");
    }

    // =========================================================================
    // MÉTODOS PÚBLICOS DE VISTA (STRICT MVC)
    // =========================================================================

    /**
     * @param imagenes nombre de cada evidencia, en orden; un valor null indica que no se pudo cargar
     */
    public void mostrarEvidencias(Map<String, BufferedImage> imagenes) {
        iconos.clear();
        modeloMiniaturas.clear();
        imagenes.forEach((nombre, imagen) -> {
            if (imagen != null) {
                iconos.put(nombre, crearMiniatura(imagen));
            }
            modeloMiniaturas.addElement(nombre);
        });

        actualizarBotonQuitar();
        actualizarMensajeVacio();
        layoutContenido.show(pnlContenido, modeloMiniaturas.isEmpty() ? TARJETA_VACIO : TARJETA_MINIATURAS);
    }

    public String getEvidenciaSeleccionada() {
        return lstMiniaturas.getSelectedValue();
    }

    public void setSoloLectura(boolean soloLectura) {
        this.soloLectura = soloLectura;
        pnlBotones.setVisible(!soloLectura);
        actualizarMensajeVacio();
    }

    /**
     * @return los archivos elegidos, o una lista vacía si el usuario canceló
     */
    public List<Path> seleccionarImagenes() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Seleccionar imágenes de evidencia");
        selector.setMultiSelectionEnabled(true);
        selector.setFileFilter(new FileNameExtensionFilter("Imágenes JPG o PNG", "jpg", "jpeg", "png"));

        List<Path> archivos = new ArrayList<>();
        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            for (File archivo : selector.getSelectedFiles()) {
                archivos.add(archivo.toPath());
            }
        }
        return archivos;
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Evidencias", JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarImagenCompleta(BufferedImage imagen) {
        JDialog dialogo = new JDialog(SwingUtilities.getWindowAncestor(this), "Evidencia", Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialogo.add(new JScrollPane(new JLabel(new ImageIcon(imagen))));

        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        dialogo.setSize(Math.min(imagen.getWidth() + 40, pantalla.width * 85 / 100),
                Math.min(imagen.getHeight() + 60, pantalla.height * 85 / 100));
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    public void setAgregarListener(ActionListener l) {
        btnAgregar.addActionListener(l);
    }

    public void setQuitarListener(ActionListener l) {
        btnQuitar.addActionListener(l);
    }

    public void setAbrirImagenListener(Consumer<String> l) {
        abrirImagenListener = l;
    }
}
