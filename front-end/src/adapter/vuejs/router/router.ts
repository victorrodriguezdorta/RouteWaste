import {
  createRouter,
  createWebHistory,
  type Router,
  //type NavigationGuardNext,
  //type RouteLocationNormalized,
  type RouteRecordRaw,
} from 'vue-router';

/**
 * Vue Router configuration for the Vehicle Management App.
 * Defines routes for listing, adding, editing, and showing vehicles.
 * Uses lazy loading for route components to optimize performance.
 */
const routes: RouteRecordRaw[] = [
    {
        path: '/',
        name: 'Selection',
        component: () => import('../view/selection/selection-view.vue'),
    },
    {
        path: '/vehicles',
        name: 'Vehicles',
        component: () => import('../view/vehicle-crud/vehicles-view.vue'),
    },
    {
        path: '/containers',
        name: 'Containers',
        component: () => import('../view/container-crud/containers-view.vue'),
    },
    {
        path: '/facilities',
        name: 'Facilities',
        component: () => import('../view/vehicle-crud/vehicles-view.vue'), // Placeholder
    },
    {
        path: '/vehicles/add',
        name: 'AddVehicle',
        component: () => import('../view/vehicle-crud/add-vehicle.vue'),
    },
    {
        path: '/containers/add',
        name: 'AddContainer',
        component: () => import('../view/container-crud/add-container.vue'),
    },
    {
        path: '/containers/:id/edit',
        name: 'EditContainer',
        component: () => import('../view/container-crud/edit-container.vue'),
        props: true,
    },
    {
        path: '/containers/:id',
        name: 'ShowContainer',
        component: () => import('../view/container-crud/show-container.vue'),
        props: true,
    },
    {
        path: '/vehicles/:id/edit',
        name: 'EditVehicle',
        component: () => import('../view/vehicle-crud/edit-vehicle.vue'),
        props: true,
    },
    {
        path: '/vehicles/:id',
        name: 'ShowVehicle',
        component: () => import('../view/vehicle-crud/show-vehicle.vue'),
        props: true,
    },
];

const router: Router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
