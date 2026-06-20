package com.ull.domain.valueobject;

import com.ull.domain.enumerate.WasteType;
import com.ull.domain.valueobject.converter.WasteVolumeToMassConverter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class WasteVolumeToMassConverterTest {

  private static final double DELTA = 0.000001;

  /**
   * Tests conversion of organic waste liters to kilograms.
   */
  @Test
  void shouldConvertOrganicLitersToKilograms() {
    assertEquals(25.0, WasteVolumeToMassConverter.litersToKilograms(50.0, WasteType.ORGANIC), DELTA);
  }

  /**
   * Tests that liters are limited by remaining kilogram capacity.
   */
  @Test
  void shouldLimitLitersByRemainingKilogramCapacity() {
    assertEquals(120.0,
        WasteVolumeToMassConverter.litersFromRemainingKilograms(60.0, WasteType.ORGANIC),
        DELTA);
  }

  /**
   * Tests that different waste types use different densities.
   */
  @Test
  void shouldUseDifferentDensitiesPerWasteType() {
    assertEquals(4.0, WasteVolumeToMassConverter.litersToKilograms(50.0, WasteType.PACKAGING), DELTA);
    assertEquals(27.5, WasteVolumeToMassConverter.litersToKilograms(50.0, WasteType.GLASS), DELTA);
  }
}
