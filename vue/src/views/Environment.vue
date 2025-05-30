<template>
  <div class="p-6 bg-[#0D122A] min-h-screen text-white space-y-6">
    <!-- 状态卡片 -->
    <div class="grid grid-cols-4 gap-4 text-sm">
      <div
        class="bg-dark-blue-light border border-blue-800 rounded-lg shadow-xl p-4 flex items-center space-x-2"
      >
        <i class="fas fa-thermometer-half text-blue-400 text-lg"></i>
        <span
          >温度：<span class="text-green-400">{{ tempData[3] }}°C</span></span
        >
      </div>
      <div
        class="bg-dark-blue-light border border-blue-800 rounded-lg shadow-xl p-4 flex items-center space-x-2"
      >
        <i class="fas fa-tint text-green-400 text-lg"></i>
        <span
          >湿度：<span class="text-green-400">{{ humData[3] }}%</span></span
        >
      </div>
      <div
        class="bg-dark-blue-light border border-blue-800 rounded-lg shadow-xl p-4 flex items-center space-x-2"
      >
        <i class="fas fa-burn text-yellow-400 text-lg"></i>
        <span
          >瓦斯浓度：<span class="text-yellow-400"
            >{{ gasData[3] }}%</span
          ></span
        >
      </div>
      <div
        class="bg-dark-blue-light border border-blue-800 rounded-lg shadow-xl p-4 flex items-center space-x-2"
      >
        <i class="fas fa-smog text-blue-300 text-lg"></i>
        <span
          >空气质量：<span class="text-blue-300">{{ aqiData[3] }}</span></span
        >
      </div>
    </div>

    <!-- 折线图 + 仪表盘 -->
    <div class="grid grid-cols-3 gap-4">
      <div
        class="bg-dark-blue-light border border-blue-800 rounded-lg shadow-xl p-4"
      >
        <h2 class="text-lg font-bold mb-2">
          <i class="fas fa-thermometer-half mr-2 text-blue-400"></i>温度趋势
        </h2>
        <div id="tempChart" class="h-48"></div>
      </div>
      <div
        class="bg-dark-blue-light border border-blue-800 rounded-lg shadow-xl p-4"
      >
        <h2 class="text-lg font-bold mb-2">
          <i class="fas fa-tint mr-2 text-green-400"></i>湿度趋势
        </h2>
        <div id="humidityChart" class="h-48"></div>
      </div>
      <div
        class="bg-dark-blue-light border border-blue-800 rounded-lg shadow-xl p-4"
      >
        <h2 class="text-lg font-bold mb-2">
          <i class="fas fa-tachometer-alt text-red-400 mr-2"></i>瓦斯浓度仪表盘
        </h2>
        <div id="gasGauge" class="w-full h-[150px]"></div>
      </div>
    </div>

    <!-- 沉降速率 + 空气质量 -->
    <div class="grid grid-cols-2 gap-4">
      <div
        class="bg-dark-blue-light border border-blue-800 rounded-lg shadow-xl p-4"
      >
        <h2 class="text-lg font-bold mb-2">
          <i class="fas fa-arrow-down text-yellow-400 mr-2"></i
          >沉降速率变化（mm/h）
        </h2>
        <div id="settleChart" class="h-48"></div>
      </div>
      <div
        class="bg-dark-blue-light border border-blue-800 rounded-lg shadow-xl p-4"
      >
        <h2 class="text-lg font-bold mb-2">
          <i class="fas fa-smog text-blue-300 mr-2"></i>空气质量变化
        </h2>
        <div id="aqiChart" class="h-48"></div>
      </div>
    </div>

    <!-- 告警事件日志区域 -->
    <div class="mt-6 max-h-[300px] overflow-y-auto pr-1">
      <div class="grid grid-cols-3 gap-4">
        <div
          v-for="event in sortedEvents"
          :key="event.id"
          :class="[
            'rounded p-3 text-sm shadow flex justify-between items-center',
            event.type === 'warn'
              ? 'bg-red-900/50 border border-red-800 text-red-300'
              : event.type === 'info'
              ? 'bg-blue-900/30 border border-blue-800 text-blue-300'
              : 'bg-yellow-900/30 border border-yellow-600 text-yellow-300',
            event.highlight ? 'animate-highlight' : '',
          ]"
        >
          <div class="flex-1 truncate flex items-center">
            <i class="fas fa-exclamation-triangle text-yellow-300 mr-2"></i>
            {{ event.message }}
          </div>
          <div class="ml-4 text-xs whitespace-nowrap">{{ event.time }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from "vue";
import * as echarts from "echarts";

const times = ["10:00", "11:00", "12:00", "13:00"];
const tempData = ref([22.3, 23.5, 25.1, 23.7]);
const humData = ref([60.2, 63.0, 65.5, 68.8]);
const aqiData = ref([81, 79, 85, 84.4]);
const gasData = ref([0.15, 0.3, 0.1, 0.3]);
const settleData = ref([1.2, 1.5, 2.1, 3.2]);

const events = ref([
  {
    id: 1,
    type: "warn",
    message: "K5+300区段沉降超过警戒值6mm",
    time: "14:32:45",
    highlight: false,
  },
  {
    id: 2,
    type: "info",
    message: "隧道温度上升至28℃",
    time: "14:30:12",
    highlight: false,
  },
  {
    id: 3,
    type: "success",
    message: "系统自检完成",
    time: "14:00:00",
    highlight: false,
  },
]);

const sortedEvents = computed(() =>
  [...events.value].sort((a, b) => b.time.localeCompare(a.time))
);

function pushNewEvent() {
  const messages = [
    "PM2.5 超标，请立即处理",
    "沉降速率超过阈值",
    "瓦斯浓度持续升高",
    "通信模块掉线",
    "环境温度异常波动",
  ];
  const newEvent = {
    id: Date.now(),
    type: ["warn", "info", "success"][Math.floor(Math.random() * 3)],
    message: messages[Math.floor(Math.random() * messages.length)],
    time: new Date().toTimeString().split(" ")[0],
    highlight: true,
  };
  events.value.unshift(newEvent);
  setTimeout(() => {
    const evt = events.value.find((e) => e.id === newEvent.id);
    if (evt) evt.highlight = false;
  }, 5000);
}

function drawLine(id, data, color) {
  const chart = echarts.init(document.getElementById(id));
  chart.setOption({
    xAxis: { type: "category", data: times, axisLabel: { color: "#fff" } },
    yAxis: { type: "value", axisLabel: { color: "#fff" } },
    series: [
      {
        type: "line",
        data,
        smooth: true,
        symbol: "circle",
        symbolSize: 10,
        itemStyle: { color },
        lineStyle: { color },
      },
    ],
    grid: { left: "10%", right: "10%", top: "10%", bottom: "10%" },
  });
}

function drawGauge() {
  nextTick(() => {
    const el = document.getElementById("gasGauge");
    if (!el || el.clientHeight === 0) return;
    echarts.dispose(el);
    const gauge = echarts.init(el);
    gauge.setOption({
      series: [
        {
          type: "gauge",
          startAngle: 0,
          endAngle: 360,
          min: 0,
          max: 0.5,
          radius: "100%",
          axisLine: {
            lineStyle: {
              width: 20,
              color: [
                [0.2, "#10B981"],
                [0.3, "#FBBF24"],
                [1, "#EF4444"],
              ],
            },
          },
          pointer: { show: false },
          progress: {
            show: true,
            width: 20,
            itemStyle: {
              color: "#fff",
            },
          },
          axisTick: { show: false },
          splitLine: { show: false },
          axisLabel: { show: false },
          anchor: { show: false },
          detail: {
            valueAnimation: true,
            fontSize: 24,
            color: "#fff",
            offsetCenter: [0, "20%"],
            formatter: "{value}%",
          },
          title: {
            show: true,
            offsetCenter: [0, "45%"],
            color: "#aaa",
            fontSize: 12,
          },
          data: [{ value: parseFloat(gasData.value[3]), name: "瓦斯浓度" }],
        },
      ],
    });
  });
}

onMounted(() => {
  nextTick(() => {
    setInterval(() => {
      pushNewEvent();
      drawCharts();
    }, 10000);
    drawCharts();
  });
});

function drawCharts() {
  drawLine("tempChart", tempData.value, "#3B82F6");
  drawLine("humidityChart", humData.value, "#10B981");
  drawLine("aqiChart", aqiData.value, "#60A5FA");
  drawLine("settleChart", settleData.value, "#FBBF24");
  drawGauge();
}
</script>

<style scoped>
@keyframes highlight {
  0% {
    background-color: #facc15;
  }
  50% {
    background-color: #fde047;
  }
  100% {
    background-color: transparent;
  }
}
.animate-highlight {
  animation: highlight 1s ease-in-out 2;
}
</style>
