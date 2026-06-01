<template>
  <CrudLayout
    :title="t('selection.welcome')"
    icon="mdi-apps"
    :content-md="12"
    :content-lg="12"
  >
    <v-row class="ma-0" align="stretch">
      <v-col cols="12" lg="8" class="pa-0 pe-lg-3">
    <div class="home-cards-stack px-4 pb-4 pb-lg-8">
    <!-- Algorithm — full width -->
    <v-row justify="center" class="ma-0">
      <v-col cols="12">
        <v-card
          elevation="3"
          hover
          class="algorithm-card d-flex rounded-xl"
          @click="navigateTo('Algorithm')"
          style="height: 280px; overflow: hidden"
        >
          <!-- Left Side - Image -->
          <div class="algorithm-image-container">
            <img :src="routeImage" alt="Algorithm" class="algorithm-image-full" />
          </div>
          
          <!-- Right Side - Content -->
          <div class="algorithm-content d-flex flex-column justify-space-between pa-8">
            <div class="d-flex flex-column">
              <div>
                <h3
                  class="algorithm-card-title text-h4 font-weight-bold text-primary mb-3 d-flex align-center flex-wrap ga-3"
                >
                  <span class="entity-count-badge" aria-hidden="true">{{
                    entityCounts.infrastructurePlans
                  }}</span>
                  <span>{{ t('selection.types.algorithm.title') }}</span>
                </h3>
              </div>
              <div>
                <p class="text-body-2 text-theme-muted ma-0">
                  {{ t('selection.types.algorithm.description') }}
                </p>
              </div>
            </div>
            <div>
              <ButtonTooltip
                color="primary"
                variant="elevated"
                size="large"
                class="px-8 rounded-pill font-weight-bold"
                icon="mdi-chevron-right"
                :text="t('selection.types.algorithm.buttonText')"
                :tooltip="t('selection.types.algorithm.title')"
                :eventclick="() => navigateTo('Algorithm')"
              />
            </div>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <v-row justify="center" class="home-entity-row ma-0">
      <!-- Vehicles Card -->
      <v-col cols="12" md="4">
        <v-card
          elevation="3"
          hover
          class="selection-card h-100 d-flex flex-column pa-4 text-center rounded-xl"
          @click="navigateTo('Vehicles')"
        >
          <div class="card-icon-wrapper mb-4 mt-4">
            <img :src="vehicleImage" alt="Vehicle" class="card-image" />
          </div>
          <v-card-title
            class="entity-card-title text-h4 font-weight-bold mb-4 text-primary d-flex align-center justify-center ga-3 flex-wrap"
          >
            <span class="entity-count-badge" aria-hidden="true">{{ entityCounts.vehicles }}</span>
            <span>{{ t('selection.types.vehicles.title') }}</span>
          </v-card-title>
          <v-card-text class="text-body-1 text-theme-muted flex-grow-1">
            {{ t('selection.types.vehicles.description') }}
          </v-card-text>
          <v-card-actions class="justify-center mt-4">
            <ButtonTooltip
              color="primary"
              variant="elevated"
              size="large"
              class="px-8 rounded-pill font-weight-bold"
              icon="mdi-chevron-right"
              :text="t('common.buttons.edit')"
              :tooltip="t('selection.types.vehicles.title')"
              :eventclick="() => navigateTo('Vehicles')"
            />
          </v-card-actions>
        </v-card>
      </v-col>

      <!-- Containers Card -->
      <v-col cols="12" md="4">
        <v-card
          elevation="3"
          hover
          class="selection-card h-100 d-flex flex-column pa-4 text-center rounded-xl"
          @click="navigateTo('Containers')"
        >
          <div class="card-icon-wrapper mb-4 mt-4">
            <img :src="containerImage" alt="Container" class="card-image" />
          </div>
          <v-card-title
            class="entity-card-title text-h4 font-weight-bold mb-4 text-primary d-flex align-center justify-center ga-3 flex-wrap"
          >
            <span class="entity-count-badge" aria-hidden="true">{{ entityCounts.containers }}</span>
            <span>{{ t('selection.types.containers.title') }}</span>
          </v-card-title>
          <v-card-text class="text-body-1 text-theme-muted flex-grow-1">
            {{ t('selection.types.containers.description') }}
          </v-card-text>
          <v-card-actions class="justify-center mt-4">
            <ButtonTooltip
              color="primary"
              variant="elevated"
              size="large"
              class="px-8 rounded-pill font-weight-bold"
              icon="mdi-chevron-right"
              :text="t('common.buttons.edit')"
              :tooltip="t('selection.types.containers.title')"
              :eventclick="() => navigateTo('Containers')"
            />
          </v-card-actions>
        </v-card>
      </v-col>

      <!-- Facilities Card -->
      <v-col cols="12" md="4">
        <v-card
          elevation="3"
          hover
          class="selection-card h-100 d-flex flex-column pa-4 text-center rounded-xl"
          @click="navigateTo('Facilities')"
        >
          <div class="card-icon-wrapper mb-4 mt-4">
            <img :src="facilityImage" alt="Facility" class="card-image" />
          </div>
          <v-card-title
            class="entity-card-title text-h4 font-weight-bold mb-4 text-primary d-flex align-center justify-center ga-3 flex-wrap"
          >
            <span class="entity-count-badge" aria-hidden="true">{{ entityCounts.facilities }}</span>
            <span>{{ t('selection.types.facilities.title') }}</span>
          </v-card-title>
          <v-card-text class="text-body-1 text-theme-muted flex-grow-1">
            {{ t('selection.types.facilities.description') }}
          </v-card-text>
          <v-card-actions class="justify-center mt-4">
            <ButtonTooltip
              color="primary"
              variant="elevated"
              size="large"
              class="px-8 rounded-pill font-weight-bold"
              icon="mdi-chevron-right"
              :text="t('common.buttons.edit')"
              :tooltip="t('selection.types.facilities.title')"
              :eventclick="() => navigateTo('Facilities')"
            />
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
    </div>
      </v-col>

      <v-col cols="12" lg="4" class="pa-0">
        <div class="home-recent-panel px-4 pb-8 pt-2 pt-lg-0">
          <v-card elevation="3" class="recent-plans-card rounded-xl d-flex flex-column">
            <v-card-title class="text-h6 font-weight-bold text-primary pt-4 px-4 pb-1">
              {{ t('selection.dashboard.recentTitle') }}
            </v-card-title>
            <p class="text-body-2 text-theme-muted px-4 pb-3 ma-0">
              {{ t('selection.dashboard.recentSubtitle') }}
            </p>
            <v-divider class="border-opacity-25" />

            <template v-if="recentPlans.length > 0">
              <v-list lines="one" class="recent-plans-list py-0 flex-grow-1 bg-transparent">
                <v-list-item
                  v-for="plan in recentPlans"
                  :key="plan.id"
                  class="recent-plans-list-item px-4"
                >
                  <v-list-item-title>
                    <div class="font-weight-medium text-body-1">
                      {{ formatExecutedAt(plan) }}
                    </div>
                    <div class="text-caption text-theme-muted mt-1">
                      <span v-if="plan.estimatedTotalCost">
                        {{ plan.estimatedTotalCost.amount }}
                        {{ plan.estimatedTotalCost.currency ?? '' }}
                      </span>
                      <span v-if="plan.numberOfDays != null" class="ms-1">
                        · {{ plan.numberOfDays }} {{ t('selection.dashboard.daysAbbr') }}
                      </span>
                    </div>
                  </v-list-item-title>
                  <template #append>
                    <ButtonTooltip
                      text=""
                      icon="mdi-eye"
                      :tooltip="t('selection.dashboard.viewPlan')"
                      color="info"
                      size="small"
                      variant="text"
                      :eventclick="() => showInfrastructurePlan(plan.id)"
                    />
                  </template>
                  <template #prepend>
                    <v-chip
                      :color="infrastructurePlanValidityStateColor(planValidity(plan))"
                      size="small"
                      variant="flat"
                      class="font-weight-medium"
                    >
                      {{ validityLabel(plan) }}
                    </v-chip>
                  </template>
                </v-list-item>
              </v-list>
            </template>
            <v-card-text v-else class="text-body-2 text-theme-muted flex-grow-1">
              {{ t('selection.dashboard.emptyRecent') }}
            </v-card-text>

            <v-card-actions class="px-4 pb-4 pt-0">
              <ButtonTooltip
                color="primary"
                variant="tonal"
                size="small"
                icon="mdi-format-list-bulleted"
                :text="t('selection.dashboard.viewAllPlans')"
                :tooltip="t('selection.dashboard.viewAllPlans')"
                :eventclick="() => navigateTo('Algorithm')"
              />
            </v-card-actions>
          </v-card>
        </div>
      </v-col>
    </v-row>
  </CrudLayout>
