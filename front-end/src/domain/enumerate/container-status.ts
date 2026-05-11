/**
 * ContainerStatus
 *
 * Represents the monitoring status of a container for a given plan day.
 * Values mirror backend Java enum used by the API.
 */
export enum ContainerStatus {
  /** Container state is correct (no overflow). */
  CORRECT = 'CORRECT',
  /** Container has overflowed. */
  OVERFLOWED = 'OVERFLOWED',
}

export function containerStatusValues(): ContainerStatus[] {
  return Object.values(ContainerStatus) as ContainerStatus[];
}

export function containerStatusFromString(s?: string): ContainerStatus {
  if (!s) throw new Error('Container status is not defined');
  const key = s.trim().toUpperCase();
  for (const v of containerStatusValues()) {
    if (v === key) return v;
  }
  throw new Error('Container status is invalid');
}

export function isContainerStatus(s?: string): boolean {
  if (!s) return false;
  const key = s.trim().toUpperCase();
  for (const v of containerStatusValues()) {
    if (v === key) return true;
  }
  return false;
}
