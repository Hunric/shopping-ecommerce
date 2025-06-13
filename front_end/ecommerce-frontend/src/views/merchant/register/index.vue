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
      <div class="agreement" :class="{'has-error': errors.agreement}">
        <input type="checkbox" id="agreement" v-model="formData.agreement" @change="checkFormValid" />
        <label for="agreement">我已阅读并同意<a href="/static/agreements/merchant-agreement.html" target="_blank" class="agreement-link">《商家入驻协议》</a></label>
      </div>
      <div class="error-message" v-if="errors.agreement">{{ errors.agreement }}</div>
      <button 
        type="submit" 
        class="submit-btn" 
        :disabled="!isFormValid || !isEmailVerified" 
        :class="{'disabled': !isFormValid || !isEmailVerified}"
        @click.prevent="submitForm"
      >
        <span v-if="!isEmailVerified">请先验证邮箱</span>
        <span v-else-if="!isFormValid">请完善表单信息</span>
        <span v-else>注册</span>
      </button>
    </div>
    
    <div class="register-box">
      <div class="register-right">
        <h2 class="title">注册商家账号</h2>
        <div class="form-container">
          <div class="register-content">
            <!-- 提示消息 -->
            <div v-if="message" :class="['message', { 'error': !success, 'success': success }]">
              {{ message }}
            </div>
            
            <form class="register-form">
              <!-- 基本信息 -->
              <div class="form-section">
                <h3 class="section-title">基本信息</h3>
                <div class="form-row">
                  <div class="form-group">
                    <label>商家名称 <span class="required-mark">*</span></label>
                    <input 
                      type="text" 
                      class="form-control" 
                      placeholder="请输入商家名称" 
                      v-model="formData.merchantName"
                      :class="{'has-error': errors.merchantName}" 
                    />
                    <div class="error-message" v-if="errors.merchantName">{{ errors.merchantName }}</div>
                  </div>
                  <div class="form-group">
                    <label>商家类型 <span class="required-mark">*</span></label>
                    <select 
                      class="form-control" 
                      v-model="formData.merchantType"
                      :class="{'has-error': errors.merchantType}"
                    >
                      <option value="">请选择商家类型</option>
                      <option value="company">企业商家</option>
                      <option value="individual">个体商家</option>
                    </select>
                    <div class="error-message" v-if="errors.merchantType">{{ errors.merchantType }}</div>
                  </div>
                </div>
                
                <div class="form-row">
                  <div class="form-group">
                    <label>邮箱 <span class="required-mark">*</span></label>
                    <div class="email-input-wrapper">
                      <input type="email" 
                             class="form-control" 
                             placeholder="请输入邮箱" 
                             v-model="merchantEmail"
                             :class="{'verified': isEmailVerified, 'has-error': emailError || errors.email}" 
                             :disabled="isEmailVerified" />
                      <span class="verify-success-icon" v-if="isEmailVerified">✓</span>
                    </div>
                    <div class="error-message" v-if="emailError">{{ emailError }}</div>
                    <div class="error-message" v-if="errors.email">{{ errors.email }}</div>
                    <div class="verify-status" v-if="isEmailVerified">
                      <span class="verify-success-text">邮箱验证成功</span>
                    </div>
                    <div class="verify-status error" v-else-if="formSubmitted">
                      <span class="verify-error-text">邮箱尚未验证，请完成验证</span>
                    </div>
                  </div>
                  <div class="form-group verify-group" v-if="!isEmailVerified">
                    <label>邮箱验证码</label>
                    <div class="verify-input-group">
                      <input type="text" class="form-control verify-input" 
                             placeholder="请输入验证码" 
                             v-model="verifyCode" 
                             :class="{'has-error': verifyCodeError}" />
                      <button type="button" class="verify-send-btn" @click="sendVerifyCode" :disabled="countdown > 0">{{ sendBtnText }}</button>
                      <button type="button" class="verify-confirm-btn" 
                              @click="verifyEmailCode" 
                              :disabled="!verifyCodeSent || !verifyCode">验证</button>
                    </div>
                    <div class="error-message" v-if="verifyCodeError">{{ verifyCodeError }}</div>
                  </div>
                </div>
                
                <div class="form-row">
                  <div class="form-group">
                    <label>密码 <span class="required-mark">*</span></label>
                    <input 
                      type="password" 
                      class="form-control" 
                      placeholder="请输入密码（至少6位）" 
                      v-model="formData.password"
                      :class="{'has-error': errors.password}"
                    />
                    <div class="error-message" v-if="errors.password">{{ errors.password }}</div>
                    <div class="password-requirements">
                      密码要求：至少6位字符
                    </div>
                  </div>
                  <div class="form-group">
                    <label>确认密码 <span class="required-mark">*</span></label>
                    <input 
                      type="password" 
                      class="form-control" 
                      placeholder="请再次输入密码" 
                      v-model="formData.confirmPassword"
                      :class="{'has-error': errors.confirmPassword}"
                    />
                    <div class="error-message" v-if="errors.confirmPassword">{{ errors.confirmPassword }}</div>
                  </div>
                </div>
                
              </div>
              
              <!-- 营业执照信息 -->
              <div class="form-section">
                <h3 class="section-title">营业执照信息</h3>
                <div class="form-row">
                  <div class="form-group">
                    <label>营业执照编号 (统一社会信用代码) <span class="required-mark">*</span></label>
                    <input 
                      type="text" 
                      class="form-control" 
                      placeholder="请输入18位统一社会信用代码" 
                      v-model="formData.licenseNumber"
                      maxlength="18"
                      :class="{'has-error': errors.licenseNumber}"
                    />
                    <div class="error-message" v-if="errors.licenseNumber">{{ errors.licenseNumber }}</div>
                  </div>
                  <div class="form-group">
                    <!-- 添加空的占位分组 -->
                  </div>
                </div>
                
                <div class="form-row">
                  <div class="form-group">
                    <label>法人姓名 <span class="required-mark">*</span></label>
                    <input 
                      type="text" 
                      class="form-control" 
                      placeholder="请输入法人姓名" 
                      v-model="formData.legalPersonName"
                      :class="{'has-error': errors.legalPersonName}"
                    />
                    <div class="error-message" v-if="errors.legalPersonName">{{ errors.legalPersonName }}</div>
                  </div>
                  <div class="form-group">
                    <label>法人身份证号 <span class="required-mark">*</span></label>
                    <input 
                      type="text" 
                      class="form-control" 
                      placeholder="请输入18位法人身份证号" 
                      v-model="formData.legalPersonId"
                      maxlength="18"
                      :class="{'has-error': errors.legalPersonId}"
                    />
                    <div class="error-message" v-if="errors.legalPersonId">{{ errors.legalPersonId }}</div>
                  </div>
                </div>
              </div>
              
              <!-- 联系人信息 -->
              <div class="form-section">
                <h3 class="section-title">联系人信息</h3>
                <div class="form-row">
                  <div class="form-group">
                    <label>联系人姓名 <span class="required-mark">*</span></label>
                    <input 
                      type="text" 
                      class="form-control" 
                      placeholder="请输入联系人姓名" 
                      v-model="formData.contactName"
                      :class="{'has-error': errors.contactName}"
                    />
                    <div class="error-message" v-if="errors.contactName">{{ errors.contactName }}</div>
                  </div>
                  <div class="form-group">
                    <label>联系电话 <span class="required-mark">*</span></label>
                    <input 
                      type="text" 
                      class="form-control" 
                      placeholder="请输入联系电话" 
                      v-model="formData.contactPhone"
                      maxlength="11"
                      :class="{'has-error': errors.contactPhone}"
                    />
                    <div class="error-message" v-if="errors.contactPhone">{{ errors.contactPhone }}</div>
                  </div>
                </div>
                
                <div class="form-row">
                  <div class="form-group">
                    <label>联系邮箱 <span class="required-mark">*</span></label>
                    <input 
                      type="email" 
                      class="form-control" 
                      placeholder="请输入联系邮箱" 
                      v-model="formData.contactEmail"
                      :class="{'has-error': errors.contactEmail}"
                    />
                    <div class="error-message" v-if="errors.contactEmail">{{ errors.contactEmail }}</div>
                  </div>
                  <div class="form-group">
                    <!-- 添加空的占位分组 -->
                  </div>
                </div>
              </div>
              
              <!-- 地址信息 -->
              <div class="form-section">
                <h3 class="section-title">地址信息</h3>
                <div class="form-row">
                  <div class="form-group">
                    <label>省市区 <span class="required-mark">*</span></label>
                    <el-cascader
                      v-model="addressCodes"
                      :options="provinceOptions"
                      :props="cascaderProps"
                      placeholder="请选择省/市/区"
                      class="form-control address-cascader"
                      :class="{'has-error': errors.address}"
                      clearable
                      @mouseenter="enableScrollPropagation"
                    />
                    <div class="error-message" v-if="errors.address">{{ errors.address }}</div>
                  </div>
                  <div class="form-group">
                    <label>详细地址 <span class="required-mark">*</span></label>
                    <input 
                      type="text" 
                      class="form-control" 
                      placeholder="请输入详细地址" 
                      v-model="formData.detailAddress"
                      :class="{'has-error': errors.detailAddress}"
                    />
                    <div class="error-message" v-if="errors.detailAddress">{{ errors.detailAddress }}</div>
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
  </div>
