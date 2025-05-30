import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import "./style.css";
// 导入Font Awesome样式
import "@fortawesome/fontawesome-free/css/all.min.css";
import "@fortawesome/fontawesome-free/js/all.min.js";

createApp(App).use(router).mount("#app");
