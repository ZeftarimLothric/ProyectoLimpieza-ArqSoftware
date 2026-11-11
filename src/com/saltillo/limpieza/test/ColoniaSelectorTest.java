package com.saltillo.limpieza.test;

import com.saltillo.limpieza.controllers.ColoniaSelectorController;
import com.saltillo.limpieza.models.entities.Colonia;
import com.saltillo.limpieza.repositories.MockColoniaRepository;
import com.saltillo.limpieza.views.actividades.ColoniaSelectorDialog;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Pruebas del selector de colonias atendidas (Integrante 3 - Issue #11).
 */
public class ColoniaSelectorTest {

    private static final MockColoniaRepository CATALOGO = new MockColoniaRepository();

    /** Sustituye la ventana modal: en lugar de esperar al usuario ejecuta un guion de acciones. */
    private static class DialogDePrueba extends ColoniaSelectorDialog {
        private static final long serialVersionUID = 1L;
        private Consumer<ColoniaSelectorController> guion = c -> { };
        private ColoniaSelectorController controller;
        private List<Colonia> ultimosResultados = new ArrayList<>();
        private List<Colonia> ultimasSeleccionadas = new ArrayList<>();
        private String ultimoError;
        private boolean abierto;

        @Override
        public void abrir(Component padre) {
            abierto = true;
            guion.accept(controller);
        }

        @Override
        public void cerrar() {
            abierto = false;
        }

        @Override
        public void mostrarResultados(List<Colonia> colonias) {
            ultimosResultados = colonias;
        }

        @Override
        public void mostrarSeleccionadas(List<Colonia> colonias) {
            ultimasSeleccionadas = new ArrayList<>(colonias);
        }

        @Override
        public void mostrarError(String mensaje) {
            ultimoError = mensaje;
        }
    }

    public static void main(String[] args) {
        System.out.println("===================================================================");
        System.out.println("VERIFICACION DEL SELECTOR DE COLONIAS - INTEGRANTE 3 (ISSUE #11)");
        System.out.println("===================================================================");

        testBusqueda();
        testSeleccionMultiple();
        testColoniaObligatoria();
        testCancelar();
        testResolverNombres();
        testMockImplementaContrato();

        System.out.println("\n>>> TODAS LAS PRUEBAS DEL SELECTOR DE COLONIAS PASARON CON EXITO <<<");
    }

    private static void testBusqueda() {
        System.out.print("[TEST 1] Verificando busqueda por nombre y codigo postal... ");
        DialogDePrueba dialog = new DialogDePrueba();
        ColoniaSelectorController controller = crear(dialog);

        controller.buscar("");
        assert dialog.ultimosResultados.size() == 9 : "Sin texto deben mostrarse todas";

        controller.buscar("  republica ");
        assert nombres(dialog.ultimosResultados).equals(List.of("República Oriente", "República Poniente"))
                : "Debe ignorar acentos, mayúsculas y espacios";

        controller.buscar("2500");
        assert nombres(dialog.ultimosResultados).equals(List.of("Centro Histórico"))
                : "Debe buscar por inicio del código postal";

        controller.buscar("xyz");
        assert dialog.ultimosResultados.isEmpty() : "Sin coincidencias debe quedar vacío";
        System.out.println("PASO (nombre, codigo postal y sin coincidencias)");
    }

    private static void testSeleccionMultiple() {
        System.out.print("[TEST 2] Verificando seleccion multiple sin duplicados... ");
        DialogDePrueba dialog = new DialogDePrueba();
        ColoniaSelectorController controller = crear(dialog);
        Colonia centro = CATALOGO.buscarPorNombreOCodigoPostal("Centro").get(0);
        List<Colonia> republicas = CATALOGO.buscarPorNombreOCodigoPostal("República");

        dialog.guion = c -> {
            c.agregar(republicas);
            c.agregar(List.of(copiar(republicas.get(0))));
            c.quitar(List.of(centro));
            c.aceptar();
        };
        Optional<List<Colonia>> resultado = controller.seleccionar(null, List.of(centro));

        assert resultado.isPresent() : "Aceptar debe devolver las colonias";
        assert nombres(resultado.get()).equals(List.of("República Oriente", "República Poniente"))
                : "Debe sumar varias, sin duplicar y permitir quitar: " + resultado.get();
        assert !dialog.abierto : "Aceptar debe cerrar el diálogo";
        System.out.println("PASO (agregar varias, duplicado por ID ignorado y quitar)");
    }

    private static void testColoniaObligatoria() {
        System.out.print("[TEST 3] Verificando que se exija al menos una colonia... ");
        DialogDePrueba dialog = new DialogDePrueba();
        ColoniaSelectorController controller = crear(dialog);
        List<Colonia> centro = CATALOGO.buscarPorNombreOCodigoPostal("Centro");

        dialog.guion = c -> {
            c.aceptar();
            assert "Selecciona al menos una colonia.".equals(dialog.ultimoError) : "Debe avisar que falta la colonia";
            assert dialog.abierto : "Sin colonias el diálogo sigue abierto";
            c.agregar(centro);
            c.aceptar();
        };
        Optional<List<Colonia>> resultado = controller.seleccionar(null, List.of());

        assert resultado.isPresent() && resultado.get().equals(centro) : "Tras agregar una colonia debe aceptarse";
        System.out.println("PASO (rechazo sin colonias y aceptacion con una)");
    }

    private static void testCancelar() {
        System.out.print("[TEST 4] Verificando cancelar y reapertura... ");
        DialogDePrueba dialog = new DialogDePrueba();
        ColoniaSelectorController controller = crear(dialog);
        List<Colonia> iniciales = CATALOGO.buscarPorNombreOCodigoPostal("Doctores");

        dialog.guion = c -> {
            c.agregar(CATALOGO.buscarPorNombreOCodigoPostal("Zaragoza"));
            c.cancelar();
        };
        Optional<List<Colonia>> resultado = controller.seleccionar(null, iniciales);
        assert resultado.isEmpty() : "Cancelar no debe devolver colonias";

        dialog.guion = ColoniaSelectorController::aceptar;
        resultado = controller.seleccionar(null, iniciales);
        assert resultado.isPresent() && resultado.get().equals(iniciales) : "Lo cancelado no debe arrastrarse a la siguiente apertura";
        System.out.println("PASO (cancelar descarta los cambios)");
    }

    private static void testResolverNombres() {
        System.out.print("[TEST 5] Verificando recuperacion de colonias guardadas por nombre... ");
        ColoniaSelectorController controller = crear(new DialogDePrueba());

        List<Colonia> colonias = controller.resolverNombres(List.of("Mirasierra", "Colonia Inexistente"));
        assert colonias.size() == 2 : "No debe perderse ningún nombre";
        assert "25016".equals(colonias.get(0).getCodigoPostal()) : "Un nombre del catálogo recupera su código postal";
        assert "Colonia Inexistente".equals(colonias.get(1).getNombre()) : "Un nombre desconocido se conserva";
        assert !colonias.get(0).equals(colonias.get(1)) : "Las colonias recuperadas deben ser distintas";
        System.out.println("PASO (catalogo y nombres desconocidos)");
    }

    private static void testMockImplementaContrato() {
        System.out.print("[TEST 6] Verificando MockColoniaRepository contra IColoniaRepository... ");
        MockColoniaRepository repo = new MockColoniaRepository();
        int total = repo.listar().size();

        Colonia nueva = new Colonia(0, "Lomas de Lourdes", "25090", "Saltillo", "Coahuila de Zaragoza");
        assert repo.guardar(nueva) && nueva.getIdColonia() > 0 : "Guardar debe asignar un ID";
        assert repo.buscarPorId(nueva.getIdColonia()).isPresent() : "Debe encontrarse por ID";
        assert repo.listar().size() == total + 1 : "Debe agregarse a la lista";
        assert repo.eliminar(nueva.getIdColonia()) && repo.buscarPorId(nueva.getIdColonia()).isEmpty()
                : "Debe poder eliminarse";
        System.out.println("PASO (guardar, buscar por ID y eliminar)");
    }

    private static Colonia copiar(Colonia c) {
        return new Colonia(c.getIdColonia(), c.getNombre(), c.getCodigoPostal(), c.getMunicipio(), c.getEstado());
    }

    private static ColoniaSelectorController crear(DialogDePrueba dialog) {
        ColoniaSelectorController controller = new ColoniaSelectorController(dialog, CATALOGO);
        dialog.controller = controller;
        return controller;
    }

    private static List<String> nombres(List<Colonia> colonias) {
        return colonias.stream().map(Colonia::getNombre).toList();
    }
}
