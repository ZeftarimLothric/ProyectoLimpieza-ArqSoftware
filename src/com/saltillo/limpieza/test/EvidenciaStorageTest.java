package com.saltillo.limpieza.test;

import com.saltillo.limpieza.controllers.EvidenciaController;
import com.saltillo.limpieza.services.EvidenciaInvalidaException;
import com.saltillo.limpieza.services.EvidenciaStorageService;
import com.saltillo.limpieza.views.actividades.EvidenciaPanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Pruebas del almacenamiento de evidencias y su controlador (Integrante 3 - Issue #12).
 * Trabaja en una carpeta temporal que se borra al terminar.
 */
public class EvidenciaStorageTest {

    private static final int CINCO_MB = 5 * 1024 * 1024;
    private static final byte[] FIRMA_JPG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
    private static final byte[] FIRMA_PNG = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

    /** Sustituye el cuadro de diálogo para que la prueba no se detenga esperando al usuario. */
    private static class PanelDePrueba extends EvidenciaPanel {
        private static final long serialVersionUID = 1L;
        private String ultimoError;

        @Override
        public void mostrarError(String mensaje) {
            ultimoError = mensaje;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("===================================================================");
        System.out.println("VERIFICACION DE EVIDENCIAS - INTEGRANTE 3 (ISSUE #12)");
        System.out.println("===================================================================");

        Path raiz = Files.createTempDirectory("evidencias-test");
        try {
            Path origen = Files.createDirectory(raiz.resolve("origen"));
            Path carpeta = raiz.resolve("evidencias");
            EvidenciaStorageService servicio = new EvidenciaStorageService(carpeta);

            testImagenesValidas(servicio, origen, carpeta);
            testRechazos(servicio, origen);
            testRutaSegura(servicio, raiz, carpeta);
            testCancelarYConfirmar(servicio, origen);
        } finally {
            borrarCarpeta(raiz);
        }

        System.out.println("\n>>> TODAS LAS PRUEBAS DE EVIDENCIAS PASARON CON EXITO <<<");
    }

    private static void testImagenesValidas(EvidenciaStorageService servicio, Path origen, Path carpeta) throws Exception {
        System.out.print("[TEST 1] Verificando imagenes validas y nombre generado... ");

        String nombreJpg = servicio.guardar(crearArchivo(origen, "foto.jpg", FIRMA_JPG, 100));
        assert nombreJpg.matches("[0-9a-f-]{36}\\.jpg") : "Debe devolver solo UUID + .jpg: " + nombreJpg;
        assert servicio.existe(nombreJpg) : "La imagen debe quedar en la carpeta";

        String nombreJpeg = servicio.guardar(crearArchivo(origen, "foto.jpeg", FIRMA_JPG, 100));
        assert nombreJpeg.endsWith(".jpg") : ".jpeg debe guardarse como .jpg";

        Path png = crearArchivo(origen, "FOTO.PNG", FIRMA_PNG, 100);
        String nombrePng = servicio.guardar(png);
        assert nombrePng.endsWith(".png") : "La extensión debe normalizarse a minúsculas";
        assert Files.mismatch(png, carpeta.resolve(nombrePng)) == -1 : "La copia debe ser idéntica al original";

        servicio.validar(crearArchivo(origen, "limite.png", FIRMA_PNG, CINCO_MB));

        System.out.println("PASO (JPG, JPEG, PNG y limite exacto de 5 MB)");
    }

    private static void testRechazos(EvidenciaStorageService servicio, Path origen) throws Exception {
        System.out.print("[TEST 2] Verificando cada rechazo con su mensaje... ");

        esperarRechazo(servicio, origen.resolve("no-existe.png"), "El archivo seleccionado no existe.");
        esperarRechazo(servicio, null, "El archivo seleccionado no existe.");
        esperarRechazo(servicio, crearArchivo(origen, "foto.gif", FIRMA_PNG, 100),
                "Solo se permiten imágenes JPG, JPEG o PNG.");
        esperarRechazo(servicio, crearArchivo(origen, "sin-extension", FIRMA_PNG, 100),
                "Solo se permiten imágenes JPG, JPEG o PNG.");
        esperarRechazo(servicio, crearArchivo(origen, "vacia.png", new byte[0], 0), "La imagen está vacía.");
        esperarRechazo(servicio, crearArchivo(origen, "grande.png", FIRMA_PNG, CINCO_MB + 1),
                "La imagen supera el tamaño máximo de 5 MB.");

        String mensajeFirma = "El contenido del archivo no corresponde a una imagen JPG o PNG válida.";
        esperarRechazo(servicio, crearArchivo(origen, "texto.png", "no soy imagen".getBytes(), 100), mensajeFirma);
        esperarRechazo(servicio, crearArchivo(origen, "cruzada.jpg", FIRMA_PNG, 100), mensajeFirma);
        esperarRechazo(servicio, crearArchivo(origen, "corta.png", new byte[]{(byte) 0x89}, 1), mensajeFirma);

        System.out.println("PASO (no existe, formato, vacia, tamano y firma)");
    }

    private static void testRutaSegura(EvidenciaStorageService servicio, Path raiz, Path carpeta) throws Exception {
        System.out.print("[TEST 3] Verificando que no se pueda salir de la carpeta... ");

        Path externo = crearArchivo(raiz, "externo.png", FIRMA_PNG, 100);
        for (String nombre : Arrays.asList("../externo.png", "..\\externo.png", "..", ".", "sub/a.png",
                externo.toString(), "", "   ", null)) {
            esperarNombreInvalido(servicio, nombre);
            assert !servicio.existe(nombre) : "existe() debe ser false para: " + nombre;
        }
        assert Files.exists(externo) : "El archivo externo no debe verse afectado";

        assert carpeta.toAbsolutePath().normalize().resolve("a.png").equals(servicio.resolver("a.png"))
                : "Un nombre simple debe resolverse dentro de la carpeta";
        assert !servicio.eliminar("no-existe.png") : "Eliminar algo inexistente debe devolver false";

        System.out.println("PASO (rutas relativas, absolutas y vacias bloqueadas)");
    }

    private static void testCancelarYConfirmar(EvidenciaStorageService servicio, Path origen) throws Exception {
        System.out.print("[TEST 4] Verificando cancelar y confirmar del controlador... ");

        Path imagen = crearImagenReal(origen);
        PanelDePrueba panel = new PanelDePrueba();
        EvidenciaController controller = new EvidenciaController(panel, servicio);

        // Cancelar borra lo nuevo
        controller.cargar(List.of());
        controller.agregarArchivo(imagen);
        String nueva = controller.getEvidencias().get(0);
        assert servicio.existe(nueva) : "La imagen nueva debe guardarse al agregarla";
        controller.descartarCambios();
        assert controller.getEvidencias().isEmpty() : "Cancelar debe vaciar la lista";
        assert !servicio.existe(nueva) : "Cancelar debe borrar la imagen nueva";

        // Quitar una original se puede deshacer hasta confirmar
        String original = servicio.guardar(imagen);
        controller.cargar(List.of(original));
        controller.quitar(original);
        assert controller.getEvidencias().isEmpty() : "La imagen debe salir de la lista";
        assert servicio.existe(original) : "La original no se borra antes de confirmar";
        controller.descartarCambios();
        assert controller.getEvidencias().equals(List.of(original)) : "Cancelar debe restaurar la original";
        assert servicio.existe(original) : "Cancelar no debe borrar la original";

        // Quitar una nueva la borra de inmediato
        controller.agregarArchivo(imagen);
        String temporal = controller.getEvidencias().get(1);
        controller.quitar(temporal);
        assert !servicio.existe(temporal) : "Una imagen nueva quitada no debe quedar huerfana";

        // Confirmar aplica los cambios
        controller.quitar(original);
        controller.agregarArchivo(imagen);
        String conservada = controller.getEvidencias().get(0);
        controller.confirmarCambios();
        assert !servicio.existe(original) : "Confirmar debe borrar la imagen quitada";
        assert servicio.existe(conservada) : "Confirmar debe conservar la imagen nueva";

        // Tras confirmar, cancelar ya no borra lo confirmado
        controller.descartarCambios();
        assert servicio.existe(conservada) : "Lo confirmado ya es parte de los originales";

        // Un archivo inválido llega a la vista como error
        assert panel.ultimoError == null : "No debio haber errores hasta aqui: " + panel.ultimoError;
        controller.agregarArchivo(crearArchivo(origen, "mala.gif", FIRMA_PNG, 100));
        assert "Solo se permiten imágenes JPG, JPEG o PNG.".equals(panel.ultimoError) : "El panel debe mostrar el rechazo";
        assert controller.getEvidencias().equals(List.of(conservada)) : "Un rechazo no altera la lista";

        System.out.println("PASO (cancelar, confirmar y errores hacia la vista)");
    }

    private static void esperarRechazo(EvidenciaStorageService servicio, Path archivo, String mensaje) {
        try {
            servicio.validar(archivo);
            assert false : "Debió rechazarse: " + archivo;
        } catch (EvidenciaInvalidaException ex) {
            assert mensaje.equals(ex.getMessage()) : "Mensaje inesperado: " + ex.getMessage();
        }
    }

    private static void esperarNombreInvalido(EvidenciaStorageService servicio, String nombre) {
        try {
            servicio.resolver(nombre);
            assert false : "Debió bloquearse el nombre: " + nombre;
        } catch (EvidenciaInvalidaException ex) {
            assert "El nombre de la evidencia no es válido.".equals(ex.getMessage()) : "Mensaje inesperado: " + ex.getMessage();
        }
    }

    private static Path crearArchivo(Path carpeta, String nombre, byte[] inicio, int tamano) throws IOException {
        byte[] contenido = Arrays.copyOf(inicio, Math.max(inicio.length, tamano));
        return Files.write(carpeta.resolve(nombre), contenido);
    }

    private static Path crearImagenReal(Path carpeta) throws IOException {
        Path archivo = carpeta.resolve("real.png");
        ImageIO.write(new BufferedImage(40, 30, BufferedImage.TYPE_INT_RGB), "png", archivo.toFile());
        return archivo;
    }

    private static void borrarCarpeta(Path carpeta) throws IOException {
        try (Stream<Path> rutas = Files.walk(carpeta)) {
            for (Path ruta : rutas.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(ruta);
            }
        }
    }
}