</template>

<script setup lang="ts">
// 注册页面逻辑
import { ref, onMounted, reactive, watch } from 'vue';
import { verificationApi } from '@/api/verification';
import merchantApi from '@/api/merchant';
import { useRouter } from 'vue-router';
import type { Region } from '@/services/address/types';
import addressService from '@/services/address/addressService';

const router = useRouter();

// 表单数据
const formData = reactive({
  merchantName: '',
  merchantType: '',
  password: '',
  confirmPassword: '',
  licenseNumber: '',
  legalPersonName: '',
  legalPersonId: '',
  contactName: '',
  contactPhone: '',
  contactEmail: '',
  detailAddress: '',
  agreement: false
});

// 表单错误信息
const errors = reactive({
  merchantName: '',
  merchantType: '',
  email: '',
  password: '',
  confirmPassword: '',
  licenseNumber: '',
  legalPersonName: '',
  legalPersonId: '',
  contactName: '',
  contactPhone: '',
  contactEmail: '',
  address: '',
  detailAddress: '',
  agreement: ''
});

const merchantEmail = ref('');

// 邮箱验证码倒计时逻辑
const countdown = ref(0);
const sendBtnText = ref('获取验证码');
const verifyCode = ref('');
const isEmailVerified = ref(false);
let timer: number | null = null;

