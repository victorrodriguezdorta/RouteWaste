import type {
  InfrastructurePlanContainerDailyStateDetail,
  InfrastructurePlanContainerDetail,
} from '@/domain/read-model/infrastructure-plan-detail';
import { professionalLightColors } from '@/theme/professional-light-colors';

export type ContainerFillMarkerTone = 'unknown' | 'normal' | 'medium' | 'overflow';

export function normalizePlanDay(value: number | string | null | undefined): number {
  if (typeof value === 'number' && Number.isFinite(value)) {
    return value;
  }

  if (typeof value === 'string') {
    const parsedValue = Number(value);
    if (Number.isFinite(parsedValue)) {
      return parsedValue;
    }
  }

  return -1;
}

export function computeContainerFillPercent(entry: {
  container: InfrastructurePlanContainerDetail;
  state?: InfrastructurePlanContainerDailyStateDetail;
}): number | null {
  const filling = entry.state?.dailyFillingLiters;
  const capacityFromState = entry.state?.containerCapacityLiters?.getLiters?.();
  const capacityFromContainer = entry.container.capacityLiters?.getLiters?.();
  const capacity = typeof capacityFromState === 'number' ? capacityFromState : capacityFromContainer;

  if (typeof filling !== 'number' || !Number.isFinite(filling)) {
    return null;
  }
  if (typeof capacity !== 'number' || !Number.isFinite(capacity) || capacity === 0) {
    return null;
  }

  const percent = (filling / capacity) * 100;
  if (!Number.isFinite(percent)) {
    return null;
  }

  return Math.round(percent);
}

export function resolveContainerFillMarkerTone(fillPercent: number | null): ContainerFillMarkerTone {
  if (fillPercent === null) {
    return 'unknown';
  }
  if (fillPercent >= 100) {
    return 'overflow';
  }
  if (fillPercent >= 50) {
    return 'medium';
  }
  return 'normal';
}

export function containerFillToneForEntry(entry: {
  container: InfrastructurePlanContainerDetail;
  state?: InfrastructurePlanContainerDailyStateDetail;
}): ContainerFillMarkerTone {
  return resolveContainerFillMarkerTone(computeContainerFillPercent(entry));
}

export type ContainerFillThemePalette = Record<ContainerFillMarkerTone, string>;

/** Misma fuente que `main.ts` → `professionalLightColors`. */
export function buildContainerFillThemePalette(): ContainerFillThemePalette {
  return {
    unknown: professionalLightColors['map-pin-muted'],
    normal: professionalLightColors['container-marker-fill-normal'],
    medium: professionalLightColors['container-marker-fill-medium'],
    overflow: professionalLightColors['container-marker-fill-overflow'],
  };
}

export function containerFillColorForTone(
  palette: ContainerFillThemePalette,
  tone: ContainerFillMarkerTone,
): string {
  return palette[tone];
}

export function buildContainerFillCssVars(): Record<string, string> {
  const palette = buildContainerFillThemePalette();

  return {
    '--cbf-unknown': palette.unknown,
    '--cbf-normal': palette.normal,
    '--cbf-medium': palette.medium,
    '--cbf-overflow': palette.overflow,
    '--cbf-normal-border': hexToRgba(palette.normal, 0.45),
    '--cbf-medium-bg': hexToRgba(palette.medium, 0.1),
    '--cbf-medium-border': hexToRgba(palette.medium, 0.55),
    '--cbf-overflow-bg': hexToRgba(palette.overflow, 0.1),
    '--cbf-overflow-border': hexToRgba(palette.overflow, 0.55),
  };
}

export function hexToRgba(hex: string, alpha: number): string {
  const normalized = hex.replace('#', '').trim();
  const expanded = normalized.length === 3
    ? normalized.split('').map((character) => character + character).join('')
    : normalized;

  if (expanded.length !== 6) {
    return `rgba(0, 0, 0, ${alpha})`;
  }

  const value = Number.parseInt(expanded, 16);
  if (!Number.isFinite(value)) {
    return `rgba(0, 0, 0, ${alpha})`;
  }

  const red = (value >> 16) & 255;
  const green = (value >> 8) & 255;
  const blue = value & 255;
  return `rgba(${red}, ${green}, ${blue}, ${alpha})`;
}
