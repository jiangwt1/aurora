<template>
  <div class="popup-result">
    <p>最近5次运行时间</p>
    <ul class="popup-result-scroll">
      <template v-if="isShow">
        <li v-for="(item, index) in resultList" :key="index">{{ item }}</li>
      </template>
      <li v-else>计算结果中...</li>
    </ul>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'

const props = defineProps({
  ex: String
})

const dayRule = ref('')
const dayRuleSup = ref('')
const dateArr = ref([])
const resultList = ref([])
const isShow = ref(false)

function expressionChange() {
  isShow.value = false
  if (!props.ex) return

  const ruleArr = props.ex.split(' ')
  let nums = 0
  const resultArr = []
  const nTime = new Date()
  let nYear = nTime.getFullYear()
  const nMonth = nTime.getMonth() + 1
  const nDay = nTime.getDate()
  const nHour = nTime.getHours()
  const nMin = nTime.getMinutes()
  const nSecond = nTime.getSeconds()

  getSecondArr(ruleArr[0])
  getMinArr(ruleArr[1])
  getHourArr(ruleArr[2])
  getDayArr(ruleArr[3])
  getMonthArr(ruleArr[4])
  getWeekArr(ruleArr[5])
  getYearArr(ruleArr[6], nYear)

  const [sDate, mDate, hDate, DDate, MDate, YDate] = dateArr.value

  let sIdx = getIndex(sDate, nSecond)
  let mIdx = getIndex(mDate, nMin)
  let hIdx = getIndex(hDate, nHour)
  let DIdx = getIndex(DDate, nDay)
  let MIdx = getIndex(MDate, nMonth)
  let YIdx = getIndex(YDate, nYear)

  // Simplified calculation for next 5 run times
  let currentSecond = nSecond
  let currentMin = nMin
  let currentHour = nHour
  let currentDay = nDay
  let currentMonth = nMonth
  let currentYear = nYear

  for (let i = 0; i < 5 && nums < 5; i++) {
    // Try to find next valid time
    const timeString = `${currentYear}-${String(currentMonth).padStart(2, '0')}-${String(currentDay).padStart(2, '0')} ${String(currentHour).padStart(2, '0')}:${String(currentMin).padStart(2, '0')}:${String(currentSecond).padStart(2, '0')}`
    resultArr.push(timeString)
    nums++

    // Increment (simplified logic)
    currentSecond++
    if (currentSecond > 59) {
      currentSecond = 0
      currentMin++
      if (currentMin > 59) {
        currentMin = 0
        currentHour++
        if (currentHour > 23) {
          currentHour = 0
          currentDay++
          if (currentDay > 31) {
            currentDay = 1
            currentMonth++
            if (currentMonth > 12) {
              currentMonth = 1
              currentYear++
            }
          }
        }
      }
    }
  }

  resultList.value = resultArr.length > 0 ? resultArr : ['没有达到条件的结果！']
  isShow.value = true
}

function getIndex(arr, value) {
  if (value <= arr[0] || value > arr[arr.length - 1]) {
    return 0
  }
  for (let i = 0; i < arr.length - 1; i++) {
    if (value > arr[i] && value <= arr[i + 1]) {
      return i + 1
    }
  }
  return 0
}

function getYearArr(rule, year) {
  dateArr.value[5] = getOrderArr(year, year + 100)
  if (rule !== undefined) {
    if (rule.indexOf('-') >= 0) {
      dateArr.value[5] = getCycleArr(rule, year + 100, false)
    } else if (rule.indexOf('/') >= 0) {
      dateArr.value[5] = getAverageArr(rule, year + 100)
    } else if (rule !== '*') {
      dateArr.value[5] = getAssignArr(rule)
    }
  }
}

function getMonthArr(rule) {
  dateArr.value[4] = getOrderArr(1, 12)
  if (rule && rule.indexOf('-') >= 0) {
    dateArr.value[4] = getCycleArr(rule, 12, false)
  } else if (rule && rule.indexOf('/') >= 0) {
    dateArr.value[4] = getAverageArr(rule, 12)
  } else if (rule && rule !== '*') {
    dateArr.value[4] = getAssignArr(rule)
  }
}

function getWeekArr(rule) {
  // Simplified week handling
  if (dayRule.value === '' && dayRuleSup.value === '') {
    if (rule && rule.indexOf('-') >= 0) {
      dayRule.value = 'weekDay'
      dayRuleSup.value = getCycleArr(rule, 7, false)
    }
  }
}

