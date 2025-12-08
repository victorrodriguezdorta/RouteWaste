package es.ull.project.domain.entity;
// TODO: Change the types of some attributes as needed

import java.util.Objects;
import java.util.UUID;

public class InfrastructurePlan {

    /**
     * Identifier of the infrastructure plan. It is a computed attribute.
     */
    private final UUID planId;

    /**
     * Planning period (e.g. year, quarter).
     */
    private String periodoPlanificacion;

    /**
     * Selected infrastructures for the plan.
     */
    private int infraestructuras;

    /**
     * Service assignments (one per container).
     */
    private int asignacionesServicio;

    /**
     * Service policies applied to the plan.
     */
    private String politicasServicio;

    /**
     * Maximum available budget.
     */
    private double presupuestoMaximo;

    /**
     * Estimated total cost of the plan.
     */
    private double costeTotalEstimado;

    public InfrastructurePlan(String periodoPlanificacion,
                              int infraestructuras,
                              int asignacionesServicio,
                              String politicasServicio,
                              double presupuestoMaximo,
                              double costeTotalEstimado) {

        this.planId = UUID.randomUUID();
        this.periodoPlanificacion = periodoPlanificacion;
        this.infraestructuras = infraestructuras;
        this.asignacionesServicio = asignacionesServicio;
        this.politicasServicio = politicasServicio;
        this.presupuestoMaximo = presupuestoMaximo;
        this.costeTotalEstimado = costeTotalEstimado;
    }

    public UUID getPlanId() {
        return this.planId;
    }

    public String getPeriodoPlanificacion() {
        return this.periodoPlanificacion;
    }

    public void setPeriodoPlanificacion(String periodoPlanificacion) {
        this.periodoPlanificacion = periodoPlanificacion;
    }

    public int getInfraestructuras() {
        return this.infraestructuras;
    }

    public void setInfraestructuras(int infraestructuras) {
        this.infraestructuras = infraestructuras;
    }

    public int getAsignacionesServicio() {
        return this.asignacionesServicio;
    }

    public void setAsignacionesServicio(int asignacionesServicio) {
    this.asignacionesServicio = asignacionesServicio;
    }

    public String getPoliticasServicio() {
        return this.politicasServicio;
    }

    public void setPoliticasServicio(String politicasServicio) {
        this.politicasServicio = politicasServicio;
    }

    public double getPresupuestoMaximo() {
        return this.presupuestoMaximo;
    }

    public void setPresupuestoMaximo(double presupuestoMaximo) {
        this.presupuestoMaximo = presupuestoMaximo;
    }

    public double getCosteTotalEstimado() {
        return this.costeTotalEstimado;
    }

    public void setCosteTotalEstimado(double costeTotalEstimado) {
        this.costeTotalEstimado = costeTotalEstimado;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null) {
            return false;
        }
        if (getClass() != otherObject.getClass()) {
            return false;
        }
        final InfrastructurePlan otherPlan = (InfrastructurePlan) otherObject;
        return Objects.equals(this.planId, otherPlan.planId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.planId);
    }

    @Override
    public String toString() {
        return String.format(
            "InfrastructurePlan={id=%s, periodo=%s, infraestructuras=%s, asignaciones=%s, politicas=%s, presupuestoMaximo=%s, costeTotalEstimado=%s}",
            this.planId,
            this.periodoPlanificacion,
            this.infraestructuras,
            this.asignacionesServicio,
            this.politicasServicio,
            this.presupuestoMaximo,
            this.costeTotalEstimado
        );
    }
}