// 验证码是否已发送标志
const verifyCodeSent = ref(false);

// 错误信息
const emailError = ref('');
const verifyCodeError = ref('');

// 提示消息
const message = ref('');
const success = ref(false);

// 地址数据
const addressCodes = ref<string[]>([]);
const provinceOptions = ref<Region[]>([]);

// 级联选择器配置
const cascaderProps = {
  value: 'code',
  label: 'name',
  children: 'children',
  checkStrictly: false
};

// 添加表单是否已提交状态
const formSubmitted = ref(false);

// 表单数据
const isFormValid = ref(false); // 表单是否有效

// 初始化地址数据
onMounted(() => {
  // 加载省份数据
  addressService.getProvinces().then(provinces => {
    provinceOptions.value = provinces;
  }).catch(error => {
    console.error('加载省份数据失败:', error);
  });
  
  // 设置表单字段监听
  setupFormWatchers();
});

// 显示消息函数
const showMessage = (msg: string, isSuccess: boolean) => {
  message.value = msg;
  success.value = isSuccess;
  
  // 3秒后自动清除消息
  setTimeout(() => {
    message.value = '';
  }, 3000);
};

// 清除错误信息
const clearErrors = () => {
  emailError.value = '';
  verifyCodeError.value = '';
};

// 发送邮箱验证码
const sendVerifyCode = async () => {
  // 如果正在倒计时，则不执行
  if (countdown.value > 0) return;
  
  // 清除之前的错误信息
  clearErrors();
  
  // 验证邮箱格式
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(merchantEmail.value)) {
    emailError.value = '请输入有效的邮箱地址';
    return;
  }
  
  try {
    // 立即开始倒计时（60秒）
    countdown.value = 60;
    sendBtnText.value = `${countdown.value}秒后重新获取`;
    
    // 设置定时器
    timer = window.setInterval(() => {
      countdown.value--;
      sendBtnText.value = `${countdown.value}秒后重新获取`;
      
      if (countdown.value <= 0) {
        sendBtnText.value = '获取验证码';
        clearInterval(timer!);
        timer = null;
      }
    }, 1000);
    
    // 调用验证码API
    await verificationApi.sendVerificationCode(merchantEmail.value, 'register');
    showMessage('验证码已发送到您的邮箱', true);
    verifyCodeSent.value = true; // 标记验证码已发送
  } catch (error) {
    // 如果发送失败，重置倒计时
    console.error('发送验证码失败:', error);
    emailError.value = '验证码发送失败，请稍后重试';
    
    // 重置倒计时
    countdown.value = 0;
    sendBtnText.value = '获取验证码';
    verifyCodeSent.value = false; // 重置验证码发送状态
    if (timer) {
      clearInterval(timer);
      timer = null;
    }
  }
};