</template>

<script setup lang="ts">
import type { InfrastructurePlanSummaryJsonResponse } from '@/adapter/http/dto/infrastructure-plan/infrastructure-plan-summary-json-response';
import {
  infrastructurePlanValidityStateColor,
  infrastructurePlanValidityStateFromString,
  infrastructurePlanValidityStateLabel,
} from '@/domain/enumerate/infrastructure-plan-validity-state';
import { ButtonTooltip } from '@ull-tfg/ull-tfg-vue';
import { http } from '@ull-tfg/ull-tfg-typescript';
import { onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import containerImage from '../../../../assets/images/3dimagen/container3D.png';
import facilityImage from '../../../../assets/images/3dimagen/facility3D.png';
import routeImage from '../../../../assets/images/3dimagen/route.png';
import vehicleImage from '../../../../assets/images/3dimagen/vehicle3D.png';
import CrudLayout from '../../components/common/CrudLayout.vue';

const { t } = useI18n();
const router = useRouter();

/** Response shape of GET /api/v1/application-overview */
interface ApplicationOverviewEntityCountsJson {
  containers: number;
  vehicles: number;
  facilities: number;
  infrastructurePlans: number;
}

interface ApplicationOverviewJson {
  entityCounts: ApplicationOverviewEntityCountsJson;
  recentInfrastructurePlans?: InfrastructurePlanSummaryJsonResponse[];
}

const entityCounts = reactive({
  containers: 0,
  vehicles: 0,
  facilities: 0,
  infrastructurePlans: 0,
});

const recentPlans = ref<InfrastructurePlanSummaryJsonResponse[]>([]);

/** Normalizes executedAt whether the API sends a string or a nested timestamp object. */
const rawExecutedAtToIso = (plan: InfrastructurePlanSummaryJsonResponse): string | null => {
  const raw = plan.executedAt as unknown;
  if (typeof raw === 'string' && raw.trim()) {
    return raw;
  }
  if (raw && typeof raw === 'object' && raw !== null && 'timestamp' in raw) {
    const ts = (raw as { timestamp?: unknown }).timestamp;
    if (typeof ts === 'string' && ts.trim()) {
      return ts;
    }
  }
  return null;
};

const formatExecutedAt = (plan: InfrastructurePlanSummaryJsonResponse) => {
  const iso = rawExecutedAtToIso(plan);
  if (!iso) {
    return '—';
  }
  try {
    return new Date(iso).toLocaleString();
  } catch {
    return iso;
  }
};

const planValidity = (plan: InfrastructurePlanSummaryJsonResponse) =>
  infrastructurePlanValidityStateFromString(plan.validityState);

const validityLabel = (plan: InfrastructurePlanSummaryJsonResponse) =>
  infrastructurePlanValidityStateLabel(t, planValidity(plan));

const showInfrastructurePlan = (itemId: string) => {
  void router.push({ name: 'ShowInfrastructurePlan', params: { id: itemId } });
};

const loadEntityCounts = async () => {
  const baseUrl = import.meta.env.VITE_APP_API_URL as string;
  const url = `${baseUrl}application-overview`;
  const headers = new Headers();
  headers.append('Content-Type', 'application/json');
  try {
    const response = await http.get(url, headers);
    if (!response.ok) {
      return;
    }
    const data = (await response.json()) as ApplicationOverviewJson;
    if (!data?.entityCounts) {
      return;
    }
    entityCounts.containers = Number(data.entityCounts.containers) || 0;
    entityCounts.vehicles = Number(data.entityCounts.vehicles) || 0;
    entityCounts.facilities = Number(data.entityCounts.facilities) || 0;
    entityCounts.infrastructurePlans = Number(data.entityCounts.infrastructurePlans) || 0;
    recentPlans.value = Array.isArray(data.recentInfrastructurePlans)
      ? data.recentInfrastructurePlans
      : [];
  } catch {
    /* keep zeros if API is unreachable */
  }
};

onMounted(() => {
  void loadEntityCounts();
});

/**
 * Navigate to the selected entity management view
 * @param routeName - The name of the route to navigate to
 */
const navigateTo = (routeName: string) => {
  void router.push({ name: routeName });
};
</script>

<style scoped>
.home-cards-stack {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-top: 0.5rem;
}

.home-recent-panel {
  position: sticky;
  top: 1rem;
}

.home-entity-row {
  margin-top: 0 !important;
}

.recent-plans-card {
  border: 1px solid rgba(var(--v-theme-neutral-base), 0.06);
  min-height: 200px;
}

.recent-plans-list :deep(.v-list-item__prepend) {
  align-self: flex-start;
  margin-top: 4px;
}

.recent-plans-list-item {
  border-bottom: 1px solid rgba(var(--v-theme-neutral-base), 0.06);
}

.recent-plans-list-item:last-child {
  border-bottom: none;
}

.selection-card {
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  border: 1px solid rgba(var(--v-theme-neutral-base), 0.05);
}

.selection-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(var(--v-theme-neutral-base), 0.1) !important;
  border-color: rgba(var(--v-theme-primary), 0.3);
}

