<template>
  <view class="page">
    <!-- 顶部导航栏 -->
    <!-- <view class="nav-bar">
      <view class="nav-left cursor-pointer" @click="goBack">
        <uni-icons type="arrow-left" size="24" color="#333"></uni-icons>
      </view>
      <view class="nav-title">数据模拟</view>
      <view class="nav-right"></view>
    </view> -->

    <scroll-view scroll-y class="content-container" :style="{ height: scrollViewHeight + 'px' }">
      <!-- 时间显示区 -->
      <view class="time-card">
        <view class="time-display">{{ formattedTime }}</view>
        <view class="time-picker cursor-pointer" @click="showTimePicker">
          <uni-icons type="calendar" size="24" color="#3370ff"></uni-icons>
        </view>
      </view>

      <!-- 数据模拟区 -->
      <view class="simulation-params">
        <view class="param-title">模拟参数设置</view>
        
        <view class="param-item">
          <view class="param-label">列车型号</view>
          <view class="param-value">
            <picker @change="onTrainTypeChange" :value="trainTypeIndex" :range="trainTypes" class="picker cursor-pointer">
              <view class="picker-text">{{ trainTypes[trainTypeIndex] }}</view>
            </picker>
          </view>
        </view>

        <view class="param-item">
          <view class="param-label">列车负载 ({{ trainLoad }}吨)</view>
          <view class="param-value">
            <slider :value="trainLoad" :min="0" :max="200" :step="1" @change="onTrainLoadChange" show-value></slider>
          </view>
        </view>

        <view class="param-item">
          <view class="param-label">列车运行速度 ({{ trainSpeed }}km/h)</view>
          <view class="param-value">
            <slider :value="trainSpeed" :min="0" :max="350" :step="5" @change="onTrainSpeedChange" show-value></slider>
          </view>
        </view>

        <view class="param-item">
          <view class="param-label">土层含水量 ({{ soilWaterContent }}%)</view>
          <view class="param-value">
            <slider :value="soilWaterContent" :min="0" :max="100" :step="1" @change="onSoilWaterContentChange" show-value></slider>
          </view>
        </view>
      </view>

      <!-- 操作控制区 -->
      <view class="control-area">
        <button class="control-btn start-btn cursor-pointer" @click="startSimulation">开始模拟</button>
        <button class="control-btn pause-btn cursor-pointer" @click="pauseSimulation">暂停模拟</button>
        <button class="control-btn clear-btn cursor-pointer" @click="clearData">清空数据</button>
      </view>

      <!-- 结果分析区 -->
      <view class="result-area">
        <view class="result-title">监测结果分析</view>
        
        <view class="joint-cards">
          <view class="joint-card">
            <view class="joint-title">Joint 1 (拱顶)</view>
            <view class="joint-data">
              <view class="data-item">
                <view class="data-label">孔隙水压力值:</view>
                <view class="data-value">{{ joint1Data.pressure }} kPa</view>
              </view>
              <view class="data-item">
                <view class="data-label">渗漏路径:</view>
                <view class="data-value">{{ joint1Data.leakagePath }} mm</view>
              </view>
              <view class="data-item">
                <view class="data-label">土层密度:</view>
                <view class="data-value">{{ joint1Data.soilDensity }} g/cm³</view>
              </view>
            </view>
          </view>

          <view class="joint-card">
            <view class="joint-title">Joint 2 (拱腰)</view>
            <view class="joint-data">
              <view class="data-item">
                <view class="data-label">孔隙水压力值:</view>
                <view class="data-value">{{ joint2Data.pressure }} kPa</view>
              </view>
              <view class="data-item">
                <view class="data-label">渗漏路径:</view>
                <view class="data-value">{{ joint2Data.leakagePath }} mm</view>
              </view>
              <view class="data-item">
                <view class="data-label">土层密度:</view>
                <view class="data-value">{{ joint2Data.soilDensity }} g/cm³</view>
              </view>
            </view>
          </view>

          <view class="joint-card">
            <view class="joint-title">Joint 3 (拱底)</view>
            <view class="joint-data">
              <view class="data-item">
                <view class="data-label">孔隙水压力值:</view>
                <view class="data-value">{{ joint3Data.pressure }} kPa</view>
              </view>
              <view class="data-item">
                <view class="data-label">渗漏路径:</view>
                <view class="data-value">{{ joint3Data.leakagePath }} mm</view>
              </view>
              <view class="data-item">
                <view class="data-label">土层密度:</view>
                <view class="data-value">{{ joint3Data.soilDensity }} g/cm³</view>
              </view>
            </view>
          </view>
        </view>

        <view class="deformation-card">
          <view class="deformation-title">隧道变形幅度</view>
          <view class="deformation-value">{{ tunnelDeformation }} mm</view>
          <view class="deformation-status" :class="deformationStatusClass">{{ deformationStatus }}</view>
        </view>
      </view>

      <!-- 图表预览区 -->
      <view class="chart-area">
        <view class="chart-tabs">
          <view 
            class="chart-tab cursor-pointer" 
            :class="{ active: activeChartType === 'line' }" 
            @click="setChartType('line')"
          >折线图</view>
          <view 
            class="chart-tab cursor-pointer" 
            :class="{ active: activeChartType === 'bar' }" 
            @click="setChartType('bar')"
          >柱状图</view>
        </view>
        
        <view class="chart-container">
          <image class="chart-image" :src="chartImageUrl" mode="aspectFit"></image>
        </view>
      </view>

      <!-- 上传与导出区 -->
      <view class="export-area">
        <button class="export-btn primary-btn cursor-pointer" @click="exportData">导出模拟数据</button>
        <button class="export-btn secondary-btn cursor-pointer" @click="exportCSV">导出CSV</button>
      </view>
    </scroll-view>

    <tab-bar></tab-bar>
  </view>
