import type { InfrastructurePlanContainerDailyStateDetail } from '@/domain/read-model/infrastructure-plan-detail';
import type { InfrastructurePlanContainerDetail } from '@/domain/read-model/infrastructure-plan-detail';
import { computeContainerFillPercent, normalizePlanDay } from '@/adapter/vuejs/utils/container-fill-level';
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

export interface ContainerFillChartInput {
  containers: InfrastructurePlanContainerDetail[];
  monitoringStates: InfrastructurePlanContainerDailyStateDetail[];
  labelForContainer: (container: InfrastructurePlanContainerDetail) => string;
}

export interface ContainerFillChartResult {
  data: RouteProgressChartDatum[];
  series: RouteProgressChartSeries[];
}

function normalizeIdentifier(value: string | null | undefined): string {
  return String(value ?? '').trim().toLowerCase();
}

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

  const data = Array.from(availableDays)
    .sort((left, right) => left - right)
    .map((day) => {
      const row: RouteProgressChartDatum = { stop: day };

      containers.forEach((container) => {
        const containerId = normalizeIdentifier(container.id.getValue());
        const state = statesByDayAndContainer.get(`${day}::${containerId}`);
        const percent = computeContainerFillPercent({ container, state });
        row[container.id.getValue()] = percent ?? 0;
      });

      return row;
    });

  return { data, series };
}
