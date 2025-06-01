<template>
  <view class="profile-page">
    <!-- 用户信息区 -->
    <view class="user-info">
      <image class="avatar" src="/static/default-avatar.png" mode="aspectFill"></image>
      <view class="user-details">
        <text class="username">测试用户</text>
        <text class="user-id">ID: 10086</text>
      </view>
    </view>

    <!-- 功能列表 -->
    <view class="function-list">
      <view class="function-item">
        <view class="item-left">
          <uni-icons type="person" size="24" color="#333"></uni-icons>
          <text class="item-text">账号管理</text>
        </view>
        <uni-icons type="right" size="16" color="#999"></uni-icons>
      </view>

      <view class="function-item">
        <view class="item-left">
          <uni-icons type="notification" size="24" color="#333"></uni-icons>
          <text class="item-text">消息通知</text>
        </view>
        <uni-icons type="right" size="16" color="#999"></uni-icons>
      </view>

      <view class="function-item">
        <view class="item-left">
          <uni-icons type="gear" size="24" color="#333"></uni-icons>
          <text class="item-text">系统设置</text>
        </view>
        <uni-icons type="right" size="16" color="#999"></uni-icons>
      </view>

      <view class="function-item">
        <view class="item-left">
          <uni-icons type="help" size="24" color="#333"></uni-icons>
          <text class="item-text">帮助与反馈</text>
        </view>
        <uni-icons type="right" size="16" color="#999"></uni-icons>
      </view>

      <view class="function-item">
        <view class="item-left">
          <uni-icons type="info" size="24" color="#333"></uni-icons>
          <text class="item-text">关于我们</text>
        </view>
        <uni-icons type="right" size="16" color="#999"></uni-icons>
      </view>
    </view>

    <tab-bar></tab-bar>
  </view>
</template>

<script lang="ts" setup>
import TabBar from '@/components/tab-bar/tab-bar.vue'
// 可以在这里添加需要的逻辑
</script>

<style>
.profile-page {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding-bottom: 30rpx;
}

.user-info {
  background-color: #ffffff;
  padding: 40rpx 30rpx;
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  margin-right: 30rpx;
}

.user-details {
  flex: 1;
}

.username {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 10rpx;
  display: block;
}

.user-id {
  font-size: 24rpx;
  color: #999999;
}

.function-list {
  background-color: #ffffff;
}

.function-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1px solid #f5f5f5;
}

.function-item:last-child {
  border-bottom: none;
}

.item-left {
  display: flex;
  align-items: center;
}

.item-text {
  font-size: 28rpx;
  color: #333333;
  margin-left: 20rpx;
}
</style> 