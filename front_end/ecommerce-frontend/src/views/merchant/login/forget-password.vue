<template>
  <div class="forget-password-container">
    <div class="forget-password-box">
      <h2 class="title">找回密码</h2>
      
      <div v-if="message" :class="['message', { 'error': !success, 'success': success }]">
        {{ message }}
      </div>
      
      <!-- 步骤指示器 -->
      <div class="steps-container">
        <div class="step" :class="{ 'active': currentStep >= 1, 'completed': currentStep > 1 }">
          <div class="step-number">1</div>
          <div class="step-text">验证邮箱</div>
        </div>
        <div class="step-line" :class="{ 'active': currentStep > 1 }"></div>
        <div class="step" :class="{ 'active': currentStep >= 2, 'completed': currentStep > 2 }">
          <div class="step-number">2</div>
          <div class="step-text">重置密码</div>
        </div>
        <div class="step-line" :class="{ 'active': currentStep > 2 }"></div>
        <div class="step" :class="{ 'active': currentStep >= 3 }">
          <div class="step-number">3</div>
          <div class="step-text">完成</div>
        </div>
      </div>
      
      <!-- 步骤1：输入邮箱并验证 -->
      <div class="step-content" v-if="currentStep === 1">
        <div class="form-group">
          <label>邮箱</label>
          <input 
            type="email" 
            class="form-control" 
            placeholder="请输入注册邮箱" 
            v-model="email"
            :class="{'has-error': emailError}"
          />
          <div class="error-message" v-if="emailError">{{ emailError }}</div>
        </div>
        
        <div class="form-group">
          <label>验证码</label>
          <div class="verify-input-group">
            <input 
              type="text" 
              class="form-control verify-input" 
              placeholder="请输入验证码" 
              v-model="verifyCode"
              :class="{'has-error': verifyCodeError}"
            />
            <button 
              type="button" 
              class="verify-send-btn" 
              @click="sendVerifyCode" 
              :disabled="countdown > 0"
            >
              {{ sendBtnText }}
            </button>
          </div>
          <div class="error-message" v-if="verifyCodeError">{{ verifyCodeError }}</div>
        </div>
        
        <div class="form-actions">
          <button 
            type="button" 
            class="next-btn" 
            @click="verifyEmail"
            :disabled="!email || !verifyCode"
          >
            下一步
          </button>
          <router-link to="/merchant/login" class="back-link">返回登录</router-link>
        </div>
      </div>
      
      <!-- 步骤2：设置新密码 -->
      <div class="step-content" v-if="currentStep === 2">
        <div class="form-group">
          <label>新密码</label>
          <input 
            type="password" 
            class="form-control" 
            placeholder="请输入新密码（至少6位）" 
            v-model="newPassword"
            :class="{'has-error': passwordError}"
          />
          <div class="error-message" v-if="passwordError">{{ passwordError }}</div>
          <div class="password-requirements">
            密码要求：至少6位字符
          </div>
        </div>
        
        <div class="form-group">
          <label>确认密码</label>
          <input 
            type="password" 
            class="form-control" 
            placeholder="请再次输入新密码" 
            v-model="confirmPassword"
            :class="{'has-error': confirmPasswordError}"
          />
          <div class="error-message" v-if="confirmPasswordError">{{ confirmPasswordError }}</div>
        </div>
        
        <div class="form-actions">
          <button 
            type="button" 
            class="next-btn" 
            @click="resetPassword"
            :disabled="!newPassword || !confirmPassword"
          >
            确认修改
          </button>
          <button type="button" class="back-btn" @click="currentStep = 1">上一步</button>
        </div>
      </div>
      
      <!-- 步骤3：完成 -->
      <div class="step-content" v-if="currentStep === 3">
        <div class="success-icon">✓</div>
        <div class="success-message">密码已重置成功</div>
        <div class="form-actions">
          <router-link to="/merchant/login" class="next-btn">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { verificationApi } from '@/api/verification';
