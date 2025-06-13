<!--
/**
 * 电商平台商家登录页面组件
 * 
 * @description 商家端登录页面，提供邮箱验证码登录和密码登录功能。
 *              采用现代化的双栏布局设计，左侧为品牌展示，右侧为登录表单。
 * 
 * @features
 * - 邮箱验证码登录
 * - 密码登录
 * - 登录方式切换
 * - 实时邮箱格式验证
 * - 验证码发送倒计时（60秒）
 * - 登录状态管理
 * - 自动跳转到商家后台
 * - 响应式布局设计
 * - 错误提示和成功反馈
 * - 防重复提交保护
 * 
 * @layout
 * - 左侧：品牌Logo、欢迎信息、注册引导
 * - 右侧：登录表单、登录方式切换、登录按钮
 * 
 * @validation
 * - 邮箱格式验证（正则表达式）
 * - 必填字段验证
 * - 验证码长度验证
 * - 密码长度验证
 * 
 * @state_management
 * - 使用Pinia进行认证状态管理
 * - localStorage持久化存储
 * - JWT token自动管理
 * 
 * @navigation
 * - 登录成功 -> /merchant/dashboard
 * - 注册链接 -> /merchant/register
 * 
 * @dependencies
 * - Vue 3 Composition API
 * - Vue Router 4
 * - Pinia状态管理
 * - @/api/merchant: 商家API接口
 * - @/store/modules/auth: 认证状态管理
 * 
 * @author 开发团队
 * @version 1.1.0
 * @since 2024
 * 
 * @see {@link ../register/index.vue} 商家注册页面
 * @see {@link ../dashboard/index.vue} 商家后台首页
 */
-->

<template>
  <div class="merchant-login-container">
    <div class="login-box">
      <div class="login-left">
        <div class="logo">Ecommerce-Shopping</div>
        <h2 class="welcome">Welcome Back!</h2>
        <p class="description">请选择登录方式进入</p>
        <p class="description">商家管理后台系统</p>
        <router-link to="/merchant/register">
          <button class="signup-btn">SIGN UP</button>
        </router-link>
      </div>
      <div class="login-right">
        <h2 class="title">Sign in to System</h2>
        <div class="login-type-switch">
          <button 
            :class="['switch-btn', { active: !isPasswordLogin }]" 
            @click="isPasswordLogin = false"
          >
            验证码登录
          </button>
          <button 
            :class="['switch-btn', { active: isPasswordLogin }]" 
            @click="isPasswordLogin = true"
          >
            密码登录
          </button>
        </div>
        <div v-if="message" :class="['message', { 'error': !success, 'success': success }]">
          {{ message }}
        </div>
        <div class="login-form">
          <div class="form-group">
            <input 
              type="email" 
              class="form-control" 
              placeholder="邮箱" 
              v-model="email"
              :disabled="loading"
            />
          </div>
          <template v-if="!isPasswordLogin">
            <div class="verification-group">
              <input 
                type="text" 
                class="verification-input" 
                placeholder="验证码" 
                v-model="verifyCode"
                :disabled="loading"
              />
              <button 
                class="verification-btn" 
                @click="sendVerifyCode" 
                :disabled="countdown > 0 || loading"
              >
                {{ sendBtnText }}
              </button>
            </div>
          </template>
          <template v-else>
            <div class="form-group">
              <input 
                type="password" 
                class="form-control" 
                placeholder="密码" 
                v-model="password"
                :disabled="loading"
              />
            </div>
          </template>
          <div class="forgot-password">
            <a href="#" @click.prevent="handleForgotPassword">忘记密码?</a>
          </div>
          <button 
            class="signin-btn" 
            @click="login"
            :disabled="loading"
          >
            {{ loading ? '登录中...' : 'SIGN IN' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { merchantApi } from '@/api/merchant'
import { useAuthStore } from '@/store/modules/auth'
import type { LoginResponse } from '@/store/modules/auth'

const router = useRouter()
const authStore = useAuthStore()

// 响应式数据
const email = ref('')
const verifyCode = ref('')
const password = ref('')
const loading = ref(false)
const isPasswordLogin = ref(false)

// 倒计时逻辑
const countdown = ref(0)
const sendBtnText = ref('发送验证码')
let timer: number | null = null

// 提示消息
const message = ref('')
const success = ref(false)

// 显示消息函数
const showMessage = (msg: string, isSuccess: boolean) => {
  message.value = msg
  success.value = isSuccess
  
  // 3秒后自动清除消息
  setTimeout(() => {
    message.value = ''
  }, 3000)
}

// 验证邮箱格式
const validateEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

// 验证密码格式
const validatePassword = (password: string): boolean => {
  return password.length >= 6
}

// 发送邮箱验证码
const sendVerifyCode = async () => {
  // 如果正在倒计时或加载中，则不执行
  if (countdown.value > 0 || loading.value) return
  
  // 验证邮箱格式
  if (!validateEmail(email.value)) {
    showMessage('请输入有效的邮箱地址', false)
    return
  }
  
  loading.value = true
  
  try {
    const response = await merchantApi.sendLoginCode(email.value)
    
    if (response.data.success) {
      showMessage('验证码已发送到您的邮箱', true)
      
      // 开始倒计时（60秒）
      countdown.value = 60
      sendBtnText.value = `${countdown.value}秒后重新获取`
      
      // 设置定时器
      timer = window.setInterval(() => {
        countdown.value--
        sendBtnText.value = `${countdown.value}秒后重新获取`
        
        if (countdown.value <= 0) {
          sendBtnText.value = '发送验证码'
          if (timer) {
            clearInterval(timer)
            timer = null
          }
        }
      }, 1000)
    } else {
      showMessage(response.data.message || '发送验证码失败', false)
    }
  } catch (error: any) {
    console.error('发送验证码失败:', error)
    const errorMessage = error.response?.data?.message || '发送验证码失败，请稍后重试'
    showMessage(errorMessage, false)
  } finally {
    loading.value = false
  }
}

// 处理忘记密码
const handleForgotPassword = () => {
  router.push('/merchant/forget-password')
}

// 登录逻辑
const login = async () => {
  // 验证输入
  if (!email.value.trim()) {
    showMessage('请输入邮箱', false)
    return
  }
  
  if (!validateEmail(email.value)) {
    showMessage('请输入有效的邮箱地址', false)
    return
  }
  
  if (isPasswordLogin.value) {
    if (!password.value) {
      showMessage('请输入密码', false)
      return
    }
    if (!validatePassword(password.value)) {
      showMessage('密码长度至少为6位', false)
      return
    }
  } else {
    if (!verifyCode.value.trim()) {
      showMessage('请输入验证码', false)
      return
    }
  }
  
  loading.value = true
  
  try {
    let response
    if (isPasswordLogin.value) {
      response = await merchantApi.loginWithPassword(email.value, password.value)
    } else {
      response = await merchantApi.login(email.value, verifyCode.value)
    }
    
    if (response.data.success) {
      console.log('登录响应数据:', response.data)
      
      // 保存认证信息到store
      await authStore.setAuthData(response.data as LoginResponse)
      
      showMessage('登录成功，正在跳转...', true)
      
      // 延迟跳转，确保状态已设置
      setTimeout(() => {
        router.push('/merchant/dashboard')
      }, 500)
    } else {
      showMessage(response.data.message || '登录失败', false)
    }
  } catch (error: any) {
    console.error('登录失败:', error)
    if (error.response?.data?.message) {
      showMessage(error.response.data.message, false)
    } else {
      showMessage('登录失败，请稍后重试', false)
    }
  } finally {
    loading.value = false
  }
}

// 组件挂载时检查是否已登录
onMounted(() => {
  // 初始化认证状态
  authStore.initializeAuth()
  
  // 如果已登录，直接跳转到dashboard
  if (authStore.isLoggedIn && !authStore.isTokenExpired()) {
    router.push('/merchant/dashboard')
  }
})

// 组件卸载时清理定时器
onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
})
</script>

