<template>
  <v-card variant="flat" class="mt-4">
    <v-card-title class="d-flex align-center justify-space-between ga-3 flex-wrap">
      <div class="d-flex align-center ga-2">
        <v-icon icon="mdi-calendar-range" color="primary" />
        <span>{{ t('infrastructurePlan.show.daily.navigator.title') }}</span>
      </div>

      <div class="d-flex align-center ga-2">
        <v-btn
          icon="mdi-chevron-left"
          variant="text"
          :disabled="!canGoPrevious"
          @click="goPrevious"
        />

        <div class="day-indicator">
          {{ t('infrastructurePlan.show.daily.navigator.dayLabel', { current: currentDay, total: totalDays }) }}
        </div>

        <v-btn
          icon="mdi-chevron-right"
          variant="text"
          :disabled="!canGoNext"
          @click="goNext"
        />
      </div>
    </v-card-title>

    <v-divider />

    <v-card-text>
      <div class="text-body-2 text-medium-emphasis mb-2">
        {{ t('infrastructurePlan.show.daily.navigator.serviceDate') }}: {{ selectedServiceDate ?? '-' }}
      </div>

      <DailyPlanContentJson
        :plan-day="currentDay"
        :service-date="selectedServiceDate"
        :daily-plans="dailyPlansForCurrentDay"
        :facilities="facilitiesForCurrentDay"
      />
    </v-card-text>
  </v-card>
</template>

<script lang="ts" setup>
import { computed, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import DailyPlanContentJson from './DailyPlanContentJson.vue';

interface DailyPlanLike {
  id?: string;
  infrastructurePlanId?: string;
  facilityId?: string;
  serviceDate?: unknown;
  planDay?: number;
  vehicleId?: string;
  totalCollectedKilograms?: unknown;
  totalCollectedLiters?: unknown;
  totalDistanceMeters?: unknown;
  stops?: unknown[];
}

interface FacilityLike {
  id?: string;
  facilityType?: string;
  status?: string;
  location?: unknown;
  capacities?: unknown;
  assignedContainers?: unknown[];
  dailyPlans?: DailyPlanLike[];
}

interface InfrastructurePlanLike {
  numberOfDays?: number;
  facilities?: FacilityLike[];
}

const props = defineProps<{
  plan?: unknown;
}>();

const { t } = useI18n();

const currentDay = ref(1);

const totalDays = computed(() => {
  const plan = (props.plan ?? {}) as InfrastructurePlanLike;
  const declaredDays = typeof plan.numberOfDays === 'number' ? plan.numberOfDays : 0;
  if (declaredDays > 0) return declaredDays;

  const allPlanDays = (plan.facilities ?? [])
    .flatMap((facility) => facility.dailyPlans ?? [])
    .map((dailyPlan) => (typeof dailyPlan.planDay === 'number' ? dailyPlan.planDay : undefined))
    .filter((value): value is number => typeof value === 'number' && value > 0);

  return allPlanDays.length > 0 ? Math.max(...allPlanDays) : 1;
});

const allDailyPlans = computed(() =>
  (((props.plan ?? {}) as InfrastructurePlanLike).facilities ?? []).flatMap((facility) =>
    (facility.dailyPlans ?? []).map((dailyPlan) => ({
      ...dailyPlan,
      facilityId: dailyPlan.facilityId ?? facility.id,
    })),
  ),
);

const dailyPlansForCurrentDay = computed(() =>
  allDailyPlans.value.filter((dailyPlan) => dailyPlan.planDay === currentDay.value),
);

const selectedServiceDate = computed(() => {
  const fromCurrentDay = dailyPlansForCurrentDay.value[0]?.serviceDate;
  return getServiceDate(fromCurrentDay);
});

const planFacilities = computed(() => (((props.plan ?? {}) as InfrastructurePlanLike).facilities ?? []));

const facilitiesForCurrentDay = computed(() => {
  const ids = new Set(
    dailyPlansForCurrentDay.value
      .map((dailyPlan) => dailyPlan.facilityId)
      .filter((value): value is string => typeof value === 'string' && value.length > 0),
  );

  return planFacilities.value
    .filter((facility) => typeof facility.id === 'string' && ids.has(facility.id))
    .map(({ dailyPlans, ...facility }) => facility);
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
.day-indicator {
  font-weight: 600;
  min-width: 120px;
  text-align: center;
}
</style>
