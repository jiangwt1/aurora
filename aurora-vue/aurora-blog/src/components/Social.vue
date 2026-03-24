<template>
  <ul class="flex flex-row justify-evenly flex-wrap w-full py-4 px-2 text-center items-center">
    <a :href="websiteConfig.github" target="_blank" v-if="websiteConfig.github">
      <li class="diamond-clip-path diamond-icon">
        <svg-icon icon-class="github" class="fill-current" />
      </li>
    </a>
    <a :href="websiteConfig.gitee" target="_blank" v-if="websiteConfig.gitee">
      <li class="diamond-clip-path diamond-icon">
        <svg-icon icon-class="gitee" class="fill-current" />
      </li>
    </a>
    <a :href="websiteConfig.twitter" target="_blank" v-if="websiteConfig.twitter">
      <li class="diamond-clip-path diamond-icon">
        <svg-icon icon-class="twitter" class="fill-current" />
      </li>
    </a>
    <a :href="websiteConfig.stackoverflow" target="_blank" v-if="websiteConfig.stackoverflow">
      <li class="diamond-clip-path diamond-icon">
        <svg-icon icon-class="stackoverflow" class="fill-current" />
      </li>
    </a>
    <li class="diamond-clip-path diamond-icon cursor-pointer" v-if="websiteConfig.weChat" @click="showContact('wechat', websiteConfig.weChat)">
      <svg-icon icon-class="wechat" class="fill-current" />
    </li>
    <li class="diamond-clip-path diamond-icon cursor-pointer" v-if="websiteConfig.qq" @click="showContact('qq', websiteConfig.qq)">
      <svg-icon icon-class="qq" class="fill-current" />
    </li>
    <a :href="websiteConfig.weibo" target="_blank" v-if="websiteConfig.weibo">
      <li class="diamond-clip-path diamond-icon">
        <svg-icon icon-class="weibo" class="fill-current" />
      </li>
    </a>
    <a :href="websiteConfig.csdn" target="_blank" v-if="websiteConfig.csdn">
      <li class="diamond-clip-path diamond-icon">
        <svg-icon icon-class="csdn" class="fill-current" />
      </li>
    </a>
    <a :href="websiteConfig.zhihu" target="_blank" v-if="websiteConfig.zhihu">
      <li class="diamond-clip-path diamond-icon">
        <svg-icon icon-class="zhifu" class="fill-current" />
      </li>
    </a>
    <a :href="websiteConfig.juejin" target="_blank" v-if="websiteConfig.juejin">
      <li class="diamond-clip-path diamond-icon">掘</li>
    </a>
  </ul>

  <!-- 联系方式弹窗 -->
  <Teleport to="body">
    <Transition name="contact-fade">
      <div class="contact-overlay" v-if="visible" @click="closeModal">
        <div class="contact-modal" @click.stop>
          <div class="contact-header">
            <div class="contact-icon" :class="contactType">
              <svg-icon :icon-class="contactType" class="fill-current" v-if="contactType" />
            </div>
            <h3 class="contact-title">{{ contactType === 'qq' ? 'QQ' : '微信' }}</h3>
          </div>
          <div class="contact-body">
            <div class="contact-value">{{ contactValue }}</div>
            <button class="copy-btn" @click="copyToClipboard">
              <svg class="copy-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
                <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
              </svg>
              <span>{{ copied ? '已复制' : '复制' }}</span>
            </button>
          </div>
          <div class="contact-footer">
            <span class="contact-tip">{{ contactType === 'qq' ? '添加好友时请备注来意' : '扫描二维码或搜索微信号添加' }}</span>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script lang="ts">
import { computed, defineComponent, ref } from 'vue'
import { useAppStore } from '@/stores/app'

export default defineComponent({
  name: 'Social',
  setup() {
    const appStore = useAppStore()
    const visible = ref(false)
    const contactType = ref('')
    const contactValue = ref('')
    const copied = ref(false)

    const showContact = (type: string, value: string) => {
      contactType.value = type
      contactValue.value = value
      visible.value = true
      copied.value = false
    }

    const closeModal = () => {
      visible.value = false
    }

    const copyToClipboard = async () => {
      try {
        await navigator.clipboard.writeText(contactValue.value)
        copied.value = true
        setTimeout(() => {
          copied.value = false
        }, 2000)
      } catch (err) {
        // 降级方案
        const textArea = document.createElement('textarea')
        textArea.value = contactValue.value
        textArea.style.position = 'fixed'
        textArea.style.opacity = '0'
        document.body.appendChild(textArea)
        textArea.select()
        document.execCommand('copy')
        document.body.removeChild(textArea)
        copied.value = true
        setTimeout(() => {
          copied.value = false
        }, 2000)
      }
    }

    return {
      websiteConfig: computed(() => {
        return appStore.websiteConfig
      }),
      visible,
      contactType,
      contactValue,
      copied,
      showContact,
      closeModal,
      copyToClipboard
    }
  }
})
</script>

<style scoped>
.custom-social-svg-icon {
  width: 1em;
  height: 1em;
  font-size: 1em;
  vertical-align: -0.15em;
  fill: var(--text-bright);
  stroke: var(--background-primary);
  overflow: hidden;
}

/* 弹窗样式 */
.contact-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.contact-modal {
  background: var(--background-secondary);
  border-radius: 16px;
  padding: 24px 32px;
  min-width: 280px;
  max-width: 90vw;
  box-shadow: var(--accent-shadow);
  animation: modal-pop 0.3s ease;
}

@keyframes modal-pop {
  0% {
    transform: scale(0.9);
    opacity: 0;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.contact-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

.contact-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
}

.contact-icon.qq {
  background: linear-gradient(135deg, #12B7F5 0%, #0099FF 100%);
}

.contact-icon.wechat {
  background: linear-gradient(135deg, #07C160 0%, #1AAD19 100%);
}

.contact-icon svg {
  width: 28px;
  height: 28px;
  color: #fff;
}

.contact-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-bright);
  margin: 0;
}

.contact-body {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.contact-value {
  font-size: 20px;
  font-weight: 500;
  color: var(--text-accent);
  letter-spacing: 1px;
  padding: 12px 24px;
  background: var(--bg-accent-05);
  border-radius: 8px;
  border: 1px dashed var(--text-accent);
}

.copy-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  background: #ffffff;
  color: #333;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.copy-btn:hover {
  background: #f5f5f5;
  border-color: #ccc;
}

.copy-icon {
  width: 16px;
  height: 16px;
}

.contact-footer {
  margin-top: 16px;
  text-align: center;
}

.contact-tip {
  font-size: 12px;
  color: var(--text-dim);
}

/* 过渡动画 */
.contact-fade-enter-active,
.contact-fade-leave-active {
  transition: opacity 0.3s ease;
}

.contact-fade-enter-active .contact-modal,
.contact-fade-leave-active .contact-modal {
  transition: transform 0.3s ease;
}

.contact-fade-enter-from,
.contact-fade-leave-to {
  opacity: 0;
}

.contact-fade-enter-from .contact-modal,
.contact-fade-leave-to .contact-modal {
  transform: scale(0.9);
}
</style>
