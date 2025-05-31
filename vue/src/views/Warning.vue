<template>
  <div class="h-screen bg-[#0D122A] text-white p-6">
    <h2 class="text-xl font-bold mb-4">告警管理</h2>

    <!-- 筛选区域 -->
    <div class="mb-4 flex flex-wrap gap-4 items-center">
      <select v-model="filter.level" class="form-input w-40">
        <option value="">全部级别</option>
        <option value="高">高</option>
        <option value="中">中</option>
        <option value="低">低</option>
      </select>
      <select v-model="filter.type" class="form-input w-40">
        <option value="">全部类型</option>
        <option value="设备">设备</option>
        <option value="环境">环境</option>
      </select>
      <input
        v-model="filter.keyword"
        type="text"
        placeholder="关键词搜索..."
        class="form-input w-60"
      />
      <button
        @click="applyFilter"
        class="bg-blue-600 px-4 py-1 rounded text-sm"
      >
        查询
      </button>
      <button
        @click="clearFilter"
        class="bg-gray-600 px-3 py-1 rounded text-sm"
      >
        清除
      </button>
      <button
        @click="showExportModal = true"
        class="bg-green-600 px-3 py-1 rounded text-sm"
      >
        导出 CSV
      </button>
      <button
        @click="openChart"
        class="bg-purple-600 px-3 py-1 rounded text-sm"
      >
        查看图表
      </button>
    </div>

    <!-- 表格区域 -->
    <div class="bg-[#1B1F3B] rounded-lg p-4 overflow-auto">
      <table class="w-full text-[15px] leading-relaxed text-left">
        <thead class="text-gray-300 border-b border-gray-600">
          <tr>
            <th class="px-2 py-3">时间</th>
            <th class="px-2 py-3">类型</th>
            <th class="px-2 py-3">级别</th>
            <th class="px-2 py-3">内容</th>
            <th class="px-2 py-3">状态</th>
            <th class="px-2 py-3">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="alarm in paginatedAlarms"
            :key="alarm.id"
            class="border-b border-gray-700 hover:bg-[#2a2f50]"
          >
            <td class="px-2 py-3">{{ alarm.time }}</td>
            <td class="px-2 py-3">{{ alarm.type }}</td>
            <td class="px-2 py-3">{{ alarm.level }}</td>
            <td class="px-2 py-3">{{ alarm.content }}</td>
            <td class="px-2 py-3">{{ alarm.status }}</td>
            <td class="px-2 py-3">
              <button
                class="text-blue-400 hover:underline mr-2"
                @click="viewDetail(alarm)"
              >
                查看
              </button>
              <button
                v-if="alarm.status === '未处理'"
                class="text-green-400"
                @click="markProcessed(alarm)"
              >
                ✅
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页 -->
    <div class="flex justify-center mt-4 space-x-4 text-[15px]">
      <button
        @click="prevPage"
        :disabled="page === 1"
        class="px-3 py-1 rounded bg-gray-700"
      >
        上一页
      </button>
      <span>第 {{ page }} 页 / 共 {{ totalPages }} 页</span>
      <button
        @click="nextPage"
        :disabled="page === totalPages"
        class="px-3 py-1 rounded bg-gray-700"
      >
        下一页
      </button>
    </div>

    <!-- 查看弹窗 -->
    <div
      v-if="selectedAlarm"
      class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50"
    >
      <div class="bg-[#1B1F3B] p-6 rounded-lg w-[450px] text-[15px] text-white">
        <h3 class="text-lg font-bold mb-4">告警详情</h3>
        <p><strong>时间：</strong>{{ selectedAlarm.time }}</p>
        <p><strong>类型：</strong>{{ selectedAlarm.type }}</p>
        <p><strong>级别：</strong>{{ selectedAlarm.level }}</p>
        <p><strong>内容：</strong>{{ selectedAlarm.content }}</p>
        <p><strong>状态：</strong>{{ selectedAlarm.status }}</p>
        <label class="block mt-4 mb-1 font-semibold">备注：</label>
        <textarea
          v-model="selectedAlarm.remark"
          class="form-input w-full h-20 resize-none"
        ></textarea>
        <div class="mt-4 flex justify-end space-x-3">
          <button
            v-if="selectedAlarm.status === '未处理'"
            class="bg-green-500 px-4 py-1 rounded"
            @click="markProcessedInModal"
          >
            标记为已处理
          </button>
          <button
            class="bg-blue-500 px-4 py-1 rounded"
            @click="saveRemarkAndClose"
          >
            保存
          </button>
        </div>
      </div>
    </div>

    <!-- 导出弹窗 -->
    <div
      v-if="showExportModal"
      class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50"
    >
      <div class="bg-[#1B1F3B] p-6 rounded w-96 text-[15px]">
        <h3 class="text-lg font-bold mb-4">选择导出范围</h3>
        <div class="space-y-2 mb-4">
          <label
            ><input type="radio" v-model="exportMode" value="all" />
            全部数据</label
          ><br />
          <label
            ><input type="radio" v-model="exportMode" value="filtered" />
            当前筛选结果</label
          ><br />
          <label
            ><input type="radio" v-model="exportMode" value="page" />
            当前分页</label
          >
        </div>
        <div class="flex justify-end gap-4">
          <button
            class="bg-gray-500 px-4 py-1 rounded"
            @click="showExportModal = false"
          >
            取消
          </button>
          <button class="bg-blue-600 px-4 py-1 rounded" @click="handleExport">
            导出
          </button>
        </div>
      </div>
    </div>

    <!-- 图表弹窗 -->
    <div
      v-if="showChartModal"
      class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50"
    >
      <div class="bg-[#1B1F3B] p-4 rounded w-[600px] h-[400px] text-white">
        <h3 class="text-lg font-bold mb-2">告警等级分布图</h3>
        <div ref="chartRef" class="w-full h-[300px]"></div>
        <div class="text-right mt-3">
          <button
            class="bg-blue-500 px-4 py-1 rounded"
            @click="showChartModal = false"
          >
            关闭
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import warningApi from '@/api/warning'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

