package com.saltillo.limpieza.views.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;

/**
 * Diálogo modal con selector de fecha (calendario mensual interactivo) y hora/minutos (JSpinner).
 * Formatea automáticamente a "dd/MM/uuuu HH:mm" cumpliendo con los estándares del sistema.
 */
public class DateTimePickerDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");

    private YearMonth mesActual;
    private LocalDate fechaSeleccionada;
    private final JSpinner spinnerHora;
    private final JLabel lblMesAno = new JLabel("", SwingConstants.CENTER);
    private final JPanel pnlDias = new JPanel(new GridLayout(7, 7, 3, 3));
    private final Consumer<String> alAceptar;

    public DateTimePickerDialog(Window padre, String titulo, String valorInicial, Consumer<String> alAceptar) {
        super(padre, titulo, ModalityType.APPLICATION_MODAL);
        this.alAceptar = alAceptar;

        LocalDateTime ldt = parsearFecha(valorInicial);
        this.fechaSeleccionada = ldt.toLocalDate();
        this.mesActual = YearMonth.from(fechaSeleccionada);

        // Modelo de spinner para hora
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, ldt.getHour());
        cal.set(Calendar.MINUTE, ldt.getMinute());
        Date dateHora = cal.getTime();
        SpinnerDateModel modelHora = new SpinnerDateModel(dateHora, null, null, Calendar.MINUTE);
        this.spinnerHora = new JSpinner(modelHora);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinnerHora, "HH:mm");
        spinnerHora.setEditor(editor);
        spinnerHora.setFont(new Font("Segoe UI", Font.BOLD, 13));

        initUI();
        pack();
        setResizable(false);
        setLocationRelativeTo(padre);
    }

    private LocalDateTime parsearFecha(String texto) {
        if (texto != null && !texto.isBlank()) {
            try {
                return LocalDateTime.parse(texto.trim(), FORMATO);
            } catch (Exception ignored) {
            }
        }
        return LocalDateTime.now().withSecond(0).withNano(0);
    }

    private void initUI() {
        JPanel principal = new JPanel(new BorderLayout(0, 12));
        principal.setBackground(new Color(248, 250, 252));
        principal.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Cabecera del mes con navegación anterior/siguiente
        JPanel pnlNavegacion = new JPanel(new BorderLayout());
        pnlNavegacion.setOpaque(false);

        JButton btnAnt = crearBotonNav("◀");
        JButton btnSig = crearBotonNav("▶");
        btnAnt.addActionListener(e -> {
            mesActual = mesActual.minusMonths(1);
            actualizarCalendario();
        });
        btnSig.addActionListener(e -> {
            mesActual = mesActual.plusMonths(1);
            actualizarCalendario();
        });

        lblMesAno.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblMesAno.setForeground(new Color(30, 41, 59));

        pnlNavegacion.add(btnAnt, BorderLayout.WEST);
        pnlNavegacion.add(lblMesAno, BorderLayout.CENTER);
        pnlNavegacion.add(btnSig, BorderLayout.EAST);

        // Panel del calendario
        pnlDias.setOpaque(false);
        pnlDias.setPreferredSize(new Dimension(300, 210));
        actualizarCalendario();

        JPanel pnlCentro = new JPanel(new BorderLayout(0, 8));
        pnlCentro.setOpaque(false);
        pnlCentro.add(pnlNavegacion, BorderLayout.NORTH);
        pnlCentro.add(pnlDias, BorderLayout.CENTER);

        // Panel de selección de hora
        JPanel pnlHora = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        pnlHora.setOpaque(false);
        JLabel lblHora = new JLabel("Hora (24h):");
        lblHora.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblHora.setForeground(new Color(71, 85, 105));
        pnlHora.add(lblHora);
        pnlHora.add(spinnerHora);

        JButton btnAhora = new JButton("Ahora");
        btnAhora.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnAhora.setBackground(new Color(241, 245, 249));
        btnAhora.setFocusPainted(false);
        btnAhora.addActionListener(e -> {
            LocalDateTime ahora = LocalDateTime.now();
            fechaSeleccionada = ahora.toLocalDate();
            mesActual = YearMonth.from(fechaSeleccionada);
            actualizarCalendario();
            Calendar c = Calendar.getInstance();
            spinnerHora.setValue(c.getTime());
        });
        pnlHora.add(btnAhora);

        JPanel pnlMedio = new JPanel(new BorderLayout(0, 6));
        pnlMedio.setOpaque(false);
        pnlMedio.add(pnlCentro, BorderLayout.CENTER);
        pnlMedio.add(pnlHora, BorderLayout.SOUTH);

        // Botones Aceptar / Cancelar
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlBotones.setOpaque(false);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancelar.addActionListener(e -> dispose());

        JButton btnAceptar = new JButton("Seleccionar");
        btnAceptar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAceptar.setBackground(new Color(2, 132, 199));
        btnAceptar.setForeground(Color.WHITE);
        btnAceptar.setFocusPainted(false);
        btnAceptar.addActionListener(e -> {
            Date horaDate = (Date) spinnerHora.getValue();
            Calendar c = Calendar.getInstance();
            c.setTime(horaDate);
            LocalDateTime resultado = LocalDateTime.of(
                    fechaSeleccionada.getYear(),
                    fechaSeleccionada.getMonthValue(),
                    fechaSeleccionada.getDayOfMonth(),
                    c.get(Calendar.HOUR_OF_DAY),
                    c.get(Calendar.MINUTE)
            );
            if (alAceptar != null) {
                alAceptar.accept(resultado.format(FORMATO));
            }
            dispose();
        });

        pnlBotones.add(btnCancelar);
        pnlBotones.add(btnAceptar);

        principal.add(pnlMedio, BorderLayout.CENTER);
        principal.add(pnlBotones, BorderLayout.SOUTH);
        setContentPane(principal);
    }

    private JButton crearBotonNav(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setFocusPainted(false);
        b.setBackground(Color.WHITE);
        b.setPreferredSize(new Dimension(38, 26));
        return b;
    }

    private void actualizarCalendario() {
        pnlDias.removeAll();
        lblMesAno.setText(nombreMes(mesActual.getMonthValue()) + " " + mesActual.getYear());

        // Cabecera con días de la semana (Dom a Sáb)
        String[] cabecera = {"Do", "Lu", "Ma", "Mi", "Ju", "Vi", "Sá"};
        for (String c : cabecera) {
            JLabel lbl = new JLabel(c, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(new Color(100, 116, 139));
            pnlDias.add(lbl);
        }

        LocalDate primero = mesActual.atDay(1);
        int diaSemana = primero.getDayOfWeek().getValue() % 7; // Domingo = 0
        int diasEnMes = mesActual.lengthOfMonth();

        // Celdas vacías previas
        for (int i = 0; i < diaSemana; i++) {
            pnlDias.add(new JLabel(""));
        }

        // Días del mes
        for (int dia = 1; dia <= diasEnMes; dia++) {
            LocalDate fechaDia = mesActual.atDay(dia);
            JButton btnDia = new JButton(String.valueOf(dia));
            btnDia.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btnDia.setMargin(new Insets(1, 1, 1, 1));
            btnDia.setFocusPainted(false);

            boolean esSeleccionado = fechaDia.equals(fechaSeleccionada);
            boolean esHoy = fechaDia.equals(LocalDate.now());

            if (esSeleccionado) {
                btnDia.setBackground(new Color(2, 132, 199));
                btnDia.setForeground(Color.WHITE);
                btnDia.setFont(new Font("Segoe UI", Font.BOLD, 12));
            } else if (esHoy) {
                btnDia.setBackground(new Color(224, 242, 254));
                btnDia.setForeground(new Color(3, 105, 161));
                btnDia.setBorder(BorderFactory.createLineBorder(new Color(2, 132, 199)));
            } else {
                btnDia.setBackground(Color.WHITE);
                btnDia.setForeground(new Color(30, 41, 59));
            }

            btnDia.addActionListener(e -> {
                fechaSeleccionada = fechaDia;
                actualizarCalendario();
            });

            pnlDias.add(btnDia);
        }

        // Completar la cuadrícula si sobra espacio
        int totalCeldas = 7 + diaSemana + diasEnMes;
        int restantes = (totalCeldas % 7 == 0) ? 0 : 7 - (totalCeldas % 7);
        for (int i = 0; i < restantes; i++) {
            pnlDias.add(new JLabel(""));
        }

        pnlDias.revalidate();
        pnlDias.repaint();
    }

    private static String nombreMes(int mes) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                          "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return (mes >= 1 && mes <= 12) ? meses[mes - 1] : "";
    }
}
