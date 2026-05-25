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
        component: () => import('../view/home-menu/HomeMenu.vue'),
    },
    {
        path: '/vehicles',
        name: 'Vehicles',
        component: () => import('../view/vehicle-crud/VehiclesView.vue'),
    },
    {
        path: '/containers',
        name: 'Containers',
        component: () => import('../view/container-crud/ContainersView.vue'),
    },
    {
        path: '/facilities',
        name: 'Facilities',
        component: () => import('../view/facility-crud/FacilitiesView.vue'),
    },
    {
        path: '/algorithm',
        name: 'Algorithm',
        component: () => import('../view/algorithm/AlgorithmView.vue'),
    },
    {
        path: '/vehicles/add',
        name: 'AddVehicle',
        component: () => import('../view/vehicle-crud/AddVehicle.vue'),
    },
    {
        path: '/containers/add',
        name: 'AddContainer',
        component: () => import('../view/container-crud/AddContainer.vue'),
    },
    {
        path: '/facilities/add',
        name: 'AddFacility',
        component: () => import('../view/facility-crud/AddFacility.vue'),
    },
    {
        path: '/containers/:id/edit',
        name: 'EditContainer',
        component: () => import('../view/container-crud/EditContainer.vue'),
        props: true,
    },
    {
        path: '/containers/:id',
        name: 'ShowContainer',
        component: () => import('../view/container-crud/ShowContainer.vue'),
        props: true,
    },
    {
        path: '/vehicles/:id/edit',
        name: 'EditVehicle',
        component: () => import('../view/vehicle-crud/EditVehicle.vue'),
        props: true,
    },
    {
        path: '/vehicles/:id',
        name: 'ShowVehicle',
        component: () => import('../view/vehicle-crud/ShowVehicle.vue'),
        props: true,
    },
    {
        path: '/facilities/:id/edit',
        name: 'EditFacility',
        component: () => import('../view/facility-crud/EditFacility.vue'),
        props: true,
    },
    {
        path: '/facilities/:id',
        name: 'ShowFacility',
        component: () => import('../view/facility-crud/ShowFacility.vue'),
        props: true,
    },
    {
        path: '/algorithm/execute',
        name: 'ExecuteAlgorithm',
        component: () => import('../view/algorithm/ExecuteAlgorithm.vue'),
    },
    {
        path: '/algorithm/:id',
        name: 'ShowInfrastructurePlan',
        component: () => import('../view/algorithm/ShowInfrastructurePlan.vue'),
        props: true,
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: () => import('../view/not-found/NotFound.vue'),
    },
];

const router: Router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
