<template>
  <div class="merchant-info-form">
    <el-card class="form-card">
      <template #header>
        <div class="card-header">
          <span>基本信息</span>
        </div>
      </template>
      
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="120px"
        class="merchant-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商家名称" prop="merchantName">
              <el-input v-model="formData.merchantName" placeholder="请输入商家名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商家类型" prop="merchantType">
              <el-select v-model="formData.merchantType" placeholder="请选择商家类型">
                <el-option label="企业" value="enterprise" />
                <el-option label="个体" value="individual" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phoneNumber">
              <el-input v-model="formData.phoneNumber" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="formData.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="营业执照编号" prop="businessLicenseNo">
              <el-input v-model="formData.businessLicenseNo" placeholder="请输入营业执照编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="法人姓名" prop="legalPersonName">
              <el-input v-model="formData.legalPersonName" placeholder="请输入法人姓名" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="法人身份证号" prop="legalPersonIdCard">
              <el-input v-model="formData.legalPersonIdCard" placeholder="请输入法人身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系人姓名" prop="contactPersonName">
              <el-input v-model="formData.contactPersonName" placeholder="请输入联系人姓名" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系邮箱" prop="contactEmail">
              <el-input v-model="formData.contactEmail" placeholder="请输入联系邮箱" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="详细地址" prop="detailedAddress">
          <el-input
            v-model="formData.detailedAddress"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            保存修改
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="form-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>结算信息</span>
        </div>
      </template>
      
      <el-form
        ref="settlementFormRef"
        :model="settlementData"
        :rules="settlementRules"
        label-width="120px"
        class="settlement-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开户银行" prop="bankName">
              <el-input v-model="settlementData.bankName" placeholder="请输入开户银行" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="银行账号" prop="bankAccount">
              <el-input v-model="settlementData.bankAccount" placeholder="请输入银行账号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开户名" prop="accountName">
              <el-input v-model="settlementData.accountName" placeholder="请输入开户名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结算周期" prop="settlementCycle">
              <el-select v-model="settlementData.settlementCycle" placeholder="请选择结算周期">
                <el-option label="日结" value="daily" />
                <el-option label="周结" value="weekly" />
                <el-option label="月结" value="monthly" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <el-button type="primary" @click="handleSettlementSubmit" :loading="settlementLoading">
            保存结算信息
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElCard, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton, ElRow, ElCol } from 'element-plus'

// 表单引用
const formRef = ref()
const settlementFormRef = ref()

// 加载状态
const loading = ref(false)
const settlementLoading = ref(false)

// 商家基本信息表单数据
const formData = reactive({
  merchantName: '',
  merchantType: '',
  phoneNumber: '',
  email: '',
  businessLicenseNo: '',
  legalPersonName: '',
  legalPersonIdCard: '',
  contactPersonName: '',
  contactPhone: '',
  contactEmail: '',
  province: '',
  city: '',
  county: '',
  district: '',
  detailedAddress: ''
})

// 结算信息表单数据
const settlementData = reactive({
  bankName: '',
  bankAccount: '',
  accountName: '',
  settlementCycle: 'monthly'
})

// 表单验证规则
const rules = {
  merchantName: [
    { required: true, message: '请输入商家名称', trigger: 'blur' }
  ],
  merchantType: [
    { required: true, message: '请选择商家类型', trigger: 'change' }
  ],
  phoneNumber: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email' as const, message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  businessLicenseNo: [
    { required: true, message: '请输入营业执照编号', trigger: 'blur' }
  ],
  legalPersonName: [
    { required: true, message: '请输入法人姓名', trigger: 'blur' }
  ],
  legalPersonIdCard: [
    { required: true, message: '请输入法人身份证号', trigger: 'blur' },
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  contactPersonName: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' }
  ],
  contactEmail: [
    { required: true, message: '请输入联系邮箱', trigger: 'blur' },
    { type: 'email' as const, message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  detailedAddress: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ]
}

// 结算信息验证规则
const settlementRules = {
  bankName: [
    { required: true, message: '请输入开户银行', trigger: 'blur' }
  ],
  bankAccount: [
    { required: true, message: '请输入银行账号', trigger: 'blur' }
  ],
  accountName: [
    { required: true, message: '请输入开户名', trigger: 'blur' }
  ],
  settlementCycle: [
    { required: true, message: '请选择结算周期', trigger: 'change' }
  ]
}

// 提交基本信息
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true
    
    // TODO: 调用API保存商家信息
    await new Promise(resolve => setTimeout(resolve, 1000)) // 模拟API调用
    
    ElMessage.success('商家信息保存成功')
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    loading.value = false
  }
}

// 提交结算信息
const handleSettlementSubmit = async () => {
  try {
    await settlementFormRef.value.validate()
    settlementLoading.value = true
    
    // TODO: 调用API保存结算信息
    await new Promise(resolve => setTimeout(resolve, 1000)) // 模拟API调用
    
    ElMessage.success('结算信息保存成功')
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    settlementLoading.value = false
  }
}

// 重置表单
const handleReset = () => {
  formRef.value.resetFields()
}

// 加载商家信息
const loadMerchantInfo = async () => {
  try {
    // TODO: 从API加载商家信息
    // 模拟数据
    Object.assign(formData, {
      merchantName: '示例商家',
      merchantType: 'enterprise',
      phoneNumber: '13800138000',
      email: 'merchant@example.com',
      businessLicenseNo: '91110000000000000X',
      legalPersonName: '张三',
      legalPersonIdCard: '110101199001011234',
      contactPersonName: '李四',
      contactPhone: '13900139000',
      contactEmail: 'contact@example.com',
      detailedAddress: '北京市朝阳区示例街道123号'
    })

    Object.assign(settlementData, {
      bankName: '中国银行',
      bankAccount: '1234567890123456789',
      accountName: '示例商家',
      settlementCycle: 'monthly'
    })
  } catch (error) {
    console.error('加载商家信息失败:', error)
    ElMessage.error('加载商家信息失败')
  }
}

onMounted(() => {
  loadMerchantInfo()
})
</script>

<style scoped>
.merchant-info-form {
  max-width: 1000px;
}

.form-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card-header {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.merchant-form,
.settlement-form {
  padding: 20px 0;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #333;
}

:deep(.el-input__wrapper) {
  border-radius: 6px;
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-textarea__inner) {
  border-radius: 6px;
}
</style> 