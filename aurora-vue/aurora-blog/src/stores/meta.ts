import { defineStore } from 'pinia'

export const useMetaStore = defineStore('metaStore', {
  state: () => {
    return {
      title: ''
    }
  }
})
