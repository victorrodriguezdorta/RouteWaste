package com.ull.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ull.domain.enumerate.WasteType;
import com.ull.domain.valueobject.converter.WasteVolumeToMassConverter;

import org.junit.jupiter.api.Test;

class WasteVolumeToMassConverterTest {

  private static final double DELTA = 0.000001;

  @Test
  void shouldConvertOrganicLitersToKilograms() {
    assertEquals(25.0, WasteVolumeToMassConverter.litersToKilograms(50.0, WasteType.ORGANIC), DELTA);
  }

  @Test
  void shouldLimitLitersByRemainingKilogramCapacity() {
    assertEquals(120.0,
        WasteVolumeToMassConverter.litersFromRemainingKilograms(60.0, WasteType.ORGANIC),
        DELTA);
  }

  @Test
  void shouldUseDifferentDensitiesPerWasteType() {
    assertEquals(4.0, WasteVolumeToMassConverter.litersToKilograms(50.0, WasteType.PACKAGING), DELTA);
    assertEquals(27.5, WasteVolumeToMassConverter.litersToKilograms(50.0, WasteType.GLASS), DELTA);
  }
}
