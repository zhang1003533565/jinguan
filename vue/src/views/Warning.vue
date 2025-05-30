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
import { ref, computed, nextTick } from "vue";
import * as echarts from "echarts";

const filter = ref({ level: "", type: "", keyword: "" });
const page = ref(1);
const pageSize = 10;
const showExportModal = ref(false);
const showChartModal = ref(false);
const exportMode = ref("page");
const chartRef = ref(null);
const selectedAlarm = ref(null);

const alarmList = ref([
  {
    id: 1,
    time: "2025-05-27 23:10",
    type: "设备",
    level: "高",
    content: "温度异常",
    status: "已处理",
    remark: "手动复位设备",
  },
  {
    id: 2,
    time: "2025-05-27 23:15",
    type: "环境",
    level: "中",
    content: "湿度波动较大",
    status: "未处理",
    remark: "",
  },
]);

const filteredAlarms = computed(() => {
  return alarmList.value.filter((a) => {
    return (
      (!filter.value.level || a.level === filter.value.level) &&
      (!filter.value.type || a.type === filter.value.type) &&
      (!filter.value.keyword || a.content.includes(filter.value.keyword))
    );
  });
});
const totalPages = computed(() =>
  Math.ceil(filteredAlarms.value.length / pageSize)
);
const paginatedAlarms = computed(() => {
  const start = (page.value - 1) * pageSize;
  return filteredAlarms.value.slice(start, start + pageSize);
});

function applyFilter() {
  page.value = 1;
}
function clearFilter() {
  filter.value = { level: "", type: "", keyword: "" };
  page.value = 1;
}
function prevPage() {
  if (page.value > 1) page.value--;
}
function nextPage() {
  if (page.value < totalPages.value) page.value++;
}
function viewDetail(alarm) {
  selectedAlarm.value = { ...alarm };
}
function saveRemarkAndClose() {
  const index = alarmList.value.findIndex(
    (a) => a.id === selectedAlarm.value.id
  );
  if (index !== -1) alarmList.value[index].remark = selectedAlarm.value.remark;
  selectedAlarm.value = null;
}
function markProcessed(alarm) {
  alarm.status = "已处理";
}
function markProcessedInModal() {
  const index = alarmList.value.findIndex(
    (a) => a.id === selectedAlarm.value.id
  );
  if (index !== -1) {
    alarmList.value[index].status = "已处理";
    alarmList.value[index].remark = selectedAlarm.value.remark;
  }
  selectedAlarm.value = null;
}
function handleExport() {
  let data = [];
  if (exportMode.value === "all") data = alarmList.value;
  else if (exportMode.value === "filtered") data = filteredAlarms.value;
  else data = paginatedAlarms.value;
  const csv = [
    ["时间", "类型", "级别", "内容", "状态", "备注"],
    ...data.map((a) => [
      a.time,
      a.type,
      a.level,
      a.content,
      a.status,
      a.remark,
    ]),
  ]
    .map((e) => e.join(","))
    .join("\n");
  const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
  const link = document.createElement("a");
  link.href = URL.createObjectURL(blob);
  link.setAttribute("download", "告警导出.csv");
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  showExportModal.value = false;
}
function openChart() {
  showChartModal.value = true;
  nextTick(() => {
    const chart = echarts.init(chartRef.value);
    const levelCount = { 高: 0, 中: 0, 低: 0 };
    filteredAlarms.value.forEach((a) => {
      levelCount[a.level] = (levelCount[a.level] || 0) + 1;
    });
    chart.setOption({
      backgroundColor: "#1B1F3B",
      tooltip: {},
      xAxis: {
        type: "category",
        data: ["高", "中", "低"],
        axisLabel: { color: "#fff" },
      },
      yAxis: { type: "value", axisLabel: { color: "#fff" } },
      series: [
        {
          type: "bar",
          data: [levelCount["高"], levelCount["中"], levelCount["低"]],
          itemStyle: {
            color: (p) => ["#ef4444", "#facc15", "#10b981"][p.dataIndex],
          },
        },
      ],
    });
  });
}
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
