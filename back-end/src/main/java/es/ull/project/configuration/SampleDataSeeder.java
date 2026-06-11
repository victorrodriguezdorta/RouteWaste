package es.ull.project.configuration;

import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.repository.VehicleRepository;
import es.ull.project.domain.entity.Container;
import es.ull.project.domain.entity.Facility;
import es.ull.project.domain.entity.Vehicle;
import es.ull.project.domain.enumerate.FacilityStatus;
import es.ull.project.domain.enumerate.FacilityType;
import es.ull.project.domain.enumerate.ServiceZone;
import es.ull.project.domain.enumerate.VehicleType;
import es.ull.project.domain.enumerate.WasteType;
import es.ull.project.domain.valueobject.capacity.ContainerCapacityLiters;
import es.ull.project.domain.valueobject.capacity.ProcessingCapacityKilogramsPerDay;
import es.ull.project.domain.valueobject.capacity.StorageCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.UnloadingTime;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityKilograms;
import es.ull.project.domain.valueobject.capacity.VehicleCapacityLiters;
import es.ull.project.domain.valueobject.cost.OpeningFixedCost;
import es.ull.project.domain.valueobject.cost.TransportationVariableCost;
import es.ull.project.domain.valueobject.demand.DailyWasteDemandLitersPerDay;
import es.ull.project.domain.valueobject.location.Location;
import es.ull.project.domain.valueobject.name.Name;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Loads sample MongoDB data for local testing.
 *
 * Activate with the "sample-data" profile.
 */
