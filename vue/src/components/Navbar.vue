<template>
  <header
    class="h-20 bg-dark-blue-transparent border-b border-blue-800 flex items-center justify-between px-6 z-10"
  >
    <!-- LOGO -->
    <div class="flex items-center">
      <i class="fas fa-subway text-orange text-2xl mr-3"></i>
      <h1 class="text-2xl font-bold text-white">地铁隧道监测系统</h1>
    </div>

    <!-- 中间导航 -->
    <nav class="flex-1 flex items-center justify-center">
      <ul class="flex space-x-8">
        <li v-for="item in navItems" :key="item.route">
          <router-link
            :to="item.route"
            class="flex items-center px-4 py-2 rounded-md transition-all duration-200 text-white"
            active-class="active-link"
            exact-active-class="active-link"
          >
            <!-- 图标颜色根据激活类控制 -->
            <i :class="['fas', item.icon, 'text-xl mr-2', 'icon']"></i>
            <span>{{ item.name }}</span>
          </router-link>
        </li>
      </ul>
    </nav>

    <!-- 状态时间 -->
    <div class="flex items-center text-white">
      <div class="mr-6 flex items-center">
        <i class="fas fa-circle text-green-500 animate-pulse mr-2"></i>
        <span>系统运行正常</span>
      </div>
      <div class="text-xl">{{ currentDateTime }}</div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";

const navItems = [
  { name: "隧道监测", icon: "fa-chart-line", route: "/tunnel" },
  { name: "环境监测", icon: "fa-temperature-high", route: "/environment" },
  { name: "摄像头画面", icon: "fa-video", route: "/camera" },
  { name: "设备管理", icon: "fa-cogs", route: "/device" },
  { name: "告警管理", icon: "fa-bell", route: "/warning" },
  { name: "系统设置", icon: "fa-wrench", route: "/settings" },
];

const currentDateTime = ref("");
let timer;

function updateDateTime() {
  const now = new Date();
  const y = now.getFullYear();
  const m = String(now.getMonth() + 1).padStart(2, "0");
  const d = String(now.getDate()).padStart(2, "0");
  const h = String(now.getHours()).padStart(2, "0");
  const min = String(now.getMinutes()).padStart(2, "0");
  const s = String(now.getSeconds()).padStart(2, "0");
  currentDateTime.value = `${y}-${m}-${d} ${h}:${min}:${s}`;
}

onMounted(() => {
  updateDateTime();
  timer = setInterval(updateDateTime, 1000);
});

onUnmounted(() => {
  if (timer) {
    clearInterval(timer);
  }
});
</script>

<style scoped>
.bg-dark-blue-transparent {
  background-color: rgba(26, 31, 61, 0.9);
}

/* 默认图标灰色 */
.icon {
  color: #9ca3af; /* tailwind 的 gray-400 */
}

/* 激活状态的 router-link 会加这个类 */
.active-link .icon {
  color: #ff7b00 !important; /* 橙色 */
}

.active-link {
  background-color: #1e3a8a; /* 深蓝背景 */
}
</style>
