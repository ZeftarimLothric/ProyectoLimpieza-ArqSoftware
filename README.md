# Sistema de Gestión de Actividades de Limpieza Urbana — Municipio de Saltillo

## Proyecto Parcial 2 — Diseño y Arquitectura de Software

**Responsabilidad Asignada:**

1. **Shell / Ventana Principal con Menú de Navegación Modular (Content Host)**.
2. **Dashboard Ejecutivo de Desempeño por Cuadrilla (KPIs y Tabla de Rendimiento)**.
3. **Desacoplamiento e Inversión de Dependencias (Contratos `IDashboardRepository` y DTOs limpios)**.
4. **Patrón de Diseño Observer (`ActividadEventBus`)** para sincronización y actualización reactiva en tiempo real.

---

## Estructura Arquitectónica (Strict MVC + Observer)

El código está organizado en paquetes desacoplados bajo el estándar de separación de responsabilidades:

```
src/
└── com/saltillo/limpieza/
    ├── controllers/
    │   ├── DashboardController.java         # Controlador del Dashboard (Observador reactivo)
    │   └── MainNavigationController.java    # Controlador del Shell, sesión y navegación
    ├── models/dto/
    │   ├── ActividadEventDTO.java           # Payload del evento de nueva actividad
    │   ├── CuadrillaResumenDTO.java         # DTO con estadísticas por cuadrilla
    │   └── DashboardKpisDTO.java            # DTO con indicadores ejecutivos globales
    ├── patterns/observer/
    │   ├── IObserver.java                   # Interfaz genérica de Observador
    │   ├── ISubject.java                    # Interfaz genérica de Sujeto (Observable)
    │   └── ActividadEventBus.java           # Event Bus desacoplado (Singleton / Broker)
    ├── repositories/
    │   ├── IDashboardRepository.java        # Contrato de acceso a datos (Inversión de Dependencias)
    │   └── MockDashboardRepository.java     # Implementación mock en memoria con datos de Saltillo
    ├── views/
    │   ├── components/
    │   │   ├── KpiCardComponent.java        # Tarjeta visual ejecutiva de KPI
    │   │   ├── ProgressBarRenderer.java     # Renderer con barra de progreso para la tabla
    │   │   ├── RoundedPanel.java            # Panel con bordes redondeados y estilo card
    │   │   └── StatusBadgeRenderer.java     # Renderer con pastilla / badge de estado operativo
    │   ├── DashboardView.java               # Vista del Dashboard Ejecutivo
    │   ├── MainShellView.java               # Shell principal con menú y Content Host (CardLayout)
    │   └── PlaceholderModuleView.java       # Paneles contenedores modulares para Integrantes 1, 2 y 3
    └── Main.java                            # Composition Root (Punto de entrada e inyección)
```

---

## Cómo Compilar y Ejecutar

### Opción 1: Scripts Automáticos (Windows)

- **Compilar**: Doble clic en `build.bat`
- **Ejecutar**: Doble clic en `run.bat`
- **Pruebas Arquitectónicas**: Doble clic en `test.bat`

### Opción 2: Consola / Terminal

```bash
# 1. Compilación
javac -encoding UTF-8 -d bin (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })

# 2. Ejecución
java -cp bin com.saltillo.limpieza.Main

# 3. Verificación de Arquitectura y Patrón Observer
java -ea -cp bin com.saltillo.limpieza.test.ArchitectureVerificationTest
```

### Opción 3: En tu IDE Favorito

El proyecto incluye `pom.xml`, por lo que puedes abrir la carpeta directamente en **IntelliJ IDEA**, **Apache NetBeans** o **Eclipse** como proyecto Maven o Java Estándar (Java 21).

---

## Guía de Integración para el Equipo

### Integrantes 1 y 2 (Base de Datos & DAOs)

1. Creen su clase conectora a MySQL/SQL implementando el contrato:
   ```java
   public class SqlDashboardRepository implements IDashboardRepository {
       @Override
       public DashboardKpisDTO obtenerKpis() { ... }
       @Override
       public List<CuadrillaResumenDTO> obtenerResumenPorCuadrillas() { ... }
       ...
   }
   ```
2. En [`Main.java`](file:///src/com/saltillo/limpieza/Main.java), sustituyan únicamente:
   ```java
   // Sustituir:
   IDashboardRepository dashboardRepo = new MockDashboardRepository();
   // Por:
   IDashboardRepository dashboardRepo = new SqlDashboardRepository();
   ```

### Integrante 3 (Registro de Actividades)

Cuando el formulario registre una actividad de limpieza, publica el evento en una sola línea:

```java
ActividadEventBus.getInstance().publicarNuevaActividad(
    new ActividadEventDTO(idCuadrilla, nombreCuadrilla, colonia, tipoLimpieza, notas)
);
```

El **Dashboard se actualizará automáticamente** sin necesidad de consultar manualmente ni recargar ventanas.

---

## Verificación contra la Rúbrica


| Rubro                                     | Estado   | Implementación Integrante 4                                                                                            |
| ----------------------------------------- | -------- | ----------------------------------------------------------------------------------------------------------------------- |
| **1. Totalmente POO**                     | Cumplido | Clases fuertemente tipadas, encapsulamiento, polimorfismo e interfaces.                                                 |
| **2. Arquitectura MVC**                   | Cumplido | Vistas 100% pasivas, Controladores que gestionan eventos, DTOs inmutables.                                              |
| **3. Cierre de Sistema & Navegación**    | Cumplido | Menú lateral persistente, botón "Cerrar Sesión Segura" que destruye variables globales de sesión y libera recursos. |
| **4. Uso de Git modular**                 | Cumplido | Paquetes separados para evitar conflictos de merge con los otros 3 compañeros.                                         |
| **5. Patrón de Diseño (Observer)**      | Cumplido | `ActividadEventBus` (Subject) y `DashboardController` (Observer).                                                       |
| **11. Dashboard Ejecutivo por Cuadrilla** | Cumplido | Resumen de cuadrillas, total de actividades, KPIs ejecutivos, meta y visualización tabular.                            |
