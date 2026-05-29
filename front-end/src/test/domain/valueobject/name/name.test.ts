import { describe, expect, it } from 'vitest';
import { Name } from '@/domain/valueobject/name/name';

describe('Name', () => {
  it('should trim and store value', () => {
    const name = new Name('  Main depot  ');
    expect(name.getValue()).toBe('Main depot');
  });

  it('should compare equality by value', () => {
    expect(new Name('A').equals(new Name('A'))).toBe(true);
    expect(new Name('A').equals(new Name('B'))).toBe(false);
  });

  it('should reject blank values', () => {
    expect(() => new Name('')).toThrow('Name must not be null or blank');
    expect(() => new Name('   ')).toThrow('Name must not be null or blank');
  });
});