</template>
<script lang="ts" setup>
import { ref, computed, onMounted, reactive } from 'vue';
import TabBar from '@/components/tab-bar/tab-bar.vue'

// 页面高度计算
const scrollViewHeight = ref(0);

onMounted(() => {
  uni.getSystemInfo({
    success: (res) => {
      // 移除导航栏高度的计算，直接使用窗口高度
      scrollViewHeight.value = res.windowHeight;
    }
  });
});

// 导航相关
// const goBack = () => {
//   uni.navigateBack();
// };

// 时间选择相关
const currentTime = ref(new Date());
const formattedTime = computed(() => {
  const year = currentTime.value.getFullYear();
  const month = String(currentTime.value.getMonth() + 1).padStart(2, '0');
  const day = String(currentTime.value.getDate()).padStart(2, '0');
  const hour = String(currentTime.value.getHours()).padStart(2, '0');
  const minute = String(currentTime.value.getMinutes()).padStart(2, '0');
  
  return `${year}年${month}月${day}日 ${hour}:${minute}分`;
});

const timePopup = ref();
const showTimePicker = () => {
  timePopup.value.open();
};

// 时间选择器数据
const years = Array.from({ length: 10 }, (_, i) => 2020 + i);
const months = Array.from({ length: 12 }, (_, i) => i + 1);
const days = Array.from({ length: 31 }, (_, i) => i + 1);
const hours = Array.from({ length: 24 }, (_, i) => i);
const minutes = Array.from({ length: 60 }, (_, i) => i);

const timePickerValue = ref([5, 4, 23, 10, 30]); // 默认 2025-05-24 10:30

const onTimePickerChange = (e: any) => {
  timePickerValue.value = e.detail.value;
};

const confirmTimePicker = () => {
  const [yearIndex, monthIndex, dayIndex, hourIndex, minuteIndex] = timePickerValue.value;
  const newDate = new Date(
    years[yearIndex],
    months[monthIndex] - 1,
    days[dayIndex],
    hours[hourIndex],
    minutes[minuteIndex]
  );
  currentTime.value = newDate;
  timePopup.value.close();
};

