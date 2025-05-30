<template>
  <Navbar />
  <router-view />
</template>

<script setup>
import Navbar from "@/components/Navbar.vue";
</script>

<style>
body {
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    "Helvetica Neue", Arial, sans-serif;
}
#app {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}
</style>
