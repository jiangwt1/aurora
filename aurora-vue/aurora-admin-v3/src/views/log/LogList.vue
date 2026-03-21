<template>
  <AppPage>
    <n-card title="日志管理" rounded-10>
      <n-space vertical size="large">
        <!-- 日志类型标签 -->
        <n-tabs v-model:value="activeTab" type="line" animated>
          <n-tab-pane name="operation" tab="操作日志">
            <OperationLog />
          </n-tab-pane>
          <n-tab-pane name="exception" tab="异常日志">
            <ExceptionLog />
          </n-tab-pane>
        </n-tabs>
      </n-space>
    </n-card>
  </AppPage>
</template>

<script setup>
import { ref } from 'vue'
import OperationLog from './components/OperationLog.vue'
import ExceptionLog from './components/ExceptionLog.vue'

const activeTab = ref('operation')
</script>

<style scoped>
</style>
