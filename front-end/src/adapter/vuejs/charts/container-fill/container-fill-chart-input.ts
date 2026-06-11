import type {
  InfrastructurePlanContainerDailyStateDetail,
  InfrastructurePlanContainerDetail,
} from '@/domain/read-model/infrastructure-plan-detail';

export interface ContainerFillChartInput {
  containers: InfrastructurePlanContainerDetail[];
  monitoringStates: InfrastructurePlanContainerDailyStateDetail[];
  labelForContainer: (container: InfrastructurePlanContainerDetail) => string;
  beforeCollectionLabel?: string;
  afterCollectionLabel?: string;
}
