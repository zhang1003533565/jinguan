<template>
<view class="monitoring-page">
<!-- 顶部导航栏 -->
<view class="nav-bar">
<view class="nav-left">
<uni-icons type="back" size="24" color="#ffffff" @click="goBack" class="cursor-pointer"></uni-icons>
</view>
<view class="nav-title">数据展示与监控</view>
<view class="nav-right"></view>
</view>
<!-- 内容区域 -->
<scroll-view scroll-y class="content-container" @refresherrefresh="onRefresh" refresher-enabled :refresher-triggered="isRefreshing">
<!-- 概览卡片区 -->
<scroll-view scroll-x class="overview-cards" show-scrollbar="false">
<view class="card strain-card cursor-pointer" @click="selectDataType('strain')">
<view class="card-content">
<view class="card-title">应变最大值</view>
<view class="card-value">{{ overviewData.strain.value }}</view>
<view class="card-unit">{{ overviewData.strain.unit }}</view>
</view>
<view class="card-trend">
<view class="trend-icon" :class="{'trend-up': overviewData.strain.trend > 0, 'trend-down': overviewData.strain.trend < 0}">
<uni-icons :type="overviewData.strain.trend > 0 ? 'arrow-up' : 'arrow-down'" size="16" color="#ffffff"></uni-icons>
</view>
</view>
</view>
<view class="card leakage-card cursor-pointer" @click="selectDataType('leakage')">
<view class="card-content">
<view class="card-title">渗漏速率</view>
<view class="card-value">{{ overviewData.leakage.value }}</view>
<view class="card-unit">{{ overviewData.leakage.unit }}</view>
</view>
<view class="card-status" :class="{'status-normal': overviewData.leakage.status === 'normal', 'status-warning': overviewData.leakage.status === 'warning', 'status-danger': overviewData.leakage.status === 'danger'}">
<uni-icons type="circle-filled" size="16" color="#ffffff"></uni-icons>
</view>
</view>
<view class="card vibration-card cursor-pointer" @click="selectDataType('vibration')">
<view class="card-content">
<view class="card-title">振动峰值</view>
<view class="card-value">{{ overviewData.vibration.value }}</view>
<view class="card-unit">{{ overviewData.vibration.unit }}</view>
</view>
<view class="card-wave">
<uni-icons type="sound-filled" size="16" color="#ffffff"></uni-icons>
</view>
</view>
</scroll-view>
<!-- 实时图表区 -->
<view class="chart-container">
<view class="chart-header">
<view class="chart-tabs">
<view
v-for="(tab, index) in chartTabs"
:key="index"
class="chart-tab cursor-pointer"
:class="{'active': currentDataType === tab.type}"
@click="selectDataType(tab.type)"
>
{{ tab.name }}
</view>
</view>
<view class="chart-settings cursor-pointer" @click="toggleChartSettings">
<uni-icons type="gear" size="20" color="#ffffff"></uni-icons>
</view>
</view>
<view class="chart-body">
<view class="chart-placeholder">
<image class="chart-image" :src="getChartImage()" mode="aspectFit"></image>
</view>
</view>
<view class="chart-legend">
<view
v-for="(sensor, index) in sensorData"
:key="index"
class="legend-item cursor-pointer"
:class="{'active': sensor.active}"
@click="toggleSensor(index)"
>
<view class="legend-color" :style="{ backgroundColor: sensor.color }"></view>
<view class="legend-name">{{ sensor.name }}</view>
</view>
</view>
</view>
<!-- 历史数据查询与回放 -->
<view class="history-control">
<view class="date-picker cursor-pointer" @click="showDatePicker">
<uni-icons type="calendar" size="20" color="#ffffff"></uni-icons>
<text class="date-text">{{ formatDateRange(dateRange) }}</text>
</view>
<view class="query-button cursor-pointer" @click="queryHistoryData">
<uni-icons type="search" size="20" color="#ffffff"></uni-icons>
<text>查询</text>
</view>
<view class="playback-controls">
<view class="playback-button cursor-pointer" @click="togglePlayback">
<uni-icons :type="isPlaying ? 'pause' : 'play'" size="20" color="#ffffff"></uni-icons>
</view>
<view class="speed-control cursor-pointer" @click="cyclePlaybackSpeed">
<text>{{ playbackSpeedText }}</text>
</view>
</view>
</view>
<!-- 异常告警信息区 -->
<view class="alert-container">
<view class="section-title">
<text>异常告警信息</text>
<view class="title-badge">{{ alertData.length }}</view>
</view>
<view class="alert-list">
<view
v-for="(alert, index) in alertData"
:key="index"
class="alert-item cursor-pointer"
:class="{'alert-critical': alert.level === 'critical', 'alert-warning': alert.level === 'warning'}"
@click="viewAlertDetail(alert)"
>
<view class="alert-sensor">
<text class="sensor-id">{{ alert.sensorId }}</text>
</view>
<view class="alert-info">
<text class="alert-type">{{ alert.type }}</text>
<text class="alert-value">{{ alert.value }} {{ alert.unit }}</text>
</view>
<view class="alert-time">
<text class="time-text">{{ formatAlertTime(alert.time) }}</text>
<view class="alert-status" :class="{'status-new': alert.status === 'new', 'status-processing': alert.status === 'processing', 'status-resolved': alert.status === 'resolved'}">
{{ alert.statusText }}
</view>
</view>
</view>
</view>
</view>
<!-- 三维/分布图 -->
<view class="map-container">
<view class="section-title">
<text>传感器分布图</text>
<view class="map-toggle cursor-pointer" @click="toggleMapType">
<text>{{ currentMapType === '2d' ? '切换3D' : '切换2D' }}</text>
</view>
</view>
<view class="map-view">
<image class="map-image" :src="getMapImage()" mode="aspectFit"></image>
<view class="map-controls">
<view class="zoom-controls">
<view class="zoom-button zoom-in cursor-pointer" @click="zoomMap('in')">
<uni-icons type="plus" size="20" color="#ffffff"></uni-icons>
</view>
<view class="zoom-button zoom-out cursor-pointer" @click="zoomMap('out')">
<uni-icons type="minus" size="20" color="#ffffff"></uni-icons>
</view>
</view>
<view class="layer-controls cursor-pointer" @click="toggleLayerPanel">
<uni-icons type="layers" size="20" color="#ffffff"></uni-icons>
</view>
</view>
</view>
</view>
</scroll-view>
</view>
</template>
<script lang="ts" setup>
import { ref, reactive, computed, onMounted } from 'vue';
// 页面状态
const isRefreshing = ref(false);
const currentDataType = ref('strain');
const isPlaying = ref(false);
const playbackSpeed = ref(1);
const currentMapType = ref('2d');
// 日期范围
const dateRange = reactive({
start: new Date(new Date().getTime() - 7 * 24 * 60 * 60 * 1000), // 一周前
end: new Date()
});
// 概览数据
const overviewData = reactive({
strain: {
value: '256.8',
unit: 'μm/m',
trend: 1
},
leakage: {
value: '0.42',
unit: 'L/min',
status: 'normal'
},
vibration: {
value: '3.76',
unit: 'g',
trend: -1
}
});
// 图表标签
const chartTabs = [
{ name: '应变', type: 'strain' },
{ name: '渗漏', type: 'leakage' },
{ name: '振动', type: 'vibration' }
];
// 传感器数据
const sensorData = reactive([
{ name: '传感器A-01', color: '#4ecdc4', active: true },
{ name: '传感器A-02', color: '#ff6b6b', active: true },
{ name: '传感器A-03', color: '#ffe66d', active: false },
{ name: '传感器B-01', color: '#1a535c', active: true },
{ name: '传感器B-02', color: '#f7b801', active: false }
]);
// 告警数据
const alertData = reactive([
{
sensorId: 'A-02',
type: '应变异常',
value: '358.6',
unit: 'μm/m',
time: new Date(new Date().getTime() - 15 * 60 * 1000),
level: 'critical',
status: 'new',
statusText: '未处理'
},
{
sensorId: 'B-01',
type: '渗漏超标',
value: '0.87',
unit: 'L/min',
time: new Date(new Date().getTime() - 2 * 60 * 60 * 1000),
level: 'warning',
status: 'processing',
statusText: '处理中'
},
{
sensorId: 'A-03',
type: '振动异常',
value: '5.12',
unit: 'g',
time: new Date(new Date().getTime() - 6 * 60 * 60 * 1000),
level: 'warning',
status: 'resolved',
statusText: '已解决'
}
]);
// 计算属性
const playbackSpeedText = computed(() => {
return `${playbackSpeed.value}x`;
});
// 方法
const goBack = () => {
uni.navigateBack();
};
const onRefresh = () => {
isRefreshing.value = true;
setTimeout(() => {
// 模拟数据刷新
overviewData.strain.value = (Math.random() * 100 + 200).toFixed(1);
overviewData.leakage.value = (Math.random() * 0.5 + 0.1).toFixed(2);
overviewData.vibration.value = (Math.random() * 5 + 1).toFixed(2);
isRefreshing.value = false;
}, 1000);
};
const selectDataType = (type: string) => {
currentDataType.value = type;
};
const toggleChartSettings = () => {
uni.showActionSheet({
itemList: ['刷新频率设置', '显示范围调整', '导出数据'],
success: (res) => {
uni.showToast({
title: '功能开发中',
icon: 'none'
});
}
});
};
const toggleSensor = (index: number) => {
sensorData[index].active = !sensorData[index].active;
};
const showDatePicker = () => {
uni.showToast({
title: '日期选择器开发中',
icon: 'none'
});
};
const queryHistoryData = () => {
uni.showLoading({
title: '查询中...'
});
setTimeout(() => {
uni.hideLoading();
uni.showToast({
title: '查询完成',
icon: 'success'
});
}, 1500);
};
const togglePlayback = () => {
isPlaying.value = !isPlaying.value;
if (isPlaying.value) {
uni.showToast({
title: `开始回放 (${playbackSpeedText.value})`,
icon: 'none'
});
} else {
uni.showToast({
title: '暂停回放',
icon: 'none'
});
}
};
const cyclePlaybackSpeed = () => {
const speeds = [1, 2, 4, 8];
const currentIndex = speeds.indexOf(playbackSpeed.value);
const nextIndex = (currentIndex + 1) % speeds.length;
playbackSpeed.value = speeds[nextIndex];
};
const viewAlertDetail = (alert: any) => {
uni.showModal({
title: `${alert.sensorId} ${alert.type}`,
content: `传感器值: ${alert.value}${alert.unit}\n时间: ${formatAlertTime(alert.time)}\n状态: ${alert.statusText}`,
showCancel: false
});
};
const toggleMapType = () => {
currentMapType.value = currentMapType.value === '2d' ? '3d' : '2d';
};
const zoomMap = (type: 'in' | 'out') => {
uni.showToast({
title: type === 'in' ? '放大地图' : '缩小地图',
icon: 'none'
});
};
const toggleLayerPanel = () => {
uni.showActionSheet({
itemList: ['全部传感器', '应变传感器', '渗漏传感器', '振动传感器'],
success: (res) => {
uni.showToast({
title: '切换图层: ' + ['全部传感器', '应变传感器', '渗漏传感器', '振动传感器'][res.tapIndex],
icon: 'none'
});
}
});
};
// 辅助函数
const formatDateRange = (range: { start: Date, end: Date }) => {
const formatDate = (date: Date) => {
return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
};
return `${formatDate(range.start)} 至 ${formatDate(range.end)}`;
};
const formatAlertTime = (time: Date) => {
const now = new Date();
const diff = now.getTime() - time.getTime();
// 小于1小时显示分钟
if (diff < 60 * 60 * 1000) {
const minutes = Math.floor(diff / (60 * 1000));
return `${minutes}分钟前`;
}
// 小于24小时显示小时
if (diff < 24 * 60 * 60 * 1000) {
const hours = Math.floor(diff / (60 * 60 * 1000));
return `${hours}小时前`;
}
// 其他情况显示日期
return `${time.getMonth() + 1}月${time.getDate()}日 ${time.getHours().toString().padStart(2, '0')}:${time.getMinutes().toString().padStart(2, '0')}`;
};
// 图表和地图图片
const getChartImage = () => {
if (currentDataType.value === 'strain') {
return 'https://readdy.ai/api/search-image?query=Professional%20data%20visualization%20chart%20showing%20strain%20measurement%20over%20time%2C%20blue%20line%20graph%20with%20grid%2C%20time%20on%20x-axis%2C%20strain%20values%20on%20y-axis%2C%20dark%20background%20with%20light%20grid%20lines%2C%20clean%20modern%20UI%20design%2C%20high%20detail%20quality%2C%20technical%20dashboard%20style%2C%20with%20multiple%20sensor%20data%20lines%20in%20different%20colors%2C%20engineering%20monitoring%20interface&width=600&height=300&seq=chart1&orientation=landscape';
} else if (currentDataType.value === 'leakage') {
return 'https://readdy.ai/api/search-image?query=Professional%20data%20visualization%20chart%20showing%20leakage%20rate%20over%20time%2C%20red%20line%20graph%20with%20grid%2C%20time%20on%20x-axis%2C%20leakage%20values%20on%20y-axis%2C%20dark%20background%20with%20light%20grid%20lines%2C%20clean%20modern%20UI%20design%2C%20high%20detail%20quality%2C%20technical%20dashboard%20style%2C%20with%20multiple%20sensor%20data%20lines%20in%20different%20colors%2C%20engineering%20monitoring%20interface&width=600&height=300&seq=chart2&orientation=landscape';
} else {
return 'https://readdy.ai/api/search-image?query=Professional%20data%20visualization%20chart%20showing%20vibration%20amplitude%20over%20time%2C%20yellow%20line%20graph%20with%20grid%2C%20time%20on%20x-axis%2C%20vibration%20values%20on%20y-axis%2C%20dark%20background%20with%20light%20grid%20lines%2C%20clean%20modern%20UI%20design%2C%20high%20detail%20quality%2C%20technical%20dashboard%20style%2C%20with%20multiple%20sensor%20data%20lines%20in%20different%20colors%2C%20engineering%20monitoring%20interface&width=600&height=300&seq=chart3&orientation=landscape';
}
};
const getMapImage = () => {
if (currentMapType.value === '2d') {
return 'https://readdy.ai/api/search-image?query=2D%20technical%20blueprint%20or%20floor%20plan%20showing%20sensor%20distribution%20in%20a%20building%20or%20structure%2C%20with%20colored%20dots%20representing%20different%20sensor%20types%2C%20grid%20overlay%2C%20dark%20blue%20background%20with%20white%20and%20light%20blue%20lines%2C%20high%20detail%20engineering%20schematic%20style%2C%20top-down%20view%2C%20professional%20monitoring%20system%20interface&width=600&height=400&seq=map1&orientation=landscape';
} else {
return 'https://readdy.ai/api/search-image?query=3D%20rendering%20of%20a%20building%20or%20structure%20with%20sensor%20placement%20visualization%2C%20isometric%20view%2C%20colored%20dots%20or%20icons%20showing%20different%20sensor%20types%2C%20semi-transparent%20walls%2C%20grid%20overlay%2C%20dark%20blue%20background%20with%20highlights%2C%20professional%20engineering%20visualization%2C%20monitoring%20system%20interface%2C%20high%20detail%20technical%20illustration&width=600&height=400&seq=map2&orientation=landscape';
}
};
onMounted(() => {
// 页面加载完成后的初始化操作
uni.showToast({
title: '数据加载完成',
icon: 'success'
});
});
</script>
<style>
page {
height: 100%;
}
.cursor-pointer {
cursor: pointer;
}
.monitoring-page {
height: 100%;
display: flex;
flex-direction: column;
background-color: #ffffff;
color: #333333;
}
/* 导航栏 */
.nav-bar {
height: 88rpx;
display: flex;
align-items: center;
justify-content: space-between;
padding: 0 30rpx;
background-color: #2196f3;
z-index: 100;
flex-shrink: 0;
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
color: #ffffff;
}
/* 内容区域 */
.content-container {
flex: 1;
overflow: auto;
padding: 20rpx;
}
/* 概览卡片区 */
.overview-cards {
display: flex;
white-space: nowrap;
margin-bottom: 30rpx;
}
.card {
display: inline-flex;
justify-content: space-between;
align-items: center;
width: 300rpx;
height: 160rpx;
margin-right: 20rpx;
padding: 20rpx;
border-radius: 16rpx;
box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.strain-card {
background: linear-gradient(135deg, #0288d1, #01579b);
}
.leakage-card {
background: linear-gradient(135deg, #d32f2f, #b71c1c);
}
.vibration-card {
background: linear-gradient(135deg, #ffa000, #ff6f00);
}
.card-content {
display: flex;
flex-direction: column;
}
.card-title {
font-size: 14px;
color: rgba(255, 255, 255, 0.8);
margin-bottom: 10rpx;
}
.card-value {
font-size: 24px;
font-weight: 700;
margin-bottom: 6rpx;
}
.card-unit {
font-size: 12px;
color: rgba(255, 255, 255, 0.7);
}
.card-trend, .card-status, .card-wave {
display: flex;
align-items: center;
justify-content: center;
width: 60rpx;
height: 60rpx;
}
.trend-up {
color: #4caf50;
}
.trend-down {
color: #f44336;
}
.status-normal {
color: #4caf50;
}
.status-warning {
color: #ff9800;
}
.status-danger {
color: #f44336;
}
/* 图表区域 */
.chart-container {
margin-bottom: 30rpx;
background-color: #ffffff;
border-radius: 16rpx;
padding: 20rpx;
box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.chart-header {
display: flex;
justify-content: space-between;
align-items: center;
margin-bottom: 20rpx;
}
.chart-tabs {
display: flex;
}
.chart-tab {
padding: 10rpx 20rpx;
margin-right: 10rpx;
font-size: 14px;
border-radius: 8rpx;
background-color: #f5f5f5;
color: #666666;
}
.chart-tab.active {
background-color: rgba(66, 165, 245, 0.6);
font-weight: 600;
}
.chart-settings {
width: 60rpx;
height: 60rpx;
display: flex;
align-items: center;
justify-content: center;
}
.chart-body {
height: 400rpx;
margin-bottom: 20rpx;
border-radius: 8rpx;
overflow: hidden;
}
.chart-placeholder {
width: 100%;
height: 100%;
display: flex;
align-items: center;
justify-content: center;
}
.chart-image {
width: 100%;
height: 100%;
}
.chart-legend {
display: flex;
flex-wrap: wrap;
}
.legend-item {
display: flex;
align-items: center;
margin-right: 20rpx;
margin-bottom: 10rpx;
padding: 6rpx 16rpx;
border-radius: 30rpx;
background-color: rgba(255, 255, 255, 0.1);
opacity: 0.6;
}
.legend-item.active {
opacity: 1;
}
.legend-color {
width: 20rpx;
height: 20rpx;
border-radius: 50%;
margin-right: 10rpx;
}
.legend-name {
font-size: 12px;
}
/* 历史数据控制 */
.history-control {
display: flex;
justify-content: space-between;
align-items: center;
margin-bottom: 30rpx;
padding: 20rpx;
background-color: #ffffff;
border-radius: 16rpx;
box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.date-picker {
display: flex;
align-items: center;
padding: 10rpx 20rpx;
background-color: rgba(255, 255, 255, 0.1);
border-radius: 8rpx;
}
.date-text {
margin-left: 10rpx;
font-size: 14px;
}
.query-button {
display: flex;
align-items: center;
justify-content: center;
padding: 10rpx 30rpx;
background-color: #2196f3;
border-radius: 8rpx;
}
.query-button text {
margin-left: 10rpx;
font-size: 14px;
}
.playback-controls {
display: flex;
align-items: center;
}
.playback-button {
width: 60rpx;
height: 60rpx;
display: flex;
align-items: center;
justify-content: center;
background-color: rgba(255, 255, 255, 0.1);
border-radius: 50%;
margin-right: 10rpx;
}
.speed-control {
padding: 6rpx 16rpx;
background-color: rgba(255, 255, 255, 0.1);
border-radius: 8rpx;
font-size: 12px;
}
/* 告警信息区 */
.alert-container {
margin-bottom: 30rpx;
padding: 20rpx;
background-color: #ffffff;
border-radius: 16rpx;
box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.section-title {
display: flex;
align-items: center;
margin-bottom: 20rpx;
font-size: 16px;
font-weight: 600;
}
.title-badge {
margin-left: 10rpx;
padding: 2rpx 12rpx;
background-color: #f44336;
border-radius: 20rpx;
font-size: 12px;
font-weight: normal;
}
.alert-list {
max-height: 400rpx;
overflow-y: auto;
}
.alert-item {
display: flex;
justify-content: space-between;
align-items: center;
padding: 20rpx;
margin-bottom: 10rpx;
background-color: rgba(255, 255, 255, 0.05);
border-radius: 8rpx;
border-left: 6rpx solid #4caf50;
}
.alert-item.alert-warning {
border-left-color: #ff9800;
}
.alert-item.alert-critical {
border-left-color: #f44336;
}
.alert-sensor {
width: 120rpx;
}
.sensor-id {
font-size: 16px;
font-weight: 600;
}
.alert-info {
flex: 1;
padding: 0 20rpx;
}
.alert-type {
display: block;
font-size: 14px;
margin-bottom: 6rpx;
}
.alert-value {
font-size: 12px;
color: rgba(255, 255, 255, 0.7);
}
.alert-time {
text-align: right;
}
.time-text {
display: block;
font-size: 12px;
color: rgba(255, 255, 255, 0.7);
margin-bottom: 6rpx;
}
.alert-status {
font-size: 12px;
padding: 2rpx 12rpx;
border-radius: 20rpx;
background-color: rgba(255, 255, 255, 0.1);
}
.status-new {
background-color: rgba(244, 67, 54, 0.2);
color: #f44336;
}
.status-processing {
background-color: rgba(255, 152, 0, 0.2);
color: #ff9800;
}
.status-resolved {
background-color: rgba(76, 175, 80, 0.2);
color: #4caf50;
}
/* 地图区域 */
.map-container {
margin-bottom: 30rpx;
padding: 20rpx;
background-color: #ffffff;
border-radius: 16rpx;
box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.map-toggle {
margin-left: auto;
padding: 6rpx 16rpx;
background-color: rgba(255, 255, 255, 0.1);
border-radius: 8rpx;
font-size: 12px;
}
.map-view {
position: relative;
height: 500rpx;
border-radius: 8rpx;
overflow: hidden;
}
.map-image {
width: 100%;
height: 100%;
}
.map-controls {
position: absolute;
bottom: 20rpx;
right: 20rpx;
display: flex;
flex-direction: column;
align-items: flex-end;
}
.zoom-controls {
display: flex;
flex-direction: column;
margin-bottom: 20rpx;
}
.zoom-button {
width: 60rpx;
height: 60rpx;
display: flex;
align-items: center;
justify-content: center;
background-color: rgba(0, 0, 0, 0.5);
border-radius: 8rpx;
margin-bottom: 10rpx;
}
.layer-controls {
width: 60rpx;
height: 60rpx;
display: flex;
align-items: center;
justify-content: center;
background-color: rgba(0, 0, 0, 0.5);
border-radius: 8rpx;
}
</style>