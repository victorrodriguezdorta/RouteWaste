package es.ull.project.adapter.mongo.document.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import es.ull.project.adapter.mongo.document.valueobject.MaximumBudgetDocument;
import es.ull.project.adapter.mongo.document.valueobject.ServicePoliciesDocument;
import es.ull.project.adapter.mongo.document.valueobject.TotalCostDocument;

@Document(collection = "infrastructure_plan")
public class InfrastructurePlanDocument {
    @Id
    private String id;

    private String planningPeriod;
    private List<String> selectedFacilityIds;
    private List<String> serviceAssignmentIds;
    private ServicePoliciesDocument servicePolicies;
    private MaximumBudgetDocument maxBudget;
    private TotalCostDocument estimatedTotalCost;

    public InfrastructurePlanDocument() {
    }

    public InfrastructurePlanDocument(String id, String planningPeriod, List<String> selectedFacilityIds, List<String> serviceAssignmentIds, ServicePoliciesDocument servicePolicies, MaximumBudgetDocument maxBudget, TotalCostDocument estimatedTotalCost) {
        this.id = id;
        this.planningPeriod = planningPeriod;
        this.selectedFacilityIds = selectedFacilityIds;
        this.serviceAssignmentIds = serviceAssignmentIds;
        this.servicePolicies = servicePolicies;
        this.maxBudget = maxBudget;
        this.estimatedTotalCost = estimatedTotalCost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlanningPeriod() {
        return planningPeriod;
    }

    public void setPlanningPeriod(String planningPeriod) {
        this.planningPeriod = planningPeriod;
    }

    public List<String> getSelectedFacilityIds() {
        return selectedFacilityIds;
    }

    public void setSelectedFacilityIds(List<String> selectedFacilityIds) {
        this.selectedFacilityIds = selectedFacilityIds;
    }

    public List<String> getServiceAssignmentIds() {
        return serviceAssignmentIds;
    }

    public void setServiceAssignmentIds(List<String> serviceAssignmentIds) {
        this.serviceAssignmentIds = serviceAssignmentIds;
    }

    public ServicePoliciesDocument getServicePolicies() {
        return servicePolicies;
    }

    public void setServicePolicies(ServicePoliciesDocument servicePolicies) {
        this.servicePolicies = servicePolicies;
    }

    public MaximumBudgetDocument getMaxBudget() {
        return maxBudget;
    }

    public void setMaxBudget(MaximumBudgetDocument maxBudget) {
        this.maxBudget = maxBudget;
    }

    public TotalCostDocument getEstimatedTotalCost() {
        return estimatedTotalCost;
    }

    public void setEstimatedTotalCost(TotalCostDocument estimatedTotalCost) {
        this.estimatedTotalCost = estimatedTotalCost;
    }
}
