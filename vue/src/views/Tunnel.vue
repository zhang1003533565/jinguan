<!-- The exported code uses Tailwind CSS. Install Tailwind CSS in your dev environment to ensure all styles work. -->
<template>
  <div class="min-h-screen bg-dark-blue text-white overflow-hidden">
    <!-- 顶部标题栏 -->

    <div class="flex h-[calc(100vh-80px)]">
      <!-- 主内容区 -->
      <main class="flex-1 flex flex-col">
        <!-- 主视图区 -->
        <div class="flex-1 p-4 relative">
          <div class="w-full h-full flex gap-4">
            <!-- 左侧监测数据面板 -->
            <div class="w-[400px] flex flex-col gap-4">
              <!-- 沉降监测数据 -->
              <div
                class="flex-1 bg-dark-blue-light rounded-lg border border-blue-800 shadow-xl p-4 flex flex-col"
              >
                <h3 class="text-lg font-bold mb-4 flex items-center">
                  <i class="fas fa-chart-bar mr-2 text-orange"></i>
                  沉降监测数据
                </h3>
                <div ref="settlementChartRef" class="flex-1"></div>
                <div
                  class="mt-4 p-3 bg-red-900/30 border border-red-800 rounded-md"
                  v-if="hasWarning"
                >
                  <div class="flex items-center text-red-400">
                    <i class="fas fa-exclamation-triangle mr-2"></i>
                    <span class="font-bold">沉降预警</span>
                  </div>
                  <p class="text-sm mt-1 text-red-300">
                    K5+300 - K5+350 区间沉降超过警戒值，请注意检查
                  </p>
                </div>
              </div>

              <!-- 事件日志 -->
              <div
                class="h-[300px] bg-dark-blue-light rounded-lg border border-blue-800 shadow-xl p-4"
              >
                <div class="flex items-center mb-3">
                  <h3 class="text-lg font-bold flex items-center">
                    <i class="fas fa-history mr-2 text-orange"></i>
                    事件日志
                  </h3>
                  <div class="ml-4 flex">
                    <span
                      class="px-2 py-1 rounded-md text-xs mr-2 bg-red-900 text-red-300"
                      >告警</span
                    >
                    <span
                      class="px-2 py-1 rounded-md text-xs mr-2 bg-yellow-900 text-yellow-300"
                      >提示</span
                    >
                    <span
                      class="px-2 py-1 rounded-md text-xs bg-blue-900 text-blue-300"
                      >操作</span
                    >
                  </div>
                </div>
                <div
                  class="h-[calc(100%-40px)] overflow-hidden relative"
                  ref="eventLogContainer"
                >
                  <TransitionGroup
                    name="log-slide"
                    tag="div"
                    class="absolute w-full"
                  >
                    <div
                      v-for="(log, index) in eventLogs"
                      :key="log.time + log.message"
                      :class="[
                        'flex items-center p-2 mb-1 rounded-md transform transition-all duration-300',
                        log.type === 'alert'
                          ? 'bg-red-900/50'
                          : log.type === 'warning'
                          ? 'bg-yellow-900/30'
                          : 'bg-blue-900/30',
                      ]"
                    >
                      <i
                        :class="[
                          'fas',
                          log.type === 'alert'
                            ? 'fa-exclamation-triangle text-red-500'
                            : log.type === 'warning'
                            ? 'fa-exclamation-circle text-yellow-500'
                            : 'fa-info-circle text-blue-500',
                          'mr-3',
                        ]"
                      ></i>
                      <div class="flex-1">{{ log.message }}</div>
                      <div class="text-gray-400 text-sm">{{ log.time }}</div>
                    </div>
                  </TransitionGroup>
                </div>
              </div>
            </div>

            <!-- 右侧大屏显示区域 -->
            <div
              ref="dashboardContainer"
              class="flex-1 bg-dark-blue-light rounded-lg border border-blue-800 shadow-xl overflow-hidden"
            >
              <div class="w-full h-full p-6">
                <div class="flex items-center justify-between mb-6">
                  <h2 class="text-xl font-bold flex items-center">
                    <i class="fas fa-desktop text-orange mr-3"></i>
                    大屏监控
                  </h2>
                  <div class="flex items-center space-x-4">
                    <button
                      @click="toggleFullscreen"
                      class="px-4 py-2 bg-blue-900/50 hover:bg-blue-900 rounded-button flex items-center transition-all duration-200"
                    >
                      <i
                        :class="[
                          'fas',
                          isFullscreen
                            ? 'fa-compress-arrows-alt'
                            : 'fa-expand-arrows-alt',
                          'mr-2',
                        ]"
                      ></i>
                      {{ isFullscreen ? "退出全屏" : "全屏显示" }}
                    </button>
                    <button
                      class="px-4 py-2 bg-blue-900/50 hover:bg-blue-900 rounded-button flex items-center transition-all duration-200"
                    >
                      <i class="fas fa-sync-alt mr-2"></i>
                      刷新
                    </button>
                  </div>
                </div>
                <div
                  class="w-full h-[calc(100%-4rem)] bg-dark-blue rounded-lg border border-blue-900/50 p-4"
                >
                  <video
                    class="w-full h-full object-cover rounded-lg"
                    src="/assets/vedio/ditie.mp4"
                    autoplay
                    loop
                    muted
                    playsinline
                  ></video>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
      <!-- 右侧数据面板 -->
      <aside
        class="w-[320px] bg-dark-blue-light border-l border-blue-800 overflow-y-auto custom-scrollbar"
      >
        <!-- 列车运行信息 -->
        <div class="p-4 border-b border-blue-800">
          <h3 class="text-lg font-bold mb-4 flex items-center">
            <i class="fas fa-train mr-2 text-orange"></i>
            列车运行信息
          </h3>
          <!-- 速度仪表盘 -->
          <div class="mb-6">
            <div class="flex justify-between items-center mb-2">
              <span class="text-gray-300">当前速度</span>
              <span class="text-2xl font-bold text-cyan-400"
                >{{ trainSpeed }} <span class="text-sm">km/h</span></span
              >
            </div>
            <div class="w-full bg-dark-blue rounded-full h-3">
              <div
                class="bg-gradient-to-r from-green-500 to-cyan-500 h-3 rounded-full transition-all duration-500"
                :style="{ width: `${(trainSpeed / 120) * 100}%` }"
              ></div>
            </div>
          </div>
          <!-- 位置指示器 -->
          <div class="mb-6">
            <div class="flex justify-between items-center mb-2">
              <span class="text-gray-300">当前位置</span>
              <span class="text-cyan-400">{{ trainPosition }}m</span>
            </div>
            <div
              class="relative w-full h-8 bg-dark-blue rounded-md overflow-hidden border border-blue-800"
            >
              <div
                class="absolute top-0 left-0 w-full h-full flex items-center px-2"
              >
                <div class="w-2 h-2 rounded-full bg-gray-500 mx-1"></div>
                <div class="w-2 h-2 rounded-full bg-gray-500 mx-1"></div>
                <div class="w-2 h-2 rounded-full bg-gray-500 mx-1"></div>
                <div class="w-2 h-2 rounded-full bg-gray-500 mx-1"></div>
                <div class="w-2 h-2 rounded-full bg-gray-500 mx-1"></div>
                <div class="w-2 h-2 rounded-full bg-gray-500 mx-1"></div>
                <div class="w-2 h-2 rounded-full bg-gray-500 mx-1"></div>
                <div class="w-2 h-2 rounded-full bg-gray-500 mx-1"></div>
              </div>
              <div
                class="absolute top-0 h-full bg-orange/20 transition-all duration-500 flex items-center justify-center"
                :style="{
                  left: `${(trainPosition / 1000) * 100}%`,
                  width: '30px',
                }"
              >
                <i class="fas fa-subway text-orange"></i>
              </div>
            </div>
          </div>
          <!-- 运行方向 -->
          <div class="mb-4">
            <div class="flex justify-between items-center mb-2">
              <span class="text-gray-300">运行方向</span>
              <span class="text-cyan-400">{{
                trainDirection === 1 ? "正向" : "反向"
              }}</span>
            </div>
            <div class="flex justify-center">
              <div class="flex items-center bg-dark-blue p-3 rounded-full">
                <i
                  :class="[
                    'fas fa-arrow-right text-2xl transition-all duration-300 transform',
                    trainDirection === 1
                      ? 'text-orange'
                      : 'text-gray-600 rotate-180',
                  ]"
                ></i>
              </div>
            </div>
          </div>
        </div>
        <!-- 环境监测数据 -->
        <div class="p-4 border-b border-blue-800">
          <h3 class="text-lg font-bold mb-4 flex items-center">
            <i class="fas fa-chart-line mr-2 text-orange"></i>
            环境监测数据
          </h3>
          <!-- 温度 -->
          <div class="mb-4">
            <div class="flex justify-between items-center mb-1">
              <span class="text-gray-300">温度</span>
              <span
                :class="[
                  'text-xl font-bold',
                  temperature > 30
                    ? 'text-red-500'
                    : temperature < 15
                    ? 'text-blue-400'
                    : 'text-green-500',
                ]"
                >{{ temperature }}°C</span
              >
            </div>
            <div class="w-full bg-dark-blue rounded-full h-2">
              <div
                :class="[
                  'h-2 rounded-full transition-all duration-500',
                  temperature > 30
                    ? 'bg-red-500'
                    : temperature < 15
                    ? 'bg-blue-400'
                    : 'bg-green-500',
                ]"
                :style="{ width: `${(temperature / 50) * 100}%` }"
              ></div>
            </div>
          </div>
          <!-- 湿度 -->
          <div class="mb-4">
            <div class="flex justify-between items-center mb-1">
              <span class="text-gray-300">湿度</span>
              <span
                :class="[
                  'text-xl font-bold',
                  humidity > 80
                    ? 'text-blue-400'
                    : humidity < 30
                    ? 'text-yellow-500'
                    : 'text-green-500',
                ]"
                >{{ humidity }}%</span
              >
            </div>
            <div class="w-full bg-dark-blue rounded-full h-2">
              <div
                :class="[
                  'h-2 rounded-full transition-all duration-500',
                  humidity > 80
                    ? 'bg-blue-400'
                    : humidity < 30
                    ? 'bg-yellow-500'
                    : 'bg-green-500',
                ]"
                :style="{ width: `${humidity}%` }"
              ></div>
            </div>
          </div>
          <!-- 瓦斯浓度 -->
          <div class="mb-4">
            <div class="flex justify-between items-center mb-1">
              <span class="text-gray-300">瓦斯浓度</span>
              <span
                :class="[
                  'text-xl font-bold',
                  gasConcentration > 0.8
                    ? 'text-red-500'
                    : gasConcentration > 0.5
                    ? 'text-yellow-500'
                    : 'text-green-500',
                ]"
                >{{ gasConcentration }}%</span
              >
            </div>
            <div class="w-full bg-dark-blue rounded-full h-2">
              <div
                :class="[
                  'h-2 rounded-full transition-all duration-500',
                  gasConcentration > 0.8
                    ? 'bg-red-500'
                    : gasConcentration > 0.5
                    ? 'bg-yellow-500'
                    : 'bg-green-500',
                ]"
                :style="{ width: `${(gasConcentration / 2) * 100}%` }"
              ></div>
            </div>
          </div>
          <!-- 空气质量 -->
          <div class="mb-4">
            <div class="flex justify-between items-center mb-1">
              <span class="text-gray-300">空气质量指数</span>
              <span
                :class="[
                  'text-xl font-bold',
                  airQuality > 150
                    ? 'text-red-500'
                    : airQuality > 100
                    ? 'text-yellow-500'
                    : 'text-green-500',
                ]"
                >{{ airQuality }}</span
              >
            </div>
            <div class="w-full bg-dark-blue rounded-full h-2">
              <div
                :class="[
                  'h-2 rounded-full transition-all duration-500',
                  airQuality > 150
                    ? 'bg-red-500'
                    : airQuality > 100
                    ? 'bg-yellow-500'
                    : 'bg-green-500',
                ]"
                :style="{ width: `${(airQuality / 300) * 100}%` }"
              ></div>
            </div>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { ref, onMounted, onUnmounted, computed } from "vue";
