# Dominio del problema

Sistema de planificación de infraestructuras y rutas de recogida de residuos. El dominio responde a preguntas como:

- Dónde localizar infraestructuras (bases operativas, estaciones de transferencia, plantas de tratamiento, etc.).
- Qué contenedores atiende cada infraestructura (agrupados en clústeres).
- Cómo se comportan costes, capacidades, rutas diarias y niveles de servicio.

El modelo de dominio vive en `back-end/src/main/java/es/ull/project/domain/`.

## 2. Entidades (Entities)

### 2.1. Contenedor (`Container`)

Punto físico de recogida de residuos (contenedor de calle, punto de aportación, etc.).

| Atributo | Tipo | Obligatorio | Descripción |
|----------|------|-------------|-------------|
| `id` | `UUID` | Sí (inmutable) | Identificador único |
| `name` | `Name` | Sí | Nombre legible |
| `location` | `Location` | Sí | Ubicación física |
| `wasteType` | `WasteType` | Sí | Tipo de residuo recogido |
| `capacityLiters` | `ContainerCapacityLiters` | Sí | Capacidad máxima del contenedor (litros) |
| `dailyDemandLitersPerDay` | `DailyWasteDemandLitersPerDay` | Sí | Demanda diaria aproximada (litros/día) |
| `serviceZone` | `ServiceZone` | No | Zona de servicio geográfica |

**Reglas:**

- En un plan, cada contenedor pertenece a un único clúster (`ServiceAssignment`).
- Solo puede asignarse a infraestructuras que cumplan las políticas de servicio del plan.

### 2.2. Infraestructura (`Facility`)

Infraestructura de gestión de residuos. Es **raíz de agregado**.

| Atributo | Tipo | Obligatorio | Descripción |
|----------|------|-------------|-------------|
| `id` | `UUID` | Sí (inmutable) | Identificador único |
| `name` | `Name` | Sí | Nombre legible |
| `facilityType` | `FacilityType` | Sí | Tipo de instalación |
| `location` | `Location` | Sí | Ubicación física |
| `storageCapacity` | `StorageCapacityKilograms` | Sí | Capacidad de almacenamiento (kg) |
| `processingCapacity` | `ProcessingCapacityKilogramsPerDay` | Sí | Capacidad de procesamiento (kg/día) |
| `unloadingTime` | `UnloadingTime` | Sí | Tiempo de descarga de camiones (minutos) |
| `openingFixedCost` | `OpeningFixedCost` | Sí | Coste fijo de apertura |
| `status` | `FacilityStatus` | Sí | Estado del ciclo de vida |
| `currentFillingLevel` | `DailyWasteDemandLitersPerDay` | Calculado | Nivel de llenado actual (litros/día) |

**Valores de `FacilityType`:** `OPERATIONAL_BASE`, `TRANSFER_STATION`, `TREATMENT_PLANT`.

**Valores de `FacilityStatus`:** `CANDIDATE`, `PLANNED`, `OPEN`, `DISCARDED`.

**Invariantes:**

- No se asignan contenedores si `status` es `DISCARDED`.
- Las aperturas del plan deben respetar el presupuesto máximo (`MaximumBudget`).

### 2.3. Plan de infraestructuras (`InfrastructurePlan`)

Decisión completa de planificación para un horizonte temporal. Agrega instalaciones, asignaciones, planes diarios y métricas del algoritmo.

| Atributo | Tipo | Obligatorio | Descripción |
|----------|------|-------------|-------------|
| `id` | `UUID` | Sí (inmutable) | Identificador único |
| `period` | `PlanningPeriod` | Sí | Periodo de planificación |
| `selectedFacilities` | `List<Facility>` | Calculado | Instalaciones seleccionadas |
| `serviceAssignments` | `List<ServiceAssignment>` | Calculado | Clústeres contenedor → instalación |
| `dailyPlans` | `List<DailyPlan>` | Calculado | Rutas diarias generadas |
| `servicePolicies` | `ServicePolicies` | Sí | Políticas de servicio |
| `maxBudget` | `MaximumBudget` | Sí | Presupuesto máximo |
| `estimatedTotalCost` | `TotalCost` | Calculado | Coste total estimado |
| `totalCollectedKilograms` | `CollectedWeightKilograms` | Calculado | Peso total recogido (kg) |
| `totalCollectedLiters` | `CollectedVolumeLiters` | Calculado | Volumen total recogido (litros) |
| `totalDistanceMeters` | `Distance` | Calculado | Distancia total de rutas (m) |
| `numberOfDays` | `NumberOfDays` | No | Días del horizonte |
| `averagePickupTimeMinutes` | `AveragePickupTimeMinutes` | No | Tiempo medio de recogida (min) |
| `executedAt` | `ExecutedAt` | No | Marca temporal de ejecución del algoritmo |
| `validityState` | `InfrastructurePlanValidityState` | Sí | Alineación con datos maestros |
| `executionRequestJson` | `AlgorithmJsonPayload` | No | Snapshot JSON de la petición de ejecución |
| `containerDailyStates` | `List<ContainerDailyState>` | Calculado | Estados diarios de contenedores |

