package es.ull.project.domain.entity;
// TODO: Change the types of some attributes as needed

import java.util.Objects;
import java.util.UUID;

public class Facility {

    /**
     * Identifier of the infrastructure. It is a computed attribute.
     */
    private final UUID infraestructuraId;

    /**
     * Type of the infrastructure (e.g. base, transfer station, treatment plant).
     */
    private String tipoInfraestructura;

    /**
     * Physical location of the infrastructure.
     */
    private String ubicacion;

    /**
     * Maximum capacity of the infrastructure.
     */
    private double capacidad;

    /**
     * Fixed cost required to open the infrastructure.
     */
    private double costeFijoApertura;

    /**
     * Current state of the infrastructure (e.g. candidate, planned, open, discarded).
     */
    private String estadoInfraestructura;

    public Facility(String tipoInfraestructura,
                    String ubicacion,
                    double capacidad,
                    double costeFijoApertura,
                    String estadoInfraestructura) {

        this.infraestructuraId = UUID.randomUUID();
        this.tipoInfraestructura = tipoInfraestructura;
        this.ubicacion = ubicacion;
        this.capacidad = capacidad;
        this.costeFijoApertura = costeFijoApertura;
        this.estadoInfraestructura = estadoInfraestructura;
    }

    public UUID getInfraestructuraId() {
        return this.infraestructuraId;
    }

    public String getTipoInfraestructura() {
        return this.tipoInfraestructura;
    }

    public void setTipoInfraestructura(String tipoInfraestructura) {
        this.tipoInfraestructura = tipoInfraestructura;
    }

    public String getUbicacion() {
        return this.ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public double getCapacidad() {
        return this.capacidad;
    }

    public void setCapacidad(double capacidad) {
        this.capacidad = capacidad;
    }

    public double getCosteFijoApertura() {
        return this.costeFijoApertura;
    }

    public void setCosteFijoApertura(double costeFijoApertura) {
        this.costeFijoApertura = costeFijoApertura;
    }

    public String getEstadoInfraestructura() {
        return this.estadoInfraestructura;
    }

    public void setEstadoInfraestructura(String estadoInfraestructura) {
        this.estadoInfraestructura = estadoInfraestructura;
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
        final Facility otherFacility = (Facility) otherObject;
        return Objects.equals(this.infraestructuraId, otherFacility.infraestructuraId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.infraestructuraId);
    }

    @Override
    public String toString() {
        return String.format(
            "Facility={id=%s, tipo=%s, ubicacion=%s, capacidad=%s, costeFijoApertura=%s, estado=%s}",
            this.infraestructuraId,
            this.tipoInfraestructura,
            this.ubicacion,
            this.capacidad,
            this.costeFijoApertura,
            this.estadoInfraestructura
        );
    }
}