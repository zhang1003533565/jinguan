<template>
  <div class="h-[calc(100vh-64px)] lg:h-[calc(100vh-80px)] xl:h-[calc(100vh-96px)] overflow-y-auto bg-[#0D122A] text-white">
    <div class="p-4 lg:p-6 space-y-4 lg:space-y-6 pb-16">
      <!-- 隧道选择器 -->
      <div class="flex items-center space-x-4 mb-4">
        <span class="text-gray-300 text-sm lg:text-base">当前隧道：</span>
        <div class="relative w-36 lg:w-48">
          <el-select 
            v-model="selectedTunnel" 
            size="small"
            class="!w-full custom-select"
            @change="handleTunnelChange"
          >
            <el-option
              v-for="tunnel in tunnels"
              :key="tunnel.id"
              :label="tunnel.name"
              :value="tunnel.id"
            />
          </el-select>
        </div>
      </div>
      
      <!-- 状态卡片 -->
      <div class="space-y-4">
        <!-- 入口监测点 -->
        <div class="bg-dark-blue-light border border-blue-800 rounded-lg shadow-xl p-3 lg:p-4">
          <h3 class="text-base lg:text-lg font-bold mb-3 lg:mb-4 text-blue-400">
            <i class="fas fa-door-open mr-2"></i>入口监测点
          </h3>
          <div class="grid grid-cols-2 md:grid-cols-4 gap-2 lg:gap-4 text-xs lg:text-sm">
            <div class="bg-[#1a1f37] border border-blue-900 rounded-lg p-2 lg:p-4 flex items-center space-x-2">
              <i class="fas fa-thermometer-half text-blue-400 text-base lg:text-lg"></i>
              <span>温度：<span class="text-green-400">{{ entranceData.temperature || '--' }}°C</span></span>
            </div>
            <div class="bg-[#1a1f37] border border-blue-900 rounded-lg p-2 lg:p-4 flex items-center space-x-2">
              <i class="fas fa-tint text-green-400 text-base lg:text-lg"></i>
              <span>湿度：<span class="text-green-400">{{ entranceData.humidity || '--' }}%</span></span>
            </div>
            <div class="bg-[#1a1f37] border border-blue-900 rounded-lg p-2 lg:p-4 flex items-center space-x-2">
              <i class="fas fa-burn text-yellow-400 text-base lg:text-lg"></i>
              <span>瓦斯浓度：<span class="text-yellow-400">{{ formatGasValue(entranceData.co2) }}%</span></span>
            </div>
            <div class="bg-[#1a1f37] border border-blue-900 rounded-lg p-2 lg:p-4 flex items-center space-x-2">
              <i class="fas fa-smog text-blue-300 text-base lg:text-lg"></i>
              <span>空气质量：<span class="text-blue-300">{{ entranceData.pm25 || '--' }}</span></span>
            </div>
          </div>
        </div>

        <!-- 中段监测点 -->
        <div class="bg-dark-blue-light border border-blue-800 rounded-lg shadow-xl p-3 lg:p-4">
          <h3 class="text-base lg:text-lg font-bold mb-3 lg:mb-4 text-blue-400">
            <i class="fas fa-grip-lines-vertical mr-2"></i>中段监测点
          </h3>
          <div class="grid grid-cols-2 md:grid-cols-4 gap-2 lg:gap-4 text-xs lg:text-sm">
            <div class="bg-[#1a1f37] border border-blue-900 rounded-lg p-2 lg:p-4 flex items-center space-x-2">
              <i class="fas fa-thermometer-half text-blue-400 text-base lg:text-lg"></i>
              <span>温度：<span class="text-green-400">{{ middleData.temperature || '--' }}°C</span></span>
            </div>
            <div class="bg-[#1a1f37] border border-blue-900 rounded-lg p-2 lg:p-4 flex items-center space-x-2">
              <i class="fas fa-tint text-green-400 text-base lg:text-lg"></i>
              <span>湿度：<span class="text-green-400">{{ middleData.humidity || '--' }}%</span></span>
            </div>
            <div class="bg-[#1a1f37] border border-blue-900 rounded-lg p-2 lg:p-4 flex items-center space-x-2">
              <i class="fas fa-burn text-yellow-400 text-base lg:text-lg"></i>
              <span>瓦斯浓度：<span class="text-yellow-400">{{ formatGasValue(middleData.co2) }}%</span></span>
            </div>
            <div class="bg-[#1a1f37] border border-blue-900 rounded-lg p-2 lg:p-4 flex items-center space-x-2">
              <i class="fas fa-smog text-blue-300 text-base lg:text-lg"></i>
              <span>空气质量：<span class="text-blue-300">{{ middleData.pm25 || '--' }}</span></span>
            </div>
          </div>
        </div>

        <!-- 出口监测点 -->
        <div class="bg-dark-blue-light border border-blue-800 rounded-lg shadow-xl p-3 lg:p-4">
          <h3 class="text-base lg:text-lg font-bold mb-3 lg:mb-4 text-blue-400">
            <i class="fas fa-door-closed mr-2"></i>出口监测点
          </h3>
          <div class="grid grid-cols-2 md:grid-cols-4 gap-2 lg:gap-4 text-xs lg:text-sm">
            <div class="bg-[#1a1f37] border border-blue-900 rounded-lg p-2 lg:p-4 flex items-center space-x-2">
              <i class="fas fa-thermometer-half text-blue-400 text-base lg:text-lg"></i>
              <span>温度：<span class="text-green-400">{{ exitData.temperature || '--' }}°C</span></span>
            </div>
            <div class="bg-[#1a1f37] border border-blue-900 rounded-lg p-2 lg:p-4 flex items-center space-x-2">
              <i class="fas fa-tint text-green-400 text-base lg:text-lg"></i>
              <span>湿度：<span class="text-green-400">{{ exitData.humidity || '--' }}%</span></span>
            </div>
            <div class="bg-[#1a1f37] border border-blue-900 rounded-lg p-2 lg:p-4 flex items-center space-x-2">
              <i class="fas fa-burn text-yellow-400 text-base lg:text-lg"></i>
              <span>瓦斯浓度：<span class="text-yellow-400">{{ formatGasValue(exitData.co2) }}%</span></span>
            </div>
            <div class="bg-[#1a1f37] border border-blue-900 rounded-lg p-2 lg:p-4 flex items-center space-x-2">
              <i class="fas fa-smog text-blue-300 text-base lg:text-lg"></i>
              <span>空气质量：<span class="text-blue-300">{{ exitData.pm25 || '--' }}</span></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 折线图区域 -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div class="bg-dark-blue-light border border-blue-800 rounded-lg shadow-xl p-3 lg:p-4">
          <div class="flex flex-col lg:flex-row lg:justify-between lg:items-center mb-2">
            <h2 class="text-base lg:text-lg font-bold mb-2 lg:mb-0">
              <i class="fas fa-thermometer-half mr-2 text-blue-400"></i>温度趋势
            </h2>
            <div class="flex flex-wrap gap-1">
              <button
                v-for="location in locationOptions"
                :key="location"
                @click="handleLocationChange(location)"
                :class="[
                  'px-2 lg:px-3 py-1 rounded text-xs lg:text-sm whitespace-nowrap',
                  selectedLocation === location
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-700 text-gray-300 hover:bg-gray-600'
                ]"
              >
                {{ location }}
              </button>
            </div>
          </div>
          <div id="tempChart" class="h-32 lg:h-40"></div>
        </div>
        
        <div class="bg-dark-blue-light border border-blue-800 rounded-lg shadow-xl p-3 lg:p-4">
          <div class="flex flex-col lg:flex-row lg:justify-between lg:items-center mb-2">
            <h2 class="text-base lg:text-lg font-bold mb-2 lg:mb-0">
              <i class="fas fa-tint mr-2 text-green-400"></i>湿度趋势
            </h2>
            <div class="flex flex-wrap gap-1">
              <button
                v-for="location in locationOptions"
                :key="location"
                @click="handleLocationChange(location)"
                :class="[
                  'px-2 lg:px-3 py-1 rounded text-xs lg:text-sm whitespace-nowrap',
                  selectedLocation === location
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-700 text-gray-300 hover:bg-gray-600'
                ]"
              >
                {{ location }}
              </button>
            </div>
          </div>
          <div id="humidityChart" class="h-32 lg:h-40"></div>
        </div>

        <div class="bg-dark-blue-light border border-blue-800 rounded-lg shadow-xl p-3 lg:p-4">
          <div class="flex flex-col lg:flex-row lg:justify-between lg:items-center mb-4">
            <h2 class="text-base lg:text-lg font-bold mb-2 lg:mb-0">
              <i class="fas fa-burn text-yellow-400 mr-2"></i>瓦斯浓度趋势
            </h2>
            <div class="flex space-x-2">
              <button
                v-for="location in locationOptions"
                :key="location"
                @click="handleLocationChange(location)"
                :class="[
                  'px-2 lg:px-3 py-1 rounded text-xs lg:text-sm',
                  selectedLocation === location
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-700 text-gray-300 hover:bg-gray-600'
                ]"
              >
                {{ location }}
              </button>
            </div>
          </div>
          <div id="gasChart" class="h-40 lg:h-48"></div>
        </div>
      </div>

      <!-- 沉降速率 + 空气质量 -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div class="bg-dark-blue-light border border-blue-800 rounded-lg shadow-xl p-3 lg:p-4">
          <div class="flex flex-col lg:flex-row lg:justify-between lg:items-center mb-2">
            <h2 class="text-base lg:text-lg font-bold mb-2 lg:mb-0">
              <i class="fas fa-arrow-down text-yellow-400 mr-2"></i>沉降速率变化（mm/h）
            </h2>
            <div class="flex flex-wrap gap-1">
              <button
                v-for="location in locationOptions"
                :key="location"
                @click="handleLocationChange(location)"
                :class="[
                  'px-2 lg:px-3 py-1 rounded text-xs lg:text-sm whitespace-nowrap',
                  selectedLocation === location
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-700 text-gray-300 hover:bg-gray-600'
                ]"
              >
                {{ location }}
              </button>
            </div>
          </div>
          <div id="settleChart" class="h-32 lg:h-40"></div>
        </div>

        <div class="bg-dark-blue-light border border-blue-800 rounded-lg shadow-xl p-3 lg:p-4">
          <div class="flex flex-col lg:flex-row lg:justify-between lg:items-center mb-2">
            <h2 class="text-base lg:text-lg font-bold mb-2 lg:mb-0">
              <i class="fas fa-smog text-blue-300 mr-2"></i>空气质量变化
            </h2>
            <div class="flex flex-wrap gap-1">
              <button
                v-for="location in locationOptions"
                :key="location"
                @click="handleLocationChange(location)"
                :class="[
                  'px-2 lg:px-3 py-1 rounded text-xs lg:text-sm whitespace-nowrap',
                  selectedLocation === location
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-700 text-gray-300 hover:bg-gray-600'
                ]"
              >
                {{ location }}
              </button>
            </div>
          </div>
          <div id="aqiChart" class="h-32 lg:h-40"></div>
        </div>
      </div>

      <!-- 告警事件日志区域 -->
      <div class="mt-4 lg:mt-6 max-h-[180px] overflow-y-auto pr-1">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-2 lg:gap-4">
          <div
            v-for="event in sortedEvents"
            :key="event.id"
            :class="[
              'rounded p-2 lg:p-3 text-xs lg:text-sm shadow flex justify-between items-center',
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
            <div class="ml-2 lg:ml-4 text-[10px] lg:text-xs whitespace-nowrap">{{ event.time }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick, onUnmounted } from "vue";