**Valores de `InfrastructurePlanValidityState`:** `VALID`, `OBSOLETE` (entidades referenciadas modificadas tras la ejecución).

**Invariantes:**

- `estimatedTotalCost` ≤ `maxBudget`.
- Cada contenedor del ámbito queda cubierto por exactamente un clúster en `serviceAssignments`.
- El plan cumple `servicePolicies` cuando están definidas.

**Métodos de dominio relevantes:** `addServiceAssignment`, `addFacility`, `addDailyPlan`, `recalculateTotalCost`, `isPlanValid`, `markObsoleteIfStillValid`.

### 2.4. Asignación de servicio (`ServiceAssignment`)

Relación **inmutable** entre un plan, una instalación y un **clúster de contenedores** (no es 1:1 contenedor-instalación).

| Atributo | Tipo | Obligatorio | Descripción |
|----------|------|-------------|-------------|
| `id` | `UUID` | Sí (inmutable) | Identificador del clúster |
| `infrastructurePlan` | `InfrastructurePlan` | Sí | Plan padre |
| `facility` | `Facility` | Sí | Instalación que presta servicio |
| `assignedContainers` | `List<Container>` | Sí (no vacía) | Contenedores del clúster |

**Reglas:**

- La lista de contenedores no puede estar vacía.
- La instalación no puede estar en estado `DISCARDED`.

### 2.5. Vehículo (`Vehicle`)

Camión o unidad de transporte. Es **raíz de agregado**.

| Atributo | Tipo | Obligatorio | Descripción |
|----------|------|-------------|-------------|
| `id` | `UUID` | Sí (inmutable) | Identificador único |
| `name` | `Name` | Sí | Nombre legible |
| `vehicleType` | `VehicleType` | Sí | Tipo funcional |
| `capacityKilograms` | `VehicleCapacityKilograms` | Sí | Capacidad de carga (kg) |
| `capacityLiters` | `VehicleCapacityLiters` | Sí | Capacidad de carga (litros) |
| `costPerKilometer` | `TransportationVariableCost` | Sí | Coste variable por km |

**Valores de `VehicleType`:** `COLLECTION_TRUCK`, `TRANSFER_TRUCK`, `SUPPORT_VEHICLE`.

### 2.6. Plan diario (`DailyPlan`)

Ruta de recogida de un día: vehículo, instalación de origen/destino y paradas. Es **raíz de agregado**.

| Atributo | Tipo | Obligatorio | Descripción |
|----------|------|-------------|-------------|
| `id` | `UUID` | Sí (inmutable) | Identificador único |
| `infrastructurePlan` | `InfrastructurePlan` | Sí | Plan padre |
| `facility` | `Facility` | Sí | Instalación inicio/fin de ruta |
| `serviceDate` | `LocalDate` | Sí | Fecha de ejecución del servicio |
| `planDay` | `PlanDay` | No | Día dentro del horizonte (1, 2, …) |
| `vehicle` | `Vehicle` | Sí | Vehículo asignado |
| `totalCollectedKilograms` | `CollectedWeightKilograms` | Calculado | Peso total recogido en la ruta |
| `totalCollectedLiters` | `CollectedVolumeLiters` | Calculado | Volumen total recogido |
| `totalDistanceMeters` | `Distance` | Calculado | Distancia total de la ruta (m) |
| `stops` | `List<Stop>` | Calculado | Paradas ordenadas de la ruta |

### 2.7. Parada (`Stop`)

Punto visitado dentro de un `DailyPlan` (entidad local del agregado).

