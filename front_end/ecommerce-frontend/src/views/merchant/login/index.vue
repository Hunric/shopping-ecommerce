<template>
  <div class="merchant-login-container">
    <div class="login-box">
      <div class="login-left">
        <div class="logo">Ecommerce-Shopping</div>
        <h2 class="welcome">Welcome Back!</h2>
        <p class="description">请输入您的邮箱进行验证</p>
        <p class="description">登录商家管理后台系统</p>
        <router-link to="/merchant/register">
          <button class="signup-btn">SIGN UP</button>
        </router-link>
      </div>
      <div class="login-right">
        <h2 class="title">Sign in to System</h2>
        <div v-if="message" :class="['message', { 'error': !success, 'success': success }]">
          {{ message }}
        </div>
        <div class="login-form">
          <div class="form-group">
            <input type="email" class="form-control" placeholder="邮箱" v-model="email" />
          </div>
          <div class="verification-group">
            <input type="text" class="verification-input" placeholder="验证码" v-model="verifyCode" />
            <button class="verification-btn" @click="sendVerifyCode" :disabled="countdown > 0">{{ sendBtnText }}</button>
          </div>
          <div class="forgot-password">
            <a href="#">忘记密码?</a>
          </div>
          <button class="signin-btn" @click="login">SIGN IN</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 登录页面逻辑
import { ref } from 'vue';
import { verificationApi } from '@/api/verification';
import { useRouter } from 'vue-router';

const router = useRouter();

// 邮箱和验证码
const email = ref('');
const verifyCode = ref('');

// 倒计时逻辑
const countdown = ref(0);
const sendBtnText = ref('发送验证码');
let timer: number | null = null;

// 提示消息
const message = ref('');
const success = ref(false);

// 显示消息函数
const showMessage = (msg: string, isSuccess: boolean) => {
  message.value = msg;
  success.value = isSuccess;
  
  // 3秒后自动清除消息
  setTimeout(() => {
    message.value = '';
  }, 3000);
};

// 发送邮箱验证码
const sendVerifyCode = async () => {
  // 如果正在倒计时，则不执行
  if (countdown.value > 0) return;
  
  // 验证邮箱格式
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email.value)) {
    showMessage('请输入有效的邮箱地址', false);
    return;
  }
  
  try {
    // 调用验证码API
    await verificationApi.sendVerificationCode(email.value, 'login');
    showMessage('验证码已发送到您的邮箱', true);
    
    // 开始倒计时（60秒）
    countdown.value = 60;
    sendBtnText.value = `${countdown.value}秒后重新获取`;
    
    // 设置定时器
    timer = window.setInterval(() => {
      countdown.value--;
      sendBtnText.value = `${countdown.value}秒后重新获取`;
      
      if (countdown.value <= 0) {
        sendBtnText.value = '发送验证码';
        clearInterval(timer!);
        timer = null;
      }
    }, 1000);
  } catch (error) {
    console.error('发送验证码失败:', error);
    showMessage('发送验证码失败，请稍后重试', false);
  }
};

// 登录逻辑
const login = async () => {
  // 验证邮箱和验证码
  if (!email.value) {
    showMessage('请输入邮箱', false);
    return;
  }
  
  if (!verifyCode.value) {
    showMessage('请输入验证码', false);
    return;
  }
  
  try {
    // 验证验证码
    const response = await verificationApi.verifyCode(email.value, verifyCode.value, 'login');
    
    if (response.data.success) {
      showMessage('验证成功，正在登录...', true);
      
      // 这里可以添加调用登录API的逻辑
      // ...
      
      // 登录成功后跳转到商家后台
      setTimeout(() => {
        router.push('/merchant/dashboard');
      }, 1000);
    } else {
      showMessage(response.data.message || '验证码错误', false);
    }
  } catch (error) {
    console.error('验证失败:', error);
    showMessage('验证失败，请检查验证码是否正确', false);
  }
};
</script>

<style scoped>
.merchant-login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100%;
  background-color: #f5f5f5;
}

.login-box {
  display: flex;
  width: 1000px;
  height: 600px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.login-left {
  width: 45%;
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  background: linear-gradient(135deg, rgba(144, 238, 196, 0.8), rgba(73, 190, 255, 0.8));
  color: white;
  user-select: none;
}

.login-right {
  width: 55%;
  padding: 40px 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.logo {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 30px;
}

.welcome {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 20px;
}

.description {
  font-size: 16px;
  margin-bottom: 5px;
  line-height: 1.5;
}

.signup-btn {
  margin-top: 40px;
  padding: 10px 40px;
  border: 2px solid white;
  border-radius: 30px;
  background: transparent;
  color: white;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.signup-btn:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.title {
  font-size: 30px;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

.form-group {
  margin-bottom: 10px;
}

.form-control {
  width: 100%;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 16px;
  outline: none;
  background-color: #FAFAFA;
  color: #333;
}

.form-control::placeholder {
  font-size: 14px;
  color: #999;
}

.verification-group {
  display: flex;
  margin-bottom: 10px;
}

.verification-input {
  flex: 1;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 16px;
  outline: none;
  margin-right: 10px;
  background-color: #FAFAFA;
  color: #333;
}

.verification-input::placeholder {
  font-size: 14px;
  color: #999;
}

.verification-btn {
  padding: 0 20px;
  background-color: #10b5b8;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.verification-btn:hover {  
  background-color: #0e9a9d;
}

.verification-btn:disabled {  
  background-color: #ccc;  
  cursor: not-allowed;
}

.forgot-password {
  text-align: right;
  margin-bottom: 15px;
}

.forgot-password a {
  color: #10b5b8;
  text-decoration: none;
}

.signin-btn {
  padding: 15px;
  background-color: #10b5b8;
  color: white;
  border: none;
  border-radius: 30px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s;
}

.signin-btn:hover {
  background-color: #0e9a9d;
}

.message {
  padding: 10px;
  margin-bottom: 20px;
  border-radius: 5px;
  text-align: center;
  font-size: 14px;
}

.error {
  background-color: #ffebee;
  color: #d32f2f;
  border: 1px solid #ffcdd2;
}

.success {
  background-color: #e8f5e9;
  color: #388e3c;
  border: 1px solid #c8e6c9;
}
</style> 