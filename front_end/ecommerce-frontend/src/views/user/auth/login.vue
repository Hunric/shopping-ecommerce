<template>
  <div class="login-container">
    <!-- 返回主页按钮 -->
    
    <div class="login-card">
      <div class="login-header">
        <h2>用户登录</h2>
        <p>欢迎回来，请登录您的账户</p>
      </div>
      
      <el-tabs v-model="activeTab" class="login-tabs">
        <!-- 验证码登录 -->
        <el-tab-pane label="验证码登录" name="code">
          <el-form
            ref="codeFormRef"
            :model="codeForm"
            :rules="codeRules"
            label-width="0"
            class="login-form"
          >
            <el-form-item prop="email">
              <el-input
                v-model="codeForm.email"
                type="email"
                placeholder="请输入邮箱地址"
                prefix-icon="Message"
                clearable
              />
            </el-form-item>
            
            <el-form-item prop="verificationCode">
              <div class="code-input-group">
                <el-input
                  v-model="codeForm.verificationCode"
                  placeholder="请输入验证码"
                  prefix-icon="Key"
                  clearable
                />
                <el-button
                  :disabled="codeCountdown > 0"
                  :loading="sendingCode"
                  @click="sendVerificationCode"
                  class="send-code-btn"
                >
                  {{ codeCountdown > 0 ? `${codeCountdown}s` : '发送验证码' }}
                </el-button>
              </div>
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                :loading="loading"
                @click="handleCodeLogin"
                class="login-btn"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <!-- 密码登录 -->
        <el-tab-pane label="密码登录" name="password">
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="0"
            class="login-form"
          >
            <el-form-item prop="email">
              <el-input
                v-model="passwordForm.email"
                type="email"
                placeholder="请输入邮箱地址"
                prefix-icon="Message"
                clearable
              />
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input
                v-model="passwordForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                show-password
                clearable
              />
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                :loading="loading"
                @click="handlePasswordLogin"
                class="login-btn"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      
      <div class="login-footer">
        <div class="footer-links">
          <router-link to="/user/forgot-password" class="forgot-link">忘记密码？</router-link>
        </div>
        <p>
          还没有账户？
          <router-link to="/user/register" class="register-link">立即注册</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import {
  loginWithCode,
  loginWithPassword,
  sendLoginCode,
  type UserLoginRequest,
  type UserPasswordLoginRequest
} from '@/api/user/auth'
import { useUserAuthStore } from '@/store/modules/userAuth'
import BackToHomeButton from '@/components/common/BackToHomeButton.vue'

const router = useRouter()
const userAuthStore = useUserAuthStore()
const activeTab = ref('code')
const loading = ref(false)
const sendingCode = ref(false)
const codeCountdown = ref(0)

const codeFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

// 验证码登录表单
const codeForm = reactive<UserLoginRequest>({
  email: '',
  verificationCode: ''
})

// 密码登录表单
const passwordForm = reactive<UserPasswordLoginRequest>({
  email: '',
  password: ''
})

// 验证码登录规则
const codeRules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  verificationCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位数字', trigger: 'blur' }
  ]
}

// 密码登录规则
const passwordRules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
  ]
}

// 发送验证码
const sendVerificationCode = async () => {
  if (!codeForm.email) {
    ElMessage.warning('请先输入邮箱地址')
    return
  }
  
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(codeForm.email)) {
    ElMessage.warning('请输入正确的邮箱地址')
    return
  }
  
  try {
    sendingCode.value = true
    console.log('=== 发送登录验证码 ===')
    console.log('邮箱:', codeForm.email)
    
    const response = await sendLoginCode({ email: codeForm.email })
    console.log('发送验证码API响应:', response)
    
    const apiResponse = response.data
    console.log('发送验证码API数据:', apiResponse)
    
    if (apiResponse.success) {
      ElMessage.success(apiResponse.message || '验证码已发送')
      startCountdown()
    } else {
      ElMessage.error(apiResponse.message || '发送失败')
    }
  } catch (error: any) {
    console.error('发送验证码失败:', error)
    ElMessage.error(error.message || '发送失败，请稍后重试')
  } finally {
    sendingCode.value = false
  }
}

