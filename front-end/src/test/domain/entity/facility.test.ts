import { describe, expect, it } from 'vitest';
import { Facility } from '@/domain/entity/facility';
import { FacilityStatus } from '@/domain/enumerate/facility-status';
import { FacilityType } from '@/domain/enumerate/facility-type';
import { DailyWasteDemandLitersPerDay } from '@/domain/valueobject/demand/daily-waste-demand-liters-per-day';
import { laLagunaLocation, sampleFacility, sampleName } from '../fixtures';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

describe('Facility', () => {
  it('should create facility with required attributes', () => {
    const facility = sampleFacility();

    expect(facility.getId()).toBeDefined();
    expect(facility.getName().getValue()).toBe('Facility A');
    expect(facility.getFacilityType()).toBe(FacilityType.TRANSFER_STATION);
    expect(facility.getStatus()).toBe(FacilityStatus.OPEN);
    expect(facility.getCurrentFillingLevel().getLitersPerDay()).toBe(0);
  });

  it('should assign waste demand when facility is open', () => {
    const facility = sampleFacility({ status: FacilityStatus.OPEN });
    facility.assignWasteDemand(new DailyWasteDemandLitersPerDay(120));

    expect(facility.getCurrentFillingLevel().getLitersPerDay()).toBe(120);
  });

  it('should reject assignments when facility is discarded', () => {
    const facility = sampleFacility({ status: FacilityStatus.DISCARDED });

    expect(() => facility.assignWasteDemand(new DailyWasteDemandLitersPerDay(10))).toThrow(
      'Facility is discarded and cannot receive assignments',
    );
  });

  it('should update facility attributes', () => {
    const facility = sampleFacility();

    facility.updateName(sampleName('Updated facility'));
    facility.updateLocation(laLagunaLocation());
    facility.updateFacilityType(FacilityType.TREATMENT_PLANT);
    facility.updateStatus(FacilityStatus.PLANNED);

    expect(facility.getName().getValue()).toBe('Updated facility');
    expect(facility.getLocation()).toEqual(laLagunaLocation());
    expect(facility.getFacilityType()).toBe(FacilityType.TREATMENT_PLANT);
    expect(facility.getStatus()).toBe(FacilityStatus.PLANNED);
  });

  it('should compare equality by id', () => {
    const id = UllUUID.random();
    expect(sampleFacility({ id }).equals(sampleFacility({ id }))).toBe(true);
    expect(sampleFacility().equals(sampleFacility())).toBe(false);
  });

});
