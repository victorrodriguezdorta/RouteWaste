package es.ull.project.adapter.rest.deserialization.serviceassignment;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import es.ull.project.adapter.rest.deserialization.JsonFields;
import es.ull.project.adapter.rest.request.serviceassignment.ServiceAssignmentPutRequestBody;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.demand.QuantityUnit;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.location.ServiceTime;

/**
 * ServiceAssignmentPutRequestBodyDeserializer
 * 
 * Custom JSON deserializer for ServiceAssignmentPutRequestBody.
 * This class converts incoming JSON from PUT requests into ServiceAssignmentPutRequestBody
 * objects, performing validation and constructing required value objects and entities from
 * primitive JSON fields.
 * 
 * The deserializer validates each attribute and provides meaningful error messages
 * when validation fails. It handles nested entities (Container, Facility) and value objects
 * by extracting their constituent parts from the JSON structure.
 */
public class ServiceAssignmentPutRequestBodyDeserializer extends JsonDeserializer<ServiceAssignmentPutRequestBody> {

    /**
     * Deserializes JSON content into a ServiceAssignmentPutRequestBody object.
     * 
     * @param parser the JSON parser
     * @param context the deserialization context
     * @return the deserialized ServiceAssignmentPutRequestBody
     * @throws IOException if parsing or validation fails
     */
    @Override
    public ServiceAssignmentPutRequestBody deserialize(JsonParser parser, DeserializationContext context) 
            throws IOException {
        
        JsonNode rootNode = parser.getCodec().readTree(parser);
        
        try {
            // Parse container
            Container container = parseContainer(rootNode);
            
            // Parse facility
            Facility facility = parseFacility(rootNode);
            
            // Parse wasteDemand
            WasteDemand wasteDemand = parseWasteDemand(rootNode);
            
            // Parse distance
            Distance distance = parseDistance(rootNode);
            
            // Parse serviceTime
            ServiceTime serviceTime = parseServiceTime(rootNode);
            
            // Parse transportCost
            TransportationVariableCost transportCost = parseTransportCost(rootNode);
            
            // Create and populate request body
            ServiceAssignmentPutRequestBody requestBody = new ServiceAssignmentPutRequestBody();
            requestBody.container = container;
            requestBody.facility = facility;
            requestBody.wasteDemand = wasteDemand;
            requestBody.distance = distance;
            requestBody.serviceTime = serviceTime;
            requestBody.transportCost = transportCost;
            
            return requestBody;
            
        } catch (Exception e) {
            throw new IOException("Failed to deserialize ServiceAssignmentPutRequestBody: " + e.getMessage(), e);
        }
    }

    /**
     * Parses the container nested entity from JSON.
     * 
     * @param rootNode the root JSON node
     * @return the parsed Container entity
     * @throws IllegalArgumentException if the field is missing or invalid
     */
    private Container parseContainer(JsonNode rootNode) {
        if (!rootNode.has(JsonFields.CONTAINER)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.CONTAINER + "' is missing");
        }
        
        JsonNode containerNode = rootNode.get(JsonFields.CONTAINER);
        if (containerNode.isNull() || !containerNode.isObject()) {
            throw new IllegalArgumentException("Field '" + JsonFields.CONTAINER + "' must be a non-null object");
        }
        
