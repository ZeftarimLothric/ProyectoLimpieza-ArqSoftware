package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.models.dto.ColoniaDTO;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Catálogo de colonias en memoria (Integrante 3 - Issue #11).
 * Los códigos postales son ilustrativos.
 *
 * TODO(#3): implementar IColoniaRepository cuando exista; el método ya tiene el nombre del contrato.
 * TODO(#2): devolver la entidad Colonia en lugar de ColoniaDTO.
 */
public class MockColoniaRepository {

    private final List<ColoniaDTO> colonias = List.of(
            new ColoniaDTO(1, "Centro Histórico", "25000"),
            new ColoniaDTO(2, "Bellavista", "25060"),
            new ColoniaDTO(3, "República Oriente", "25280"),
            new ColoniaDTO(4, "República Poniente", "25265"),
            new ColoniaDTO(5, "Mirasierra", "25016"),
            new ColoniaDTO(6, "Saltillo 2000", "25115"),
            new ColoniaDTO(7, "San Patricio", "25204"),
            new ColoniaDTO(8, "Doctores", "25250"),
            new ColoniaDTO(9, "Zaragoza", "25040"));

    /**
     * Coincide por parte del nombre (sin importar acentos ni mayúsculas) o por el inicio del código postal.
     */
    public List<ColoniaDTO> buscarPorNombreOCodigoPostal(String texto) {
        String buscado = normalizar(texto);
        List<ColoniaDTO> resultado = new ArrayList<>();
        for (ColoniaDTO c : colonias) {
            if (normalizar(c.getNombre()).contains(buscado) || c.getCodigoPostal().startsWith(buscado)) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    private String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }
}
