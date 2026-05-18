package es.ull.project.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import es.ull.project.adapter.mongodb.reader.ContainerDailyStateReadingConverter;
import es.ull.project.adapter.mongodb.reader.ContainerReadingConverter;
import es.ull.project.adapter.mongodb.reader.DailyPlanReadingConverter;
import es.ull.project.adapter.mongodb.reader.FacilityReadingConverter;
import es.ull.project.adapter.mongodb.reader.InfrastructurePlanReadingConverter;
import es.ull.project.adapter.mongodb.reader.ServiceAssignmentReadingConverter;
import es.ull.project.adapter.mongodb.reader.VehicleReadingConverter;
import es.ull.project.adapter.mongodb.writer.ContainerDailyStateWritingConverter;
import es.ull.project.adapter.mongodb.writer.ContainerWritingConverter;
import es.ull.project.adapter.mongodb.writer.DailyPlanWritingConverter;
import es.ull.project.adapter.mongodb.writer.FacilityWritingConverter;
import es.ull.project.adapter.mongodb.writer.InfrastructurePlanWritingConverter;
import es.ull.project.adapter.mongodb.writer.ServiceAssignmentWritingConverter;
import es.ull.project.adapter.mongodb.writer.VehicleWritingConverter;
import es.ull.project.application.repository.ContainerDailyStateRepository;
import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.repository.DailyPlanRepository;
import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.repository.VehicleRepository;
import java.util.Arrays;
import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.lang.NonNull;

/**
 * MongoDB configuration class.
 *
 * This class configures MongoDB client settings, custom conversions,
 * and creates repository beans for dependency injection.
 */
@Configuration
public class MongoConfiguration extends AbstractMongoClientConfiguration {

    public static final String MONGO_CLIENT_CREATION_FAILED = "MongoClient creation failed";
    public static final String DATABASE_NAME_NOT_CONFIGURED = "Database name is not configured";

    @Value("${spring.data.mongodb.uri}")
    private String uri;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Lazy
    @Autowired
    private ContainerRepository containerRepository;

    @Lazy
    @Autowired
    private ContainerDailyStateRepository containerDailyStateRepository;

    @Lazy
    @Autowired
    private FacilityRepository facilityRepository;

    @Lazy
    @Autowired
    private ServiceAssignmentRepository serviceAssignmentRepository;

    @Lazy
    @Autowired
    private VehicleRepository vehicleRepository;

    @Lazy
    @Autowired
    private DailyPlanRepository dailyPlanRepository;

    /**
     * Returns the ContainerRepository instance.
     *
     * @return ContainerRepository
     */
    public ContainerRepository containerRepository() {
        return this.containerRepository;
    }

    /**
     * Returns the ContainerDailyStateRepository instance.
     *
     * @return ContainerDailyStateRepository
     */
    public ContainerDailyStateRepository containerDailyStateRepository() {
        return this.containerDailyStateRepository;
    }

    /**
     * Returns the FacilityRepository instance.
     *
     * @return FacilityRepository
     */
    public FacilityRepository facilityRepository() {
        return this.facilityRepository;
    }

    /**
     * Returns the ServiceAssignmentRepository instance.
     *
     * @return ServiceAssignmentRepository
     */
    public ServiceAssignmentRepository serviceAssignmentRepository() {
        return this.serviceAssignmentRepository;
    }

    /**
     * Returns the VehicleRepository instance.
     *
     * @return VehicleRepository
     */
    public VehicleRepository vehicleRepository() {
        return this.vehicleRepository;
    }

    /**
     * Returns the DailyPlanRepository instance.
     *
     * @return DailyPlanRepository
     */
    public DailyPlanRepository dailyPlanRepository() {
        return this.dailyPlanRepository;
    }

    /**
     * Configure the MongoDB client with UUID support.
     *
     * @return configured MongoClient instance
     */
    @Override
    @Bean
    public @NonNull MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(this.uri);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .applyConnectionString(connectionString)
                .build();
        MongoClient client = MongoClients.create(mongoClientSettings);
        if (client == null) {
            throw new IllegalStateException(MONGO_CLIENT_CREATION_FAILED);
        }
        return client;
    }

    /**
     * Get the database name from configuration.
     *
     * @return database name
     */
    @Override
    protected @NonNull String getDatabaseName() {
        if (this.databaseName == null) {
            throw new IllegalStateException(DATABASE_NAME_NOT_CONFIGURED);
        }
        return this.databaseName;
    }

    /**
     * Configure the mapping converter to handle custom type conversions.
     *
     * @param databaseFactory   MongoDB database factory
     * @param customConversions custom conversions to apply
     * @param mappingContext    MongoDB mapping context
     * @return configured MappingMongoConverter
     */
    @Bean
    public @NonNull MappingMongoConverter mappingMongoConverter(
            @NonNull MongoDatabaseFactory databaseFactory,
            @NonNull MongoCustomConversions customConversions,
            @NonNull MongoMappingContext mappingContext) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(databaseFactory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mappingContext);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        converter.setCustomConversions(customConversions());
        converter.afterPropertiesSet();
        return converter;
    }

    /**
     * Define custom conversions for MongoDB.
     *
     * @return MongoCustomConversions instance
     */
    @Bean
    public @NonNull MongoCustomConversions customConversions() {
        return new MongoCustomConversions(
                Arrays.asList(
                        new ContainerReadingConverter(this),
                        new ContainerWritingConverter(this),
                        new ContainerDailyStateReadingConverter(),
                        new ContainerDailyStateWritingConverter(),
                        new DailyPlanReadingConverter(this),
                        new DailyPlanWritingConverter(this),
                        new FacilityReadingConverter(this),
                        new FacilityWritingConverter(this),
                        new InfrastructurePlanReadingConverter(this),
                        new InfrastructurePlanWritingConverter(this),
                        new ServiceAssignmentReadingConverter(this),
                        new ServiceAssignmentWritingConverter(this),
                        new VehicleReadingConverter(this),
                        new VehicleWritingConverter(this)));
    }
}
