# UnoCSS 样式指南

## 🎨 基础概念

UnoCSS 是一个原子化CSS引擎，按需生成CSS，类似Tailwind CSS。

## 📝 使用方式

### 1. 常用工具类（快捷键）

```html
<!-- 布局 -->
<div class="flex">Flex布局</div>
<div class="flex-col">垂直Flex布局</div>
<div class="flex-center">水平垂直居中</div>
<div class="flex-between">两端对齐</div>
<div class="flex-col-center">垂直居中</div>

<!-- 尺寸 -->
<div class="w-full">宽度100%</div>
<div class="h-full">高度100%</div>
<div class="wh-full">宽高100%</div>

<!-- 间距 -->
<div class="p-4">padding 16px (4*4)</div>
<div class="mt-4">上边距 16px</div>
<div class="mb-4">下边距 16px</div>
<div class="ml-4">左边距 16px</div>
<div class="mr-4">右边距 16px</div>

<!-- 文本 -->
<div class="text-center">文本居中</div>
<div class="text-left">左对齐</div>
<div class="text-right">右对齐</div>
<div class="text-14">字体大小 14px</div>

<!-- 背景色 -->
<div class="bg-white">白色背景</div>
<div class="bg-gray-100">浅灰背景</div>

<!-- 边框 -->
<div class="border">边框</div>
<div class="border-t">上边框</div>
<div class="rounded">圆角</div>
<div class="rounded-10">圆角10px</div>

<!-- 显示 -->
<div class="block">块级</div>
<div class="hidden">隐藏</div>
<div class="invisible">不可见</div>
```

### 2. 颜色系统

```html
<!-- 主色 -->
<div class="text-primary">主色文本</div>
<div class="bg-primary">主色背景</div>
<div class="border-primary">主色边框</div>

<!-- 状态色 -->
<div class="text-success">成功色</div>
<div class="text-warning">警告色</div>
<div class="text-danger">危险色</div>
<div class="text-info">信息色</div>
```

### 3. 组合示例

```vue
<!-- 卡片 -->
<template>
  <n-card class="p-4 rounded-10 bg-white">
    <div class="flex items-center justify-between mb-4">
      <h2 class="text-20 font-bold">标题</h2>
      <n-button type="primary" size="small">按钮</n-button>
    </div>
    <div class="text-14 text-gray-600">
      内容描述
    </div>
  </n-card>
</template>

<!-- 按钮组 -->
<template>
  <div class="flex gap-2">
    <n-button type="primary">主要按钮</n-button>
    <n-button secondary>次要按钮</n-button>
    <n-button tertiary>第三按钮</n-button>
  </div>
</template>

<!-- 响应式网格 -->
<template>
  <n-grid x-gap="12" :cols="4" responsive="screen">
    <n-gi>
      <n-card>内容1</n-card>
    </n-gi>
    <n-gi>
      <n-card>内容2</n-card>
    </n-gi>
  </n-grid>
</template>
```

## 🎯 最佳实践

### 1. 优先使用原子化类
```vue
<!-- 推荐 -->
<div class="flex items-center justify-between p-4">

<!-- 不推荐 -->
<div style="display: flex; align-items: center; justify-content: space-between; padding: 16px;">
```

### 2. 提取常用组合
```javascript
// unocss.config.js
shortcuts: {
  'btn-base': 'px-4 py-2 rounded border border-gray-300 bg-white hover:bg-gray-50',
  'card-base': 'p-4 rounded-lg bg-white shadow',
  'input-base': 'px-3 py-2 border rounded bg-white'
}
```

### 3. 响应式设计
```vue
<!-- 移动端全宽，桌面端分栏 -->
<n-grid :cols="1" :x-gap="12" responsive="screen">
  <n-gi span="0 m:2">移动端占1列，中等屏幕占2列</n-gi>
</n-grid>

<!-- 响应式显示 -->
<div class="hidden md:block">桌面端显示</div>
<div class="block md:hidden">移动端显示</div>
```

### 4. 动态类名
```vue
<script setup>
const isActive = ref(false)
const classes = computed(() => [
  'base-class',
  isActive && 'active-class',
  someCondition && 'conditional-class'
])
</script>

<div :class="classes">内容</div>
```

## 📚 常用模式

### 表单样式
```vue
<template>
  <n-form inline class="flex items-end gap-4">
    <n-form-item label="用户名">
      <n-input class="w-200" placeholder="请输入" />
    </n-form-item>
    <n-form-item label="密码">
      <n-input type="password" class="w-200" />
    </n-form-item>
    <n-button type="primary" attr="click="handleSubmit">提交</n-button>
  </n-form>
</template>
```

### 表格样式
```vue
<template>
  <n-data-table
    :columns="columns"
    :data="data"
    :row-key="(row) => row.id"
    striped
    size="small"
  />
</template>
```

### 卡片网格
```vue
<template>
  <n-grid x-gap="16" y-gap="16" :cols="4" responsive="screen">
    <n-gi v-for="item in list" :key="item.id">
      <n-card class="h-full">
        {{ item.content }}
      </n-card>
    </n-gi>
  </n-grid>
</template>
```

## 🔧 自定义快捷键

在 `unocss.config.js` 中已配置的快捷键：

```javascript
shortcuts: {
  'wh-full': 'w-full h-full',
  'flex-center': 'flex justify-center items-center',
  'flex-col': 'flex flex-col',
  'flex-between': 'flex justify-between items-center',
  'flex-col-center': 'flex flex-col justify-center items-center',
  'absolute-center': 'absolute left-0 top-0 flex justify-center items-center wh-full',
  'p-20': 'p-5',
  'mt-20': 'mt-5',
  'mb-20': 'mb-5',
  'ml-20': 'ml-5',
  'mr-20': 'mr-5',
  'rounded-10': 'rounded-2.5'
}
```

## 📋 检查清单

在开发新页面时，确保：

- [ ] 使用UnoCSS原子类而不是内联样式
- [ ] 响应式设计考虑（mobile、tablet、desktop）
- [ ] 使用语义化的颜色变量
- [ ] 保持一致的间距系统
- [ ] 合理使用快捷键减少重复