import merchantApi from '@/api/merchant';

const router = useRouter();

// 表单数据
const email = ref('');
const verifyCode = ref('');
const newPassword = ref('');
const confirmPassword = ref('');

// 表单错误信息
const emailError = ref('');
const verifyCodeError = ref('');
const passwordError = ref('');
const confirmPasswordError = ref('');

// 消息提示
const message = ref('');
const success = ref(false);

// 验证码倒计时
const countdown = ref(0);
const sendBtnText = computed(() => {
  return countdown.value > 0 ? `重新发送(${countdown.value}s)` : '获取验证码';
});

// 步骤控制
const currentStep = ref(1);

/**
 * 显示消息
 */
const showMessage = (msg: string, isSuccess: boolean = true) => {
  message.value = msg;
  success.value = isSuccess;
  
  // 自动清除消息
  setTimeout(() => {
    message.value = '';
  }, 5000);
};

/**
 * 验证邮箱格式
 */
const validateEmail = (email: string): boolean => {
  const regex = /^[a-zA-Z0-9_+&*-]+(?:\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,7}$/;
  return regex.test(email);
};

/**
 * 验证密码格式
 */
const validatePassword = (password: string): boolean => {
  return password.length >= 6;
};

/**
 * 发送验证码
 */
const sendVerifyCode = async () => {
  // 清除错误信息
  emailError.value = '';
  verifyCodeError.value = '';
  
  // 验证邮箱
  if (!email.value) {
    emailError.value = '请输入邮箱';
    return;
  }
  
  if (!validateEmail(email.value)) {
    emailError.value = '请输入正确的邮箱格式';
    return;
  }
  
  try {
    // 发送重置密码验证码
    const response = await merchantApi.sendResetPasswordCode(email.value);
    
    if (response.data.success) {
      showMessage('验证码已发送到您的邮箱，请注意查收');
      
      // 开始倒计时
      countdown.value = 60;
      const timer = setInterval(() => {
        countdown.value--;
        if (countdown.value <= 0) {
          clearInterval(timer);
        }
      }, 1000);
    } else {
      showMessage(response.data.message || '发送验证码失败，请稍后重试', false);
    }
  } catch (error) {
    showMessage('发送验证码失败，请稍后重试', false);
    console.error('发送验证码出错:', error);
  }
};

/**
 * 验证邮箱和验证码
 */
const verifyEmail = async () => {
  // 清除错误信息
  emailError.value = '';
  verifyCodeError.value = '';
  
  // 验证邮箱
  if (!email.value) {
    emailError.value = '请输入邮箱';
    return;
  }
  
  if (!validateEmail(email.value)) {
    emailError.value = '请输入正确的邮箱格式';
    return;
  }
  
  // 验证验证码
  if (!verifyCode.value) {
    verifyCodeError.value = '请输入验证码';
    return;
  }
  
  try {
    // 验证重置密码的验证码
    const response = await merchantApi.verifyResetPasswordCode(email.value, verifyCode.value);
    
    if (response.data.success) {
      // 验证成功，进入下一步
      currentStep.value = 2;
      showMessage('邮箱验证成功，请设置新密码');
    } else {
      showMessage(response.data.message || '验证码错误或已过期', false);
      verifyCodeError.value = '验证码错误或已过期';
    }
  } catch (error) {
    showMessage('验证失败，请稍后重试', false);
    console.error('验证邮箱出错:', error);
  }
};

/**
 * 重置密码
 */
