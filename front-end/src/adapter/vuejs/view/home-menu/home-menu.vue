<template>
  <CrudLayout
    :title="t('selection.welcome')"
    icon="mdi-apps"
    :content-md="12"
    :content-lg="10"
  >
    <!-- Welcome Message -->
    <div class="mb-12 mt-6 px-4">
      <p class="text-body-1 text-grey-darken-2 text-center">
        {{ t('selection.welcomeDescription') }}
      </p>
    </div>

    <!-- Algorithm Button - Full Width -->
    <v-row justify="center" class="mt-2 mb-12">
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
                <h3 class="text-h4 font-weight-bold text-primary mb-3">
                  {{ t('selection.types.algorithm.title') }}
                </h3>
              </div>
              <div>
                <p class="text-body-2 text-grey-darken-2 ma-0">
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

    <!-- Entity Management Section -->
    <div class="mb-10 mt-8 px-4">
      <SectionSubtitle
        :text="t('selection.title')"
        align="center"
        fontWeight="400"
        fontSize="1.25rem"
        textColor="#757575"
        :underline="true"
        lineColor="primary"
        :lineOpacity="0.3"
      />
    </div>

    <div class="mb-6 mt-2 px-4">
      <p class="text-body-1 text-grey-darken-2 text-center">
        {{ t('selection.subtitle') }}
      </p>
    </div>

    <v-row justify="center" class="mt-4 pb-12">
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
          <v-card-title class="text-h4 font-weight-bold mb-4 text-primary">
            {{ t('selection.types.vehicles.title') }}
          </v-card-title>
          <v-card-text class="text-body-1 text-grey-darken-2 flex-grow-1">
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
          <v-card-title class="text-h4 font-weight-bold mb-4 text-primary">
            {{ t('selection.types.containers.title') }}
          </v-card-title>
          <v-card-text class="text-body-1 text-grey-darken-2 flex-grow-1">
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
          <v-card-title class="text-h4 font-weight-bold mb-4 text-primary">
            {{ t('selection.types.facilities.title') }}
          </v-card-title>
          <v-card-text class="text-body-1 text-grey-darken-2 flex-grow-1">
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
  </CrudLayout>
</template>

<script setup lang="ts">
import { ButtonTooltip, SectionSubtitle } from '@ull-tfg/ull-tfg-vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import containerImage from '../../../../assets/images/3dimagen/container3D.png';
import facilityImage from '../../../../assets/images/3dimagen/facility3D.png';
import routeImage from '../../../../assets/images/3dimagen/route.png';
import vehicleImage from '../../../../assets/images/3dimagen/vehicle3D.png';
import CrudLayout from '../../components/common/CrudLayout.vue';

const { t } = useI18n();
const router = useRouter();

/**
 * Navigate to the selected entity management view
 * @param routeName - The name of the route to navigate to
 */
const navigateTo = (routeName: string) => {
  router.push({ name: routeName });
};
</script>

<style scoped>
.selection-card {
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.selection-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1) !important;
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
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.algorithm-card:hover {
  transform: translateX(8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1) !important;
  border-color: rgba(var(--v-theme-primary), 0.3);
}

.algorithm-image-container {
  width: 50%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(var(--v-theme-primary), 0.02);
  flex-shrink: 0;
  position: relative;
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
</style>
