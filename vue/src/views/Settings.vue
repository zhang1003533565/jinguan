<template>
  <div class="bg-[#0D122A] min-h-screen text-white flex font-sans">
    <!-- 左侧美化折叠菜单栏（终极美化版） -->
    <aside class="w-72 bg-[#1B1F3B] p-4 shadow-xl text-white text-[15px]">
      <h2 class="text-xl font-bold mb-4">系统设置</h2>
      <div
        v-for="(group, gIndex) in menuTree"
        :key="gIndex"
        class="mb-3 border-t border-gray-600/30 pt-3 first:pt-0 first:border-0"
      >
        <div
          class="flex items-center justify-between cursor-pointer px-3 py-2 rounded-lg bg-[#2c2f4e] hover:bg-[#3c3f66] transition duration-200"
          @click="toggleGroup(group.title)"
        >
          <div class="flex items-center space-x-2 font-medium">
            <i :class="group.icon + ' w-5 text-center'" />
            <span>{{ group.title }}</span>
          </div>
          <i
            class="fas fa-chevron-down inline-block transform transition-transform duration-300"
            :class="{ 'rotate-180': expandedGroups.has(group.title) }"
          />
        </div>

        <ul v-if="expandedGroups.has(group.title)" class="mt-2 space-y-1">
          <li
            v-for="item in group.children"
            :key="item.key"
            @click="activeKey = item.key"
            :class="[
              'flex items-center cursor-pointer px-4 py-2 rounded-md text-sm transition transform hover:scale-[1.01]',
              activeKey === item.key
                ? 'bg-gradient-to-r from-blue-600 to-blue-800 text-white shadow-md'
                : 'text-gray-300 hover:bg-[#2d3253]',
            ]"
          >
            <span>{{ item.label }}</span>
          </li>
        </ul>
      </div>
    </aside>

    <!-- 右侧内容区域 -->
    <main class="flex-1 px-8 py-6 space-y-6 max-w-[1200px] mx-auto">
      <section
        v-if="activeKey === 'base'"
        class="bg-gradient-to-tr from-[#1b1f3b] to-[#232846] p-6 rounded-2xl shadow-xl border border-blue-900/30"
      >
        <h3 class="text-xl font-semibold mb-6">基础配置</h3>
        <div class="grid grid-cols-2 gap-6">
          <div>
            <label class="block text-sm mb-1">系统名称</label>
            <input
              v-model="form.systemName"
              type="text"
              class="w-full rounded-md bg-[#0D122A] border border-gray-600 shadow-inner px-3 py-2 focus:outline-none focus:ring-1 focus:ring-blue-500"
            />
          </div>
          <div>
            <label class="block text-sm mb-1">运行模式</label>
            <select
              v-model="form.runMode"
              class="w-full rounded-md bg-[#0D122A] border border-gray-600 px-3 py-2 focus:outline-none focus:ring-1 focus:ring-blue-500"
            >
              <option value="正式模式">正式模式</option>
              <option value="测试模式">测试模式</option>
            </select>
          </div>
        </div>
      </section>

      <section
        v-if="activeKey === 'sync'"
        class="bg-gradient-to-tr from-[#1b1f3b] to-[#232846] p-6 rounded-2xl shadow-xl border border-blue-900/30"
      >
        <h3 class="text-xl font-semibold mb-6">时间同步</h3>
        <div class="space-x-6">
          <label class="inline-flex items-center">
            <input
              type="radio"
              value="auto"
              v-model="form.timeSync"
              class="mr-2"
            />
            自动同步
          </label>
          <label class="inline-flex items-center">
            <input
              type="radio"
              value="manual"
              v-model="form.timeSync"
              class="mr-2"
            />
            手动设置
          </label>
        </div>
      </section>

      <section
        v-if="activeKey === 'admin'"
        class="bg-gradient-to-tr from-[#1b1f3b] to-[#232846] p-6 rounded-2xl shadow-xl border border-blue-900/30"
      >
        <h3 class="text-xl font-semibold mb-6">管理员设置</h3>
        <div class="grid grid-cols-2 gap-6">
          <div>
            <label class="block text-sm mb-1">管理员用户名</label>
            <input
              v-model="form.username"
              type="text"
              class="w-full rounded-md bg-[#0D122A] border border-gray-600 shadow-inner px-3 py-2"
            />
          </div>
          <div>
            <label class="block text-sm mb-1">新密码</label>
            <input
              v-model="form.password"
              type="password"
              class="w-full rounded-md bg-[#0D122A] border border-gray-600 shadow-inner px-3 py-2"
            />
          </div>
        </div>
      </section>

      <section
        v-if="activeKey === 'theme'"
        class="bg-gradient-to-tr from-[#1b1f3b] to-[#232846] p-6 rounded-2xl shadow-xl border border-blue-900/30"
      >
        <h3 class="text-xl font-semibold mb-6">主题设置</h3>
        <label class="block text-sm mb-1">主题模式</label>
        <select
          v-model="form.theme"
          class="w-full rounded-md bg-[#0D122A] border border-gray-600 px-3 py-2"
        >
          <option value="dark">暗黑模式</option>
          <option value="light">明亮模式</option>
        </select>
      </section>

      <section
        v-if="activeKey === 'log'"
        class="bg-gradient-to-tr from-[#1b1f3b] to-[#232846] p-6 rounded-2xl shadow-xl border border-blue-900/30"
      >
        <h3 class="text-xl font-semibold mb-6">日志设置</h3>
        <label class="block text-sm mb-1">日志保留时间</label>
        <select
          v-model="form.logDays"
          class="w-full rounded-md bg-[#0D122A] border border-gray-600 px-3 py-2"
        >
          <option value="7">7 天</option>
          <option value="30">30 天</option>
          <option value="forever">永久</option>
        </select>
      </section>

      <section
        v-if="activeKey === 'email'"
        class="bg-gradient-to-tr from-[#1b1f3b] to-[#232846] p-6 rounded-2xl shadow-xl border border-blue-900/30"
      >
        <h3 class="text-xl font-semibold mb-6">通知设置</h3>
        <div class="grid grid-cols-2 gap-6">
          <div>
            <label class="block text-sm mb-1">告警通知邮箱</label>
            <input
              v-model="form.alertEmail"
              type="email"
              class="w-full rounded-md bg-[#0D122A] border border-gray-600 shadow-inner px-3 py-2"
            />
          </div>
          <div>
            <label class="block text-sm mb-1">启用告警推送</label>
            <input
              type="checkbox"
              v-model="form.alertPush"
              class="form-checkbox mr-2"
            />
            启用
          </div>
        </div>
      </section>

      <div class="text-right mt-4">
        <button
          @click="saveSetting"
          class="bg-green-600 hover:bg-green-700 text-white px-6 py-2 rounded-full shadow transition"
        >
          保存设置
        </button>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";