import * as echarts from "echarts";
import environmentApi from "@/api/environment";
import { ElMessage, ElSelect, ElOption } from "element-plus";

// 隧道相关数据
const selectedTunnel = ref(null);
const tunnels = ref([]);

// 数据响应式定义
const loading = ref(false);
const times = ref([]);
const tempData = ref([]);
const humData = ref([]);
const aqiData = ref([]);
const gasData = ref([]);
const settleData = ref([]);
const events = ref([]);
const thresholds = ref(null);

// 图表实例存储
const charts = ref({});

// 添加三个监测点的数据对象
const entranceData = ref({});
const middleData = ref({});
const exitData = ref({});

// 添加当前选中的监测点
const selectedLocation = ref('入口');
const locationOptions = ['入口', '中段', '出口'];

// 获取隧道列表
const fetchTunnels = async () => {
  try {
    console.log('开始获取隧道列表...');
    const res = await environmentApi.getTunnels();
    console.log('获取隧道列表响应:', res);
    
    // 从分页数据中提取隧道列表
    tunnels.value = res.data.content || [];
    console.log('处理后的隧道列表数据:', tunnels.value);
    
    if (tunnels.value.length > 0) {
      console.log('存在隧道数据，选择第一个隧道:', tunnels.value[0]);
      selectedTunnel.value = tunnels.value[0].id;
      handleTunnelChange();
    } else {
      console.log('警告: 隧道列表为空');
      ElMessage.warning('没有可用的隧道数据');
    }
  } catch (error) {
    console.error('获取隧道列表失败详情:', {
      message: error.message,
      response: error.response?.data,
      status: error.response?.status,
      config: {
        url: error.config?.url,
        method: error.config?.method,
        headers: error.config?.headers
      }
    });
    ElMessage.error("获取隧道列表失败");
  }
};