@Component
@Profile("sample-data & !memory")
public class SampleDataSeeder implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(SampleDataSeeder.class);
    private static final String CONTAINER_ENTITY_TYPE = "container";
    private static final String FACILITY_ENTITY_TYPE = "facility";
    private static final String VEHICLE_ENTITY_TYPE = "vehicle";
    private static final String CONTAINER_REFERENCE_PREFIX = "SAMPLE-CONTAINER-";
    private static final String FACILITY_REFERENCE_PREFIX = "SAMPLE-FACILITY-";
    private static final String SAMPLE_ID_PREFIX = "sample-data-";
    private static final String SAMPLE_ID_SEPARATOR = "-";
    private static final String[] CONTAINER_NAMES = {
        "Contenedor Centro",
        "Contenedor Mercado",
        "Contenedor Rambla",
        "Contenedor Anaga",
        "Contenedor Ofra",
        "Contenedor Tome Cano",
        "Contenedor La Salle",
        "Contenedor Salud",
        "Contenedor Cabo Llanos",
        "Contenedor Chapatal"
    };
    private static final String[] CONTAINER_ADDRESSES = {
        "Calle Castillo 1",
        "Avenida San Sebastian 12",
        "Rambla Pulido 45",
        "Calle Anaga 8",
        "Avenida Venezuela 31",
        "Calle Tome Cano 19",
        "Avenida La Salle 4",
        "Calle Salud 27",
        "Avenida Tres de Mayo 18",
        "Calle Volcan Elena 6"
    };
    private static final String[] FACILITY_NAMES = {
        "Base Operativa Centro",
        "Estacion Transferencia Norte",
        "Planta Tratamiento Sur",
        "Base Operativa Anaga",
        "Estacion Transferencia Laguna",
        "Planta Clasificacion Taco",
        "Base Operativa Ofra",
        "Estacion Transferencia Guimar",
        "Planta Tratamiento Arico",
        "Base Operativa Acoran"
    };
    private static final String[] FACILITY_ADDRESSES = {
        "Avenida Tres de Mayo 10",
        "Carretera General Norte 22",
        "Poligono Industrial 3",
        "Carretera Anaga 15",
        "Calle Heraclio Sanchez 7",
        "Camino del Medio 40",
        "Avenida Principes Espana 5",
        "Poligono Valle Guimar 12",
        "Complejo Ambiental 1",
        "Avenida Maritima Acoran 2"
    };
    private static final String[] VEHICLE_NAMES = {
        "Recolector Ligero 1",
        "Recolector Ligero 2",
        "Recolector Medio 1",
        "Recolector Medio 2",
        "Recolector Pesado 1",
        "Transferencia 1",
        "Transferencia 2",
        "Transferencia 3",
        "Soporte Taller",
        "Soporte Inspeccion"
    };

    private final ContainerRepository containerRepository;
    private final FacilityRepository facilityRepository;
    private final VehicleRepository vehicleRepository;
    private final ConfigurableApplicationContext applicationContext;
    private final boolean exitAfterSeed;

    /**
     * Creates the seeder with repositories and execution options.
     *
     * @param containerRepository repository used to persist sample containers
     * @param facilityRepository repository used to persist sample facilities
     * @param vehicleRepository repository used to persist sample vehicles
     * @param applicationContext Spring application context used to stop the app
     * @param exitAfterSeed whether the application should exit after seeding
     */
    public SampleDataSeeder(
            ContainerRepository containerRepository,
            FacilityRepository facilityRepository,
            VehicleRepository vehicleRepository,
            ConfigurableApplicationContext applicationContext,
            @Value("${sample-data.exit-after-seed:false}") boolean exitAfterSeed) {
        this.containerRepository = containerRepository;
        this.facilityRepository = facilityRepository;
        this.vehicleRepository = vehicleRepository;
        this.applicationContext = applicationContext;
        this.exitAfterSeed = exitAfterSeed;
    }

    /**
     * Seeds missing sample data when the configured profile is active.
     *
     * @param args application arguments supplied by Spring Boot
     */
    @Override
    public void run(ApplicationArguments args) {
        int containersCreated = saveMissingContainers(buildContainers());
        int facilitiesCreated = saveMissingFacilities(buildFacilities());
        int vehiclesCreated = saveMissingVehicles(buildVehicles());
        logger.info(
                "Sample data seed finished. Created {} containers, {} facilities and {} vehicles.",
                containersCreated,
                facilitiesCreated,
                vehiclesCreated);
        if (this.exitAfterSeed) {
            SpringApplication.exit(this.applicationContext, () -> 0);
        }
    }

    /**
     * Saves only containers that are not already stored.
     *
     * @param containers sample containers to check and persist
     * @return number of containers created
     */
    private int saveMissingContainers(List<Container> containers) {
        int created = 0;
        for (Container container : containers) {
            if (this.containerRepository.findById(container.getId()).isEmpty()) {
                this.containerRepository.save(container);
                created++;
            }
        }
        return created;
    }

    /**
     * Saves only facilities that are not already stored.
     *
     * @param facilities sample facilities to check and persist
     * @return number of facilities created
     */
    private int saveMissingFacilities(List<Facility> facilities) {
        int created = 0;
        for (Facility facility : facilities) {
            if (this.facilityRepository.findById(facility.getId()).isEmpty()) {
                this.facilityRepository.save(facility);
                created++;
            }
        }
        return created;
    }

    /**
     * Saves only vehicles that are not already stored.
     *
     * @param vehicles sample vehicles to check and persist
     * @return number of vehicles created
     */
    private int saveMissingVehicles(List<Vehicle> vehicles) {
        int created = 0;
        for (Vehicle vehicle : vehicles) {
            if (this.vehicleRepository.findById(vehicle.getId()).isEmpty()) {
                this.vehicleRepository.save(vehicle);
                created++;
            }
        }
        return created;
    }

    /**
     * Builds the configured sample containers.
     *
     * @return sample containers
     */
    private List<Container> buildContainers() {
        return List.of(
                container(1, CONTAINER_NAMES[0], 28.4682, -16.2546, CONTAINER_ADDRESSES[0], WasteType.ORGANIC, 800.0, 260.0, ServiceZone.NEIGHBORHOOD),
                container(2, CONTAINER_NAMES[1], 28.4667, -16.2483, CONTAINER_ADDRESSES[1], WasteType.PACKAGING, 1000.0, 340.0, ServiceZone.DISTRICT),
                container(3, CONTAINER_NAMES[2], 28.4721, -16.2598, CONTAINER_ADDRESSES[2], WasteType.PAPER_CARDBOARD, 1100.0, 300.0, ServiceZone.GEOGRAPHICAL_AREA),
                container(4, CONTAINER_NAMES[3], 28.4780, -16.2471, CONTAINER_ADDRESSES[3], WasteType.GLASS, 700.0, 180.0, ServiceZone.NEIGHBORHOOD),
                container(5, CONTAINER_NAMES[4], 28.4556, -16.2822, CONTAINER_ADDRESSES[4], WasteType.RESIDUAL, 1200.0, 410.0, ServiceZone.DISTRICT),
                container(6, CONTAINER_NAMES[5], 28.4605, -16.2654, CONTAINER_ADDRESSES[5], WasteType.ORGANIC, 900.0, 280.0, ServiceZone.NEIGHBORHOOD),
                container(7, CONTAINER_NAMES[6], 28.4630, -16.2608, CONTAINER_ADDRESSES[6], WasteType.PACKAGING, 1050.0, 365.0, ServiceZone.DISTRICT),
                container(8, CONTAINER_NAMES[7], 28.4689, -16.2703, CONTAINER_ADDRESSES[7], WasteType.PAPER_CARDBOARD, 950.0, 290.0, ServiceZone.NEIGHBORHOOD),
                container(9, CONTAINER_NAMES[8], 28.4569, -16.2516, CONTAINER_ADDRESSES[8], WasteType.GLASS, 750.0, 210.0, ServiceZone.GEOGRAPHICAL_AREA),
                container(10, CONTAINER_NAMES[9], 28.4517, -16.2724, CONTAINER_ADDRESSES[9], WasteType.RESIDUAL, 1300.0, 450.0, ServiceZone.DISTRICT));
    }

    /**
     * Builds the configured sample facilities.
     *
     * @return sample facilities
     */
    private List<Facility> buildFacilities() {
        return List.of(
                facility(1, FACILITY_NAMES[0], FacilityType.OPERATIONAL_BASE, 28.4629, -16.2519, FACILITY_ADDRESSES[0], 25000.0, 12000.0, 20, 150000.0, FacilityStatus.OPEN),
                facility(2, FACILITY_NAMES[1], FacilityType.TRANSFER_STATION, 28.4902, -16.3194, FACILITY_ADDRESSES[1], 40000.0, 18000.0, 30, 230000.0, FacilityStatus.PLANNED),
                facility(3, FACILITY_NAMES[2], FacilityType.TREATMENT_PLANT, 28.0961, -16.6806, FACILITY_ADDRESSES[2], 80000.0, 45000.0, 45, 520000.0, FacilityStatus.CANDIDATE),
                facility(4, FACILITY_NAMES[3], FacilityType.OPERATIONAL_BASE, 28.5262, -16.2464, FACILITY_ADDRESSES[3], 20000.0, 9000.0, 18, 125000.0, FacilityStatus.OPEN),
                facility(5, FACILITY_NAMES[4], FacilityType.TRANSFER_STATION, 28.4874, -16.3159, FACILITY_ADDRESSES[4], 38000.0, 17000.0, 28, 215000.0, FacilityStatus.OPEN),
                facility(6, FACILITY_NAMES[5], FacilityType.TREATMENT_PLANT, 28.4528, -16.3019, FACILITY_ADDRESSES[5], 70000.0, 36000.0, 40, 430000.0, FacilityStatus.PLANNED),
                facility(7, FACILITY_NAMES[6], FacilityType.OPERATIONAL_BASE, 28.4549, -16.2860, FACILITY_ADDRESSES[6], 22000.0, 11000.0, 22, 138000.0, FacilityStatus.CANDIDATE),
                facility(8, FACILITY_NAMES[7], FacilityType.TRANSFER_STATION, 28.3188, -16.4105, FACILITY_ADDRESSES[7], 42000.0, 21000.0, 32, 260000.0, FacilityStatus.OPEN),
                facility(9, FACILITY_NAMES[8], FacilityType.TREATMENT_PLANT, 28.1770, -16.4992, FACILITY_ADDRESSES[8], 95000.0, 52000.0, 50, 610000.0, FacilityStatus.OPEN),
                facility(10, FACILITY_NAMES[9], FacilityType.OPERATIONAL_BASE, 28.4257, -16.3096, FACILITY_ADDRESSES[9], 24000.0, 10000.0, 24, 145000.0, FacilityStatus.DISCARDED));
    }

    /**
     * Builds the configured sample vehicles.
     *
     * @return sample vehicles
     */
    private List<Vehicle> buildVehicles() {
        return List.of(
                vehicle(1, VEHICLE_NAMES[0], VehicleType.COLLECTION_TRUCK, 3500.0, 7000.0, 0.85),
                vehicle(2, VEHICLE_NAMES[1], VehicleType.COLLECTION_TRUCK, 3700.0, 7400.0, 0.88),
                vehicle(3, VEHICLE_NAMES[2], VehicleType.COLLECTION_TRUCK, 5200.0, 10500.0, 1.10),
                vehicle(4, VEHICLE_NAMES[3], VehicleType.COLLECTION_TRUCK, 5600.0, 11200.0, 1.16),
                vehicle(5, VEHICLE_NAMES[4], VehicleType.COLLECTION_TRUCK, 8000.0, 16000.0, 1.45),
                vehicle(6, VEHICLE_NAMES[5], VehicleType.TRANSFER_TRUCK, 12000.0, 22000.0, 1.80),
                vehicle(7, VEHICLE_NAMES[6], VehicleType.TRANSFER_TRUCK, 13500.0, 25000.0, 1.95),
                vehicle(8, VEHICLE_NAMES[7], VehicleType.TRANSFER_TRUCK, 15000.0, 28000.0, 2.10),
                vehicle(9, VEHICLE_NAMES[8], VehicleType.SUPPORT_VEHICLE, 1200.0, 2200.0, 0.55),
                vehicle(10, VEHICLE_NAMES[9], VehicleType.SUPPORT_VEHICLE, 900.0, 1600.0, 0.48));
    }

    /**
     * Creates a sample container with value objects.
     *
     * @param index stable sample index
     * @param name container display name
     * @param latitude container latitude
     * @param longitude container longitude
     * @param postalAddress container postal address
     * @param wasteType waste type collected by the container
     * @param capacityLiters container capacity in liters
     * @param dailyDemandLiters expected daily demand in liters
     * @param serviceZone service zone assigned to the container
     * @return configured sample container
     */
    private Container container(
            int index,
            String name,
            double latitude,
            double longitude,
            String postalAddress,
            WasteType wasteType,
            double capacityLiters,
            double dailyDemandLiters,
            ServiceZone serviceZone) {
        return new Container(
                new Name(name),
                new Location(latitude, longitude, postalAddress, CONTAINER_REFERENCE_PREFIX + index),
                wasteType,
                new ContainerCapacityLiters(capacityLiters),
                new DailyWasteDemandLitersPerDay(dailyDemandLiters),
                serviceZone);
    }

    /**
     * Creates a sample facility with value objects.
     *
     * @param index stable sample index
     * @param name facility display name
     * @param facilityType type of facility
     * @param latitude facility latitude
     * @param longitude facility longitude
     * @param postalAddress facility postal address
     * @param storageKilograms storage capacity in kilograms
     * @param processingKilogramsPerDay processing capacity per day
     * @param unloadingMinutes unloading time in minutes
     * @param openingFixedCost fixed opening cost
     * @param status facility planning status
     * @return configured sample facility
     */
    private Facility facility(
            int index,
            String name,
            FacilityType facilityType,
            double latitude,
            double longitude,
            String postalAddress,
            double storageKilograms,
            double processingKilogramsPerDay,
            int unloadingMinutes,
            double openingFixedCost,
            FacilityStatus status) {
        return new Facility(
                sampleId(FACILITY_ENTITY_TYPE, index),
                new Name(name),
                facilityType,
                new Location(latitude, longitude, postalAddress, FACILITY_REFERENCE_PREFIX + index),
                new StorageCapacityKilograms(storageKilograms),
                new ProcessingCapacityKilogramsPerDay(processingKilogramsPerDay),
                new UnloadingTime(unloadingMinutes),
                new OpeningFixedCost(openingFixedCost),
                status,
                new DailyWasteDemandLitersPerDay(0.0));
    }

    /**
     * Creates a sample vehicle with value objects.
     *
     * @param index stable sample index
     * @param name vehicle display name
     * @param vehicleType type of vehicle
     * @param capacityKilograms vehicle capacity in kilograms
     * @param capacityLiters vehicle capacity in liters
     * @param costPerKilometer variable transportation cost per kilometer
     * @return configured sample vehicle
     */
    private Vehicle vehicle(
            int index,
            String name,
            VehicleType vehicleType,
            double capacityKilograms,
            double capacityLiters,
            double costPerKilometer) {
        return new Vehicle(
                sampleId(VEHICLE_ENTITY_TYPE, index),
                new Name(name),
                vehicleType,
                new VehicleCapacityKilograms(capacityKilograms),
                new VehicleCapacityLiters(capacityLiters),
                new TransportationVariableCost(costPerKilometer));
    }

    /**
     * Builds a deterministic UUID for a sample entity.
     *
     * @param entityType sample entity type
     * @param index stable sample index
     * @return deterministic UUID for the sample entity
     */
    private UUID sampleId(String entityType, int index) {
        return UUID.nameUUIDFromBytes(
                (SAMPLE_ID_PREFIX + entityType + SAMPLE_ID_SEPARATOR + index).getBytes(StandardCharsets.UTF_8));
    }
}
