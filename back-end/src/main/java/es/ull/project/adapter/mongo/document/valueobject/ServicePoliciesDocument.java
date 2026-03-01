package es.ull.project.adapter.mongo.document.valueobject;

/**
 * DTO for ServicePolicies value object.
 */
public class ServicePoliciesDocument {
    private Double maxServiceDistance;
    private Integer maxServiceTime;
    private Integer maxInfrastructureCount;
    private Double maxEmissions;

    public ServicePoliciesDocument() {
    }

    public ServicePoliciesDocument(Double maxServiceDistance, Integer maxServiceTime, Integer maxInfrastructureCount, Double maxEmissions) {
        this.maxServiceDistance = maxServiceDistance;
        this.maxServiceTime = maxServiceTime;
        this.maxInfrastructureCount = maxInfrastructureCount;
        this.maxEmissions = maxEmissions;
    }

    public Double getMaxServiceDistance() {
        return maxServiceDistance;
    }

    public void setMaxServiceDistance(Double maxServiceDistance) {
        this.maxServiceDistance = maxServiceDistance;
    }

    public Integer getMaxServiceTime() {
        return maxServiceTime;
    }

    public void setMaxServiceTime(Integer maxServiceTime) {
        this.maxServiceTime = maxServiceTime;
    }

    public Integer getMaxInfrastructureCount() {
        return maxInfrastructureCount;
    }

    public void setMaxInfrastructureCount(Integer maxInfrastructureCount) {
        this.maxInfrastructureCount = maxInfrastructureCount;
    }

    public Double getMaxEmissions() {
        return maxEmissions;
    }

    public void setMaxEmissions(Double maxEmissions) {
        this.maxEmissions = maxEmissions;
    }
}
