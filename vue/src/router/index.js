// src/router/index.js
import { createRouter, createWebHistory } from "vue-router";
import Tunnel from "@/views/Tunnel.vue"; // ✅ 加上这一行
import Environment from "@/views/Environment.vue";
import Camera from "@/views/Camera.vue";
import Device from "@/views/Device.vue";
import Warning from "@/views/Warning.vue";
import Settings from "@/views/Settings.vue";

const routes = [
  { path: "/", redirect: "/environment" },
  { path: "/environment", component: Environment },
  { path: "/tunnel", component: Tunnel },
  { path: "/camera", component: Camera },
  { path: "/device", component: Device },
  { path: "/warning", component: Warning },
  { path: "/settings", component: Settings },
  {
    path: '/tunnel',
    name: 'TunnelManagement',
    component: () => import('@/views/TunnelManagement.vue')
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
