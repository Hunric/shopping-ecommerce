<template>
  <div class="merchant-register-container">
    <!-- 左侧跟随卡片 -->
    <div class="floating-left-card">
      <div class="logo">Ecommerce-Shopping</div>
      <h2 class="welcome">Hello, Friend!</h2>
      <p class="description">已有账号?</p>
      <p class="description">返回登录页面</p>
      <router-link to="/merchant/login">
        <button class="signin-btn">SIGN IN</button>
      </router-link>
      
      <!-- 注册按钮和协议勾选框 -->
      <div class="card-divider"></div>
      <div class="agreement">
        <input type="checkbox" id="agreement" />
        <label for="agreement">我已阅读并同意<a href="/static/agreements/merchant-agreement.html" target="_blank" class="agreement-link">《商家入驻协议》</a></label>
      </div>
      <button type="submit" class="submit-btn">注册</button>
    </div>
    
    <div class="register-box">
      <div class="register-right">
        <h2 class="title">注册商家账号</h2>
        <div class="form-container">
          <form class="register-form">
            <!-- 基本信息 -->
            <div class="form-section">
              <h3 class="section-title">基本信息</h3>
              <div class="form-row">
                <div class="form-group">
                  <label>商家名称</label>
                  <input type="text" class="form-control" placeholder="请输入商家名称" />
                </div>
                <div class="form-group">
                  <label>商家类型</label>
                  <select class="form-control">
                    <option value="">请选择商家类型</option>
                    <option value="enterprise">企业商家</option>
                    <option value="individual">个体商家</option>
                  </select>
                </div>
              </div>
              
              <div class="form-row">
                <div class="form-group">
                  <label>手机号</label>
                  <input type="text" class="form-control" placeholder="请输入手机号" />
                </div>
                <div class="form-group">
                  <label>邮箱</label>
                  <input type="email" class="form-control" placeholder="请输入邮箱" />
                </div>
              </div>
              
              <div class="form-row">
                <div class="form-group">
                  <label>密码</label>
                  <input type="password" class="form-control" placeholder="请输入密码" />
                </div>
                <div class="form-group">
                  <label>确认密码</label>
                  <input type="password" class="form-control" placeholder="请再次输入密码" />
                </div>
              </div>
            </div>
            
            <!-- 营业执照信息 -->
            <div class="form-section">
              <h3 class="section-title">营业执照信息</h3>
              <div class="form-row">
                <div class="form-group">
                  <label>营业执照编号 (统一社会信用代码)</label>
                  <input type="text" class="form-control" placeholder="请输入18位统一社会信用代码" />
                </div>
              </div>
              
              <div class="form-row">
                <div class="form-group">
                  <label>法人姓名</label>
                  <input type="text" class="form-control" placeholder="请输入法人姓名" />
                </div>
                <div class="form-group">
                  <label>法人身份证号</label>
                  <input type="text" class="form-control" placeholder="请输入18位法人身份证号" />
                </div>
              </div>
            </div>
            
            <!-- 联系人信息 -->
            <div class="form-section">
              <h3 class="section-title">联系人信息</h3>
              <div class="form-row">
                <div class="form-group">
                  <label>联系人姓名</label>
                  <input type="text" class="form-control" placeholder="请输入联系人姓名" />
                </div>
                <div class="form-group">
                  <label>联系电话</label>
                  <input type="text" class="form-control" placeholder="请输入联系电话" />
                </div>
              </div>
              
              <div class="form-row">
                <div class="form-group">
                  <label>联系邮箱</label>
                  <input type="email" class="form-control" placeholder="请输入联系邮箱" />
                </div>
              </div>
            </div>
            
            <!-- 地址信息 -->
            <div class="form-section">
              <h3 class="section-title">地址信息</h3>
              <div class="form-row">
                <div class="form-group">
                  <label>省</label>
                  <select class="form-control">
                    <option value="">请选择省份</option>
                    <option value="北京">北京市</option>
                    <option value="上海">上海市</option>
                    <!-- 其他省份选项 -->
                  </select>
                </div>
                <div class="form-group">
                  <label>市</label>
                  <select class="form-control">
                    <option value="">请选择城市</option>
                    <!-- 城市选项会根据省份动态加载 -->
                  </select>
                </div>
              </div>
              
              <div class="form-row">
                <div class="form-group">
                  <label>县/区</label>
                  <select class="form-control">
                    <option value="">请选择县/区</option>
                    <!-- 县区选项会根据城市动态加载 -->
                  </select>
                </div>
                <div class="form-group">
                  <label>详细地址</label>
                  <input type="text" class="form-control" placeholder="请输入详细地址" />
                </div>
              </div>
            </div>
            
            <!-- 在表单底部添加占位空间，避免底部固定栏遮挡内容 -->
            <div class="form-section-spacer"></div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 注册页面逻辑
