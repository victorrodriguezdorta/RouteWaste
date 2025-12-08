package es.ull.project.domain.entity;

import java.util.Objects;
import java.util.UUID;

public class Vehicle {

    /**
     * Identifier of the vehicle. It is a computed attribute.
     */
    private final UUID vehiculoId;

    /**
     * Type of vehicle.
     */
    private String tipoVehiculo;

    /**
     * Vehicle capacity.
     */
    private double capacidadVehiculo;

    /**
     * Operating cost per kilometer.
     */
    private double costeOperacionPorKm;

    public Vehicle(String tipoVehiculo,
                   double capacidadVehiculo,
                   double costeOperacionPorKm) {

        this.vehiculoId = UUID.randomUUID();
        this.tipoVehiculo = tipoVehiculo;
        this.capacidadVehiculo = capacidadVehiculo;
        this.costeOperacionPorKm = costeOperacionPorKm;
    }

    public UUID getVehiculoId() {
        return this.vehiculoId;
    }

    public String getTipoVehiculo() {
        return this.tipoVehiculo;
    }

    public void setTipoVehiculo(String tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public double getCapacidadVehiculo() {
        return this.capacidadVehiculo;
    }

    public void setCapacidadVehiculo(double capacidadVehiculo) {
        this.capacidadVehiculo = capacidadVehiculo;
    }

    public double getCosteOperacionPorKm() {
        return this.costeOperacionPorKm;
    }

    public void setCosteOperacionPorKm(double costeOperacionPorKm) {
        this.costeOperacionPorKm = costeOperacionPorKm;
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
        final Vehicle otherVehicle = (Vehicle) otherObject;
        return Objects.equals(this.vehiculoId, otherVehicle.vehiculoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.vehiculoId);
    }

    @Override
    public String toString() {
        return String.format(
            "Vehicle={id=%s, tipoVehiculo=%s, capacidad=%s, costePorKm=%s}",
            this.vehiculoId,
            this.tipoVehiculo,
            this.capacidadVehiculo,
            this.costeOperacionPorKm
        );
    }
}