// 获取最新环境数据
const fetchLatestData = async () => {
  if (!selectedTunnel.value) {
    console.log('未选择隧道，跳过获取最新数据');
    return;
  }
  
  try {
    console.log('开始获取最新环境数据，隧道ID:', selectedTunnel.value);
    loading.value = true;
    const res = await environmentApi.getLatestData(selectedTunnel.value);
    console.log('获取最新环境数据响应:', res);
    const dataList = res.data;
    
    if (!dataList || !Array.isArray(dataList)) {
      console.warn('获取到的数据为空或格式不正确');
      return;
    }
    
    // 根据location分类数据
    dataList.forEach(data => {
      if (data.location === '隧道入口') {
        entranceData.value = data;
      } else if (data.location === '隧道中段') {
        middleData.value = data;
      } else if (data.location === '隧道出口') {
        exitData.value = data;
      }
    });
    
    // 更新图表数据
    times.value.push(new Date().toLocaleTimeString());
    if (times.value.length > 4) {
      times.value.shift();
    }
    
    // 更新折线图数据数组
    tempData.value = [entranceData.value.temperature, middleData.value.temperature, exitData.value.temperature];
    humData.value = [entranceData.value.humidity, middleData.value.humidity, exitData.value.humidity];
    gasData.value = [
      entranceData.value.co2 / 10000,
      middleData.value.co2 / 10000,
      exitData.value.co2 / 10000
    ];
    aqiData.value = [entranceData.value.pm25, middleData.value.pm25, exitData.value.pm25];
    
    // 检查是否超过阈值
    if (thresholds.value) {
      [entranceData.value, middleData.value, exitData.value].forEach(data => {
        if (data) {
          checkAlarmThreshold(data);
        }
      });
    }
    
    // 更新图表
    nextTick(() => {
      updateCharts();
    });
    
  } catch (error) {
    console.error("获取最新环境数据失败：", error);
    ElMessage.error("获取最新环境数据失败");
  } finally {
    loading.value = false;
  }
};

