import {
    createRouter,
    createWebHistory,
    type Router,
    type RouteRecordRaw
} from 'vue-router';

const routes: RouteRecordRaw[] = [
    // Define the routes , when its needed
];

const router: Router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
