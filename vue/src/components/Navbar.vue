<template>
  <header
    class="h-16 lg:h-20 xl:h-24 bg-dark-blue-transparent border-b border-blue-800 flex items-center justify-between px-4 lg:px-6 xl:px-8 z-10"
  >
    <!-- LOGO -->
    <div class="flex items-center shrink-0">
      <i class="fas fa-subway text-orange text-xl lg:text-2xl xl:text-3xl mr-2 lg:mr-3"></i>
      <h1 class="text-lg lg:text-2xl xl:text-3xl font-bold text-white whitespace-nowrap">地铁隧道监测系统</h1>
    </div>

    <!-- 中间导航 -->
    <nav class="flex-1 flex items-center justify-center px-4 overflow-hidden">
      <ul class="flex items-center space-x-1 md:space-x-2 lg:space-x-4">
        <!-- 显示的导航项 -->
        <template v-for="(item, index) in navItems" :key="item.route">
          <li v-show="index < visibleItems" class="shrink-0">
            <router-link
              :to="item.route"
              class="flex items-center px-2 lg:px-3 py-1.5 lg:py-2 rounded-md transition-all duration-200 text-white text-sm lg:text-base whitespace-nowrap"
              active-class="active-link"
              exact-active-class="active-link"
            >
              <i :class="['fas', item.icon, 'text-lg lg:text-xl mr-1.5', 'icon']"></i>
              <span>{{ item.name }}</span>
            </router-link>
          </li>
        </template>
        
        <!-- 更多按钮 -->
        <li v-if="visibleItems < navItems.length" class="shrink-0 relative">
          <button 
            @click="toggleMoreMenu"
            class="flex items-center px-2 py-1.5 rounded-md text-white hover:bg-blue-800 transition-colors duration-200"
          >
            <i class="fas fa-ellipsis-h text-lg"></i>
          </button>
          
          <!-- 更多菜单 -->
          <div 
            v-show="showMoreMenu"
            class="absolute right-0 top-full mt-1 bg-dark-blue-transparent border border-blue-800 rounded-md shadow-lg py-2 min-w-[160px] z-50"
          >
            <template v-for="(item, index) in navItems" :key="item.route">
              <router-link
                v-if="index >= visibleItems"
                :to="item.route"
                class="flex items-center px-4 py-2 text-white hover:bg-blue-800 transition-colors duration-200 text-sm whitespace-nowrap"
                active-class="active-link"
                exact-active-class="active-link"
                @click="showMoreMenu = false"
              >
                <i :class="['fas', item.icon, 'text-lg mr-2', 'icon']"></i>
                <span>{{ item.name }}</span>
              </router-link>
            </template>
          </div>
        </li>
      </ul>
    </nav>

    <!-- 状态时间 -->
    <div class="flex items-center text-white shrink-0">
      <div class="hidden md:flex items-center mr-4 lg:mr-6 text-sm lg:text-base whitespace-nowrap">
        <i class="fas fa-circle text-green-500 animate-pulse mr-1.5 lg:mr-2"></i>
        <span>系统运行正常</span>
      </div>
      <div class="text-base lg:text-lg whitespace-nowrap">{{ currentDateTime }}</div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from "vue";

const showMoreMenu = ref(false);
const visibleItems = ref(6); // 默认显示的导航项数量
const containerWidth = ref(0);

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

// 更新可见导航项数量
const updateVisibleItems = () => {
  const width = window.innerWidth;
  if (width < 640) { // sm
    visibleItems.value = 2;
  } else if (width < 768) { // md
    visibleItems.value = 3;
  } else if (width < 1024) { // lg
    visibleItems.value = 4;
  } else if (width < 1280) { // xl
    visibleItems.value = 5;
  } else {
    visibleItems.value = 6;
  }
};

const toggleMoreMenu = () => {
  showMoreMenu.value = !showMoreMenu.value;
};

// 点击外部关闭更多菜单
const handleClickOutside = (event) => {
  const moreMenu = document.querySelector('.more-menu');
  const moreButton = document.querySelector('.more-button');
  if (moreMenu && !moreMenu.contains(event.target) && !moreButton?.contains(event.target)) {
    showMoreMenu.value = false;
  }
};

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
  updateVisibleItems();
  window.addEventListener('resize', updateVisibleItems);
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  if (timer) {
    clearInterval(timer);
  }
  window.removeEventListener('resize', updateVisibleItems);
  document.removeEventListener('click', handleClickOutside);
});
</script>

<style scoped>
.bg-dark-blue-transparent {
  background-color: rgba(26, 31, 61, 0.9);
}

/* 默认图标灰色 */
.icon {
  color: #9ca3af; /* tailwind 的 gray-400 */
  transition: color 0.2s ease;
}

/* 激活状态的 router-link 会加这个类 */
.active-link {
  background-color: #1e3a8a; /* 深蓝背景 */
  .icon {
    color: #ff7b00 !important; /* 橙色 */
  }
}

.active-link:hover {
  background-color: #1e40af; /* 稍微亮一点的蓝色 */
}

/* 非激活状态的hover效果 */
.router-link-active:not(.active-link):hover {
  background-color: rgba(30, 58, 138, 0.5);
  .icon {
    color: #ffa500;
  }
}
</style>
