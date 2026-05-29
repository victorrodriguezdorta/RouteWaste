import { describe, expect, it } from 'vitest';
import { ServiceAssignment } from '@/domain/entity/service-assignment';
import { MaximumBudget } from '@/domain/valueobject/cost/maximum-budget';
import { OpeningFixedCost } from '@/domain/valueobject/cost/opening-fixed-cost';
import { sampleContainer, sampleFacility, sampleInfrastructurePlan } from '../fixtures';

describe('InfrastructurePlan', () => {
  it('should create plan with defaults', () => {
    const plan = sampleInfrastructurePlan();

    expect(plan.getId()).toBeDefined();
    expect(plan.getPeriod().getValue()).toBe('2026');
    expect(plan.getMaxBudget().getAmount()).toBe(100_000);
    expect(plan.getEstimatedTotalCost().getAmount()).toBe(0);
    expect(plan.getSelectedFacilities()).toHaveLength(0);
    expect(plan.getServiceAssignments()).toHaveLength(0);
  });

  it('should add facility and service assignment', () => {
    const plan = sampleInfrastructurePlan({ maxBudget: new MaximumBudget(1_000_000) });
    const facility = sampleFacility();
    const container = sampleContainer();
    const assignment = new ServiceAssignment(plan, facility, [container]);

    plan.addFacility(facility);
    plan.addServiceAssignment(assignment);

    expect(plan.getSelectedFacilities()).toHaveLength(1);
    expect(plan.getServiceAssignments()).toHaveLength(1);
    expect(facility.getCurrentFillingLevel().getLitersPerDay()).toBe(180);
  });

  it('should recalculate total cost from facility opening costs', () => {
    const plan = sampleInfrastructurePlan({ maxBudget: new MaximumBudget(1_000_000) });
    const facility = sampleFacility();
    facility.updateOpeningFixedCost(new OpeningFixedCost(300));
    plan.addFacility(facility);
    plan.recalculateTotalCost();

    expect(plan.getEstimatedTotalCost().getAmount()).toBe(300);
  });

  it('should reject cost above maximum budget', () => {
    const plan = sampleInfrastructurePlan({ maxBudget: new MaximumBudget(100) });
    plan.addFacility(sampleFacility());

    expect(() => plan.recalculateTotalCost()).toThrow('Total cost exceeds maximum budget');
  });
});
