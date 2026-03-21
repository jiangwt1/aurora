import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

// 创建 pinia 实例
const pinia = createPinia()

// 使用持久化插件
pinia.use(piniaPluginPersistedstate)

// 设置状态管理
export function setupStore(app) {
  app.use(pinia)
}

export default pinia
