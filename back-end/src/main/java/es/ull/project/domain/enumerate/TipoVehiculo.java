package es.ull.project.domain.enumerate;

import java.util.Random;

/**
 * Enum representing different types of vehicles.
 */
public enum TipoVehiculo {

    CAMION_RECOGIDA,
    CAMION_TRANSFERENCIA,
    VEHICULO_APOYO;

    public static TipoVehiculo fromString(String stringToCheck) {
        if (stringToCheck == null) {
            throw new IllegalArgumentException();
        }
        stringToCheck = stringToCheck.trim().toUpperCase();
        for (TipoVehiculo tipoVehiculo : values()) {
            if (tipoVehiculo.name().equals(stringToCheck)) {
                return tipoVehiculo;
            }
        }
        throw new IllegalArgumentException();
    }

    public static int indexOf(String stringToCheck) {
        return TipoVehiculo
                .fromString(stringToCheck)
                .ordinal();
    }

    public static boolean isValid(String stringToCheck) {
        if (stringToCheck == null) {
            return false;
        }
        stringToCheck = stringToCheck.trim().toUpperCase();
        for (TipoVehiculo tipoVehiculo : values()) {
            if (tipoVehiculo.name().equals(stringToCheck)) {
                return true;
            }
        }
        return false;
    }

    public static TipoVehiculo random() {
        return values()[new Random().nextInt(values().length)];
    }
}