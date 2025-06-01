<template>
  <div class="h-screen bg-[#0D122A] text-white p-4 lg:p-6">
    <h2 class="text-lg lg:text-xl font-bold mb-4">告警管理</h2>

    <!-- 筛选区域 -->
    <div class="mb-4 flex flex-wrap gap-3 items-center text-sm lg:text-base">
      <select v-model="filter.level" class="form-input w-28 lg:w-40">
        <option value="">全部级别</option>
        <option value="高">高</option>
        <option value="中">中</option>
        <option value="低">低</option>
      </select>
      <select v-model="filter.type" class="form-input w-28 lg:w-40">
        <option value="">全部类型</option>
        <option value="设备">设备</option>
        <option value="环境">环境</option>
      </select>
      <input
        v-model="filter.keyword"
        type="text"
        placeholder="关键词搜索..."
        class="form-input w-40 lg:w-60"
      />
      <button
        @click="applyFilter"
        class="bg-blue-600 px-3 py-1 rounded text-sm lg:text-base"
      >
        查询
      </button>
      <button
        @click="clearFilter"
        class="bg-gray-600 px-3 py-1 rounded text-sm lg:text-base"
      >
        清除
      </button>
      <button
        @click="() => { showExportModal = true; }"
        class="bg-green-600 px-3 py-1 rounded text-sm lg:text-base"
      >
        导出 CSV
      </button>
      <button
        @click="openChart"
        class="bg-purple-600 px-3 py-1 rounded text-sm lg:text-base"
      >
        查看图表
      </button>
    </div>

    <!-- 表格区域 -->
    <div class="bg-[#1B1F3B] rounded-lg p-2 lg:p-4 overflow-x-auto">
      <table class="w-full text-xs lg:text-sm leading-relaxed text-left min-w-[1200px]">
        <thead class="text-gray-300 border-b border-gray-600">
          <tr>
            <th class="px-2 py-2 lg:py-3 whitespace-nowrap">创建时间</th>
            <th class="px-2 py-2 lg:py-3 whitespace-nowrap">隧道名称</th>
            <th class="px-2 py-2 lg:py-3 whitespace-nowrap">类型</th>
            <th class="px-2 py-2 lg:py-3 whitespace-nowrap">级别</th>
            <th class="px-2 py-2 lg:py-3">内容</th>
            <th class="px-2 py-2 lg:py-3">处理方式</th>
            <th class="px-2 py-2 lg:py-3 whitespace-nowrap">处理人</th>
            <th class="px-2 py-2 lg:py-3 whitespace-nowrap">处理时间</th>
            <th class="px-2 py-2 lg:py-3 whitespace-nowrap">状态</th>
            <th class="px-2 py-2 lg:py-3 whitespace-nowrap">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="alarm in paginatedAlarms"
            :key="alarm.id"
            class="border-b border-gray-700 hover:bg-[#2a2f50] text-sm"
          >
            <td class="px-2 py-2 lg:py-3 whitespace-nowrap">{{ formatDateTime(alarm.createdAt) }}</td>
            <td class="px-2 py-2 lg:py-3 whitespace-nowrap">{{ tunnelNames[alarm.tunnelId] || '加载中...' }}</td>
            <td class="px-2 py-2 lg:py-3 whitespace-nowrap">{{ alarm.type }}</td>
            <td class="px-2 py-2 lg:py-3 whitespace-nowrap">
              <span :class="[
                'px-2 py-1 rounded text-white text-xs',
                alarm.level === '严重' ? 'bg-red-500' : 
                alarm.level === '中等' ? 'bg-yellow-500' : 
                'bg-green-500'
              ]">{{ alarm.level }}</span>
            </td>
            <td class="px-2 py-2 lg:py-3 max-w-[300px] truncate">{{ alarm.description }}</td>
            <td class="px-2 py-2 lg:py-3 max-w-[200px] truncate">{{ alarm.handleMethod || '-' }}</td>
            <td class="px-2 py-2 lg:py-3 whitespace-nowrap">{{ alarm.handler || '-' }}</td>
            <td class="px-2 py-2 lg:py-3 whitespace-nowrap">{{ alarm.handleTime ? formatDateTime(alarm.handleTime) : '-' }}</td>
            <td class="px-2 py-2 lg:py-3 whitespace-nowrap">
              <span :class="[
                'px-2 py-1 rounded text-white text-xs',
                alarm.status === '未处理' ? 'bg-red-500' : 
                alarm.status === '处理中' ? 'bg-yellow-500' : 
                'bg-green-500'
              ]">{{ alarm.status }}</span>
            </td>
            <td class="px-2 py-2 lg:py-3 whitespace-nowrap">
              <button
                class="text-blue-400 hover:underline mr-2 text-sm"
                @click="viewDetail(alarm)"
              >
                查看
              </button>
              <button
                v-if="alarm.status === '未处理'"
                class="text-green-400 hover:underline text-sm"
                @click="markProcessed(alarm)"
              >
                处理
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页 -->
    <div class="flex flex-col lg:flex-row justify-between items-center mt-4 text-xs lg:text-sm gap-2">
      <div class="text-gray-400">
        共 {{ total }} 条记录，每页显示 {{ pageSize }} 条
      </div>
      <div class="flex items-center space-x-4">
        <button
          @click="prevPage"
          :disabled="page === 1"
          :class="['px-3 py-1 rounded', page === 1 ? 'bg-gray-700 cursor-not-allowed' : 'bg-blue-600 hover:bg-blue-700']"
        >
          上一页
        </button>
        <span>第 {{ page }} 页 / 共 {{ totalPages }} 页</span>
        <button
          @click="nextPage"
          :disabled="page === totalPages"
          :class="['px-3 py-1 rounded', page === totalPages ? 'bg-gray-700 cursor-not-allowed' : 'bg-blue-600 hover:bg-blue-700']"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- 查看弹窗 -->
    <div
      v-if="selectedAlarm"
      class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50"
    >
      <div class="bg-[#1B1F3B] p-6 rounded-lg w-[450px] text-[15px] text-white">
        <h3 class="text-lg font-bold mb-4">告警详情</h3>
        <p><strong>创建时间：</strong>{{ formatDateTime(selectedAlarm.createdAt) }}</p>
        <p><strong>隧道名称：</strong>{{ tunnelNames[selectedAlarm.tunnelId] || '加载中...' }}</p>
        <p><strong>类型：</strong>{{ selectedAlarm.type }}</p>
        <p><strong>级别：</strong>{{ selectedAlarm.level }}</p>
        <p><strong>内容：</strong>{{ selectedAlarm.description }}</p>
        <p><strong>处理方式：</strong>{{ selectedAlarm.handleMethod || '-' }}</p>
        <p><strong>处理人：</strong>{{ selectedAlarm.handler || '-' }}</p>
        <p><strong>处理时间：</strong>{{ selectedAlarm.handleTime ? formatDateTime(selectedAlarm.handleTime) : '-' }}</p>
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
          <label class="block">
            <input type="radio" v-model="exportMode" value="all" class="mr-2" />
            全部数据
          </label>
          <label class="block">
            <input type="radio" v-model="exportMode" value="filtered" class="mr-2" />
            当前筛选结果
          </label>
          <label class="block">
            <input type="radio" v-model="exportMode" value="page" class="mr-2" />
            当前分页
          </label>
        </div>
        <div class="flex justify-end gap-4">
          <button
            class="bg-gray-500 px-4 py-1 rounded"
            @click="() => { console.log('取消导出'); showExportModal = false; }"
          >
            取消
          </button>
          <button 
            class="bg-blue-600 px-4 py-1 rounded" 
            @click="handleExport"
          >
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
      <div class="bg-[#1B1F3B] p-6 rounded-lg w-[800px] h-[600px]">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-xl font-bold">告警级别分布图</h3>
          <button
            @click="() => { showChartModal = false }"
            class="text-gray-400 hover:text-white"
          >
            ✕
          </button>
        </div>
        <div ref="chartRef" class="w-full h-[500px]"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from 'vue'
