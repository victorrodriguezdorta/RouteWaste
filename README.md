# Como ejecutar mongo db en docker para que funcione
docker run -d --name mongo-tfg -p 27017:27017 -e MONGO_INITDB_DATABASE=sensor-app mongo:6 


# Dominio del problema

- Dónde localizar infraestructuras (bases, estaciones de transferencia, plantas, etc.).
- Qué contenedores atiende cada infraestructura.
- Cómo se comportan costes, capacidades y niveles de servicio.

## 2. Entidades (Entities)

### 2.1. Contenedor (`Container`)

Representa un punto físico de recogida de residuos (contenedor de calle, punto de aportación, etc.).

**Atributos (principalmente VOs):**

- `ContenedorId`
- `Ubicacion`
- `TipoResiduo`
- `DemandaResiduos`
- `ZonaServicio` (opcional)

**Reglas:**

- En un plan concreto, cada contenedor debe estar asignado exactamente a **una** infraestructura de servicio.
- Solo puede asignarse a infraestructuras que cumplan restricciones de distancia/tiempo/servicio.

### 2.2. Infraestructura (`Facility`)

Infraestructura candidata o activa:

- Base operativa de camiones.
- Estación de transferencia.
- Planta de tratamiento o clasificación.

**Atributos:**

- `InfraestructuraId`
- `TipoInfraestructura`
- `Ubicacion`
- `Capacidad`
- `CosteFijoApertura`
- `EstadoInfraestructura`  
  (por ejemplo: `Candidata`, `Planificada`, `Abierta`, `Descartada`)

**Invariantes del agregado:**

- La suma de `DemandaResiduos` de los contenedores asignados no puede superar su `Capacidad`.
- No se permiten asignaciones a una infraestructura si `EstadoInfraestructura` no es compatible (ej. no asignar si está `Descartada`).
- Las aperturas deben respetar las restricciones de presupuesto definidas en el plan.

### 2.3. Plan de Infraestructuras (`InfrastructurePlan`)

Representa una decisión completa de planificación para un horizonte temporal.

**Atributos:**

- `PlanId`
- `PeriodoPlanificacion`
- Colección de `Infraestructura` seleccionadas (entidades o referencias).
- Colección de `AsignacionServicio` (una por contenedor).
- `PoliticasServicio`
- `PresupuestoMaximo`
- `CosteTotalEstimado` (derivado o calculado)

**Comportamientos / invariantes:**

- `CosteTotalEstimado` ≤ `PresupuestoMaximo`.
- Cada contenedor del ámbito del plan tiene exactamente **una** asignación válida.
- El plan cumple las `PoliticasServicio` (distancias máximas, tiempos máximos, etc.).

**Ejemplos de métodos de dominio:**

- `AsignarContenedorAInfraestructura(ContenedorId, InfraestructuraId)`
- `RecalcularCosteTotal()`
- `EsPlanValido()`

### 2.4. Asignación de Servicio (`ServiceAssignment`)

**Atributos:**

- `ContenedorId`
- `InfraestructuraId`
- `DemandaResiduos` (relevante para esta asignación)
- `Distancia`
- `TiempoServicio`
- `CosteTransporte`

**Reglas:**

- Debe respetar la distancia/tiempo máximo establecidos en `PoliticasServicio`.
- Su inclusión no debe provocar que la infraestructura supere su `Capacidad`.

### 2.5. Vehículo (`Vehicle`)

**Atributos:**

- `VehiculoId`
- `TipoVehiculo`
- `CapacidadVehiculo`
- `CosteOperacionPorKm`

## 3. Objetos de Valor (Value Objects)

### 3.1. Identificadores

- `ContenedorId`
- `InfraestructuraId`
- `PlanId`
- `VehiculoId` (si aplica)

Son VOs, típicamente representados por UUIDs (`UllUUID`). Se definen solo por su valor.

### 3.2. Ubicación y distancias

- `Ubicacion`
  - Coordenadas (lat/lon), dirección postal, referencia GIS, etc.
- `Distancia`
- `TiempoServicio`
- `RadioServicio`

**Reglas:**

- `Distancia` ≥ 0.
- `TiempoServicio` ≥ 0.

### 3.3. Demanda y capacidades

- `DemandaResiduos`
  - Por ejemplo, toneladas/día o toneladas/año.
- `Capacidad`
  - Capacidad máxima de tratamiento o gestión de una infraestructura.

**Reglas:**

- No pueden ser negativas.
- Pueden incorporar la unidad de medida como parte del VO.

### 3.4. Tipos y clasificación

- `TipoInfraestructura`
  - Ej.: `BaseOperativa`, `EstacionTransferencia`, `PlantaTratamiento`.
- `TipoResiduo`
  - Ej.: `Organico`, `Envases`, `PapelCarton`, `Vidrio`, `Resto`.
- `ZonaServicio`
  - Barrio, distrito, área geográfica, etc.

Normalmente se modelan como enums enriquecidos o VOs con lógica simple.

### 3.5. Costes

- `CosteFijoApertura`
- `CosteVariableTransporte`
- `CosteTotal`
- `PresupuestoMaximo`

Pueden incluir:

- Moneda.
- Reglas de redondeo.
- Operaciones de suma/comparación (`+`, `-`, `>`, `<`).

### 3.6. Tiempo y horizonte

- `PeriodoPlanificacion`
  - Ej.: año 2026, `2026-Q1`, etc.
- `FechaVigencia`
  - Desde cuándo hasta cuándo es válido un plan o una infraestructura.

### 3.7. Políticas y restricciones

- `PoliticasServicio`
  - `DistanciaMaximaServicio`
  - `TiempoMaximoServicio`
  - Otras políticas: número máximo de infraestructuras, límites de emisiones, etc.

Es un VO que agrupa parámetros coherentes de política de servicio.

## 4. Servicios de Dominio (solo mención)

No son entidades ni VOs, pero forman parte del diseño DDD:

- **Servicio de Optimización de Planificación**
  - Input: contenedores, infraestructuras candidatas, políticas, presupuesto.
  - Output: `InfrastructurePlan` válido.
- **Servicio de Cálculo de Costes**
  - Lógica del cálculo de `CosteTotal`, `CosteTransporte`, etc.
- **Servicio de Cálculo de Distancias**
  - Encapsula el acceso a GIS/road network para obtener `Distancia` y `TiempoServicio`.