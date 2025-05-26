<template>
<view class="container">
<!-- 顶部导航栏 -->
<view class="nav-bar">
<text class="nav-title">轨道隧道智能预警系统</text>
</view>
<!-- 系统状态概览卡片 -->
<view class="status-container">
  <view class="status-card danger">
    <text class="status-value">2</text>
    <text class="status-label">实时异常<br>数</text>
  </view>

  <view class="status-card info">
    <text class="status-value time">13:40</text>
    <text class="status-label">数据更新<br>时间</text>
  </view>
</view>

<!-- 快速导览区 -->
<view class="quick-nav">
<view class="section-title">
<!-- <text>功能</text> -->
</view>
<view class="nav-cards">
<view class="nav-card cursor-pointer" @click="navigateToSimulation">
<image class="nav-card-image" src="https://readdy.ai/api/search-image?query=icon%2C%203D%20cartoon%2C%20Data%20simulation%20dashboard%20with%20charts%20and%20graphs%2C%20subject%20fills%2080%20percent%20of%20frame%2C%20vibrant%20colors%20with%20soft%20gradients%2C%20minimalist%20design%2C%20smooth%20rounded%20shapes%2C%20subtle%20shading%2C%20no%20outlines%2C%20centered%20composition%2C%20isolated%20on%20white%20background%2C%20playful%20and%20friendly%20aesthetic%2C%20isometric%20perspective%2C%20high%20detail%20quality%2C%20clean%20and%20modern%20look%2C%20single%20object%20focus.&width=200&height=200&seq=1&orientation=squarish" mode="aspectFit"></image>
<text class="nav-card-title">数据模拟</text>
<!-- <text class="nav-card-desc">模拟隧道运行数据分析</text> -->
</view>
<view class="nav-card cursor-pointer" @click="navigateToUpload">
<image class="nav-card-image" src="https://readdy.ai/api/search-image?query=icon%2C%203D%20cartoon%2C%20Data%20upload%20cloud%20storage%20icon%2C%20subject%20fills%2080%20percent%20of%20frame%2C%20vibrant%20colors%20with%20soft%20gradients%2C%20minimalist%20design%2C%20smooth%20rounded%20shapes%2C%20subtle%20shading%2C%20no%20outlines%2C%20centered%20composition%2C%20isolated%20on%20white%20background%2C%20playful%20and%20friendly%20aesthetic%2C%20isometric%20perspective%2C%20high%20detail%20quality%2C%20clean%20and%20modern%20look%2C%20single%20object%20focus.&width=200&height=200&seq=2&orientation=squarish" mode="aspectFit"></image>
<text class="nav-card-title">数据上传</text>
<!-- <text class="nav-card-desc">快速导入隧道检测数据</text> -->
</view>
<view class="nav-card cursor-pointer" @click="navigateToMonitor">
<image class="nav-card-image" src="https://readdy.ai/api/search-image?query=icon%2C%203D%20cartoon%2C%20Real-time%20monitoring%20dashboard%20with%20alerts%2C%20subject%20fills%2080%20percent%20of%20frame%2C%20vibrant%20colors%20with%20soft%20gradients%2C%20minimalist%20design%2C%20smooth%20rounded%20shapes%2C%20subtle%20shading%2C%20no%20outlines%2C%20centered%20composition%2C%20isolated%20on%20white%20background%2C%20playful%20and%20friendly%20aesthetic%2C%20isometric%20perspective%2C%20high%20detail%20quality%2C%20clean%20and%20modern%20look%2C%20single%20object%20focus.&width=200&height=200&seq=3&orientation=squarish" mode="aspectFit"></image>
<text class="nav-card-title">数据监控</text>
<!-- <text class="nav-card-desc">实时监控与异常预警</text> -->
</view>
</view>
</view>
<!-- 数据展示与监控区 -->
<view class="monitor-section">
<view class="section-title">
<text>实时监控</text>
<view class="view-more cursor-pointer" @click="navigateToMonitor">
<text class="view-more-text">查看详情</text>
<uni-icons type="right" size="14" color="#4a86e8"></uni-icons>
</view>
</view>
<view class="monitor-container">
<view class="chart-container">
<image class="chart-image" src="https://readdy.ai/api/search-image?query=Professional%20data%20visualization%20chart%20showing%20tunnel%20monitoring%20data%20with%20line%20graphs%20and%20bar%20charts%2C%20showing%20stress%20and%20displacement%20metrics%20over%20time%2C%20clean%20design%20with%20blue%20and%20red%20indicators%2C%20white%20background%2C%20technical%20appearance%2C%20high%20detail%20quality%2C%20data%20analytics%20dashboard%20style.&width=650&height=300&seq=4&orientation=landscape" mode="widthFix"></image>
</view>
<view class="alert-list">
<view class="alert-title">
<text>预警记录</text>
</view>
<view class="alert-item" v-for="(alert, index) in alertData.slice(0, 3)" :key="index" :class="{ 'alert-high': alert.level === '高', 'alert-medium': alert.level === '中', 'alert-low': alert.level === '低' }">
<view class="alert-icon">
<uni-icons :type="getAlertIcon(alert.level)" size="20" :color="getAlertColor(alert.level)"></uni-icons>
</view>
<view class="alert-content">
<text class="alert-location">{{ alert.location }}</text>
<text class="alert-message">{{ alert.message }}</text>
<text class="alert-time">{{ alert.time }}</text>
</view>
</view>
</view>
</view>
</view>
<!-- 数据上传入口 -->
<view class="upload-section">
<view class="section-title">
<text>数据上传</text>
</view>
<view class="upload-container">
<view class="upload-box cursor-pointer" @click="handleUpload">
<uni-icons type="cloud-upload" size="40" color="#4a86e8"></uni-icons>
<text class="upload-text">点击上传数据文件</text>
<text class="upload-desc">支持.csv、.xlsx格式</text>
</view>
<view class="upload-progress" v-if="uploadProgress > 0">
<text class="progress-text">上传进度: {{ uploadProgress }}%</text>
<view class="progress-bar">
<view class="progress-fill" :style="{ width: uploadProgress + '%' }"></view>
</view>
</view>
</view>
</view>
<!-- 数据模拟展示区 -->
<view class="simulation-data">
<view class="section-title">
<text>最近数据模拟</text>
<view class="view-more cursor-pointer" @click="navigateToSimulation">
<text class="view-more-text">查看更多</text>
<uni-icons type="right" size="14" color="#4a86e8"></uni-icons>
</view>
</view>
<scroll-view class="table-container" scroll-x="true">
<view class="data-table">
<view class="table-header">
<view class="table-cell">位置</view>
<view class="table-cell">应力值</view>
<view class="table-cell">位移量</view>
<view class="table-cell">预警等级</view>
<view class="table-cell">时间戳</view>
</view>
<view class="table-row" v-for="(item, index) in simulationData" :key="index">
<view class="table-cell">{{ item.location }}</view>
<view class="table-cell">{{ item.stress }}</view>
<view class="table-cell">{{ item.displacement }}</view>
<view class="table-cell" :class="getWarningClass(item.warningLevel)">{{ item.warningLevel }}</view>
<view class="table-cell">{{ item.timestamp }}</view>
</view>
</view>
</scroll-view>
</view>
</view>
</template>
<script lang="ts" setup>
import { ref } from 'vue';
// 模拟数据
const simulationData = ref([
{ location: 'K32+500', stress: '245.6 MPa', displacement: '2.3 mm', warningLevel: '正常', timestamp: '2025-05-24 12:30' },
{ location: 'K32+550', stress: '356.8 MPa', displacement: '5.7 mm', warningLevel: '警告', timestamp: '2025-05-24 12:35' },
{ location: 'K32+600', stress: '289.2 MPa', displacement: '3.2 mm', warningLevel: '正常', timestamp: '2025-05-24 12:40' },
{ location: 'K32+650', stress: '412.5 MPa', displacement: '7.8 mm', warningLevel: '危险', timestamp: '2025-05-24 12:45' },
{ location: 'K32+700', stress: '267.3 MPa', displacement: '2.9 mm', warningLevel: '正常', timestamp: '2025-05-24 12:50' }
]);
// 预警数据
const alertData = ref([
{ level: '高', location: 'K32+650', message: '位移量超过安全阈值', time: '2025-05-24 12:45' },
{ level: '中', location: 'K32+550', message: '应力值接近警戒线', time: '2025-05-24 12:35' },
{ level: '低', location: 'K32+800', message: '传感器数据波动异常', time: '2025-05-24 11:20' },
{ level: '低', location: 'K32+450', message: '湿度指标略有异常', time: '2025-05-24 10:15' }
]);
// 上传进度
const uploadProgress = ref(0);
// 获取预警等级样式
const getWarningClass = (level: string) => {
if (level === '危险') return 'warning-high';
if (level === '警告') return 'warning-medium';
return 'warning-normal';
};
// 获取预警图标
const getAlertIcon = (level: string) => {
if (level === '高') return 'error';
if (level === '中') return 'info';
return 'notification';
};
// 获取预警颜色
const getAlertColor = (level: string) => {
if (level === '高') return '#ff4d4f';
if (level === '中') return '#faad14';
return '#52c41a';
};
// 导航函数
const navigateToSimulation = () => {
uni.showToast({
title: '跳转到数据模拟页面',
icon: 'none'
});
};
const navigateToUpload = () => {
uni.showToast({
title: '跳转到数据上传页面',
icon: 'none'
});
};
const navigateToMonitor = () => {
uni.showToast({
title: '跳转到数据监控页面',
icon: 'none'
});
};
// 处理上传
const handleUpload = () => {
uni.showToast({
title: '选择文件上传',
icon: 'none'
});
// 模拟上传进度
uploadProgress.value = 0;
const timer = setInterval(() => {
uploadProgress.value += 10;
if (uploadProgress.value >= 100) {
clearInterval(timer);
setTimeout(() => {
uploadProgress.value = 0;
uni.showToast({
title: '上传完成',
icon: 'success'
});
}, 500);
}
}, 300);
};
</script>
<style>

