package es.ull.project.adapter.mongodb.reader;

import java.util.UUID;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;

import es.ull.project.adapter.mongodb.MongoFields;
import es.ull.project.configuration.MongoConfiguration;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.valueobject.capacity.ProcessingCapacityKilogramsPerDay;
import es.ull.project.domain.valueobject.capacity.StorageCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.UnloadingTime;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;

/**
 * FacilityReadingConverter
 *
 * Implements custom conversion logic for transforming MongoDB documents
 * into Facility entities. This converter handles the deserialization of
 * MongoDB documents including their nested structures (Location, StorageCapacityKilograms,
 * ProcessingCapacityKilogramsPerDay, UnloadingTime, OpeningFixedCost, DailyWasteDemandLitersPerDay)
 * into a properly constructed Facility domain entity.
 */
@ReadingConverter
public class FacilityReadingConverter implements Converter<Document, Facility> {

    private static final Logger logger = LoggerFactory.getLogger(FacilityReadingConverter.class);

    @SuppressWarnings("unused")
    private final MongoConfiguration mongoConfiguration;

    /**
     * Constructs a new FacilityReadingConverter.
     *
     * @param mongoConfiguration the MongoDB configuration for converter registration
     */
    public FacilityReadingConverter(MongoConfiguration mongoConfiguration) {
        this.mongoConfiguration = mongoConfiguration;
    }

    /**
     * Converts a MongoDB Document into a Facility entity.
     *
     * @param document MongoDB Document to convert
     * @return Facility entity reconstructed from the document
     */
    @Override
    public Facility convert(@NonNull Document document) {
        logger.info("Facility to read from document '{}'", document);
        UUID id = (UUID) document.get(MongoFields.ID);
        FacilityType facilityType = FacilityType.fromString(document.getString(MongoFields.FACILITY_TYPE));
        Document locationDocument = (Document) document.get(MongoFields.LOCATION);
        double latitude = locationDocument.getDouble(MongoFields.LATITUDE);
        double longitude = locationDocument.getDouble(MongoFields.LONGITUDE);
        String postalAddress = locationDocument.getString(MongoFields.POSTAL_ADDRESS);
        String gisReference = locationDocument.getString(MongoFields.GIS_REFERENCE);
        Location location = new Location(latitude, longitude, postalAddress, gisReference);
        Document storageCapacityDocument = (Document) document.get(MongoFields.STORAGE_CAPACITY);
        double storageCapacityValue = storageCapacityDocument.getDouble(MongoFields.CAPACITY_VALUE);
        StorageCapacityKilograms storageCapacity = new StorageCapacityKilograms(storageCapacityValue);
        Document processingCapacityDocument = (Document) document.get(MongoFields.PROCESSING_CAPACITY);
        double processingCapacityValue = processingCapacityDocument.getDouble(MongoFields.CAPACITY_VALUE);
        ProcessingCapacityKilogramsPerDay processingCapacity = new ProcessingCapacityKilogramsPerDay(processingCapacityValue);
        Document unloadingTimeDocument = (Document) document.get(MongoFields.UNLOADING_TIME);
        int unloadingTimeValue = unloadingTimeDocument.getInteger(MongoFields.TIME_VALUE);
        UnloadingTime unloadingTime = new UnloadingTime(unloadingTimeValue);
        Document openingFixedCostDocument = (Document) document.get(MongoFields.OPENING_FIXED_COST);
        double openingFixedCostAmount = openingFixedCostDocument.getDouble(MongoFields.OPENING_FIXED_COST_AMOUNT);
        OpeningFixedCost openingFixedCost;
        if (openingFixedCostDocument.containsKey(MongoFields.OPENING_FIXED_COST_CURRENCY)) {
            String currencyCode = openingFixedCostDocument.getString(MongoFields.OPENING_FIXED_COST_CURRENCY);
            Currency currency = new Currency(currencyCode);
            openingFixedCost = new OpeningFixedCost(openingFixedCostAmount, currency);
        } else {
            openingFixedCost = new OpeningFixedCost(openingFixedCostAmount);
        }
        FacilityStatus status = FacilityStatus.fromString(document.getString(MongoFields.STATUS));
        Document currentFillingLevelDocument = (Document) document.get(MongoFields.CURRENT_FILLING_LEVEL);
        double currentFillingLevelValue = currentFillingLevelDocument.getDouble(MongoFields.WASTE_DEMAND_VALUE);
        DailyWasteDemandLitersPerDay currentFillingLevel = new DailyWasteDemandLitersPerDay(currentFillingLevelValue);
        Facility facility = new Facility(
                id,
                facilityType,
                location,
                storageCapacity,
                processingCapacity,
                unloadingTime,
                openingFixedCost,
                status,
                currentFillingLevel);
        return facility;
    }
}
