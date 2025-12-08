package es.ull.project.domain.enumerate;

import java.util.Random;

/**
 * Enum representing different types of infrastructure.
 */
public enum TipoInfraestructura {

    BASE_OPERATIVA,
    ESTACION_TRANSFERENCIA,
    PLANTA_TRATAMIENTO;

    public static TipoInfraestructura fromString(String stringToCheck) {
        if (stringToCheck == null) {
            throw new IllegalArgumentException();
        }
        stringToCheck = stringToCheck.trim().toUpperCase();
        for (TipoInfraestructura tipo : values()) {
            if (tipo.name().equals(stringToCheck)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException();
    }

    public static int indexOf(String stringToCheck) {
        return TipoInfraestructura
                .fromString(stringToCheck)
                .ordinal();
    }

    public static boolean isValid(String stringToCheck) {
        if (stringToCheck == null) {
            return false;
        }
        stringToCheck = stringToCheck.trim().toUpperCase();
        for (TipoInfraestructura tipo : values()) {
            if (tipo.name().equals(stringToCheck)) {
                return true;
            }
        }
        return false;
    }

    public static TipoInfraestructura random() {
        return values()[new Random().nextInt(values().length)];
    }
}