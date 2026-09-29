import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'
import { clearAuth, getToken } from '../store/auth'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 请求拦截：自动带上 token
request.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截：解包统一返回结构 { code, msg, data }
request.interceptors.response.use(
  (response) => {
    const body = response.data
    if (!body || typeof body !== 'object' || !('code' in body)) {
      return body
    }
    if (body.code === 200) {
      return body.data
    }
    ElMessage.error(body.msg || '请求失败')
    return Promise.reject(new Error(body.msg || '请求失败'))
  },
  (error) => {
    const status = error.response?.status
    if (status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      clearAuth()
      router.replace({ name: 'login' })
    } else {
      ElMessage.error(error.response?.data?.msg || error.message || '网络异常')
    }
    return Promise.reject(error)
  },
)

export default request
