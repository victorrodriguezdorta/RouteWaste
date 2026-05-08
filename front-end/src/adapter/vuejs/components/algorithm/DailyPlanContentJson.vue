<template>
  <v-card variant="outlined" class="mt-4">
    <v-card-title class="d-flex align-center ga-2">
      <v-icon icon="mdi-code-json" color="primary" />
      <span>{{ t('infrastructurePlan.show.daily.content.title') }}</span>
    </v-card-title>

    <v-divider />

    <v-card-text>
      <pre class="json-block">{{ jsonContent }}</pre>
    </v-card-text>
  </v-card>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

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
}

const props = defineProps<{
  planDay: number;
  serviceDate?: string;
  dailyPlans: DailyPlanLike[];
  facilities: FacilityLike[];
}>();

const { t } = useI18n();

const jsonContent = computed(() => {
  const dayInfo = {
    planDay: props.planDay,
    serviceDate: props.serviceDate ?? null,
    totals: {
      dailyPlans: props.dailyPlans.length,
      facilities: props.facilities.length,
      stops: props.dailyPlans.reduce((acc, plan) => acc + (Array.isArray(plan.stops) ? plan.stops.length : 0), 0),
    },
    facilities: props.facilities,
    dailyPlans: props.dailyPlans,
  };

  return JSON.stringify(dayInfo, null, 2);
});
</script>

<style scoped>
.json-block {
  background: rgb(var(--v-theme-surface-variant));
  border-radius: 8px;
  font-family: Consolas, 'Courier New', monospace;
  font-size: 0.85rem;
  line-height: 1.45;
  margin: 0;
  max-height: 420px;
  overflow: auto;
  padding: 12px;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
