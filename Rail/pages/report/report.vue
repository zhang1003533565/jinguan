<template>
  <view class="container">
    <view class="form-container">
      <view class="form-title">报告问题</view>
      
      <!-- 隧道选择 -->
      <view class="form-item">
        <text class="label">选择隧道</text>
        <picker 
          mode="selector" 
          :range="tunnels" 
          range-key="name"
          @change="(e) => { formData.tunnelId = tunnels[e.detail.value].id }"
          class="picker"
        >
          <view class="picker-value">
            <text class="picker-text">
              {{ tunnels.find(t => t.id === formData.tunnelId)?.name || '请选择隧道' }}
            </text>
            <text class="picker-arrow">></text>
          </view>
        </picker>
      </view>
      
      <!-- 问题类型 -->
      <view class="form-item">
        <text class="label">问题类型</text>
        <view class="type-group">
          <view 
            v-for="type in types" 
            :key="type.value"
            class="type-item"
            :class="{ active: formData.type === type.value }"
            @click="formData.type = type.value"
          >
            {{ type.label }}
          </view>
        </view>
      </view>
      
      <!-- 问题级别 -->
      <view class="form-item">
        <text class="label">问题级别</text>
        <view class="level-group">
          <view 
            v-for="level in levels" 
            :key="level.value"
            class="level-item"
            :class="{ 
              active: formData.level === level.value,
              'level-high': level.value === '严重',
              'level-medium': level.value === '中等',
              'level-low': level.value === '轻微'
            }"
            @click="formData.level = level.value"
          >
            {{ level.label }}
          </view>
        </view>
      </view>
      
      <!-- 问题描述 -->
      <view class="form-item">
        <text class="label">问题描述</text>
        <textarea
          v-model="formData.description"
          class="description-input"
          placeholder="请详细描述发现的问题..."
          maxlength="200"
        />
        <text class="word-count">{{ formData.description.length }}/200</text>
      </view>
      
      <!-- 处理方式 -->
      <view class="form-item">
        <text class="label">处理方式</text>
        <input 
          type="text" 
          v-model="formData.handleMethod"
          class="input"
          placeholder="请输入处理方式"
        />
      </view>
      
      <!-- 处理时间 -->
      <view class="form-item">
        <text class="label">处理时间</text>
        <picker 
          mode="date" 
          :value="formData.handleDate" 
          :start="startDate"
          :end="endDate"
          @change="onDateChange"
          class="picker"
        >
          <view class="picker-value">
            <text class="picker-text">{{ formData.handleDate || '选择日期' }}</text>
            <text class="picker-arrow">></text>
          </view>
        </picker>
        <picker 
          mode="time" 
          :value="formData.handleTime" 
          @change="onTimeChange"
          class="picker"
          style="margin-top: 20rpx;"
        >
          <view class="picker-value">
            <text class="picker-text">{{ formData.handleTime || '选择时间' }}</text>
            <text class="picker-arrow">></text>
          </view>
        </picker>
      </view>
      
      <!-- 处理人 -->
      <view class="form-item">
        <text class="label">处理人</text>
        <input 
          type="text" 
          v-model="formData.handler"
          class="input"
          placeholder="请输入处理人姓名"
        />
      </view>
      
      <!-- 状态 -->
      <view class="form-item">
        <text class="label">状态</text>
        <view class="status-group">
          <view 
            v-for="status in statuses" 
            :key="status.value"
            class="status-item"
            :class="{ 
              active: formData.status === status.value,
              'status-pending': status.value === '未处理',
              'status-processing': status.value === '处理中',
              'status-done': status.value === '已处理'
            }"
            @click="formData.status = status.value"
          >
            {{ status.label }}
          </view>
        </view>
      </view>
      
      <!-- 提交按钮 -->
      <button 
        class="submit-btn" 
        :disabled="!isFormValid || submitting"
        @click="handleSubmit"
      >
        {{ submitting ? '提交中...' : '提交报告' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { submitWarning, getAllTunnels } from '@/api/warning'

// 数据响应式定义
const submitting = ref(false)
const tunnels = ref([]) // 存储所有隧道信息

// 获取当前日期范围
const now = new Date()
const startDate = '2024-01-01'
const endDate = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`

// 表单数据
const formData = ref({
  type: '',
  level: '',
  description: '',
  handleMethod: '',
  handleDate: '',
  handleTime: '',
  handler: '',
  status: '',
  tunnelId: ''
})

// 获取所有隧道
const fetchTunnels = async () => {
  try {
    const res = await getAllTunnels()
    if (res.code === 200 && res.data) {
      tunnels.value = res.data
    }
  } catch (error) {
    console.error('获取隧道列表失败：', error)
    uni.showToast({
      title: '获取隧道列表失败',
      icon: 'none'
    })
  }
}

// 页面加载时获取隧道列表
onMounted(() => {
  fetchTunnels()
})

// 问题类型选项
const types = [
  { label: '环境警告', value: '环境警告' },
  { label: '设备警告', value: '设备警告' },
  { label: '安全警告', value: '安全警告' }
]

// 问题级别选项
const levels = [
  { label: '严重', value: '严重' },
  { label: '中等', value: '中等' },
  { label: '轻微', value: '轻微' }
]

// 状态选项
const statuses = [
  { label: '未处理', value: '未处理' },
  { label: '处理中', value: '处理中' },
  { label: '已处理', value: '已处理' }
]

// 日期选择处理
const onDateChange = (e) => {
  formData.value.handleDate = e.detail.value
}

// 时间选择处理
const onTimeChange = (e) => {
  formData.value.handleTime = e.detail.value
}

// 获取完整的处理时间
const getFullHandleTime = () => {
  if (formData.value.handleDate && formData.value.handleTime) {
    return `${formData.value.handleDate} ${formData.value.handleTime}:00`
  }
  return null
}

// 表单验证
const isFormValid = computed(() => {
  return formData.value.type &&
         formData.value.level &&
         formData.value.description.length >= 10 &&
         formData.value.status &&
         formData.value.tunnelId
})

// 提交表单
const handleSubmit = async () => {
  if (!isFormValid.value || submitting.value) {
    uni.showToast({
      title: '请完整填写必填项',
      icon: 'none'
    })
    return
  }
  
  try {
    submitting.value = true
    
    const submitData = {
      ...formData.value,
      handleTime: getFullHandleTime(),
      tunnelId: parseInt(formData.value.tunnelId)
    }
    
    const res = await submitWarning(submitData)
    
    if (res.code === 200) {
      uni.showToast({
        title: '提交成功',
        icon: 'success'
      })
      
      // 重置表单
      formData.value = {
        type: '',
        level: '',
        description: '',
        handleMethod: '',
        handleDate: '',
        handleTime: '',
        handler: '',
        status: '',
        tunnelId: ''
      }
      
      // 返回上一页
      setTimeout(() => {
        uni.navigateBack()
      }, 1500)
    }
  } catch (error) {
    console.error('提交失败：', error)
    uni.showToast({
      title: '提交失败，请重试',
      icon: 'none'
    })
  } finally {
    submitting.value = false
  }
}
</script>

<style>
.container {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 30rpx;
}

.form-container {
  background-color: #fff;
  border-radius: 20rpx;
  padding: 40rpx 30rpx;
}

.form-title {
  font-size: 36rpx;
  font-weight: bold;
  margin-bottom: 40rpx;
  color: #333;
}

.form-item {
  margin-bottom: 30rpx;
}

.label {
  display: block;
  font-size: 28rpx;
  color: #666;
  margin-bottom: 20rpx;
}

.input {
  width: 100%;
  height: 80rpx;
  border: 2rpx solid #e5e7eb;
  border-radius: 10rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  color: #333;
}

/* 类型选择样式 */
.type-group {
  display: flex;
  gap: 20rpx;
}

.type-item {
  flex: 1;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2rpx solid #e5e7eb;
  border-radius: 10rpx;
  font-size: 28rpx;
  color: #666;
}

.type-item.active {
  background-color: #3370ff;
  color: #fff;
  border-color: #3370ff;
}

/* 级别选择样式 */
.level-group {
  display: flex;
  gap: 20rpx;
}

.level-item {
  flex: 1;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2rpx solid #e5e7eb;
  border-radius: 10rpx;
  font-size: 28rpx;
  color: #666;
}

.level-item.active {
  color: #fff;
  border: none;
}

.level-item.active.level-high {
  background-color: #ff4d4f;
}

.level-item.active.level-medium {
  background-color: #faad14;
}

.level-item.active.level-low {
  background-color: #52c41a;
}

/* 状态选择样式 */
.status-group {
  display: flex;
  gap: 20rpx;
}

.status-item {
  flex: 1;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2rpx solid #e5e7eb;
  border-radius: 10rpx;
  font-size: 28rpx;
  color: #666;
}

.status-item.active {
  color: #fff;
  border: none;
}

.status-item.active.status-pending {
  background-color: #ff4d4f;
}

.status-item.active.status-processing {
  background-color: #faad14;
}

.status-item.active.status-done {
  background-color: #52c41a;
}

/* 描述输入框样式 */
.description-input {
  width: 100%;
  height: 200rpx;
  border: 2rpx solid #e5e7eb;
  border-radius: 10rpx;
  padding: 20rpx;
  font-size: 28rpx;
  color: #333;
}

.word-count {
  font-size: 24rpx;
  color: #999;
  text-align: right;
  margin-top: 10rpx;
}

/* 时间选择器样式 */
.picker {
  width: 100%;
}

.picker-value {
  height: 80rpx;
  border: 2rpx solid #e5e7eb;
  border-radius: 10rpx;
  padding: 0 20rpx;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.picker-text {
  font-size: 28rpx;
  color: #333;
}

.picker-arrow {
  color: #999;
  transform: rotate(90deg);
}

/* 提交按钮样式 */
.submit-btn {
  margin-top: 60rpx;
  width: 100%;
  height: 90rpx;
  background: linear-gradient(135deg, #3370ff, #7ba6ff);
  border-radius: 45rpx;
  color: #fff;
  font-size: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.submit-btn:disabled {
  opacity: 0.6;
}
</style> 