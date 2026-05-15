import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: MainLayout,
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/Dashboard.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'system/user',
        name: 'UserList',
        component: () => import('../views/system/user/UserList.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'system/role',
        name: 'RoleList',
        component: () => import('../views/system/role/RoleList.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: 'system/role/add',
        name: 'RoleAdd',
        component: () => import('../views/system/role/RoleForm.vue'),
        meta: { title: '新增角色' }
      },
      {
        path: 'system/role/:id/edit',
        name: 'RoleEdit',
        component: () => import('../views/system/role/RoleForm.vue'),
        meta: { title: '编辑角色' }
      },
      {
        path: 'system/role/:id/permission',
        name: 'RolePermission',
        component: () => import('../views/system/role/RolePermission.vue'),
        meta: { title: '角色权限' }
      },
      {
        path: 'system/permission',
        name: 'PermissionTree',
        component: () => import('../views/system/permission/PermissionTree.vue'),
        meta: { title: '权限管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth !== false && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router