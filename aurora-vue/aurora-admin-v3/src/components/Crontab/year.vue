<template>
  <n-form size="small">
    <n-form-item>
      <n-radio v-model:checked="radioValue" :value="1"> 不填写 </n-radio>
    </n-form-item>
    <n-form-item>
      <n-radio v-model:checked="radioValue" :value="2"> 年 </n-radio>
    </n-form-item>
    <n-form-item>
      <n-radio v-model:checked="radioValue" :value="3">
        周期从
        <n-input-number v-model:value="cycle01" :min="2000" :max="3000" size="small" style="width: 100px" /> -
        <n-input-number v-model:value="cycle02" :min="cycle01 ? cycle01 + 1 : 2001" :max="3000" size="small" style="width: 100px" />
      </n-radio>
    </n-form-item>
    <n-form-item>
      <n-radio v-model:checked="radioValue" :value="4">
        从
        <n-input-number v-model:value="average01" :min="2000" :max="3000" size="small" style="width: 100px" /> 年开始，每
        <n-input-number v-model:value="average02" :min="1" :max="100" size="small" style="width: 80px" /> 年执行一次
      </n-radio>
    </n-form-item>
    <n-form-item>
      <n-radio v-model:checked="radioValue" :value="5">
        指定
        <n-select
          v-model:value="checkboxList"
          multiple
          placeholder="可多选"
          style="width: 100%"
          :options="yearOptions"
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
const cycle01 = ref(new Date().getFullYear())
const cycle02 = ref(new Date().getFullYear() + 1)
const average01 = ref(new Date().getFullYear())
const average02 = ref(1)
const checkboxList = ref([])

const yearOptions = computed(() => {
  const currentYear = new Date().getFullYear()
  return Array.from({ length: 100 }, (_, i) => ({ label: currentYear + i, value: currentYear + i }))
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
  const c1 = checkNum(cycle01.value, 2000, 3000)
  const c2 = checkNum(cycle02.value, c1 ? c1 + 1 : 2001, 3000)
  return `${c1}-${c2}`
})

const averageTotal = computed(() => {
  const a1 = checkNum(average01.value, 2000, 3000)
  const a2 = checkNum(average02.value, 1, 100)
  return `${a1}/${a2}`
})

const checkboxString = computed(() => {
  const str = checkboxList.value.join()
  return str === '' ? '*' : str
})

function radioChange() {
  switch (radioValue.value) {
    case 1:
      emit('update', 'year', '')
      break
    case 2:
      emit('update', 'year', '*')
      break
    case 3:
      emit('update', 'year', cycleTotal.value)
      break
    case 4:
      emit('update', 'year', averageTotal.value)
      break
    case 5:
      emit('update', 'year', checkboxString.value)
      break
  }
}

watch(radioValue, radioChange)
watch(cycleTotal, () => {
  if (radioValue.value === 3) {
    emit('update', 'year', cycleTotal.value)
  }
})
watch(averageTotal, () => {
  if (radioValue.value === 4) {
    emit('update', 'year', averageTotal.value)
  }
})
watch(checkboxString, () => {
  if (radioValue.value === 5) {
    emit('update', 'year', checkboxString.value)
  }
})
</script>
