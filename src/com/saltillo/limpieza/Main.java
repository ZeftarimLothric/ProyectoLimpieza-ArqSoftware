package com.saltillo.limpieza;

import com.saltillo.limpieza.controllers.DashboardController;
import com.saltillo.limpieza.controllers.MainNavigationController;
import com.saltillo.limpieza.repositories.IDashboardRepository;
import com.saltillo.limpieza.repositories.MockDashboardRepository;
import com.saltillo.limpieza.views.DashboardView;
import com.saltillo.limpieza.views.MainShellView;
import com.saltillo.limpieza.views.PlaceholderModuleView;

import javax.swing.*;

/**
 * Punto de entrada principal de la aplicación.
 * 
 * Actúa como Composition Root (Inyección de Dependencias manual y cableado
 * MVC).
 * 
 * =============================================================================
 * PUNTO DE INTEGRACIÓN CON EL EQUIPO:
 * 1. INTEGRANTE 1 & 2 (Base de Datos & DAOs):
 * Cuando terminen su conexión Singleton y DAO SQL, simplemente sustituyan
 * la línea:
 * IDashboardRepository dashboardRepo = new MockDashboardRepository();
 * por:
 * IDashboardRepository dashboardRepo = new SqlDashboardRepository();
 * 
 * 2. INTEGRANTE 3 (Registro de Actividades & Evidencias):
 * Cuando termine su JPanel 'RegistroActividadesView', sustituya la línea:
 * shellView.registrarModulo("ACTIVIDADES", new PlaceholderModuleView(...));
 * por:
 * shellView.registrarModulo("ACTIVIDADES", new RegistroActividadesView(...));
 * =============================================================================
 */
public class Main {

        public static void main(String[] args) {
                // Configuración de Look and Feel del sistema para una apariencia nativa moderna
                try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {
                        // Si falla, continúa con el Look and Feel por defecto de Java
                }

                SwingUtilities.invokeLater(() -> {
                        // 1. REPOSITORIO (Inversión de dependencias mediante contrato
                        // IDashboardRepository)
                        // =========================================================================
                        // AQUÍ SE SUSTITUIRÁ EL MOCK POR LA BD REAL
                        // =========================================================================
                        IDashboardRepository dashboardRepo = new MockDashboardRepository();

                        // 2. VISTAS
                        MainShellView shellView = new MainShellView();
                        DashboardView dashboardView = new DashboardView();

                        // Placeholders informativos y simuladores para los compañeros de equipo
                        PlaceholderModuleView moduloActividades = new PlaceholderModuleView(
                                        "ACTIVIDADES",
                                        "Módulo: Registro de Actividades & Evidencias",
                                        "Integrante 3 (Operaciones & Evidencias)",
                                        "Captura de actividades de limpieza, asignación de cuadrillas, " +
                                                        "asociación de colonias Sepomex y carga de evidencia fotográfica.",
                                        dashboardRepo);

                        PlaceholderModuleView moduloCuadrillas = new PlaceholderModuleView(
                                        "CUADRILLAS",
                                        "Módulo: Catálogo de Cuadrillas y Jefes",
                                        "Integrante 2 (Acceso a Datos & CRUDs)",
                                        "Mantenimiento de cuadrillas (CRUD), altas, bajas y cambios de jefes de cuadrilla, "
                                                        +
                                                        "asignación de turnos y personal operativo.",
                                        dashboardRepo);

                        PlaceholderModuleView moduloColonias = new PlaceholderModuleView(
                                        "COLONIAS",
                                        "Módulo: Catálogo de Colonias (Sepomex Saltillo)",
                                        "Integrante 1 (Base de Datos & Modelo Core)",
                                        "Carga masiva e indexación del catálogo oficial de códigos postales y colonias "
                                                        +
                                                        "del Municipio de Saltillo a partir de SEPOMEX.",
                                        dashboardRepo);

                        // 3. REGISTRO MODULAR EN EL CONTENT HOST DEL SHELL (Desacoplamiento total)
                        shellView.registrarModulo("DASHBOARD", dashboardView);
                        shellView.registrarModulo("ACTIVIDADES", moduloActividades);
                        shellView.registrarModulo("CUADRILLAS", moduloCuadrillas);
                        shellView.registrarModulo("COLONIAS", moduloColonias);

                        // 4. CONTROLADORES (Orquestación MVC y Patrón Observer)
                        DashboardController dashboardController = new DashboardController(dashboardView, dashboardRepo);
                        MainNavigationController navigationController = new MainNavigationController(shellView);

                        // 5. NAVEGAR A LA VISTA INICIAL (Dashboard Ejecutivo) Y MOSTRAR
                        navigationController.navegarA("DASHBOARD");
                        shellView.setVisible(true);

                        System.out.println("=========================================================");
                        System.out.println("SISTEMA DE GESTION DE LIMPIEZA URBANA - SALTILLO COAHUILA");
                        System.out.println("Módulo Integrante 4 (Shell, Dashboard & Observer) ACTIVO.");
                        System.out.println("=========================================================");
                });
        }
}
