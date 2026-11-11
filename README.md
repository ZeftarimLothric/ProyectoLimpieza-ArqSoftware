# Sistema de Gestión de Actividades de Limpieza Urbana — Municipio de Saltillo

## Proyecto Parcial 2 — Arquitectura de Software (Desktop MVC)

Aplicación Desktop en **Java Swing** con arquitectura **Modelo-Vista-Controlador (MVC) Estricta**, persistencia en **MySQL 8.4**, gestión de evidencias fotográficas, catálogo oficial **SEPOMEX Saltillo** y actualización reactiva mediante el patrón de diseño **Observer**.

Desarrollada conforme a la rúbrica oficial de evaluación del Parcial 2.

---

## Arquitectura del Sistema (Strict MVC + Patrones de Diseño)

El sistema se estructura en capas desacopladas mediante Inversión de Dependencias (interfaces y DTOs):

```
src/com/saltillo/limpieza/
├── controllers/
│   ├── ActividadController.java           # Orquestación de CRUD de actividades y evidencias
│   ├── ColoniaSelectorController.java     # Selección múltiple y búsqueda de colonias SEPOMEX
│   ├── ConsultaActividadesController.java # Búsqueda y filtrado multicriterio (IObserver)
│   ├── DashboardController.java           # Métricas ejecutivas y KPIs en tiempo real (IObserver)
│   ├── EvidenciaController.java           # Administración de imágenes y almacenamiento
│   ├── LoginController.java               # Autenticación con SHA-256 e inicio de sesión
│   ├── MainNavigationController.java      # Navegación del Shell, desmontaje y cierre seguro
│   └── catalogos/                         # Controladores CRUD de los catálogos del sistema
│       ├── CatalogoController.java        # Controlador genérico base
│       ├── ColoniaController.java
│       ├── CuadrillaController.java
│       ├── EmpleadoController.java
│       ├── TipoActividadController.java
│       └── UsuarioController.java
├── database/
│   └── ConexionBD.java                    # Conexión única a MySQL (Patrón Singleton)
├── models/
│   ├── dto/                               # Data Transfer Objects limpios entre capas
│   │   ├── ActividadDetalleDTO.java
│   │   ├── ActividadEventDTO.java
│   │   ├── CuadrillaResumenDTO.java
│   │   ├── DashboardKpisDTO.java
│   │   └── FiltroActividadDTO.java
│   └── entities/                          # Entidades del dominio mapeadas a tablas SQL
│       ├── Actividad.java
│       ├── Colonia.java
│       ├── Cuadrilla.java
│       ├── Empleado.java
│       ├── EstadoActividad.java
│       ├── Evidencia.java
│       ├── IntegranteCuadrilla.java
│       ├── TipoActividad.java
│       └── Usuario.java
├── patterns/
│   └── observer/                          # Patrón de Diseño Observer
│       ├── ActividadEventBus.java         # Subject / Event Broker (Singleton)
│       ├── IObserver.java                 # Interfaz genérica observadora
│       └── ISubject.java                  # Interfaz genérica observable
├── repositories/                          # Capa de datos desacoplada (Patrón Repository)
│   ├── IActividadRepository.java / SqlActividadRepository.java
│   ├── IColoniaRepository.java / SqlColoniaRepository.java
│   ├── ICuadrillaRepository.java / SqlCuadrillaRepository.java
│   ├── IDashboardRepository.java / SqlDashboardRepository.java
│   ├── IEmpleadoRepository.java / SqlEmpleadoRepository.java
│   ├── IEvidenciaRepository.java / SqlEvidenciaRepository.java
│   ├── ITipoActividadRepository.java / SqlTipoActividadRepository.java
│   └── IUsuarioRepository.java / SqlUsuarioRepository.java
├── security/
│   └── PasswordHasher.java                # Hashing y validación de contraseñas con SHA-256
├── services/
│   └── EvidenciaStorageService.java       # Manejo físico, validación y copia de imágenes
├── session/
│   └── SesionUsuario.java                 # Estado global de sesión activa (Singleton)
└── views/                                 # Vistas pasivas Swing (Sin lógica de negocio)
    ├── LoginView.java                     # Ventana modal de acceso para administradores
    ├── MainShellView.java                 # Contenedor principal con menú lateral responsivo
    ├── DashboardView.java                 # Tablero ejecutivo y gráficas/KPIs de desempeño
    ├── ConsultaActividadesView.java       # Pantalla de consulta histórica y filtros
    ├── actividades/                       # Vistas de lista, formulario y detalle de actividades
    │   ├── ActividadListView.java
    │   ├── ActividadFormView.java
    │   ├── ActividadDetalleView.java
    │   ├── ColoniaSelectorDialog.java
    │   └── EvidenciaPanel.java
    └── catalogos/                         # Vistas de administración de catálogos y cuadrillas
        ├── CatalogosView.java
        ├── CuadrillaView.java
        └── ...
```

---

## Mapeo de Cumplimiento de la Rúbrica Oficial

