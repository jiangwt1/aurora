<template>
  <div>
    <n-tabs type="segment" animated>
      <n-tab-pane name="second" tab="秒">
        <CrontabSecond @update="updateCrontabValue" :check="checkNumber" :cron="crontabValueObj" ref="cronsecond" />
      </n-tab-pane>
      <n-tab-pane name="min" tab="分钟">
        <CrontabMin @update="updateCrontabValue" :check="checkNumber" :cron="crontabValueObj" ref="cronmin" />
      </n-tab-pane>
      <n-tab-pane name="hour" tab="小时">
        <CrontabHour @update="updateCrontabValue" :check="checkNumber" :cron="crontabValueObj" ref="cronhour" />
      </n-tab-pane>
      <n-tab-pane name="day" tab="日">
        <CrontabDay @update="updateCrontabValue" :check="checkNumber" :cron="crontabValueObj" ref="cronday" />
      </n-tab-pane>
      <n-tab-pane name="month" tab="月">
        <CrontabMonth @update="updateCrontabValue" :check="checkNumber" :cron="crontabValueObj" ref="cronmonth" />
      </n-tab-pane>
      <n-tab-pane name="week" tab="周">
        <CrontabWeek @update="updateCrontabValue" :check="checkNumber" :cron="crontabValueObj" ref="cronweek" />
      </n-tab-pane>
      <n-tab-pane name="year" tab="年">
        <CrontabYear @update="updateCrontabValue" :check="checkNumber" :cron="crontabValueObj" ref="cronyear" />
      </n-tab-pane>
    </n-tabs>
    <div class="popup-main">
      <div class="popup-result">
        <table>
          <thead>
            <tr>
              <th v-for="item in tabTitles" :key="item" width="40">{{ item }}</th>
              <th>Cron 表达式</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td><span>{{ crontabValueObj.second }}</span></td>
              <td><span>{{ crontabValueObj.min }}</span></td>
              <td><span>{{ crontabValueObj.hour }}</span></td>
              <td><span>{{ crontabValueObj.day }}</span></td>
              <td><span>{{ crontabValueObj.month }}</span></td>
              <td><span>{{ crontabValueObj.week }}</span></td>
              <td><span>{{ crontabValueObj.year }}</span></td>
              <td><span>{{ crontabValueString }}</span></td>
            </tr>
          </tbody>
        </table>
      </div>
      <CrontabResult :ex="crontabValueString" />
      <div class="pop_btn">
        <n-button size="small" type="primary" @click="submitFill">确定</n-button>
        <n-button size="small" @click="clearCron">重置</n-button>
        <n-button size="small" @click="hidePopup">取消</n-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import CrontabSecond from './second.vue'
import CrontabMin from './min.vue'
import CrontabHour from './hour.vue'
import CrontabDay from './day.vue'
import CrontabMonth from './month.vue'
import CrontabWeek from './week.vue'
import CrontabYear from './year.vue'
import CrontabResult from './result.vue'

const props = defineProps({
  expression: String,
  hideComponent: Boolean
})

const emit = defineEmits(['hide', 'fill'])

const tabTitles = ['秒', '分钟', '小时', '日', '月', '周', '年']

const crontabValueObj = ref({
  second: '*',
  min: '*',
  hour: '*',
  day: '*',
  month: '*',
  week: '?',
  year: ''
})

const crontabValueString = computed(() => {
  const obj = crontabValueObj.value
  let str = `${obj.second} ${obj.min} ${obj.hour} ${obj.day} ${obj.month} ${obj.week}`
  if (obj.year) {
    str += ` ${obj.year}`
  }
  return str
})

function resolveExp() {
  if (props.expression) {
    const arr = props.expression.split(' ')
    if (arr.length >= 6) {
      const obj = {
        second: arr[0],
        min: arr[1],
        hour: arr[2],
        day: arr[3],
        month: arr[4],
        week: arr[5],
        year: arr[6] || ''
      }
      crontabValueObj.value = { ...obj }
      for (const i in obj) {
        if (obj[i]) {
          changeRadio(i, obj[i])
        }
      }
    }
  } else {
    clearCron()
  }
}

function updateCrontabValue(name, value, from) {
  crontabValueObj.value[name] = value
  if (from && from !== name) {
    changeRadio(name, value)
  }
}

function changeRadio(name, value) {
  const arr = ['second', 'min', 'hour', 'month']
  const refName = 'cron' + name
  const refs = {
    cronsecond: 'cronsecond',
    cronmin: 'cronmin',
    cronhour: 'cronhour',
    cronday: 'cronday',
    cronmonth: 'cronmonth',
    cronweek: 'cronweek',
    cronyear: 'cronyear'
  }

  if (!refs[refName]) return

  if (arr.includes(name)) {
    let insValue
    if (value === '*') {
      insValue = 1
    } else if (value.indexOf('-') > -1) {
      insValue = 2
    } else if (value.indexOf('/') > -1) {
      insValue = 3
    } else {
      insValue = 4
    }
    // Update the radioValue in the child component
    // This would need the child component to expose a method or use a different approach
  }
}

function checkNumber(value, minLimit, maxLimit) {
  value = Math.floor(value)
  if (value < minLimit) {
    value = minLimit
  } else if (value > maxLimit) {
    value = maxLimit
  }
  return value
}

function hidePopup() {
  emit('hide')
}

function submitFill() {
  emit('fill', crontabValueString.value)
  hidePopup()
}

function clearCron() {
  crontabValueObj.value = {
    second: '*',
    min: '*',
    hour: '*',
    day: '*',
    month: '*',
    week: '?',
    year: ''
  }
}

watch(() => props.expression, resolveExp)

onMounted(() => {
  resolveExp()
})
</script>

<style scoped>
.pop_btn {
  text-align: center;
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 8px;
}

.popup-main {
  position: relative;
  margin: 10px auto;
  background: #fff;
  border-radius: 5px;
  font-size: 12px;
  overflow: hidden;
}

.popup-result {
  box-sizing: border-box;
  line-height: 24px;
  margin: 25px auto;
  padding: 15px 10px 10px;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  position: relative;
}

.popup-result table {
  text-align: center;
  width: 100%;
  margin: 0 auto;
  border-collapse: collapse;
}

.popup-result table th {
  background: #f5f7fa;
  padding: 8px;
  font-weight: bold;
}

.popup-result table td {
  padding: 8px;
  border: 1px solid #ebeef5;
}

.popup-result table span {
  display: block;
  width: 100%;
  font-family: arial;
  line-height: 30px;
  height: 30px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  border: 1px solid #e8e8e8;
  border-radius: 3px;
}
</style>
