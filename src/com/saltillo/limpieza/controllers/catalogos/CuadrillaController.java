package com.saltillo.limpieza.controllers.catalogos;

import com.saltillo.limpieza.models.entities.Cuadrilla;
import com.saltillo.limpieza.models.entities.Empleado;
import com.saltillo.limpieza.models.entities.IntegranteCuadrilla;
import com.saltillo.limpieza.repositories.ICuadrillaPersonalRepository;
import com.saltillo.limpieza.repositories.ReglaNegocioException;
import com.saltillo.limpieza.repositories.RepositorioException;
import com.saltillo.limpieza.views.catalogos.CuadrillaView;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador del CRUD de cuadrillas con jefe e integrantes (Issue #9).
 *
 * - El jefe se elige entre los empleados (obligatorio) y se guarda junto con la cuadrilla.
 * - El personal se agrega y se quita desde la misma pantalla.
 * - Un empleado no puede estar en dos cuadrillas activas: el combo solo ofrece a los disponibles
 *   y el repositorio vuelve a validarlo (sus mensajes se muestran tal cual).
 */
public class CuadrillaController {

    private static final String MSG_BD =
            "No se pudo completar la operación con la base de datos. Revisa que MySQL esté encendido.";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final CuadrillaView vista;
    private final ICuadrillaPersonalRepository repositorio;

    private List<Cuadrilla> cuadrillas = new ArrayList<>();
    private List<IntegranteCuadrilla> integrantes = new ArrayList<>();
    private Cuadrilla seleccionada;

    public CuadrillaController(CuadrillaView vista, ICuadrillaPersonalRepository repositorio) {
        this.vista = vista;
        this.repositorio = repositorio;
        vista.setNuevaListener(e -> prepararNueva(null));
        vista.setGuardarListener(e -> onGuardar());
        vista.setEliminarListener(e -> onEliminar());
        vista.setAgregarIntegranteListener(e -> onAgregarIntegrante());
        vista.setQuitarIntegranteListener(e -> onQuitarIntegrante());
        vista.setSeleccionCuadrillaListener(this::onSeleccion);
        prepararNueva(null);
    }

    // ---------- carga ----------

    private void recargarCuadrillas() {
        try {
            cuadrillas = repositorio.listar();
        } catch (RuntimeException ex) {
            System.err.println("[Cuadrillas] " + ex);
            cuadrillas = new ArrayList<>();
            vista.mostrarError(MSG_BD);
        }
        List<Object[]> filas = new ArrayList<>();
        for (Cuadrilla c : cuadrillas) {
            String jefe = "— sin jefe —";
            int total = 0;
            try {
                List<IntegranteCuadrilla> personal = repositorio.listarIntegrantes(c.getIdCuadrilla());
                total = personal.size();
                for (IntegranteCuadrilla i : personal) {
                    if (i.isJefe()) {
                        jefe = CuadrillaView.nombreCompleto(i.getEmpleado());
                    }
                }
            } catch (RuntimeException ex) {
                System.err.println("[Cuadrillas] " + ex);
            }
            filas.add(new Object[]{c.getIdCuadrilla(), c.getNombre(), jefe, total,
                    c.isActivo() ? "ACTIVA" : "INACTIVA"});
        }
        vista.mostrarCuadrillas(filas);
    }

    /** Carga personal, combo de agregar y combo de jefe de la cuadrilla seleccionada (o de una nueva). */
    private void cargarPersonalYOpciones() {
        int id = seleccionada == null ? 0 : seleccionada.getIdCuadrilla();
        List<Empleado> disponibles = new ArrayList<>();
        integrantes = new ArrayList<>();
        try {
            if (seleccionada != null) {
                integrantes = repositorio.listarIntegrantes(id);
            }
            disponibles = repositorio.listarEmpleadosDisponibles(id);
        } catch (RuntimeException ex) {
            System.err.println("[Cuadrillas] " + ex);
            vista.mostrarError(MSG_BD);
        }

        List<Object[]> filas = new ArrayList<>();
        List<Empleado> opcionesJefe = new ArrayList<>();
        Integer idJefe = null;
        for (IntegranteCuadrilla i : integrantes) {
            Empleado e = i.getEmpleado();
            filas.add(new Object[]{CuadrillaView.nombreCompleto(e), e.getPuesto(),
                    i.isJefe() ? "JEFE" : "INTEGRANTE",
                    i.getFechaAsignacion() == null ? "" : i.getFechaAsignacion().format(FORMATO_FECHA)});
            if (i.isJefe()) {
                idJefe = e.getIdEmpleado();
            }
            if (i.isJefe() || e.isActivo()) {
                opcionesJefe.add(e);
            }
        }
        opcionesJefe.addAll(disponibles);

        vista.mostrarIntegrantes(filas);
        vista.setEmpleadosDisponibles(disponibles);
        vista.setOpcionesJefe(opcionesJefe, idJefe);
        vista.setPersonalHabilitado(seleccionada != null);
    }

    // ---------- eventos ----------

    private void prepararNueva(String mensajeExito) {
        seleccionada = null;
        vista.limpiarSeleccionCuadrillas();
        vista.setNombre("");
        vista.setActiva(true);
        vista.limpiarMensaje();
        vista.setModo("Nueva cuadrilla", false);
        recargarCuadrillas();
        cargarPersonalYOpciones();
        if (mensajeExito != null) {
            vista.mostrarExito(mensajeExito);
        }
    }

    private void onSeleccion(int fila) {
        if (fila < 0 || fila >= cuadrillas.size()) {
            return;
        }
        seleccionar(cuadrillas.get(fila));
    }

    private void seleccionar(Cuadrilla c) {
        seleccionada = c;
        vista.limpiarMensaje();
        vista.setNombre(c.getNombre());
        vista.setActiva(c.isActivo());
        vista.setModo("Editando cuadrilla #" + c.getIdCuadrilla(), true);
        cargarPersonalYOpciones();
    }

    private void onGuardar() {
        vista.limpiarMensaje();
        List<String> errores = new ArrayList<>();
        String nombre = vista.getNombre().trim();
        if (nombre.isEmpty()) {
            errores.add("• El nombre de la cuadrilla es obligatorio.");
        } else if (nombre.length() > 100) {
            errores.add("• El nombre no puede pasar de 100 caracteres.");
        }
        Empleado jefe = vista.getJefeSeleccionado();
        if (jefe == null) {
            errores.add("• Elige al jefe de la cuadrilla.");
        }
        if (!errores.isEmpty()) {
            vista.mostrarError(String.join("\n", errores));
            return;
        }

        boolean esNueva = seleccionada == null;
        Cuadrilla c = new Cuadrilla(esNueva ? 0 : seleccionada.getIdCuadrilla(), nombre, vista.isActiva());
        try {
            boolean ok = esNueva
                    ? repositorio.guardarConJefe(c, jefe.getIdEmpleado())
                    : repositorio.actualizarConJefe(c, jefe.getIdEmpleado());
            if (!ok) {
                vista.mostrarError("No se guardó ningún cambio.");
                return;
            }
            recargarCuadrillas();
            seleccionarPorId(c.getIdCuadrilla());
            vista.mostrarExito(esNueva
                    ? "Cuadrilla creada. Ya puedes agregar a su personal."
                    : "Cuadrilla actualizada.");
        } catch (ReglaNegocioException ex) {
            vista.mostrarError(ex.getMessage());
        } catch (RepositorioException ex) {
            System.err.println("[Cuadrillas] " + ex + " / " + ex.getCause());
            vista.mostrarError(MSG_BD);
        }
    }

    private void onEliminar() {
        if (seleccionada == null) {
            vista.mostrarError("Selecciona una cuadrilla de la tabla para eliminarla.");
            return;
        }
        String detalle = integrantes.isEmpty() ? "" : "\nSe quitará también a sus " + integrantes.size() + " integrantes.";
        if (!vista.confirmar("¿Eliminar la cuadrilla \"" + seleccionada.getNombre() + "\"?" + detalle
                + "\nEsta acción no se puede deshacer.")) {
            return;
        }
        String nombre = seleccionada.getNombre();
        try {
            repositorio.eliminar(seleccionada.getIdCuadrilla());
            prepararNueva("Se eliminó la cuadrilla: " + nombre);
        } catch (ReglaNegocioException ex) {
            vista.mostrarError(ex.getMessage());
        } catch (RepositorioException ex) {
            System.err.println("[Cuadrillas] " + ex + " / " + ex.getCause());
            vista.mostrarError(tieneRestriccion(ex)
                    ? "No se puede eliminar: la cuadrilla tiene actividades registradas. Mejor márcala como inactiva."
                    : MSG_BD);
        }
    }

    private void onAgregarIntegrante() {
        if (seleccionada == null) {
            vista.mostrarError("Primero guarda la cuadrilla.");
            return;
        }
        Empleado e = vista.getDisponibleSeleccionado();
        if (e == null) {
            vista.mostrarError("Elige al empleado que quieres agregar.");
            return;
        }
        try {
            repositorio.agregarIntegrante(seleccionada.getIdCuadrilla(), e.getIdEmpleado());
            recargarCuadrillas();
            seleccionarPorId(seleccionada.getIdCuadrilla());
            vista.mostrarExito(CuadrillaView.nombreCompleto(e) + " se agregó a la cuadrilla.");
        } catch (ReglaNegocioException ex) {
            vista.mostrarError(ex.getMessage());
            cargarPersonalYOpciones();
        } catch (RepositorioException ex) {
            System.err.println("[Cuadrillas] " + ex + " / " + ex.getCause());
            vista.mostrarError(MSG_BD);
        }
    }

    private void onQuitarIntegrante() {
        if (seleccionada == null) {
            vista.mostrarError("Primero selecciona una cuadrilla.");
            return;
        }
        int fila = vista.getFilaIntegranteSeleccionada();
        if (fila < 0 || fila >= integrantes.size()) {
            vista.mostrarError("Selecciona en la tabla de personal a quién quieres quitar.");
            return;
        }
        IntegranteCuadrilla i = integrantes.get(fila);
        String nombre = CuadrillaView.nombreCompleto(i.getEmpleado());
        if (!vista.confirmar("¿Quitar a " + nombre + " de la cuadrilla \"" + seleccionada.getNombre() + "\"?")) {
            return;
        }
        try {
            repositorio.quitarIntegrante(seleccionada.getIdCuadrilla(), i.getEmpleado().getIdEmpleado());
            recargarCuadrillas();
            seleccionarPorId(seleccionada.getIdCuadrilla());
            vista.mostrarExito(nombre + " ya no forma parte de la cuadrilla.");
        } catch (ReglaNegocioException ex) {
            vista.mostrarError(ex.getMessage());
        } catch (RepositorioException ex) {
            System.err.println("[Cuadrillas] " + ex + " / " + ex.getCause());
            vista.mostrarError(MSG_BD);
        }
    }

    // ---------- utilidades ----------

    /** Vuelve a seleccionar la cuadrilla con ese id en la tabla ya recargada y refresca el panel derecho. */
    private void seleccionarPorId(int id) {
        for (int i = 0; i < cuadrillas.size(); i++) {
            if (cuadrillas.get(i).getIdCuadrilla() == id) {
                vista.seleccionarCuadrilla(i);   // dispara onSeleccion -> seleccionar()
                if (seleccionada == null || seleccionada.getIdCuadrilla() != id) {
                    seleccionar(cuadrillas.get(i));
                }
                return;
            }
        }
    }

    private static boolean tieneRestriccion(Throwable t) {
        while (t != null) {
            if (t instanceof SQLException) {
                SQLException s = (SQLException) t;
                return s.getErrorCode() == 1451 || (s.getSQLState() != null && s.getSQLState().startsWith("23"));
            }
            t = t.getCause();
        }
        return false;
    }
}
