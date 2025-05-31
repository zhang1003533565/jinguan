<template>
  <div class="flex h-screen bg-[#0D122A] text-white">
    <!-- 侧边栏 -->
    <aside class="w-[260px] bg-[#1B1F3B] p-4 overflow-y-auto">
      <h2 class="text-lg font-bold flex items-center mb-4">
        <i class="fas fa-layer-group mr-2 text-orange-400"></i> 设备分类
      </h2>
      <div class="relative mb-4">
        <i
          class="fas fa-search absolute left-2 top-2.5 text-gray-400 text-sm"
        ></i>
        <input
          v-model="search"
          type="text"
          placeholder="搜索设备..."
          class="pl-8 pr-2 py-1 w-full rounded bg-[#0D122A] text-sm placeholder-gray-400"
        />
      </div>

      <div v-for="(group, index) in filteredGroups" :key="index" class="mb-4">
        <div
          class="flex items-center justify-between cursor-pointer mb-1 text-sm font-semibold"
          @click="toggleGroup(index)"
        >
          <span>{{ group.name }}</span>
          <i
            :class="[
              'fas transition-transform',
              expandedGroups[index] ? 'fa-chevron-down' : 'fa-chevron-right',
            ]"
          ></i>
        </div>
        <ul v-show="expandedGroups[index]" class="space-y-1 pl-3">
          <li
            v-for="device in group.devices"
            :key="device.id"
            @click="selectDevice(device)"
            :class="[
              selectedDevice?.id === device.id ? 'bg-blue-600' : '',
              'cursor-pointer px-2 py-1 rounded hover:bg-blue-800 text-sm flex items-center',
            ]"
          >
            <i
              class="fas fa-circle mr-2"
              :class="
                device.status === 'online' ? 'text-green-400' : 'text-gray-400'
              "
            ></i>
            {{ device.name }}
          </li>
        </ul>
      </div>
    </aside>

    <!-- 主体区域 -->
    <main class="flex-1 p-6 space-y-4 overflow-auto">
      <!-- 顶部标题栏 -->
      <div class="flex justify-between items-center">
        <h2 class="text-xl font-bold flex items-center">
          <i class="fas fa-cogs mr-2 text-orange-400"></i> 设备管理
        </h2>
        <button
          @click="openAddModal"
          class="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded text-sm"
        >
          <i class="fas fa-plus mr-1"></i> 添加设备
        </button>
      </div>

      <!-- 设备详情 -->
      <div class="bg-[#1B1F3B] rounded-lg p-6 min-h-[160px] text-[15px]">
        <h3 class="font-bold text-lg mb-3">设备详情</h3>
        <div
          v-if="selectedDevice"
          class="grid grid-cols-2 gap-x-10 gap-y-2 text-lg leading-relaxed"
        >
          <div><strong>设备名称：</strong> {{ selectedDevice.name }}</div>
          <div><strong>编号：</strong> {{ selectedDevice.code }}</div>
          <div><strong>位置：</strong> {{ selectedDevice.location }}</div>
          <div><strong>分类：</strong> {{ selectedDevice.category }}</div>
          <div><strong>权限：</strong> {{ selectedDevice.permission }}</div>
          <div>
            <strong>备注：</strong> {{ selectedDevice.remarks || "无" }}
          </div>
          <div class="col-span-2">
            <strong>状态：</strong>
            <span
              :class="
                selectedDevice.status === 'online'
                  ? 'text-green-400'
                  : 'text-red-400'
              "
            >
              {{ selectedDevice.status }}
            </span>
          </div>
          <div class="col-span-2 mt-4 flex justify-end space-x-3">
            <button class="bg-blue-600 px-3 py-1 rounded">远程重启</button>
            <button class="bg-green-600 px-3 py-1 rounded">导出日志</button>
            <button
              class="bg-yellow-500 px-3 py-1 rounded"
              @click="openEditModal"
            >
              编辑设备
            </button>
            <button class="bg-red-600 px-3 py-1 rounded" @click="removeDeviceFromGroup">
              删除设备
            </button>
          </div>
        </div>
        <div v-else class="text-gray-400">请选择左侧设备查看详情</div>
      </div>

      <!-- 日志 + 图表 -->
      <div class="grid grid-cols-2 gap-4">
        <div class="bg-[#1B1F3B] rounded-lg p-4 min-h-[280px] text-[15px]">
          <div class="flex justify-between items-center mb-2">
            <h3 class="font-bold">运行日志记录</h3>
            <div class="text-sm space-x-4 text-gray-400">
              <span><i class="fas fa-circle text-gray-400 mr-1"></i>正常</span>
              <span
                ><i class="fas fa-circle text-yellow-400 mr-1"></i>预警</span
              >
              <span><i class="fas fa-circle text-red-500 mr-1"></i>异常</span>
            </div>
          </div>
          <table class="w-full text-sm">
            <thead class="text-gray-300 border-b border-gray-600">
              <tr>
                <th class="py-1 px-2 w-[30%] text-left">时间</th>
                <th class="px-2 w-[20%] text-left">类型</th>
                <th class="px-2 w-[50%] text-left">内容</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="log in paginatedLogs"
                :key="log.time + log.content"
                :class="[
                  'border-b border-gray-700',
                  log.type === '异常'
                    ? 'text-red-400'
                    : log.type === '预警'
                    ? 'text-yellow-400'
                    : 'text-gray-300',
                ]"
              >
                <td class="px-2 py-1">{{ log.time }}</td>
                <td class="px-2 py-1">{{ log.type }}</td>
                <td class="px-2 py-1">{{ log.content }}</td>
              </tr>
            </tbody>
          </table>
          <div
            v-if="selectedDevice?.logs.length > 5"
            class="flex justify-center mt-2 text-sm text-gray-400"
          >
            <span
              class="cursor-pointer"
              @click="currentPage = Math.max(1, currentPage - 1)"
              >«</span
            >
            <span class="mx-2">第 {{ currentPage }} 页</span>
            <span class="cursor-pointer" @click="currentPage++">»</span>
          </div>
        </div>

        <!-- 图表 -->
        <div class="bg-[#1B1F3B] rounded-lg p-4 min-h-[280px]">
          <div class="flex justify-between items-center mb-2">
            <h3 class="font-bold">设备数据图表</h3>
            <select
              v-model="chartType"
              class="bg-[#0D122A] text-sm p-1 rounded"
            >
              <option value="line">折线图</option>
              <option value="bar">柱状图</option>
              <option value="pie">饼图</option>
            </select>
          </div>
          <div id="deviceChart" class="h-48"></div>
        </div>
      </div>
    </main>

    <!-- 添加/编辑弹窗 -->
    <div
      v-if="modalVisible"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    >
      <div class="bg-[#1B1F3B] p-6 rounded-lg w-[400px]">
        <h3 class="text-lg font-bold mb-4">
          {{ isEditing ? "编辑设备" : "添加设备" }}
        </h3>
        <div class="space-y-3 text-sm">
          <input
            v-model="modalForm.name"
            placeholder="设备名称"
            class="form-input"
          />
          <input
            v-model="modalForm.code"
            placeholder="设备编号"
            class="form-input"
          />
          <select v-model="modalForm.location" class="form-input">
            <option disabled value="">选择位置</option>
            <option>A区</option>
            <option>B区</option>
          </select>
          <select v-model="modalForm.category" class="form-input">
            <option disabled value="">选择分类</option>
            <option>环境监测类</option>
            <option>安全监测类</option>
          </select>
          <select v-model="modalForm.permission" class="form-input">
            <option disabled value="">权限</option>
            <option>所有人</option>
            <option>管理员</option>
          </select>
          <select v-model="modalForm.status" class="form-input">
            <option disabled value="">选择状态</option>
            <option value="online">在线</option>
            <option value="offline">离线</option>
          </select>
          <textarea
            v-model="modalForm.remarks"
            rows="3"
            placeholder="备注（可选）"
            class="form-input"
          ></textarea>
        </div>
        <div class="mt-4 flex justify-end space-x-2">
          <button @click="closeModal" class="px-3 py-1 bg-gray-600 rounded">
            取消
          </button>
          <button @click="saveDevice" class="px-3 py-1 bg-green-600 rounded">
            保存
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from "vue";
import * as echarts from "echarts";
import deviceApi from '@/api/device'
import { ElMessage } from 'element-plus'

