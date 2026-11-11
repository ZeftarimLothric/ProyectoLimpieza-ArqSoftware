package com.saltillo.limpieza.repositories;

import com.saltillo.limpieza.models.entities.Cuadrilla;
import com.saltillo.limpieza.models.entities.Empleado;
import com.saltillo.limpieza.models.entities.IntegranteCuadrilla;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación en memoria de ICuadrillaPersonalRepository para pruebas y demos sin MySQL
 * (Integrante 3 - Issue #13). Usa los mismos IDs y nombres de cuadrilla que las actividades de ejemplo.
 */
public class MockCuadrillaRepository implements ICuadrillaPersonalRepository {

    private final List<Cuadrilla> cuadrillas = new ArrayList<>();
    private final List<Empleado> empleados = new ArrayList<>();
    private final List<IntegranteCuadrilla> asignaciones = new ArrayList<>();
    private int ultimoIdCuadrilla = 0;
    private int ultimoIdAsignacion = 0;

    public MockCuadrillaRepository() {
        agregarCuadrilla("Cuadrilla Centro Histórico");
        agregarCuadrilla("Cuadrilla Venustiano Carranza");
        agregarCuadrilla("Cuadrilla Mirasierra / Oriente");
        agregarCuadrilla("Cuadrilla República / Nazario");
        agregarCuadrilla("Cuadrilla Ramos / Zona Industrial");
        agregarCuadrilla("Cuadrilla Saltillo 2000");

        empleados.add(new Empleado(1, "Elena", "Fuentes", "Ríos", "8441000001", "Jefa de cuadrilla", true));
        empleados.add(new Empleado(2, "Jorge", "Martínez", "López", "8441000002", "Operario de limpieza", true));
        empleados.add(new Empleado(3, "Lucía", "Hernández", null, "8441000003", "Operaria de limpieza", true));
        empleados.add(new Empleado(4, "Pedro", "Morales", "Garza", "8441000004", "Jefe de cuadrilla", true));
        empleados.add(new Empleado(5, "Raúl", "Castillo", null, "8441000005", "Chofer", true));
        empleados.add(new Empleado(6, "Gabriel", "Treviño", "Saucedo", "8441000006", "Jefe de cuadrilla", true));
        empleados.add(new Empleado(7, "Andrea", "Ruiz", "Flores", "8441000007", "Operaria de limpieza", true));

        // Las cuadrillas 2, 4 y 5 quedan sin personal para probar ese caso.
        guardarConJefe(1, 1);
        agregarIntegrante(1, 2);
        agregarIntegrante(1, 3);
        guardarConJefe(3, 4);
        agregarIntegrante(3, 5);
        guardarConJefe(6, 6);
        agregarIntegrante(6, 7);
    }

    private void agregarCuadrilla(String nombre) {
        guardar(new Cuadrilla(0, nombre, true));
    }

    private void guardarConJefe(int idCuadrilla, int idEmpleado) {
        asignar(idCuadrilla, idEmpleado, true);
    }

    @Override
    public List<Cuadrilla> listar() {
        return new ArrayList<>(cuadrillas);
    }

    @Override
    public Optional<Cuadrilla> buscarPorId(int id) {
        return cuadrillas.stream().filter(c -> c.getIdCuadrilla() == id).findFirst();
    }

    @Override
    public boolean guardar(Cuadrilla cuadrilla) {
        ultimoIdCuadrilla++;
        cuadrilla.setIdCuadrilla(ultimoIdCuadrilla);
        cuadrillas.add(cuadrilla);
        return true;
    }

    @Override
    public boolean actualizar(Cuadrilla cuadrilla) {
        for (int i = 0; i < cuadrillas.size(); i++) {
            if (cuadrillas.get(i).getIdCuadrilla() == cuadrilla.getIdCuadrilla()) {
                cuadrillas.set(i, cuadrilla);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        asignaciones.removeIf(a -> a.getIdCuadrilla() == id);
        return cuadrillas.removeIf(c -> c.getIdCuadrilla() == id);
    }

    /** Igual que SqlCuadrillaRepository: el jefe primero. */
    @Override
    public List<IntegranteCuadrilla> listarIntegrantes(int idCuadrilla) {
        List<IntegranteCuadrilla> integrantes = new ArrayList<>();
        asignaciones.stream().filter(a -> a.getIdCuadrilla() == idCuadrilla && a.isJefe()).forEach(integrantes::add);
        asignaciones.stream().filter(a -> a.getIdCuadrilla() == idCuadrilla && !a.isJefe()).forEach(integrantes::add);
        return integrantes;
    }

    @Override
    public List<Empleado> listarEmpleadosDisponibles(int idCuadrilla) {
        List<Empleado> disponibles = new ArrayList<>();
        for (Empleado e : empleados) {
            boolean asignado = asignaciones.stream().anyMatch(a -> a.getEmpleado().getIdEmpleado() == e.getIdEmpleado());
            if (e.isActivo() && !asignado) {
                disponibles.add(e);
            }
        }
        return disponibles;
    }

    @Override
    public boolean guardarConJefe(Cuadrilla cuadrilla, int idEmpleadoJefe) {
        guardar(cuadrilla);
        guardarConJefe(cuadrilla.getIdCuadrilla(), idEmpleadoJefe);
        return true;
    }

    @Override
    public boolean actualizarConJefe(Cuadrilla cuadrilla, int idEmpleadoJefe) {
        boolean ok = actualizar(cuadrilla);
        asignaciones.removeIf(a -> a.getIdCuadrilla() == cuadrilla.getIdCuadrilla()
                && (a.isJefe() || a.getEmpleado().getIdEmpleado() == idEmpleadoJefe));
        guardarConJefe(cuadrilla.getIdCuadrilla(), idEmpleadoJefe);
        return ok;
    }

    @Override
    public void agregarIntegrante(int idCuadrilla, int idEmpleado) {
        asignar(idCuadrilla, idEmpleado, false);
    }

    @Override
    public boolean quitarIntegrante(int idCuadrilla, int idEmpleado) {
        boolean esJefe = asignaciones.stream().anyMatch(a -> a.getIdCuadrilla() == idCuadrilla
                && a.getEmpleado().getIdEmpleado() == idEmpleado && a.isJefe());
        if (esJefe) {
            throw new ReglaNegocioException("No se puede quitar al jefe de la cuadrilla.");
        }
        return asignaciones.removeIf(a -> a.getIdCuadrilla() == idCuadrilla && a.getEmpleado().getIdEmpleado() == idEmpleado);
    }

    private void asignar(int idCuadrilla, int idEmpleado, boolean jefe) {
        Empleado empleado = empleados.stream().filter(e -> e.getIdEmpleado() == idEmpleado).findFirst()
                .orElseThrow(() -> new ReglaNegocioException("El empleado seleccionado ya no existe."));
        ultimoIdAsignacion++;
        asignaciones.add(new IntegranteCuadrilla(ultimoIdAsignacion, idCuadrilla, empleado, LocalDate.now(), jefe));
    }
}
