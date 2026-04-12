/**
 * Frontend Container Management - Guía de Uso
 * 
 * El frontend ha sido actualizado para soportar el nuevo sistema
 * avanzado de filtración y ordenación del backend.
 */

// ============================================================
// COMPONENTES DE FILTRO DISPONIBLES EN LA VISTA
// ============================================================

/**
 * La vista containers-view.vue ahora incluye los siguientes filtros
 * en la barra de herramientas:
 */

/*
1. WASTE TYPE (Tipo de Residuo)
   - Selector dropdown con opciones: GLASS, PLASTIC, ORGANIC, PAPER, METAL
   - Tipo: Select
   - Limpiable: Sí
   - Reacción: Se filtra automáticamente

2. SERVICE ZONE (Zona de Servicio)
   - Campo de entrada de texto libre
   - Tipo: Text Input
   - Limpiable: Sí
   - Reacción: Se filtra automáticamente

3. LOCATION (Ubicación)
   - Campo de entrada para búsqueda por dirección postal
   - Tipo: Text Input (búsqueda aproximada, case-insensitive)
   - Limpiable: Sí
   - Reacción: Se filtra automáticamente

4. MIN CAPACITY - MAX CAPACITY (Rango de Capacidad en Litros)
   - Campos numéricos para mínimo y máximo
   - Tipo: Number Input
   - Limpiables: Sí
   - Reacción: Se filtran automáticamente al cambiar cualquiera

5. MIN DEMAND - MAX DEMAND (Rango de Demanda Diaria)
   - Campos numéricos para mínimo y máximo
   - Tipo: Number Input
   - Limpiables: Sí
   - Reacción: Se filtran automáticamente al cambiar cualquiera
*/

// ============================================================
// CARACTERÍSTICAS DE ORDENAMIENTO
// ============================================================

/**
 * La tabla permite ordenar por:
 * - wasteType (Tipo de residuo)
 * - location (Ubicación/Dirección postal)
 * - capacityLiters (Capacidad en litros)
 * - demand (Demanda diaria)
 * - serviceZone (Zona de servicio)
 * 
 * Al hacer clic en el encabezado de cualquier columna,
 * se alterna entre ascendente y descendente.
 * 
 * El ordenamiento se aplica junto con los filtros actuales.
 */

// ============================================================
// EJEMPLOS DE USO
// ============================================================

/*
CASO 1: Ver todos los contenedores (sin filtros)
→ Vista se carga automáticamente
→ Muestra 10 contenedores por página (configurable)
→ Todos ordenables y filtrables

CASO 2: Filtrar por tipo de residuo
→ Seleccionar "PLASTIC" en el dropdown de Waste Type
→ La tabla se actualiza automáticamente mostrando solo plástico
→ Se puede combinar con otros filtros

CASO 3: Buscar por ubicación y capacidad
→ Escribir "Madrid" en el campo Location
→ Establecer Min Capacity: 500
→ Establecer Max Capacity: 1000
→ La tabla muestra solo contenedores en Madrid con 500-1000L

CASO 4: Combinar múltiples filtros
→ Waste Type: GLASS
→ Service Zone: Zone-A
→ Min Capacity: 250
→ Max Capacity: 750
→ Max Demand: 300
→ Resultado: Todos los contenedores de GLASS en Zone-A,
  con capacidad 250-750L y demanda máxima 300L/día

CASO 5: Filtrar + Ordenar
→ Min Demand: 100
→ Ordenar por: capacity (haciendo clic en el encabezado)
→ Resultado: Contenedores con demanda >= 100L/día,
  ordenados por capacidad ascendente
*/

// ============================================================
// FLUJO DE DATOS DESDE VISTA AL BACKEND
// ============================================================

/*
VueSFC (containers-view.vue)
    ↓
Store Actions (getContainers)
    ↓
Repository HTTP (list method)
    ↓
URL construcción con parámetros:
    GET /containers/
        ?page=0
        &size=10
        &sortBy=wasteType
        &sortOrder=asc
        &wasteType=PLASTIC
        &serviceZone=Zone-A
        &location=Madrid
        &minCapacity=500
        &maxCapacity=1000
        &minDemand=100
        &maxDemand=500
    ↓
Backend ProcessaBackend Processes Query
    ↓
MongoDB Busca y devuelve resultados paginados
*/

// ============================================================
// CAMBIOS EN EL CÓDIGO TYPESCRIPT
// ============================================================

/*
archivo: list-containers-command.ts
Cambio: Se agregaron nuevos parámetros:
- serviceZone?: string
- minCapacity?: number
- maxCapacity?: number
- minDemand?: number
- maxDemand?: number
- location?: string

archivo: container-http-repository.ts
Cambio: El método list() ahora construye URL con todos los nuevos parámetros

archivo: container-store.ts
Cambio: El método getContainers() aceptanow todos los nuevos filtros

archivo: containers-view.vue
Cambios:
- Nuevas refs para almacenar valores de filtros
- Nuevas funciones manejadoras para cada filtro
- UI mejorada con 7 inputs/selects de filtro
- Todos los filtros conectados con las llamadas al API
*/

// ============================================================
// COMPORTAMIENTO DE PAGINACIÓN Y FILTROS
// ============================================================

/*
IMPORTANTE:
- Cuando se aplica un filtro, la paginación se reinicia en página 1
- El tamaño de página se mantiene (default 10, configurable: 5, 10, 25, 50)
- Los filtros se persisten al cambiar de página (mientras en la misma tabla)
- Al ordenar, se mantienen todos los filtros activos
- Al borrar un contenedor, se recarga con los filtros actuales
- Si la página actual queda vacía tras un filtro, se retrocede una página
*/

// ============================================================
// INTERNACIONALIZACIÓN
// ============================================================

/*
Los siguientes textos están i18n-ready:
(se traducen según el idioma configurado)

- container.list.filterByWasteType (existente)
- container.list.filterByServiceZone (nuevo - agregar si no existe)
- container.list.filterByLocation (nuevo - agregar si no existe)
...

Si falta alguna traducción, aparecerá el código de la clave.
Ver archivos de traducción en: src/i18n/locales/
*/

// ============================================================
// DEBUGGING / TROUBLESHOOTING
// ============================================================

/*
Si los filtros no funcionan:
1. Verificar que el backend esté corriendo en http://VITE_APP_API_URL/containers/
2. Abrir DevTools (F12) → Network → revisar últimas peticiones a /containers/
3. Verificar que los parámetros query aparezcan en la URL
4. Revisar la consola para errores de TypeScript

Si la tabla no se actualiza:
1. Revisar en Network tab que las peticiones GET se hayan completado (200 OK)
2. Verificar en Store (DevTools Vue) que containerStore.containers tenga datos
3. Revisar que containers.value en onMounted se ejecute sin errores

Si hay errores de compilación:
1. npm run dev
2. Revisar los errores en terminal
3. Asegurarse que ListContainersCommand tenga todos los parámetros
*/

// ============================================================
// PRÓXIMAS MEJORAS SUGERIDAS
// ============================================================

/*
1. Agregardatepicker para filtros de fecha (createdAt, updatedAt)
2. Guardar filtros activos en URL (query params persistentes)
3. Agregar botón "Guardar filtro como favorito"
4. Implementar búsqueda textual en múltiples campos
5. Agregar exportación de resultados filtrados (CSV, Excel)
6. Implementar filtros avanzados con lógica AND/OR
7. Agregar gráficos de distribución de containers por tipo/zona
*/
