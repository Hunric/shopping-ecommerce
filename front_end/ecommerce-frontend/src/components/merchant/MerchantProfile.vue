<template>
  <div class="merchant-profile">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      v-loading="loading"
    >
      <!-- 基本信息 -->
      <div class="form-section">
        <h3 class="section-title">
          <el-icon><User /></el-icon>
          基本信息
        </h3>
        
        <el-form-item label="商家名称" prop="merchantName">
          <el-input
            v-model="formData.merchantName"
            placeholder="请输入商家名称"
            maxlength="20"
            show-word-limit
            :disabled="!editing"
          />
        </el-form-item>
        
        <el-form-item label="商家类型" prop="merchantType">
          <el-select
            v-model="formData.merchantType"
            placeholder="请选择商家类型"
            style="width: 100%"
            :disabled="!editing || isSubmitting"
          >
            <el-option label="企业商家" value="enterprise" />
            <el-option label="个体商家" value="individual" />
          </el-select>
        </el-form-item>
      </div>
      
      <!-- 联系信息 -->
      <div class="form-section">
        <h3 class="section-title">
          <el-icon><Phone /></el-icon>
          联系信息
        </h3>
        
        <el-form-item label="联系人" prop="contactPersonName">
          <el-input
            v-model="formData.contactPersonName"
            placeholder="请输入联系人姓名"
            maxlength="16"
            :disabled="!editing || isSubmitting"
          />
        </el-form-item>
        
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input
            v-model="formData.contactPhone"
            placeholder="请输入联系电话"
            maxlength="11"
            :disabled="!editing || isSubmitting"
          />
        </el-form-item>
        
        <el-form-item label="联系邮箱" prop="contactEmail">
          <el-input
            v-model="formData.contactEmail"
            placeholder="请输入联系邮箱"
            maxlength="255"
            :disabled="!editing || isSubmitting"
          />
        </el-form-item>
        
        <el-form-item label="商家邮箱" prop="email">
          <el-input
            v-model="formData.email"
            placeholder="请输入商家邮箱"
            maxlength="255"
            disabled
          />
          <div class="form-tip">商家邮箱为登录账号，不可修改</div>
        </el-form-item>
      </div>
      
      <!-- 营业执照信息 -->
      <div class="form-section">
        <h3 class="section-title">
          <el-icon><Document /></el-icon>
          营业执照信息
        </h3>
        
        <el-form-item label="营业执照号" prop="businessLicenseNo">
          <el-input
            v-model="formData.businessLicenseNo"
            placeholder="请输入统一社会信用代码"
            maxlength="18"
            :disabled="!editing || isSubmitting"
          />
        </el-form-item>
        
        <el-form-item label="法人姓名" prop="legalPersonName">
          <el-input
            v-model="formData.legalPersonName"
            placeholder="请输入法人姓名"
            maxlength="16"
            :disabled="!editing || isSubmitting"
          />
        </el-form-item>
      </div>
      
      <!-- 地址信息 -->
      <div class="form-section">
        <h3 class="section-title">
          <el-icon><Location /></el-icon>
          地址信息
        </h3>
        
        <el-form-item label="所在地区" required>
          <div class="region-select">
            <el-select
              v-model="formData.province"
              placeholder="省/直辖市"
              style="width: 120px"
              :disabled="!editing || isSubmitting"
            >
              <el-option label="北京市" value="北京市" />
              <el-option label="上海市" value="上海市" />
              <el-option label="广东省" value="广东省" />
              <el-option label="江苏省" value="江苏省" />
              <el-option label="浙江省" value="浙江省" />
              <!-- 更多省份选项 -->
            </el-select>
            
            <el-select
              v-model="formData.city"
              placeholder="市"
              style="width: 120px; margin: 0 10px"
              :disabled="!editing || isSubmitting"
            >
              <el-option label="广州市" value="广州市" />
              <el-option label="深圳市" value="深圳市" />
              <el-option label="佛山市" value="佛山市" />
              <!-- 根据选择的省份动态加载 -->
            </el-select>
            
            <el-select
              v-model="formData.district"
              placeholder="区/县"
              style="width: 120px"
              :disabled="!editing || isSubmitting"
            >
              <el-option label="天河区" value="天河区" />
              <el-option label="海珠区" value="海珠区" />
              <el-option label="番禺区" value="番禺区" />
              <!-- 根据选择的城市动态加载 -->
            </el-select>
          </div>
        </el-form-item>
        
        <el-form-item label="详细地址" prop="detailedAddress">
          <el-input
            v-model="formData.detailedAddress"
            placeholder="请输入详细地址"
            maxlength="30"
            :disabled="!editing || isSubmitting"
          />
        </el-form-item>
      </div>
      
      <!-- 密码修改 -->
      <div class="form-section" v-if="showPasswordSection">
        <h3 class="section-title">
          <el-icon><Lock /></el-icon>
          密码修改
        </h3>
        
        <el-form-item label="当前密码" prop="currentPassword">
          <el-input
            v-model="passwordForm.currentPassword"
            type="password"
            placeholder="请输入当前密码"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
        
        <div class="password-actions">
          <el-button type="primary" @click="changePassword" :loading="isChangingPassword">
            修改密码
          </el-button>
        </div>
      </div>
      
      <!-- 底部操作按钮 -->
      <div class="form-actions">
        <template v-if="!editing">
          <el-button type="primary" @click="startEdit">
            <el-icon><Edit /></el-icon>
            编辑信息
          </el-button>
        </template>
        <template v-else>
          <el-button @click="cancelEdit">
            取消编辑
          </el-button>
          <el-button type="primary" @click="saveChanges" :loading="isSubmitting">
            <el-icon><Check /></el-icon>
            保存修改
          </el-button>
        </template>
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import {
  User, Phone, Document, Location, Lock, Edit, Check
} from '@element-plus/icons-vue';
import { getMerchantProfile, updateMerchantProfile, changePassword } from '@/api/merchant/merchant';
import { useAuthStore } from '@/store/modules/auth';

