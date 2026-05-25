package es.ull.project.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import es.ull.project.adapter.rest.deserialization.algorithm.AlgorithmExecutionRequestBodyDeserializer;
import es.ull.project.adapter.rest.deserialization.container.ContainerBulkPostRequestBodyDeserializer;
import es.ull.project.adapter.rest.deserialization.container.ContainerPostRequestBodyDeserializer;
import es.ull.project.adapter.rest.deserialization.container.ContainerPutRequestBodyDeserializer;
import es.ull.project.adapter.rest.deserialization.facility.FacilityBulkPostRequestBodyDeserializer;
import es.ull.project.adapter.rest.deserialization.facility.FacilityPostRequestBodyDeserializer;
import es.ull.project.adapter.rest.deserialization.facility.FacilityPutRequestBodyDeserializer;
import es.ull.project.adapter.rest.deserialization.vehicle.VehicleBulkPostRequestBodyDeserializer;
import es.ull.project.adapter.rest.deserialization.vehicle.VehiclePostRequestBodyDeserializer;
import es.ull.project.adapter.rest.deserialization.vehicle.VehiclePutRequestBodyDeserializer;
import es.ull.project.adapter.rest.request.algorithm.AlgorithmExecutionRequestBody;
import es.ull.project.adapter.rest.request.container.ContainerBulkPostRequestBody;
import es.ull.project.adapter.rest.request.container.ContainerPostRequestBody;
import es.ull.project.adapter.rest.request.container.ContainerPutRequestBody;
import es.ull.project.adapter.rest.request.facility.FacilityBulkPostRequestBody;
import es.ull.project.adapter.rest.request.facility.FacilityPostRequestBody;
import es.ull.project.adapter.rest.request.facility.FacilityPutRequestBody;
import es.ull.project.adapter.rest.request.vehicle.VehicleBulkPostRequestBody;
import es.ull.project.adapter.rest.request.vehicle.VehiclePostRequestBody;
import es.ull.project.adapter.rest.request.vehicle.VehiclePutRequestBody;
import es.ull.project.adapter.rest.response.container.ContainerResponseBody;
import es.ull.project.adapter.rest.response.dailyplan.DailyPlanResponseBody;
import es.ull.project.adapter.rest.response.facility.FacilityResponseBody;
import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanListResponseBody;
import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanResponseBody;
import es.ull.project.adapter.rest.response.serviceassignment.ServiceAssignmentResponseBody;
import es.ull.project.adapter.rest.response.vehicle.VehicleResponseBody;
import es.ull.project.adapter.rest.serialization.container.ContainerResponseBodySerializer;
import es.ull.project.adapter.rest.serialization.dailyplan.DailyPlanResponseBodySerializer;
import es.ull.project.adapter.rest.serialization.facility.FacilityResponseBodySerializer;
import es.ull.project.adapter.rest.serialization.infrastructureplan.InfrastructurePlanListResponseBodySerializer;
import es.ull.project.adapter.rest.serialization.infrastructureplan.InfrastructurePlanResponseBodySerializer;
import es.ull.project.adapter.rest.serialization.serviceassignment.ServiceAssignmentResponseBodySerializer;
import es.ull.project.adapter.rest.serialization.vehicle.VehicleResponseBodySerializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * RestConfiguration
 * 
 * Configuration class for the REST adapter layer.
 * This class registers all custom Jackson serializers and deserializers
 * used for converting between JSON and Java objects in HTTP requests and
 * responses.
 * 
 * Each deserializer handles the conversion of JSON request bodies into DTOs,
 * while serializers handle the conversion of DTOs into JSON responses.
 */
@Configuration
public class RestConfiguration {

    /**
     * Registers all custom Jackson serializers and deserializers.
     * This method creates a SimpleModule and adds all the custom serializers
     * and deserializers for the different entity types (Vehicle, Container,
     * Facility, InfrastructurePlan, ServiceAssignment).
     * 
     * The ObjectMapper is configured with this custom module to handle
     * JSON serialization and deserialization for REST API requests and responses.
     * 
     * @param builder Jackson2ObjectMapperBuilder provided by Spring
     * @return Configured ObjectMapper with custom serializers and deserializers
     */
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.build();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(
                AlgorithmExecutionRequestBody.class,
                new AlgorithmExecutionRequestBodyDeserializer());
        module.addDeserializer(
                VehiclePostRequestBody.class,
                new VehiclePostRequestBodyDeserializer());
        module.addDeserializer(
                VehicleBulkPostRequestBody.class,
                new VehicleBulkPostRequestBodyDeserializer());
        module.addDeserializer(
                VehiclePutRequestBody.class,
                new VehiclePutRequestBodyDeserializer());
        module.addDeserializer(
                ContainerPostRequestBody.class,
                new ContainerPostRequestBodyDeserializer());
        module.addDeserializer(
                ContainerBulkPostRequestBody.class,
                new ContainerBulkPostRequestBodyDeserializer());
        module.addDeserializer(
                ContainerPutRequestBody.class,
                new ContainerPutRequestBodyDeserializer());
        module.addDeserializer(
                FacilityPostRequestBody.class,
                new FacilityPostRequestBodyDeserializer());
        module.addDeserializer(
                FacilityBulkPostRequestBody.class,
                new FacilityBulkPostRequestBodyDeserializer());
        module.addDeserializer(
                FacilityPutRequestBody.class,
                new FacilityPutRequestBodyDeserializer());
        module.addSerializer(
                VehicleResponseBody.class,
                new VehicleResponseBodySerializer());
        module.addSerializer(
                ContainerResponseBody.class,
                new ContainerResponseBodySerializer());
        module.addSerializer(
                FacilityResponseBody.class,
                new FacilityResponseBodySerializer());
        module.addSerializer(
                InfrastructurePlanResponseBody.class,
                new InfrastructurePlanResponseBodySerializer());
        module.addSerializer(
                InfrastructurePlanListResponseBody.class,
                new InfrastructurePlanListResponseBodySerializer());
        module.addSerializer(
                DailyPlanResponseBody.class,
                new DailyPlanResponseBodySerializer());
        module.addSerializer(
                ServiceAssignmentResponseBody.class,
                new ServiceAssignmentResponseBodySerializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
