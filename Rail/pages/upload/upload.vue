
<template>
  <view class="upload-page">
    <!-- 顶部导航栏 -->
    <view class="nav-bar">
      <view class="nav-left cursor-pointer" @click="goBack">
        <uni-icons type="back" size="24" color="#333"></uni-icons>
      </view>
      <view class="nav-title">数据上传</view>
      <view class="nav-right"></view>
    </view>

    <!-- 上传方式选择区 -->
    <view class="upload-method">
      <view class="method-title">上传方式</view>
      <view class="method-options">
        <view class="method-option cursor-pointer" :class="{ active: uploadMethod === 'single' }" @click="setUploadMethod('single')">
          <view class="radio-btn">
            <view class="radio-inner" v-if="uploadMethod === 'single'"></view>
          </view>
          <view class="option-icon">
            <uni-icons type="paperplane" size="24" color="#3498db"></uni-icons>
          </view>
          <view class="option-text">单条手动上传</view>
        </view>
        <view class="method-option cursor-pointer" :class="{ active: uploadMethod === 'batch' }" @click="setUploadMethod('batch')">
          <view class="radio-btn">
            <view class="radio-inner" v-if="uploadMethod === 'batch'"></view>
          </view>
          <view class="option-icon">
            <uni-icons type="upload" size="24" color="#3498db"></uni-icons>
          </view>
          <view class="option-text">批量文件导入(CSV/Excel)</view>
        </view>
      </view>
    </view>

    <!-- 预览表单区域 -->
    <view class="data-preview">
      <view class="preview-title">数据预览</view>
      
      <!-- 表格头部 -->
      <view class="table-header">
        <view class="table-cell">传感器类型</view>
        <view class="table-cell">编号</view>
        <view class="table-cell">时间</view>
        <view class="table-cell">数据值</view>
      </view>

      <!-- 单条上传表单 -->
      <view class="table-content" v-if="uploadMethod === 'single'">
        <view class="table-row">
          <view class="table-cell">
            <input class="input-field" type="text" v-model="singleData.sensorType" placeholder="请输入类型" />
          </view>
          <view class="table-cell">
            <input class="input-field" type="text" v-model="singleData.sensorId" placeholder="请输入编号" />
          </view>
          <view class="table-cell">
            <input class="input-field" type="text" v-model="singleData.timestamp" placeholder="YYYY-MM-DD HH:MM" />
          </view>
          <view class="table-cell">
            <input class="input-field" type="text" v-model="singleData.value" placeholder="请输入数值" />
          </view>
        </view>
      </view>

      <!-- 批量上传预览 -->
      <view class="table-content" v-else>
        <view class="file-upload-area cursor-pointer" v-if="!batchData.length" @click="selectFile">
          <uni-icons type="cloud-upload" size="40" color="#3498db"></uni-icons>
          <view class="upload-text">点击选择文件上传</view>
          <view class="upload-desc">支持CSV、Excel格式</view>
        </view>
        <view class="batch-preview" v-else>
          <view class="file-info">
            <view class="file-name">{{ fileName }}</view>
            <view class="file-action cursor-pointer" @click="clearBatchData">
              <uni-icons type="trash" size="18" color="#e74c3c"></uni-icons>
            </view>
          </view>
          <scroll-view class="preview-scroll" scroll-y>
            <view class="table-row" v-for="(item, index) in batchData" :key="index">
              <view class="table-cell">{{ item.sensorType }}</view>
              <view class="table-cell">{{ item.sensorId }}</view>
              <view class="table-cell">{{ item.timestamp }}</view>
              <view class="table-cell">{{ item.value }}</view>
            </view>
          </scroll-view>
        </view>
      </view>
    </view>

    <!-- 操作按钮区域 -->
    <view class="action-buttons">
      <button class="submit-btn cursor-pointer" :disabled="isUploading" @click="submitUpload">
        <uni-icons v-if="isUploading" type="spinner-cycle" size="16" color="#fff"></uni-icons>
        <text>{{ isUploading ? '上传中...' : '提交上传' }}</text>
      </button>
      <button class="clear-btn cursor-pointer" @click="clearData">清空数据</button>
    </view>

    <!-- 上传反馈提示 -->
    <view class="upload-feedback" v-if="feedback.show">
      <view class="feedback-content" :class="feedback.type">
        <uni-icons :type="feedback.type === 'success' ? 'checkmarkempty' : 'closeempty'" size="20" :color="feedback.type === 'success' ? '#2ecc71' : '#e74c3c'"></uni-icons>
        <text>{{ feedback.message }}</text>
      </view>
    </view>

    <!-- 上传进度条 -->
    <view class="progress-bar" v-if="isUploading && uploadMethod === 'batch'">
      <view class="progress-inner" :style="{ width: `${uploadProgress}%` }"></view>
      <text class="progress-text">{{ uploadProgress }}%</text>
    </view>

    <!-- 上传日志与状态区域 -->
    <view class="upload-logs">
      <view class="logs-title">上传记录</view>
      <scroll-view class="logs-content" scroll-y>
        <view class="log-item" v-for="(log, index) in uploadLogs" :key="index" :class="log.status">
          <view class="log-status">
            <uni-icons :type="log.status === 'success' ? 'checkmarkempty' : 'closeempty'" size="20" :color="log.status === 'success' ? '#2ecc71' : '#e74c3c'"></uni-icons>
          </view>
          <view class="log-info">
            <view class="log-time">{{ log.time }}</view>
            <view class="log-desc">{{ log.desc }}</view>
            <view class="log-message" v-if="log.message">{{ log.message }}</view>
          </view>
          <view class="log-action" v-if="log.status === 'error'">
            <view class="retry-btn cursor-pointer" @click="retryUpload(index)">
              <uni-icons type="refresh" size="16" color="#3498db"></uni-icons>
              <text>重试</text>
            </view>
          </view>
        </view>
        <view class="empty-logs" v-if="!uploadLogs.length">
          <uni-icons type="info" size="24" color="#bdc3c7"></uni-icons>
          <text>暂无上传记录</text>
        </view>
      </scroll-view>
    </view>
  </view>
