
<template>
  <div class="sidebar-box" v-if="websiteConfig.isReward === 1 && (websiteConfig.weiXinQRCode || websiteConfig.alipayQRCode)" >
    <SubTitle :title="'titles.tips'" icon="category" />
    <ul class="py-4 px-2">
      <li class="flex flex-row justify-evenly flex-wrap w-full text-center items-center">
        <img v-if="websiteConfig.weiXinQRCode"  :src="websiteConfig.weiXinQRCode"  alt=""/>
        <img v-if="websiteConfig.alipayQRCode"  :src="websiteConfig.alipayQRCode"  alt=""/>
        <p v-if="websiteConfig.weiXinQRCode"  class="text-base --el-font-family"><b>WeChat Pay</b></p>
        <p v-if="websiteConfig.alipayQRCode"  class="text-base --el-font-family"><b>Alibaba Pay</b></p>
      </li>
    </ul>
  </div>

</template>
<script lang="ts">
import { computed, defineComponent } from 'vue'
import { useAppStore } from '@/stores/app'
import { SubTitle } from '@/components/Title'

export default defineComponent({
  name: 'SidebarBox',
  components: { SubTitle },
  setup() {
    const appStore = useAppStore()
    return {
      default: '',
      avatarClass: computed(() => {
        return {
          'ob-avatar': true,
          [appStore.themeConfig.profile_shape]: true
        }
      }),
      websiteConfig: computed(() => {
        return appStore.websiteConfig
      })
    }
  }
})
</script>


<style scoped lang="scss">
img, video {
  max-width: 40%;
}

audio, canvas, embed, iframe, img, object, svg, video {
  display: block;
  vertical-align: middle;
}
.py-4 {
  padding-top: 0rem;
  padding-bottom: 0rem;
}
</style>