import warningApi from '@/api/warning'
import tunnelApi from '@/api/tunnel'
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
const tunnelNames = ref({}) // 存储隧道ID和名称的映射
const total = ref(0)
const totalPages = ref(1)

// 筛选条件
const filter = ref({
  level: '',
  type: '',
  keyword: ''
})

// 获取隧道名称
const fetchTunnelNames = async (tunnelIds) => {
  try {
    const uniqueIds = [...new Set(tunnelIds)]
    for (const id of uniqueIds) {
      const res = await tunnelApi.getTunnelById(id)
      if (res.data) {
        tunnelNames.value[id] = res.data.name
      }
    }
  } catch (error) {
    console.error('获取隧道名称失败：', error)
  }
}

// 获取预警列表
const fetchWarnings = async (retryCount = 0) => {
  try {
    if (retryCount === 0) {
      loading.value = true
    }
    const params = {
      page: page.value - 1, // Spring Boot 分页从0开始
      size: pageSize,
      type: filter.value.type || undefined,
      level: filter.value.level || undefined,
      status: filter.value.status || undefined,
      keyword: filter.value.keyword || undefined
    }
    console.log(`${retryCount > 0 ? `[重试${retryCount}] ` : ''}开始获取预警列表，请求参数:`, params)
    
    const res = await warningApi.getList(params)
    console.log('预警列表响应数据:', res.data)
    
    if (!res.data || !res.data.content) {
      throw new Error('响应数据格式错误')
    }
    
    alarms.value = res.data.content
    totalPages.value = res.data.totalPages
    total.value = res.data.totalElements

    // 获取所有告警中的隧道ID
    const tunnelIds = alarms.value.map(alarm => alarm.tunnelId)
    await fetchTunnelNames(tunnelIds)
    
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

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 修改导出 CSV 功能
const exportWarnings = () => {
  try {
    console.log('开始导出 CSV...')
    let dataToExport = []
    
    // 根据导出模式选择数据
    if (exportMode.value === 'all') {
      dataToExport = alarms.value
      console.log('导出全部数据，数据量：', dataToExport.length)
    } else if (exportMode.value === 'filtered') {
      dataToExport = alarms.value.filter(alarm => {
        return (!filter.value.level || alarm.level === filter.value.level) &&
               (!filter.value.type || alarm.type === filter.value.type) &&
               (!filter.value.keyword || alarm.description.includes(filter.value.keyword))
      })
      console.log('导出筛选数据，筛选条件：', filter.value)
      console.log('筛选后数据量：', dataToExport.length)
    } else if (exportMode.value === 'page') {
      dataToExport = paginatedAlarms.value
      console.log('导出当前页数据，数据量：', dataToExport.length)
    }

    console.log('准备生成 CSV 内容...')
    // 生成 CSV 内容
    const headers = ['创建时间', '类型', '级别', '内容', '处理方式', '处理人', '处理时间', '状态']
    let csvContent = '\ufeff' // 添加 BOM 标记，确保 Excel 正确识别中文
    
    // 添加表头
    csvContent += headers.join(',') + '\n'
    console.log('已添加表头：', headers)
    
    // 添加数据行
    dataToExport.forEach((alarm, index) => {
      const row = [
        alarm.createdAt ? formatDateTime(alarm.createdAt) : '',
        alarm.type || '',
        alarm.level || '',
        `"${(alarm.description || '').replace(/"/g, '""')}"`,
        `"${(alarm.handleMethod || '').replace(/"/g, '""')}"`,
        alarm.handler || '',
        alarm.handleTime ? formatDateTime(alarm.handleTime) : '',
        alarm.status || ''
      ]
      csvContent += row.join(',') + '\n'
      if (index === 0) {
        console.log('数据样例（第一行）：', row)
      }
    })

    console.log('CSV 内容生成完成，总行数：', dataToExport.length + 1)

    // 使用 Blob 创建文件
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8' })
    console.log('Blob 对象创建成功，大小：', blob.size, 'bytes')
    
    // 获取当前时间作为文件名的一部分
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-').slice(0, 19)
    const fileName = `预警记录_${timestamp}.csv`
    console.log('生成的文件名：', fileName)

    // 使用 uni.downloadFile 方法（小程序环境）
    if (typeof uni !== 'undefined') {
      console.log('检测到小程序环境，使用 uni.downloadFile...')
      // 创建临时文件 URL
      const tempUrl = URL.createObjectURL(blob)
      console.log('临时 URL 创建成功：', tempUrl)
      
      uni.downloadFile({
        url: tempUrl,
        success: (res) => {
          console.log('下载成功，状态码：', res.statusCode)
          if (res.statusCode === 200) {
            uni.saveFile({
              tempFilePath: res.tempFilePath,
              success: function (res) {
                console.log('文件保存成功，路径：', res.savedFilePath)
                ElMessage.success('文件已保存：' + res.savedFilePath)
              },
              fail: function(error) {
                console.error('保存文件失败：', error)
                ElMessage.error('保存文件失败')
              }
            })
          }
        },
        fail: (error) => {
          console.error('下载文件失败：', error)
          ElMessage.error('下载文件失败')
        },
        complete: () => {
          console.log('清理临时 URL...')
          URL.revokeObjectURL(tempUrl)
        }
      })
    } else {
      console.log('浏览器环境，使用标准下载方式...')
      // 浏览器环境
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = fileName
      console.log('下载链接创建成功：', link.href)
      
      document.body.appendChild(link)
      console.log('触发下载...')
      link.click()
      
      document.body.removeChild(link)
      URL.revokeObjectURL(link.href)
      console.log('清理完成')
    }

    showExportModal.value = false
    console.log('导出过程完成')
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出预警记录失败，详细错误：', error)
    console.error('错误堆栈：', error.stack)
    ElMessage.error('导出预警记录失败：' + error.message)
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
const chartRef = ref(null)

const drawChart = () => {
  console.log('开始绘制图表...')
  
  // 统计不同级别的告警数量
  const statistics = {
    严重: 0,
    中等: 0,
    轻微: 0
  }
  
  alarms.value.forEach(alarm => {
    if (alarm.level in statistics) {
      statistics[alarm.level]++
    }
  })
  
  console.log('告警统计数据：', statistics)
  
  // 确保DOM元素已经渲染
  nextTick(() => {
    const chartDom = chartRef.value
    if (!chartDom) {
      console.error('图表DOM元素未找到')
      return
    }
    
    console.log('初始化图表实例')
    const chart = echarts.init(chartDom)
    
    const option = {
      title: {
        text: '告警级别分布',
        left: 'center',
        top: 20,
        textStyle: {
          color: '#fff'
        }
      },
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c}条 ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        top: 'middle',
        textStyle: {
          color: '#fff'
        }
      },
      series: [
        {
          name: '告警级别',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: true,
            color: '#fff',
            formatter: '{b}\n{c}条 ({d}%)'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '20',
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: true
          },
          data: [
            { 
              value: statistics.严重, 
              name: '严重告警',
              itemStyle: { color: '#ff4d4f' }
            },
            { 
              value: statistics.中等, 
              name: '中等告警',
              itemStyle: { color: '#faad14' }
            },
            { 
              value: statistics.轻微, 
              name: '轻微告警',
              itemStyle: { color: '#52c41a' }
            }
          ]
        }
      ]
    }
    
    console.log('设置图表配置')
    chart.setOption(option)
    
    // 监听窗口大小变化，调整图表大小
    window.addEventListener('resize', () => {
      chart.resize()
    })
  })
}

// 分页相关
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
  page.value = 1 // 重置到第一页
  fetchWarnings()
}

