import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import "./style.css";
// 导入Font Awesome样式
import "@fortawesome/fontawesome-free/css/all.min.css";
import "@fortawesome/fontawesome-free/js/all.min.js";

// 导入 Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)
app.use(router)
app.use(ElementPlus)
app.mount("#app")
