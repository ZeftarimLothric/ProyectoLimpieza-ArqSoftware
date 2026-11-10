package com.saltillo.limpieza.test;

import com.saltillo.limpieza.database.ConexionBD;
import com.saltillo.limpieza.models.entities.Usuario;
import com.saltillo.limpieza.repositories.SqlUsuarioRepository;
import com.saltillo.limpieza.security.PasswordHasher;

import java.util.Scanner;

/**
 * Herramienta de consola (Issue #7) para dar de alta un administrador desde Java,
 * sin escribir el hash a mano: pide los datos, hashea con SHA-256 y guarda con
 * SqlUsuarioRepository.guardar(). Ejecutar con Run File y capturar los datos en la consola.
 *
 * La app no tiene pantalla de registro a propósito: es un sistema interno, y que cualquiera
 * pudiera crear administradores desde el login sería un hueco de seguridad.
 */
public class CrearUsuario {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Usuario (login): ");
        String username = in.nextLine().trim();
        System.out.print("Nombre completo: ");
        String nombre = in.nextLine().trim();
        System.out.print("Contraseña (mínimo 6 caracteres): ");
        String contrasena = in.nextLine();

        if (username.isEmpty() || nombre.isEmpty() || contrasena.length() < 6) {
            System.out.println("Datos incompletos o contraseña demasiado corta. No se creó nada.");
            return;
        }

        try {
            SqlUsuarioRepository repo = new SqlUsuarioRepository();
            if (repo.buscarPorUsername(username).isPresent()) {
                System.out.println("Ya existe un usuario llamado '" + username + "'.");
                return;
            }
            Usuario nuevo = new Usuario(0, nombre, username, PasswordHasher.hash(contrasena), "ADMINISTRADOR", true);
            if (repo.guardar(nuevo)) {
                System.out.println("Usuario '" + username + "' creado con id " + nuevo.getIdUsuario() + ".");
            } else {
                System.out.println("No se pudo crear el usuario.");
            }
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage() + (e.getCause() != null ? " (" + e.getCause().getMessage() + ")" : ""));
        } finally {
            ConexionBD.getInstance().cerrar();
        }
    }
}
