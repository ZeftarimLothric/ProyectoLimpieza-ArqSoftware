package com.saltillo.limpieza.controllers.catalogos;

import com.saltillo.limpieza.models.entities.Colonia;
import com.saltillo.limpieza.repositories.IColoniaRepository;
import com.saltillo.limpieza.views.catalogos.ColoniaView;

import java.util.ArrayList;
import java.util.List;

/** CRUD de colonias SEPOMEX (Issue #8). La búsqueda usa IColoniaRepository.buscarPorNombreOCodigoPostal. */
public class ColoniaController extends CatalogoController<Colonia> {

    private final ColoniaView v;
    private final IColoniaRepository repo;

    public ColoniaController(ColoniaView vista, IColoniaRepository repositorio) {
        super(vista, repositorio);
        this.v = vista;
        this.repo = repositorio;
        iniciar();
    }

    @Override
    protected List<Colonia> buscar(String texto) {
        return texto.isEmpty() ? repo.listar() : repo.buscarPorNombreOCodigoPostal(texto);
    }

    @Override
    protected Object[] aFila(Colonia c) {
        return new Object[]{c.getIdColonia(), c.getNombre(), c.getCodigoPostal(), c.getMunicipio(), c.getEstado()};
    }

    @Override
    protected int idDe(Colonia c) {
        return c.getIdColonia();
    }

    @Override
    protected String descripcion(Colonia c) {
        return c.getNombre() + " (C.P. " + c.getCodigoPostal() + ")";
    }

    @Override
    protected String nombreEntidad() {
        return "colonia";
    }

    @Override
    protected void mostrarEnFormulario(Colonia c) {
        v.setNombre(c.getNombre());
        v.setCodigoPostal(c.getCodigoPostal());
        v.setMunicipio(c.getMunicipio());
        v.setEstado(c.getEstado());
    }

    @Override
    protected List<String> validar(boolean esNuevo) {
        List<String> e = new ArrayList<>();
        if (vacio(v.getNombre())) {
            e.add("• El nombre de la colonia es obligatorio.");
        } else if (v.getNombre().trim().length() > 100) {
            e.add("• El nombre de la colonia no puede pasar de 100 caracteres.");
        }
        if (vacio(v.getCodigoPostal())) {
            e.add("• El código postal es obligatorio.");
        } else if (!v.getCodigoPostal().trim().matches("\\d{5}")) {
            e.add("• El código postal debe tener exactamente 5 dígitos.");
        }
        if (vacio(v.getMunicipio())) {
            e.add("• El municipio es obligatorio.");
        }
        if (vacio(v.getEstado())) {
            e.add("• El estado es obligatorio.");
        }
        return e;
    }

    @Override
    protected Colonia leerFormulario(Colonia existente) {
        return new Colonia(
                existente == null ? 0 : existente.getIdColonia(),
                v.getNombre().trim(),
                v.getCodigoPostal().trim(),
                v.getMunicipio().trim(),
                v.getEstado().trim());
    }
}