| Atributo | Tipo | Obligatorio | Descripción |
|----------|------|-------------|-------------|
| `id` | `UUID` | Sí (inmutable) | Identificador único |
| `sequence` | `RouteSequence` | Sí | Posición en la ruta |
| `type` | `StopType` | Sí | Tipo de parada |
| `container` | `Container` | Sí si `CONTAINER` | Contenedor visitado |
| `collectedKilograms` | `CollectedWeightKilograms` | Sí | Residuos recogidos (kg) |
| `collectedLiters` | `CollectedVolumeLiters` | Sí | Residuos recogidos (litros) |
| `distanceFromPreviousMeters` | `Distance` | Sí | Distancia desde la parada anterior (m) |
| `cumulativeDistanceMeters` | `Distance` | Sí | Distancia acumulada hasta esta parada (m) |
| `containerActualLiters` | `CollectedVolumeLiters` | No | Litros en el contenedor antes de la recogida |
| `alerts` | `List<StopAlert>` | No | Alertas generadas en la parada |

**Valores de `StopType`:** `CONTAINER`, `FACILITY` (vuelta a instalación para descargar).

### 2.8. Estado diario del contenedor (`ContainerDailyState`)

Snapshot del estado de un contenedor en un día concreto del plan (persistido con el plan).

| Atributo | Tipo | Obligatorio | Descripción |
|----------|------|-------------|-------------|
| `id` | `UUID` | Sí (inmutable) | Identificador del snapshot |
| `infrastructurePlanId` | `InfrastructurePlanId` | No | Plan padre (legacy sin enlace) |
| `containerId` | `ContainerId` | Sí | Contenedor referenciado |
| `planDay` | `PlanDay` | Sí | Día de planificación (≥ 1) |
| `dailyFillingLiters` | `CollectedVolumeLiters` | Sí | Nivel de llenado del día (litros) |
| `containerCapacityLiters` | `ContainerCapacityLiters` | Sí | Capacidad en el momento del plan |
| `dailyDemandLitersPerDay` | `DailyWasteDemandLitersPerDay` | Sí | Demanda diaria esperada |
| `status` | `ContainerStatus` | Sí | Estado calculado |

**Valores de `ContainerStatus`:** `CORRECT`, `OVERFLOWED`.

### 2.9. Alerta de parada (`StopAlert`)

Alerta generada durante el procesamiento de una parada (valor de dominio embebido en `Stop`).

| Atributo | Tipo | Obligatorio | Descripción |
|----------|------|-------------|-------------|
| `type` | `StopAlertType` | Sí | Tipo de alerta |
| `message` | `StopAlertMessage` | Sí | Mensaje descriptivo |
| `value` | `StopAlertValue` | No | Valor numérico de contexto |

## 3. Objetos de valor (Value Objects)

### 3.1. Identificadores y nombres

- `ContainerId`, `InfrastructurePlanId` — wrappers de `UUID`.
- `Name` — nombre legible de contenedor, instalación o vehículo.
- Los identificadores de entidad principales (`Container`, `Facility`, `Vehicle`, etc.) son `UUID` inmutables generados en el constructor.

### 3.2. Ubicación y distancias

- `Location` — `latitude`, `longitude` (`UllGeolocationPoint`), `postalAddress`, `gisReference`.
- `Distance` — distancia en metros (≥ 0).
- `ServiceRadius` — radio de servicio.
- `RouteSequence` — orden de una parada en la ruta.

### 3.3. Demanda y capacidades

| Value Object | Unidad | Uso |
|--------------|--------|-----|
| `DailyWasteDemandLitersPerDay` | litros/día | Demanda de contenedor; nivel de llenado de instalación |
| `ContainerCapacityLiters` | litros | Capacidad del contenedor |
| `StorageCapacityKilograms` | kg | Almacenamiento de instalación |
| `ProcessingCapacityKilogramsPerDay` | kg/día | Procesamiento de instalación |
| `VehicleCapacityKilograms` | kg | Carga del vehículo |
| `VehicleCapacityLiters` | litros | Carga del vehículo |
| `CollectedWeightKilograms` | kg | Recogido en parada o ruta |
| `CollectedVolumeLiters` | litros | Recogido en parada o ruta |
| `UnloadingTime` | minutos | Descarga en instalación |

### 3.4. Enumeraciones de clasificación

| Enum | Valores |
|------|---------|
| `WasteType` | `ORGANIC`, `PACKAGING`, `PAPER_CARDBOARD`, `GLASS`, `RESIDUAL` |
| `FacilityType` | `OPERATIONAL_BASE`, `TRANSFER_STATION`, `TREATMENT_PLANT` |
| `FacilityStatus` | `CANDIDATE`, `PLANNED`, `OPEN`, `DISCARDED` |
| `VehicleType` | `COLLECTION_TRUCK`, `TRANSFER_TRUCK`, `SUPPORT_VEHICLE` |
| `ServiceZone` | `NEIGHBORHOOD`, `DISTRICT`, `GEOGRAPHICAL_AREA` |
| `StopType` | `CONTAINER`, `FACILITY` |
| `ContainerStatus` | `CORRECT`, `OVERFLOWED` |
| `InfrastructurePlanValidityState` | `VALID`, `OBSOLETE` |
| `InfrastructurePlanStatus` | `SUBOPTIMAL`, `OVERBUDGET` (estado calculado para la API) |

