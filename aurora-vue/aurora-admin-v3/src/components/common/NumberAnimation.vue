<template>
  <span>{{ animatedValue }}</span>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'

const props = defineProps({
  from: {
    type: Number,
    default: 0
  },
  to: {
    type: Number,
    required: true
  },
  duration: {
    type: Number,
    default: 1000
  }
})

const animatedValue = ref(props.from)
let animationFrame = null
let startTime = null

const animate = (timestamp) => {
  if (!startTime) startTime = timestamp
  const progress = Math.min((timestamp - startTime) / props.duration, 1)

  animatedValue.value = Math.floor(props.from + (props.to - props.from) * progress)

  if (progress < 1) {
    animationFrame = requestAnimationFrame(animate)
  }
}

onMounted(() => {
  animationFrame = requestAnimationFrame(animate)
})

watch(() => props.to, () => {
  if (animationFrame) {
    cancelAnimationFrame(animationFrame)
  }
  startTime = null
  animationFrame = requestAnimationFrame(animate)
})
</script>