</template>
<script lang="ts" setup>
import { ref, reactive } from 'vue';

// 上传方式
const uploadMethod = ref('single');

// 单条数据
const singleData = reactive({
  sensorType: '',
  sensorId: '',
  timestamp: '',
  value: ''
});

// 批量数据
const batchData = ref<Array<{
  sensorType: string;
  sensorId: string;
  timestamp: string;
  value: string;
}>>([]);

// 文件名
const fileName = ref('');

// 上传状态
const isUploading = ref(false);
const uploadProgress = ref(0);

// 反馈信息
const feedback = reactive({
  show: false,
  type: 'success',
  message: ''
});

// 上传日志
const uploadLogs = ref<Array<{
  time: string;
  desc: string;
  status: 'success' | 'error';
  message?: string;
}>>([]);

// 设置上传方式
const setUploadMethod = (method: string) => {
  uploadMethod.value = method;
};

// 返回主页
const goBack = () => {
  uni.navigateBack();
};

// 选择文件
const selectFile = () => {
  // 模拟文件选择和解析
  setTimeout(() => {
    fileName.value = '传感器数据20250524.csv';
    batchData.value = [
      { sensorType: '应变传感器', sensorId: 'STR001', timestamp: '2025-05-24 08:30:45', value: '0.056' },
      { sensorType: '应变传感器', sensorId: 'STR002', timestamp: '2025-05-24 08:30:45', value: '0.062' },
      { sensorType: '渗漏传感器', sensorId: 'LK001', timestamp: '2025-05-24 08:30:45', value: '0.003' },
      { sensorType: '振动传感器', sensorId: 'VIB001', timestamp: '2025-05-24 08:30:45', value: '0.127' },
      { sensorType: '振动传感器', sensorId: 'VIB002', timestamp: '2025-05-24 08:30:45', value: '0.135' }
    ];
  }, 500);
};

// 清空批量数据
const clearBatchData = () => {
  batchData.value = [];
  fileName.value = '';
};

// 清空所有数据
const clearData = () => {
  if (uploadMethod.value === 'single') {
    singleData.sensorType = '';
    singleData.sensorId = '';
    singleData.timestamp = '';
    singleData.value = '';
  } else {
    clearBatchData();
  }
  showFeedback('success', '数据已清空');
};

// 显示反馈信息
const showFeedback = (type: 'success' | 'error', message: string) => {
  feedback.show = true;
  feedback.type = type;
  feedback.message = message;
  
  setTimeout(() => {
    feedback.show = false;
  }, 3000);
};

// 获取当前时间
const getCurrentTime = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const day = String(now.getDate()).padStart(2, '0');
  const hours = String(now.getHours()).padStart(2, '0');
  const minutes = String(now.getMinutes()).padStart(2, '0');
  const seconds = String(now.getSeconds()).padStart(2, '0');
  
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