### 3.5. Costes

- `OpeningFixedCost` — coste fijo de apertura de instalación.
- `TransportationVariableCost` — coste por kilómetro del vehículo.
- `TotalCost` — coste total estimado del plan.
- `MaximumBudget` — presupuesto máximo permitido.
- `Currency` — moneda (cuando aplica).

### 3.6. Tiempo y horizonte

- `PlanningPeriod` — periodo de planificación (p. ej. año `2026`).
- `PlanDay` — día dentro del horizonte (entero ≥ 1).
- `ExecutedAt` — marca temporal ISO 8601 de ejecución del algoritmo.
- `NumberOfDays`, `AveragePickupTimeMinutes` — parámetros de ejecución del algoritmo.
- `ValidityPeriod` — vigencia temporal cuando aplica.

### 3.7. Políticas y restricciones

- `ServicePolicies` — agrupa restricciones opcionales:
  - `maxServiceDistance` (metros)
  - `maxServiceTime` (minutos)
  - `maxInfrastructureCount`
  - `maxEmissions` (kg CO₂)

### 3.8. Algoritmo y alertas

- `AlgorithmJsonPayload` — snapshot JSON de la petición de ejecución.
- `StopAlertType`, `StopAlertMessage`, `StopAlertValue` — componentes de `StopAlert`.

## 4. Servicios de dominio (solo mención)

No son entidades ni VOs, pero forman parte del diseño DDD:

- **Servicio de Optimización de Planificación**
  - Input: contenedores, infraestructuras candidatas, políticas, presupuesto.
  - Output: `InfrastructurePlan` válido.
- **Servicio de Cálculo de Costes**
  - Lógica del cálculo de `CosteTotal`, `CosteTransporte`, etc.
- **Servicio de Cálculo de Distancias**
  - Encapsula el acceso a GIS/road network para obtener `Distancia` y `TiempoServicio`.

## Tests REST (Postman / Newman)

La suite de tests de la API REST vive en la raíz del repositorio:

- `rest-api_tests.json` — colección Postman (v2.1) con un caso por endpoint del back-end
- `rest-api_environment.json` — entorno local (`baseUrl`, ids creados en runtime, etc.)

Los ficheros se generan con:

```bash
node scripts/generate-rest-api-postman.mjs
```

### Requisitos

- [Node.js](https://nodejs.org/) (v18 o superior recomendado)
- Dependencias del proyecto (incluye Newman **local**, sin permisos de administrador):

```bash
# Desde la raíz del repositorio (no hace falta entrar en back-end/)
npm install
```

Si prefieres instalar Newman globalmente y te sale `EACCES` en Linux/WSL, **no uses `sudo`**: instala solo en el proyecto con el comando anterior, o ejecuta `npx newman` tras `npm install`.

- Back-end y MongoDB en ejecución (por ejemplo con Docker):

```bash
docker compose --profile back-end up -d
```

### Ejecutar la suite

Desde la **raíz** del proyecto (`sensor-app/`, no `back-end/`):

```bash
npm run test:api
```

Equivalente manual:

```bash
npx newman run rest-api_tests.json -e rest-api_environment.json
```

La variable `baseUrl` apunta por defecto a `http://localhost:8080`.

### Qué cubre la colección

| Recurso | Endpoints |
|---------|-----------|
| Application overview | `GET /api/v1/application-overview` |
| Vehicles | list, create, get, update, delete + errores 400/404 |
| Containers | list, create, get, update, delete + errores 400/404 |
| Facilities | list, create, get, update, delete + errores 400/404 |
| Infrastructure plans | list, get by id, delete (404 seguro) + errores 400/404 |
| Algorithms | `POST /api/v1/algorithms/execute` (solo validación 400, sin lanzar Docker) |

Los tests de CRUD crean entidades de prueba con nombre `Newman Test … {{$timestamp}}` y las eliminan al final de cada carpeta. El test de algoritmo envía un body vacío para comprobar la validación sin ejecutar el servicio `algorithm`.

### Actualizar los tests

1. Edita `scripts/generate-rest-api-postman.mjs` si cambian rutas o contratos JSON.
2. Regenera los JSON: `npm run postman:generate`
3. Vuelve a ejecutar: `npm run test:api`