const cancelTimePicker = () => {
  timePopup.value.close();
};

// 模拟参数相关
const trainTypes = ['和谐号CRH380A', '复兴号CR400BF', '高铁动车组CRH2A', '货运列车HXD3C', '普通客车25T'];
const trainTypeIndex = ref(0);
const trainLoad = ref(80);
const trainSpeed = ref(200);
const soilWaterContent = ref(35);

const onTrainTypeChange = (e: any) => {
  trainTypeIndex.value = e.detail.value;
};

const onTrainLoadChange = (e: any) => {
  trainLoad.value = e.detail.value;
};

const onTrainSpeedChange = (e: any) => {
  trainSpeed.value = e.detail.value;
};

const onSoilWaterContentChange = (e: any) => {
  soilWaterContent.value = e.detail.value;
};

// 模拟控制相关
const isSimulating = ref(false);
const simulationTimer = ref<number | null>(null);

const startSimulation = () => {
  if (isSimulating.value) return;
  isSimulating.value = true;
  
  // 模拟数据更新
  simulationTimer.value = setInterval(() => {
    updateSimulationData();
  }, 2000) as unknown as number;
};

const pauseSimulation = () => {
  if (!isSimulating.value) return;
  
  if (simulationTimer.value !== null) {
    clearInterval(simulationTimer.value);
    simulationTimer.value = null;
  }
  isSimulating.value = false;
};

const clearData = () => {
  // 重置所有数据
  if (simulationTimer.value !== null) {
    clearInterval(simulationTimer.value);
    simulationTimer.value = null;
  }
  isSimulating.value = false;
  
  // 重置监测点数据
  joint1Data.pressure = 0;
  joint1Data.leakagePath = 0;
  joint1Data.soilDensity = 1.5;
  
  joint2Data.pressure = 0;
  joint2Data.leakagePath = 0;
  joint2Data.soilDensity = 1.5;
  
  joint3Data.pressure = 0;
  joint3Data.leakagePath = 0;
  joint3Data.soilDensity = 1.5;
  
  tunnelDeformation.value = 0;
  
  // 清空历史数据
  simulationHistory.value = [];
};

// 监测点数据
const joint1Data = reactive({
  pressure: 0,
  leakagePath: 0,
  soilDensity: 1.5
});

const joint2Data = reactive({
  pressure: 0,
  leakagePath: 0,
  soilDensity: 1.5
});

const joint3Data = reactive({
  pressure: 0,
  leakagePath: 0,
  soilDensity: 1.5
});

const tunnelDeformation = ref(0);

const deformationStatus = computed(() => {
  if (tunnelDeformation.value < 5) {
    return '安全';
  } else if (tunnelDeformation.value < 15) {
    return '注意';
  } else {
    return '警告';
  }
});

const deformationStatusClass = computed(() => {
  if (tunnelDeformation.value < 5) {
    return 'status-safe';
  } else if (tunnelDeformation.value < 15) {
    return 'status-warning';
  } else {
    return 'status-danger';
  }
});

// 模拟数据更新
const simulationHistory = ref<any[]>([]);

