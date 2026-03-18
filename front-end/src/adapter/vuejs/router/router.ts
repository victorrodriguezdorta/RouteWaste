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
        name: 'Home',
        redirect: '/vehicles',
    },
    {
        path: '/vehicles',
        name: 'Vehicles',
        component: () => import('../view/vehicle-crud/vehicles-view.vue'),
    },
    {
        path: '/vehicles/add',
        name: 'AddVehicle',
        component: () => import('../view/vehicle-crud/add-vehicle.vue'),
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
