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
| `infrastructurePlanId` | `UUID` | No | Plan padre (legacy sin enlace) |
| `containerId` | `UUID` | Sí | Contenedor referenciado |
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

## Ejecución con Docker (stack completo)

Levanta **MongoDB**, **back-end** y **front-end** en contenedores. El front-end se compila en el host (los paquetes `@ull-tfg` de GitHub Packages no instalan bien dentro del build de Docker); el back-end puede lanzar el **algoritmo** vía el socket de Docker del host.

### Requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (o Docker Engine + Compose) en ejecución
- [Node.js](https://nodejs.org/) v18+
- Token de GitHub con permiso **`read:packages`** (paquetes `@ull-tfg` y Maven `ull-tfg-utils`)

```bash
# Desde la raíz del repositorio
npm install
cp .env.example .env
# Edita .env y define GITHUB_TOKEN=ghp_...
```

### Arrancar y parar

```bash
# Construye front-end, imágenes Docker y levanta mongo + back-end + front-end
npm run docker:full

# Para todos los contenedores del perfil full (conserva volúmenes de MongoDB)
npm run docker:full:down
```

| Script | Descripción |
|--------|-------------|
| `npm run docker:full` | Build del front-end en host + `docker compose build algorithm` + stack `full` |
| `npm run docker:full:down` | `docker compose --profile full down` |

La primera ejecución de `docker:full` puede tardar varios minutos (Maven en el back-end, descarga de imágenes).

### URLs

| Servicio | URL |
|----------|-----|
| Front-end | http://localhost |
| Back-end (API) | http://localhost:8080/api/v1/ |
| Swagger | http://localhost:8080/swagger-ui.html |
| MongoDB (host) | `mongodb://localhost:27017/db-application` |

### Notas

- Si el puerto **27017** está ocupado (MongoDB local), para el stack `full` o detén ese servicio o cambia el mapeo de puertos en `docker-compose.yml`.
- Tras cambiar código del **back-end** (p. ej. CORS o algoritmo), reconstruye: `docker compose build back-end && docker compose --profile full up -d back-end`.
- Perfil Compose: `full` (servicios `mongodb`, `back-end`, `front-end`). Imagen del algoritmo: `sensor-app_algorithm:latest` (se construye antes de levantar el stack).

Equivalente manual (sin los scripts npm):

```bash
node scripts/build-front-end-for-docker.mjs
docker compose build algorithm
FRONTEND_DOCKERFILE=Dockerfile.dist docker compose --profile full up -d --build
```

## Tests REST (Postman / Newman)

La suite de tests de la API REST vive en la raíz del repositorio:

- `rest-api_tests.json` — colección Postman (v2.1) con un caso por endpoint del back-end
- `rest-api_environment.json` — entorno de prueba (`baseUrl`, ids creados en runtime, etc.)

Los ficheros se generan con:

```bash
node scripts/generate-rest-api-postman.mjs
```

### Base de datos aislada

Los tests Newman **no deben** ejecutarse contra la base de datos de desarrollo (`db-application`). Usan un entorno dedicado:

| Recurso | Desarrollo | Newman (API test) |
|---------|------------|-------------------|
| MongoDB database | `db-application` | `db-application-api-test` |
| Back-end (host) | `http://localhost:8080` | `http://localhost:8081` |
| Perfil Spring | (por defecto) | `api-test` |
| Docker Compose | `--profile back-end` | `--profile api-test` |

Así puedes tener el back-end de desarrollo en `:8080` y el de tests en `:8081` sin mezclar datos. En Docker, el MongoDB de Newman es un servicio interno (`mongo-api-test`) y no publica el puerto `27017`, por lo que no entra en conflicto con una MongoDB local o de desarrollo.

### Requisitos

- [Node.js](https://nodejs.org/) (v18 o superior recomendado)
- Dependencias del proyecto (incluye Newman **local**, sin permisos de administrador):

```bash
# Desde la raíz del repositorio (no hace falta entrar en back-end/)
npm install
```

Si prefieres instalar Newman globalmente y te sale `EACCES` en Linux/WSL, **no uses `sudo`**: instala solo en el proyecto con el comando anterior, o ejecuta `npx newman` tras `npm install`.

- Back-end con perfil **`api-test`** (puerto **8081**). Con Docker no necesitas tener libre el puerto `27017`, porque la MongoDB de test es interna al stack.

**Opción A — Docker (recomendado):**

```bash
# 1. Compila el JAR (necesario para construir la imagen local)
cd back-end && mvn package -DskipTests && cd ..
# 2. Levanta MongoDB interna + back-end de test
docker compose --profile api-test up -d
```

Levanta `mongo-api-test` y `back-end-api-test` (imagen construida desde `./back-end/Dockerfile`) contra `db-application-api-test`. `mongo-api-test` no expone puertos al host.

> La imagen pública `kaizten/sensor-app_back-end` es privada. El perfil `api-test` construye
> la imagen localmente como `sensor-app_back-end:local`.

**Opción B — Back-end local con Maven:**

```bash
cd back-end
mvn spring-boot:run -Dspring-boot.run.profiles=api-test
```

(Requiere MongoDB en `localhost:27017`; usa `application-api-test.yml`.)

### Ejecutar la suite

Desde la **raíz** del proyecto (`sensor-app/`, no `back-end/`):

```bash
# Stack Docker + Newman + bajar stack
npm run test:api:full

# Solo Newman (con el stack api-test ya levantado)
npm run test:api
```

| Script | Descripción |
|--------|-------------|
| `npm run test:api:jar` | Compila el JAR del back-end (`mvn package -DskipTests`) |
| `npm run test:api:stack:up` | `docker compose --profile api-test up -d` |
| `npm run test:api:stack:down` | Para el stack de tests |
| `npm run test:api:wait` | Espera a que responda `:8081` |
| `npm run test:api` | Newman contra el entorno de test |
| `npm run test:api:full` | Compila JAR + sube stack + Newman + baja stack |

Equivalente manual:

```bash
npx newman run rest-api_tests.json -e rest-api_environment.json
```

La variable `baseUrl` apunta por defecto a `http://localhost:8081` (back-end con perfil `api-test`).

### Inspeccionar la base de datos de Newman

Cuando se usa Docker, Newman **sí usa MongoDB**, pero no usa la MongoDB local publicada en `localhost:27017`. Usa el contenedor interno `mongo-api-test`:

- Servicio Docker: `mongo-api-test`
- Base de datos: `db-application-api-test`
- URI desde el back-end de test: `mongodb://mongo-api-test:27017/db-application-api-test`
- Puerto en el host: ninguno (`mongo-api-test` no publica `27017`)

Por eso `db-application-api-test` no aparece en MongoDB Compass si estás conectado a `localhost:27017`: Compass está mirando tu MongoDB local/de desarrollo, no el contenedor interno de Newman.

Para inspeccionarla, deja el stack levantado y no uses `test:api:full`, porque ese script ejecuta `test:api:stack:down` al final:

```bash
npm run test:api:jar
npm run test:api:stack:up
npm run test:api
```

Después entra al contenedor de MongoDB de test:

```bash
docker exec -it mongo-api-test mongosh
```

Dentro de `mongosh`:

```javascript
show dbs
use db-application-api-test
show collections
```

Ten en cuenta que los tests CRUD crean datos de prueba y los eliminan al final de cada carpeta. Si la base queda sin documentos, MongoDB puede no mostrarla en `show dbs`, aunque el back-end sí haya usado `mongo-api-test` durante la ejecución.

Cuando termines de inspeccionar:

```bash
npm run test:api:stack:down
```

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

## Front-end E2E tests (Playwright)

UI tests live in `front-end/e2e/` and run from the **terminal** (the Playwright UI is optional).

### Requirements

- Node.js in `front-end/`
- Chromium browser (one-time install):

```bash
cd front-end
npm install
npx playwright install chromium
```

- For API-dependent tests: isolated api-test stack (`docker compose --profile api-test up -d`), same as Newman — **not** the dev back-end on `:8080`.

E2E tests write to **`db-application-api-test`** (MongoDB inside `mongo-api-test`), not `db-application`.

### Run

From the **repo root** (waits for `:8081`, then runs Playwright):

```bash
docker compose --profile api-test up -d
npm run test:e2e
```

Or full cycle (build JAR, up stack, e2e, down stack):

```bash
npm run test:e2e:full
```

From `front-end/` only (stack must already be up on `:8081`):

```bash
cd front-end
npm run test:e2e
```

Useful commands:

| Command | Description |
|---------|-------------|
| `npm run test:e2e` (root) | Wait for `:8081` + full Playwright suite |
| `npm run test:e2e:full` (root) | JAR + api-test stack + e2e + stack down |
| `npm run test:e2e` (`front-end/`) | Full suite (starts Vite e2e on `:5174` → API `:8081`) |
| `npm run test:e2e:headed` | Same, with a visible browser |
| `npm run test:e2e:ui` | Interactive mode (explore and debug tests) |
| `npm run test:e2e:report` | Open the HTML report from the last run |

The API URL for e2e is `http://localhost:8081/api/v1/` (`front-end/.env.e2e`, `playwright-config.ts`). Dev UI uses `front-end/.env.development` (`8080` / `db-application`).

Test source code (names, comments, messages) is in **English**. UI assertions use **Spanish** labels because the app defaults to `es`.

### Suite coverage (40 tests)

| File | Coverage |
|------|----------|
| `e2e/home-navigation-end-to-end-spec.ts` | Home cards, side menu links |
| `e2e/not-found-end-to-end-spec.ts` | 404 page and return home |
| `e2e/vehicles-end-to-end-spec.ts` | List, headers, create, cancel, validation, view, edit, delete |
| `e2e/containers-end-to-end-spec.ts` | Same flows as vehicles |
| `e2e/facilities-end-to-end-spec.ts` | Same flows as vehicles |
| `e2e/algorithm-end-to-end-spec.ts` | Plans list, headers, execution wizard, API table load |

Tests that create data via API (view, edit, delete, algorithm table) are skipped when the api-test back-end is not available at `localhost:8081`. Smoke tests (navigation, forms, validation) do not require the API.