// 获取历史环境数据
const fetchHistoryData = async () => {
  if (!selectedTunnel.value) {
    console.log('未选择隧道，跳过获取历史数据');
    return;
  }
  
  try {
    const end = new Date();
    const start = new Date(end.getTime() - 24 * 60 * 60 * 1000); // 获取24小时的数据，后面筛选最近10条
    
    const formatDate = (date) => {
      const pad = (num) => String(num).padStart(2, '0');
      return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
    };
    
    const params = {
      tunnelId: selectedTunnel.value,
      startTime: formatDate(start),
      endTime: formatDate(end)
    };
    
    console.log('开始获取历史环境数据:', params);
    
    const res = await environmentApi.getHistoryData(params);
    console.log('获取历史环境数据响应:', res);
    const allData = res.data || [];
    console.log('历史环境数据:', allData);
    
    if (allData.length === 0) {
      console.warn('警告: 历史数据为空');
      ElMessage.warning("暂无历史数据");
      return;
    }

    // 按位置分组数据
    const groupedData = {
      '入口': allData.filter(d => d.location === '隧道入口'),
      '中段': allData.filter(d => d.location === '隧道中段'),
      '出口': allData.filter(d => d.location === '隧道出口')
    };

    // 更新当前监测点的最新数据
    Object.entries(groupedData).forEach(([location, data]) => {
      if (data.length > 0) {
        const latest = data[data.length - 1];
        switch(location) {
          case '入口':
            entranceData.value = latest;
            break;
          case '中段':
            middleData.value = latest;
            break;
          case '出口':
            exitData.value = latest;
            break;
        }
      }
    });

    // 获取选中位置的最近10条数据
    const locationData = groupedData[selectedLocation.value] || [];
    const recentData = locationData.slice(-10);

    // 更新图表数据
    times.value = recentData.map(item => new Date(item.createdAt).toLocaleTimeString());
    tempData.value = recentData.map(item => item.temperature);
    humData.value = recentData.map(item => item.humidity);
    gasData.value = recentData.map(item => item.co2 / 10000);
    aqiData.value = recentData.map(item => item.pm25);
    
    console.log('处理后的历史数据:', {
      监测点: selectedLocation.value,
      时间: times.value,
      温度: tempData.value,
      湿度: humData.value,
      空气质量: aqiData.value,
      瓦斯浓度: gasData.value
    });
    
    // 更新图表
    nextTick(() => {
      updateCharts();
    });
    
  } catch (error) {
    console.error("获取历史环境数据失败：", error);
    ElMessage.error("获取历史环境数据失败");
  }
};

