<template>
  <div class="profile-page">
    <div class="profile-header">
      <div class="user-avatar">
        <el-avatar :size="80" :src="userInfo?.avatar">
          <el-icon><User /></el-icon>
        </el-avatar>
      </div>
      <div class="user-info">
        <h2>{{ userInfo?.username || '用户' }}</h2>
        <p>{{ userInfo?.email || '暂无邮箱' }}</p>
      </div>
    </div>

    <div class="profile-content">
      <div class="profile-menu">
        <div class="menu-section">
          <h3>订单管理</h3>
          <div class="menu-items">
            <div class="menu-item" @click="goToOrders">
              <el-icon><Document /></el-icon>
              <span>我的订单</span>
              <el-icon class="arrow"><ArrowRight /></el-icon>
            </div>
            <div class="menu-item" @click="goToCart">
              <el-icon><ShoppingCart /></el-icon>
              <span>购物车</span>
              <el-icon class="arrow"><ArrowRight /></el-icon>
            </div>
          </div>
        </div>

        <div class="menu-section">
          <h3>账户设置</h3>
          <div class="menu-items">
            <div class="menu-item" @click="showEditProfile = true">
              <el-icon><Edit /></el-icon>
              <span>编辑资料</span>
              <el-icon class="arrow"><ArrowRight /></el-icon>
            </div>
            <div class="menu-item" @click="goToAddress">
              <el-icon><Location /></el-icon>
              <span>收货地址</span>
              <el-icon class="arrow"><ArrowRight /></el-icon>
            </div>
            <div class="menu-item" @click="showChangePassword = true">
              <el-icon><Lock /></el-icon>
              <span>修改密码</span>
              <el-icon class="arrow"><ArrowRight /></el-icon>
            </div>
          </div>
        </div>

        <div class="menu-section">
          <h3>其他</h3>
          <div class="menu-items">
            <div class="menu-item" @click="goHome">
              <el-icon><House /></el-icon>
              <span>返回首页</span>
              <el-icon class="arrow"><ArrowRight /></el-icon>
            </div>
            <div class="menu-item danger" @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              <span>退出登录</span>
              <el-icon class="arrow"><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑资料对话框 -->
    <el-dialog v-model="showEditProfile" title="编辑资料" width="500px">
      <el-form :model="profileForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="profileForm.username" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="profileForm.email" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="profileForm.phone" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditProfile = false">取消</el-button>
        <el-button type="primary" @click="updateProfile">保存</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="showChangePassword" title="修改密码" width="500px">
      <el-form :model="passwordForm" label-width="100px">
        <el-form-item label="当前密码">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showChangePassword = false">取消</el-button>
        <el-button type="primary" @click="changePassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  User, Document, ShoppingCart, Edit, Location, Lock, House, SwitchButton, ArrowRight
} from '@element-plus/icons-vue'
import { useUserAuthStore } from '@/store/modules/userAuth'

const router = useRouter()
const userAuthStore = useUserAuthStore()

// 响应式数据
const showEditProfile = ref(false)
const showChangePassword = ref(false)

const profileForm = ref({
  username: '',
  email: '',
  phone: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 计算属性
const userInfo = computed(() => userAuthStore.userInfo)

// 方法
const goToOrders = () => {
  router.push('/user/orders')
}

const goToCart = () => {
  router.push('/cart')
}

const goToAddress = () => {
  ElMessage.info('收货地址管理功能开发中...')
}

const goHome = () => {
  router.push('/')
}

const updateProfile = () => {
  // TODO: 实现更新用户资料的API调用
  ElMessage.success('资料更新成功')
  showEditProfile.value = false
}

const changePassword = () => {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  
  // TODO: 实现修改密码的API调用
  ElMessage.success('密码修改成功')
  showChangePassword.value = false
  
  // 清空表单
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    userAuthStore.logout()
    ElMessage.success('退出登录成功')
    router.push('/')
  } catch {
    // 用户取消
  }
}

// 生命周期
onMounted(() => {
  // 检查登录状态
  if (!userAuthStore.isLoggedIn) {
    ElMessage.error('请先登录')
    router.push('/user/login')
    return
  }
  
  // 初始化表单数据
  if (userInfo.value) {
    profileForm.value = {
      username: userInfo.value.username || '',
      email: userInfo.value.email || '',
      phone: userInfo.value.phone || ''
    }
  }
})
</script>

<style scoped>
.profile-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background: #f8f9fa;
  min-height: 100vh;
}

.profile-header {
  background: white;
  padding: 30px;
  border-radius: 12px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.user-avatar {
  flex-shrink: 0;
}

.user-info h2 {
  font-size: 24px;
  color: #303133;
  margin: 0 0 8px 0;
}

.user-info p {
  font-size: 14px;
  color: #606266;
  margin: 0;
}

.profile-content {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.menu-section {
  border-bottom: 1px solid #f0f0f0;
}

.menu-section:last-child {
  border-bottom: none;
}

.menu-section h3 {
  font-size: 16px;
  color: #303133;
  margin: 0;
  padding: 20px 30px 10px;
  font-weight: 600;
}

.menu-items {
  padding-bottom: 10px;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 15px 30px;
  cursor: pointer;
  transition: background-color 0.3s;
  gap: 15px;
}

.menu-item:hover {
  background-color: #f5f7fa;
}

.menu-item.danger {
  color: #f56c6c;
}

.menu-item.danger:hover {
  background-color: #fef0f0;
}

.menu-item span {
  flex: 1;
  font-size: 15px;
}

.menu-item .arrow {
  color: #c0c4cc;
  font-size: 14px;
}

.menu-item:hover .arrow {
  color: #409eff;
}

.menu-item.danger:hover .arrow {
  color: #f56c6c;
}
</style> 