// 验证邮箱验证码
const verifyEmailCode = async () => {
  // 清除之前的错误信息
  clearErrors();
  
  if (!verifyCode.value) {
    verifyCodeError.value = '请输入验证码';
    return;
  }
  
  try {
    // 验证验证码
    const response = await verificationApi.verifyCode(merchantEmail.value, verifyCode.value, 'register');
    
    if (response.data.success) {
      isEmailVerified.value = true;
      showMessage('邮箱验证成功', true);
      verifyCodeSent.value = false; // 重置验证码发送状态
      // 清除email相关错误
      errors.email = '';
      
      // 验证成功后重新检查表单有效性
      checkFormValid();
    } else {
      verifyCodeError.value = response.data.message || '验证码错误';
      isFormValid.value = false;
    }
  } catch (error) {
    console.error('验证失败:', error);
    verifyCodeError.value = '验证失败，请检查验证码是否正确';
    isFormValid.value = false;
  }
};

// 检查表单是否有效
const checkFormValid = () => {
  // 先清除所有错误
  clearAllErrors();
  
  // 验证表单
  const valid = validateForm();
  
  // 检查邮箱验证状态
  if (!isEmailVerified.value) {
    errors.email = '请验证邮箱';
    isFormValid.value = false;
    return;
  }
  
  // 检查协议是否同意
  if (!formData.agreement) {
    errors.agreement = '请阅读并同意商家入驻协议';
    isFormValid.value = false;
    return;
  }
  
  // 只有当所有条件都满足时，表单才有效
  isFormValid.value = valid;
};

// 输入框变化时重新检查表单有效性
const setupFormWatchers = () => {
  // 监听所有表单字段变化
  Object.keys(formData).forEach(key => {
    watch(() => formData[key as keyof typeof formData], () => {
      checkFormValid();
    });
  });
  
  // 监听地址选择变化
  watch(addressCodes, () => {
    checkFormValid();
  });
};