import * as echarts from "echarts";
import { TransitionGroup } from "vue";
import tunnelApi from '@/api/tunnel'
import environmentApi from '@/api/environment'
import warningApi from '@/api/warning'
import { ElMessage } from 'element-plus'

// 时间显示
const currentDateTime = ref("");
// 导航项
const navItems = [
  { name: "隧道监测", icon: "fa-chart-line" },
  { name: "环境监测", icon: "fa-temperature-high" },
  { name: "摄像头画面", icon: "fa-video" },
  { name: "设备管理", icon: "fa-cogs" },
  { name: "告警管理", icon: "fa-bell" },
  { name: "系统设置", icon: "fa-wrench" },
];
const activeNavIndex = ref(0);
// 列车数据
const trainSpeed = ref(0);
const trainPosition = ref(0);
const trainDirection = ref(1);
const maxPosition = 1000;
// 环境数据
const temperature = ref(25);
const humidity = ref(65);
const gasConcentration = ref(0.2);
const airQuality = ref(85);
// 沉降数据
const settlementData = ref([
  { name: "K5+100", value: 2.1 },
  { name: "K5+150", value: 2.3 },
  { name: "K5+200", value: 2.5 },
  { name: "K5+250", value: 3.2 },
  { name: "K5+300", value: 5.8 },
  { name: "K5+350", value: 6.2 },
  { name: "K5+400", value: 3.5 },
  { name: "K5+450", value: 2.8 },
]);
// 事件日志
const eventLogs = ref([
  { type: "alert", message: "K5+300区段沉降超过警戒值6mm", time: "14:32:45" },
  { type: "warning", message: "隧道温度上升至28°C", time: "14:30:12" },
  { type: "info", message: "系统自检完成", time: "14:00:00" },
  { type: "info", message: "列车进入监测区域", time: "13:55:23" },
  { type: "warning", message: "瓦斯浓度轻微上升", time: "13:50:18" },
  { type: "info", message: "系统启动", time: "13:45:00" },
]);
// 模拟状态
const isSimulating = ref(false);
const simulationInterval = ref<number | null>(null);
// 图表引用
const tunnelChartRef = ref<HTMLElement | null>(null);
const settlementChartRef = ref<HTMLElement | null>(null);
let tunnelChart: echarts.ECharts | null = null;
let settlementChart: echarts.ECharts | null = null;
// 计算属性
const hasWarning = computed(() => {
  return settlementData.value.some((item) => item.value > 5);
});
// 更新时间
function updateDateTime() {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");
  const hours = String(now.getHours()).padStart(2, "0");
  const minutes = String(now.getMinutes()).padStart(2, "0");
  const seconds = String(now.getSeconds()).padStart(2, "0");
  currentDateTime.value = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}
