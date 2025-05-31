<template>
  <div class="p-6 bg-[#0D122A] min-h-screen text-white space-y-6">
    <h2 class="text-xl font-bold flex items-center">
      <i class="fas fa-video mr-2 text-orange-400"></i> 摄像头实时画面
    </h2>

    <!-- 分组展示 -->
    <div
      v-for="(group, gIndex) in cameraGroups"
      :key="gIndex"
      class="space-y-4"
    >
      <h3 class="text-lg font-semibold flex items-center mt-4">
        <i class="fas fa-map-marker-alt mr-2 text-blue-400"></i>{{ group.name }}
      </h3>

      <div class="grid grid-cols-3 gap-4">
        <div
          v-for="(cam, index) in group.cameras"
          :key="index"
          class="relative bg-[#1B1F3B] rounded-lg shadow-lg overflow-hidden group transition-all"
        >
          <!-- 操作按钮 -->
          <div class="absolute top-2 right-2 flex space-x-2 z-10">
            <button
              @click="refreshCamera(group, index)"
              class="text-white hover:text-blue-400"
            >
              <i class="fas fa-sync-alt"></i>
            </button>
            <button
              @click="fullscreen(group, index)"
              class="text-white hover:text-green-400"
            >
              <i class="fas fa-expand"></i>
            </button>
          </div>

          <!-- 摄像头画面或离线状态 -->
          <div class="relative">
            <video
              :src="cam.src"
              :alt="cam.name"
              class="w-full h-48 object-cover transition-all duration-300 cursor-pointer"
              :class="{ 'grayscale brightness-50': !cam.online }"
              @dblclick="fullscreen(group, index)"
              autoplay
              muted
              loop
              playsinline
            ></video>
            <!-- 离线遮罩 -->
            <div
              v-if="!cam.online"
              class="absolute inset-0 bg-black/50 flex items-center justify-center text-red-400 font-bold text-sm"
            >
              <i class="fas fa-times-circle mr-2"></i> 离线
            </div>
          </div>

          <!-- 摄像头信息栏 -->
          <div
            class="p-2 text-sm flex items-center justify-between bg-[#141a33]"
          >
            <div class="flex items-center space-x-2">
              <i
                :class="[
                  cam.online
                    ? 'fas fa-circle text-green-400'
                    : 'fas fa-circle text-gray-400',
                ]"
              ></i>
              <span>{{ cam.name }}</span>
            </div>
            <span class="text-gray-400">{{ cam.updated }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import cameraApi from '@/api/camera'
import { ElMessage } from 'element-plus'

// 数据响应式定义
const cameraGroups = ref([])
const loading = ref(false)

// 获取摄像头列表
const fetchCameraList = async () => {
  try {
    loading.value = true
    const res = await cameraApi.getList()
    // 将摄像头列表按隧道分组
    const groups = {}
    res.data.content.forEach(camera => {
      if (!groups[camera.tunnelId]) {
        groups[camera.tunnelId] = {
          name: `隧道${camera.tunnelId}`,
          cameras: []
        }
      }
      groups[camera.tunnelId].cameras.push({
        id: camera.id,
        name: camera.name,
        src: camera.streamUrl || `/api/admin/camera/${camera.id}/stream`,
        updated: new Date(camera.updatedAt).toLocaleTimeString(),
        online: camera.isOnline,
        status: camera.status,
        location: camera.location,
        lastMaintenanceTime: camera.lastMaintenanceTime,
        lastOnlineTime: camera.lastOnlineTime
      })
    })
    cameraGroups.value = Object.values(groups)
  } catch (error) {
    console.error('获取摄像头列表失败：', error)
    ElMessage.error('获取摄像头列表失败')
  } finally {
    loading.value = false
  }
}

// 获取RTSP地址
const getRtspUrl = async (cameraId) => {
  try {
    const res = await cameraApi.getRtspUrl(cameraId)
    return res.data
  } catch (error) {
    console.error('获取RTSP地址失败：', error)
    ElMessage.error('获取RTSP地址失败')
    return null
  }
}

// 更新摄像头状态
const updateCameraStatus = async (cameraId, isOnline) => {
  try {
    await cameraApi.updateOnlineStatus(cameraId, isOnline)
  } catch (error) {
    console.error('更新摄像头状态失败：', error)
    ElMessage.error('更新摄像头状态失败')
  }
}

// 控制云台
const controlPTZ = async (cameraId, command, speed = 1) => {
  try {
    await cameraApi.controlPTZ(cameraId, command, speed)
  } catch (error) {
    console.error('控制云台失败：', error)
    ElMessage.error('控制云台失败')
  }
}

// 刷新摄像头画面
const refreshCamera = async (group, index) => {
  const camera = group.cameras[index]
  try {
    const rtspUrl = await getRtspUrl(camera.id)
    if (rtspUrl) {
      camera.src = rtspUrl
      camera.updated = new Date().toLocaleTimeString()
    }
  } catch (error) {
    console.error('刷新摄像头画面失败：', error)
    ElMessage.error('刷新摄像头画面失败')
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchCameraList()
})

// 全屏显示
function fullscreen(group, index) {
  const flatCams = cameraGroups.value.flatMap((g) => g.cameras)
  const camIndex = flatCams.indexOf(group.cameras[index])
  const allVideos = document.querySelectorAll('video')
  const videoEl = allVideos[camIndex]
  if (videoEl?.requestFullscreen) {
    videoEl.requestFullscreen()
  }
}
</script>
