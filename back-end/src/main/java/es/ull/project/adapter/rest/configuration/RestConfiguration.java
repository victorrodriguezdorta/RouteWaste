package es.ull.project.adapter.rest.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import es.ull.project.adapter.rest.deserialization.container.ContainerPostRequestBodyDeserializer;
import es.ull.project.adapter.rest.deserialization.container.ContainerPutRequestBodyDeserializer;
import es.ull.project.adapter.rest.deserialization.facility.FacilityPostRequestBodyDeserializer;
import es.ull.project.adapter.rest.deserialization.facility.FacilityPutRequestBodyDeserializer;
import es.ull.project.adapter.rest.deserialization.infrastructureplan.InfrastructurePlanPostRequestBodyDeserializer;
import es.ull.project.adapter.rest.deserialization.infrastructureplan.InfrastructurePlanPutRequestBodyDeserializer;
import es.ull.project.adapter.rest.deserialization.serviceassignment.ServiceAssignmentPostRequestBodyDeserializer;
import es.ull.project.adapter.rest.deserialization.serviceassignment.ServiceAssignmentPutRequestBodyDeserializer;
import es.ull.project.adapter.rest.deserialization.vehicle.VehiclePostRequestBodyDeserializer;
import es.ull.project.adapter.rest.deserialization.vehicle.VehiclePutRequestBodyDeserializer;
import es.ull.project.adapter.rest.request.container.ContainerPostRequestBody;
import es.ull.project.adapter.rest.request.container.ContainerPutRequestBody;
import es.ull.project.adapter.rest.request.facility.FacilityPostRequestBody;
import es.ull.project.adapter.rest.request.facility.FacilityPutRequestBody;
import es.ull.project.adapter.rest.request.infrastructureplan.InfrastructurePlanPostRequestBody;
import es.ull.project.adapter.rest.request.infrastructureplan.InfrastructurePlanPutRequestBody;
import es.ull.project.adapter.rest.request.serviceassignment.ServiceAssignmentPostRequestBody;
import es.ull.project.adapter.rest.request.serviceassignment.ServiceAssignmentPutRequestBody;
import es.ull.project.adapter.rest.request.vehicle.VehiclePostRequestBody;
import es.ull.project.adapter.rest.request.vehicle.VehiclePutRequestBody;
import es.ull.project.adapter.rest.response.container.ContainerResponseBody;
import es.ull.project.adapter.rest.response.facility.FacilityResponseBody;
import es.ull.project.adapter.rest.response.infrastructureplan.InfrastructurePlanResponseBody;
import es.ull.project.adapter.rest.response.serviceassignment.ServiceAssignmentResponseBody;
import es.ull.project.adapter.rest.response.vehicle.VehicleResponseBody;
import es.ull.project.adapter.rest.serialization.container.ContainerResponseBodySerializer;
import es.ull.project.adapter.rest.serialization.facility.FacilityResponseBodySerializer;
import es.ull.project.adapter.rest.serialization.infrastructureplan.InfrastructurePlanResponseBodySerializer;
import es.ull.project.adapter.rest.serialization.serviceassignment.ServiceAssignmentResponseBodySerializer;
import es.ull.project.adapter.rest.serialization.vehicle.VehicleResponseBodySerializer;

/**
 * RestConfiguration
 * 
 * Configuration class for the REST adapter layer.
 * This class registers all custom Jackson serializers and deserializers
 * used for converting between JSON and Java objects in HTTP requests and responses.
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
                VehiclePostRequestBody.class,
                new VehiclePostRequestBodyDeserializer());
        module.addDeserializer(
                VehiclePutRequestBody.class,
                new VehiclePutRequestBodyDeserializer());
        module.addDeserializer(
                ContainerPostRequestBody.class,
                new ContainerPostRequestBodyDeserializer());
        module.addDeserializer(
                ContainerPutRequestBody.class,
                new ContainerPutRequestBodyDeserializer());
        module.addDeserializer(
                FacilityPostRequestBody.class,
                new FacilityPostRequestBodyDeserializer());
        module.addDeserializer(
                FacilityPutRequestBody.class,
                new FacilityPutRequestBodyDeserializer());
        module.addDeserializer(
                InfrastructurePlanPostRequestBody.class,
                new InfrastructurePlanPostRequestBodyDeserializer());
        module.addDeserializer(
                InfrastructurePlanPutRequestBody.class,
                new InfrastructurePlanPutRequestBodyDeserializer());
        module.addDeserializer(
                ServiceAssignmentPostRequestBody.class,
                new ServiceAssignmentPostRequestBodyDeserializer());
        module.addDeserializer(
                ServiceAssignmentPutRequestBody.class,
                new ServiceAssignmentPutRequestBodyDeserializer());
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
                ServiceAssignmentResponseBody.class,
                new ServiceAssignmentResponseBodySerializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
