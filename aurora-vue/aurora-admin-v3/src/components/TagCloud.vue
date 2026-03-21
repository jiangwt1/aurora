<template>
  <div ref="wrapperRef" class="tag-cloud">
    <span
      v-for="(item, index) in data"
      :key="index"
      ref="tagRef"
      class="tag-item"
      @click="clickTag(item)"
      @dblclick="dbclickTag"
    >
      {{ item.name }}
    </span>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  },
  config: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['clickTag'])

const wrapperRef = ref(null)
const tagRef = ref([])
const tagList = ref([])
const timer = ref(null)

const defaultOption = {
  radius: 120,
  maxFont: 24,
  color: null,
  rotateAngleXbase: 500,
  rotateAngleYbase: 500,
  hover: false
}

const option = ref({ ...defaultOption })

if (props.config) {
  Object.assign(option.value, props.config)
}

let rotateAngleX = 0
let rotateAngleY = 0

function initTags() {
  // 清理旧数据
  tagList.value = []
  if (timer.value) {
    clearInterval(timer.value)
    timer.value = null
  }

  rotateAngleX = Math.PI / option.value.rotateAngleXbase
  rotateAngleY = Math.PI / option.value.rotateAngleYbase

  if (option.value.hover && wrapperRef.value) {
    wrapperRef.value.onmousemove = (e) => {
      rotateAngleY = (e.pageX - wrapperRef.value.offsetLeft - wrapperRef.value.offsetWidth / 2) / 10000
      rotateAngleX = -(e.pageY - wrapperRef.value.offsetTop - wrapperRef.value.offsetHeight / 2) / 10000
    }
  } else if (wrapperRef.value) {
    wrapperRef.value.onmousemove = null
  }

  for (let i = 0; i < props.data.length; i++) {
    const phi = Math.acos((2 * (i + 1) - 1) / props.data.length - 1)
    const theta = phi * Math.sqrt(props.data.length * Math.PI)
    const x = option.value.radius * Math.sin(phi) * Math.cos(theta)
    const y = option.value.radius * Math.sin(phi) * Math.sin(theta)
    const z = option.value.radius * Math.cos(phi)

    if (tagRef.value[i]) {
      if (option.value.color) {
        tagRef.value[i].style.color = option.value.color
      } else {
        tagRef.value[i].style.color = `rgb(${Math.round(Math.random() * 255)},${Math.round(Math.random() * 255)},${Math.round(Math.random() * 255)})`
      }

      tagList.value.push({ x, y, z, ele: tagRef.value[i] })
    }
  }

  timer.value = setInterval(() => {
    for (let i = 0; i < tagList.value.length; i++) {
      rotateX(tagList.value[i])
      rotateY(tagList.value[i])
      setPosition(tagList.value[i], option.value.radius, option.value.maxFont)
    }
  }, 20)
}

function setPosition(tag, radius, maxFont) {
  if (!wrapperRef.value) return
  tag.ele.style.transform = `translate(${tag.x + wrapperRef.value.offsetWidth / 2 - tag.ele.offsetWidth / 2}px, ${tag.y + wrapperRef.value.offsetHeight / 2 - tag.ele.offsetHeight / 2}px)`
  tag.ele.style.opacity = tag.z / radius / 2 + 0.7
  tag.ele.style.fontSize = (tag.z / radius / 2 + 0.5) * maxFont + 'px'
}

function rotateX(tag) {
  const cos = Math.cos(rotateAngleX)
  const sin = Math.sin(rotateAngleX)
  const y = tag.y * cos - tag.z * sin
  const z = tag.y * sin + tag.z * cos
  tag.y = y
  tag.z = z
}

function rotateY(tag) {
  const cos = Math.cos(rotateAngleY)
  const sin = Math.sin(rotateAngleY)
  const x = tag.z * sin + tag.x * cos
  const z = tag.z * cos - tag.x * sin
  tag.x = x
  tag.z = z
}

function dbclickTag() {
  if (timer.value) {
    clearInterval(timer.value)
    timer.value = null
  } else {
    timer.value = setInterval(() => {
      for (let i = 0; i < tagList.value.length; i++) {
        rotateX(tagList.value[i])
        rotateY(tagList.value[i])
        setPosition(tagList.value[i], option.value.radius, option.value.maxFont)
      }
    }, 20)
  }
}

function clickTag(item) {
  emit('clickTag', item)
}

onMounted(() => {
  initTags()
})

onBeforeUnmount(() => {
  if (timer.value) {
    clearInterval(timer.value)
    timer.value = null
  }
})

watch(() => props.data, () => {
  nextTick(() => {
    initTags()
  })
})
</script>

<style scoped>
.tag-cloud {
  width: 300px;
  height: 300px;
  position: relative;
  color: #333;
  margin: 0 auto;
  text-align: center;
}

.tag-item {
  position: absolute;
  top: 0;
  left: 0;
  color: #333;
  font-family: Arial;
  text-decoration: none;
  margin: 0 10px 15px 0;
  line-height: 18px;
  text-align: center;
  font-size: 16px;
  padding: 4px 9px;
  display: inline-block;
  border-radius: 3px;
  cursor: pointer;
  transition: color 0.3s;
}

.tag-item:hover {
  color: #409eff !important;
}
</style>