// 开始倒计时
const startCountdown = () => {
  codeCountdown.value = 60
  const timer = setInterval(() => {
    codeCountdown.value--
    if (codeCountdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

// 验证码登录
const handleCodeLogin = async () => {
  if (!codeFormRef.value) return
  
  try {
    await codeFormRef.value.validate()
    loading.value = true
    
    console.log('=== 开始验证码登录 ===')
    console.log('登录数据:', codeForm)
    
    const response = await loginWithCode(codeForm)
    console.log('验证码登录API响应:', response)
    
    const apiResponse = response.data
    console.log('验证码登录API数据:', apiResponse)
    
    if (apiResponse.success) {
      ElMessage.success(apiResponse.message || '登录成功')
      
      console.log('=== 用户验证码登录成功 ===')
      console.log('登录响应数据:', apiResponse)
      
      // 使用userAuth store设置认证状态
      userAuthStore.setAuthData({
        success: apiResponse.success,
        message: apiResponse.message || '',
        accessToken: apiResponse.accessToken || '',
        refreshToken: apiResponse.refreshToken || '',
        tokenType: apiResponse.tokenType || 'Bearer',
        expiresIn: apiResponse.expiresIn || 3600,
        userId: apiResponse.userId || 0,
        username: apiResponse.username || '',
        email: apiResponse.email || '',
        phone: apiResponse.phone,
        avatar: apiResponse.avatarUrl
      })
      
      // 跳转到首页
      console.log('正在跳转到首页...')
      await router.push('/')
      
    } else {
      ElMessage.error(apiResponse.message || '登录失败')
    }
  } catch (error: any) {
    console.error('登录失败:', error)
    ElMessage.error(error.message || '登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 密码登录
const handlePasswordLogin = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    loading.value = true
    
    console.log('=== 开始密码登录 ===')
    console.log('登录数据:', passwordForm)
    
    const response = await loginWithPassword(passwordForm)
    console.log('密码登录API响应:', response)
    
    const apiResponse = response.data
    console.log('密码登录API数据:', apiResponse)
    
    if (apiResponse.success) {
      ElMessage.success(apiResponse.message || '登录成功')
      
      console.log('=== 用户密码登录成功 ===')
      console.log('登录响应数据:', apiResponse)
      
      // 使用userAuth store设置认证状态
      userAuthStore.setAuthData({
        success: apiResponse.success,
        message: apiResponse.message || '',
        accessToken: apiResponse.accessToken || '',
        refreshToken: apiResponse.refreshToken || '',
        tokenType: apiResponse.tokenType || 'Bearer',
        expiresIn: apiResponse.expiresIn || 3600,
        userId: apiResponse.userId || 0,
        username: apiResponse.username || '',
        email: apiResponse.email || '',
        phone: apiResponse.phone,
        avatar: apiResponse.avatarUrl
      })
      
      // 跳转到首页
      console.log('正在跳转到首页...')
      await router.push('/')
      
    } else {
      ElMessage.error(apiResponse.message || '登录失败')
    }
  } catch (error: any) {
    console.error('登录失败:', error)
    ElMessage.error(error.message || '登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 450px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  color: #333;
  margin-bottom: 8px;
  font-size: 28px;
  font-weight: 600;
}

.login-header p {
  color: #666;
  font-size: 14px;
}

.login-tabs {
  margin-bottom: 20px;
}

.login-form {
  margin-top: 20px;
}

.code-input-group {
  display: flex;
  gap: 10px;
}

.code-input-group .el-input {
  flex: 1;
}

.send-code-btn {
  white-space: nowrap;
  min-width: 100px;
}

.login-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
  font-weight: 500;
}

.login-footer {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.footer-links {
  margin-bottom: 15px;
}

.forgot-link {
  color: #409eff;
  text-decoration: none;
  font-size: 14px;
}

.forgot-link:hover {
  text-decoration: underline;
}

.login-footer p {
  color: #666;
  font-size: 14px;
}

.register-link {
  color: #409eff;
  text-decoration: none;
  font-weight: 500;
}

.register-link:hover {
  text-decoration: underline;
}

:deep(.el-input__inner) {
  height: 45px;
  border-radius: 6px;
}

:deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 500;
}

:deep(.el-tabs__active-bar) {
  background-color: #409eff;
}

:deep(.el-tabs__item.is-active) {
  color: #409eff;
}
</style>