// 获取报警阈值
const fetchThresholds = async () => {
  try {
    console.log('开始获取报警阈值...');
    const res = await environmentApi.getAlarmThreshold();
    console.log('获取报警阈值响应:', res);
    thresholds.value = res.data;
    console.log('报警阈值数据:', thresholds.value);
  } catch (error) {
    console.error("获取报警阈值失败：", error);
    console.error('错误详情:', {
      message: error.message,
      response: error.response?.data,
      status: error.response?.status,
      config: error.config
    });
    ElMessage.error("获取报警阈值失败");
  }
};

// 检查环境数据是否超过阈值
const checkAlarmThreshold = async (data) => {
  try {
    console.log('开始检查报警阈值，当前数据:', data);
    const res = await environmentApi.checkAlarmThreshold({
      temperature: data.temperature,
      humidity: data.humidity,
      co2: data.co2,
      pm25: data.pm25
    });
    console.log('检查报警阈值响应:', res);
    
    if (Object.keys(res.data).length > 0) {
      console.log('发现报警信息:', res.data);
      // 有报警信息
      Object.entries(res.data).forEach(([key, value]) => {
        pushNewEvent({
          type: "warn",
          message: value,
          time: new Date().toLocaleTimeString()
        });
      });
    }
  } catch (error) {
    console.error("检查报警阈值失败：", error);
    console.error('错误详情:', {
      message: error.message,
      response: error.response?.data,
      status: error.response?.status,
      config: error.config
    });
  }
};

// 添加新事件
const pushNewEvent = (event) => {
  const newEvent = {
    id: Date.now(),
    ...event,
    highlight: true
  };
  events.value.unshift(newEvent);
  setTimeout(() => {
    const evt = events.value.find(e => e.id === newEvent.id);
    if (evt) evt.highlight = false;
  }, 5000);
};

// 初始化图表
const initCharts = () => {
  const chartIds = ['tempChart', 'humidityChart', 'aqiChart', 'settleChart', 'gasChart'];
  
  chartIds.forEach(id => {
    const el = document.getElementById(id);
    if (!el) return;
    
    // 如果已存在实例且未被销毁，则不重新创建
    if (charts.value[id] && !charts.value[id].isDisposed()) {
      return;
    }
    
    // 创建新实例
    charts.value[id] = echarts.init(el);
  });
};