.card-icon-wrapper {
  background: rgba(var(--v-theme-primary), 0.05);
  width: 140px;
  height: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  margin: 0 auto;
  transition: transform 0.3s ease;
}

.selection-card:hover .card-icon-wrapper {
  transform: scale(1.1);
  background: rgba(var(--v-theme-primary), 0.1);
}

.card-image {
  width: 118px;
  height: 118px;
  object-fit: contain;
}

.rounded-xl {
  border-radius: 24px !important;
}

.algorithm-card {
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  border: 1px solid rgba(var(--v-theme-neutral-base), 0.05);
}

.algorithm-card:hover {
  transform: translateX(8px);
  box-shadow: 0 12px 24px rgba(var(--v-theme-neutral-base), 0.1) !important;
  border-color: rgba(var(--v-theme-primary), 0.3);
}

.algorithm-image-container {
  width: 50%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgb(var(--v-theme-surface));
  flex-shrink: 0;
  position: relative;
}

.text-theme-muted {
  color: rgb(var(--v-theme-text-muted));
}

.algorithm-image-container::before {
  content: '';
  position: absolute;
  width: 240px;
  height: 240px;
  background: rgba(var(--v-theme-primary), 0.08);
  border-radius: 50%;
  transition: all 0.3s ease;
  z-index: 0;
}

.algorithm-card:hover .algorithm-image-container::before {
  width: 280px;
  height: 280px;
  background: rgba(var(--v-theme-primary), 0.12);
}

.algorithm-image-full {
  width: 90%;
  height: 90%;
  object-fit: contain;
  position: relative;
  z-index: 1;
}

.algorithm-content {
  width: 50%;
  flex-grow: 1;
}

.algorithm-card-title {
  line-height: 1.2;
}

.entity-card-title {
  line-height: 1.2;
}

.entity-count-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 2.75rem;
  height: 2.75rem;
  padding: 0 0.35rem;
  border-radius: 9999px;
  background: rgba(var(--v-theme-primary), 0.05);
  color: rgb(var(--v-theme-primary));
  font-size: 1.125rem;
  font-weight: 800;
  flex-shrink: 0;
  transition: background 0.3s ease;
}

.selection-card:hover .entity-count-badge,
.algorithm-card:hover .entity-count-badge {
  background: rgba(var(--v-theme-primary), 0.1);
}
</style>