// 响应式数据
const formRef = ref<FormInstance>();
const loading = ref(false);
const editing = ref(false);
const isSubmitting = ref(false);
const isChangingPassword = ref(false);
const showPasswordSection = ref(false);
const authStore = useAuthStore();

// 表单数据
const formData = reactive({
  merchantId: 0,
  merchantName: '',
  merchantType: '',
  email: '',
  businessLicenseNo: '',
  legalPersonName: '',
  contactPersonName: '',
  contactPhone: '',
  contactEmail: '',
  province: '',
  city: '',
  district: '',
  detailedAddress: ''
});

// 密码表单
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 表单验证规则
const formRules: FormRules = {
  merchantName: [
    { required: true, message: '请输入商家名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在2到20个字符之间', trigger: 'blur' }
  ],
  merchantType: [
    { required: true, message: '请选择商家类型', trigger: 'change' }
  ],
  contactPersonName: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' },
    { min: 2, max: 16, message: '长度在2到16个字符之间', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  contactEmail: [
    { required: true, message: '请输入联系邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  businessLicenseNo: [
    { required: true, message: '请输入营业执照号', trigger: 'blur' },
    { pattern: /^[0-9A-Z]{18}$/, message: '请输入18位统一社会信用代码', trigger: 'blur' }
  ],
  legalPersonName: [
    { required: true, message: '请输入法人姓名', trigger: 'blur' }
  ],
  detailedAddress: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ]
};

// 生命周期钩子
onMounted(async () => {
  await loadMerchantProfile();
});

// 加载商家信息
const loadMerchantProfile = async () => {
  loading.value = true;
  try {
    const merchantId = authStore.merchantInfo?.merchantId;
    if (!merchantId) {
      ElMessage.error('获取商家ID失败');
      return;
    }
    
    const response = await getMerchantProfile(merchantId);
    if (response.success && response.data) {
      // 更新表单数据
      Object.assign(formData, response.data);
    } else {
      ElMessage.error(response.message || '获取商家信息失败');
    }
  } catch (error) {
    console.error('加载商家信息出错:', error);
    ElMessage.error('加载商家信息出错');
  } finally {
    loading.value = false;
  }
};

// 开始编辑
const startEdit = () => {
  editing.value = true;
};

// 取消编辑
const cancelEdit = () => {
  ElMessageBox.confirm('确定要取消编辑吗？未保存的修改将会丢失。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    editing.value = false;
    loadMerchantProfile(); // 重新加载数据
  }).catch(() => {
    // 用户取消操作
  });
};

// 保存修改
const saveChanges = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    
    isSubmitting.value = true;
    const response = await updateMerchantProfile(formData);
    
    if (response.success) {
      ElMessage.success('商家信息更新成功');
      editing.value = false;
      
      // 更新认证存储中的商家名称
      if (authStore.merchantInfo && formData.merchantName !== authStore.merchantInfo.merchantName) {
        authStore.updateMerchantName(formData.merchantName);
      }
    } else {
      ElMessage.error(response.message || '更新商家信息失败');
    }
  } catch (error) {
    console.error('保存商家信息出错:', error);
    ElMessage.error('表单验证失败，请检查输入');
  } finally {
    isSubmitting.value = false;
  }
};

// 显示密码修改区域
const togglePasswordSection = () => {
  showPasswordSection.value = !showPasswordSection.value;
  if (!showPasswordSection.value) {
    // 清空密码表单
    passwordForm.currentPassword = '';
    passwordForm.newPassword = '';
    passwordForm.confirmPassword = '';
  }
};

// 修改密码
const changePassword = async () => {
  // 表单验证
  if (!passwordForm.currentPassword) {
    ElMessage.warning('请输入当前密码');
    return;
  }
  
  if (!passwordForm.newPassword) {
    ElMessage.warning('请输入新密码');
    return;
  }
  
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.warning('两次输入的新密码不一致');
    return;
  }
  
  // 密码强度验证
  if (passwordForm.newPassword.length < 8) {
    ElMessage.warning('新密码长度至少8位');
    return;
  }
  
  try {
    isChangingPassword.value = true;
    
    const response = await changePassword({
      merchantId: formData.merchantId,
      currentPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword
    });
    
    if (response.success) {
      ElMessage.success('密码修改成功');
      
      // 清空密码表单
      passwordForm.currentPassword = '';
      passwordForm.newPassword = '';
      passwordForm.confirmPassword = '';
      
      // 隐藏密码修改区域
      showPasswordSection.value = false;
    } else {
      ElMessage.error(response.message || '密码修改失败');
    }
  } catch (error) {
    console.error('修改密码出错:', error);
    ElMessage.error('修改密码出错');
  } finally {
    isChangingPassword.value = false;
  }
};
</script>

<style scoped>
.merchant-profile {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.form-section {
  margin-bottom: 30px;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.section-title {
  display: flex;
  align-items: center;
  font-size: 18px;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
  color: #303133;
}

.section-title .el-icon {
  margin-right: 8px;
  font-size: 20px;
  color: #409eff;
}

.region-select {
  display: flex;
  align-items: center;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.password-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.form-actions {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  gap: 20px;
}
</style> 