// 表单提交函数
const submitForm = async () => {
  // 标记表单已尝试提交
  formSubmitted.value = true;
  
  // 清除所有错误信息
  clearAllErrors();
  
  // 首先检查邮箱是否已验证
  if (!isEmailVerified.value) {
    errors.email = '请先完成邮箱验证';
    showMessage('请先完成邮箱验证再提交表单', false);
    // 滚动到邮箱验证区域
    document.querySelector('.email-input-wrapper')?.scrollIntoView({ behavior: 'smooth', block: 'center' });
    isFormValid.value = false;
    return;
  }
  
  // 验证表单
  if (!validateForm()) {
    showMessage('请完善表单信息', false);
    isFormValid.value = false;
    return;
  }
  
  // 检查协议是否同意
  if (!formData.agreement) {
    errors.agreement = '请阅读并同意商家入驻协议';
    showMessage('请阅读并同意商家入驻协议', false);
    isFormValid.value = false;
    return;
  }
  
  // 再次确认表单完全有效
  if (!isFormValid.value) {
    showMessage('表单存在错误，请检查', false);
    return;
  }
  
  // 准备注册数据
  const registerData = {
    merchantName: formData.merchantName,
    merchantType: formData.merchantType,
    email: merchantEmail.value,
    password: formData.password,
    licenseNumber: formData.licenseNumber,
    legalPersonName: formData.legalPersonName,
    legalPersonId: formData.legalPersonId,
    contactName: formData.contactName,
    contactPhone: formData.contactPhone,
    contactEmail: formData.contactEmail,
    addressCodes: addressCodes.value,
    detailAddress: formData.detailAddress
  };
  
  // 表单验证通过，发起真正的注册请求
  showMessage('表单验证通过，正在提交...', true);
  
  try {
    // 调用商家注册API
    const response = await merchantApi.register(registerData);
    
    // 检查注册结果
    if (response.data && response.data.success) {
      showMessage('注册成功，正在跳转...', true);
      setTimeout(() => {
        router.push('/merchant/login');
      }, 2000);
    } else {
      // 注册失败
      const errorMsg = response.data && response.data.message ? response.data.message : '注册失败，请重试';
      showMessage(errorMsg, false);
    }
  } catch (error) {
    console.error('注册请求发生错误:', error);
    let errorMsg = '注册失败，请稍后重试';
    
    if (error && typeof error === 'object' && 'response' in error && 
        error.response && typeof error.response === 'object' && 
        'data' in error.response && error.response.data && 
        typeof error.response.data === 'object' && 'message' in error.response.data) {
      errorMsg = String(error.response.data.message);
    }
    
    showMessage(errorMsg, false);
  }
};

// 清除所有错误信息
const clearAllErrors = () => {
  Object.keys(errors).forEach(key => {
    errors[key as keyof typeof errors] = '';
  });
};

// 验证密码
const validatePassword = (password: string): boolean => {
  return password.length >= 6;
}

// 表单验证
const validateForm = () => {
  const errors: Record<string, string> = {};
  
  // 验证商家名称
  if (!formData.merchantName?.trim()) {
    errors.merchantName = '请输入商家名称';
  }
  
  // 验证商家类型
  if (!formData.merchantType) {
    errors.merchantType = '请选择商家类型';
  }
  
  // 验证密码
  if (!formData.password) {
    errors.password = '请输入密码';
  } else if (!validatePassword(formData.password)) {
    errors.password = '密码长度至少为6位';
  }
  
  // 验证确认密码
  if (!formData.confirmPassword) {
    errors.confirmPassword = '请再次输入密码';
  } else if (formData.password !== formData.confirmPassword) {
    errors.confirmPassword = '两次输入的密码不一致';
  }
  
  // 验证营业执照编号
  if (!formData.licenseNumber?.trim()) {
    errors.licenseNumber = '请输入营业执照编号';
  } else if (formData.licenseNumber.length !== 18) {
    errors.licenseNumber = '请输入18位统一社会信用代码';
  }
  
  // 验证法人信息
  if (!formData.legalPersonName?.trim()) {
    errors.legalPersonName = '请输入法人姓名';
  }
  if (!formData.legalPersonId?.trim()) {
    errors.legalPersonId = '请输入法人身份证号';
  } else if (formData.legalPersonId.length !== 18) {
    errors.legalPersonId = '请输入18位法人身份证号';
  }
  
  // 验证联系人信息
  if (!formData.contactName?.trim()) {
    errors.contactName = '请输入联系人姓名';
  }
  if (!formData.contactPhone?.trim()) {
    errors.contactPhone = '请输入联系电话';
  } else if (!/^1[3-9]\d{9}$/.test(formData.contactPhone)) {
    errors.contactPhone = '请输入正确的手机号码';
  }
  if (!formData.contactEmail?.trim()) {
    errors.contactEmail = '请输入联系邮箱';
  } else if (!validateEmail(formData.contactEmail)) {
    errors.contactEmail = '请输入正确的邮箱格式';
  }
  
  // 验证地址
  if (!addressCodes || addressCodes.length < 3) {
    errors.address = '请选择完整的省市区';
  }
  if (!formData.detailAddress?.trim()) {
    errors.detailAddress = '请输入详细地址';
  }
  
  // 验证协议
  if (!formData.agreement) {
    errors.agreement = '请阅读并同意商家入驻协议';
  }
  
  return errors;
}

