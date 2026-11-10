package com.saltillo.limpieza.controllers.catalogos;

import com.saltillo.limpieza.models.entities.Empleado;
import com.saltillo.limpieza.repositories.IEmpleadoRepository;
import com.saltillo.limpieza.views.catalogos.EmpleadoView;

import java.util.ArrayList;
import java.util.List;

/** CRUD de empleados (Issue #8). */
public class EmpleadoController extends CatalogoController<Empleado> {

    private static final String PATRON_TELEFONO = "[0-9 ()+\\-]{7,15}";

    private final EmpleadoView v;

    public EmpleadoController(EmpleadoView vista, IEmpleadoRepository repositorio) {
        super(vista, repositorio);
        this.v = vista;
        iniciar();
    }

    private static String nombreCompleto(Empleado e) {
        String materno = vacio(e.getApellidoMaterno()) ? "" : " " + e.getApellidoMaterno();
        return e.getNombre() + " " + e.getApellidoPaterno() + materno;
    }

    @Override
    protected Object[] aFila(Empleado e) {
        return new Object[]{e.getIdEmpleado(), nombreCompleto(e), e.getTelefono() == null ? "" : e.getTelefono(),
                e.getPuesto(), e.isActivo() ? "ACTIVO" : "INACTIVO"};
    }

    @Override
    protected int idDe(Empleado e) {
        return e.getIdEmpleado();
    }

    @Override
    protected String descripcion(Empleado e) {
        return nombreCompleto(e);
    }

    @Override
    protected String nombreEntidad() {
        return "empleado";
    }

    @Override
    protected void mostrarEnFormulario(Empleado e) {
        v.setNombre(e.getNombre());
        v.setApellidoPaterno(e.getApellidoPaterno());
        v.setApellidoMaterno(e.getApellidoMaterno() == null ? "" : e.getApellidoMaterno());
        v.setTelefono(e.getTelefono() == null ? "" : e.getTelefono());
        v.setPuesto(e.getPuesto());
        v.setActivo(e.isActivo());
    }

    @Override
    protected List<String> validar(boolean esNuevo) {
        List<String> e = new ArrayList<>();
        if (vacio(v.getNombre())) {
            e.add("• El nombre es obligatorio.");
        }
        if (vacio(v.getApellidoPaterno())) {
            e.add("• El apellido paterno es obligatorio.");
        }
        if (vacio(v.getPuesto())) {
            e.add("• El puesto es obligatorio.");
        }
        if (v.getNombre().trim().length() > 100 || v.getApellidoPaterno().trim().length() > 100
                || v.getApellidoMaterno().trim().length() > 100) {
            e.add("• Nombre y apellidos no pueden pasar de 100 caracteres.");
        }
        if (v.getPuesto().trim().length() > 50) {
            e.add("• El puesto no puede pasar de 50 caracteres.");
        }
        String tel = v.getTelefono().trim();
        if (!tel.isEmpty() && !tel.matches(PATRON_TELEFONO)) {
            e.add("• El teléfono debe tener de 7 a 15 caracteres (dígitos, espacios, + - ( )).");
        }
        return e;
    }

    @Override
    protected Empleado leerFormulario(Empleado existente) {
        String materno = v.getApellidoMaterno().trim();
        String telefono = v.getTelefono().trim();
        return new Empleado(
                existente == null ? 0 : existente.getIdEmpleado(),
                v.getNombre().trim(),
                v.getApellidoPaterno().trim(),
                materno.isEmpty() ? null : materno,
                telefono.isEmpty() ? null : telefono,
                v.getPuesto().trim(),
                v.isActivo());
    }
}
