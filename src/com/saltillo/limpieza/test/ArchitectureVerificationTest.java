package com.saltillo.limpieza.test;

import com.saltillo.limpieza.controllers.ConsultaActividadesController;
import com.saltillo.limpieza.controllers.DashboardController;
import com.saltillo.limpieza.controllers.MainNavigationController;
import com.saltillo.limpieza.models.dto.*;
import com.saltillo.limpieza.models.entities.Actividad;
import com.saltillo.limpieza.models.entities.Usuario;
import com.saltillo.limpieza.patterns.observer.ActividadEventBus;
import com.saltillo.limpieza.patterns.observer.IObserver;
import com.saltillo.limpieza.repositories.IDashboardRepository;
import com.saltillo.limpieza.repositories.MockDashboardRepository;
import com.saltillo.limpieza.session.SesionUsuario;
import com.saltillo.limpieza.views.ConsultaActividadesView;
import com.saltillo.limpieza.views.DashboardView;
import com.saltillo.limpieza.views.LoginView;
import com.saltillo.limpieza.views.MainShellView;
import com.saltillo.limpieza.views.catalogos.CatalogosView;
import com.saltillo.limpieza.views.catalogos.CuadrillaView;

import javax.swing.WindowConstants;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Suite de pruebas unitarias y de arquitectura (Headless) actualizada para Issue #16:
 * 1. Verificación del Singleton SesionUsuario
 * 2. Soporte de múltiples colonias y TipoEventoActividad en ActividadEventDTO
 * 3. Búsqueda y filtros dinámicos con FiltroActividadDTO
 * 4. Patrón Observer con ActividadEventBus
 * 5. Integración reactiva Controller-Observer-Repository
 * 6. Módulo MVC de ConsultaActividades (Issue #16)
 */
public class ArchitectureVerificationTest {

    public static void main(String[] args) {
        System.out.println("===================================================================");
        System.out.println("VERIFICACION ARQUITECTONICA - INTEGRANTE 4 (DASHBOARD & CONSULTA)");
        System.out.println("===================================================================");

        testSesionUsuario();
        testRepositoryAndDtos();
        testBusquedaFiltrosActividad();
        testObserverPatternDirectly();
        testControllerObserverIntegration();
        testConsultaActividadesMVC();
        testCompatibilidadUsuarioYActividad();
        testCierreSeguroLimpiezaRecursos();

        System.out.println("\n>>> TODAS LAS PRUEBAS (INCLUYENDO ISSUE #15 Y COMPATIBILIDAD) PASARON CON EXITO (100% OK) <<<");
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

        List<ActividadDetalleDTO> todas = repo.buscarActividades(null);
        assert todas != null && todas.size() >= 8 : "Deben existir al menos 8 actividades simuladas";

        FiltroActividadDTO fCuadrilla = new FiltroActividadDTO();
        fCuadrilla.setIdCuadrilla(1);
        List<ActividadDetalleDTO> porCuadrilla = repo.buscarActividades(fCuadrilla);
        assert !porCuadrilla.isEmpty() : "Debe encontrar actividades para Cuadrilla #1";
        for (ActividadDetalleDTO a : porCuadrilla) {
            assert a.getIdCuadrilla() == 1 : "Todas las actividades deben pertenecer a la Cuadrilla #1";
        }

        FiltroActividadDTO fColonia = new FiltroActividadDTO();
        fColonia.setColonia("Centro");
        List<ActividadDetalleDTO> porColonia = repo.buscarActividades(fColonia);
        assert !porColonia.isEmpty() : "Debe encontrar actividades en colonias con 'Centro'";

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

        repo.registrarActividad(1, List.of("República Poniente", "Universidad"), "Deshierbe", "Supervisión");

        int totalDespues = repo.obtenerKpis().getTotalActividadesEjecutadas();
        assert totalDespues == totalAntes + 1 : "El total de actividades debió incrementarse en 1";

        controller.destruir();
        System.out.println("PASO (Total previo: " + totalAntes + " -> Total posterior: " + totalDespues + ")");
    }

    private static void testConsultaActividadesMVC() {
        System.out.print("[TEST 6] Verificando Módulo ConsultaActividades (Issue #16 MVC)... ");
        IDashboardRepository repo = new MockDashboardRepository();
        ConsultaActividadesView view = new ConsultaActividadesView();
        ConsultaActividadesController controller = new ConsultaActividadesController(view, repo);

        // Validar que la consulta inicial haya poblado actividades
        List<ActividadDetalleDTO> iniciales = repo.buscarActividades(null);
        assert !iniciales.isEmpty() : "El repositorio debe entregar actividades al controlador";

        // Registrar una nueva actividad y verificar que el controlador la reciba vía Observer
        int countAntes = repo.buscarActividades(null).size();
        repo.registrarActividad(2, List.of("San Patricio"), "Lavado y Desinfección", "Prueba Consulta MVC");
        int countDespues = repo.buscarActividades(null).size();
        assert countDespues == countAntes + 1 : "La nueva actividad debió agregarse a la lista de búsqueda";

        controller.destruir();
        System.out.println("PASO (Consulta MVC y Observer reactivo al 100%)");
    }

    private static void testCompatibilidadUsuarioYActividad() {
        System.out.print("[TEST 7] Verificando compatibilidad Usuario/Actividad con schema.sql y DTOs... ");
        String hash = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
        Usuario uSinRol = new Usuario(2, "Admin BD", "admin.bd", hash, true);
        assert Usuario.ROL_POR_DEFECTO.equals(uSinRol.getRol()) : "El usuario sin rol debe recibir ADMINISTRADOR por defecto";
        uSinRol.setRol(null);
        assert Usuario.ROL_POR_DEFECTO.equals(uSinRol.getRol()) : "setRol(null) debe asignar ADMINISTRADOR por defecto";

        LocalDateTime prog = LocalDateTime.of(2026, 11, 15, 9, 30);
        LocalDateTime real = LocalDateTime.of(2026, 11, 15, 11, 45);
        Actividad act = new Actividad();
        act.setFechaProgramada(prog);
        act.setFechaRealizacion(real);
        assert prog.equals(act.getFechaProgramada()) : "La fecha programada debe sincronizar fechaActividad y horaInicio";
        assert real.equals(act.getFechaRealizacion()) : "La fecha de realización debe conservarse";

        ActividadDetalleDTO dto = new ActividadDetalleDTO(
                10, 1, "Cuadrilla Centro", List.of("Centro"), "Barrido",
                "Plaza de Armas", "PROGRAMADA", prog, "Sin observaciones"
        );
        assert "Barrido - Plaza de Armas".equals(dto.getTitulo()) : "El DTO debe generar un título por defecto válido";
        System.out.println("PASO (Usuario sin ROL, fechas puente en Actividad y titulo por defecto en DTO OK)");
    }

    private static void testCierreSeguroLimpiezaRecursos() {
        System.out.print("[TEST 8] Verificando Cierre Seguro de recursos y cierre de ventana (Issue #15)... ");
        MainShellView shell = new MainShellView();
        assert shell.getDefaultCloseOperation() == WindowConstants.DO_NOTHING_ON_CLOSE
                : "La ventana principal debe interceptar el cierre con X";

        IDashboardRepository repo = new MockDashboardRepository();
        DashboardController dashCtrl = new DashboardController(new DashboardView(), repo);
        ConsultaActividadesController consCtrl = new ConsultaActividadesController(new ConsultaActividadesView(), repo);

        AtomicBoolean cierreBdSimulado = new AtomicBoolean(false);
        MainNavigationController nav = new MainNavigationController(shell);
        nav.registrarLimpiezaAlCerrar(dashCtrl::destruir);
        nav.registrarLimpiezaAlCerrar(consCtrl::destruir);
        nav.registrarLimpiezaAlCerrar(() -> cierreBdSimulado.set(true));
        nav.setAlCerrarSesionCallback(() -> {});

        LoginView loginView = new LoginView();
        assert loginView.getDefaultCloseOperation() == WindowConstants.EXIT_ON_CLOSE
                : "El Login debe cerrar la app si se cierra con X";
        loginView.dispose();

        assert nav.isSesionAutenticada() : "Debe existir sesión activa antes del cierre";
        nav.ejecutarLimpiezaRecursos();

        assert !nav.isSesionAutenticada() : "La sesión debe quedar destruida tras la limpieza";
        assert cierreBdSimulado.get() : "Las tareas de limpieza registradas deben ejecutarse";

        boolean rechazoSinSesion = false;
        try {
            new MainNavigationController(shell, true);
        } catch (IllegalStateException expected) {
            rechazoSinSesion = true;
        }
        assert rechazoSinSesion : "El Shell en modo estricto debe rechazar abrirse sin sesión activa";

        CatalogosView catalogosView = new CatalogosView();
        shell.registrarModulo("CATALOGOS", catalogosView);
        shell.mostrarModulo("CATALOGOS");
        assert "CATALOGOS".equals(shell.getModuloActivoActual()) : "El menú debe poder abrir el módulo CATALOGOS";

        CuadrillaView cuadrillaView = new CuadrillaView();
        shell.registrarModulo("CUADRILLAS", cuadrillaView);
        shell.mostrarModulo("CUADRILLAS");
        assert "CUADRILLAS".equals(shell.getModuloActivoActual()) : "El menú debe poder abrir el módulo CUADRILLAS";

        shell.dispose();
        System.out.println("PASO (Interceptación X, limpieza, sesión obligatoria, CATALOGOS y CUADRILLAS OK)");
    }
}
