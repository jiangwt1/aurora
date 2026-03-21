import { defineConfig, presetUno, presetAttributify, presetIcons } from 'unocss'

export default defineConfig({
  presets: [
    presetUno(),
    presetAttributify(),
    presetIcons({
      scale: 1.2,
      warn: true
    })
  ],
  shortcuts: {
    // 布局
    'wh-full': 'w-full h-full',
    'flex-center': 'flex justify-center items-center',
    'flex-col': 'flex flex-col',
    'flex-between': 'flex justify-between items-center',
    'flex-col-center': 'flex flex-col justify-center items-center',
    'absolute-center': 'absolute left-0 top-0 flex justify-center items-center wh-full',

    // 间距
    'p-20': 'p-5',
    'mt-20': 'mt-5',
    'mb-20': 'mb-5',
    'ml-20': 'ml-5',
    'mr-20': 'mr-5',

    // 圆角
    'rounded-10': 'rounded-2.5'
  },
  theme: {
    colors: {
      primary: '#409EFF',
      success: '#67C23A',
      warning: '#E6A23C',
      danger: '#F56C6C',
      error: '#F56C6C',
      info: '#909399'
    }
  },
  rules: [
    [/^bg-hex-(.+)$/, ([, c]) => ({ 'background-color': `#${c}` })],
    [/^text-hex-(.+)$/, ([, c]) => ({ 'color': `#${c}` })],
    [/^border-hex-(.+)$/, ([, c]) => ({ 'border-color': `#${c}` })],
    [/^mt-(\d+)$/, ([, d]) => ({ 'margin-top': `${d / 4}rem` })],
    [/^mb-(\d+)$/, ([, d]) => ({ 'margin-bottom': `${d / 4}rem` })],
    [/^ml-(\d+)$/, ([, d]) => ({ 'margin-left': `${d / 4}rem` })],
    [/^mr-(\d+)$/, ([, d]) => ({ 'margin-right': `${d / 4}rem` })],
    [/^p-(\d+)$/, ([, d]) => ({ 'padding': `${d / 4}rem` })],
    [/^text-(\d+)$/, ([, d]) => ({ 'font-size': `${d / 4}rem` })]
  ]
})
