package com.saltillo.limpieza.controllers.catalogos;

import com.saltillo.limpieza.models.entities.TipoActividad;
import com.saltillo.limpieza.repositories.ITipoActividadRepository;
import com.saltillo.limpieza.views.catalogos.TipoActividadView;

import java.util.ArrayList;
import java.util.List;

/** CRUD de tipos de actividad (Issue #8). */
public class TipoActividadController extends CatalogoController<TipoActividad> {

    private final TipoActividadView v;

    public TipoActividadController(TipoActividadView vista, ITipoActividadRepository repositorio) {
        super(vista, repositorio);
        this.v = vista;
        iniciar();
    }

    @Override
    protected Object[] aFila(TipoActividad t) {
        return new Object[]{t.getIdTipoActividad(), t.getNombre(), t.getDescripcion() == null ? "" : t.getDescripcion()};
    }

    @Override
    protected int idDe(TipoActividad t) {
        return t.getIdTipoActividad();
    }

    @Override
    protected String descripcion(TipoActividad t) {
        return t.getNombre();
    }

    @Override
    protected String nombreEntidad() {
        return "tipo de actividad";
    }

    @Override
    protected void mostrarEnFormulario(TipoActividad t) {
        v.setNombre(t.getNombre());
        v.setDescripcion(t.getDescripcion() == null ? "" : t.getDescripcion());
    }

    @Override
    protected List<String> validar(boolean esNuevo) {
        List<String> e = new ArrayList<>();
        if (vacio(v.getNombre())) {
            e.add("• El nombre es obligatorio.");
        } else if (v.getNombre().trim().length() > 100) {
            e.add("• El nombre no puede pasar de 100 caracteres.");
        }
        if (v.getDescripcion().trim().length() > 200) {
            e.add("• La descripción no puede pasar de 200 caracteres.");
        }
        return e;
    }

    @Override
    protected TipoActividad leerFormulario(TipoActividad existente) {
        String desc = v.getDescripcion().trim();
        return new TipoActividad(
                existente == null ? 0 : existente.getIdTipoActividad(),
                v.getNombre().trim(),
                desc.isEmpty() ? null : desc);
    }
}
