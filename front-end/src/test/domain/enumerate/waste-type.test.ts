import { describe, expect, it } from 'vitest';
import { WasteType, isWasteType, wasteTypeColor, wasteTypeFromString, wasteTypeValues } from '@/domain/enumerate/waste-type';
import { describeStandardEnum } from './enum-test-helpers';

describeStandardEnum({
  label: 'WasteType parsing',
  values: wasteTypeValues,
  fromString: wasteTypeFromString,
  isValid: isWasteType,
  sample: WasteType.ORGANIC,
  undefinedMessage: 'Waste type is not defined',
});

describe('WasteType helpers', () => {
  it('should map colors for known types', () => {
    expect(wasteTypeColor(WasteType.GLASS)).toBe('green');
    expect(wasteTypeColor('organic')).toBe('brown');
  });

  it('should expose allowed values string', () => {
    expect(wasteTypeValues().join(', ')).toContain('ORGANIC');
  });
});
