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
    private static final int SAMPLE_SIZE = 10;

    private final ContainerRepository containerRepository;
    private final FacilityRepository facilityRepository;
    private final VehicleRepository vehicleRepository;
    private final ConfigurableApplicationContext applicationContext;
    private final boolean exitAfterSeed;

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

    private List<Container> buildContainers() {
        return List.of(
                container(1, "Contenedor Centro", 28.4682, -16.2546, "Calle Castillo 1", WasteType.ORGANIC, 800.0, 260.0, ServiceZone.NEIGHBORHOOD),
                container(2, "Contenedor Mercado", 28.4667, -16.2483, "Avenida San Sebastian 12", WasteType.PACKAGING, 1000.0, 340.0, ServiceZone.DISTRICT),
                container(3, "Contenedor Rambla", 28.4721, -16.2598, "Rambla Pulido 45", WasteType.PAPER_CARDBOARD, 1100.0, 300.0, ServiceZone.GEOGRAPHICAL_AREA),
                container(4, "Contenedor Anaga", 28.4780, -16.2471, "Calle Anaga 8", WasteType.GLASS, 700.0, 180.0, ServiceZone.NEIGHBORHOOD),
                container(5, "Contenedor Ofra", 28.4556, -16.2822, "Avenida Venezuela 31", WasteType.RESIDUAL, 1200.0, 410.0, ServiceZone.DISTRICT),
                container(6, "Contenedor Tome Cano", 28.4605, -16.2654, "Calle Tome Cano 19", WasteType.ORGANIC, 900.0, 280.0, ServiceZone.NEIGHBORHOOD),
                container(7, "Contenedor La Salle", 28.4630, -16.2608, "Avenida La Salle 4", WasteType.PACKAGING, 1050.0, 365.0, ServiceZone.DISTRICT),
                container(8, "Contenedor Salud", 28.4689, -16.2703, "Calle Salud 27", WasteType.PAPER_CARDBOARD, 950.0, 290.0, ServiceZone.NEIGHBORHOOD),
                container(9, "Contenedor Cabo Llanos", 28.4569, -16.2516, "Avenida Tres de Mayo 18", WasteType.GLASS, 750.0, 210.0, ServiceZone.GEOGRAPHICAL_AREA),
                container(10, "Contenedor Chapatal", 28.4517, -16.2724, "Calle Volcan Elena 6", WasteType.RESIDUAL, 1300.0, 450.0, ServiceZone.DISTRICT));
    }

    private List<Facility> buildFacilities() {
        return List.of(
                facility(1, "Base Operativa Centro", FacilityType.OPERATIONAL_BASE, 28.4629, -16.2519, "Avenida Tres de Mayo 10", 25000.0, 12000.0, 20, 150000.0, FacilityStatus.OPEN),
                facility(2, "Estacion Transferencia Norte", FacilityType.TRANSFER_STATION, 28.4902, -16.3194, "Carretera General Norte 22", 40000.0, 18000.0, 30, 230000.0, FacilityStatus.PLANNED),
                facility(3, "Planta Tratamiento Sur", FacilityType.TREATMENT_PLANT, 28.0961, -16.6806, "Poligono Industrial 3", 80000.0, 45000.0, 45, 520000.0, FacilityStatus.CANDIDATE),
                facility(4, "Base Operativa Anaga", FacilityType.OPERATIONAL_BASE, 28.5262, -16.2464, "Carretera Anaga 15", 20000.0, 9000.0, 18, 125000.0, FacilityStatus.OPEN),
                facility(5, "Estacion Transferencia Laguna", FacilityType.TRANSFER_STATION, 28.4874, -16.3159, "Calle Heraclio Sanchez 7", 38000.0, 17000.0, 28, 215000.0, FacilityStatus.OPEN),
                facility(6, "Planta Clasificacion Taco", FacilityType.TREATMENT_PLANT, 28.4528, -16.3019, "Camino del Medio 40", 70000.0, 36000.0, 40, 430000.0, FacilityStatus.PLANNED),
                facility(7, "Base Operativa Ofra", FacilityType.OPERATIONAL_BASE, 28.4549, -16.2860, "Avenida Principes Espana 5", 22000.0, 11000.0, 22, 138000.0, FacilityStatus.CANDIDATE),
                facility(8, "Estacion Transferencia Guimar", FacilityType.TRANSFER_STATION, 28.3188, -16.4105, "Poligono Valle Guimar 12", 42000.0, 21000.0, 32, 260000.0, FacilityStatus.OPEN),
                facility(9, "Planta Tratamiento Arico", FacilityType.TREATMENT_PLANT, 28.1770, -16.4992, "Complejo Ambiental 1", 95000.0, 52000.0, 50, 610000.0, FacilityStatus.OPEN),
                facility(10, "Base Operativa Acoran", FacilityType.OPERATIONAL_BASE, 28.4257, -16.3096, "Avenida Maritima Acoran 2", 24000.0, 10000.0, 24, 145000.0, FacilityStatus.DISCARDED));
    }

    private List<Vehicle> buildVehicles() {
        return List.of(
                vehicle(1, "Recolector Ligero 1", VehicleType.COLLECTION_TRUCK, 3500.0, 7000.0, 0.85),
                vehicle(2, "Recolector Ligero 2", VehicleType.COLLECTION_TRUCK, 3700.0, 7400.0, 0.88),
                vehicle(3, "Recolector Medio 1", VehicleType.COLLECTION_TRUCK, 5200.0, 10500.0, 1.10),
                vehicle(4, "Recolector Medio 2", VehicleType.COLLECTION_TRUCK, 5600.0, 11200.0, 1.16),
                vehicle(5, "Recolector Pesado 1", VehicleType.COLLECTION_TRUCK, 8000.0, 16000.0, 1.45),
                vehicle(6, "Transferencia 1", VehicleType.TRANSFER_TRUCK, 12000.0, 22000.0, 1.80),
                vehicle(7, "Transferencia 2", VehicleType.TRANSFER_TRUCK, 13500.0, 25000.0, 1.95),
                vehicle(8, "Transferencia 3", VehicleType.TRANSFER_TRUCK, 15000.0, 28000.0, 2.10),
                vehicle(9, "Soporte Taller", VehicleType.SUPPORT_VEHICLE, 1200.0, 2200.0, 0.55),
                vehicle(10, "Soporte Inspeccion", VehicleType.SUPPORT_VEHICLE, 900.0, 1600.0, 0.48));
    }

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
                sampleId("container", index),
                new Name(name),
                new Location(latitude, longitude, postalAddress, "SAMPLE-CONTAINER-" + index),
                wasteType,
                new ContainerCapacityLiters(capacityLiters),
                new DailyWasteDemandLitersPerDay(dailyDemandLiters),
                serviceZone);
    }

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
                sampleId("facility", index),
                new Name(name),
                facilityType,
                new Location(latitude, longitude, postalAddress, "SAMPLE-FACILITY-" + index),
                new StorageCapacityKilograms(storageKilograms),
                new ProcessingCapacityKilogramsPerDay(processingKilogramsPerDay),
                new UnloadingTime(unloadingMinutes),
                new OpeningFixedCost(openingFixedCost),
                status,
                new DailyWasteDemandLitersPerDay(0.0));
    }

    private Vehicle vehicle(
            int index,
            String name,
            VehicleType vehicleType,
            double capacityKilograms,
            double capacityLiters,
            double costPerKilometer) {
        return new Vehicle(
                sampleId("vehicle", index),
                new Name(name),
                vehicleType,
                new VehicleCapacityKilograms(capacityKilograms),
                new VehicleCapacityLiters(capacityLiters),
                new TransportationVariableCost(costPerKilometer));
    }

    private UUID sampleId(String entityType, int index) {
        return UUID.nameUUIDFromBytes(("sample-data-" + entityType + "-" + index).getBytes(StandardCharsets.UTF_8));
    }
}
