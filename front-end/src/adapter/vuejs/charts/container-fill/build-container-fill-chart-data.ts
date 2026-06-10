import type { InfrastructurePlanContainerDailyStateDetail } from '@/domain/read-model/infrastructure-plan-detail';
import type { InfrastructurePlanContainerDetail } from '@/domain/read-model/infrastructure-plan-detail';
import {
  computeContainerFillPercentFromLiters,
  normalizePlanDay,
} from '@/adapter/vuejs/utils/container-fill-level';
import type { RouteProgressChartDatum, RouteProgressChartSeries } from '../route-progress/types';

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

const BEFORE_COLLECTION_OFFSET = -0.25;
const AFTER_COLLECTION_OFFSET = 0.25;

export interface ContainerFillChartInput {
  containers: InfrastructurePlanContainerDetail[];
  monitoringStates: InfrastructurePlanContainerDailyStateDetail[];
  labelForContainer: (container: InfrastructurePlanContainerDetail) => string;
  beforeCollectionLabel?: string;
  afterCollectionLabel?: string;
}

export interface ContainerFillChartResult {
  data: RouteProgressChartDatum[];
  series: RouteProgressChartSeries[];
}

function normalizeIdentifier(value: string | null | undefined): string {
  return String(value ?? '').trim().toLowerCase();
}

function resolveCapacityLiters(
  container: InfrastructurePlanContainerDetail,
  state?: InfrastructurePlanContainerDailyStateDetail,
): number | null {
  const capacityFromState = state?.containerCapacityLiters?.getLiters?.();
  const capacityFromContainer = container.capacityLiters?.getLiters?.();
  const capacity = typeof capacityFromState === 'number' ? capacityFromState : capacityFromContainer;

  if (typeof capacity !== 'number' || !Number.isFinite(capacity) || capacity === 0) {
    return null;
  }

  return capacity;
}

function resolveBeforeCollectionLiters(
  state?: InfrastructurePlanContainerDailyStateDetail,
): number | null {
  const beforeCollection = state?.dailyFillingLitersBeforeCollection;
  if (typeof beforeCollection === 'number' && Number.isFinite(beforeCollection)) {
    return beforeCollection;
  }

  return typeof state?.dailyFillingLiters === 'number' && Number.isFinite(state.dailyFillingLiters)
    ? state.dailyFillingLiters
    : null;
}

function resolveAfterCollectionLiters(
  state?: InfrastructurePlanContainerDailyStateDetail,
): number | null {
  return typeof state?.dailyFillingLiters === 'number' && Number.isFinite(state.dailyFillingLiters)
    ? state.dailyFillingLiters
    : null;
}

export function buildContainerFillChartData(
  input: ContainerFillChartInput,
): ContainerFillChartResult {
  const {
    containers,
    monitoringStates,
    labelForContainer,
    beforeCollectionLabel = '',
    afterCollectionLabel = '',
  } = input;
  if (containers.length === 0) {
    return { data: [], series: [] };
  }

  const containerIds = new Set(
    containers.map((container) => normalizeIdentifier(container.id.getValue())),
  );
  const statesByDayAndContainer = new Map<string, InfrastructurePlanContainerDailyStateDetail>();
  const availableDays = new Set<number>();

  monitoringStates.forEach((state) => {
    const containerId = normalizeIdentifier(state.containerId.getValue());
    if (!containerIds.has(containerId)) {
      return;
    }

    const day = normalizePlanDay(state.planDay);
    if (day < 0) {
      return;
    }

    availableDays.add(day);
    statesByDayAndContainer.set(`${day}::${containerId}`, state);
  });

  const series = containers.map((container, index) => ({
    key: container.id.getValue(),
    label: labelForContainer(container),
    color: CONTAINER_FILL_CHART_COLORS[index % CONTAINER_FILL_CHART_COLORS.length],
  }));

  const data: RouteProgressChartDatum[] = [];

  Array.from(availableDays)
    .sort((left, right) => left - right)
    .forEach((day) => {
      const beforeRow: RouteProgressChartDatum = {
        stop: day + BEFORE_COLLECTION_OFFSET,
        stopLabel: String(day),
      };
      const afterRow: RouteProgressChartDatum = {
        stop: day + AFTER_COLLECTION_OFFSET,
        stopLabel: afterCollectionLabel,
      };

      containers.forEach((container) => {
        const containerId = normalizeIdentifier(container.id.getValue());
        const state = statesByDayAndContainer.get(`${day}::${containerId}`);
        const capacity = resolveCapacityLiters(container, state);
        const containerKey = container.id.getValue();

        const beforePercent = computeContainerFillPercentFromLiters(
          resolveBeforeCollectionLiters(state),
          capacity,
        );
        const afterPercent = computeContainerFillPercentFromLiters(
          resolveAfterCollectionLiters(state),
          capacity,
        );

        beforeRow[containerKey] = beforePercent ?? 0;
        afterRow[containerKey] = afterPercent ?? 0;
      });

      data.push(beforeRow, afterRow);
    });

  if (beforeCollectionLabel) {
    data.forEach((row, index) => {
      if (!row.stopLabel && index % 2 === 1) {
        row.stopLabel = afterCollectionLabel;
      }
    });
  }

  return { data, series };
}