// 提交上传
const submitUpload = () => {
  if (uploadMethod.value === 'single') {
    // 验证单条数据
    if (!singleData.sensorType || !singleData.sensorId || !singleData.timestamp || !singleData.value) {
      showFeedback('error', '请填写完整的数据信息');
      return;
    }
    
    // 模拟上传
    isUploading.value = true;
    
    setTimeout(() => {
      isUploading.value = false;
      
      // 随机成功或失败
      const isSuccess = Math.random() > 0.2;
      
      if (isSuccess) {
        showFeedback('success', '数据上传成功');
        uploadLogs.value.unshift({
          time: getCurrentTime(),
          desc: `单条数据上传：${singleData.sensorType} - ${singleData.sensorId}`,
          status: 'success'
        });
        
        // 清空表单
        singleData.sensorType = '';
        singleData.sensorId = '';
        singleData.timestamp = '';
        singleData.value = '';
      } else {
        showFeedback('error', '数据上传失败，请重试');
        uploadLogs.value.unshift({
          time: getCurrentTime(),
          desc: `单条数据上传：${singleData.sensorType} - ${singleData.sensorId}`,
          status: 'error',
          message: '网络连接超时，请检查网络'
        });
      }
    }, 1500);
  } else {
    // 验证批量数据
    if (batchData.value.length === 0) {
      showFeedback('error', '请先选择要上传的文件');
      return;
    }
    
    // 模拟批量上传
    isUploading.value = true;
    uploadProgress.value = 0;
    
    const totalItems = batchData.value.length;
    let processedItems = 0;
    
    const progressInterval = setInterval(() => {
      processedItems++;
      uploadProgress.value = Math.floor((processedItems / totalItems) * 100);
      
      if (processedItems >= totalItems) {
        clearInterval(progressInterval);
        isUploading.value = false;
        
        // 随机成功或失败
        const isSuccess = Math.random() > 0.2;
        
        if (isSuccess) {
          showFeedback('success', '批量数据上传成功');
          uploadLogs.value.unshift({
            time: getCurrentTime(),
            desc: `批量数据上传：${fileName.value}（${totalItems}条）`,
            status: 'success'
          });
          
          // 清空批量数据
          clearBatchData();
        } else {
          showFeedback('error', '批量数据上传失败，请重试');
          uploadLogs.value.unshift({
            time: getCurrentTime(),
            desc: `批量数据上传：${fileName.value}（${totalItems}条）`,
            status: 'error',
            message: '服务器处理异常，请稍后再试'
          });
        }
      }
    }, 300);
  }
};

// 重试上传
const retryUpload = (index: number) => {
  const log = uploadLogs.value[index];
  
  // 修改状态为上传中
  uploadLogs.value[index] = {
    ...log,
    desc: `${log.desc}（重试中...）`,
    status: 'success',
    message: '正在重新上传'
  };
  
  // 模拟重试
  setTimeout(() => {
    // 随机成功或失败
    const isSuccess = Math.random() > 0.2;
    
    if (isSuccess) {
      uploadLogs.value[index] = {
        time: getCurrentTime(),
        desc: log.desc.replace('（重试中...）', ''),
        status: 'success'
      };
      showFeedback('success', '重试上传成功');
    } else {
      uploadLogs.value[index] = {
        time: getCurrentTime(),
        desc: log.desc.replace('（重试中...）', ''),
        status: 'error',
        message: '重试失败，服务器无响应'
      };
      showFeedback('error', '重试上传失败');
    }
  }, 1500);
};
</script>
<style>
page {
  height: 100%;
}

.cursor-pointer {
  cursor: pointer;
}

.upload-page {
  display: flex;
  flex-direction: column;
  min-height: 100%;
  background-color: #f5f7fa;
  padding-bottom: 30rpx;
}

/* 导航栏 */
.nav-bar {
  display: flex;
  align-items: center;
  height: 90rpx;
  background-color: #ffffff;
  padding: 0 30rpx;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  position: relative;
  z-index: 10;
}

.nav-left {
  width: 60rpx;
}

.nav-title {
  flex: 1;
  text-align: center;
  font-size: 18px;
  font-weight: 600;
  color: #333333;
}

.nav-right {
  width: 60rpx;
}

