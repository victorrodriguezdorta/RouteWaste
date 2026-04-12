/**
 * Container API - Ejemplos de Uso
 * 
 * Esta documentación muestra la nueva API de contenedores con
 * múltiples opciones de filtrado, ordenamiento y paginación.
 */

// ============================================================
// 1️⃣ EJEMPLOS BÁSICOS
// ============================================================

// Obtener todos los contenedores (sin filtros)
GET /containers/

// Obtener con paginación (página 2, 20 elementos per página)
GET /containers/?page=1&size=20

// Ordenar por tipo de residuo ascendente
GET /containers/?sortBy=wasteType&sortOrder=asc

// Ordenar por capacidad descendente
GET /containers/?sortBy=capacity&sortOrder=desc

// ============================================================
// 2️⃣ EJEMPLOS CON FILTROS SIMPLES
// ============================================================

// Filtrar por tipo de residuo
GET /containers/?wasteType=PLASTIC

// Filtrar por zona de servicio
GET /containers/?serviceZone=Zone-A

// Filtrar por localización (búsqueda aproximada - case insensitive)
GET /containers/?location=Madrid

// ============================================================
// 3️⃣ EJEMPLOS CON RANGOS (CAPACIDAD Y DEMANDA)
// ============================================================

// Contenedores con capacidad MÍNIMA de 500 litros
GET /containers/?minCapacity=500

// Contenedores con capacidad MÁXIMA de 1000 litros
GET /containers/?maxCapacity=1000

// Contenedores con capacidad ENTRE 500 y 1000 litros
GET /containers/?minCapacity=500&maxCapacity=1000

// Contenedores con demanda diaria MÍNIMA de 100 litros/día
GET /containers/?minDemand=100

// Contenedores con demanda ENTRE 100 y 500 litros/día
GET /containers/?minDemand=100&maxDemand=500

// ============================================================
// 4️⃣ EJEMPLOS COMPLEJOS (MÚLTIPLES FILTROS)
// ============================================================

// Todos los contenedores de PLASTIC en la zona Zone-A
GET /containers/?wasteType=PLASTIC&serviceZone=Zone-A

// Contenedores GLASS en Madrid con capacidad 500-1000L
GET /containers/?wasteType=GLASS&location=Madrid&minCapacity=500&maxCapacity=1000

// Contenedores ORGANIC con demanda alta en Zone-B, ordenados por demanda DESC
GET /containers/?wasteType=ORGANIC&serviceZone=Zone-B&minDemand=200&sortBy=demand&sortOrder=desc

// ============================================================
// 5️⃣ EJEMPLOS COMPLETOS CON PAGINACIÓN Y ORDENAMIENTO
// ============================================================

// Página 2, 15 elementos, PLASTIC en Zone-A, ordenados por ubicación
GET /containers/?page=1&size=15&wasteType=PLASTIC&serviceZone=Zone-A&sortBy=location&sortOrder=asc

// Página 1, 25 elementos, capacidad 250-750L, demanda 50-300, ordenados por capacidad DESC
GET /containers/?page=0&size=25&minCapacity=250&maxCapacity=750&minDemand=50&maxDemand=300&sortBy=capacity&sortOrder=desc

// ============================================================
// 6️⃣ CAMPOS DISPONIBLES PARA ORDENAR (sortBy)
// ============================================================

// Ordenables:
// - id
// - wasteType
// - serviceZone
// - location (por dirección postal)
// - postalAddress
// - latitude
// - longitude
// - capacity (capacidad en litros)
// - capacityLiters
// - demand (demanda diaria)
// - dailyDemand
// - createdAt
// - updatedAt

// Ejemplos de ordenamiento:
GET /containers/?sortBy=id&sortOrder=asc
GET /containers/?sortBy=location&sortOrder=desc
GET /containers/?sortBy=dailyDemand&sortOrder=asc

// ============================================================
// 7️⃣ TIPOS DE RESIDUOS VÁLIDOS (Para wasteType)
// ============================================================

// GLASS (Cristal/Vidrio)
GET /containers/?wasteType=GLASS

// PLASTIC (Plástico)
GET /containers/?wasteType=PLASTIC

// ORGANIC (Orgánico)
GET /containers/?wasteType=ORGANIC

// PAPER (Papel)
GET /containers/?wasteType=PAPER

// METAL (Metal)
GET /containers/?wasteType=METAL

// ============================================================
// 8️⃣ RESPUESTA EXITOSA (HTTP 200)
// ============================================================

{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "wasteType": "PLASTIC",
      "location": {
        "postalAddress": "Calle Mayor 123, Madrid",
        "latitude": 40.4168,
        "longitude": -3.7038
      },
      "capacityLiters": {
        "liters": 750
      },
      "dailyDemandLitersPerDay": {
        "litersPerDay": 150
      },
      "serviceZone": "Zone-A"
    }
  ],
  "totalElements": 245,
  "totalPages": 17,
  "page": 0,
  "size": 15,
  "numberOfElements": 15,
  "first": true,
  "last": false
}

// ============================================================
// 9️⃣ ERRORES POSIBLES
// ============================================================

// 400 BAD_REQUEST - Page < 0 o size <= 0
GET /containers/?page=-1&size=10

// 400 BAD_REQUEST - Sort field no válido
GET /containers/?sortBy=invalidField

// 400 BAD_REQUEST - WasteType no válido
GET /containers/?wasteType=INVALID_TYPE

// ============================================================
// 🔟 PARÁMETROS POR DEFECTO
// ============================================================

// Si no especificas parámetros:
// page = 0 (primera página)
// size = 10 (10 elementos por página)
// sortOrder = asc (ascendente)
// sortBy = sin ordenamiento especial
