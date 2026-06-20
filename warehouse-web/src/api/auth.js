/*
 * axios 请求封装
 * baseURL='/api' + withCredentials(Cookie) + 响应拦截
 */
import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  withCredentials: true
})

request.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    console.error('请求失败:', error)
    return Promise.reject(error)
  }
)

export default request