/* 上传方式选择区 */
.upload-method {
  margin: 30rpx;
  background-color: #ffffff;
  border-radius: 12rpx;
  padding: 30rpx;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.method-title {
  font-size: 16px;
  font-weight: 600;
  color: #333333;
  margin-bottom: 20rpx;
}

.method-options {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.method-option {
  display: flex;
  align-items: center;
  flex: 1;
  padding: 20rpx;
  border-radius: 8rpx;
  transition: all 0.3s;
}

.method-option.active {
  background-color: rgba(52, 152, 219, 0.1);
}

.radio-btn {
  width: 36rpx;
  height: 36rpx;
  border-radius: 50%;
  border: 2px solid #3498db;
  margin-right: 20rpx;
  display: flex;
  justify-content: center;
  align-items: center;
}

.radio-inner {
  width: 20rpx;
  height: 20rpx;
  border-radius: 50%;
  background-color: #3498db;
}

.option-icon {
  margin-right: 16rpx;
}

.option-text {
  font-size: 14px;
  color: #333333;
}

/* 预览表单区域 */
.data-preview {
  margin: 0 30rpx 30rpx;
  background-color: #ffffff;
  border-radius: 12rpx;
  padding: 30rpx;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.preview-title {
  font-size: 16px;
  font-weight: 600;
  color: #333333;
  margin-bottom: 20rpx;
}

.table-header {
  display: flex;
  background-color: #f2f6fc;
  border-radius: 8rpx 8rpx 0 0;
  border-bottom: 1px solid #ebeef5;
}

.table-cell {
  flex: 1;
  padding: 20rpx 16rpx;
  font-size: 14px;
  color: #606266;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.table-content {
  max-height: 400rpx;
  overflow: auto;
  border: 1px solid #ebeef5;
  border-top: none;
  border-radius: 0 0 8rpx 8rpx;
}

.table-row {
  display: flex;
  border-bottom: 1px solid #ebeef5;
}

.table-row:last-child {
  border-bottom: none;
}

.input-field {
  width: 100%;
  height: 70rpx;
  font-size: 14px;
  color: #333333;
  text-align: center;
}

.file-upload-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60rpx 0;
  border: 2px dashed #dcdfe6;
  border-radius: 8rpx;
}

.upload-text {
  font-size: 16px;
  color: #3498db;
  margin-top: 20rpx;
  font-weight: 500;
}

.upload-desc {
  font-size: 12px;
  color: #909399;
  margin-top: 10rpx;
}

.batch-preview {
  display: flex;
  flex-direction: column;
}

.file-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx;
  background-color: #f2f6fc;
}

.file-name {
  font-size: 14px;
  color: #3498db;
  font-weight: 500;
}

.preview-scroll {
  max-height: 300rpx;
}

/* 操作按钮区域 */
.action-buttons {
  display: flex;
  justify-content: space-between;
  margin: 0 30rpx 30rpx;
}

.submit-btn {
  flex: 1;
  height: 90rpx;
  background-color: #3498db;
  color: #ffffff;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
}

.submit-btn:disabled {
  background-color: #a0cfee;
}

.clear-btn {
  flex: 1;
  height: 90rpx;
  background-color: #ffffff;
  color: #606266;
  font-size: 16px;
  font-weight: 500;
  border: 1px solid #dcdfe6;
  border-radius: 8rpx;
}

/* 上传反馈提示 */
.upload-feedback {
  margin: 0 30rpx 30rpx;
}

.feedback-content {
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  border-radius: 8rpx;
}

.feedback-content.success {
  background-color: #eafaf1;
  border-left: 4px solid #2ecc71;
}

.feedback-content.error {
  background-color: #fdecea;
  border-left: 4px solid #e74c3c;
}

.feedback-content text {
  margin-left: 16rpx;
  font-size: 14px;
  color: #333333;
}

/* 进度条 */
.progress-bar {
  margin: 0 30rpx 30rpx;
  height: 40rpx;
  background-color: #ecf0f1;
  border-radius: 20rpx;
  overflow: hidden;
  position: relative;
}

.progress-inner {
  height: 100%;
  background-color: #3498db;
  border-radius: 20rpx;
  transition: width 0.3s;
}

.progress-text {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #ffffff;
  font-weight: 500;
}

/* 上传日志与状态区域 */
.upload-logs {
  margin: 0 30rpx;
  background-color: #ffffff;
  border-radius: 12rpx;
  padding: 30rpx;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  flex: 1;
}

.logs-title {
  font-size: 16px;
  font-weight: 600;
  color: #333333;
  margin-bottom: 20rpx;
}

.logs-content {
  max-height: 600rpx;
}

.log-item {
  display: flex;
  padding: 20rpx;
  border-bottom: 1px solid #ebeef5;
}

.log-item:last-child {
  border-bottom: none;
}

.log-status {
  margin-right: 20rpx;
  display: flex;
  align-items: flex-start;
}

.log-info {
  flex: 1;
}

.log-time {
  font-size: 12px;
  color: #909399;
  margin-bottom: 10rpx;
}

.log-desc {
  font-size: 14px;
  color: #333333;
  margin-bottom: 10rpx;
}

.log-message {
  font-size: 12px;
  color: #e74c3c;
}

.log-action {
  display: flex;
  align-items: center;
}

.retry-btn {
  display: flex;
  align-items: center;
  padding: 10rpx 20rpx;
  background-color: #edf2fc;
  border-radius: 4rpx;
}

.retry-btn text {
  font-size: 12px;
  color: #3498db;
  margin-left: 8rpx;
}

.empty-logs {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60rpx 0;
  color: #909399;
}

.empty-logs text {
  font-size: 14px;
  color: #909399;
  margin-top: 20rpx;
}
</style>
