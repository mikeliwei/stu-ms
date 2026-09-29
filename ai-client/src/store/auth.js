import { reactive } from 'vue'

const TOKEN_KEY = 'ai_stu_token'
const USER_KEY = 'ai_stu_user'

function readUser() {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY) || 'null')
  } catch {
    return null
  }
}

/** 登录状态：token + 用户信息，持久化在 localStorage 中 */
export const authState = reactive({
  token: localStorage.getItem(TOKEN_KEY) || '',
  user: readUser(),
})

export function getToken() {
  return authState.token
}

export function setAuth(data) {
  const { token, ...user } = data
  authState.token = token || ''
  authState.user = user
  localStorage.setItem(TOKEN_KEY, authState.token)
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function clearAuth() {
  authState.token = ''
  authState.user = null
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}