        try {
            // Parse container location
            Location location = parseLocationFromNode(containerNode, JsonFields.LOCATION);
            
            // Parse container wasteType
            WasteType wasteType = parseWasteTypeFromNode(containerNode);
            
            // Parse container wasteDemand
            WasteDemand wasteDemand = parseWasteDemandFromNode(containerNode, JsonFields.WASTE_DEMAND);
            
            // Parse container serviceZone (optional)
            ServiceZone serviceZone = null;
            if (containerNode.has(JsonFields.SERVICE_ZONE) && !containerNode.get(JsonFields.SERVICE_ZONE).isNull()) {
                String serviceZoneStr = containerNode.get(JsonFields.SERVICE_ZONE).asText();
                serviceZone = ServiceZone.fromString(serviceZoneStr);
            }
            
            return new Container(location, wasteType, wasteDemand, serviceZone);
            
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value for field '" + JsonFields.CONTAINER + "': " + e.getMessage(), e);
        }
    }

    /**
     * Parses the facility nested entity from JSON.
     * 
     * @param rootNode the root JSON node
     * @return the parsed Facility entity
     * @throws IllegalArgumentException if the field is missing or invalid
     */
    private Facility parseFacility(JsonNode rootNode) {
        if (!rootNode.has(JsonFields.FACILITY)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.FACILITY + "' is missing");
        }
        
        JsonNode facilityNode = rootNode.get(JsonFields.FACILITY);
        if (facilityNode.isNull() || !facilityNode.isObject()) {
            throw new IllegalArgumentException("Field '" + JsonFields.FACILITY + "' must be a non-null object");
        }
        
        try {
            // Parse facility type
            if (!facilityNode.has(JsonFields.FACILITY_TYPE)) {
                throw new IllegalArgumentException("Required field '" + JsonFields.FACILITY_TYPE + "' is missing in facility");
            }
            String facilityTypeStr = facilityNode.get(JsonFields.FACILITY_TYPE).asText();
            FacilityType facilityType = FacilityType.fromString(facilityTypeStr);
            
            // Parse facility location
            Location location = parseLocationFromNode(facilityNode, JsonFields.LOCATION);
            
            // Parse facility capacity
            Capacity capacity = parseCapacityFromNode(facilityNode, JsonFields.CAPACITY);
            
            // Parse facility opening fixed cost
            OpeningFixedCost openingFixedCost = parseOpeningFixedCostFromNode(facilityNode);
            
            // Parse facility status
            if (!facilityNode.has(JsonFields.STATUS)) {
                throw new IllegalArgumentException("Required field '" + JsonFields.STATUS + "' is missing in facility");
            }
            String statusStr = facilityNode.get(JsonFields.STATUS).asText();
            FacilityStatus status = FacilityStatus.fromString(statusStr);
            
            return new Facility(facilityType, location, capacity, openingFixedCost, status);
            
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value for field '" + JsonFields.FACILITY + "': " + e.getMessage(), e);
        }
    }

    /**
     * Parses a Location value object from a parent node.
     * 
     * @param parentNode the parent JSON node containing the location
     * @param fieldName the name of the location field
     * @return the parsed Location
     */
    private Location parseLocationFromNode(JsonNode parentNode, String fieldName) {
        if (!parentNode.has(fieldName)) {
            throw new IllegalArgumentException("Required field '" + fieldName + "' is missing");
        }
        
        JsonNode locationNode = parentNode.get(fieldName);
        if (locationNode.isNull() || !locationNode.isObject()) {
            throw new IllegalArgumentException("Field '" + fieldName + "' must be a non-null object");
        }
        
        double latitude = locationNode.get(JsonFields.LATITUDE).asDouble();
        double longitude = locationNode.get(JsonFields.LONGITUDE).asDouble();
        
        String postalAddress = null;
        if (locationNode.has(JsonFields.POSTAL_ADDRESS) && !locationNode.get(JsonFields.POSTAL_ADDRESS).isNull()) {
            postalAddress = locationNode.get(JsonFields.POSTAL_ADDRESS).asText();
        }
        
        String gisReference = null;
        if (locationNode.has(JsonFields.GIS_REFERENCE) && !locationNode.get(JsonFields.GIS_REFERENCE).isNull()) {
            gisReference = locationNode.get(JsonFields.GIS_REFERENCE).asText();
        }
        
        return new Location(latitude, longitude, postalAddress, gisReference);
    }

    /**
     * Parses a WasteType enum from a parent node.
     * 
     * @param parentNode the parent JSON node containing the waste type
     * @return the parsed WasteType
     */
    private WasteType parseWasteTypeFromNode(JsonNode parentNode) {
        if (!parentNode.has(JsonFields.WASTE_TYPE)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.WASTE_TYPE + "' is missing");
        }
        String wasteTypeStr = parentNode.get(JsonFields.WASTE_TYPE).asText();
        return WasteType.fromString(wasteTypeStr);
    }

    /**
     * Parses a WasteDemand value object from a parent node.
     * 
     * @param parentNode the parent JSON node containing the waste demand
     * @param fieldName the name of the waste demand field
     * @return the parsed WasteDemand
     */
    private WasteDemand parseWasteDemandFromNode(JsonNode parentNode, String fieldName) {
        if (!parentNode.has(fieldName)) {
            throw new IllegalArgumentException("Required field '" + fieldName + "' is missing");
        }
        
        JsonNode demandNode = parentNode.get(fieldName);
        if (demandNode.isNull() || !demandNode.isObject()) {
            throw new IllegalArgumentException("Field '" + fieldName + "' must be a non-null object");
        }
        
        double value = demandNode.get(JsonFields.CAPACITY_VALUE).asDouble();
        String quantityUnitStr = demandNode.get(JsonFields.QUANTITY_UNIT).asText();
        QuantityUnit quantityUnit = new QuantityUnit(quantityUnitStr);
        String timeUnitStr = demandNode.get(JsonFields.TIME_UNIT).asText();
        TimeUnit timeUnit = TimeUnit.valueOf(timeUnitStr.toUpperCase());
        
        return new WasteDemand(value, quantityUnit, timeUnit);
    }

    /**
     * Parses a Capacity value object from a parent node.
     * 
     * @param parentNode the parent JSON node containing the capacity
     * @param fieldName the name of the capacity field
     * @return the parsed Capacity
     */
    private Capacity parseCapacityFromNode(JsonNode parentNode, String fieldName) {
        if (!parentNode.has(fieldName)) {
            throw new IllegalArgumentException("Required field '" + fieldName + "' is missing");
        }
        
        JsonNode capacityNode = parentNode.get(fieldName);
        if (capacityNode.isNull() || !capacityNode.isObject()) {
            throw new IllegalArgumentException("Field '" + fieldName + "' must be a non-null object");
        }
        
        double value = capacityNode.get(JsonFields.CAPACITY_VALUE).asDouble();
        String quantityUnitStr = capacityNode.get(JsonFields.QUANTITY_UNIT).asText();
        QuantityUnit quantityUnit = new QuantityUnit(quantityUnitStr);
        String timeUnitStr = capacityNode.get(JsonFields.TIME_UNIT).asText();
        TimeUnit timeUnit = TimeUnit.valueOf(timeUnitStr.toUpperCase());
        
        return new Capacity(value, quantityUnit, timeUnit);
    }

    /**
     * Parses an OpeningFixedCost value object from a parent node.
     * 
     * @param parentNode the parent JSON node containing the opening fixed cost
     * @return the parsed OpeningFixedCost
     */
    private OpeningFixedCost parseOpeningFixedCostFromNode(JsonNode parentNode) {
        if (!parentNode.has(JsonFields.OPENING_FIXED_COST)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.OPENING_FIXED_COST + "' is missing");
        }
        
        JsonNode costNode = parentNode.get(JsonFields.OPENING_FIXED_COST);
        if (costNode.isNull() || !costNode.isObject()) {
            throw new IllegalArgumentException("Field '" + JsonFields.OPENING_FIXED_COST + "' must be a non-null object");
        }
        
        double amount = costNode.get(JsonFields.AMOUNT).asDouble();
        
        String currencyCode = null;
        if (costNode.has(JsonFields.CURRENCY) && !costNode.get(JsonFields.CURRENCY).isNull()) {
            currencyCode = costNode.get(JsonFields.CURRENCY).asText();
        }
        
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            return new OpeningFixedCost(amount);
        } else {
            Currency currency = new Currency(currencyCode);
            return new OpeningFixedCost(amount, currency);
        }
    }

    /**
     * Parses the wasteDemand field from the root node.
     * 
     * @param rootNode the root JSON node
     * @return the parsed WasteDemand value object
     */
    private WasteDemand parseWasteDemand(JsonNode rootNode) {
        return parseWasteDemandFromNode(rootNode, JsonFields.WASTE_DEMAND);
    }

    /**
     * Parses the distance field from JSON.
     * 
     * @param rootNode the root JSON node
     * @return the parsed Distance value object
     * @throws IllegalArgumentException if the field is missing or invalid
     */
    private Distance parseDistance(JsonNode rootNode) {
        if (!rootNode.has(JsonFields.DISTANCE)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.DISTANCE + "' is missing");
        }
        
        JsonNode distanceNode = rootNode.get(JsonFields.DISTANCE);
        if (distanceNode.isNull() || !distanceNode.isObject()) {
            throw new IllegalArgumentException("Field '" + JsonFields.DISTANCE + "' must be a non-null object");
        }
        
        try {
            // Check which unit is provided (meters, kilometers, or miles)
            if (distanceNode.has(JsonFields.METERS)) {
                double meters = distanceNode.get(JsonFields.METERS).asDouble();
                return Distance.fromMeters(meters);
            } else if (distanceNode.has(JsonFields.KILOMETERS)) {
                double kilometers = distanceNode.get(JsonFields.KILOMETERS).asDouble();
                return Distance.fromKilometers(kilometers);
            } else if (distanceNode.has(JsonFields.MILES)) {
                double miles = distanceNode.get(JsonFields.MILES).asDouble();
                return Distance.fromMiles(miles);
            } else {
                throw new IllegalArgumentException("Distance must specify one of: meters, kilometers, or miles");
            }
            
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value for field '" + JsonFields.DISTANCE + "': " + e.getMessage(), e);
        }
    }

    /**
     * Parses the serviceTime field from JSON.
     * 
     * @param rootNode the root JSON node
     * @return the parsed ServiceTime value object
     * @throws IllegalArgumentException if the field is missing or invalid
     */
    private ServiceTime parseServiceTime(JsonNode rootNode) {
        if (!rootNode.has(JsonFields.SERVICE_TIME)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.SERVICE_TIME + "' is missing");
        }
        
        JsonNode timeNode = rootNode.get(JsonFields.SERVICE_TIME);
        if (timeNode.isNull() || !timeNode.isObject()) {
            throw new IllegalArgumentException("Field '" + JsonFields.SERVICE_TIME + "' must be a non-null object");
        }
        
        try {
            // Check which unit is provided (minutes, hours, or seconds)
            if (timeNode.has(JsonFields.MINUTES)) {
                double minutes = timeNode.get(JsonFields.MINUTES).asDouble();
                return new ServiceTime(minutes);
            } else if (timeNode.has(JsonFields.HOURS)) {
                double hours = timeNode.get(JsonFields.HOURS).asDouble();
                return ServiceTime.fromHours(hours);
            } else if (timeNode.has(JsonFields.SECONDS)) {
                double seconds = timeNode.get(JsonFields.SECONDS).asDouble();
                return ServiceTime.fromSeconds(seconds);
            } else {
                throw new IllegalArgumentException("ServiceTime must specify one of: minutes, hours, or seconds");
            }
            
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value for field '" + JsonFields.SERVICE_TIME + "': " + e.getMessage(), e);
        }
    }

    /**
     * Parses the transportCost field from JSON.
     * 
     * @param rootNode the root JSON node
     * @return the parsed TransportationVariableCost value object
     * @throws IllegalArgumentException if the field is missing or invalid
     */
    private TransportationVariableCost parseTransportCost(JsonNode rootNode) {
        if (!rootNode.has(JsonFields.TRANSPORT_COST)) {
            throw new IllegalArgumentException("Required field '" + JsonFields.TRANSPORT_COST + "' is missing");
        }
        
        JsonNode costNode = rootNode.get(JsonFields.TRANSPORT_COST);
        if (costNode.isNull() || !costNode.isObject()) {
            throw new IllegalArgumentException("Field '" + JsonFields.TRANSPORT_COST + "' must be a non-null object");
        }
        
        try {
            double amount = costNode.get(JsonFields.AMOUNT).asDouble();
            
            String currencyCode = null;
            if (costNode.has(JsonFields.CURRENCY) && !costNode.get(JsonFields.CURRENCY).isNull()) {
                currencyCode = costNode.get(JsonFields.CURRENCY).asText();
            }
            
            if (currencyCode == null || currencyCode.trim().isEmpty()) {
                return new TransportationVariableCost(amount);
            } else {
                Currency currency = new Currency(currencyCode);
                return new TransportationVariableCost(amount, currency);
            }
            
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Invalid value for field '" + JsonFields.TRANSPORT_COST + "': " + e.getMessage(), e);
        }
    }
}
