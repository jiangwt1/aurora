import service from './http'

// 封装请求方法
export const request = {
  get(url, params = {}, config = {}) {
    return service({
      method: 'get',
      url,
      params,
      ...config
    })
  },

  post(url, data = {}, config = {}) {
    return service({
      method: 'post',
      url,
      data,
      ...config
    })
  },

  put(url, data = {}, config = {}) {
    return service({
      method: 'put',
      url,
      data,
      ...config
    })
  },

  delete(url, params = {}, config = {}) {
    return service({
      method: 'delete',
      url,
      params,
      ...config
    })
  }
}

export default request
