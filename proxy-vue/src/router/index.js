import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('../views/Home.vue'),
    hidden: true
  },
  {
    path: '/proxy',
    component: () => import('../views/Proxy.vue'),
    hidden: true
  },
  {
    path: '/text',
    component: () => import('../views/Text.vue'),
    hidden: true
  },
  {
    path: '/donate',
    component: () => import('../views/Donate.vue'),
    hidden: true
  },
  {
    path: '/detectionIp',
    component: () => import('../views/DetectionIp.vue'),
    hidden: true
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 