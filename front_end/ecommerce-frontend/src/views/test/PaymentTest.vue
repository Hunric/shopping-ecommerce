<template>
  <div class="payment-test">
    <!-- 返回主页按钮 -->
    <BackToHomeButton :fixed="true" position="top-right" size="small" />
    
    <h1>模拟支付功能测试</h1>
    
    <div class="test-section">
      <h2>功能特性</h2>
      <ul class="feature-list">
        <li>✅ 支持支付宝模拟支付（二维码扫描）</li>
        <li>✅ 支持微信支付模拟支付（二维码扫描）</li>
        <li>✅ 支持银行卡支付（表单填写）</li>
        <li>✅ 真实的支付流程体验</li>
        <li>✅ 支付状态实时反馈</li>
        <li>✅ 支付成功/失败处理</li>
        <li>✅ 订单状态自动更新</li>
        <li>✅ 响应式设计，支持移动端</li>
      </ul>
    </div>
    
    <div class="test-section">
      <h2>支付流程测试</h2>
      <div class="flow-steps">
        <div class="step">
          <div class="step-number">1</div>
          <div class="step-content">
            <h4>创建测试订单</h4>
            <p>在购物车中添加商品，进入结算页面</p>
            <el-button @click="goToCart" type="primary">前往购物车</el-button>
          </div>
        </div>
        
        <div class="step">
          <div class="step-number">2</div>
          <div class="step-content">
            <h4>提交订单</h4>
            <p>填写收货地址，选择支付方式，提交订单</p>
            <el-button @click="goToCheckout" type="primary">前往结算页面</el-button>
          </div>
        </div>
        
        <div class="step">
          <div class="step-number">3</div>
          <div class="step-content">
            <h4>进入支付页面</h4>
            <p>订单提交成功后自动跳转到支付页面</p>
            <el-button @click="goToOrders" type="primary">查看我的订单</el-button>
          </div>
        </div>
        
        <div class="step">
          <div class="step-number">4</div>
          <div class="step-content">
            <h4>完成支付</h4>
            <p>选择支付方式，完成模拟支付流程</p>
            <el-button @click="testDirectPayment" type="success">直接测试支付</el-button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="test-section">
      <h2>支付方式详情</h2>
      <div class="payment-methods">
        <div class="method-card alipay">
          <div class="method-icon">💰</div>
          <h3>支付宝支付</h3>
          <ul>
            <li>显示模拟二维码</li>
            <li>扫码支付体验</li>
            <li>支付状态实时更新</li>
            <li>90%成功率模拟</li>
          </ul>
        </div>
        
        <div class="method-card wechat">
          <div class="method-icon">💚</div>
          <h3>微信支付</h3>
          <ul>
            <li>微信风格二维码</li>
            <li>扫码支付体验</li>
            <li>支付状态实时更新</li>
            <li>90%成功率模拟</li>
          </ul>
        </div>
        
        <div class="method-card bank">
          <div class="method-icon">🏦</div>
          <h3>银行卡支付</h3>
          <ul>
            <li>银行卡信息表单</li>
            <li>卡号格式化输入</li>
            <li>表单验证</li>
            <li>95%成功率模拟</li>
          </ul>
        </div>
      </div>
    </div>
    
    <div class="test-section">
      <h2>技术实现</h2>
      <div class="tech-details">
        <div class="tech-item">
          <h4>前端技术</h4>
          <ul>
            <li>Vue 3 + TypeScript</li>
            <li>Element Plus UI组件</li>
            <li>响应式设计</li>
            <li>动画效果</li>
          </ul>
        </div>
        
        <div class="tech-item">
          <h4>支付模拟</h4>
          <ul>
            <li>二维码动态生成</li>
            <li>支付状态机</li>
            <li>随机成功率</li>
            <li>延时模拟</li>
          </ul>
        </div>
        
        <div class="tech-item">
          <h4>用户体验</h4>
          <ul>
            <li>加载状态提示</li>
            <li>错误处理</li>
            <li>成功反馈</li>
            <li>流程引导</li>
          </ul>
        </div>
      </div>
    </div>
    
    <div class="test-section">
      <h2>测试说明</h2>
      <div class="test-notes">
        <div class="note-item warning">
          <h4>⚠️ 注意事项</h4>
          <ul>
            <li>这是模拟支付功能，不会产生真实的资金流动</li>
            <li>支付成功率为模拟设置，实际项目中需要对接真实支付接口</li>
            <li>二维码为演示用途，无法真实扫描</li>
          </ul>
        </div>
        
        <div class="note-item info">
          <h4>💡 测试建议</h4>
          <ul>
            <li>测试不同支付方式的用户体验</li>
            <li>测试支付失败的处理流程</li>
            <li>测试移动端的响应式效果</li>
            <li>测试支付成功后的订单状态更新</li>
          </ul>
        </div>
      </div>
    </div>
    
    <div class="test-section">
      <h2>快速测试</h2>
      <div class="quick-test">
        <p>如果您有待支付的订单，可以直接测试支付功能：</p>
        <div class="test-input">
          <el-input 
            v-model="testOrderId" 
            placeholder="请输入订单ID"
            style="width: 200px; margin-right: 10px;"
          />
          <el-button @click="goToPayment" type="primary" :disabled="!testOrderId">
            前往支付页面
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElButton, ElInput } from 'element-plus'
import BackToHomeButton from '@/components/common/BackToHomeButton.vue'