// 更新图表数据
const updateCharts = () => {
  if (!charts.value) return;
  
  Object.entries(charts.value).forEach(([id, chart]) => {
    if (!chart || chart.isDisposed()) return;
    
    switch (id) {
      case 'tempChart':
        updateLineChart(chart, tempData.value, times.value, "#3B82F6", "温度");
        break;
      case 'humidityChart':
        updateLineChart(chart, humData.value, times.value, "#10B981", "湿度");
        break;
      case 'aqiChart':
        updateLineChart(chart, aqiData.value, times.value, "#60A5FA", "空气质量");
        break;
      case 'settleChart':
        updateLineChart(chart, settleData.value, times.value, "#FBBF24", "沉降速率");
        break;
      case 'gasChart':
        updateGasChart(chart);
        break;
    }
  });
};

// 更新折线图
function updateLineChart(chart, data, xData, color, name) {
  if (!chart) return;
  
  const option = {
    grid: { 
      left: "10%", 
      right: "10%", 
      top: "10%", 
      bottom: "10%",
      z: 0
    },
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        const data = params[0];
        return `${data.name}<br/>${name}：${data.value}`;
      }
    },
    xAxis: { 
      type: "category", 
      data: xData, 
      axisLabel: { color: "#fff" },
      z: 1
    },
    yAxis: { 
      type: "value", 
      axisLabel: { color: "#fff" },
      z: 1
    },
    series: [
      {
        name: name,
        type: "line",
        data: data,
        smooth: true,
        symbol: "circle",
        symbolSize: 8,
        itemStyle: { color },
        lineStyle: { color },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [{
              offset: 0,
              color: `${color}33`
            }, {
              offset: 1,
              color: `${color}00`
            }]
          }
        }
      }
    ]
  };
  
  chart.setOption(option, true);
}

// 格式化瓦斯浓度显示
const formatGasValue = (value) => {
  if (!value) return '--';
  return (value / 10000).toFixed(4);
};

// 更新瓦斯浓度图表
function updateGasChart(chart) {
  if (!chart) return;
  
  const option = {
    grid: {
      left: "10%",
      right: "10%",
      top: "15%",
      bottom: "10%",
      z: 0
    },
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        const data = params[0];
        return `时间：${data.name}<br/>瓦斯浓度：${formatGasValue(data.value * 10000)}%`;
      }
    },
    xAxis: {
      type: "category",
      data: times.value,
      axisLabel: { 
        color: "#fff",
        formatter: (value) => {
          return value.split(':').slice(0, 2).join(':');
        }
      },
      z: 1
    },
    yAxis: {
      type: "value",
      min: 0,
      max: 0.5,
      interval: 0.1,
      axisLabel: {
        color: "#fff",
        formatter: '{value}%'
      },
      splitLine: {
        show: true,
        lineStyle: {
          color: '#ffffff20'
        }
      },
      z: 1
    },
    visualMap: {
      show: false,
      pieces: [
        {
          gt: 0.3,
          lte: 0.5,
          color: '#EF4444'  // 红色区域 (危险)
        },
        {
          gt: 0.1,
          lte: 0.3,
          color: '#FBBF24'  // 黄色区域 (警告)
        },
        {
          gt: 0,
          lte: 0.1,
          color: '#10B981'  // 绿色区域 (安全)
        }
      ]
    },
    series: [
      {
        name: selectedLocation.value,
        type: "line",
        data: gasData.value,
        smooth: true,
        symbol: "circle",
        symbolSize: 8,
        markLine: {
          silent: true,
          symbol: ['none', 'none'],
          label: {
            formatter: '{b}',
            position: 'insideEndTop',
            color: '#fff',
            backgroundColor: '#475569',
            padding: [2, 4],
            borderRadius: 2,
          },
          data: [
            {
              name: '警戒线',
              yAxis: 0.1,
              lineStyle: {
                color: '#FBBF24',
                type: 'dashed',
                width: 2
              },
              label: {
                show: true,
                formatter: '警戒线 0.1%'
              }
            },
            {
              name: '危险线',
              yAxis: 0.3,
              lineStyle: {
                color: '#EF4444',
                type: 'dashed',
                width: 2
              },
              label: {
                show: true,
                formatter: '危险线 0.3%'
              }
            }
          ]
        },
        itemStyle: { color: "#FBBF24" },
        lineStyle: { color: "#FBBF24" },
        areaStyle: {
          opacity: 0.3,
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            {
              offset: 0,
              color: '#EF4444'  // 红色
            },
            {
              offset: 0.4,
              color: '#FBBF24'  // 黄色
            },
            {
              offset: 0.8,
              color: '#10B981'  // 绿色
            }
          ])
        }
      }
    ]
  };
  
  chart.setOption(option, true);
}

