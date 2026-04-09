package es.ull.project.adapter.mongodb.reader;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.configuration.MongoConfiguration;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.ServiceAssignment;
import es.ull.project.domain.enumerate.TimeUnit;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.QuantityUnit;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.ServiceTime;
import java.util.UUID;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

/**
 * ServiceAssignmentReadingConverter
 *
 * Implements custom conversion logic for transforming MongoDB documents
 * into ServiceAssignment entities. This converter handles the deserialization of
 * MongoDB documents including their entity references (Container, Facility) by
 * loading them from their respective repositories, and their nested value objects
 * (WasteDemand, Distance, ServiceTime, TransportationVariableCost) into a properly
 * constructed ServiceAssignment domain entity.
 */
@ReadingConverter
public class ServiceAssignmentReadingConverter implements Converter<Document, ServiceAssignment> {

    private static final Logger logger = LoggerFactory.getLogger(ServiceAssignmentReadingConverter.class);

    private MongoConfiguration mongoConfiguration;

    /**
     * Constructor with MongoConfiguration dependency.
     *
     * @param mongoConfiguration MongoDB configuration for accessing repositories
     */
    public ServiceAssignmentReadingConverter(MongoConfiguration mongoConfiguration) {
        this.mongoConfiguration = mongoConfiguration;
    }

    /**
     * Converts a MongoDB Document into a ServiceAssignment entity.
     *
     * @param document MongoDB Document to convert
     * @return ServiceAssignment entity reconstructed from the document
     */
    @Override
    public ServiceAssignment convert(@NonNull Document document) {
        logger.info("ServiceAssignment to read from document '{}'", document);
        UUID id = (UUID) document.get(MongoFields.ID);
        UUID containerId = (UUID) document.get(MongoFields.CONTAINER_ID);
        Container container = this.mongoConfiguration.containerRepository()
                .findById(containerId)
                .orElseThrow(() -> new IllegalStateException("Container with id " + containerId + " not found"));
        UUID facilityId = (UUID) document.get(MongoFields.FACILITY_ID);
        Facility facility = this.mongoConfiguration.facilityRepository()
                .findById(facilityId)
                .orElseThrow(() -> new IllegalStateException("Facility with id " + facilityId + " not found"));
        Document wasteDemandDocument = (Document) document.get(MongoFields.WASTE_DEMAND);
        double wasteDemandValue = wasteDemandDocument.getDouble(MongoFields.WASTE_DEMAND_VALUE);
        String wasteDemandQuantityUnitValue = wasteDemandDocument.getString(MongoFields.WASTE_DEMAND_QUANTITY_UNIT);
        QuantityUnit wasteDemandQuantityUnit = new QuantityUnit(wasteDemandQuantityUnitValue);
        String wasteDemandTimeUnitString = wasteDemandDocument.getString(MongoFields.WASTE_DEMAND_TIME_UNIT);
        TimeUnit wasteDemandTimeUnit = TimeUnit.fromString(wasteDemandTimeUnitString);
        WasteDemand wasteDemand = new WasteDemand(wasteDemandValue, wasteDemandQuantityUnit, wasteDemandTimeUnit);
        double distanceValue = document.getDouble(MongoFields.DISTANCE);
        Distance distance = Distance.fromMeters(distanceValue);
        double serviceTimeValue = document.getDouble(MongoFields.SERVICE_TIME);
        ServiceTime serviceTime = new ServiceTime(serviceTimeValue);
        Document transportCostDocument = (Document) document.get(MongoFields.TRANSPORT_COST);
        double transportCostAmount = transportCostDocument.getDouble(MongoFields.TRANSPORT_COST_AMOUNT);
        TransportationVariableCost transportCost;
        if (transportCostDocument.containsKey(MongoFields.TRANSPORT_COST_CURRENCY)) {
            String currencyCode = transportCostDocument.getString(MongoFields.TRANSPORT_COST_CURRENCY);
            Currency currency = new Currency(currencyCode);
            transportCost = new TransportationVariableCost(transportCostAmount, currency);
        } else {
            transportCost = new TransportationVariableCost(transportCostAmount);
        }
        ServiceAssignment serviceAssignment = new ServiceAssignment(
                id,
                container,
                facility,
                wasteDemand,
                distance,
                serviceTime,
                transportCost);
        return serviceAssignment;
    }
}