// 解决级联选择器滚动到边界不触发页面滚动问题
const enableScrollPropagation = () => {
  // 获取级联选择器下拉菜单元素
  setTimeout(() => {
    const cascaderMenus = document.querySelectorAll('.el-cascader__dropdown .el-scrollbar__wrap');
    
    cascaderMenus.forEach(menu => {
      if (menu && !menu.getAttribute('scroll-handler-added')) {
        menu.setAttribute('scroll-handler-added', 'true');
        
        // 添加滚动事件监听器
        menu.addEventListener('wheel', (e: Event) => {
          const wheelEvent = e as WheelEvent;
          const scrollWrapper = wheelEvent.currentTarget as HTMLElement;
          
          // 检查是否已经滚动到边界
          const atTop = scrollWrapper.scrollTop <= 0 && wheelEvent.deltaY < 0;
          const atBottom = 
            Math.abs(scrollWrapper.scrollHeight - scrollWrapper.scrollTop - scrollWrapper.clientHeight) < 1 
            && wheelEvent.deltaY > 0;
          
          // 如果在边界处继续滚动，不阻止事件默认行为，让页面继续滚动
          if (atTop || atBottom) {
            // 允许事件继续冒泡，传递给页面
            return true;
          } else {
            // 否则阻止默认行为，保持下拉框内部滚动
            wheelEvent.stopPropagation();
          }
        }, { passive: false });
      }
    });
  }, 100); // 短暂延时确保下拉菜单已经渲染
};
</script>

<style>
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

.form-group label .required-mark {
  color: #f56c6c;
  margin-left: 2px;
}

.form-control {
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
  background-color: #FAFAFA !important;
  color: #333;
  width: 100%;
  box-sizing: border-box;
}

.form-control::placeholder {
  font-size: 13px;
  color: #999;
}

.form-control:focus {
  border-color: #10b5b8;
}

/* 添加通用的输入框背景色规则 */
input.form-control, 
select.form-control,
textarea.form-control,
.verify-input {
  background-color: #FAFAFA !important;
}

/* 恢复被移除的email-input-wrapper类 */
.email-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
}

.email-input-wrapper input {
  background-color: #FAFAFA !important;
}

/* 恢复被移除的verify-input-group类 */
.verify-input-group {
  display: flex;
  gap: 10px;
  width: 100%;
}

/* 恢复被移除的verify-input类 */
.verify-input {
  flex: 1;
  max-width: 120px;
  background-color: #FAFAFA !important;
}

.verify-input-group input {
  background-color: #FAFAFA !important;
}

.verify-success-icon {
  position: absolute;
  right: 10px;
  color: #4caf50;
  font-size: 18px;
  font-weight: bold;
}

input.verified {
  border-color: #4caf50;
  background-color: rgba(76, 175, 80, 0.05);
}