// 数据响应式定义
const loading = ref(false)
const alarms = ref([])
const selectedAlarm = ref(null)
const showExportModal = ref(false)
const showChartModal = ref(false)
const exportMode = ref('all')
const page = ref(1)
const pageSize = 10

// 筛选条件
const filter = ref({
  level: '',
  type: '',
  keyword: ''
})

// 获取预警列表
const fetchWarnings = async (retryCount = 0) => {
  try {
    if (retryCount === 0) {
      loading.value = true
    }
    const params = {
      page: page.value - 1,
      size: pageSize,
      ...filter.value
    }
    console.log(`${retryCount > 0 ? `[重试${retryCount}] ` : ''}开始获取预警列表，请求参数:`, params)
    
    const res = await warningApi.getList(params)
    console.log('预警列表响应数据:', res.data)
    
    if (!res.data || !Array.isArray(res.data.content)) {
      throw new Error('响应数据格式错误')
    }
    
    alarms.value = res.data.content
    totalPages.value = res.data.totalPages
  } catch (error) {
    console.error('获取预警列表失败：', error)
    
    // 记录详细错误信息
    const errorDetails = {
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      url: error.config?.url,
      method: error.config?.method,
      params: error.config?.params,
      headers: error.config?.headers
    }
    console.error('错误详情:', errorDetails)

    // 如果是500错误且重试次数小于3次，则重试
    if (error.response?.status === 500 && retryCount < 3) {
      console.log(`第${retryCount + 1}次重试获取预警列表`)
      await new Promise(resolve => setTimeout(resolve, 1000 * (retryCount + 1))) // 递增重试间隔
      return fetchWarnings(retryCount + 1)
    }

    let errorMessage = '获取预警列表失败'
    if (error.response?.status === 500) {
      if (retryCount >= 3) {
        errorMessage = '服务器出现问题，请稍后再试'
        ElMessage({
          type: 'error',
          message: errorMessage,
          duration: 5000,
          showClose: true
        })
      } else {
        errorMessage += ': 服务器内部错误'
        ElMessage.error(errorMessage)
      }
    } else if (error.response?.status === 401) {
      errorMessage += ': 未授权，请重新登录'
      ElMessage.error(errorMessage)
      // 可以在这里添加重定向到登录页的逻辑
    } else if (error.response?.status === 403) {
      errorMessage += ': 无权限访问'
      ElMessage.error(errorMessage)
    } else {
      errorMessage += `: ${error.message}`
      ElMessage.error(errorMessage)
    }
  } finally {
    if (retryCount === 0 || retryCount >= 3) {
      loading.value = false
    }
  }
}

