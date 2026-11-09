package com.saltillo.limpieza.services;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

/**
 * Servicio de almacenamiento de imágenes de evidencia (Integrante 3 - Issue #12).
 *
 * Valida las imágenes y las guarda en una carpeta local. A la base de datos solo
 * debe llegar el nombre del archivo que devuelve guardar(Path).
 */
public class EvidenciaStorageService {

    private static final String CARPETA_POR_DEFECTO = "evidencias";
    private static final long TAMANO_MAXIMO_BYTES = 5L * 1024 * 1024;
    private static final String EXTENSION_JPG = "jpg";
    private static final String EXTENSION_PNG = "png";

    private static final byte[] FIRMA_JPG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] FIRMA_PNG = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

    private final Path carpeta;

    public EvidenciaStorageService() {
        this(Path.of(CARPETA_POR_DEFECTO));
    }

    public EvidenciaStorageService(Path carpeta) {
        this.carpeta = carpeta.toAbsolutePath().normalize();
    }

    /**
     * Comprueba extensión, tamaño y que los primeros bytes correspondan a la extensión.
     */
    public void validar(Path archivo) throws EvidenciaInvalidaException {
        if (archivo == null || !Files.isRegularFile(archivo)) {
            throw new EvidenciaInvalidaException("El archivo seleccionado no existe.");
        }

        String extension = obtenerExtension(archivo);
        if (!EXTENSION_JPG.equals(extension) && !EXTENSION_PNG.equals(extension)) {
            throw new EvidenciaInvalidaException("Solo se permiten imágenes JPG, JPEG o PNG.");
        }

        try {
            long tamano = Files.size(archivo);
            if (tamano == 0) {
                throw new EvidenciaInvalidaException("La imagen está vacía.");
            }
            if (tamano > TAMANO_MAXIMO_BYTES) {
                throw new EvidenciaInvalidaException("La imagen supera el tamaño máximo de 5 MB.");
            }
            if (!tieneFirmaEsperada(archivo, extension)) {
                throw new EvidenciaInvalidaException("El contenido del archivo no corresponde a una imagen JPG o PNG válida.");
            }
        } catch (IOException ex) {
            throw new EvidenciaInvalidaException("No se pudo leer el archivo seleccionado.", ex);
        }
    }

    /**
     * Valida y copia la imagen a la carpeta de evidencias.
     *
     * @return únicamente el nombre generado (UUID + extensión)
     */
    public String guardar(Path archivo) throws EvidenciaInvalidaException {
        validar(archivo);

        String nombre = UUID.randomUUID() + "." + obtenerExtension(archivo);
        try {
            Files.createDirectories(carpeta);
            Files.copy(archivo, carpeta.resolve(nombre));
        } catch (IOException ex) {
            throw new EvidenciaInvalidaException("No se pudo guardar la imagen. Intenta de nuevo.", ex);
        }
        return nombre;
    }

    /**
     * Convierte un nombre en su ruta, rechazando cualquiera que salga de la carpeta.
     */
    public Path resolver(String nombre) throws EvidenciaInvalidaException {
        if (nombre == null || nombre.isBlank()) {
            throw new EvidenciaInvalidaException("El nombre de la evidencia no es válido.");
        }

        try {
            Path destino = carpeta.resolve(nombre).normalize();
            if (!carpeta.equals(destino.getParent())) {
                throw new EvidenciaInvalidaException("El nombre de la evidencia no es válido.");
            }
            return destino;
        } catch (InvalidPathException ex) {
            throw new EvidenciaInvalidaException("El nombre de la evidencia no es válido.", ex);
        }
    }

    public boolean existe(String nombre) {
        try {
            return Files.isRegularFile(resolver(nombre));
        } catch (EvidenciaInvalidaException ex) {
            return false;
        }
    }

    /**
     * @return true si el archivo existía y fue borrado
     */
    public boolean eliminar(String nombre) throws EvidenciaInvalidaException {
        try {
            return Files.deleteIfExists(resolver(nombre));
        } catch (IOException ex) {
            throw new EvidenciaInvalidaException("No se pudo eliminar la imagen.", ex);
        }
    }

    /**
     * Carga la imagen para que el controlador la entregue a la vista, que no accede a archivos.
     */
    public BufferedImage leerImagen(String nombre) throws EvidenciaInvalidaException {
        try {
            BufferedImage imagen = ImageIO.read(resolver(nombre).toFile());
            if (imagen == null) {
                throw new EvidenciaInvalidaException("La imagen está dañada y no se puede mostrar.");
            }
            return imagen;
        } catch (IOException ex) {
            throw new EvidenciaInvalidaException("No se pudo abrir la imagen.", ex);
        }
    }

    private String obtenerExtension(Path archivo) {
        String nombre = archivo.getFileName().toString().toLowerCase(Locale.ROOT);
        int punto = nombre.lastIndexOf('.');
        String extension = punto >= 0 ? nombre.substring(punto + 1) : "";
        return "jpeg".equals(extension) ? EXTENSION_JPG : extension;
    }

    private boolean tieneFirmaEsperada(Path archivo, String extension) throws IOException {
        byte[] firma = EXTENSION_PNG.equals(extension) ? FIRMA_PNG : FIRMA_JPG;
        try (InputStream entrada = Files.newInputStream(archivo)) {
            return Arrays.equals(firma, entrada.readNBytes(firma.length));
        }
    }
}