</script>

<style scoped>
.merchant-register-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 100vh;
  width: 100%;
  background-color: #f5f5f5;
  padding: 40px 0;
  position: relative;
}

.register-box {
  width: 900px;
  min-height: 800px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  margin-left: 300px;
}

/* 左侧浮动卡片 */
.floating-left-card {
  position: fixed;
  left: calc(50% - 600px);
  top: 80px;
  width: 260px;
  padding: 30px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  background: linear-gradient(135deg, rgba(144, 238, 196, 0.8), rgba(73, 190, 255, 0.8));
  color: white;
  user-select: none;
  border-radius: 20px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);
  z-index: 90;
  transition: all 0.3s ease;
}

.card-divider {
  width: 100%;
  height: 1px;
  background-color: rgba(255, 255, 255, 0.5);
  margin: 40px 0 20px;
}

.register-right {
  width: 100%;
  padding: 40px;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
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

.signin-btn {
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

.signin-btn:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.title {
  font-size: 30px;
  color: #333;
  margin-bottom: 30px;
  text-align: center;
}

.form-container {
  flex: 1;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.form-section {
  background-color: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.form-section-spacer {
  height: 20px; /* 为固定底栏留出额外空间 */
}

.section-title {
  font-size: 18px;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.form-group {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-size: 14px;
  margin-bottom: 5px;
  color: #555;
}

.form-control {
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
  background-color: #F2F2F2;
  color: #333;
}

.form-control:focus {
  border-color: #10b5b8;
}

select.form-control {
  appearance: none;
  background-image: url("data:image/svg+xml;charset=utf8,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%23888' d='M10.3 3.3L6 7.6 1.7 3.3c-.4-.4-1-.4-1.4 0s-.4 1 0 1.4l5 5c.2.2.4.3.7.3s.5-.1.7-.3l5-5c.4-.4.4-1 0-1.4s-1-.4-1.4 0z'/%3E%3C/svg%3E");
  background-position: right 10px center;
  background-repeat: no-repeat;
  padding-right: 30px;
  background-color: #F2F2F2;
  color: #333;
}

.agreement {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-bottom: 15px;
}

.agreement input[type="checkbox"] {
  width: 16px;
  height: 16px;
}

.agreement label {
  font-size: 14px;
  color: white;
}

.agreement-link {
  color: white;
  text-decoration: underline;
  font-weight: bold;
}

.agreement-link:hover {
  color: #f0f0f0;
}

.submit-btn {
  padding: 12px 0;
  width: 100%;
  background-color: white;
  color: #10b5b8;
  border: none;
  border-radius: 30px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  background-color: rgba(255, 255, 255, 0.9);
}

/* 响应式适配 */
@media (max-width: 1400px) {
  .floating-left-card {
    left: 40px;
  }
  
  .register-box {
    margin-left: 0;
    width: 800px;
  }
}

@media (max-width: 1200px) {
  .register-box {
    width: 95%;
    max-width: 800px;
  }
  
  .floating-left-card {
    left: 20px;
    width: 220px;
    padding: 20px;
  }
}

@media (max-width: 992px) {
  .merchant-register-container {
    flex-direction: column;
    align-items: center;
  }
  
  .floating-left-card {
    position: relative;
    left: 0;
    top: 0;
    width: 95%;
    max-width: 800px;
    margin-bottom: 20px;
    padding: 25px;
  }
  
  .register-box {
    margin-left: 0;
  }
}

@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: 15px;
  }
  
  .welcome {
    font-size: 28px;
  }
  
  .card-divider {
    margin: 20px 0 15px;
  }
}
</style> 