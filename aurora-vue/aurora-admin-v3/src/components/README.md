# 通用组件使用指南

## 1. Editor 组件

富文本编辑器组件，基于 contenteditable 实现。

### 使用示例

```vue
<template>
  <Editor
    v-model="content"
    :disable="true"
    placeholder="请输入内容..."
    @focus="handleFocus"
    @blur="handleBlur"
    ref="editor"
  />
  <n-button @click="addEmoji">添加表情</n-button>
</template>

<script setup>
import { ref } from 'vue'
import Editor from '@/components/Editor.vue'

const content = ref('<p>Hello World</p>')
const editor = ref(null)

function handleFocus(html) {
  console.log('Editor focused:', html)
}

function handleBlur(html) {
  console.log('Editor blurred:', html)
}

function addEmoji() {
  editor.value.addText('😊')
}
</script>
```

### Props

| 参数 | 说明 | 类型 | 默认值 |
|------|------|------|--------|
| value/v-model | 编辑器内容 | String | '' |
| disable | 是否可编辑 | Boolean | true |
| placeholder | 占位文本 | String | '' |

### Events

| 事件名 | 说明 | 回调参数 |
|--------|------|----------|
| input | 内容变化时触发 | (html: string) |
| focus | 获得焦点时触发 | (html: string) |
| blur | 失去焦点时触发 | (html: string) |

### Methods

通过 ref 可以调用以下方法：

| 方法名 | 说明 | 参数 |
|--------|------|------|
| clear | 清空内容 | - |
| addText | 在光标位置插入文本 | (text: string) |

---

## 2. TagCloud 组件

3D旋转标签云组件。

### 使用示例

```vue
<template>
  <TagCloud
    :data="tags"
    :config="config"
    @click-tag="handleTagClick"
  />
</template>

<script setup>
import { ref } from 'vue'
import TagCloud from '@/components/TagCloud.vue'

const tags = ref([
  { name: 'Vue', id: 1 },
  { name: 'React', id: 2 },
  { name: 'Angular', id: 3 },
  { name: 'Node.js', id: 4 },
  { name: 'TypeScript', id: 5 },
  { name: 'Vite', id: 6 }
])

const config = {
  radius: 150,          // 旋转半径
  maxFont: 20,          // 最大字体大小
  color: '#409eff',     // 统一颜色（不设置则随机颜色）
  rotateAngleXbase: 500,// X轴旋转速度基数
  rotateAngleYbase: 500,// Y轴旋转速度基数
  hover: true           // 是否启用鼠标悬停控制
}

function handleTagClick(tag) {
  console.log('Clicked tag:', tag)
}
</script>
```

### Props

| 参数 | 说明 | 类型 | 默认值 |
|------|------|------|--------|
| data | 标签数据数组 | Array<{ name: string, [key: string]: any }> | [] |
| config | 配置对象 | Object | 见下方默认配置 |

### Config 默认配置

```javascript
{
  radius: 120,           // 旋转半径
  maxFont: 24,           // 最大字体
  color: null,           // 颜色（null为随机颜色）
  rotateAngleXbase: 500, // X轴旋转基数（越小越快）
  rotateAngleYbase: 500, // Y轴旋转基数（越小越快）
  hover: false           // 鼠标悬停控制
}
```

### Events

| 事件名 | 说明 | 回调参数 |
|--------|------|----------|
| click-tag | 点击标签时触发 | (tag: object) |

### 交互

- **单击标签**: 触发 click-tag 事件
- **双击标签**: 暂停/恢复旋转动画

---

## 3. Crontab 组件

Cron表达式生成器组件，用于配置定时任务。

### 使用示例

```vue
<template>
  <n-button @click="showCrontab = true">配置定时任务</n-button>
  <n-modal v-model:show="showCrontab">
    <n-card style="width: 800px" title="Cron表达式配置" :bordered="false">
      <Crontab
        v-if="showCrontab"
        :expression="cronExpression"
        @hide="showCrontab = false"
        @fill="handleCrontabFill"
      />
    </n-card>
  </n-modal>
  <div v-if="cronExpression">
    当前表达式: <n-tag type="success">{{ cronExpression }}</n-tag>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Crontab from '@/components/Crontab/index.vue'

const showCrontab = ref(false)
const cronExpression = ref('0 0 12 * * ?')

function handleCrontabFill(expression) {
  cronExpression.value = expression
  console.log('Cron expression:', expression)
  // 0 0 12 * * ? 表示每天中午12点执行
}
</script>
```