const updateSimulationData = () => {
  // 基于输入参数生成模拟数据
  const loadFactor = trainLoad.value / 100;
  const speedFactor = trainSpeed.value / 200;
  const waterFactor = soilWaterContent.value / 50;
  
  // 随机波动
  const randomFactor = () => 0.8 + Math.random() * 0.4;
  
  // 更新监测点数据
  joint1Data.pressure = +(10 * loadFactor * waterFactor * randomFactor()).toFixed(2);
  joint1Data.leakagePath = +(5 * waterFactor * randomFactor()).toFixed(2);
  joint1Data.soilDensity = +(1.5 + 0.5 * waterFactor * randomFactor()).toFixed(2);
  
  joint2Data.pressure = +(15 * loadFactor * waterFactor * randomFactor()).toFixed(2);
  joint2Data.leakagePath = +(8 * waterFactor * randomFactor()).toFixed(2);
  joint2Data.soilDensity = +(1.6 + 0.4 * waterFactor * randomFactor()).toFixed(2);
  
  joint3Data.pressure = +(20 * loadFactor * waterFactor * randomFactor()).toFixed(2);
  joint3Data.leakagePath = +(10 * waterFactor * randomFactor()).toFixed(2);
  joint3Data.soilDensity = +(1.7 + 0.3 * waterFactor * randomFactor()).toFixed(2);
  
  // 计算隧道变形
  tunnelDeformation.value = +(
    (joint1Data.pressure + joint2Data.pressure + joint3Data.pressure) / 3 * 
    speedFactor * 
    waterFactor * 
    randomFactor()
  ).toFixed(2);
  
  // 记录历史数据
  simulationHistory.value.push({
    timestamp: new Date().toISOString(),
    joint1: { ...joint1Data },
    joint2: { ...joint2Data },
    joint3: { ...joint3Data },
    deformation: tunnelDeformation.value
  });
  
  // 保持历史记录不超过20条
  if (simulationHistory.value.length > 20) {
    simulationHistory.value.shift();
  }
};

// 图表相关
const activeChartType = ref('line');
const setChartType = (type: string) => {
  activeChartType.value = type;
};

// 模拟的图表图片URL
const chartImageUrl = computed(() => {
  if (activeChartType.value === 'line') {
    return 'https://readdy.ai/api/search-image?query=A%20professional%20line%20chart%20visualization%20showing%20tunnel%20deformation%20data%20over%20time%2C%20with%20three%20colored%20lines%20representing%20different%20measurement%20points%2C%20clear%20grid%20lines%2C%20labeled%20axes%2C%20data%20points%20marked%20with%20small%20circles%2C%20subtle%20gradient%20background%2C%20clean%20modern%20design%20style%2C%20no%20text%20labels%2C%20high%20quality%20data%20visualization&width=700&height=400&seq=1&orientation=landscape';
  } else {
    return 'https://readdy.ai/api/search-image?query=A%20professional%20bar%20chart%20visualization%20showing%20tunnel%20monitoring%20data%20comparison%20between%20three%20measurement%20points%2C%20with%20grouped%20colored%20bars%2C%20clear%20grid%20lines%2C%20labeled%20axes%2C%20subtle%20gradient%20background%2C%20clean%20modern%20design%20style%2C%20no%20text%20labels%2C%20high%20quality%20data%20visualization&width=700&height=400&seq=2&orientation=landscape';
  }
});

// 导出功能
const exportData = () => {
  uni.showToast({
    title: '模拟数据导出成功',
    icon: 'success'
  });
};

const exportCSV = () => {
  uni.showToast({
    title: 'CSV文件导出成功',
    icon: 'success'
  });
};
</script>
<style>
page {
  height: 100%;
}

.page {
  height: 100%;
  background-color: #f5f7fa;
  display: flex;
  flex-direction: column;
}

.cursor-pointer {
  cursor: pointer;
}

/* 导航栏 */
/* .nav-bar {
  height: 90rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30rpx;
  background-color: #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  position: relative;
  z-index: 10;
}

.nav-left, .nav-right {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-title {
  font-size: 18px;
  font-weight: 600;
  color: #333333;
} */

/* 内容区域 */
.content-container {
  flex: 1;
  box-sizing: border-box;
  padding: 20rpx;
  overflow-y: auto;
}

