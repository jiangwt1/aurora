<template>
  <div
    ref="editorRef"
    class="edit-container"
    v-html="innerText"
    :contenteditable="disable"
    @focus="onFocus"
    @blur="onBlur"
    @input="onInput"
  />
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  value: {
    type: String,
    default: ''
  },
  disable: {
    type: Boolean,
    default: true
  },
  placeholder: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['input', 'focus', 'blur'])

const editorRef = ref(null)
const innerText = ref(props.value)
const isLocked = ref(false)
const range = ref(null)

watch(() => props.value, (newVal) => {
  if (!isLocked.value) {
    innerText.value = newVal
  }
})

function clear() {
  editorRef.value.innerHTML = ''
  emit('input', editorRef.value.innerHTML)
}

function onInput() {
  emit('input', editorRef.value.innerHTML)
}

function onFocus() {
  emit('focus', editorRef.value.innerHTML)
  isLocked.value = true
}

function onBlur() {
  // 记录光标
  if (window.getSelection) {
    const selection = window.getSelection()
    range.value = selection.getRangeAt(0)
  }
  emit('blur', editorRef.value.innerHTML)
  isLocked.value = false
}

function addText(value) {
  // 还原光标
  if (window.getSelection) {
    const selection = window.getSelection()
    selection.removeAllRanges()
    // 为空初始化光标
    if (range.value == null) {
      editorRef.value.focus()
      range.value = selection.getRangeAt(0)
    }
    // 删除选中内容
    range.value.deleteContents()
    // 添加内容
    range.value.insertNode(range.value.createContextualFragment(value))
    range.value.collapse(false)
    selection.addRange(range.value)
    emit('input', editorRef.value.innerHTML)
  }
}

defineExpose({
  clear,
  addText
})
</script>

<style scoped>
.edit-container {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 8px;
  background: #f0f1f4;
  font-size: 14px;
  line-height: 1.5;
  padding: 6px 12px;
  box-sizing: border-box;
  overflow: auto;
  word-break: break-all;
  outline: none;
  user-select: text;
  white-space: pre-wrap;
  text-align: left;
  -webkit-user-modify: read-write-plaintext-only;
}
.edit-container:empty::before {
  cursor: text;
  content: attr(placeholder);
  color: #999;
}
</style>