const search = ref("");
const currentPage = ref(1);
const chartType = ref("line");

const modalVisible = ref(false);
const isEditing = ref(false);
const selectedDevice = ref(null);
const expandedGroups = ref([true, true]);

const modalForm = ref({
  name: "",
  code: "",
  location: "",
  category: "",
  permission: "",
  status: "offline",
  remarks: "",
});

const deviceGroups = ref([
  {
    name: "环境监测类",
    devices: [
      {
        id: 1,
        name: "温湿度传感器",
        code: "A-1",
        location: "A区",
        category: "环境监测类",
        status: "online",
        permission: "所有人",
        remarks: "定期校准",
        logs: [
          { time: "2025-05-27 15:00", type: "正常", content: "设备启动" },
          { time: "2025-05-27 15:20", type: "预警", content: "温度略高" },
          { time: "2025-05-27 15:40", type: "异常", content: "温湿中断" },
          { time: "2025-05-27 16:00", type: "正常", content: "恢复连接" },
          { time: "2025-05-27 16:20", type: "正常", content: "数据上传成功" },
        ],
      },
    ],
  },
  {
    name: "安全监测类",
    devices: [
      {
        id: 2,
        name: "瓦斯检测仪",
        code: "B-1",
        location: "B区",
        category: "安全监测类",
        status: "offline",
        permission: "管理员",
        remarks: "",
        logs: [{ time: "2025-05-27 22:00", type: "异常", content: "通信中断" }],
      },
    ],
  },
]);

