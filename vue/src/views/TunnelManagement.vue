<template>
  <div class="p-6 bg-[#0D122A] min-h-screen text-white">
    <!-- 顶部操作栏 -->
    <div class="flex justify-between items-center mb-6">
      <div class="flex items-center space-x-4">
        <h1 class="text-2xl font-bold">隧道管理</h1>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索隧道..."
          class="w-64"
          @input="handleSearch"
        >
          <template #prefix>
            <i class="fas fa-search text-gray-400"></i>
          </template>
        </el-input>
      </div>
      <el-button type="primary" @click="showAddDialog">
        <i class="fas fa-plus mr-2"></i>添加隧道
      </el-button>
    </div>

    <!-- 隧道列表 -->
    <div class="grid grid-cols-3 gap-4">
      <div
        v-for="tunnel in tunnels"
        :key="tunnel.id"
        class="bg-dark-blue-light border border-blue-800 rounded-lg p-4 shadow-xl"
      >
        <div class="flex justify-between items-start mb-4">
          <h2 class="text-xl font-bold">{{ tunnel.name }}</h2>
          <div class="flex space-x-2">
            <el-tag :type="getStatusType(tunnel.status)">
              {{ getStatusText(tunnel.status) }}
            </el-tag>
          </div>
        </div>
        
        <div class="space-y-2 text-gray-300">
          <p>{{ tunnel.description }}</p>
          <div class="grid grid-cols-2 gap-4 mt-4">
            <div>
              <p class="text-sm text-gray-400">位置</p>
              <p>{{ tunnel.location }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-400">尺寸</p>
              <p>{{ tunnel.length }}m × {{ tunnel.width }}m × {{ tunnel.height }}m</p>
            </div>
          </div>
        </div>

        <div class="flex justify-end space-x-2 mt-4">
          <el-button type="info" size="small" @click="handleEdit(tunnel)">
            <i class="fas fa-edit mr-1"></i>编辑
          </el-button>
          <el-button type="danger" size="small" @click="handleDelete(tunnel)">
            <i class="fas fa-trash-alt mr-1"></i>删除
          </el-button>
        </div>
      </div>
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      :title="isEdit ? '编辑隧道' : '添加隧道'"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input type="textarea" v-model="form.description" />
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="form.location" />
        </el-form-item>
        <el-form-item label="长度(m)">
          <el-input-number v-model="form.length" :min="0" />
        </el-form-item>
        <el-form-item label="宽度(m)">
          <el-input-number v-model="form.width" :min="0" />
        </el-form-item>
        <el-form-item label="高度(m)">
          <el-input-number v-model="form.height" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="运行中" value="active" />
            <el-option label="已停用" value="inactive" />
            <el-option label="维护中" value="maintenance" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="flex justify-end space-x-2">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import tunnelApi from '@/api/tunnel'

const tunnels = ref([])
const searchKeyword = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({
  name: '',
  description: '',
  location: '',
  length: 0,
  width: 0,
  height: 0,
  status: 'active'
})

// 获取隧道列表
const fetchTunnels = async () => {
  try {
    const res = await tunnelApi.getTunnelList()
    tunnels.value = res.data
  } catch (error) {
    ElMessage.error('获取隧道列表失败')
  }
}

// 搜索隧道
const handleSearch = async () => {
  if (!searchKeyword.value) {
    await fetchTunnels()
    return
  }
  try {
    const res = await tunnelApi.searchTunnels(searchKeyword.value)
    tunnels.value = res.data
  } catch (error) {
    ElMessage.error('搜索隧道失败')
  }
}

// 显示添加对话框
const showAddDialog = () => {
  isEdit.value = false
  form.value = {
    name: '',
    description: '',
    location: '',
    length: 0,
    width: 0,
    height: 0,
    status: 'active'
  }
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (tunnel) => {
  isEdit.value = true
  form.value = { ...tunnel }
  dialogVisible.value = true
}

// 处理删除
const handleDelete = async (tunnel) => {
  try {
    await ElMessageBox.confirm('确定要删除该隧道吗？', '提示', {
      type: 'warning'
    })
    await tunnelApi.deleteTunnel(tunnel.id)
    ElMessage.success('删除成功')
    await fetchTunnels()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 处理提交
const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await tunnelApi.updateTunnel(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await tunnelApi.createTunnel(form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    await fetchTunnels()
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
  }
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    active: 'success',
    inactive: 'info',
    maintenance: 'warning'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    active: '运行中',
    inactive: '已停用',
    maintenance: '维护中'
  }
  return texts[status] || status
}

onMounted(() => {
  fetchTunnels()
})
</script> 