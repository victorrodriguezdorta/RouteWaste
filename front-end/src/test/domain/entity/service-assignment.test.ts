import { describe, expect, it } from 'vitest';
import { ServiceAssignment } from '@/domain/entity/service-assignment';
import { sampleContainer, sampleFacility, sampleInfrastructurePlan, sampleName } from '../fixtures';
import { UllUUID } from '@ull-tfg/ull-tfg-typescript';

describe('ServiceAssignment', () => {
  it('should create assignment with containers', () => {
    const plan = sampleInfrastructurePlan();
    const facility = sampleFacility();
    const containers = [sampleContainer(), sampleContainer({ name: sampleName('Container B') })];
    const assignment = new ServiceAssignment(plan, facility, containers);

    expect(assignment.getId()).toBeDefined();
    expect(assignment.getInfrastructurePlan()).toBe(plan);
    expect(assignment.getFacility()).toBe(facility);
    expect(assignment.getNumberOfContainers()).toBe(2);
    expect(assignment.containsContainer(containers[0]!)).toBe(true);
  });

  it('should compare equality by id', () => {
    const plan = sampleInfrastructurePlan();
    const facility = sampleFacility();
    const containers = [sampleContainer()];
    const id = UllUUID.random();

    expect(new ServiceAssignment(plan, facility, containers, id).equals(new ServiceAssignment(plan, facility, containers, id))).toBe(true);
  });

  it('should reject empty or missing containers', () => {
    const plan = sampleInfrastructurePlan();
    const facility = sampleFacility();

    expect(() => new ServiceAssignment(plan, facility, [])).toThrow('Assigned containers are not defined or empty');
    expect(() => new ServiceAssignment(null as never, facility, [sampleContainer()])).toThrow('Infrastructure plan is not defined');
    expect(() => new ServiceAssignment(plan, null as never, [sampleContainer()])).toThrow('Facility is not defined');
  });
});