### Props

| 参数 | 说明 | 类型 | 默认值 |
|------|------|------|--------|
| expression | Cron表达式 | String | '' |
| hideComponent | 是否隐藏部分组件 | Boolean | false |

### Events

| 事件名 | 说明 | 回调参数 |
|--------|------|----------|
| hide | 点击取消时触发 | - |
| fill | 点击确定时触发 | (expression: string) |

### Cron 表达式格式

```
秒 分 时 日 月 周 年

*    : 任意值
?    : 不指定（仅日和周可用）
-    : 范围 (如: 1-5)
/    : 间隔 (如: 0/15 表示每15分钟)
,    : 列表 (如: 1,2,3)
L    : 最后 (日: 月末, 周: 周六)
W    : 工作日 (仅日可用)
#    : 第几周 (仅周可用, 如: 1#2 表示第2个周一)

示例:
0 0 12 * * ?      : 每天12点
0 0/5 * * * ?     : 每5分钟
0 0 12 ? * MON-FRI : 工作日12点
0 0 0 1 * ?       : 每月1号0点
```

### 组件结构

Crontab组件由以下子组件组成：

- **second.vue** - 秒配置 (0-59)
- **min.vue** - 分钟配置 (0-59)
- **hour.vue** - 小时配置 (0-23)
- **day.vue** - 日配置 (1-31)
- **month.vue** - 月配置 (1-12)
- **week.vue** - 周配置 (1-7)
- **year.vue** - 年配置 (可选)
- **result.vue** - 显示最近5次运行时间

---

## 完整示例：定时任务配置页面

```vue
<template>
  <n-card title="定时任务管理">
    <n-form :model="taskForm" label-placement="left">
      <n-form-item label="任务名称">
        <n-input v-model:value="taskForm.name" placeholder="请输入任务名称" />
      </n-form-item>
      <n-form-item label="Cron表达式">
        <n-space align="center">
          <n-input v-model:value="taskForm.cronExpression" readonly style="width: 300px" />
          <n-button type="primary" @click="showCrontab = true">配置</n-button>
        </n-space>
      </n-form-item>
      <n-form-item label="任务描述">
        <Editor
          v-model="taskForm.description"
          :disable="true"
          placeholder="请输入任务描述"
        />
      </n-form-item>
    </n-form>

    <n-divider />

    <n-card title="标签云" size="small">
      <TagCloud
        :data="tags"
        :config="{ radius: 120, maxFont: 18, hover: true }"
        @click-tag="filterByTag"
      />
    </n-card>
  </n-card>

  <n-modal v-model:show="showCrontab">
    <n-card style="width: 800px" title="配置Cron表达式" :bordered="false">
      <Crontab
        v-if="showCrontab"
        :expression="taskForm.cronExpression"
        @hide="showCrontab = false"
        @fill="updateCronExpression"
      />
    </n-card>
  </n-modal>
</template>

<script setup>
import { ref } from 'vue'
import Editor from '@/components/Editor.vue'
import TagCloud from '@/components/TagCloud.vue'
import Crontab from '@/components/Crontab/index.vue'

const showCrontab = ref(false)

const taskForm = ref({
  name: '',
  cronExpression: '0 0 12 * * ?',
  description: ''
})

const tags = ref([
  { name: '备份', id: 1 },
  { name: '清理', id: 2 },
  { name: '同步', id: 3 },
  { name: '通知', id: 4 },
  { name: '统计', id: 5 }
])

function updateCronExpression(expression) {
  taskForm.value.cronExpression = expression
  showCrontab.value = false
}

function filterByTag(tag) {
  console.log('Filter by tag:', tag.name)
}
</script>
```

---

## 注意事项

1. **Editor 组件**:
   - 输出的是 HTML 字符串，不是 Markdown
   - 适用于简单的富文本编辑需求
   - 如需 Markdown 编辑，请使用 mavon-editor

2. **TagCloud 组件**:
   - 建议标签数量在 10-30 个之间
   - 过多标签会影响视觉效果
   - 鼠标悬停控制需要设置 `hover: true`

3. **Crontab 组件**:
   - 组件会自动处理日和周的互斥关系
   - 年字段为可选，不填写则不显示
   - 结果预览仅供参考，实际执行时间由后端调度器决定
