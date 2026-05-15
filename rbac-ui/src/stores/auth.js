import { defineStore } from 'pinia'
import { login as loginApi, logout as logoutApi, getUserInfo } from '../api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null
  }),
  getters: {
    isLoggedIn: (state) => !!state.token
  },
  actions: {
    async login(loginData) {
      const res = await loginApi(loginData)
      this.token = res.data.token
      this.userInfo = res.data.user
      localStorage.setItem('token', res.data.token)
    },
    async fetchUserInfo() {
      const res = await getUserInfo()
      this.userInfo = res.data
    },
    async logout() {
      try {
        await logoutApi()
      } finally {
        this.token = ''
        this.userInfo = null
        localStorage.removeItem('token')
      }
    }
  }
})