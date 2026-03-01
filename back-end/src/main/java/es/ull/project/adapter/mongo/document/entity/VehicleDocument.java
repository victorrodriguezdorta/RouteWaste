package es.ull.project.adapter.mongo.document.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import es.ull.project.adapter.mongo.document.valueobject.CapacityDocument;
import es.ull.project.adapter.mongo.document.valueobject.TransportationVariableCostDocument;

@Document(collection = "vehicles")
public class VehicleDocument {

    @Id
    private String id;

    private String vehicleType;

    private CapacityDocument transportCapacity;

    private TransportationVariableCostDocument costPerKilometer;

    public VehicleDocument() {
    }

    public VehicleDocument(String id, String vehicleType, CapacityDocument transportCapacity, TransportationVariableCostDocument costPerKilometer) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.transportCapacity = transportCapacity;
        this.costPerKilometer = costPerKilometer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public CapacityDocument getTransportCapacity() {
        return transportCapacity;
    }

    public void setTransportCapacity(CapacityDocument transportCapacity) {
        this.transportCapacity = transportCapacity;
    }

    public TransportationVariableCostDocument getCostPerKilometer() {
        return costPerKilometer;
    }

    public void setCostPerKilometer(TransportationVariableCostDocument costPerKilometer) {
        this.costPerKilometer = costPerKilometer;
    }
}
