import { describe, expect, it } from 'vitest';
import { Location } from '@/domain/valueobject/location/location';
import { laLagunaLocation, santaCruzLocation } from '../../fixtures';

describe('Location', () => {
  it('should create valid locations', () => {
    const location = santaCruzLocation();
    expect(location.latitude).toBeCloseTo(28.4636, 4);
    expect(location.longitude).toBeCloseTo(-16.2518, 4);
    expect(location.postalAddress).toBe('Santa Cruz de Tenerife');
    expect(location.gisReference).toBe('SC-001');
  });

  it('should compare equality', () => {
    expect(santaCruzLocation().equals(santaCruzLocation())).toBe(true);
    expect(santaCruzLocation().equals(laLagunaLocation())).toBe(false);
  });

  it('should reject invalid postal address or gis reference', () => {
    expect(() => new Location(28.4636, -16.2518, '', 'SC-001')).toThrow('Postal address cannot be empty');
    expect(() => new Location(28.4636, -16.2518, 'Bad @ address', 'SC-001')).toThrow('Postal address format is invalid');
    expect(() => new Location(28.4636, -16.2518, 'Valid street 1', '')).toThrow('GIS reference cannot be empty');
  });
});
