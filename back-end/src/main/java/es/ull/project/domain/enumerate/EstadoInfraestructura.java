package es.ull.project.domain.enumerate;

import java.util.Random;

/**
 * Enum representing different states of infrastructure.
 */
public enum EstadoInfraestructura {

    CANDIDATA,
    PLANIFICADA,
    ABIERTA,
    DESCARTADA;

    public static EstadoInfraestructura fromString(String stringToCheck) {
        if (stringToCheck == null) {
            throw new IllegalArgumentException();
        }
        stringToCheck = stringToCheck.trim().toUpperCase();
        for (EstadoInfraestructura estado : values()) {
            if (estado.name().equals(stringToCheck)) {
                return estado;
            }
        }
        throw new IllegalArgumentException();
    }

    public static int indexOf(String stringToCheck) {
        return EstadoInfraestructura
                .fromString(stringToCheck)
                .ordinal();
    }

    public static boolean isValid(String stringToCheck) {
        if (stringToCheck == null) {
            return false;
        }
        stringToCheck = stringToCheck.trim().toUpperCase();
        for (EstadoInfraestructura estado : values()) {
            if (estado.name().equals(stringToCheck)) {
                return true;
            }
        }
        return false;
    }

    public static EstadoInfraestructura random() {
        return values()[new Random().nextInt(values().length)];
    }
}
