import { createRouter, createWebHistory } from 'vue-router'; // imports functions to use
import type { RouteRecordRaw } from 'vue-router'; // import types to use
import Dashboard from '../pages/Dashboard.vue';
//import CompanySummaries from '../pages/CompanySummaries.vue';
//import SectorSummaries from '../pages/SectorSummaries.vue';

// Type the routes
const routes: Array<RouteRecordRaw> = [ //RouteRecordRaw is a predefined interface from vue-router package, It tells TypeScript, every object in this array must have fields like path, component, maybe children, etc.
  { path: '/', component: Dashboard },
  //{ path: '/companies', component: CompanySummaries },
  //{ path: '/sectors', component: SectorSummaries },
];

// Create the router
// history - How URLs should behave (browser history, hash history, memory history). URLs like http://localhost:5173/companies (no # signs like /#/companies).
// routes - The list of URL paths and matching components created above
const router = createRouter({
  history: createWebHistory(),
  routes,
});

// export default router - this file exports one main thing — the router object. When another file imports this, like in main.ts import router from './router'
export default router;