/* 时间显示区 */
.time-card {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.time-display {
  font-size: 16px;
  font-weight: 500;
  color: #333333;
}

.time-picker {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 数据模拟区 */
.simulation-params {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.param-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 20rpx;
}

.param-item {
  margin-bottom: 30rpx;
}

.param-label {
  font-size: 28rpx;
  color: #666666;
  margin-bottom: 10rpx;
}

.param-value {
  width: 100%;
}

.picker {
  height: 70rpx;
  border: 1px solid #e0e0e0;
  border-radius: 8rpx;
  padding: 0 20rpx;
  display: flex;
  align-items: center;
  background-color: #f9f9f9;
}

.picker-text {
  font-size: 28rpx;
  color: #333333;
}

/* 操作控制区 */
.control-area {
  display: flex;
  justify-content: space-between;
  margin-bottom: 30rpx;
}

.control-btn {
  flex: 1;
  margin: 0 10rpx;
  height: 80rpx;
  line-height: 80rpx;
  border-radius: 8rpx;
  font-size: 14px;
  font-weight: 500;
}

.start-btn {
  background-color: #3370ff;
  color: #ffffff;
}

.pause-btn {
  background-color: #f5f7fa;
  color: #666666;
  border: 1px solid #e0e0e0;
}

.clear-btn {
  background-color: #ff6b6b;
  color: #ffffff;
}

/* 结果分析区 */
.result-area {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.result-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 20rpx;
}

.joint-cards {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  margin-bottom: 30rpx;
}

.joint-card {
  background-color: #f9f9f9;
  border-radius: 12rpx;
  padding: 20rpx;
}

.joint-title {
  font-size: 15px;
  font-weight: 500;
  color: #333333;
  margin-bottom: 16rpx;
}

.joint-data {
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}

.data-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10rpx 0;
}

.data-label {
  font-size: 26rpx;
  color: #666666;
}

.data-value {
  font-size: 26rpx;
  font-weight: 500;
  color: #333333;
}

.deformation-card {
  background-color: #f0f5ff;
  border-radius: 12rpx;
  padding: 20rpx;
  text-align: center;
  margin-top: 15rpx;
}

.deformation-title {
  font-size: 15px;
  font-weight: 500;
  color: #333333;
  margin-bottom: 16rpx;
}

.deformation-value {
  font-size: 48rpx;
  font-weight: 600;
  color: #3370ff;
  margin: 10rpx 0;
}

.deformation-status {
  font-size: 14px;
  font-weight: 500;
  padding: 4rpx 16rpx;
  border-radius: 20rpx;
}

.status-safe {
  background-color: #e6f7ee;
  color: #00b578;
}

.status-warning {
  background-color: #fff8e6;
  color: #ff9a2e;
}

.status-danger {
  background-color: #ffecec;
  color: #ff4d4f;
}

/* 图表预览区 */
.chart-area {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.chart-tabs {
  display: flex;
  border-bottom: 1px solid #e0e0e0;
  margin-bottom: 15rpx;
}

.chart-tab {
  padding: 15rpx 30rpx;
  font-size: 28rpx;
  color: #666666;
  position: relative;
}

.chart-tab.active {
  color: #3370ff;
  font-weight: 500;
}

.chart-tab.active::after {
  content: '';
  position: absolute;
  bottom: -2rpx;
  left: 0;
  width: 100%;
  height: 4rpx;
  background-color: #3370ff;
}

.chart-container {
  height: 400rpx;
  display: flex;
  justify-content: center;
  align-items: center;
}

.chart-image {
  width: 100%;
  height: 100%;
  border-radius: 8rpx;
}

/* 上传与导出区 */
.export-area {
  display: flex;
  justify-content: space-between;
  margin-bottom: 60rpx;
}

.export-btn {
  flex: 1;
  margin: 0 10rpx;
  height: 80rpx;
  line-height: 80rpx;
  border-radius: 8rpx;
  font-size: 14px;
  font-weight: 500;
}

.primary-btn {
  background-color: #3370ff;
  color: #ffffff;
}

.secondary-btn {
  background-color: #f5f7fa;
  color: #666666;
  border: 1px solid #e0e0e0;
}
</style>