.verify-send-btn {
  padding: 0 15px;
  background-color: #10b5b8;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
  white-space: nowrap;
  flex-shrink: 0;
  width: auto;
  min-width: 90px;
}

.verify-confirm-btn {
  padding: 0 15px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
  white-space: nowrap;
  flex-shrink: 0;
  width: auto;
  min-width: 60px;
}

.verify-confirm-btn:hover {
  background-color: #3d8b40;
}

.verify-send-btn:hover {
  background-color: #0d9598;
}

.verify-send-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.verify-confirm-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

select.form-control {
  appearance: none;
  background-image: url("data:image/svg+xml;charset=utf8,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%23888' d='M10.3 3.3L6 7.6 1.7 3.3c-.4-.4-1-.4-1.4 0s-.4 1 0 1.4l5 5c.2.2.4.3.7.3s.5-.1.7-.3l5-5c.4-.4.4-1 0-1.4s-1-.4-1.4 0z'/%3E%3C/svg%3E");
  background-position: right 10px center;
  background-repeat: no-repeat;
  padding-right: 30px;
  background-color: #FAFAFA !important;
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
  position: relative;
  overflow: hidden;
  min-height: 50px;
}

.submit-btn span {
  display: inline-block;
  position: relative;
  z-index: 2;
}

.submit-btn:hover {
  background-color: rgba(255, 255, 255, 0.9);
}

.submit-btn:disabled,
.submit-btn.disabled {
  background-color: rgba(255, 255, 255, 0.5);
  color: #999;
  cursor: not-allowed;
  opacity: 0.7;
}

.submit-btn:disabled span,
.submit-btn.disabled span {
  color: #999;
}

.submit-btn:disabled:hover,
.submit-btn.disabled:hover {
  background-color: rgba(255, 255, 255, 0.5);
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

.form-control.has-error {
  border-color: #d32f2f;
}

.error-message {
  color: #d32f2f;
  font-size: 12px;
  margin-top: 4px;
}

/* 级联选择器样式 */
:deep(.el-cascader) {
  width: 100%;
}

:deep(.el-input__wrapper) {
  background-color: #FAFAFA !important;
  border-radius: 5px;
  box-shadow: 0 0 0 1px #ddd inset !important;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #10b5b8 inset !important;
}

:deep(.el-input__inner) {
  height: 40px;
  font-size: 14px;
}

:deep(.el-input__inner::placeholder) {
  font-size: 13px;
  color: #999;
}

/* 级联选择器下拉菜单样式 */
:deep(.el-cascader__dropdown) {
  background-color: #FFFFFF;
  overflow: visible !important;
  z-index: 3000; /* 确保在页面其他元素之上 */
}

:deep(.el-cascader-menu) {
  background-color: #FFFFFF;
}

/* 级联选择器下拉菜单滚动优化 */
:deep(.el-scrollbar__wrap) {
  overflow-x: hidden;
  max-height: 300px;
  overscroll-behavior: contain; /* 防止滚动传递 */
  scrollbar-width: thin; /* Firefox */
}

:deep(.el-scrollbar__wrap::-webkit-scrollbar) {
  width: 6px;
}

:deep(.el-scrollbar__wrap::-webkit-scrollbar-thumb) {
  background-color: rgba(144, 147, 153, 0.3);
  border-radius: 3px;
}

:deep(.el-cascader-panel) {
  overflow: visible !important;
}

/* 表单验证错误样式 */
.has-error {
  border-color: #f56c6c;
}

.error-message {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 5px;
}

.verify-status {
  font-size: 12px;
  margin-top: 5px;
}

.verify-success-text {
  color: #4caf50;
}

.verify-error-text {
  color: #f56c6c;
}

.verify-status.error {
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0% {
    opacity: 0.6;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0.6;
  }
}

.agreement.has-error {
  border: 1px solid #f56c6c;
  border-radius: 5px;
  padding: 5px;
  background-color: rgba(245, 108, 108, 0.05);
}

.agreement.has-error label {
  color: #f56c6c;
}

.password-requirements {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}
</style> 