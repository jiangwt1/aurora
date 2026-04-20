import 'virtual:svg-icons-register'
import SvgIcon from '@/components/SvgIcon/index.vue'
import { App } from 'vue'

export const registerSvgIcon = (app: App): void => {
  app.component('svg-icon', SvgIcon)
}
