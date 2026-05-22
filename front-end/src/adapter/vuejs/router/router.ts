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
        component: () => import('../view/home-menu/home-menu.vue'),
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
        component: () => import('../view/facility-crud/facilities-view.vue'),
    },
    {
        path: '/algorithm',
        name: 'Algorithm',
        component: () => import('../view/algorithm/algorithm-view.vue'),
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
        path: '/facilities/add',
        name: 'AddFacility',
        component: () => import('../view/facility-crud/add-facility.vue'),
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
    {
        path: '/facilities/:id/edit',
        name: 'EditFacility',
        component: () => import('../view/facility-crud/edit-facility.vue'),
        props: true,
    },
    {
        path: '/facilities/:id',
        name: 'ShowFacility',
        component: () => import('../view/facility-crud/show-facility.vue'),
        props: true,
    },
    {
        path: '/algorithm/execute',
        name: 'ExecuteAlgorithm',
        component: () => import('../view/algorithm/execute-algorithm.vue'),
    },
    {
        path: '/algorithm/:id',
        name: 'ShowInfrastructurePlan',
        component: () => import('../view/algorithm/show-infrastructure-plan.vue'),
        props: true,
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: () => import('../view/not-found/not-found.vue'),
    },
];

const router: Router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