const resetPassword = async () => {
  // 清除错误信息
  passwordError.value = '';
  confirmPasswordError.value = '';
  
  // 验证密码
  if (!newPassword.value) {
    passwordError.value = '请输入新密码';
    return;
  }
  
  if (!validatePassword(newPassword.value)) {
    passwordError.value = '密码长度至少为6位';
    return;
  }
  
  // 验证确认密码
  if (!confirmPassword.value) {
    confirmPasswordError.value = '请确认新密码';
    return;
  }
  
  if (newPassword.value !== confirmPassword.value) {
    confirmPasswordError.value = '两次输入的密码不一致';
    return;
  }
  
  try {
    // 提交重置密码请求
    const response = await merchantApi.resetPassword(email.value, newPassword.value, verifyCode.value);
    
    if (response.data.success) {
      // 密码重置成功，进入下一步
      currentStep.value = 3;
      showMessage('密码重置成功');
    } else {
      showMessage(response.data.message || '密码重置失败，请稍后重试', false);
    }
  } catch (error) {
    showMessage('密码重置失败，请稍后重试', false);
    console.error('重置密码出错:', error);
  }
};
</script>

<style scoped>
.forget-password-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px;
}

.forget-password-box {
  width: 100%;
  max-width: 500px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
  font-size: 24px;
}

.message {
  padding: 10px 15px;
  border-radius: 4px;
  margin-bottom: 20px;
  text-align: center;
}

.message.error {
  background-color: #fef0f0;
  color: #f56c6c;
  border: 1px solid #fbc4c4;
}

.message.success {
  background-color: #f0f9eb;
  color: #67c23a;
  border: 1px solid #c2e7b0;
}

.steps-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  z-index: 1;
}

.step-number {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background-color: #ebeef5;
  color: #909399;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 8px;
  font-weight: bold;
  transition: all 0.3s;
}

.step.active .step-number {
  background-color: #409eff;
  color: #fff;
}

.step.completed .step-number {
  background-color: #67c23a;
  color: #fff;
}

.step-text {
  font-size: 14px;
  color: #909399;
  transition: all 0.3s;
}

.step.active .step-text,
.step.completed .step-text {
  color: #303133;
  font-weight: 500;
}

.step-line {
  flex: 1;
  height: 2px;
  background-color: #ebeef5;
  margin: 0 10px;
  position: relative;
  top: -14px;
  z-index: 0;
  transition: all 0.3s;
}

.step-line.active {
  background-color: #67c23a;
}

.step-content {
  margin-top: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #606266;
}

.form-control {
  width: 100%;
  height: 40px;
  line-height: 40px;
  padding: 0 15px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  transition: all 0.2s;
  font-size: 14px;
}

.form-control:focus {
  outline: none;
  border-color: #409eff;
}

.form-control.has-error {
  border-color: #f56c6c;
}

.error-message {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 5px;
}

.verify-input-group {
  display: flex;
  gap: 10px;
}

.verify-input {
  flex: 1;
}

.verify-send-btn {
  width: 120px;
  height: 40px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.verify-send-btn:hover {
  background-color: #66b1ff;
}

.verify-send-btn:disabled {
  background-color: #a0cfff;
  cursor: not-allowed;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 30px;
}

.next-btn, .back-btn {
  height: 40px;
  padding: 0 20px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
}

.next-btn {
  background-color: #409eff;
  color: white;
  border: none;
}

.next-btn:hover {
  background-color: #66b1ff;
}

.next-btn:disabled {
  background-color: #a0cfff;
  cursor: not-allowed;
}

.back-btn {
  background-color: #f4f4f5;
  color: #606266;
  border: 1px solid #dcdfe6;
}

.back-btn:hover {
  background-color: #e9e9eb;
}

.back-link {
  color: #409eff;
  font-size: 14px;
  text-decoration: none;
}

.back-link:hover {
  color: #66b1ff;
  text-decoration: underline;
}

.success-icon {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  background-color: #f0f9eb;
  color: #67c23a;
  font-size: 40px;
  border-radius: 50%;
  border: 2px solid #67c23a;
}

.success-message {
  text-align: center;
  font-size: 18px;
  color: #303133;
  margin-bottom: 30px;
}

.password-requirements {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style> 