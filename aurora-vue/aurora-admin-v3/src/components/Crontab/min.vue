<template>
  <n-form size="small">
    <n-form-item>
      <n-radio v-model:checked="radioValue" :value="1"> 分钟，允许的通配符[, - * /] </n-radio>
    </n-form-item>
    <n-form-item>
      <n-radio v-model:checked="radioValue" :value="2">
        周期从
        <n-input-number v-model:value="cycle01" :min="0" :max="58" size="small" style="width: 80px" /> -
        <n-input-number v-model:value="cycle02" :min="cycle01 ? cycle01 + 1 : 1" :max="59" size="small" style="width: 80px" /> 分钟
      </n-radio>
    </n-form-item>
    <n-form-item>
      <n-radio v-model:checked="radioValue" :value="3">
        从
        <n-input-number v-model:value="average01" :min="0" :max="58" size="small" style="width: 80px" /> 分钟开始，每
        <n-input-number v-model:value="average02" :min="1" :max="59 - average01 || 0" size="small" style="width: 80px" /> 分钟执行一次
      </n-radio>
    </n-form-item>
    <n-form-item>
      <n-radio v-model:checked="radioValue" :value="4">
        指定
        <n-select
          v-model:value="checkboxList"
          multiple
          placeholder="可多选"
          style="width: 100%"
          :options="minOptions"
        />
      </n-radio>
    </n-form-item>
  </n-form>
</template>

<script setup>
import { ref, watch, computed } from 'vue'

const props = defineProps({
  check: Function,
  cron: Object
})

const emit = defineEmits(['update'])

const radioValue = ref(1)
const cycle01 = ref(1)
const cycle02 = ref(2)
const average01 = ref(0)
const average02 = ref(1)
const checkboxList = ref([])

const minOptions = computed(() => {
  return Array.from({ length: 60 }, (_, i) => ({ label: i, value: i }))
})

const checkNum = (value, minLimit, maxLimit) => {
  value = Math.floor(value)
  if (value < minLimit) {
    value = minLimit
  } else if (value > maxLimit) {
    value = maxLimit
  }
  return value
}

const cycleTotal = computed(() => {
  const c1 = checkNum(cycle01.value, 0, 58)
  const c2 = checkNum(cycle02.value, c1 ? c1 + 1 : 1, 59)
  return `${c1}-${c2}`
})

const averageTotal = computed(() => {
  const a1 = checkNum(average01.value, 0, 58)
  const a2 = checkNum(average02.value, 1, 59 - a1 || 0)
  return `${a1}/${a2}`
})

const checkboxString = computed(() => {
  const str = checkboxList.value.join()
  return str === '' ? '*' : str
})

function radioChange() {
  switch (radioValue.value) {
    case 1:
      emit('update', 'min', '*', 'min')
      break
    case 2:
      emit('update', 'min', cycleTotal.value)
      break
    case 3:
      emit('update', 'min', averageTotal.value)
      break
    case 4:
      emit('update', 'min', checkboxString.value)
      break
  }
}

watch(radioValue, radioChange)
watch(cycleTotal, () => {
  if (radioValue.value === 2) {
    emit('update', 'min', cycleTotal.value)
  }
})
watch(averageTotal, () => {
  if (radioValue.value === 3) {
    emit('update', 'min', averageTotal.value)
  }
})
watch(checkboxString, () => {
  if (radioValue.value === 4) {
    emit('update', 'min', checkboxString.value)
  }
})
</script>
