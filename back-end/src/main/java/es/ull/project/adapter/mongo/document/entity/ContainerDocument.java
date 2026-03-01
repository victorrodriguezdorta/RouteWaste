package es.ull.project.adapter.mongo.document.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import es.ull.project.adapter.mongo.document.valueobject.LocationDocument;
import es.ull.project.adapter.mongo.document.valueobject.WasteDemandDocument;

/**
 * MongoDB document representation for Container aggregate.
 */
@Document(collection = "containers")
public class ContainerDocument {

    @Id
    private String id;

    private LocationDocument location;

    private String wasteType;

    private WasteDemandDocument wasteDemand;

    private String serviceZone; // may be null

    public ContainerDocument() {
    }

    public ContainerDocument(String id, LocationDocument location, String wasteType, WasteDemandDocument wasteDemand, String serviceZone) {
        this.id = id;
        this.location = location;
        this.wasteType = wasteType;
        this.wasteDemand = wasteDemand;
        this.serviceZone = serviceZone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocationDocument getLocation() {
        return location;
    }

    public void setLocation(LocationDocument location) {
        this.location = location;
    }

    public String getWasteType() {
        return wasteType;
    }

    public void setWasteType(String wasteType) {
        this.wasteType = wasteType;
    }

    public WasteDemandDocument getWasteDemand() {
        return wasteDemand;
    }

    public void setWasteDemand(WasteDemandDocument wasteDemand) {
        this.wasteDemand = wasteDemand;
    }

    public String getServiceZone() {
        return serviceZone;
    }

    public void setServiceZone(String serviceZone) {
        this.serviceZone = serviceZone;
    }
}
