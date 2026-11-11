package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.models.entities.Colonia;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Implementación en memoria de IColoniaRepository para pruebas y demos sin MySQL
 * (Integrante 3 - Issue #11). Los códigos postales son ilustrativos.
 */
public class MockColoniaRepository implements IColoniaRepository {

    private static final String MUNICIPIO = "Saltillo";
    private static final String ESTADO = "Coahuila de Zaragoza";

    private final List<Colonia> colonias = new ArrayList<>();
    private int ultimoId = 0;

    public MockColoniaRepository() {
        agregarEjemplo("Centro Histórico", "25000");
        agregarEjemplo("Bellavista", "25060");
        agregarEjemplo("República Oriente", "25280");
        agregarEjemplo("República Poniente", "25265");
        agregarEjemplo("Mirasierra", "25016");
        agregarEjemplo("Saltillo 2000", "25115");
        agregarEjemplo("San Patricio", "25204");
        agregarEjemplo("Doctores", "25250");
        agregarEjemplo("Zaragoza", "25040");
    }

    private void agregarEjemplo(String nombre, String codigoPostal) {
        guardar(new Colonia(0, nombre, codigoPostal, MUNICIPIO, ESTADO));
    }

    @Override
    public List<Colonia> listar() {
        return new ArrayList<>(colonias);
    }

    @Override
    public Optional<Colonia> buscarPorId(int id) {
        return colonias.stream().filter(c -> c.getIdColonia() == id).findFirst();
    }

    /**
     * Igual que SqlColoniaRepository: el texto puede estar en cualquier parte del nombre o del código postal.
     * Además ignora acentos y mayúsculas, como la intercalación utf8mb4_unicode_ci de la BD.
     */
    @Override
    public List<Colonia> buscarPorNombreOCodigoPostal(String texto) {
        String buscado = normalizar(texto.trim());
        List<Colonia> resultado = new ArrayList<>();
        for (Colonia c : colonias) {
            if (normalizar(c.getNombre()).contains(buscado) || c.getCodigoPostal().contains(buscado)) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    @Override
    public boolean guardar(Colonia colonia) {
        ultimoId++;
        colonia.setIdColonia(ultimoId);
        colonias.add(colonia);
        return true;
    }

    @Override
    public boolean actualizar(Colonia colonia) {
        for (int i = 0; i < colonias.size(); i++) {
            if (colonias.get(i).getIdColonia() == colonia.getIdColonia()) {
                colonias.set(i, colonia);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        return colonias.removeIf(c -> c.getIdColonia() == id);
    }

    private String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }
}
