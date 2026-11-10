package com.saltillo.limpieza.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Pantalla de acceso de administrador (Issue #7).
 *
 * Vista pura (MVC): solo dibuja y expone getters/setters. No contiene SQL ni lógica de
 * validación; eso lo hace LoginController.
 */
public class LoginView extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final Color COLOR_FONDO = new Color(15, 23, 42);      // Slate-900
    private static final Color COLOR_TARJETA = new Color(248, 250, 252); // Slate-50
    private static final Color COLOR_ACENTO = new Color(2, 132, 199);    // Sky-600
    private static final Color COLOR_ERROR = new Color(185, 28, 28);     // Red-700

    private final JTextField txtUsuario = new JTextField(20);
    private final JPasswordField txtContrasena = new JPasswordField(20);
    private final JButton btnIngresar = new JButton("Iniciar sesión");
    private final JLabel lblError = new JLabel(" ");

    public LoginView() {
        super("Municipio de Saltillo • Acceso al sistema");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(COLOR_FONDO);
        fondo.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        fondo.add(construirTarjeta());
        setContentPane(fondo);

        getRootPane().setDefaultButton(btnIngresar); // Enter = iniciar sesión
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel construirTarjeta() {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(COLOR_TARJETA);
        tarjeta.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JLabel titulo = new JLabel("SALTILLO LIMPIO");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(COLOR_FONDO);
        JLabel subtitulo = new JLabel("Gestión de Actividades de Limpieza · Acceso de administrador");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(new Color(100, 116, 139));

        tarjeta.add(alineado(titulo));
        tarjeta.add(alineado(subtitulo));
        tarjeta.add(Box.createVerticalStrut(22));
        tarjeta.add(alineado(etiqueta("Usuario")));
        tarjeta.add(alineado(txtUsuario));
        tarjeta.add(Box.createVerticalStrut(12));
        tarjeta.add(alineado(etiqueta("Contraseña")));
        tarjeta.add(alineado(txtContrasena));
        tarjeta.add(Box.createVerticalStrut(10));

        lblError.setForeground(COLOR_ERROR);
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tarjeta.add(alineado(lblError));
        tarjeta.add(Box.createVerticalStrut(10));

        btnIngresar.setBackground(COLOR_ACENTO);
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnIngresar.setFocusPainted(false);
        btnIngresar.setOpaque(true);
        btnIngresar.setBorderPainted(false);
        btnIngresar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnIngresar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        tarjeta.add(alineado(btnIngresar));
        return tarjeta;
    }

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(51, 65, 85));
        return l;
    }

    private JComponent alineado(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    // ---------- API para el controlador ----------

    public String getUsuario() {
        return txtUsuario.getText();
    }

    public char[] getContrasena() {
        return txtContrasena.getPassword();
    }

    public void mostrarError(String mensaje) {
        lblError.setText(mensaje);
    }

    public void limpiarContrasena() {
        txtContrasena.setText("");
        txtContrasena.requestFocusInWindow();
    }

    public void setLoginListener(ActionListener listener) {
        btnIngresar.addActionListener(listener);
    }
}
