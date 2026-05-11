/**
 * StopType
 *
 * Represents whether a stop references a container or is a facility return.
 */
export enum StopType {
  CONTAINER = 'CONTAINER',
  FACILITY = 'FACILITY',
}

export function stopTypeValues(): StopType[] {
  return Object.values(StopType) as StopType[];
}

export function stopTypeFromString(s?: string): StopType {
  if (!s) throw new Error('Stop type is not defined');
  const key = s.trim().toUpperCase();
  for (const v of stopTypeValues()) {
    if (v === key) return v;
  }
  throw new Error('Stop type is invalid');
}

export function isStopType(s?: string): boolean {
  if (!s) return false;
  const key = s.trim().toUpperCase();
  for (const v of stopTypeValues()) {
    if (v === key) return true;
  }
  return false;
}
