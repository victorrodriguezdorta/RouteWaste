import { describe, expect, it } from 'vitest';
import { RouteSequence } from '@/domain/valueobject/location/route-sequence';

describe('RouteSequence', () => {
  it('should create positive sequences', () => {
    expect(RouteSequence.of(1).getSequence()).toBe(1);
    expect(RouteSequence.of(3).getNext().getSequence()).toBe(4);
    expect(RouteSequence.of(2).getPrevious().getSequence()).toBe(1);
  });

  it('should reject invalid sequences', () => {
    expect(() => RouteSequence.of(0)).toThrow('Route sequence must be a positive integer');
    expect(() => RouteSequence.of(1.5)).toThrow('Route sequence must be a positive integer');
    expect(() => RouteSequence.of(1).getPrevious()).toThrow('Cannot get previous sequence from sequence 1');
  });
});