const clearFilter = () => {
  filter.value = {
    level: '',
    type: '',
    keyword: ''
  }
  page.value = 1 // 重置到第一页
  fetchWarnings()
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
const openChart = () => {
  console.log('打开图表弹窗')
  showChartModal.value = true
  nextTick(() => {
    drawChart()
  })
}

// 导出相关
const handleExport = () => {
  console.log('点击导出按钮')
  console.log('当前导出模式：', exportMode.value)
  console.log('当前数据状态：', {
    总数据量: alarms.value?.length || 0,
    当前页数据量: paginatedAlarms.value?.length || 0,
    筛选条件: filter.value
  })
  
  if (!alarms.value || alarms.value.length === 0) {
    ElMessage.warning('没有可导出的数据')
    return
  }
  
  exportWarnings()
}

// 页面加载时获取数据
onMounted(() => {
  fetchWarnings()
})

// 添加paginatedAlarms计算属性
const paginatedAlarms = computed(() => {
  return alarms.value
})
</script>

<style scoped>
.form-input {
  @apply bg-[#0d122a] text-white rounded-md px-2 py-1 border border-gray-600 outline-none text-sm lg:text-base h-8 lg:h-10;
}

.form-input:focus {
  @apply border-blue-500;
}

/* 确保表格在小屏幕上可以水平滚动 */
.overflow-x-auto {
  @apply scrollbar-thin scrollbar-thumb-gray-600 scrollbar-track-transparent;
  -webkit-overflow-scrolling: touch;
}

/* 自定义滚动条样式 */
.scrollbar-thin::-webkit-scrollbar {
  height: 6px;
}

.scrollbar-thumb-gray-600::-webkit-scrollbar-thumb {
  background-color: #4B5563;
  border-radius: 3px;
}

.scrollbar-track-transparent::-webkit-scrollbar-track {
  background-color: transparent;
}

/* 表格内容超出时显示省略号 */
.truncate {
  @apply overflow-hidden text-ellipsis whitespace-nowrap;
}
</style>
