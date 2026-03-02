package es.ull.project.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import es.ull.project.adapter.mongodb.repository.ContainerMongoRepository;
import es.ull.project.adapter.mongodb.repository.FacilityMongoRepository;
import es.ull.project.adapter.mongodb.repository.InfrastructurePlanMongoRepository;
import es.ull.project.adapter.mongodb.repository.ServiceAssignmentMongoRepository;
import es.ull.project.adapter.mongodb.repository.VehicleMongoRepository;
import es.ull.project.application.repository.ContainerRepository;
import es.ull.project.application.repository.FacilityRepository;
import es.ull.project.application.repository.InfrastructurePlanRepository;
import es.ull.project.application.repository.ServiceAssignmentRepository;
import es.ull.project.application.repository.VehicleRepository;

import java.util.Arrays;

import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                Arrays.asList());
    }

    /**
     * Create ContainerRepository bean.
     *
     * @return ContainerRepository implementation
     */
    @Bean
    public ContainerRepository containerRepository() {
        return new ContainerMongoRepository();
    }

    /**
     * Create FacilityRepository bean.
     *
     * @return FacilityRepository implementation
     */
    @Bean
    public FacilityRepository facilityRepository() {
        return new FacilityMongoRepository();
    }

    /**
     * Create VehicleRepository bean.
     *
     * @return VehicleRepository implementation
     */
    @Bean
    public VehicleRepository vehicleRepository() {
        return new VehicleMongoRepository();
    }

    /**
     * Create ServiceAssignmentRepository bean.
     *
     * @return ServiceAssignmentRepository implementation
     */
    @Bean
    public ServiceAssignmentRepository serviceAssignmentRepository() {
        return new ServiceAssignmentMongoRepository();
    }

    /**
     * Create InfrastructurePlanRepository bean.
     *
     * @return InfrastructurePlanRepository implementation
     */
    @Bean
    public InfrastructurePlanRepository infrastructurePlanRepository() {
        return new InfrastructurePlanMongoRepository();
    }
}
