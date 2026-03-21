<template>
  <n-form size="small">
    <n-form-item>
      <n-radio v-model:checked="radioValue" :value="1"> 日，允许的通配符[, - * ? / L W] </n-radio>
    </n-form-item>
    <n-form-item>
      <n-radio v-model:checked="radioValue" :value="2"> 不指定 </n-radio>
    </n-form-item>
    <n-form-item>
      <n-radio v-model:checked="radioValue" :value="3">
        周期从
        <n-input-number v-model:value="cycle01" :min="1" :max="30" size="small" style="width: 80px" /> -
        <n-input-number v-model:value="cycle02" :min="cycle01 ? cycle01 + 1 : 2" :max="31" size="small" style="width: 80px" /> 日
      </n-radio>
    </n-form-item>
    <n-form-item>
      <n-radio v-model:checked="radioValue" :value="4">
        从
        <n-input-number v-model:value="average01" :min="1" :max="30" size="small" style="width: 80px" /> 号开始，每
        <n-input-number v-model:value="average02" :min="1" :max="31 - average01 || 1" size="small" style="width: 80px" /> 日执行一次
      </n-radio>
    </n-form-item>
    <n-form-item>
      <n-radio v-model:checked="radioValue" :value="5">
        每月
        <n-input-number v-model:value="workday" :min="1" :max="31" size="small" style="width: 80px" /> 号最近的那个工作日
      </n-radio>
    </n-form-item>
    <n-form-item>
      <n-radio v-model:checked="radioValue" :value="6"> 本月最后一天 </n-radio>
    </n-form-item>
    <n-form-item>
      <n-radio v-model:checked="radioValue" :value="7">
        指定
        <n-select
          v-model:value="checkboxList"
          multiple
          placeholder="可多选"
          style="width: 100%"
          :options="dayOptions"
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
const workday = ref(1)
const cycle01 = ref(1)
const cycle02 = ref(2)
const average01 = ref(1)
const average02 = ref(1)
const checkboxList = ref([])

const dayOptions = computed(() => {
  return Array.from({ length: 31 }, (_, i) => ({ label: i + 1, value: i + 1 }))
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
  const c1 = checkNum(cycle01.value, 1, 30)
  const c2 = checkNum(cycle02.value, c1 ? c1 + 1 : 2, 31)
  return `${c1}-${c2}`
})

const averageTotal = computed(() => {
  const a1 = checkNum(average01.value, 1, 30)
  const a2 = checkNum(average02.value, 1, 31 - a1 || 0)
  return `${a1}/${a2}`
})

const workdayCheck = computed(() => {
  return checkNum(workday.value, 1, 31)
})

const checkboxString = computed(() => {
  const str = checkboxList.value.join()
  return str === '' ? '*' : str
})

function radioChange() {
  if (radioValue.value !== 2 && props.cron?.week !== '?') {
    emit('update', 'week', '?', 'day')
  }
  switch (radioValue.value) {
    case 1:
      emit('update', 'day', '*')
      break
    case 2:
      emit('update', 'day', '?')
      break
    case 3:
      emit('update', 'day', cycleTotal.value)
      break
    case 4:
      emit('update', 'day', averageTotal.value)
      break
    case 5:
      emit('update', 'day', `${workdayCheck.value}W`)
      break
    case 6:
      emit('update', 'day', 'L')
      break
    case 7:
      emit('update', 'day', checkboxString.value)
      break
  }
}

watch(radioValue, radioChange)
watch(cycleTotal, () => {
  if (radioValue.value === 3) {
    emit('update', 'day', cycleTotal.value)
  }
})
watch(averageTotal, () => {
  if (radioValue.value === 4) {
    emit('update', 'day', averageTotal.value)
  }
})
watch(workdayCheck, () => {
  if (radioValue.value === 5) {
    emit('update', 'day', `${workdayCheck.value}W`)
  }
})
watch(checkboxString, () => {
  if (radioValue.value === 7) {
    emit('update', 'day', checkboxString.value)
  }
})
</script>
