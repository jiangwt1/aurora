<template>
  <AppPage>
    <n-result status="info" title="功能开发中" description="此功能正在开发中，敬请期待...">
      <template #footer>
        <n-button type="primary" @click="goBack">返回上一页</n-button>
      </template>
    </n-result>
  </AppPage>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function goBack() {
  router.back()
}
</script>