// 初始化隧道图表
function initTunnelChart() {
  if (!tunnelChartRef.value) return;
  tunnelChart = echarts.init(tunnelChartRef.value);
  const option = {
    animation: false,
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "shadow",
      },
    },
    grid: {
      top: 40,
      bottom: 50,
      left: 60,
      right: 40,
    },
    xAxis: {
      type: "category",
      data: [
        "K5+100",
        "K5+150",
        "K5+200",
        "K5+250",
        "K5+300",
        "K5+350",
        "K5+400",
        "K5+450",
      ],
      axisLine: {
        lineStyle: {
          color: "#2a385c",
        },
      },
      axisTick: {
        show: false,
      },
      axisLabel: {
        color: "#8e9dbd",
      },
    },
    yAxis: {
      type: "value",
      name: "隧道剖面",
      nameTextStyle: {
        color: "#8e9dbd",
        padding: [0, 0, 0, 40],
      },
      splitLine: {
        lineStyle: {
          color: "#2a385c",
          type: "dashed",
        },
      },
      axisLine: {
        show: true,
        lineStyle: {
          color: "#2a385c",
        },
      },
      axisTick: {
        show: false,
      },
      axisLabel: {
        color: "#8e9dbd",
      },
    },
    series: [
      {
        name: "隧道轮廓",
        type: "line",
        smooth: true,
        symbol: "none",
        lineStyle: {
          width: 3,
          color: "#3d5a89",
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: "rgba(61, 90, 137, 0.7)" },
            { offset: 1, color: "rgba(26, 31, 61, 0.1)" },
          ]),
        },
        data: [30, 32, 35, 38, 35, 32, 30, 28],
      },
      {
        name: "沉降热力",
        type: "bar",
        barWidth: 40,
        itemStyle: {
          color: function (params: any) {
            const value = settlementData.value[params.dataIndex].value;
            if (value > 5) return "#ff4d4f";
            if (value > 4) return "#faad14";
            if (value > 3) return "#52c41a";
            return "#1890ff";
          },
        },
        data: settlementData.value.map((item) => item.value),
      },
      {
        name: "列车位置",
        type: "scatter",
        symbol: "rect",
        symbolSize: [30, 10],
        itemStyle: {
          color: "#ff7b00",
        },
        data: [
          {
            name: "列车",
            value: [Math.floor(trainPosition.value / 50), 0],
            symbolOffset: [0, -20],
          },
        ],
      },
    ],
  };
  tunnelChart.setOption(option);
}
// 初始化沉降图表
function initSettlementChart() {
  if (!settlementChartRef.value) return;

  // 销毁已存在的图表实例
  if (settlementChart) {
    settlementChart.dispose();
  }

  settlementChart = echarts.init(settlementChartRef.value);
  const option = {
    backgroundColor: "transparent",
    animation: false,
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "shadow",
      },
    },
    grid: {
      top: "15%",
      bottom: "12%",
      left: "8%",
      right: "8%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: ["K5+100", "K5+150", "K5+200", "K5+250", "K5+300", "K5+350"],
      axisLine: {
        lineStyle: {
          color: "#2a385c",
        },
      },
      axisTick: {
        show: false,
      },
      axisLabel: {
        color: "#8e9dbd",
        fontSize: 12,
        interval: 0,
      },
    },
    yAxis: {
      type: "value",
      min: 0,
      max: 7,
      interval: 1,
      axisLabel: {
        color: "#8e9dbd",
        fontSize: 12,
      },
      splitLine: {
        lineStyle: {
          color: "#2a385c",
          type: "dashed",
          opacity: 0.3,
        },
      },
      axisLine: {
        show: false,
      },
      axisTick: {
        show: false,
      },
    },
    series: [
      {
        name: "沉降值",
        type: "bar",
        barWidth: "50%",
        itemStyle: {
          color: function (params: any) {
            const value = params.value;
            if (value > 5) return "#ff4d4f";
            if (value > 4) return "#52c41a";
            return "#1890ff";
          },
        },
        data: [2.1, 2.3, 2.5, 3.2, 5.8, 6.2],
      },
      {
        name: "警戒线",
        type: "line",
        symbol: "none",
        lineStyle: {
          color: "#ff4d4f",
          type: "dashed",
          width: 1,
        },
        data: new Array(6).fill(5),
        z: 2,
      },
    ],
  };
  settlementChart.setOption(option);
}
// 更新隧道图表
function updateTunnelChart() {
  if (!tunnelChart) return;
  const trainIndex = Math.floor(trainPosition.value / (maxPosition / 8));
  const option = {
    series: [
      {},
      {
        data: settlementData.value.map((item) => item.value),
      },
      {
        data: [
          {
            name: "列车",
            value: [trainIndex >= 8 ? 7 : trainIndex, 0],
            symbolOffset: [0, -20],
          },
        ],
      },
    ],
  };
  tunnelChart.setOption(option);
}
// 更新沉降图表
function updateSettlementChart() {
  if (!settlementChart) return;

  const newData = [
    Math.random() * 2 + 1.5, // K5+100
    Math.random() * 2 + 1.8, // K5+150
    Math.random() * 2 + 2.0, // K5+200
    Math.random() * 2 + 2.5, // K5+250
    Math.random() * 1 + 5.5, // K5+300
    Math.random() * 1 + 5.8, // K5+350
  ];

  settlementChart.setOption({
    series: [
      {
        data: newData,
      },
      {
        data: new Array(6).fill(5),
      },
    ],
  });
}
// 切换模拟状态
function toggleSimulation() {
  isSimulating.value = !isSimulating.value;
  if (isSimulating.value) {
    startSimulation();
    addEventLog("info", "开始模拟列车运行");
  } else {
    stopSimulation();
    addEventLog("info", "停止模拟列车运行");
  }
}
// 开始模拟
function startSimulation() {
  if (simulationInterval.value !== null) {
    clearInterval(simulationInterval.value);
  }
  simulationInterval.value = window.setInterval(() => {
    // 更新列车位置
    if (trainDirection.value === 1) {
      trainPosition.value += trainSpeed.value / 10;
      if (trainPosition.value >= maxPosition) {
        trainDirection.value = -1;
        addEventLog("info", "列车到达终点站，开始返程");
      }
    } else {
      trainPosition.value -= trainSpeed.value / 10;
      if (trainPosition.value <= 0) {
        trainDirection.value = 1;
        addEventLog("info", "列车到达起点站，开始出发");
      }
    }
    // 更新列车速度
    const randomSpeedChange = Math.random() * 10 - 5;
    trainSpeed.value = Math.max(
      0,
      Math.min(120, trainSpeed.value + randomSpeedChange)
    );
    // 更新环境数据
    temperature.value = Math.max(
      15,
      Math.min(35, temperature.value + (Math.random() - 0.5))
    );
    humidity.value = Math.max(
      30,
      Math.min(90, humidity.value + (Math.random() - 0.5) * 2)
    );
    gasConcentration.value = Math.max(
      0.1,
      Math.min(1.2, gasConcentration.value + (Math.random() - 0.5) * 0.1)
    );
    airQuality.value = Math.max(
      50,
      Math.min(200, airQuality.value + (Math.random() - 0.5) * 5)
    );
    // 更新沉降数据
    settlementData.value = settlementData.value.map((item) => {
      const newValue = Math.max(
        1,
        Math.min(8, item.value + (Math.random() - 0.5) * 0.2)
      );
      // 添加告警日志
      if (newValue > 5 && item.value <= 5) {
        addEventLog(
          "alert",
          `${item.name}区段沉降超过警戒值${newValue.toFixed(1)}mm`
        );
      }
      return { ...item, value: newValue };
    });
    // 更新图表
    updateTunnelChart();
    updateSettlementChart();
  }, 1000);
}
// 停止模拟
function stopSimulation() {
  if (simulationInterval.value !== null) {
    clearInterval(simulationInterval.value);
    simulationInterval.value = null;
  }
}
// 事件日志相关
const eventLogContainer = ref<HTMLElement | null>(null);
const maxLogCount = 5; // 最大显示日志数量

