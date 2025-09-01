<template>
  <div class="forgot-password-container">
    <BackToHomeButton position="top-left" :fixed="true" />
    <div class="forgot-password-card">
      <div class="forgot-password-header">
        <h2>忘记密码</h2>
        <p>请输入您的邮箱地址，我们将发送重置密码的验证码</p>
      </div>
      
      <el-steps :active="currentStep" finish-status="success" class="steps">
        <el-step title="验证邮箱" />
        <el-step title="验证码确认" />
        <el-step title="重置密码" />
      </el-steps>
      
      <!-- 步骤1：输入邮箱 -->
      <div v-if="currentStep === 0" class="step-content">
        <el-form
          ref="emailFormRef"
          :model="emailForm"
          :rules="emailRules"
          label-width="0"
          class="form"
        >
          <el-form-item prop="email">
            <el-input
              v-model="emailForm.email"
              type="email"
              placeholder="请输入邮箱地址"
              prefix-icon="Message"
              clearable
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              :loading="loading"
              @click="sendResetCode"
              class="action-btn"
            >
              发送验证码
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 步骤2：验证码确认 -->
      <div v-if="currentStep === 1" class="step-content">
        <el-form
          ref="codeFormRef"
          :model="codeForm"
          :rules="codeRules"
          label-width="0"
          class="form"
        >
          <el-form-item prop="verificationCode">
            <div class="code-input-group">
              <el-input
                v-model="codeForm.verificationCode"
                placeholder="请输入6位验证码"
                prefix-icon="Key"
                clearable
              />
              <el-button
                :disabled="codeCountdown > 0"
                :loading="sendingCode"
                @click="resendCode"
                class="resend-btn"
              >
                {{ codeCountdown > 0 ? `${codeCountdown}s` : '重新发送' }}
              </el-button>
            </div>
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              :loading="loading"
              @click="verifyCode"
              class="action-btn"
            >
              验证
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 步骤3：重置密码 -->
      <div v-if="currentStep === 2" class="step-content">
        <el-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordRules"
          label-width="0"
          class="form"
        >
          <el-form-item prop="newPassword">
            <el-input
              v-model="passwordForm.newPassword"
              type="password"
              placeholder="请输入新密码（至少6位）"
              prefix-icon="Lock"
              show-password
              clearable
            />
          </el-form-item>
          
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="passwordForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              prefix-icon="Lock"
              show-password
              clearable
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              :loading="loading"
              @click="resetPassword"
              class="action-btn"
            >
              重置密码
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <div class="footer">
        <p>
          想起密码了？
          <router-link to="/user/login" class="login-link">返回登录</router-link>
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
  sendResetPasswordCode,
  verifyResetPasswordCode,
  resetPassword as resetPasswordApi
} from '@/api/user/auth'
import BackToHomeButton from '@/components/common/BackToHomeButton.vue'

const router = useRouter()

// 响应式数据
const currentStep = ref(0)
const loading = ref(false)
const sendingCode = ref(false)
const codeCountdown = ref(0)

const emailFormRef = ref<FormInstance>()
const codeFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

// 表单数据
const emailForm = reactive({
  email: ''
})

const codeForm = reactive({
  verificationCode: ''
})

const passwordForm = reactive({
  newPassword: '',
  confirmPassword: ''
})

// 确认密码验证器
const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

// 表单验证规则
const emailRules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const codeRules: FormRules = {
  verificationCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位数字', trigger: 'blur' }
  ]
}

const passwordRules: FormRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 发送重置密码验证码
const sendResetCode = async () => {
  if (!emailFormRef.value) return
  
  try {
    await emailFormRef.value.validate()
    loading.value = true
    
    const response = await sendResetPasswordCode(emailForm.email)
    
    if (response.data.success) {
      ElMessage.success('验证码已发送到您的邮箱')
      currentStep.value = 1
      startCountdown()
    } else {
      ElMessage.error(response.data.message || '发送失败')
    }
  } catch (error: any) {
    console.error('发送验证码失败:', error)
    ElMessage.error(error.message || '发送失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 重新发送验证码
const resendCode = async () => {
  try {
    sendingCode.value = true
    
    const response = await sendResetPasswordCode(emailForm.email)
    
    if (response.data.success) {
      ElMessage.success('验证码已重新发送')
      startCountdown()
    } else {
      ElMessage.error(response.data.message || '发送失败')
    }
  } catch (error: any) {
    console.error('重新发送验证码失败:', error)
    ElMessage.error(error.message || '发送失败，请稍后重试')
  } finally {
    sendingCode.value = false
  }
}

// 验证验证码
const verifyCode = async () => {
  if (!codeFormRef.value) return
  
  try {
    await codeFormRef.value.validate()
    loading.value = true
    
    const response = await verifyResetPasswordCode(emailForm.email, codeForm.verificationCode)
    
    if (response.data.success) {
      ElMessage.success('验证码验证成功')
      currentStep.value = 2
    } else {
      ElMessage.error(response.data.message || '验证码错误')
    }
  } catch (error: any) {
    console.error('验证码验证失败:', error)
    ElMessage.error(error.message || '验证失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 重置密码
const resetPassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    loading.value = true
    
    const response = await resetPasswordApi(emailForm.email, passwordForm.newPassword, codeForm.verificationCode)
    
    if (response.data.success) {
      ElMessage.success('密码重置成功，请使用新密码登录')
      router.push('/user/login')
    } else {
      ElMessage.error(response.data.message || '重置失败')
    }
  } catch (error: any) {
    console.error('重置密码失败:', error)
    ElMessage.error(error.message || '重置失败，请稍后重试')
  } finally {
    loading.value = false
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
</script>

<style scoped>
.forgot-password-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.forgot-password-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 500px;
}

.forgot-password-header {
  text-align: center;
  margin-bottom: 30px;
}

.forgot-password-header h2 {
  color: #333;
  margin-bottom: 8px;
  font-size: 28px;
  font-weight: 600;
}

.forgot-password-header p {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

.steps {
  margin-bottom: 30px;
}

.step-content {
  margin-bottom: 20px;
}

.form {
  margin-bottom: 20px;
}

.code-input-group {
  display: flex;
  gap: 10px;
}

.code-input-group .el-input {
  flex: 1;
}

.resend-btn {
  white-space: nowrap;
}

.action-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
  font-weight: 600;
}

.footer {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.footer p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.login-link {
  color: #409eff;
  text-decoration: none;
  font-weight: 500;
}

.login-link:hover {
  color: #66b1ff;
  text-decoration: underline;
}
</style> 