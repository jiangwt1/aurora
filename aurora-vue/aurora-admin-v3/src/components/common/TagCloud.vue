<template>
  <div class="tag-cloud-wrapper" ref="containerRef">
    <!-- 背景光效 -->
    <div class="bg-glow"></div>
    <div class="bg-glow bg-glow-2"></div>
    <div class="bg-glow bg-glow-3"></div>

    <!-- 弹幕标签 -->
    <div
      v-for="(item, index) in barrageItems"
      :key="index"
      class="barrage-item"
      :class="[item.sizeClass, item.colorClass]"
      :style="{
        top: item.top + '%',
        animationDuration: item.duration + 's',
        animationDelay: item.delay + 's',
        '--float-y': item.floatY + 'px',
        '--rotate': item.rotate + 'deg',
        '--scale': item.scale
      }"
    >
      <span class="tag-icon">{{ item.icon }}</span>
      {{ item.name }}
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  }
})

const containerRef = ref(null)
const tags = ref([])

watch(
  () => props.data,
  (newData) => {
    tags.value = newData
  },
  { immediate: true }
)

// 小图标
const icons = ['✨', '🔥', '💫', '⭐', '🌟', '💎', '🎯', '🚀', '💡', '🎨']

// 生成弹幕项配置
const barrageItems = computed(() => {
  if (!tags.value.length) return []

  const sizeClasses = ['size-xl', 'size-lg', 'size-md', 'size-sm', 'size-xs']
  const colorClasses = ['color-purple', 'color-pink', 'color-blue', 'color-green', 'color-orange', 'color-cyan']
  const items = []

  // 每个标签生成3-4条弹幕
  tags.value.forEach((tag, tagIndex) => {
    const count = 3 + Math.floor(Math.random() * 2)
    for (let i = 0; i < count; i++) {
      items.push({
        name: tag.name,
        icon: icons[Math.floor(Math.random() * icons.length)],
        top: 3 + Math.random() * 90,
        duration: 5 + Math.random() * 8, // 5-13秒，更快
        delay: Math.random() * 8,
        floatY: -15 + Math.random() * 30,
        rotate: -8 + Math.random() * 16,
        scale: 0.95 + Math.random() * 0.2,
        sizeClass: sizeClasses[Math.floor(Math.random() * sizeClasses.length)],
        colorClass: colorClasses[Math.floor(Math.random() * colorClasses.length)]
      })
    }
  })

  return items
})
</script>

<style scoped>
.tag-cloud-wrapper {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  border-radius: 12px;
}

/* 背景光效 */
.bg-glow {
  position: absolute;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.4;
  animation: glowMove 15s ease-in-out infinite;
  pointer-events: none;
}

.bg-glow {
  background: #667eea;
  left: -100px;
  top: -100px;
}

.bg-glow-2 {
  background: #f093fb;
  right: -100px;
  bottom: -100px;
  left: auto;
  top: auto;
  animation-delay: -5s;
}

.bg-glow-3 {
  background: #4facfe;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  animation-delay: -10s;
}

@keyframes glowMove {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  25% {
    transform: translate(50px, 30px) scale(1.2);
  }
  50% {
    transform: translate(-30px, 60px) scale(0.9);
  }
  75% {
    transform: translate(40px, -20px) scale(1.1);
  }
}

/* 弹幕项 */
.barrage-item {
  position: absolute;
  left: 100%;
  white-space: nowrap;
  padding: 8px 20px;
  border-radius: 30px;
  font-weight: 600;
  cursor: default;
  animation: barrage 6s linear infinite, float 3s ease-in-out infinite;
  user-select: none;
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3),
              inset 0 1px 1px rgba(255, 255, 255, 0.2);
  transform: rotate(var(--rotate)) scale(var(--scale));
  letter-spacing: 0.5px;
}

.barrage-item:hover {
  animation-play-state: paused, paused;
  transform: rotate(0deg) scale(1.2) !important;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.4),
              0 0 30px currentColor;
  z-index: 100;
}

.tag-icon {
  margin-right: 6px;
  font-size: 1.1em;
  animation: iconPop 2s ease-in-out infinite;
}

@keyframes iconPop {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.3); }
}

@keyframes barrage {
  0% {
    left: 100%;
  }
  100% {
    left: -100%;
  }
}

@keyframes float {
  0%, 100% {
    transform: rotate(var(--rotate)) scale(var(--scale)) translateY(0);
  }
  50% {
    transform: rotate(var(--rotate)) scale(var(--scale)) translateY(var(--float-y));
  }
}

/* 尺寸 */
.barrage-item.size-xl {
  font-size: 17px;
  padding: 12px 28px;
}

.barrage-item.size-lg {
  font-size: 15px;
  padding: 10px 24px;
}

.barrage-item.size-md {
  font-size: 14px;
  padding: 8px 20px;
}

.barrage-item.size-sm {
  font-size: 13px;
  padding: 7px 18px;
}

.barrage-item.size-xs {
  font-size: 12px;
  padding: 6px 16px;
}

/* 颜色主题 */
.barrage-item.color-purple {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.9) 0%, rgba(118, 75, 162, 0.9) 100%);
  color: #fff;
  text-shadow: 0 2px 10px rgba(102, 126, 234, 0.5);
}

.barrage-item.color-pink {
  background: linear-gradient(135deg, rgba(240, 147, 251, 0.9) 0%, rgba(245, 87, 108, 0.9) 100%);
  color: #fff;
  text-shadow: 0 2px 10px rgba(240, 147, 251, 0.5);
}

.barrage-item.color-blue {
  background: linear-gradient(135deg, rgba(79, 172, 254, 0.9) 0%, rgba(0, 242, 254, 0.9) 100%);
  color: #fff;
  text-shadow: 0 2px 10px rgba(79, 172, 254, 0.5);
}

.barrage-item.color-green {
  background: linear-gradient(135deg, rgba(67, 233, 123, 0.9) 0%, rgba(56, 249, 215, 0.9) 100%);
  color: #fff;
  text-shadow: 0 2px 10px rgba(67, 233, 123, 0.5);
}

.barrage-item.color-orange {
  background: linear-gradient(135deg, rgba(250, 112, 154, 0.9) 0%, rgba(254, 225, 64, 0.9) 100%);
  color: #fff;
  text-shadow: 0 2px 10px rgba(250, 112, 154, 0.5);
}

.barrage-item.color-cyan {
  background: linear-gradient(135deg, rgba(0, 206, 209, 0.9) 0%, rgba(32, 178, 170, 0.9) 100%);
  color: #fff;
  text-shadow: 0 2px 10px rgba(0, 206, 209, 0.5);
}

/* 响应式 */
@media (max-width: 768px) {
  .bg-glow {
    width: 150px;
    height: 150px;
    filter: blur(50px);
  }

  .barrage-item.size-xl {
    font-size: 14px;
    padding: 9px 20px;
  }

  .barrage-item.size-lg {
    font-size: 13px;
    padding: 8px 18px;
  }

  .barrage-item.size-md {
    font-size: 12px;
    padding: 7px 16px;
  }

  .barrage-item.size-sm {
    font-size: 11px;
    padding: 6px 14px;
  }

  .barrage-item.size-xs {
    font-size: 10px;
    padding: 5px 12px;
  }
}
</style>
