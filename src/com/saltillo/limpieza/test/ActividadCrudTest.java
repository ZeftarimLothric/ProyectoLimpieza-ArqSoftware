package com.saltillo.limpieza.test;

import com.saltillo.limpieza.controllers.ActividadController;
import com.saltillo.limpieza.controllers.ColoniaSelectorController;
import com.saltillo.limpieza.models.dto.ActividadDetalleDTO;
import com.saltillo.limpieza.models.dto.ColoniaDTO;
import com.saltillo.limpieza.repositories.IActividadRepository;
import com.saltillo.limpieza.repositories.IEvidenciaRepository;
import com.saltillo.limpieza.repositories.MockActividadRepository;
import com.saltillo.limpieza.repositories.MockColoniaRepository;
import com.saltillo.limpieza.repositories.MockDashboardRepository;
import com.saltillo.limpieza.repositories.MockEvidenciaRepository;
import com.saltillo.limpieza.services.EvidenciaStorageService;
import com.saltillo.limpieza.views.actividades.ActividadFormView;
import com.saltillo.limpieza.views.actividades.ActividadListView;
import com.saltillo.limpieza.views.actividades.ColoniaSelectorDialog;

import javax.imageio.ImageIO;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Pruebas del CRUD de actividades (Integrante 3 - Issues #10 y #11).
 * Trabaja en una carpeta temporal que se borra al terminar.
 */
public class ActividadCrudTest {

    private static final String CUADRILLA = "Cuadrilla Saltillo 2000";
    private static final String TIPO = "Lavado de Espacios Públicos";
    private static final MockColoniaRepository CATALOGO = new MockColoniaRepository();

    /** Simula al usuario en el selector: agrega las colonias indicadas y acepta. */
    private static class SelectorDePrueba extends ColoniaSelectorDialog {
        private static final long serialVersionUID = 1L;
        private ColoniaSelectorController controller;
        private List<ColoniaDTO> aElegir = List.of();

        @Override
        public void abrir(Component padre) {
            controller.agregar(aElegir);
            controller.aceptar();
        }

        @Override
        public void cerrar() {
        }

        @Override
        public void mostrarError(String mensaje) {
        }
    }

    /** Evita los diálogos modales para que la prueba no espere al usuario. */
    private static class ListaDePrueba extends ActividadListView {
        private static final long serialVersionUID = 1L;
        private boolean confirmar = true;

        @Override
        public boolean confirmarEliminacion(String mensaje) {
            return confirmar;
        }

        @Override
        public void mostrarError(String mensaje) {
            throw new AssertionError("Error inesperado en la lista: " + mensaje);
        }
    }

    private static class FormDePrueba extends ActividadFormView {
        private static final long serialVersionUID = 1L;
        private boolean abierto;
        private String ultimoError;

        @Override
        public void abrir(Component padre, String titulo) {
            abierto = true;
        }

        @Override
        public void cerrar() {
            abierto = false;
        }

        @Override
        public void mostrarError(String mensaje) {
            ultimoError = mensaje;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("===================================================================");
        System.out.println("VERIFICACION DEL CRUD DE ACTIVIDADES - INTEGRANTE 3 (ISSUE #10)");
        System.out.println("===================================================================");

        Path raiz = Files.createTempDirectory("actividades-test");
        try {
            testCrudCompleto(raiz);
            testValidaciones(raiz);
            testFiltroPorEstado();
            testEvidenciasDeLaActividad(raiz);
        } finally {
            borrarCarpeta(raiz);
        }

        System.out.println("\n>>> TODAS LAS PRUEBAS DEL CRUD DE ACTIVIDADES PASARON CON EXITO <<<");
    }

    private static void testCrudCompleto(Path raiz) {
        System.out.print("[TEST 1] Verificando crear, editar y eliminar... ");
        IActividadRepository repo = new MockActividadRepository();
        ListaDePrueba lista = new ListaDePrueba();
        FormDePrueba form = new FormDePrueba();
        SelectorDePrueba selector = new SelectorDePrueba();
        ActividadController controller = crearController(lista, form, repo, new MockEvidenciaRepository(), raiz, selector);
        int totalInicial = repo.listar(null).size();

        controller.nuevaActividad();
        assert form.abierto : "El formulario debe abrirse";
        form.mostrarDatos(CUADRILLA, TIPO, "  Parque Las Maravillas  ", "15/11/2026 08:30", "", "PROGRAMADA", "Lavado general");
        selector.aElegir = CATALOGO.buscarPorNombreOCodigoPostal("Centro");
        controller.elegirColonias();
        controller.guardar();
        assert !form.abierto : "El formulario debe cerrarse al guardar";
        assert repo.listar(null).size() == totalInicial + 1 : "Debe agregarse una actividad";

        int id = ultimaActividad(repo).getIdActividad();
        ActividadDetalleDTO creada = repo.buscarPorId(id);
        assert id > 0 : "Debe asignarse un ID";
        assert creada.getIdCuadrilla() == 6 : "Debe resolverse el ID de la cuadrilla";
        assert "Parque Las Maravillas".equals(creada.getLugarEspecifico()) : "El lugar debe guardarse sin espacios sobrantes";
        assert creada.getFechaRealizacion() == null : "La fecha de realización es opcional";
        assert creada.getColonias().equals(List.of("Centro Histórico")) : "Deben guardarse las colonias elegidas";

        controller.editarActividad(id);
        form.mostrarDatos(CUADRILLA, TIPO, "Parque Nuevo", "15/11/2026 08:30", "15/11/2026 11:00", "COMPLETADA", "Terminado");
        controller.guardar();
        ActividadDetalleDTO editada = repo.buscarPorId(id);
        assert "COMPLETADA".equals(editada.getEstado()) : "El estado debe actualizarse";
        assert "15/11/2026 11:00".equals(editada.getFechaRealizacionFormateada()) : "La fecha de realización debe actualizarse";
        assert editada.getColonias().equals(List.of("Centro Histórico")) : "Editar debe conservar las colonias";
        assert repo.listar(null).size() == totalInicial + 1 : "Editar no debe crear otra actividad";

        lista.confirmar = false;
        controller.eliminarActividad(id);
        assert repo.buscarPorId(id) != null : "Sin confirmar no se elimina";
        lista.confirmar = true;
        controller.eliminarActividad(id);
        assert repo.buscarPorId(id) == null : "La actividad debe eliminarse";

        System.out.println("PASO (alta, edicion y baja con confirmacion)");
    }

    private static void testValidaciones(Path raiz) {
        System.out.print("[TEST 2] Verificando validaciones del formulario... ");
        IActividadRepository repo = new MockActividadRepository();
        FormDePrueba form = new FormDePrueba();
        SelectorDePrueba selector = new SelectorDePrueba();
        ActividadController controller = crearController(new ListaDePrueba(), form, repo, new MockEvidenciaRepository(), raiz, selector);
        int total = repo.listar(null).size();

        controller.nuevaActividad();
        controller.guardar();
        assert form.ultimoError.contains("Selecciona una cuadrilla.") : "Debe exigir cuadrilla";
        assert form.ultimoError.contains("Selecciona un tipo de actividad.") : "Debe exigir tipo";
        assert form.ultimoError.contains("Escribe el lugar específico.") : "Debe exigir lugar";
        assert form.ultimoError.contains("Elige al menos una colonia atendida.") : "Debe exigir colonia";
        assert form.ultimoError.contains("Escribe la fecha programada.") : "Debe exigir fecha";
        assert form.abierto : "Con errores el formulario sigue abierto";

        form.mostrarDatos(CUADRILLA, TIPO, "Lugar", "31/02/2026 08:00", "", "PROGRAMADA", "");
        controller.guardar();
        assert form.ultimoError.contains("La fecha programada debe tener el formato") : "Debe rechazar fechas inexistentes";

        selector.aElegir = CATALOGO.buscarPorNombreOCodigoPostal("Centro");
        controller.elegirColonias();
        form.mostrarDatos(CUADRILLA, TIPO, "Lugar", "15/11/2026 08:00", "", "COMPLETADA", "");
        controller.guardar();
        assert form.ultimoError.equals("Una actividad completada necesita fecha de realización.") : "Completada exige realización";

        controller.quitarColonias(CATALOGO.buscarPorNombreOCodigoPostal("Centro"));
        form.mostrarDatos(CUADRILLA, TIPO, "Lugar", "15/11/2026 08:00", "", "PROGRAMADA", "");
        controller.guardar();
        assert form.ultimoError.equals("Elige al menos una colonia atendida.") : "Quitar la única colonia debe impedir guardar";

        assert repo.listar(null).size() == total : "Nada inválido debe guardarse";
        System.out.println("PASO (obligatorios, colonias, fechas y estado completada)");
    }

    private static void testFiltroPorEstado() {
        System.out.print("[TEST 3] Verificando filtro por estado... ");
        IActividadRepository repo = new MockActividadRepository();
        List<ActividadDetalleDTO> programadas = repo.listar("PROGRAMADA");
        assert !programadas.isEmpty() : "Deben existir actividades programadas";
        for (ActividadDetalleDTO a : programadas) {
            assert "PROGRAMADA".equals(a.getEstado()) : "Solo deben llegar programadas";
        }
        assert repo.listar(null).size() > programadas.size() : "Sin filtro deben llegar todas";
        System.out.println("PASO (" + programadas.size() + " programadas)");
    }

    private static void testEvidenciasDeLaActividad(Path raiz) throws Exception {
        System.out.print("[TEST 4] Verificando evidencias al guardar, cancelar y eliminar... ");
        Path carpeta = raiz.resolve("evidencias");
        EvidenciaStorageService servicio = new EvidenciaStorageService(carpeta);
        IActividadRepository repo = new MockActividadRepository();
        IEvidenciaRepository evidencias = new MockEvidenciaRepository();
        FormDePrueba form = new FormDePrueba();
        ActividadController controller = new ActividadController(new ListaDePrueba(), form, repo, evidencias,
                new MockDashboardRepository(), servicio, conectar(new SelectorDePrueba()));
        String nombre = guardarImagen(servicio, raiz);

        int id = repo.listar(null).get(0).getIdActividad();
        evidencias.reemplazar(id, List.of(nombre));

        controller.editarActividad(id);
        controller.cancelar();
        assert servicio.existe(nombre) : "Cancelar no debe borrar evidencias ya guardadas";

        controller.eliminarActividad(id);
        assert !servicio.existe(nombre) : "Eliminar la actividad debe borrar sus imágenes";
        assert evidencias.listarPorActividad(id).isEmpty() : "Eliminar la actividad debe borrar sus referencias";

        System.out.println("PASO (referencias y archivos consistentes)");
    }

    private static ActividadController crearController(ActividadListView lista, ActividadFormView form, IActividadRepository repo,
                                                       IEvidenciaRepository evidencias, Path raiz, SelectorDePrueba selector) {
        return new ActividadController(lista, form, repo, evidencias, new MockDashboardRepository(),
                new EvidenciaStorageService(raiz.resolve("evidencias")), conectar(selector));
    }

    private static ColoniaSelectorController conectar(SelectorDePrueba selector) {
        ColoniaSelectorController controller = new ColoniaSelectorController(selector, CATALOGO::buscarPorNombreOCodigoPostal);
        selector.controller = controller;
        return controller;
    }

    private static ActividadDetalleDTO ultimaActividad(IActividadRepository repo) {
        List<ActividadDetalleDTO> todas = repo.listar(null);
        return todas.get(todas.size() - 1);
    }

    private static String guardarImagen(EvidenciaStorageService servicio, Path carpeta) throws Exception {
        Path archivo = carpeta.resolve("real.png");
        ImageIO.write(new BufferedImage(40, 30, BufferedImage.TYPE_INT_RGB), "png", archivo.toFile());
        return servicio.guardar(archivo);
    }

    private static void borrarCarpeta(Path carpeta) throws IOException {
        try (Stream<Path> rutas = Files.walk(carpeta)) {
            for (Path ruta : rutas.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(ruta);
            }
        }
    }
}
