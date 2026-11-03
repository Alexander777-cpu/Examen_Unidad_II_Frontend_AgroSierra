@'
# AgroCampo - Aplicación Móvil KMP (AgroSierra)

Aplicación móvil multiplataforma desarrollada en **Kotlin Multiplatform (KMP)** y **Compose Multiplatform** para la gestión de labores agrícolas en campo bajo arquitectura **Offline-First**.

## 1. Estructura de Paquetes
El código compartido se ubica bajo `shared/src/commonMain/kotlin/pe/upeu/agrosierra/`:
* `domain/`:
  * `model/`: Entidades de negocio (`Lote`, `Labor`, `TipoLabor`, `Sesion`, `ErrorApi`).
  * `repository/`: Interfaces y contratos abstractos (`LoteRepository`, `LaborRepository`, `AuthRepository`).
  * `usecase/`: Casos de uso de negocio y orquestación (`SincronizarUseCase`).
* `data/`:
  * `remote/`: Ktor Client (`HttpClientFactory`, `ApiConfig`, `MapeoErrores`), DTOs y serialización JSON.
  * `local/`: Interfaces de persistencia (`AlmacenSeguro`) y esquema de **SQLDelight** (`AgroDb.sq`).
  * `repository/`: Implementaciones con estrategia *Offline-First* y gestor de sincronización (`SyncManager`).
* `presentation/`:
  * `navigation/`: Enrutamiento y grafo de navegación (`Screen.kt`).
  * `padron/`: Padrón de lotes y filtros por valle (`LotesScreen`, `LotesViewModel`).
  * `detalle/`: Detalle del lote, historial y control de acciones (`DetalleLoteScreen`, `DetalleLoteViewModel`).
  * `labor/`: Formulario de registro/edición con validaciones (`FormularioLaborScreen`).
  * `sesion/`: Pantalla de inicio de sesión (`LoginScreen`, `LoginViewModel`).
* `di/`: Inyección de dependencias con **Koin** (`KoinModule.kt`).
* `androidApp/`: Android Keystore (`EncryptedSharedPreferences`) y driver de SQLite para Android.

## 2. Contrato del API Consumido
| Método | Ruta | Privilegio | Códigos HTTP |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/login` | Público | `200`, `401` |
| `GET` | `/api/v1/lotes?pagina&tamanio&valle` | `LOTE_LEER` | `200`, `401` |
| `GET` | `/api/v1/lotes/{id}` | `LOTE_LEER` | `200`, `401`, `404` |
| `GET` | `/api/v1/lotes/{id}/labores` | `LABOR_LEER` | `200`, `401`, `404` |
| `POST` | `/api/v1/labores` | `LABOR_CREAR` | `201`, `400`, `401`, `403`, `409` |
| `PUT` | `/api/v1/labores/{id}` | `LABOR_EDITAR` | `200`, `400`, `401`, `403`, `404`, `409` |
| `DELETE` | `/api/v1/labores/{id}` | `LABOR_ELIMINAR` | `204`, `401`, `403`, `404` |
| `POST` | `/api/v1/lotes/{id}/cerrar-campania` | `CAMPANIA_CERRAR` | `200`, `401`, `403`, `404`, `409` |
| `GET` | `/api/v1/usuarios/me` | Autenticado | `200`, `401` |

## 3. Matriz de Roles y Privilegios (RBAC)
| Privilegio | Descripción | TECNICO | JEFE_CAMPO | ADMINISTRADOR |
| :--- | :--- | :---: | :---: | :---: |
| `LOTE_LEER` | Consultar padrón y detalle de lotes | Sí | Sí | Sí |
| `LABOR_LEER` | Consultar labores de un lote | Sí | Sí | Sí |
| `LABOR_CREAR` | Registrar nueva labor | Sí | Sí | No |
| `LABOR_EDITAR` | Modificar labores | Solo propias (<24h) | Todas | No |
| `LABOR_ELIMINAR` | Eliminar labores registradas | No | Sí | No |
| `CAMPANIA_CERRAR` | Cierre formal de campaña | No | Sí | No |
| `USUARIO_GESTIONAR` | Gestión administrativa | No | No | Sí |

* **Control de UI:** La interfaz evalúa `sesion.puede(privilegio)` para condicionar la visibilidad de los botones de acción (`+`, editar, eliminar y cerrar campaña).

## 4. Decisiones de Arquitectura
1. **Estrategia Offline-First:** La interfaz observa directamente los flujos reactivos (`Flow`) de **SQLDelight**. La pantalla principal y el historial leen de SQLite local, garantizando disponibilidad en modo avión.
2. **Sincronización:** Las labores creadas sin red se guardan localmente con un ID provisional negativo y `sincronizado = 0`. Al restablecer la red, el usuario pulsa sincronizar para subirlas vía `POST` y actualizar el ID definitivo.
3. **Almacenamiento Seguro:** El token JWT se almacena cifrado en `EncryptedSharedPreferences` respaldado por **Android Keystore**.
4. **Mapeo Centralizado de Errores:** Jerarquía sellada `ErrorApi` que discrimina `Validacion` (400), `NoAutorizado` (401), `SinPrivilegio` (403), `NoEncontrado` (404), `Conflicto` (409) y `SinConexion`.

## 5. Instrucciones de Ejecución
* **Compilar e instalar APK:**
  ```bash
  ./gradlew :androidApp:assembleDebug