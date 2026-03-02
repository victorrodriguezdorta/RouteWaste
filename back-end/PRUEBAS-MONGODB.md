# Guía para Probar la Persistencia en MongoDB

Esta guía te ayudará a probar cómo se persisten las entidades en MongoDB utilizando los WritingConverters implementados.

## Prerrequisitos

1. **MongoDB debe estar ejecutándose**
   - Si tienes MongoDB instalado localmente, inícialo con:
     ```powershell
     # Opción 1: Si MongoDB está como servicio
     net start MongoDB
     
     # Opción 2: Si MongoDB no está como servicio
     "C:\Program Files\MongoDB\Server\<version>\bin\mongod.exe" --dbpath="C:\data\db"
     ```
   
   - Si usas Docker:
     ```powershell
     docker run -d -p 27017:27017 --name mongodb mongo:latest
     ```

2. **Herramientas para consultar MongoDB** (elige una):
   - **MongoDB Compass** (GUI): https://www.mongodb.com/products/compass
   - **mongosh** (CLI): Viene con la instalación de MongoDB

## Paso 1: Iniciar la Aplicación Spring Boot

Ejecuta la aplicación desde la carpeta `back-end`:

```powershell
cd C:\Users\vdort\Desktop\UNIVERSIDAD\TFG\sensor-app\back-end
mvn spring-boot:run
```

La aplicación:
1. Se conectará a MongoDB (`mongodb://localhost:27017/db-application`)
2. Ejecutará automáticamente `DataInitializer` que guardará:
   - Un **Container** de prueba (residuos orgánicos en La Laguna)
   - Un **Facility** de prueba (planta de tratamiento en Santa Cruz)

En los logs verás algo como:
```
INFO  DataInitializer - Initializing database with test data...
INFO  DataInitializer - Test Container saved with ID: 12345678-1234-1234-1234-123456789abc
INFO  DataInitializer - Test Facility saved with ID: 87654321-4321-4321-4321-cba987654321
INFO  DataInitializer - Database initialization completed.
```

## Paso 2: Verificar los Datos en MongoDB

### Opción A: Usando MongoDB Compass (Recomendado)

1. Abre MongoDB Compass
2. Conéctate a: `mongodb://localhost:27017`
3. Selecciona la base de datos: `db-application`
4. Explora las colecciones:
   - `containers`: Verás 1 documento con el container de prueba
   - `facilities`: Verás 1 documento con el facility de prueba

### Opción B: Usando mongosh (Terminal)

```powershell
# Conectar a MongoDB
mongosh

# Seleccionar la base de datos
use db-application

# Ver todas las colecciones
show collections

# Ver el container guardado
db.containers.find().pretty()

# Ver el facility guardado
db.facilities.find().pretty()
```

## Paso 3: Analizar la Estructura de los Documentos

### Estructura esperada del Container:

```json
{
  "_id": "UUID aquí",
  "location": {
    "latitude": 28.4682,
    "longitude": -16.2546,
    "postalAddress": "Calle La Noria 123, La Laguna, Tenerife",
    "gisReference": "GIS-REF-001"
  },
  "wasteType": "ORGANIC",
  "wasteDemand": {
    "value": 5.5,
    "quantityUnit": "tons",
    "timeUnit": "DAYS"
  },
  "serviceZone": "NEIGHBORHOOD"
}
```

### Estructura esperada del Facility:

```json
{
  "_id": "UUID aquí",
  "facilityType": "TREATMENT_PLANT",
  "location": {
    "latitude": 28.4850,
    "longitude": -16.3150,
    "postalAddress": "Poligono Industrial, Santa Cruz de Tenerife",
    "gisReference": "GIS-REF-FAC-001"
  },
  "capacity": {
    "value": 100.0,
    "quantityUnit": "tons",
    "timeUnit": "DAYS"
  },
  "openingFixedCost": {
    "amount": 50000.00,
    "currency": "EUR"
  },
  "status": "OPEN",
  "assignedWasteDemand": null
}
```

## Verificaciones Importantes

✅ **Value Objects descompuestos correctamente**:
- `Location` → objeto anidado con latitude, longitude, postalAddress, gisReference
- `WasteDemand` → objeto anidado con value, quantityUnit, timeUnit
- `Capacity` → objeto anidado con value, quantityUnit, timeUnit
- `OpeningFixedCost` → objeto anidado con amount, currency

✅ **Enums convertidos a String**:
- `WasteType.ORGANIC` → "ORGANIC"
- `ServiceZone.NEIGHBORHOOD` → "NEIGHBORHOOD"
- `FacilityType.TREATMENT_PLANT` → "TREATMENT_PLANT"
- `FacilityStatus.OPEN` → "OPEN"

✅ **Campos opcionales**:
- Si `serviceZone` es null, no aparece en el documento
- Si `assignedWasteDemand` es null, aparece como null o no aparece

## Probar otras entidades

Para probar otras entidades (Vehicle, ServiceAssignment, InfrastructurePlan), puedes:

1. **Modificar `DataInitializer.java`** añadiendo métodos similares:
   - `createTestVehicle()`
   - `createTestServiceAssignment()`
   - `createTestInfrastructurePlan()`

2. **Crear un Controller REST** para guardar entidades mediante HTTP requests

3. **Crear Unit Tests** con `@SpringBootTest` y `@DataMongoTest`

## Limpiar los datos de prueba

Para limpiar la base de datos:

```powershell
# Usando mongosh
mongosh
use db-application
db.containers.deleteMany({})
db.facilities.deleteMany({})
```

O directamente:
```powershell
mongosh db-application --eval "db.dropDatabase()"
```

## Troubleshooting

**Problema**: `Connection refused to localhost:27017`
- **Solución**: MongoDB no está ejecutándose. Inicia MongoDB primero.

**Problema**: Los datos no aparecen en MongoDB
- **Solución**: Revisa los logs de la aplicación. Puede haber errores de validación o de conversión.

**Problema**: Error de compilación
- **Solución**: Ejecuta `mvn clean compile` para verificar errores.

**Problema**: La aplicación no arranca
- **Solución**: Verifica que el puerto 8080 no esté ocupado y que MongoDB esté accesible.
