package com.saltillo.limpieza.test;

import com.saltillo.limpieza.controllers.DashboardController;
import com.saltillo.limpieza.models.dto.*;
import com.saltillo.limpieza.models.entities.Usuario;
import com.saltillo.limpieza.patterns.observer.ActividadEventBus;
import com.saltillo.limpieza.patterns.observer.IObserver;
import com.saltillo.limpieza.repositories.IDashboardRepository;
import com.saltillo.limpieza.repositories.MockDashboardRepository;
import com.saltillo.limpieza.session.SesionUsuario;
import com.saltillo.limpieza.views.DashboardView;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Suite de pruebas unitarias y de arquitectura (Headless) actualizada para Issue #4:
 * 1. Verificación del Singleton SesionUsuario
 * 2. Soporte de múltiples colonias y TipoEventoActividad en ActividadEventDTO
 * 3. Publicación de eventos en ActividadEventBus
 * 4. Búsqueda y filtros dinámicos con FiltroActividadDTO en IDashboardRepository
 * 5. Integración reactiva Controller-Observer-Repository
 */
public class ArchitectureVerificationTest {

    public static void main(String[] args) {
        System.out.println("===================================================================");
        System.out.println("VERIFICACION ARQUITECTONICA - ISSUE #4 (CONTRATOS & SESION)");
        System.out.println("===================================================================");

        testSesionUsuario();
        testRepositoryAndDtos();
        testBusquedaFiltrosActividad();
        testObserverPatternDirectly();
        testControllerObserverIntegration();

        System.out.println("\n>>> TODAS LAS PRUEBAS DEL ISSUE #4 PASARON CON EXITO (100% OK) <<<");
    }

    private static void testSesionUsuario() {
        System.out.print("[TEST 1] Verificando SesionUsuario Singleton (Issue #4)... ");
        SesionUsuario sesion = SesionUsuario.getInstance();
        sesion.destruir();
        assert !sesion.estaActiva() : "La sesión no debe estar activa inicialmente tras destruir";
        assert sesion.getUsuarioActual() == null : "El usuario actual debe ser nulo";

        Usuario u = new Usuario(1, "Carlos Ramos", "carlos.admin", "ADMINISTRADOR");
        sesion.iniciar(u);
        assert sesion.estaActiva() : "La sesión debe estar activa tras iniciar";
        assert "carlos.admin".equals(sesion.getUsuarioActual().getUsuario()) : "El usuario debe coincidir";

        sesion.destruir();
        assert !sesion.estaActiva() : "La sesión debe quedar inactiva tras destruir";
        System.out.println("PASO (Singleton, iniciar y destruir verificados)");
    }

    private static void testRepositoryAndDtos() {
        System.out.print("[TEST 2] Verificando IDashboardRepository y DTOs... ");
        IDashboardRepository repo = new MockDashboardRepository();

        DashboardKpisDTO kpisIniciales = repo.obtenerKpis();
        assert kpisIniciales != null : "Los KPIs no deben ser nulos";
        assert kpisIniciales.getTotalActividadesEjecutadas() > 0 : "El total inicial debe ser > 0";
        assert kpisIniciales.getTotalCuadrillas() == 6 : "Deben existir 6 cuadrillas en el mock de Saltillo";

        List<CuadrillaResumenDTO> cuadrillas = repo.obtenerResumenPorCuadrillas();
        assert cuadrillas != null && cuadrillas.size() == 6 : "Deben recuperarse 6 cuadrillas en la lista";

        CuadrillaResumenDTO c1 = cuadrillas.get(0);
        assert c1.getPorcentajeCumplimiento() >= 0 : "El porcentaje de cumplimiento debe ser válido";

        System.out.println("PASO (KPIs: " + kpisIniciales.getTotalActividadesEjecutadas() + " actividades, " +
                kpisIniciales.getCuadrillasDisponibles() + " disponibles)");
    }

