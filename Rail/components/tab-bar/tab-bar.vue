<template>
  <view class="tab-bar">
    <view 
      class="tab-item" 
      :class="{ active: currentPage === 'index' }"
      @click="switchTab('index')"
    >
      <image 
        class="tab-icon" 
        :src="currentPage === 'index' ? '/static/tabbar/home-active.png' : '/static/tabbar/home.png'"
      ></image>
      <text class="tab-text" :class="{ 'text-active': currentPage === 'index' }">首页</text>
    </view>
    
    <view 
      class="tab-item" 
      :class="{ active: currentPage === 'profile' }"
      @click="switchTab('profile')"
    >
      <image 
        class="tab-icon" 
        :src="currentPage === 'profile' ? '/static/tabbar/profile-active.png' : '/static/tabbar/profile.png'"
      ></image>
      <text class="tab-text" :class="{ 'text-active': currentPage === 'profile' }">我的</text>
    </view>
  </view>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'

// 当前页面
const currentPage = ref('index')

// 更新当前页面
const updateCurrentPage = () => {
  const pages = getCurrentPages()
  if (pages.length > 0) {
    const route = pages[pages.length - 1].route
    if (route) {
      const pageName = route.split('/')[1]
      currentPage.value = pageName
    }
  }
}

// 切换页面
const switchTab = (page: string) => {
  if (currentPage.value === page) return
  currentPage.value = page
  uni.navigateTo({
    url: `/pages/${page}/${page}`
  })
}

// 初始化当前页面
onMounted(() => {
  updateCurrentPage()
})
</script>

<style>
.tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100rpx;
  background-color: #ffffff;
  display: flex;
  padding-bottom: env(safe-area-inset-bottom);
  border-top: 1px solid #f5f5f5;
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10rpx 0;
}

.tab-icon {
  width: 48rpx;
  height: 48rpx;
  margin-bottom: 4rpx;
}

.tab-text {
  font-size: 24rpx;
  color: #999999;
}

.text-active {
  color: #3370ff;
}
</style> 