const menuTree = [
  {
    title: "系统配置",
    icon: "fas fa-cogs",
    children: [
      { key: "base", label: "基础配置" },
      { key: "sync", label: "时间同步" },
    ],
  },
  {
    title: "账户权限",
    icon: "fas fa-user-shield",
    children: [{ key: "admin", label: "管理员设置" }],
  },
  {
    title: "界面主题",
    icon: "fas fa-paint-brush",
    children: [{ key: "theme", label: "主题设置" }],
  },
  {
    title: "日志通知",
    icon: "fas fa-bell",
    children: [
      { key: "log", label: "日志设置" },
      { key: "email", label: "通知设置" },
    ],
  },
];

const activeKey = ref("base");
const expandedGroups = ref(new Set(menuTree.map((g) => g.title)));
const toggleGroup = (title) => {
  expandedGroups.value.has(title)
    ? expandedGroups.value.delete(title)
    : expandedGroups.value.add(title);
};

const form = reactive({
  systemName: "地铁隧道监测系统",
  runMode: "正式模式",
  timeSync: "auto",
  username: "admin",
  password: "",
  theme: "dark",
  logDays: "30",
  alertEmail: "",
  alertPush: false,
});

const saveSetting = () => {
  console.log("保存设置:", form);
};
</script>

<style scoped>
@import url("https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css");
</style>