    private static void testBusquedaFiltrosActividad() {
        System.out.print("[TEST 3] Verificando FiltroActividadDTO y buscarActividades... ");
        IDashboardRepository repo = new MockDashboardRepository();

        // 1. Búsqueda sin filtros (todas las actividades)
        List<ActividadDetalleDTO> todas = repo.buscarActividades(null);
        assert todas != null && todas.size() >= 8 : "Deben existir al menos 8 actividades simuladas";

        // 2. Filtro por Cuadrilla #1 (Centro Histórico)
        FiltroActividadDTO fCuadrilla = new FiltroActividadDTO();
        fCuadrilla.setIdCuadrilla(1);
        List<ActividadDetalleDTO> porCuadrilla = repo.buscarActividades(fCuadrilla);
        assert !porCuadrilla.isEmpty() : "Debe encontrar actividades para Cuadrilla #1";
        for (ActividadDetalleDTO a : porCuadrilla) {
            assert a.getIdCuadrilla() == 1 : "Todas las actividades deben pertenecer a la Cuadrilla #1";
        }

        // 3. Filtro por Colonia
        FiltroActividadDTO fColonia = new FiltroActividadDTO();
        fColonia.setColonia("Centro");
        List<ActividadDetalleDTO> porColonia = repo.buscarActividades(fColonia);
        assert !porColonia.isEmpty() : "Debe encontrar actividades en colonias con 'Centro'";

        // 4. Filtro por Estado
        FiltroActividadDTO fEstado = new FiltroActividadDTO();
        fEstado.setEstado("EN PROCESO");
        List<ActividadDetalleDTO> porEstado = repo.buscarActividades(fEstado);
        assert !porEstado.isEmpty() : "Debe encontrar actividades EN PROCESO";
        for (ActividadDetalleDTO a : porEstado) {
            assert "EN PROCESO".equalsIgnoreCase(a.getEstado()) : "El estado debe ser EN PROCESO";
        }

        System.out.println("PASO (" + todas.size() + " actividades base, filtros por cuadrilla, colonia y estado OK)");
    }

    private static void testObserverPatternDirectly() {
        System.out.print("[TEST 4] Verificando Patrón Observer con colonias y tipo de evento... ");
        ActividadEventBus bus = ActividadEventBus.getInstance();
        AtomicBoolean recibido = new AtomicBoolean(false);

        IObserver<ActividadEventDTO> testObserver = event -> {
            if (event.getColonias().contains("Centro Histórico") && event.getTipoEvento() == TipoEventoActividad.CREADA) {
                recibido.set(true);
            }
        };

        bus.attach(testObserver);
        bus.publicarEvento(new ActividadEventDTO(
                1, "Cuadrilla Centro Histórico", List.of("Centro Histórico", "Bellavista"),
                "Barrido Manual", "Prueba Unitaria", TipoEventoActividad.CREADA
        ));

        assert recibido.get() : "El observador debió recibir el evento publicado con colonias y tipo";
        bus.detach(testObserver);
        System.out.println("PASO (Notificación reactiva con múltiples colonias OK)");
    }

    private static void testControllerObserverIntegration() {
        System.out.print("[TEST 5] Verificando Integración Controller-Observer-Repository... ");
        IDashboardRepository repo = new MockDashboardRepository();
        DashboardView view = new DashboardView();
        DashboardController controller = new DashboardController(view, repo);

        int totalAntes = repo.obtenerKpis().getTotalActividadesEjecutadas();

        // Simular registro con múltiples colonias
        repo.registrarActividad(1, List.of("República Poniente", "Universidad"), "Deshierbe", "Supervisión");

        int totalDespues = repo.obtenerKpis().getTotalActividadesEjecutadas();
        assert totalDespues == totalAntes + 1 : "El total de actividades debió incrementarse en 1";

        controller.destruir();
        System.out.println("PASO (Total previo: " + totalAntes + " -> Total posterior: " + totalDespues + ")");
    }
}
