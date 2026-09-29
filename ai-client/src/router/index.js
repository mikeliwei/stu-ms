import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '../store/auth'

const routes = [
  {
    path: '/login' ,
    name: 'login',
    component: () => import('../views/LoginView.vue'),
    meta: { title: '用户登录', public: true },
  },
  {
    path: '/',
    component: () => import('../layout/BasicLayout.vue'),
    redirect: '/students',
    children: [
      {
        path: 'students',
        name: 'students',
        component: () => import('../views/StudentView.vue'),
        meta: { title: '学生信息管理' },
      },
      {
        path: 'classes',
        name: 'classes',
        component: () => import('../views/ClassView.vue'),
        meta: { title: '班级信息管理' },
      },
    ],
  },
  { path: '/:pathMatch(.*)*', redirect: '/students' },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 登录守卫：未登录只能访问公开页面
router.beforeEach((to) => {
  const token = getToken()
  if (to.meta.public) {
    return token ? { name: 'students' } : true
  }
  if (!token) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  return true
})

export default router
