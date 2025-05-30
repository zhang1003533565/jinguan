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
            <img
              :src="cam.src"
              :alt="cam.name"
              class="w-full h-48 object-cover transition-all duration-300 cursor-pointer"
              :class="{ 'grayscale brightness-50': !cam.online }"
              @dblclick="fullscreen(group, index)"
            />
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
import { reactive } from "vue";

const getTime = () => new Date().toLocaleTimeString();

const cameraGroups = reactive([
  {
    name: "隧道 A 段",
    cameras: [
      {
        name: "A-1",
        src: "https://placehold.co/400x240?text=A-1",
        updated: getTime(),
        online: true,
      },
      {
        name: "A-2",
        src: "https://placehold.co/400x240?text=A-2",
        updated: getTime(),
        online: false,
      },
      {
        name: "A-3",
        src: "https://placehold.co/400x240?text=A-3",
        updated: getTime(),
        online: true,
      },
    ],
  },
  {
    name: "隧道 B 段",
    cameras: [
      {
        name: "B-1",
        src: "https://placehold.co/400x240?text=B-1",
        updated: getTime(),
        online: true,
      },
      {
        name: "B-2",
        src: "https://placehold.co/400x240?text=B-2",
        updated: getTime(),
        online: true,
      },
      {
        name: "B-3",
        src: "https://placehold.co/400x240?text=B-3",
        updated: getTime(),
        online: false,
      },
    ],
  },
]);

function refreshCamera(group, index) {
  const cam = group.cameras[index];
  cam.src = cam.src.split("?")[0] + `?t=${Date.now()}`;
  cam.updated = getTime();
}

function fullscreen(group, index) {
  const flatCams = cameraGroups.flatMap((g) => g.cameras);
  const camIndex = flatCams.indexOf(group.cameras[index]);
  const allImgs = document.querySelectorAll("img");
  const imgEl = allImgs[camIndex];
  if (imgEl?.requestFullscreen) {
    imgEl.requestFullscreen();
  }
}
</script>
