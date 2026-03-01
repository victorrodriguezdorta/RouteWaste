package es.ull.project.adapter.mongo.document.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import es.ull.project.adapter.mongo.document.valueobject.CapacityDocument;
import es.ull.project.adapter.mongo.document.valueobject.LocationDocument;
import es.ull.project.adapter.mongo.document.valueobject.OpeningFixedCostDocument;
import es.ull.project.adapter.mongo.document.valueobject.WasteDemandDocument;

@Document(collection = "facilities")
public class FacilityDocument {

    @Id
    private String id;

    private String facilityType;

    private LocationDocument location;

    private CapacityDocument capacity;

    private OpeningFixedCostDocument openingFixedCost;

    private String status;

    private WasteDemandDocument assignedWasteDemand;

    public FacilityDocument() {
    }

    public FacilityDocument(String id, String facilityType, LocationDocument location, CapacityDocument capacity, OpeningFixedCostDocument openingFixedCost, String status, WasteDemandDocument assignedWasteDemand) {
        this.id = id;
        this.facilityType = facilityType;
        this.location = location;
        this.capacity = capacity;
        this.openingFixedCost = openingFixedCost;
        this.status = status;
        this.assignedWasteDemand = assignedWasteDemand;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public LocationDocument getLocation() {
        return location;
    }

    public void setLocation(LocationDocument location) {
        this.location = location;
    }

    public CapacityDocument getCapacity() {
        return capacity;
    }

    public void setCapacity(CapacityDocument capacity) {
        this.capacity = capacity;
    }

    public OpeningFixedCostDocument getOpeningFixedCost() {
        return openingFixedCost;
    }

    public void setOpeningFixedCost(OpeningFixedCostDocument openingFixedCost) {
        this.openingFixedCost = openingFixedCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public WasteDemandDocument getAssignedWasteDemand() {
        return assignedWasteDemand;
    }

    public void setAssignedWasteDemand(WasteDemandDocument assignedWasteDemand) {
        this.assignedWasteDemand = assignedWasteDemand;
    }
}
