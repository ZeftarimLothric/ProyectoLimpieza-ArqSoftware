package com.saltillo.limpieza.controllers.catalogos;

import com.saltillo.limpieza.repositories.ICrudRepository;
import com.saltillo.limpieza.repositories.RepositorioException;
import com.saltillo.limpieza.views.catalogos.CatalogoBaseView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Controlador base de los catálogos CRUD (Issue #8). Implementa una sola vez el flujo de las
 * 4 operaciones: consultar/buscar, alta, edición (guardar) y baja (con confirmación).
 * Cada catálogo solo define sus campos, su validación y cómo convertir formulario <-> entidad.
 *
 * Depende del contrato ICrudRepository (no de MySQL): inversión de dependencias.
 *
 * Importante: las subclases deben llamar a iniciar() al FINAL de su constructor.
 */
public abstract class CatalogoController<T> {

    protected static final String MSG_BD =
            "No se pudo completar la operación con la base de datos. Revisa que MySQL esté encendido.";

    protected final CatalogoBaseView vista;
    protected final ICrudRepository<T> repositorio;

    private List<T> mostrados = new ArrayList<>();
    private T seleccionado;

    protected CatalogoController(CatalogoBaseView vista, ICrudRepository<T> repositorio) {
        this.vista = vista;
        this.repositorio = repositorio;
        vista.setNuevoListener(e -> reiniciar(null));
        vista.setGuardarListener(e -> onGuardar());
        vista.setEliminarListener(e -> onEliminar());
        vista.setBuscarListener(e -> cargar());
        vista.setSeleccionListener(this::onSeleccion);
    }

    // ---------- lo que define cada catálogo ----------

    /** Fila de la tabla (mismo orden que las columnas de la vista). */
    protected abstract Object[] aFila(T entidad);

    protected abstract int idDe(T entidad);

    /** Texto corto que identifica el registro en los mensajes ("Juan Pérez"). */
    protected abstract String descripcion(T entidad);

    /** Nombre del catálogo en singular y minúsculas, para los mensajes ("empleado"). */
    protected abstract String nombreEntidad();

    protected abstract void mostrarEnFormulario(T entidad);

    /** Mensajes de campos obligatorios o inválidos; lista vacía si todo está bien. */
    protected abstract List<String> validar(boolean esNuevo);

    /** Crea la entidad con lo capturado; existente es null cuando es un alta. */
    protected abstract T leerFormulario(T existente);

    // ---------- ganchos opcionales ----------

    protected List<T> buscar(String texto) {
        List<T> todos = repositorio.listar();
        if (texto.isEmpty()) {
            return todos;
        }
        String q = texto.toLowerCase(Locale.ROOT);
        List<T> filtrados = new ArrayList<>();
        for (T e : todos) {
            for (Object celda : aFila(e)) {
                if (celda != null && celda.toString().toLowerCase(Locale.ROOT).contains(q)) {
                    filtrados.add(e);
                    break;
                }
            }
        }
        return filtrados;
    }

    protected String mensajeDuplicado() {
        return "Ya existe un registro con esos datos.";
    }

    /** Si no se puede borrar, devuelve el motivo; null si sí se puede. */
    protected String motivoNoEliminable(T entidad) {
        return null;
    }

    /** Se llama al pasar de "nuevo" a "editando" y viceversa. */
    protected void alCambiarModo(boolean editando) {
    }

    // ---------- flujo común ----------

    protected T getSeleccionado() {
        return seleccionado;
    }

    /** Carga la tabla y deja el formulario listo para un alta. */
    public final void iniciar() {
        reiniciar(null);
    }

    private void reiniciar(String mensajeExito) {
        seleccionado = null;
        vista.limpiarSeleccionTabla();
        vista.limpiarFormulario();
        vista.setModo("Nuevo " + nombreEntidad(), false);
        alCambiarModo(false);
        cargar();
        if (mensajeExito != null) {
            vista.mostrarExito(mensajeExito);
        }
    }

    private void cargar() {
        try {
            mostrados = buscar(vista.getTextoBusqueda());
        } catch (RuntimeException ex) {
            System.err.println("[Catálogo] " + ex);
            mostrados = new ArrayList<>();
            vista.mostrarError(MSG_BD);
        }
        List<Object[]> filas = new ArrayList<>();
        for (T e : mostrados) {
            filas.add(aFila(e));
        }
        vista.mostrarFilas(filas);
    }

    private void onSeleccion(int fila) {
        if (fila < 0 || fila >= mostrados.size()) {
            return;
        }
        seleccionado = mostrados.get(fila);
        vista.limpiarMensaje();
        mostrarEnFormulario(seleccionado);
        vista.setModo("Editando " + nombreEntidad() + " #" + idDe(seleccionado), true);
        alCambiarModo(true);
    }

    private void onGuardar() {
        vista.limpiarMensaje();
        List<String> errores = validar(seleccionado == null);
        if (!errores.isEmpty()) {
            vista.mostrarError(String.join("\n", errores));
            return;
        }
        boolean esNuevo = seleccionado == null;
        T entidad = leerFormulario(seleccionado);
        try {
            boolean ok = esNuevo ? repositorio.guardar(entidad) : repositorio.actualizar(entidad);
            if (!ok) {
                vista.mostrarError("No se guardó ningún cambio.");
                return;
            }
            reiniciar(esNuevo ? "Se agregó: " + descripcion(entidad) : "Se actualizó: " + descripcion(entidad));
        } catch (RepositorioException ex) {
            System.err.println("[Catálogo] " + ex + " / " + ex.getCause());
            vista.mostrarError(traducir(ex, false));
        }
    }

    private void onEliminar() {
        if (seleccionado == null) {
            vista.mostrarError("Selecciona un registro de la tabla para eliminarlo.");
            return;
        }
        String motivo = motivoNoEliminable(seleccionado);
        if (motivo != null) {
            vista.mostrarError(motivo);
            return;
        }
        String nombre = descripcion(seleccionado);
        if (!vista.confirmar("¿Eliminar " + nombreEntidad() + " \"" + nombre + "\"?\n"
                + "Esta acción no se puede deshacer.")) {
            return;
        }
        try {
            if (repositorio.eliminar(idDe(seleccionado))) {
                reiniciar("Se eliminó: " + nombre);
            } else {
                vista.mostrarError("El registro ya no existe.");
                reiniciar(null);
            }
        } catch (RepositorioException ex) {
            System.err.println("[Catálogo] " + ex + " / " + ex.getCause());
            vista.mostrarError(traducir(ex, true));
        }
    }

    // ---------- errores de MySQL en lenguaje de usuario ----------

    private String traducir(RepositorioException ex, boolean eliminando) {
        SQLException sql = causaSql(ex);
        if (sql != null) {
            if (sql.getErrorCode() == 1062) {
                return mensajeDuplicado();
            }
            boolean integridad = sql.getErrorCode() == 1451 || sql.getErrorCode() == 1452
                    || (sql.getSQLState() != null && sql.getSQLState().startsWith("23"));
            if (integridad) {
                return eliminando
                        ? "No se puede eliminar porque otros registros lo usan (cuadrillas o actividades). "
                          + "Si ya no se ocupa, márcalo como inactivo."
                        : "Los datos no son válidos para la base de datos.";
            }
        }
        return MSG_BD;
    }

    static SQLException causaSql(Throwable t) {
        while (t != null) {
            if (t instanceof SQLException) {
                return (SQLException) t;
            }
            t = t.getCause();
        }
        return null;
    }

    /** Utilidad para validar: true si el texto es nulo o vacío. */
    protected static boolean vacio(String s) {
        return s == null || s.trim().isEmpty();
    }
}