function getDayArr(rule) {
  dateArr.value[3] = getOrderArr(1, 31)
  dayRule.value = ''
  dayRuleSup.value = ''
  if (rule && rule.indexOf('-') >= 0) {
    dateArr.value[3] = getCycleArr(rule, 31, false)
    dayRuleSup.value = 'null'
  } else if (rule && rule.indexOf('/') >= 0) {
    dateArr.value[3] = getAverageArr(rule, 31)
    dayRuleSup.value = 'null'
  } else if (rule && rule.indexOf('W') >= 0) {
    dayRule.value = 'workDay'
    dayRuleSup.value = Number(rule.match(/[0-9]{1,2}/g)[0])
    dateArr.value[3] = [dayRuleSup.value]
  } else if (rule && rule.indexOf('L') >= 0) {
    dayRule.value = 'lastDay'
    dayRuleSup.value = 'null'
    dateArr.value[3] = [31]
  } else if (rule && rule !== '*' && rule !== '?') {
    dateArr.value[3] = getAssignArr(rule)
    dayRuleSup.value = 'null'
  } else if (rule === '*') {
    dayRuleSup.value = 'null'
  }
}

function getHourArr(rule) {
  dateArr.value[2] = getOrderArr(0, 23)
  if (rule && rule.indexOf('-') >= 0) {
    dateArr.value[2] = getCycleArr(rule, 24, true)
  } else if (rule && rule.indexOf('/') >= 0) {
    dateArr.value[2] = getAverageArr(rule, 23)
  } else if (rule && rule !== '*') {
    dateArr.value[2] = getAssignArr(rule)
  }
}

function getMinArr(rule) {
  dateArr.value[1] = getOrderArr(0, 59)
  if (rule && rule.indexOf('-') >= 0) {
    dateArr.value[1] = getCycleArr(rule, 60, true)
  } else if (rule && rule.indexOf('/') >= 0) {
    dateArr.value[1] = getAverageArr(rule, 59)
  } else if (rule && rule !== '*') {
    dateArr.value[1] = getAssignArr(rule)
  }
}

function getSecondArr(rule) {
  dateArr.value[0] = getOrderArr(0, 59)
  if (rule && rule.indexOf('-') >= 0) {
    dateArr.value[0] = getCycleArr(rule, 60, true)
  } else if (rule && rule.indexOf('/') >= 0) {
    dateArr.value[0] = getAverageArr(rule, 59)
  } else if (rule && rule !== '*') {
    dateArr.value[0] = getAssignArr(rule)
  }
}

function getOrderArr(min, max) {
  const arr = []
  for (let i = min; i <= max; i++) {
    arr.push(i)
  }
  return arr
}

function getAssignArr(rule) {
  const arr = []
  const assignArr = rule.split(',')
  for (let i = 0; i < assignArr.length; i++) {
    arr[i] = Number(assignArr[i])
  }
  arr.sort((a, b) => a - b)
  return arr
}

function getAverageArr(rule, limit) {
  const arr = []
  const agArr = rule.split('/')
  let min = Number(agArr[0])
  const step = Number(agArr[1])
  while (min <= limit) {
    arr.push(min)
    min += step
  }
  return arr
}

function getCycleArr(rule, limit, status) {
  const arr = []
  const cycleArr = rule.split('-')
  let min = Number(cycleArr[0])
  let max = Number(cycleArr[1])
  if (min > max) {
    max += limit
  }
  for (let i = min; i <= max; i++) {
    let add = 0
    if (status === false && i % limit === 0) {
      add = limit
    }
    arr.push(Math.round((i % limit) + add))
  }
  arr.sort((a, b) => a - b)
  return arr
}

watch(() => props.ex, expressionChange)

onMounted(() => {
  expressionChange()
})
</script>

<style scoped>
.popup-result {
  box-sizing: border-box;
  line-height: 24px;
  margin: 25px auto;
  padding: 15px 10px 10px;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  position: relative;
}

.popup-result p {
  margin: 0 0 10px 0;
  font-weight: bold;
}

.popup-result-scroll {
  font-size: 12px;
  line-height: 24px;
  max-height: 200px;
  overflow-y: auto;
  padding: 0;
  margin: 0;
  list-style: none;
}

.popup-result-scroll li {
  padding: 4px 8px;
  border-bottom: 1px solid #f0f0f0;
}

.popup-result-scroll li:last-child {
  border-bottom: none;
}
</style>
