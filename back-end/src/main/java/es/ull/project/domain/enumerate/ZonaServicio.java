package es.ull.project.domain.enumerate;

import java.util.Random;

/**
 * Enum representing different service zones. (Optional)
 */
public enum ZonaServicio {

    NORTE, // if needed, put all the municipalities of the island
    SUR,
    ESTE,
    OESTE,
    CENTRO;

    public static ZonaServicio fromString(String stringToCheck) {
        if (stringToCheck == null) {
            throw new IllegalArgumentException();
        }
        stringToCheck = stringToCheck.trim().toUpperCase();
        for (ZonaServicio zonaServicio : values()) {
            if (zonaServicio.name().equals(stringToCheck)) {
                return zonaServicio;
            }
        }
        throw new IllegalArgumentException();
    }

    public static int indexOf(String stringToCheck) {
        return ZonaServicio
                .fromString(stringToCheck)
                .ordinal();
    }

    public static boolean isValid(String stringToCheck) {
        if (stringToCheck == null) {
            return false;
        }
        stringToCheck = stringToCheck.trim().toUpperCase();
        for (ZonaServicio zonaServicio : values()) {
            if (zonaServicio.name().equals(stringToCheck)) {
                return true;
            }
        }
        return false;
    }

    public static ZonaServicio random() {
        return values()[new Random().nextInt(values().length)];
    }
}