const filteredGroups = computed(() =>
  deviceGroups.value.map((g) => ({
    ...g,
    devices: g.devices.filter((d) => d.name.includes(search.value)),
  }))
);

const paginatedLogs = computed(() =>
  selectedDevice.value ? selectedDevice.value.logs.slice(0, 5) : []
);

function toggleGroup(index) {
  expandedGroups.value[index] = !expandedGroups.value[index];
}

function selectDevice(device) {
  selectedDevice.value = device;
  drawChart();
}

function openAddModal() {
  modalForm.value = {
    name: "",
    code: "",
    location: "",
    category: "",
    permission: "",
    status: "offline",
    remarks: "",
  };
  isEditing.value = false;
  modalVisible.value = true;
}

function openEditModal() {
  if (!selectedDevice.value) return;
  modalForm.value = { ...selectedDevice.value };
  isEditing.value = true;
  modalVisible.value = true;
}

function closeModal() {
  modalVisible.value = false;
}

function saveDevice() {
  const form = modalForm.value;
  if (isEditing.value) {
    Object.assign(selectedDevice.value, form);
  } else {
    deviceGroups.value[0].devices.push({
      ...form,
      id: Date.now(),
      logs: [],
    });
  }
  modalVisible.value = false;
}

function removeDeviceFromGroup() {
  if (!selectedDevice.value) return;
  deviceGroups.value.forEach((group) => {
    group.devices = group.devices.filter(
      (d) => d.id !== selectedDevice.value.id
    );
  });
  selectedDevice.value = null;
}

function drawChart() {
  nextTick(() => {
    const dom = document.getElementById("deviceChart");
    if (!dom || !selectedDevice.value) return;
    const chart = echarts.init(dom);
    chart.setOption({
      tooltip: {},
      xAxis: {
        type: "category",
        data: ["15:00", "15:20", "15:40", "16:00", "16:20"],
      },
      yAxis: { type: "value" },
      series: [
        {
          name: "温度",
          type: chartType.value,
          data: [26, 29, 30, 27, 28],
          itemStyle: { color: "#f87171" },
        },
        {
          name: "湿度",
          type: chartType.value,
          data: [60, 62, 58, 65, 63],
          itemStyle: { color: "#34d399" },
        },
        {
          name: "瓦斯",
          type: chartType.value,
          data: [0.2, 0.3, 0.5, 0.4, 0.3],
          itemStyle: { color: "#60a5fa" },
        },
      ],
      grid: { left: "10%", right: "10%", bottom: "12%" },
    });
  });
}

onMounted(drawChart);

// 数据响应式定义
const deviceList = ref([])
const loading = ref(false)

// 获取设备列表
const fetchDeviceList = async () => {
  try {
    loading.value = true
    const res = await deviceApi.getList()
    deviceList.value = res.data
  } catch (error) {
    console.error('获取设备列表失败：', error)
    ElMessage.error('获取设备列表失败')
  } finally {
    loading.value = false
  }
}

// 获取设备详情
const fetchDeviceDetail = async (id) => {
  try {
    const res = await deviceApi.getById(id)
    selectedDevice.value = res.data
  } catch (error) {
    console.error('获取设备详情失败：', error)
    ElMessage.error('获取设备详情失败')
  }
}

// 添加设备
const addDevice = async (deviceData) => {
  try {
    await deviceApi.create(deviceData)
    ElMessage.success('添加设备成功')
    await fetchDeviceList()
  } catch (error) {
    console.error('添加设备失败：', error)
    ElMessage.error('添加设备失败')
  }
}

// 更新设备
const updateDevice = async (id, deviceData) => {
  try {
    await deviceApi.update(id, deviceData)
    ElMessage.success('更新设备成功')
    await fetchDeviceDetail(id)
  } catch (error) {
    console.error('更新设备失败：', error)
    ElMessage.error('更新设备失败')
  }
}

// 删除设备
const deleteDevice = async (id) => {
  try {
    await deviceApi.delete(id)
    ElMessage.success('删除设备成功')
    selectedDevice.value = null
    await fetchDeviceList()
  } catch (error) {
    console.error('删除设备失败：', error)
    ElMessage.error('删除设备失败')
  }
}

// 更新设备状态
const updateDeviceStatus = async (id, status) => {
  try {
    await deviceApi.updateStatus(id, status)
    await fetchDeviceDetail(id)
  } catch (error) {
    console.error('更新设备状态失败：', error)
    ElMessage.error('更新设备状态失败')
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchDeviceList()
})
</script>

<style scoped>
.form-input {
  width: 100%;
  padding: 0.5rem;
  border-radius: 0.25rem;
  background-color: #0d122a;
  color: white;
  outline: none;
  font-size: 14px;
}
.form-input:focus {
  outline: 1px solid #3b82f6;
}
</style>