const router = useRouter()
const testOrderId = ref('')

const goToCart = () => {
  router.push('/cart')
}

const goToCheckout = () => {
  router.push('/checkout')
}

const goToOrders = () => {
  router.push('/user/orders')
}

const goToPayment = () => {
  if (!testOrderId.value) {
    ElMessage.warning('请输入订单ID')
    return
  }
  router.push(`/payment/${testOrderId.value}`)
}

const testDirectPayment = () => {
  ElMessage.info('请先创建一个待支付的订单，然后使用订单ID进行测试')
}
</script>

<style scoped>
.payment-test {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

h1 {
  color: #333;
  text-align: center;
  margin-bottom: 30px;
  font-size: 28px;
}

.test-section {
  background: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.test-section h2 {
  color: #409eff;
  margin-bottom: 20px;
  font-size: 20px;
  border-bottom: 2px solid #409eff;
  padding-bottom: 8px;
}

.feature-list {
  list-style: none;
  padding: 0;
}

.feature-list li {
  padding: 8px 0;
  font-size: 16px;
  color: #606266;
}

.flow-steps {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.step {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.step-number {
  width: 40px;
  height: 40px;
  background: #409eff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 18px;
  flex-shrink: 0;
}

.step-content {
  flex: 1;
}

.step-content h4 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 18px;
}

.step-content p {
  margin: 0 0 15px 0;
  color: #666;
  line-height: 1.5;
}

.payment-methods {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.method-card {
  padding: 25px;
  border-radius: 12px;
  text-align: center;
  color: white;
  position: relative;
  overflow: hidden;
}

.method-card.alipay {
  background: linear-gradient(135deg, #1677ff, #69c0ff);
}

.method-card.wechat {
  background: linear-gradient(135deg, #07c160, #52c41a);
}

.method-card.bank {
  background: linear-gradient(135deg, #722ed1, #b37feb);
}

.method-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.method-card h3 {
  margin: 0 0 15px 0;
  font-size: 20px;
}

.method-card ul {
  list-style: none;
  padding: 0;
  margin: 0;
  text-align: left;
}

.method-card li {
  padding: 5px 0;
  font-size: 14px;
  opacity: 0.9;
}

.tech-details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.tech-item {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #67c23a;
}

.tech-item h4 {
  margin: 0 0 15px 0;
  color: #67c23a;
  font-size: 16px;
}

.tech-item ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tech-item li {
  padding: 4px 0;
  color: #606266;
  font-size: 14px;
}

.test-notes {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.note-item {
  padding: 20px;
  border-radius: 8px;
}

.note-item.warning {
  background: #fef0f0;
  border-left: 4px solid #f56c6c;
}

.note-item.info {
  background: #f0f9ff;
  border-left: 4px solid #409eff;
}

.note-item h4 {
  margin: 0 0 15px 0;
  font-size: 16px;
}

.note-item.warning h4 {
  color: #f56c6c;
}

.note-item.info h4 {
  color: #409eff;
}

.note-item ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.note-item li {
  padding: 4px 0;
  color: #606266;
  font-size: 14px;
}

.quick-test {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.quick-test p {
  margin: 0 0 20px 0;
  color: #666;
  font-size: 16px;
}

.test-input {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .payment-test {
    padding: 15px;
  }
  
  .flow-steps {
    gap: 15px;
  }
  
  .step {
    flex-direction: column;
    text-align: center;
  }
  
  .payment-methods {
    grid-template-columns: 1fr;
  }
  
  .tech-details {
    grid-template-columns: 1fr;
  }
  
  .test-notes {
    grid-template-columns: 1fr;
  }
  
  .test-input {
    flex-direction: column;
    align-items: stretch;
  }
}
</style> 