// 处理窗口大小变化
const handleResize = () => {
  Object.values(charts.value).forEach(chart => {
    if (chart && !chart.isDisposed()) {
      chart.resize();
    }
  });
};

// 组件挂载时初始化
onMounted(() => {
  initCharts();
  window.addEventListener('resize', handleResize);
  
  // 获取数据并更新图表
  fetchTunnels();
  Promise.all([
    fetchHistoryData(),
    fetchThresholds()
  ]).then(() => {
    updateCharts();
  });
  
  // 定时更新数据
  const updateInterval = setInterval(async () => {
    await fetchLatestData();
    updateCharts();
  }, 60000);
  
  // 清理定时器
  onUnmounted(() => {
    clearInterval(updateInterval);
    window.removeEventListener('resize', handleResize);
    // 销毁图表实例
    Object.values(charts.value).forEach(chart => {
      if (chart && !chart.isDisposed()) {
        chart.dispose();
      }
    });
    charts.value = {};
  });
});

const sortedEvents = computed(() =>
  [...events.value].sort((a, b) => b.time.localeCompare(a.time))
);

// 处理隧道切换
const handleTunnelChange = async () => {
  await fetchHistoryData();
};

// 监测点切换处理函数
const handleLocationChange = (location) => {
  selectedLocation.value = location;
  fetchHistoryData();
};
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

:deep(.custom-select) {
  .el-input {
    --el-select-input-focus-border-color: #3B82F6;
  }
  
  .el-input__wrapper {
    padding: 0 8px !important;
    background-color: transparent !important;
    box-shadow: none !important;
    height: 24px !important;
    min-height: 24px !important;
    border: none !important;
  }
  
  .el-input__inner {
    height: 24px !important;
    line-height: 24px !important;
    font-size: 12px !important;
    color: #fff !important;
    background: #1a1f37 !important;
    border: 1px solid #3B82F6 !important;
    border-radius: 4px !important;
    padding: 0 20px 0 8px !important;
  }

  .el-input__suffix {
    right: 5px !important;
  }
}

:deep(.el-select__popper) {
  background: #1a1f37 !important;
  border: 1px solid #3B82F6 !important;
  
  .el-select-dropdown__item {
    color: #fff !important;
    font-size: 12px !important;
    height: 24px !important;
    line-height: 24px !important;
    padding: 0 8px !important;
  }
  
  .el-select-dropdown__item.hover,
  .el-select-dropdown__item:hover {
    background-color: #3B82F6 !important;
  }
  
  .el-select-dropdown__item.selected {
    background-color: #2563eb !important;
    font-weight: normal !important;
  }
}

/* 修改滚动条样式 */
.overflow-y-auto {
  scrollbar-width: thin;
  scrollbar-color: #3B82F6 transparent;
}

.overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: transparent;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background-color: #3B82F6;
  border-radius: 3px;
}

/* 修改图表容器的高度 */
#tempChart,
#humidityChart,
#gasChart,
#settleChart,
#aqiChart {
  min-height: 120px;
}

@media (min-width: 1024px) {
  #tempChart,
  #humidityChart,
  #gasChart,
  #settleChart,
  #aqiChart {
    min-height: 160px;
  }
}
</style>