<style scoped>
.merchant-login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.login-box {
  display: flex;
  width: 900px;
  height: 600px;
  background: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

.login-left {
  width: 40%;
  background: #4A90E2;
  padding: 50px;
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.logo {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 30px;
}

.welcome {
  font-size: 28px;
  margin-bottom: 20px;
}

.description {
  text-align: center;
  margin-bottom: 10px;
  font-size: 16px;
  opacity: 0.9;
}

.signup-btn {
  margin-top: 30px;
  padding: 12px 40px;
  border: 2px solid white;
  background: transparent;
  color: white;
  border-radius: 25px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
}

.signup-btn:hover {
  background: white;
  color: #4A90E2;
}

.login-right {
  width: 60%;
  padding: 50px;
  display: flex;
  flex-direction: column;
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.login-type-switch {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
  gap: 10px;
}

.switch-btn {
  padding: 8px 20px;
  border: none;
  background: #f5f5f5;
  color: #666;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.switch-btn.active {
  background: #4A90E2;
  color: white;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  position: relative;
}

.form-control {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.form-control:focus {
  border-color: #4A90E2;
  outline: none;
}

.verification-group {
  display: flex;
  gap: 10px;
}

.verification-input {
  flex: 1;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 16px;
}

.verification-btn {
  padding: 12px 20px;
  background: #4A90E2;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  white-space: nowrap;
  transition: background 0.3s;
}

.verification-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.forgot-password {
  text-align: right;
}

.forgot-password a {
  color: #666;
  text-decoration: none;
  font-size: 14px;
}

.forgot-password a:hover {
  color: #4A90E2;
}

.signin-btn {
  padding: 12px;
  background: #4A90E2;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
}

.signin-btn:hover:not(:disabled) {
  background: #357abd;
}

.signin-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.message {
  padding: 10px;
  border-radius: 5px;
  text-align: center;
  margin-bottom: 15px;
}

.message.error {
  background: #ffe6e6;
  color: #ff4d4d;
}

.message.success {
  background: #e6ffe6;
  color: #00cc00;
}

@media (max-width: 768px) {
  .login-box {
    width: 100%;
    height: auto;
    flex-direction: column;
  }
  
  .login-left {
    width: 100%;
    padding: 30px;
  }
  
  .login-right {
    width: 100%;
    padding: 30px;
  }
}
</style> 