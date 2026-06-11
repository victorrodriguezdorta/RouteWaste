import type { ContainerFillChartInput } from '@/adapter/vuejs/charts/container-fill/container-fill-chart-input';
import type { ContainerFillChartResult } from '@/adapter/vuejs/charts/container-fill/container-fill-chart-result';
import type { RouteProgressChartDatum } from '@/adapter/vuejs/charts/route-progress/route-progress-chart-datum';
import {
  computeContainerFillPercentFromLiters,
  normalizePlanDay,
} from '@/adapter/vuejs/utils/container-fill-level';

const CONTAINER_FILL_CHART_COLORS = [
  '#4e79a7',
  '#f28e2b',
  '#59a14f',
  '#e15759',
  '#76b7b2',
  '#edc948',
  '#b07aa1',
  '#ff9da7',
  '#9c755f',
  '#bab0ac',
  '#86bcb6',
  '#d37295',
  '#8cd17d',
  '#b6992d',
  '#499894',
  '#e17c05',
  '#aecde1',
  '#fabfd2',
  '#ffbe7d',
  '#d4a6c8',
] as const;

const MINUTES_PER_DAY = 24 * 60;
const DEFAULT_TIME_MINUTES = 0;

type MonitoringState = ContainerFillChartInput['monitoringStates'][number];

interface TimelineColumn {
  /** Unique sortable value combining day and time of day. */
  key: number;
  day: number;
  minutes: number;
}

function normalizeIdentifier(value: string | null | undefined): string {
  return String(value ?? '').trim().toLowerCase();
}

/**
 * Parses a "HH:mm" or "HH:mm:ss" string into minutes from midnight.
 *
 * @param time time-of-day string, when available
 * @returns minutes from midnight, or a default when the value is missing/invalid
 */
function parseMinutes(time: string | null | undefined): number {
  if (typeof time !== 'string' || time.trim().length === 0) {
    return DEFAULT_TIME_MINUTES;
  }
  const [hoursRaw, minutesRaw] = time.split(':');
  const hours = Number(hoursRaw);
  const minutes = Number(minutesRaw);
  if (!Number.isFinite(hours) || !Number.isFinite(minutes)) {
    return DEFAULT_TIME_MINUTES;
  }
  return Math.max(0, Math.min(MINUTES_PER_DAY - 1, hours * 60 + minutes));
}

/**
 * Formats minutes from midnight into a "HH:mm" label.
 *
 * @param minutes minutes from midnight
 * @returns formatted time label
 */
function formatTimeLabel(minutes: number): string {
  const hours = Math.floor(minutes / 60);
  const remainder = minutes % 60;
  return `${String(hours).padStart(2, '0')}:${String(remainder).padStart(2, '0')}`;
}

function buildColumnKey(day: number, minutes: number): number {
  return day * MINUTES_PER_DAY + minutes;
}

function resolveCapacityLiters(
  container: ContainerFillChartInput['containers'][number],
  state?: MonitoringState,
): number | null {
  const capacityFromState = state?.containerCapacityLiters?.getLiters?.();
  const capacityFromContainer = container.capacityLiters?.getLiters?.();
  const capacity = typeof capacityFromState === 'number' ? capacityFromState : capacityFromContainer;

  if (typeof capacity !== 'number' || !Number.isFinite(capacity) || capacity === 0) {
    return null;
  }

  return capacity;
}

/**
 * Builds the per-moment container fill timeline.
 *
 * <p>The X axis spans every planning day and, within each day, the exact times at which a
 * container snapshot was recorded (workday start and each collection). Each container is a series
 * and its fill percentage is carried forward between snapshots so the line stays continuous.</p>
 *
 * @param input chart input with containers and their monitoring snapshots
 * @returns chart data rows and series
 */
export function buildContainerFillChartData(
  input: ContainerFillChartInput,
): ContainerFillChartResult {
  const { containers, monitoringStates, labelForContainer } = input;
  if (containers.length === 0) {
    return { data: [], series: [] };
  }

  const containerIds = new Set(
    containers.map((container) => normalizeIdentifier(container.id.getValue())),
  );

  const columnByKey = new Map<number, TimelineColumn>();
  const statesByContainerAndColumn = new Map<string, MonitoringState>();

  monitoringStates.forEach((state) => {
    const containerId = normalizeIdentifier(state.containerId.getValue());
    if (!containerIds.has(containerId)) {
      return;
    }
    const day = normalizePlanDay(state.planDay);
    if (day < 0) {
      return;
    }
    const minutes = parseMinutes(state.time);
    const columnKey = buildColumnKey(day, minutes);
    if (!columnByKey.has(columnKey)) {
      columnByKey.set(columnKey, { key: columnKey, day, minutes });
    }
    // Keep the latest snapshot when several share the same container/day/time.
    statesByContainerAndColumn.set(`${columnKey}::${containerId}`, state);
  });

  const columns = Array.from(columnByKey.values()).sort((left, right) => left.key - right.key);
  if (columns.length === 0) {
    return { data: [], series: [] };
  }

  const series = containers.map((container, index) => ({
    key: container.id.getValue(),
    label: labelForContainer(container),
    color: CONTAINER_FILL_CHART_COLORS[index % CONTAINER_FILL_CHART_COLORS.length],
  }));

  const data: RouteProgressChartDatum[] = columns.map((column) => ({
    stop: column.key,
    stopLabel: formatTimeLabel(column.minutes),
  }));

  let previousDay: number | null = null;
  columns.forEach((column, columnIndex) => {
    if (column.day !== previousDay) {
      data[columnIndex].stopLabel = `D${column.day} ${formatTimeLabel(column.minutes)}`;
      previousDay = column.day;
    }
  });

  containers.forEach((container) => {
    const containerId = normalizeIdentifier(container.id.getValue());
    const containerKey = container.id.getValue();
    let carriedPercent: number | null = null;

    columns.forEach((column, columnIndex) => {
      const state = statesByContainerAndColumn.get(`${column.key}::${containerId}`);
      if (state) {
        const capacity = resolveCapacityLiters(container, state);
        const percent = computeContainerFillPercentFromLiters(state.dailyFillingLiters, capacity);
        if (percent !== null) {
          carriedPercent = percent;
        }
      }
      if (carriedPercent !== null) {
        data[columnIndex][containerKey] = carriedPercent;
      }
    });
  });

  return { data, series };
}