| # | Requisito de la Rúbrica | Estado | Implementación en el Proyecto |
|---|---|---|---|
| 1 | Totalmente Orientado a Objetos | Cumplido | Encapsulamiento estricto, entidades fuertemente tipadas, polimorfismo mediante interfaces `ICrudRepository` y herencia limpia. |
| 2 | Arquitectura en MVC | Cumplido | Separación formal en paquetes `models`, `views` (100% pasivas) y `controllers`. Comunicación por eventos y callbacks. |
| 3 | Base de Datos MySQL | Cumplido | Esquema relacional con 10 tablas normalizadas (`db/schema.sql`) y conector oficial `mysql-connector-j:8.4.0`. |
| 4 | Modelo de Clases | Pendiente (Doc) | Diagrama de clases y documentación UML del sistema (Issue #20). |
| 5 | Modelo de Bases de Datos | Cumplido | Diagrama Entidad-Relación documentado en `docs/modelo-er.png`. |
| 6 | Comunicación Frontend / Backend | Cumplido | Desacoplamiento total mediante inversión de dependencias: controladores consumen contratos de repositorio hacia MySQL. |
| R1 | Login de Administradores (5 pts) | Cumplido | `LoginView` + `LoginController` con credenciales cifradas con SHA-256 (`PasswordHasher`) y control de acceso. |
| R2 | CRUD Catálogos Operable (5 pts) | Cumplido | Módulo de Catálogos (`CatalogosView`) operable para Usuarios, Empleados, Colonias y Tipos de Actividad. |
| R3 | Cerrar Sistema y Menú (5 pts) | Cumplido | Menú lateral persistente en `MainShellView`, captura de ventana y botón "Cerrar Sesión Segura" que libera conexiones JDBC y destruye variables de sesión. |
| R4 | Uso de GitHub Colaborativo (5 pts) | Cumplido | Historial de commits, ramas por funcionalidad y Pull Requests mergeados por todos los integrantes. |
| R5 | 2 Patrones de Diseño (10 pts) | Cumplido | **Singleton** (`ConexionBD`, `SesionUsuario`, `ActividadEventBus`) y **Observer** (`ISubject`, `IObserver`, suscripción reactiva en `DashboardController` y `ConsultaActividadesController`). |
| R6 | Actividad y Detalle (10 pts) | Cumplido | CRUD completo de actividades (`ActividadController`), lista paginada y ventana de inspección detallada con personal y estado. |
| R7 | Cargar Imagen de Evidencia (10 pts) | Cumplido | Carga, previsualización, validación y persistencia de archivos fotográficos (`EvidenciaStorageService`, `EvidenciaPanel`). |
| R8 | Colonias Atendidas y SEPOMEX (10 pts) | Cumplido | Importación oficial de SEPOMEX Saltillo (771 colonias en base de datos) y diálogo de asignación múltiple por actividad. |
| R9 | Jefes de Cuadrilla y Personal (10 pts) | Cumplido | Módulo `CuadrillaView` para definir cuadrillas, designar supervisor/jefe, asignar operarios y enviar a lugares específicos. |
| R10 | Estrategia CRUD del Sistema (10 pts) | Cumplido | Operaciones Create, Read, Update, Delete operables en Actividades, Cuadrillas, Evidencias y Catálogos. |
| R11 | Consulta y Dashboard Ejecutivo (10 pts) | Cumplido | Pantalla de búsqueda histórica con filtros combinables y Tablero Ejecutivo con KPIs, metas y resumen por cuadrilla en vivo. |

---

## Configuración y Puesta en Marcha

### 1. Prerrequisitos
- Java JDK 21 o superior.
- MySQL Server 8.0 o superior ejecutándose en el puerto 3306.

### 2. Configuración de Base de Datos
1. Copiar el archivo de ejemplo de configuración en la carpeta `config`:
   ```bash
   copy config\db.properties.example config\db.properties
   ```
2. Editar `config/db.properties` con las credenciales locales de MySQL:
   ```properties
   db.url=jdbc:mysql://localhost:3306/gestion_limpieza?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   db.user=root
   db.password=tu_password
   ```
3. Ejecutar los scripts en el gestor de MySQL en este orden:
   - `db/schema.sql` (Crea la base de datos `gestion_limpieza` y sus 10 tablas).
   - `db/seed_admin.sql` (Crea el usuario administrador inicial).
   - `db/sepomex_saltillo.sql` (Carga las 771 colonias oficiales de Saltillo).
   - `db/seed.sql` (Carga empleados, cuadrillas y actividades de prueba).

Credenciales iniciales de prueba:
- **Usuario:** `admin`
- **Contraseña:** `admin123`

---

## Compilación y Ejecución

El proyecto incluye scripts en la raíz para facilitar la compilación y prueba en Windows:

- **Compilar el proyecto:**
  ```cmd
  build.bat
  ```
- **Ejecutar la aplicación:**
  ```cmd
  run.bat
  ```
- **Ejecutar suite de pruebas unitarias y de arquitectura:**
  ```cmd
  test.bat
  ```

---

## Equipo de Desarrollo
- **Eduardo (ZeftarimLothric):** Arquitectura base, infraestructura de persistencia Singleton, ciclo de vida de sesión, cierre seguro, módulo de consulta de actividades e integración general.
- **Angel (miwaangel):** CRUD de actividades, integración de evidencias, diálogo multi-colonia y repositorios SQL de actividades/evidencias.
- **Ximena (KimXimena):** Catálogos, datos de prueba iniciales, catálogo SEPOMEX y repositorio SQL del Dashboard ejecutivo.
- **Eli (za-driel):** Módulo de cuadrillas (supervisores y personal operativo) e interfaces de catálogos.
