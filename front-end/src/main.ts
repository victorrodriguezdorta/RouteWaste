import { createApp } from 'vue'
import router from './adapter/vuejs/router/router'
import App from './App.vue'
import './style.css'

const app = createApp(App)
app.use(router)
app.mount('#app')       