// 添加事件日志
function addEventLog(type: "alert" | "warning" | "info", message: string) {
  const now = new Date();
  const time = `${String(now.getHours()).padStart(2, "0")}:${String(
    now.getMinutes()
  ).padStart(2, "0")}:${String(now.getSeconds()).padStart(2, "0")}`;

  // 添加新日志到开头
  eventLogs.value.unshift({ type, message, time });

  // 保持最大显示数量
  if (eventLogs.value.length > maxLogCount) {
    eventLogs.value = eventLogs.value.slice(0, maxLogCount);
  }
}

// 全屏相关
const isFullscreen = ref(false);
const dashboardContainer = ref<HTMLElement | null>(null);

function toggleFullscreen() {
  if (!document.fullscreenElement) {
    dashboardContainer.value?.requestFullscreen();
    isFullscreen.value = true;
  } else {
    document.exitFullscreen();
    isFullscreen.value = false;
  }
}

// 监听全屏变化
onMounted(() => {
  document.addEventListener("fullscreenchange", () => {
    isFullscreen.value = !!document.fullscreenElement;
  });
});
// 窗口大小变化时重绘图表
function handleResize() {
  if (settlementChart) {
    settlementChart.resize();
  }
}
// 生命周期钩子
onMounted(() => {
  // 初始化时间
  updateDateTime();
  setInterval(updateDateTime, 1000);
  // 初始化图表
  initTunnelChart();
  initSettlementChart();
  // 初始化列车数据
  trainSpeed.value = 60;
  trainPosition.value = 0;
  // 监听窗口大小变化
  window.addEventListener("resize", handleResize);

  // 设置定时器，定期添加一些模拟日志
  setInterval(() => {
    const randomMessages = [
      { type: "info", message: "系统运行正常" },
      { type: "warning", message: "温度略有升高，请注意观察" },
      { type: "info", message: "完成例行检查" },
      { type: "alert", message: "检测到异常振动" },
    ];
    const randomIndex = Math.floor(Math.random() * randomMessages.length);
    const { type, message } = randomMessages[randomIndex];
    addEventLog(type as "alert" | "warning" | "info", message);
  }, 5000);
});
onUnmounted(() => {
  // 清除定时器
  if (simulationInterval.value !== null) {
    clearInterval(simulationInterval.value);
  }
  // 销毁图表
  if (settlementChart) {
    settlementChart.dispose();
  }
  // 移除事件监听
  window.removeEventListener("resize", handleResize);
});

