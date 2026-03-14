import {
    createRouter,
    createWebHistory,
    type Router,
    //type NavigationGuardNext,
    //type RouteLocationNormalized,
    type RouteRecordRaw,
} from 'vue-router';

const routes: RouteRecordRaw[] = [
    {
        path: '/',
        name: 'Home',
        redirect: '/vehicles',
    },
    {
        path: '/vehicles',
        name: 'Vehicles',
        component: () => import('../view/VehiclesView.vue'),
    },
    {
        path: '/vehicles/add',
        name: 'AddVehicle',
        component: () => import('../view/AddVehicle.vue'),
    },
    {
        path: '/vehicles/:id/edit',
        name: 'EditVehicle',
        component: () => import('../view/EditVehicle.vue'),
        props: true,
    },
    {
        path: '/vehicles/:id',
        name: 'ShowVehicle',
        component: () => import('../view/ShowVehicle.vue'),
        props: true,
    },
];

const router: Router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
