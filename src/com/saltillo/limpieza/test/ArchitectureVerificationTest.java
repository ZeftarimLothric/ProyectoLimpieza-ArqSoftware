package com.saltillo.limpieza.test;

import com.saltillo.limpieza.controllers.DashboardController;
import com.saltillo.limpieza.models.dto.ActividadEventDTO;
import com.saltillo.limpieza.models.dto.CuadrillaResumenDTO;
import com.saltillo.limpieza.models.dto.DashboardKpisDTO;
import com.saltillo.limpieza.patterns.observer.ActividadEventBus;
import com.saltillo.limpieza.patterns.observer.IObserver;
import com.saltillo.limpieza.repositories.IDashboardRepository;
import com.saltillo.limpieza.repositories.MockDashboardRepository;
import com.saltillo.limpieza.views.DashboardView;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Suite de pruebas unitarias y de arquitectura (Headless) para verificar:
 * 1. Desacoplamiento de contratos (IDashboardRepository)
 * 2. Integridad de los DTOs
 * 3. Funcionamiento reactivo del Patrón Observer (ActividadEventBus)
 * 4. Actualización en tiempo real de métricas ejecutivas
 */
public class ArchitectureVerificationTest {

    public static void main(String[] args) {
        System.out.println("===================================================================");
        System.out.println("VERIFICACION ARQUITECTONICA - INTEGRANTE 4 (DASHBOARD & OBSERVER)");
        System.out.println("===================================================================");

        testRepositoryAndDtos();
        testObserverPatternDirectly();
        testControllerObserverIntegration();

        System.out.println("\n>>> TODAS LAS PRUEBAS ARQUITECTONICAS PASARON CON EXITO (100% OK) <<<");
    }

    private static void testRepositoryAndDtos() {
        System.out.print("[TEST 1] Verificando IDashboardRepository y DTOs... ");
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

    private static void testObserverPatternDirectly() {
        System.out.print("[TEST 2] Verificando Patrón Observer (ActividadEventBus)... ");
        ActividadEventBus bus = ActividadEventBus.getInstance();
        AtomicBoolean recibido = new AtomicBoolean(false);

        IObserver<ActividadEventDTO> testObserver = event -> {
            if ("Centro Histórico".equals(event.getColonia())) {
                recibido.set(true);
            }
        };

        bus.attach(testObserver);
        bus.publicarNuevaActividad(new ActividadEventDTO(
                1, "Cuadrilla Centro Histórico", "Centro Histórico", "Barrido Manual", "Prueba Unitaria"
        ));

        assert recibido.get() : "El observador debió recibir el evento publicado";
        bus.detach(testObserver);
        System.out.println("PASO (Notificación recibida reactivamente)");
    }

    private static void testControllerObserverIntegration() {
        System.out.print("[TEST 3] Verificando Integración Controller-Observer-Repository... ");
        IDashboardRepository repo = new MockDashboardRepository();
        DashboardView view = new DashboardView();
        DashboardController controller = new DashboardController(view, repo);

        int totalAntes = repo.obtenerKpis().getTotalActividadesEjecutadas();

        // Simular registro de actividad en Cuadrilla #1
        repo.registrarActividad(1, "República Poniente", "Deshierbe", "Supervisión");

        int totalDespues = repo.obtenerKpis().getTotalActividadesEjecutadas();
        assert totalDespues == totalAntes + 1 : "El total de actividades debió incrementarse en 1";

        controller.destruir();
        System.out.println("PASO (Total previo: " + totalAntes + " -> Total posterior: " + totalDespues + ")");
    }
}
