import { createApp } from 'vue'
import { setupRouter } from './router'
import { setupStore } from './store'
import App from './App.vue'

// 引入样式
import './styles/reset.css'
import 'uno.css'
import './styles/global.scss'
import './styles/naive.scss'
import 'animate.css'
import 'virtual:svg-icons-register'

// 引入 ECharts (使用正确的导入方式)
import ECharts from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import {
  LineChart,
  BarChart,
  PieChart,
  MapChart
} from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'

// 注册 ECharts 组件
use([
  CanvasRenderer,
  LineChart,
  BarChart,
  PieChart,
  MapChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

// 引入图标
import { Icon } from '@iconify/vue'

async function setupApp() {
  const app = createApp(App)

  // 注册全局组件
  app.component('v-chart', ECharts)
  app.component('Icon', Icon)

  // 设置路由和状态管理
  setupStore(app)
  await setupRouter(app)

  app.mount('#app')
}

setupApp()
