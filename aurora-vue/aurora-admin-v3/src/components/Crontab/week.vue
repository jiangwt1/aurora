<template>
  <n-form size="small">
    <n-form-item>
      <n-radio v-model:checked="radioValue" :value="1"> 周，允许的通配符[, - * / / L #] </n-radio>
    </n-form-item>
    <n-form-item>
      <n-radio v-model:checked="radioValue" :value="2"> 不指定 </n-radio>
    </n-form-item>
    <n-form-item>
      <n-radio v-model:checked="radioValue" :value="3">
        周期从
        <n-input-number v-model:value="cycle01" :min="1" :max="6" size="small" style="width: 80px" /> -
        <n-input-number v-model:value="cycle02" :min="cycle01 ? cycle01 + 1 : 2" :max="7" size="small" style="width: 80px" />
      </n-radio>
    </n-form-item>
    <n-form-item>
      <n-radio v-model:checked="radioValue" :value="4">
        第
        <n-input-number v-model:value="average01" :min="1" :max="4" size="small" style="width: 80px" /> 周的
        <n-input-number v-model:value="average02" :min="1" :max="7" size="small" style="width: 80px" />
      </n-radio>
    </n-form-item>
    <n-form-item>
      <n-radio v-model:checked="radioValue" :value="5">
        本月最后一个
        <n-input-number v-model:value="weekday" :min="1" :max="7" size="small" style="width: 80px" />
      </n-radio>
    </n-form-item>
    <n-form-item>
      <n-radio v-model:checked="radioValue" :value="6">
        指定
        <n-select
          v-model:value="checkboxList"
          multiple
          placeholder="可多选"
          style="width: 100%"
          :options="weekOptions"
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
const weekday = ref(1)
const cycle01 = ref(1)
const cycle02 = ref(2)
const average01 = ref(1)
const average02 = ref(1)
const checkboxList = ref([])

const weekOptions = computed(() => {
  return [
    { label: '周日', value: 1 },
    { label: '周一', value: 2 },
    { label: '周二', value: 3 },
    { label: '周三', value: 4 },
    { label: '周四', value: 5 },
    { label: '周五', value: 6 },
    { label: '周六', value: 7 }
  ]
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
  const c1 = checkNum(cycle01.value, 1, 6)
  const c2 = checkNum(cycle02.value, c1 ? c1 + 1 : 2, 7)
  return `${c1}-${c2}`
})

const checkboxString = computed(() => {
  const str = checkboxList.value.join()
  return str === '' ? '*' : str
})

function radioChange() {
  if (radioValue.value !== 2 && props.cron?.day !== '?') {
    emit('update', 'day', '?', 'week')
  }
  switch (radioValue.value) {
    case 1:
      emit('update', 'week', '*')
      break
    case 2:
      emit('update', 'week', '?')
      break
    case 3:
      emit('update', 'week', cycleTotal.value)
      break
    case 4:
      emit('update', 'week', `${average01.value}#${average02.value}`)
      break
    case 5:
      emit('update', 'week', `${weekday.value}L`)
      break
    case 6:
      emit('update', 'week', checkboxString.value)
      break
  }
}

watch(radioValue, radioChange)
watch(cycleTotal, () => {
  if (radioValue.value === 3) {
    emit('update', 'week', cycleTotal.value)
  }
})
watch([average01, average02], () => {
  if (radioValue.value === 4) {
    emit('update', 'week', `${average01.value}#${average02.value}`)
  }
})
watch(weekday, () => {
  if (radioValue.value === 5) {
    emit('update', 'week', `${weekday.value}L`)
  }
})
watch(checkboxString, () => {
  if (radioValue.value === 6) {
    emit('update', 'week', checkboxString.value)
  }
})
</script>
