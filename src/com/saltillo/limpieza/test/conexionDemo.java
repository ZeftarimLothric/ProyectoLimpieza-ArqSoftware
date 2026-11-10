package com.saltillo.limpieza.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.saltillo.limpieza.database.ConexionBD;

public class conexionDemo {
    public static void main(String[] args) {
        try {
            Connection con = ConexionBD.getInstance().getConexion();
            System.out.println("Conectado: " + (con != null && !con.isClosed()));
            System.out.println("Base de datos: " + con.getCatalog());

            try (Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM estados_actividad")) {
                if (rs.next()) {
                    System.out.println("Estados encontrados: " + rs.getInt(1));
                }
            }
        } catch (Exception e) {
            System.err.println("Error al conectar:");
            e.printStackTrace();
        } finally {
            ConexionBD.getInstance().cerrar();
        }
    }
}