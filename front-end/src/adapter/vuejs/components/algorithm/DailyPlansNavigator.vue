<template>
  <div class="daily-plans-navigator mt-4">
    <div class="navigator-content">
      <DailyPlanContentJson
        :plan-day="currentDay"
        :service-date="selectedServiceDate"
        :daily-plans="dailyPlansForCurrentDay"
        :facilities="facilitiesForCurrentDay"
        :container-state-monitoring="plan?.containerStateMonitoring ?? []"
        :current-day="currentDay"
        :total-days="totalDays"
        :can-go-previous="canGoPrevious"
        :can-go-next="canGoNext"
        @go-previous="goPrevious"
        @go-next="goNext"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import type {
  InfrastructurePlanDailyPlanDetail,
  InfrastructurePlanDetail,
  InfrastructurePlanFacilityDetail,
} from '@/domain/read-model/infrastructure-plan-detail';
import { computed, ref, watch } from 'vue';
import DailyPlanContentJson from './DailyPlanContentJson.vue';

const props = defineProps<{
  plan?: InfrastructurePlanDetail;
}>();

const currentDay = ref(1);

const totalDays = computed(() => {
  const declaredDays = typeof props.plan?.numberOfDays === 'number' ? props.plan.numberOfDays : 0;
  if (declaredDays > 0) return declaredDays;

  const allPlanDays = (props.plan?.getDailyPlans() ?? [])
    .map((dailyPlan) => dailyPlan.planDay)
    .filter((value): value is number => typeof value === 'number' && value > 0);

  return allPlanDays.length > 0 ? Math.max(...allPlanDays) : 1;
});

const allDailyPlans = computed<InfrastructurePlanDailyPlanDetail[]>(() => props.plan?.getDailyPlans() ?? []);

const dailyPlansForCurrentDay = computed(() =>
  allDailyPlans.value.filter((dailyPlan) => dailyPlan.planDay === currentDay.value),
);

const selectedServiceDate = computed(() => {
  const fromCurrentDay = dailyPlansForCurrentDay.value[0]?.serviceDate;
  return getServiceDate(fromCurrentDay);
});

const planFacilities = computed<InfrastructurePlanFacilityDetail[]>(() => props.plan?.facilities ?? []);

const facilitiesForCurrentDay = computed(() => {
  const ids = new Set(
    dailyPlansForCurrentDay.value
      .map((dailyPlan) => dailyPlan.facilityId.getValue())
      .filter((value): value is string => typeof value === 'string' && value.length > 0),
  );

  return planFacilities.value
    .filter((facility) => ids.has(facility.id.getValue()));
});

const canGoPrevious = computed(() => currentDay.value > 1);
const canGoNext = computed(() => currentDay.value < totalDays.value);

watch(
  totalDays,
  (value) => {
    if (currentDay.value > value) {
      currentDay.value = value;
    }
    if (currentDay.value < 1) {
      currentDay.value = 1;
    }
  },
  { immediate: true },
);

function goPrevious(): void {
  if (canGoPrevious.value) {
    currentDay.value -= 1;
  }
}

function goNext(): void {
  if (canGoNext.value) {
    currentDay.value += 1;
  }
}

function getServiceDate(value: unknown): string | undefined {
  if (typeof value === 'string') return value;
  if (!value || typeof value !== 'object') return undefined;

  const date = (value as Record<string, unknown>).date;
  if (typeof date === 'string') return date;

  const rawValue = (value as Record<string, unknown>).value;
  if (typeof rawValue === 'string') return rawValue;

  return undefined;
}
</script>

<style scoped>
.daily-plans-navigator {
  display: flex;
  flex-direction: column;
}

.navigator-content {
  width: 100%;
}
</style>