// 处理预警
const handleWarning = async (id, handler, handleMethod) => {
  try {
    await warningApi.handleWarning(id, handler, handleMethod)
    ElMessage.success('处理成功')
    await fetchWarnings()
  } catch (error) {
    console.error('处理预警失败：', error)
    ElMessage.error('处理预警失败')
  }
}

// 导出预警记录
const exportWarnings = async () => {
  try {
    const params = {
      exportMode: exportMode.value,
      ...filter.value
    }
    await warningApi.exportWarnings(params)
    ElMessage.success('导出成功')
    showExportModal.value = false
  } catch (error) {
    console.error('导出预警记录失败：', error)
    ElMessage.error('导出预警记录失败')
  }
}

// 获取预警统计信息
const fetchWarningStatistics = async () => {
  try {
    const res = await warningApi.getWarningStatistics()
    const data = res.data
    drawChart(data)
  } catch (error) {
    console.error('获取预警统计信息失败：', error)
    ElMessage.error('获取预警统计信息失败')
  }
}

// 绘制图表
const drawChart = (data) => {
  const chartDom = document.getElementById('warningChart')
  const chart = echarts.init(chartDom)
  chart.setOption({
    title: {
      text: '预警等级分布',
      textStyle: { color: '#fff' }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    series: [
      {
        type: 'pie',
        radius: '70%',
        data: [
          { value: data.high, name: '高级预警' },
          { value: data.medium, name: '中级预警' },
          { value: data.low, name: '低级预警' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  })
}

// 分页相关
const totalPages = ref(1)

// 添加paginatedAlarms计算属性
const paginatedAlarms = computed(() => {
  return alarms.value
})

const prevPage = () => {
  if (page.value > 1) {
    page.value--
    fetchWarnings()
  }
}
const nextPage = () => {
  if (page.value < totalPages.value) {
    page.value++
    fetchWarnings()
  }
}

// 筛选相关
const applyFilter = () => {
  page.value = 1
  fetchWarnings()
}
const clearFilter = () => {
  filter.value = {
    level: '',
    type: '',
    keyword: ''
  }
  applyFilter()
}

// 查看详情
const viewDetail = (alarm) => {
  selectedAlarm.value = { ...alarm }
}

// 标记为已处理
const markProcessed = async (alarm) => {
  await handleWarning(alarm.id, 'system', '自动处理')
}

// 在弹窗中标记为已处理
const markProcessedInModal = async () => {
  if (selectedAlarm.value) {
    await handleWarning(selectedAlarm.value.id, 'system', '手动处理')
    selectedAlarm.value = null
  }
}

// 保存备注并关闭
const saveRemarkAndClose = () => {
  // 这里可以添加保存备注的API调用
  selectedAlarm.value = null
}

// 打开图表
const openChart = async () => {
  showChartModal.value = true
  await fetchWarningStatistics()
}

// 页面加载时获取数据
onMounted(() => {
  fetchWarnings()
})
</script>

<style scoped>
.form-input {
  background-color: #0d122a;
  color: white;
  border-radius: 0.25rem;
  padding: 0.4rem 0.6rem;
  border: 1px solid #334155;
  outline: none;
  font-size: 16px;
  height: 42px;
}

.form-input:focus {
  border-color: #3b82f6;
}

button,
select,
input[type="text"] {
  font-size: 16px;
  height: 42px;
}
</style>
