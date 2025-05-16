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
</template>

<script setup lang="ts">
// 登录页面逻辑
import { ref } from 'vue';

// 邮箱和验证码
const email = ref('');
const verifyCode = ref('');

// 倒计时逻辑
const countdown = ref(0);
const sendBtnText = ref('发送验证码');
let timer: number | null = null;

// 发送邮箱验证码
const sendVerifyCode = () => {
  // 如果正在倒计时，则不执行
  if (countdown.value > 0) return;
  
  // 验证邮箱格式
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email.value)) {
    console.error('请输入有效的邮箱地址');
    return;
  }
  
  // 模拟发送验证码的API调用
  // 实际项目中替换为真实的API调用
  console.log('发送邮箱验证码到:', email.value);
  
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
};

// 登录逻辑
const login = () => {
  // 验证邮箱和验证码
  if (!email.value) {
    console.error('请输入邮箱');
    return;
  }
  
  if (!verifyCode.value) {
    console.error('请输入验证码');
    return;
  }
  
  // 这里调用登录API
  console.log('登录请求:', { email: email.value, verifyCode: verifyCode.value });
  // 实际项目需要连接后端API
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
  margin-bottom: 40px;
  text-align: center;
}

.form-group {
  margin-bottom: 20px;
}

.form-control {
  width: 100%;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 16px;
  outline: none;
  background-color: #F2F2F2;
  color: #333;
}

.verification-group {
  display: flex;
  margin-bottom: 20px;
}

.verification-input {
  flex: 1;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 16px;
  outline: none;
  margin-right: 10px;
  background-color: #F2F2F2;
  color: #333;
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
  margin-bottom: 30px;
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
</style> 