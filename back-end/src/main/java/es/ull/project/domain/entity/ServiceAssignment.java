package es.ull.project.domain.entity;

import java.util.Objects;
import java.util.UUID;

public class ServiceAssignment {

    /**
     * Identifier of the service assignment. It is a computed attribute.
     */
    private final UUID serviceAssignmentId;

    /**
     * Identifier of the container involved in the assignment.
     */
    private UUID contenedorId;

    /**
     * Identifier of the infrastructure involved in the assignment.
     */
    private UUID infraestructuraId;

    /**
     * Waste demand associated with this assignment.
     */
    private double demandaResiduos;

    /**
     * Distance between container and infrastructure.
     */
    private double distancia;

    /**
     * Service time required for this assignment.
     */
    private double tiempoServicio;

    /**
     * Transport cost associated with this assignment.
     */
    private double costeTransporte;

    public ServiceAssignment(UUID contenedorId,
                             UUID infraestructuraId,
                             double demandaResiduos,
                             double distancia,
                             double tiempoServicio,
                             double costeTransporte) {

        this.serviceAssignmentId = UUID.randomUUID();
        this.contenedorId = contenedorId;
        this.infraestructuraId = infraestructuraId;
        this.demandaResiduos = demandaResiduos;
        this.distancia = distancia;
        this.tiempoServicio = tiempoServicio;
        this.costeTransporte = costeTransporte;
    }

    public UUID getServiceAssignmentId() {
        return this.serviceAssignmentId;
    }

    public UUID getContenedorId() {
        return this.contenedorId;
    }

    public void setContenedorId(UUID contenedorId) {
        this.contenedorId = contenedorId;
    }

    public UUID getInfraestructuraId() {
        return this.infraestructuraId;
    }

    public void setInfraestructuraId(UUID infraestructuraId) {
        this.infraestructuraId = infraestructuraId;
    }

    public double getDemandaResiduos() {
        return this.demandaResiduos;
    }

    public void setDemandaResiduos(double demandaResiduos) {
        this.demandaResiduos = demandaResiduos;
    }

    public double getDistancia() {
        return this.distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public double getTiempoServicio() {
        return this.tiempoServicio;
    }

    public void setTiempoServicio(double tiempoServicio) {
        this.tiempoServicio = tiempoServicio;
    }

    public double getCosteTransporte() {
        return this.costeTransporte;
    }

    public void setCosteTransporte(double costeTransporte) {
        this.costeTransporte = costeTransporte;
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
        final ServiceAssignment otherAssignment = (ServiceAssignment) otherObject;
        return Objects.equals(this.serviceAssignmentId, otherAssignment.serviceAssignmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.serviceAssignmentId);
    }

    @Override
    public String toString() {
        return String.format(
            "ServiceAssignment={id=%s, contenedorId=%s, infraestructuraId=%s, demanda=%s, distancia=%s, tiempoServicio=%s, costeTransporte=%s}",
            this.serviceAssignmentId,
            this.contenedorId,
            this.infraestructuraId,
            this.demandaResiduos,
            this.distancia,
            this.tiempoServicio,
            this.costeTransporte
        );
    }
}