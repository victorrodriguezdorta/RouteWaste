package es.ull.project;

import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.cost.Currency;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.demand.Capacity;
import es.ull.project.domain.valueobject.demand.QuantityUnit;
import es.ull.project.domain.valueobject.demand.WasteDemand;
import es.ull.project.domain.valueobject.location.Location;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * DataInitializer
 *
 * Component that initializes the database with test data when the application starts.
 * This is useful for testing the MongoDB persistence layer.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final ContainerRepository containerRepository;
    private final FacilityRepository facilityRepository;

    /**
     * Constructor for DataInitializer.
     *
     * @param containerRepository Repository for container persistence operations.
     * @param facilityRepository Repository for facility persistence operations.
     */
    public DataInitializer(ContainerRepository containerRepository,
                           FacilityRepository facilityRepository) {
        this.containerRepository = containerRepository;
        this.facilityRepository = facilityRepository;
    }

    /**
     * Runs the initialization logic when the application starts.
     * Creates test containers and facilities in the database.
     *
     * @param args Command line arguments passed to the application.
     */
    @Override
    public void run(String... args) {
        logger.info("Initializing database with test data...");
        createTestContainer();
        createTestFacility();
        logger.info("Database initialization completed.");
    }

    /**
     * Creates and persists a test container in the database.
     * The container is created with predefined location, waste type, and demand values.
     */
    private void createTestContainer() {
        Location location = new Location(
                28.4682,
                -16.2546,
                "Calle La Noria 123, La Laguna, Tenerife",
                "GIS-REF-001"
        );
        WasteDemand wasteDemand = new WasteDemand(
                5.5,
                new QuantityUnit("tons"),
                TimeUnit.DAYS
        );
        Container container = new Container(
                location,
                WasteType.ORGANIC,
                wasteDemand,
                ServiceZone.NEIGHBORHOOD
        );
        containerRepository.save(container);
        logger.info("Test Container saved with ID: {}", container.getId());
    }

    /**
     * Creates and persists a test facility in the database.
     * The facility is created with predefined type, location, capacity, and cost values.
     */
    private void createTestFacility() {
        Location location = new Location(
                28.4850,
                -16.3150,
                "Poligono Industrial, Santa Cruz de Tenerife",
                "GIS-REF-FAC-001"
        );
        Capacity capacity = new Capacity(
                100.0,
                new QuantityUnit("tons"),
                TimeUnit.DAYS
        );
        OpeningFixedCost openingFixedCost = new OpeningFixedCost(
                50000.0,
                new Currency("EUR")
        );
        Facility facility = new Facility(
                FacilityType.TREATMENT_PLANT,
                location,
                capacity,
                openingFixedCost,
                FacilityStatus.OPEN
        );
        facilityRepository.save(facility);
        logger.info("Test Facility saved with ID: {}", facility.getId());
    }
}
