package es.ull.project.adapter.mongodb.reader;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.configuration.MongoConfiguration;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.DailyPlan;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.InfrastructurePlan;
import es.ull.project.domain.entity.Stop;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.capacity.CollectedVolumeLiters;
import es.ull.project.domain.valueobject.capacity.CollectedWeightKilograms;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.capacity.ProcessingCapacityKilogramsPerDay;
import es.ull.project.domain.valueobject.capacity.StorageCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.UnloadingTime;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.MaximumBudget;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.cost.TotalCost;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Distance;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.route.RouteSequence;
import es.ull.project.domain.valueobject.time.PlanningPeriod;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

/**
 * DailyPlanReadingConverter
 *
 * Implements custom conversion logic for transforming MongoDB documents
 * into DailyPlan entities, including their nested facility, vehicle,
 * stop, and container snapshots.
 */
@ReadingConverter
public class DailyPlanReadingConverter implements Converter<Document, DailyPlan> {

    private static final Logger logger = LoggerFactory.getLogger(DailyPlanReadingConverter.class);
    private static final String CONTAINER_FIELD = "containerId";
    private static final String ERR_FACILITY_SNAPSHOT = "Facility with id '%s' not found when loading DailyPlan";
    private static final String ERR_VEHICLE_SNAPSHOT = "Vehicle with id '%s' not found when loading DailyPlan";
    private static final String ERR_CONTAINER_SNAPSHOT = "Container with id '%s' not found when loading DailyPlan";

    @SuppressWarnings("unused")
    private final MongoConfiguration mongoConfiguration;

    /**
     * Constructs a new DailyPlanReadingConverter.
     *
     * @param mongoConfiguration the MongoDB configuration for converter registration
     */
    public DailyPlanReadingConverter(MongoConfiguration mongoConfiguration) {
        this.mongoConfiguration = mongoConfiguration;
    }

    /**
     * Converts a MongoDB Document into a DailyPlan entity.
     *
     * @param document MongoDB Document to convert
     * @return DailyPlan entity reconstructed from the document
     */
    @Override
    public DailyPlan convert(@NonNull Document document) {
        logger.info("DailyPlan to read from document '{}'", document);
        UUID id = (UUID) document.get(MongoFields.ID);
        UUID planId = (UUID) document.get(MongoFields.INFRASTRUCTURE_PLAN_ID);
        InfrastructurePlan infrastructurePlan = new InfrastructurePlan(
                planId,
            new PlanningPeriod(String.valueOf(LocalDate.now().getYear())),
                null, null, null, null,
                new MaximumBudget(1.0),
                new TotalCost(0.0),
                null, null, null, null, null, null);
        UUID facilityId = (UUID) document.get(MongoFields.FACILITY_ID);
        Facility facility = mongoConfiguration.facilityRepository().findById(facilityId)
                .orElseThrow(() -> new IllegalStateException(String.format(ERR_FACILITY_SNAPSHOT, facilityId)));
        LocalDate serviceDate = LocalDate.parse(document.getString(MongoFields.SERVICE_DATE));
        Integer planDay = document.containsKey(MongoFields.PLAN_DAY) ? document.getInteger(MongoFields.PLAN_DAY) : null;
        UUID vehicleId = (UUID) document.get(MongoFields.VEHICLE);
        Vehicle vehicle = mongoConfiguration.vehicleRepository().findById(vehicleId)
                .orElseThrow(() -> new IllegalStateException(String.format(ERR_VEHICLE_SNAPSHOT, vehicleId)));
        CollectedWeightKilograms totalCollectedKilograms = CollectedWeightKilograms.fromKilograms(document.getDouble(MongoFields.TOTAL_COLLECTED_KILOGRAMS));
        CollectedVolumeLiters totalCollectedLiters = CollectedVolumeLiters.fromLiters(document.getDouble(MongoFields.TOTAL_COLLECTED_LITERS));
        Distance totalDistanceMeters = Distance.fromMeters(document.getDouble(MongoFields.TOTAL_DISTANCE_METERS));
        List<?> stopDocuments = document.get(MongoFields.STOPS, List.class);
        List<Stop> stops = new ArrayList<>();
        if (stopDocuments != null) {
            for (Object stopDocumentObject : stopDocuments) {
                stops.add(readStopSnapshot((Document) stopDocumentObject));
            }
        }
        return new DailyPlan(
                id,
                infrastructurePlan,
                facility,
                serviceDate,
                planDay,
                vehicle,
                totalCollectedKilograms,
                totalCollectedLiters,
                totalDistanceMeters,
                stops);
    }



    /**
     * Reads a Stop snapshot from a nested MongoDB document.
     *
     * @param document the nested Document containing stop data
     * @return the reconstructed Stop entity
     */
    private Stop readStopSnapshot(Document document) {
        int sequence = document.getInteger(MongoFields.SEQUENCE);
        double collectedKilograms = document.getDouble(MongoFields.COLLECTED_KILOGRAMS);
        double collectedLiters = document.getDouble(MongoFields.COLLECTED_LITERS);
        double distanceFromPreviousMeters = document.getDouble(MongoFields.DISTANCE_FROM_PREVIOUS_METERS);
        double cumulativeDistanceMeters = document.getDouble(MongoFields.CUMULATIVE_DISTANCE_METERS);
        UUID containerId = (UUID) document.get(CONTAINER_FIELD);
        Container container = mongoConfiguration.containerRepository().findById(containerId)
                .orElseThrow(() -> new IllegalStateException(String.format(ERR_CONTAINER_SNAPSHOT, containerId)));
        return new Stop(
                RouteSequence.of(sequence),
                container,
                CollectedWeightKilograms.fromKilograms(collectedKilograms),
                CollectedVolumeLiters.fromLiters(collectedLiters),
                Distance.fromMeters(distanceFromPreviousMeters),
                Distance.fromMeters(cumulativeDistanceMeters));
    }


}
