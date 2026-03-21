<template>
  <div class="app-page" :class="{ 'show-footer': showFooter }">
    <slot></slot>
    <footer v-if="showFooter" class="app-footer">
      <slot name="footer"></slot>
    </footer>
  </div>
</template>

<script setup>
defineProps({
  showFooter: {
    type: Boolean,
    default: false
  }
})
</script>

<style scoped>
.app-page {
  padding: 20px;
}

.app-page.show-footer {
  padding-bottom: 60px;
}

.app-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 15px 20px;
  background: #fff;
  border-top: 1px solid #e4e7ed;
  text-align: center;
  color: #909399;
  font-size: 14px;
}
</style>
