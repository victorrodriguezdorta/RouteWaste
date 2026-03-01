package es.ull.project.adapter.mongo.document.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import es.ull.project.adapter.mongo.document.valueobject.DistanceDocument;
import es.ull.project.adapter.mongo.document.valueobject.ServiceTimeDocument;
import es.ull.project.adapter.mongo.document.valueobject.TransportationVariableCostDocument;
import es.ull.project.adapter.mongo.document.valueobject.WasteDemandDocument;

@Document(collection = "service_assignment")
public class ServiceAssignmentDocument {
    @Id
    private String id;

    private String containerId;
    private String facilityId;
    private DistanceDocument distance;
    private ServiceTimeDocument serviceTime;
    private WasteDemandDocument wasteDemand;
    private TransportationVariableCostDocument transportCost;

    public ServiceAssignmentDocument() {
    }

    public ServiceAssignmentDocument(String id, String containerId, String facilityId, DistanceDocument distance, ServiceTimeDocument serviceTime, WasteDemandDocument wasteDemand, TransportationVariableCostDocument transportCost) {
        this.id = id;
        this.containerId = containerId;
        this.facilityId = facilityId;
        this.distance = distance;
        this.serviceTime = serviceTime;
        this.wasteDemand = wasteDemand;
        this.transportCost = transportCost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public DistanceDocument getDistance() {
        return distance;
    }

    public void setDistance(DistanceDocument distance) {
        this.distance = distance;
    }

    public ServiceTimeDocument getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(ServiceTimeDocument serviceTime) {
        this.serviceTime = serviceTime;
    }

    public WasteDemandDocument getWasteDemand() {
        return wasteDemand;
    }

    public void setWasteDemand(WasteDemandDocument wasteDemand) {
        this.wasteDemand = wasteDemand;
    }

    public TransportationVariableCostDocument getTransportCost() {
        return transportCost;
    }

    public void setTransportCost(TransportationVariableCostDocument transportCost) {
        this.transportCost = transportCost;
    }
}