page {
height: 100%;
background-color: #f5f7fa;
font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
}
.cursor-pointer {
cursor: pointer;
}
.container {
display: flex;
flex-direction: column;
min-height: 100%;
padding-bottom: 40rpx;
}
/* 导航栏样式 */
.nav-bar {
background-color: #1a3a8f;
height: 150rpx;
display: flex;
justify-content: center;
align-items: center;
box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
position: relative;
z-index: 10;
}
.nav-title {
color: #ffffff;
font-size: 36px;
font-weight: 600;
}
/* 状态卡片样式 */
.status-container {
  display: flex;
  justify-content: center;
  gap: 20rpx;
  
  padding: 20rpx;
}
.status-card {
  width: 50%;
  padding: 80rpx 0;
  border-radius: 20rpx;
  /* min-height: 180rpx; */
  background: linear-gradient(to bottom right, #fff5f5, #ffffff); /* 默认淡粉 */
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  align-items: center;
  transition: all 0.3s;
}

/* 红色卡片（异常） */
.status-card.danger {
  background: linear-gradient(to bottom right, #fff0f0, #ffffff);
}

/* 蓝色卡片（时间） */
.status-card.info {
  background: linear-gradient(to bottom right, #f0f7ff, #ffffff);
}
.status-value {
  font-size: 65rpx;
  font-weight: bold;
  color: #f54545;  /* 红色数字 */
}
.status-value.time {
  color: #333333;  /* 时间字体色 */
}
.status-label {
  font-size: 33rpx;
  color: #666;
  text-align: center;
  line-height: 1.6;
}

/* 快速导览区样式 */
.quick-nav {
margin: 15rpx;
}
.section-title {
display: flex;
justify-content: space-between;
align-items: center;
margin-bottom: 20rpx;
}
.section-title text {
font-size: 18px;
font-weight: 600;
color: #333333;
}
.view-more {
display: flex;
align-items: center;
}
.view-more-text {
font-size: 20x;
color: #4a86e8;
margin-right: 6rpx;
}
.nav-cards {
display: flex;
justify-content: space-between;
padding: 0 25rpx;
margin-top: 20rpx;
}
.nav-card {
width: 150rpx;
background-color: #ffffff;
border-radius: 16rpx;
padding: 30rpx;
display: flex;
flex-direction: column;
align-items: center;
box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
transition: transform 0.3s ease;
}
.nav-card:active {
transform: scale(0.98);
}
.nav-card-image {
width: 120rpx;
height: 120rpx;
margin-bottom: 20rpx;
}
.nav-card-title {
font-size: 18px;
font-weight: 600;
color: #333333;
margin-bottom: 10rpx;
}
.nav-card-desc {
font-size: 15px;
color: #999999;
text-align: center;
line-height: 1.4;
}
/* 数据模拟展示区样式 */
.simulation-data {
margin: 30rpx;
background-color: #ffffff;
border-radius: 16rpx;
padding: 30rpx;
box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}
.table-container {
overflow: auto;
margin-top: 20rpx;
}
.data-table {
min-width: 100%;
border-collapse: collapse;
}
.table-header {
display: flex;
background-color: #f0f5ff;
border-radius: 8rpx 8rpx 0 0;
font-weight: 600;
}
.table-row {
display: flex;
border-bottom: 1px solid #f0f0f0;
}
.table-row:last-child {
border-bottom: none;
}
.table-cell {
flex: 1;
padding: 20rpx 30rpx;
font-size: 18px;
color: #333333;
white-space: nowrap;
overflow: hidden;
text-overflow: ellipsis;
min-width: 160rpx;
text-align: center;
}
.warning-high {
color: #ff4d4f;
font-weight: 600;
}
.warning-medium {
color: #faad14;
font-weight: 600;
}
.warning-normal {
color: #52c41a;
}
/* 数据上传入口样式 */
.upload-section {
margin: 30rpx;
background-color: #ffffff;
border-radius: 16rpx;
padding: 30rpx;
box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}
.upload-container {
margin-top: 20rpx;
}
.upload-box {
border: 2px dashed #d9d9d9;
border-radius: 12rpx;
padding: 60rpx 0;
display: flex;
flex-direction: column;
align-items: center;
justify-content: center;
background-color: #fafafa;
transition: all 0.3s;
}
.upload-box:active {
border-color: #4a86e8;
background-color: #f0f5ff;
}
.upload-text {
font-size: 20px;
color: #333333;
margin-top: 20rpx;
font-weight: 500;
}
.upload-desc {
font-size: 15px;
color: #999999;
margin-top: 10rpx;
}
.upload-progress {
margin-top: 30rpx;
}
.progress-text {
font-size: 15px;
color: #666666;
margin-bottom: 10rpx;
display: block;
}
.progress-bar {
height: 16rpx;
background-color: #f0f0f0;
border-radius: 8rpx;
overflow: hidden;
}
.progress-fill {
height: 100%;
background-color: #4a86e8;
border-radius: 8rpx;
transition: width 0.3s ease;
}
/* 数据展示与监控区样式 */
.monitor-section {
margin: 30rpx;
background-color: #ffffff;
border-radius: 16rpx;
padding: 30rpx;
box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}
.monitor-container {
margin-top: 20rpx;
}
.chart-container {
margin-bottom: 30rpx;
border-radius: 12rpx;
overflow: hidden;
box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
.chart-image {
width: 100%;
display: block;
}
.alert-list {
margin-top: 20rpx;
}
.alert-title {
font-size: 21px;
font-weight: 600;
color: #333333;
margin-bottom: 20rpx;
}
.alert-item {
display: flex;
padding: 13rpx;
border-radius: 12rpx;
margin-bottom: 20rpx;
background-color: #fafafa;
transition: transform 0.2s;
}
.alert-item:last-child {
margin-bottom: 0;
}
.alert-high {
background-color: #fff1f0;
border-left: 8rpx solid #ff4d4f;
}
.alert-medium {
background-color: #fffbe6;
border-left: 8rpx solid #faad14;
}
.alert-low {
background-color: #f6ffed;
border-left: 8rpx solid #52c41a;
}
.alert-icon {
margin-right: 20rpx;
display: flex;
align-items: flex-start;
padding-top: 6rpx;
}
.alert-content {
flex: 1;
}
.alert-location {
font-size: 22px;
font-weight: 600;
color: #333333;
display: block;
margin-bottom: 6rpx;
}
.alert-message {
font-size: 15px;
color: #666666;
display: block;
margin-bottom: 10rpx;
}
.alert-time {
font-size: 10px;
color: #999999;
display: block;
}

</style>