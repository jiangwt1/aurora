import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    collapse: false, // 侧边栏折叠状态
    tabList: [], // 标签页列表
    activeTab: '' // 当前激活的标签页
  }),

  getters: {
    // 获取标签页列表
    getTabList: (state) => state.tabList,
    // 获取当前激活的标签页
    getActiveTab: (state) => state.activeTab
  },

  actions: {
    // 切换侧边栏折叠状态
    toggleCollapse() {
      this.collapse = !this.collapse
    },

    // 添加标签页
    addTab(tab) {
      // 检查标签页是否已存在
      const existIndex = this.tabList.findIndex(item => item.path === tab.path)
      if (existIndex === -1) {
        this.tabList.push(tab)
      }
      this.activeTab = tab.path
    },

    // 移除标签页
    removeTab(path) {
      const index = this.tabList.findIndex(item => item.path === path)
      if (index > -1) {
        this.tabList.splice(index, 1)
        // 如果删除的是当前激活的标签页，激活前一个或后一个
        if (this.activeTab === path && this.tabList.length > 0) {
          const newIndex = index >= this.tabList.length ? this.tabList.length - 1 : index
          this.activeTab = this.tabList[newIndex].path
        }
      }
    },

    // 设置当前激活的标签页
    setActiveTab(path) {
      this.activeTab = path
    },

    // 清空所有标签页
    clearTabs() {
      this.tabList = []
      this.activeTab = ''
    }
  }
})