// 数据响应式定义
const tunnelList = ref([])
const currentTunnel = ref(null)
const environmentData = ref(null)
const warningList = ref([])

// 获取隧道列表
const fetchTunnelList = async () => {
  try {
    const res = await tunnelApi.getTunnelList()
    tunnelList.value = res.data
  } catch (error) {
    console.error('获取隧道列表失败：', error)
    ElMessage.error('获取隧道列表失败')
  }
}

// 获取环境数据
const fetchEnvironmentData = async (tunnelId) => {
  try {
    const res = await tunnelApi.getLatestEnvironment(tunnelId)
    environmentData.value = res.data
  } catch (error) {
    console.error('获取环境数据失败：', error)
  }
}

// 获取预警信息
const fetchWarnings = async (tunnelId) => {
  try {
    const res = await tunnelApi.getWarnings(tunnelId)
    warningList.value = res.data
  } catch (error) {
    console.error('获取预警信息失败：', error)
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchTunnelList()
})

// 选择隧道时更新数据
const handleTunnelSelect = async (tunnel) => {
  currentTunnel.value = tunnel
  await Promise.all([
    fetchEnvironmentData(tunnel.id),
    fetchWarnings(tunnel.id)
  ])
}
</script>
<style scoped>
.bg-dark-blue {
  background-color: #1a1f3d;
}
.bg-dark-blue-light {
  background-color: #232a4e;
}
.bg-dark-blue-transparent {
  background-color: rgba(26, 31, 61, 0.9);
}
.text-orange {
  color: #ff7b00;
}
.bg-orange {
  background-color: #ff7b00;
}
.border-orange {
  border-color: #ff7b00;
}
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: #1a1f3d;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #3d5a89;
  border-radius: 3px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #4d6a99;
}
/* 日志轮播动画 */
.log-slide-enter-active,
.log-slide-leave-active {
  transition: all 0.5s ease;
}

.log-slide-enter-from {
  transform: translateY(100%);
  opacity: 0;
}

.log-slide-leave-to {
  transform: translateY(-100%);
  opacity: 0;
}

.log-slide-move {
  transition: transform 0.5s ease;
}

/* 导航菜单动画 */
.nav-item-enter-active,
.nav-item-leave-active {
  transition: all 0.3s ease;
}

.nav-item-enter-from,
.nav-item-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
