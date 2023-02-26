<template>
  <div id="footer" class="relative w-full pt-1" :style="gradientBackground">
    <span class="bg-ob-deep-900 flex justify-center">
      <div
          class="bg-ob-deep-900 rounded-lg max-w-10/12 lg:max-w-screen-2xl text-sm text-ob-normal w-full py-6 px-6 grid grid-rows-1 lg:grid-rows-none lg:grid-cols-4 justify-center items-center gap-8 h-36 mx-auto">
        <div
            class="flex flex-col lg:flex-row gap-6 lg:gap-12 row-span-1 lg:col-span-5 text-center lg:text-left mx-auto">
          <ul class="flex flex-col gap-1.5 mx-auto">
            <li>
              Copyright © 2019 - {{ currentYear }}
              <b class="font-extrabold">{{ websiteConfig.author }}</b>
            </li>
            <li v-if="websiteConfig.beian_number != ''" class="flex flex-row gap-3 ">
              <a href="https://beian.miit.gov.cn/" target="_blank">
                <b class="font-extrabold border-b-2 border-ob hover:text-ob" style="margin-left: 23px"> {{ websiteConfig.beianNumber }} </b>
              </a>
            </li>
            <li style="width:300px;margin:0 auto; padding:0px 0;">
      <a target="_blank" href="http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=33011002016933" style="display:inline-block;text-decoration:none;height:20px;line-height:20px;"><img src="../../public/国徽.png" style="float:left;"/><p style="float:left;height:20px;line-height:20px;margin: 0px 0px 0px 5px; color:#939393;">浙公网安备 33011002016933号</p></a>
            </li>
          </ul>
        </div>
      </div>
    </span>
  </div>
</template>
<!--页脚，备案的地方-->
<script lang="ts">
import { computed, defineComponent } from 'vue'
import { useAppStore } from '@/stores/app'
import { useI18n } from 'vue-i18n'

export default defineComponent({
  name: 'Footer',
  setup() {
    const appStore = useAppStore()
    const { t } = useI18n()
    return {
      avatarClass: computed(() => {
        return {
          'footer-avatar': true,
          [appStore.themeConfig.profile_shape]: true
        }
      }),
      gradientText: computed(() => appStore.themeConfig.background_gradient_style),
      gradientBackground: computed(() => {
        return { background: appStore.themeConfig.header_gradient_css }
      }),
      currentYear: computed(() => new Date().getUTCFullYear()),
      websiteConfig: computed(() => appStore.websiteConfig),
      t
    }
  }
})
</script>
