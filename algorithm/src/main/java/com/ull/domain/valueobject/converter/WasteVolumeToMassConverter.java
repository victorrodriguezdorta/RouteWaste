package com.ull.domain.valueobject.converter;

import com.ull.domain.enumerate.WasteType;

/**
 * Converts collected waste volume (liters) to mass (kilograms) using nominal bulk
 * densities per waste stream. Volume and mass are not interchangeable: the same liter
 * capacity holds different kg depending on material (e.g. paper vs organic).
 */
public final class WasteVolumeToMassConverter {

  private static final double ORGANIC_KG_PER_LITER = 0.50;
  private static final double PACKAGING_KG_PER_LITER = 0.08;
  private static final double PAPER_CARDBOARD_KG_PER_LITER = 0.15;
  private static final double GLASS_KG_PER_LITER = 0.55;
  private static final double RESIDUAL_KG_PER_LITER = 0.40;
  private static final double DEFAULT_KG_PER_LITER = RESIDUAL_KG_PER_LITER;

  private WasteVolumeToMassConverter() {
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * Nominal bulk density for the waste type (kg per liter of collected volume).
   *
   * @param wasteType container waste stream
   * @return density in kg/L
   */
  public static double kilogramsPerLiter(WasteType wasteType) {
    if (wasteType == null) {
      return DEFAULT_KG_PER_LITER;
    }
    return switch (wasteType) {
      case ORGANIC -> ORGANIC_KG_PER_LITER;
      case PACKAGING -> PACKAGING_KG_PER_LITER;
      case PAPER_CARDBOARD -> PAPER_CARDBOARD_KG_PER_LITER;
      case GLASS -> GLASS_KG_PER_LITER;
      case RESIDUAL -> RESIDUAL_KG_PER_LITER;
    };
  }

  /**
   * Converts a collected volume to mass using the waste-type density.
   *
   * @param liters collected volume in liters
   * @param wasteType container waste stream
   * @return mass in kilograms
   */
  public static double litersToKilograms(double liters, WasteType wasteType) {
    if (liters <= 0.0) {
      return 0.0;
    }
    return liters * kilogramsPerLiter(wasteType);
  }

  /**
   * Maximum liters that fit in the remaining kilogram capacity for a waste type.
   *
   * @param remainingKilograms remaining kg capacity on the vehicle
   * @param wasteType container waste stream
   * @return liters limit implied by kg capacity
   */
  public static double litersFromRemainingKilograms(double remainingKilograms, WasteType wasteType) {
    if (remainingKilograms <= 0.0) {
      return 0.0;
    }
    double density = kilogramsPerLiter(wasteType);
    if (density <= 0.0) {
      return Double.MAX_VALUE;
    }
    return remainingKilograms / density;
  }
}
