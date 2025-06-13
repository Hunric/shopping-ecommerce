<template>
  <div class="dashboard-container">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-left">
        <h1>商家管理后台</h1>
      </div>
      <div class="header-right">
        <div class="user-info">
          <span>{{ merchantInfo?.merchantName }}</span>
          <el-dropdown @command="handleCommand">
            <el-button link class="dropdown-btn">
              <el-icon><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">商家信息管理</el-dropdown-item>
                <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>

    <div class="main-content">
      <!-- 侧边栏 -->
      <div class="sidebar">
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          @select="handleMenuSelect"
          :collapse="isCollapse"
        >
          <el-menu-item index="overview">
            <el-icon><DataBoard /></el-icon>
            <template #title>概览</template>
          </el-menu-item>
          
          <el-menu-item index="store-management">
            <el-icon><Shop /></el-icon>
            <template #title>店铺管理</template>
          </el-menu-item>
          
          <el-menu-item index="category-management">
            <el-icon><Menu /></el-icon>
            <template #title>分类管理</template>
          </el-menu-item>
          
          <el-menu-item index="order-management">
            <el-icon><Document /></el-icon>
            <template #title>订单管理</template>
          </el-menu-item>
          
          <el-menu-item index="analytics">
            <el-icon><TrendCharts /></el-icon>
            <template #title>数据分析</template>
          </el-menu-item>
        </el-menu>
        
        <div class="sidebar-toggle" @click="toggleSidebar">
          <el-icon><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
        </div>
      </div>

      <!-- 主内容区域 -->
      <div class="content-area">
        <!-- 概览页面 -->
        <div v-if="activeMenu === 'overview'" class="overview-content">
          <div class="stats-cards">
            <div class="stat-card">
              <div class="stat-icon shop-icon">
                <el-icon><Shop /></el-icon>
              </div>
              <div class="stat-info">
                <h3>{{ storeCount }}</h3>
                <p>店铺数量</p>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon product-icon">
                <el-icon><Goods /></el-icon>
              </div>
              <div class="stat-info">
                <h3>{{ productCount }}</h3>
                <p>商品数量</p>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon order-icon">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-info">
                <h3>{{ orderCount }}</h3>
                <p>订单数量</p>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon revenue-icon">
                <el-icon><Money /></el-icon>
              </div>
              <div class="stat-info">
                <h3>¥{{ revenue }}</h3>
                <p>总收入</p>
              </div>
            </div>
          </div>
          
          <div class="charts-section">
            <div class="chart-card">
              <h3>销售趋势</h3>
              <div class="chart-placeholder">
                <p>销售趋势图表将在此显示</p>
              </div>
            </div>
            <div class="chart-card">
              <h3>热门商品</h3>
              <div class="chart-placeholder">
                <p>热门商品统计将在此显示</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 店铺管理 -->
        <div v-else-if="activeMenu === 'store-management'" class="store-management-content">
          <div v-if="currentView === 'list'" class="store-list-view">
            <div class="page-header">
              <h2>店铺管理</h2>
              <el-button type="primary" @click="currentView = 'create'">
                <el-icon><Plus /></el-icon>
                新建店铺
              </el-button>
            </div>
            
            <!-- 店铺列表组件 -->
            <div class="store-list-container">
              <!-- 搜索和筛选 -->
              <div class="search-bar">
                <el-input
                  v-model="searchKeyword"
                  placeholder="搜索店铺名称"
                  style="width: 300px"
                  clearable
                  @input="handleSearch"
                >
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-input>
                
                <el-select
                  v-model="statusFilter"
                  placeholder="店铺状态"
                  style="width: 150px; margin-left: 16px"
                  clearable
                  @change="handleStatusFilter"
                >
                  <el-option label="营业中" value="open" />
                  <el-option label="已关闭" value="closed" />
                  <el-option label="已暂停" value="suspended" />
                </el-select>
                
                <el-button type="primary" @click="refreshStoreList" style="margin-left: 16px">
                  <el-icon><Refresh /></el-icon>
                  刷新
                </el-button>
              </div>

              <!-- 店铺卡片列表 -->
              <div class="store-cards" v-loading="storeLoading">
                <div v-if="filteredStores.length === 0 && !storeLoading" class="empty-state">
                  <el-empty description="暂无店铺数据">
                    <el-button type="primary" @click="currentView = 'create'">
                      <el-icon><Plus /></el-icon>
                      创建第一个店铺
                    </el-button>
                  </el-empty>
                </div>
                
                <div
                  v-for="store in filteredStores"
                  :key="store.storeId"
                  class="store-card"
                >
                  <div class="store-header">
                    <div class="store-logo">
                      <img
                        v-if="store.storeLogo"
                        :src="store.storeLogo"
                        :alt="store.storeName"
                        @error="handleImageError"
                      />
                      <el-icon v-else class="default-logo"><Shop /></el-icon>
                    </div>
                    
                    <div class="store-info">
                      <h3>{{ store.storeName }}</h3>
                      <p class="store-description">{{ store.storeDescription || '暂无描述' }}</p>
                      <div class="store-meta">
                        <span class="open-time">开店时间：{{ formatDate(store.openTime) }}</span>
                        <span class="credit-score">信用分：{{ store.creditScore }}</span>
                      </div>
                    </div>
                    
                    <div class="store-status">
                      <el-tag
                        :type="getStatusType(store.status)"
                        size="large"
                      >
                        {{ getStatusText(store.status) }}
                      </el-tag>
                    </div>
                  </div>
                  
                  <div class="store-actions">
                    <el-button size="small" @click="handleEditStore(store)">
                      <el-icon><Edit /></el-icon>
                      修改店铺信息
                    </el-button>
                    <el-button size="small" type="primary" @click="handleManageProducts(store)">
                      <el-icon><Goods /></el-icon>
                      商品管理
                    </el-button>
                    <el-button
                      size="small"
                      :type="store.status === 'open' ? 'warning' : 'success'"
                      @click="toggleStoreStatus(store)"
                    >
                      <el-icon><Switch /></el-icon>
                      {{ store.status === 'open' ? '暂停营业' : '恢复营业' }}
                    </el-button>
                    <el-button
                      size="small"
                      type="danger"
                      @click="handleDeleteStore(store)"
                    >
                      <el-icon><Delete /></el-icon>
                      删除店铺
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 创建店铺视图 -->
          <div v-else-if="currentView === 'create'" class="store-create-view">
            <div class="page-header">
              <h2>新建店铺</h2>
              <div class="header-actions">
                <div class="draft-info" v-if="draftCount > 0">
                  <el-tag type="info" size="small">
                    <el-icon><Document /></el-icon>
                    {{ draftCount }} 个草稿
                  </el-tag>
                </div>
                <el-button @click="currentView = 'list'">
                  <el-icon><ArrowLeft /></el-icon>
                  返回列表
                </el-button>
              </div>
            </div>
            <StoreForm
              :use-extended-api="useExtendedApi"
              @cancel="currentView = 'list'"
              @success="handleStoreSuccess"
            />
          </div>

          <!-- 编辑店铺视图 -->
          <div v-else-if="currentView === 'edit'" class="store-edit-view">
            <div class="page-header">
              <h2>编辑店铺信息</h2>
              <el-button @click="currentView = 'list'">
                <el-icon><ArrowLeft /></el-icon>
                返回列表
              </el-button>
            </div>
            <StoreForm
              v-if="currentStore"
              :edit-store="currentStore"
              :use-extended-api="useExtendedApi"
              mode="edit"
              @cancel="currentView = 'list'"
              @success="handleStoreSuccess"
            />
          </div>

          <!-- 商品管理视图 -->
          <div v-else-if="currentView === 'products'" class="products-management-view">
            <ProductManagement
              v-if="currentStore"
              :store-id="currentStore.storeId"
              :store-name="currentStore.storeName"
              @back="currentView = 'list'"
            />
          </div>
        </div>

        <!-- 订单管理 -->
        <div v-else-if="activeMenu === 'order-management'" class="order-management-content">
          <div class="page-header">
            <h2>订单管理</h2>
            <p>管理所有店铺的订单信息</p>
          </div>
          
          <!-- 订单统计卡片 -->
          <div class="order-stats">
            <div class="stat-card">
              <div class="stat-icon all-orders">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-info">
                <h3>{{ totalOrders }}</h3>
                <p>全部订单</p>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon pending-orders">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="stat-info">
                <h3>{{ pendingOrders }}</h3>
                <p>待处理</p>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon shipped-orders">
                <el-icon><Van /></el-icon>
              </div>
              <div class="stat-info">
                <h3>{{ shippedOrders }}</h3>
                <p>已发货</p>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon completed-orders">
                <el-icon><Check /></el-icon>
              </div>
              <div class="stat-info">
                <h3>{{ completedOrders }}</h3>
                <p>已完成</p>
              </div>
            </div>
          </div>

          <!-- 订单标签页 -->
          <div class="order-tabs-container">
            <el-tabs v-model="activeOrderTab" @tab-change="handleOrderTabChange">
              <el-tab-pane label="全部订单" name="all">
                <OrderList :filter="'all'" :orders="filteredOrders" />
              </el-tab-pane>
              <el-tab-pane label="待处理" name="pending">
                <OrderList :filter="'pending'" :orders="filteredOrders" />
              </el-tab-pane>
              <el-tab-pane label="已发货" name="shipped">
                <OrderList :filter="'shipped'" :orders="filteredOrders" />
              </el-tab-pane>
              <el-tab-pane label="已完成" name="completed">
                <OrderList :filter="'completed'" :orders="filteredOrders" />
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>

        <!-- 分类管理 -->
        <div v-else-if="activeMenu === 'category-management'" class="category-management-content">
          <div class="management-tabs">
            <el-tabs v-model="categoryActiveTab" @tab-change="handleCategoryTabChange">
              <el-tab-pane label="分类管理" name="categories">
                <CategoryManagement 
                  v-if="categoryActiveTab === 'categories'"
                  @switch-to-attributes="handleSwitchToAttributes" 
                />
              </el-tab-pane>
              <el-tab-pane label="属性管理" name="attributes">
                <AttributeManagement 
                  v-if="categoryActiveTab === 'attributes'"
                  :selected-category="selectedCategoryForAttributes" 
                />
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>

        <!-- 数据分析 -->
        <div v-else-if="activeMenu === 'analytics'" class="analytics-content">
          <div class="page-header">
            <h2>数据分析</h2>
            <p>查看您的店铺经营数据</p>
          </div>
          <div class="placeholder">数据分析功能开发中...</div>
        </div>
      </div>
    </div>

    <!-- 商家信息管理对话框 -->
    <el-dialog
      v-model="merchantInfoDialogVisible"
      title="商家信息管理"
      width="800px"
      @close="merchantInfoDialogVisible = false"
    >
      <MerchantProfile />
      
      <template #footer>
        <el-button @click="merchantInfoDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/modules/auth'
import { 
  ElButton, ElMessage, ElMessageBox, ElAvatar, ElDropdown, ElDropdownMenu, 
  ElDropdownItem, ElMenu, ElMenuItem, ElSubMenu, ElIcon, ElDialog,
  ElInput, ElSelect, ElOption, ElTag, ElEmpty, ElTabs, ElTabPane
} from 'element-plus'
import {
  ArrowDown, DataBoard, User, Shop, Goods, Document, 
  TrendCharts, Fold, Expand, Plus, ArrowLeft, Money,
  Search, Refresh, Edit, Switch, Clock, Van, Check, Menu, Delete
} from '@element-plus/icons-vue'

// 导入API服务
import { getDashboardStats } from '@/api/merchant/dashboard'
import type { DashboardStats } from '@/api/merchant/dashboard'
import { getStoresByMerchantId, createStore, updateStore, deleteStore } from '@/api/merchant/store'
import type { Store, StoreCreateData, StoreUpdateData } from '@/api/merchant/store'
import { 
  getStoresExtendedByMerchantId, 
  createStoreExtended, 
  updateStoreExtended, 
  deleteStoreExtended 
} from '@/api/merchant/storeExtended'
import type { StoreExtended, StoreExtendedCreateData, StoreExtendedUpdateData } from '@/api/merchant/storeExtended'
import { draftService } from '@/services/draftService'

// 导入组件
import ProductManagement from '@/components/merchant/ProductManagement.vue'
import OrderList from '@/components/merchant/OrderList.vue'
import StoreForm from '@/components/merchant/StoreForm.vue'
import CategoryManagement from '@/components/merchant/CategoryManagement.vue'
import AttributeManagement from '@/components/merchant/AttributeManagement.vue'
import MerchantProfile from '@/components/merchant/MerchantProfile.vue'

// 店铺类型已从API模块导入

const router = useRouter()
const authStore = useAuthStore()

// 响应式数据
const activeMenu = ref('overview')
const isCollapse = ref(false)
const currentView = ref('list') // 当前视图：list, create, products
const currentStore = ref<StoreExtended | null>(null) // 当前选中的店铺

// Dashboard统计数据
const storeCount = ref(0)
const productCount = ref(0)
const orderCount = ref(0)
const revenue = ref('0.00')
const pendingOrderCount = ref(0)
const shippedOrderCount = ref(0)
const monthlyRevenue = ref('0.00')
const monthlyOrderCount = ref(0)

// 店铺管理数据
const storeLoading = ref(false)
const stores = ref<StoreExtended[]>([])
const searchKeyword = ref('')
const statusFilter = ref('')
const merchantInfoDialogVisible = ref(false)
const useExtendedApi = ref(true) // 使用扩展API

// 订单管理数据
const activeOrderTab = ref('all')
const orders = ref<any[]>([])
const totalOrders = ref(0)
const pendingOrders = ref(0)
const shippedOrders = ref(0)
const completedOrders = ref(0)

// 草稿数量
const draftCount = ref(0)

// 分类管理标签页
const categoryActiveTab = ref('categories')
const selectedCategoryForAttributes = ref<any>(null)

// 计算属性
const merchantInfo = computed(() => authStore.merchantInfo)

// 过滤后的店铺列表
const filteredStores = computed(() => {
  let result = stores.value

  // 按关键词搜索
  if (searchKeyword.value) {
    result = result.filter(store =>
      store.storeName.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }

  // 按状态筛选
  if (statusFilter.value) {
    result = result.filter(store => store.status === statusFilter.value)
  }

  return result
})

// 过滤后的订单列表
const filteredOrders = computed(() => {
  let result = orders.value

  if (activeOrderTab.value === 'pending') {
    result = result.filter(order => order.status === 'pending_payment' || order.status === 'pending_shipment')
  } else if (activeOrderTab.value === 'shipped') {
    result = result.filter(order => order.status === 'pending_receipt')
  } else if (activeOrderTab.value === 'completed') {
    result = result.filter(order => order.status === 'completed')
  }

  return result
})

// 方法
const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      merchantInfoDialogVisible.value = true
      break
    case 'settings':
      ElMessage.info('系统设置功能开发中...')
      break
    case 'logout':
      handleLogout()
      break
  }
}

const handleLogout = () => {
  authStore.logout()
  ElMessage.success('已退出登录')
  router.push('/merchant/login')
}

const handleMenuSelect = (index: string) => {
  activeMenu.value = index
  // 重置视图状态
  if (index === 'store-management') {
    currentView.value = 'list'
    loadDraftCount() // 加载草稿数量
  }
}

// 加载草稿数量
const loadDraftCount = () => {
  if (authStore.merchantInfo?.merchantId) {
    const drafts = draftService.getDraftsByMerchant(authStore.merchantInfo.merchantId)
    draftCount.value = drafts.length
  }
}

const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 加载Dashboard统计数据
const loadDashboardStats = async () => {
  try {
    if (!authStore.merchantInfo?.merchantId) {
      ElMessage.warning('商家信息不完整，请重新登录')
      return
    }
    
    const response = await getDashboardStats(authStore.merchantInfo.merchantId)
    if (response.success && response.data) {
      const stats = response.data
      storeCount.value = stats.storeCount
      productCount.value = stats.productCount
      orderCount.value = stats.orderCount
      revenue.value = stats.totalRevenue.toFixed(2)
      pendingOrderCount.value = stats.pendingOrderCount
      shippedOrderCount.value = stats.shippedOrderCount
      monthlyRevenue.value = stats.monthlyRevenue.toFixed(2)
      monthlyOrderCount.value = stats.monthlyOrderCount
    } else {
      ElMessage.error(response.message || '获取统计数据失败')
    }
  } catch (error) {
    console.error('加载Dashboard数据失败:', error)
    ElMessage.error('加载统计数据失败，请稍后重试')
  }
}

// 店铺管理相关方法
const loadStores = async () => {
  storeLoading.value = true
  try {
    if (!authStore.merchantInfo?.merchantId) {
      ElMessage.warning('商家信息不完整，请重新登录')
      return
    }
    
    const response = useExtendedApi.value 
      ? await getStoresExtendedByMerchantId(authStore.merchantInfo.merchantId)
      : await getStoresByMerchantId(authStore.merchantInfo.merchantId)
      
    if (response.success && response.data) {
      stores.value = response.data as StoreExtended[]
      console.log('加载店铺列表成功:', response.data)
    } else {
      ElMessage.error(response.message || '获取店铺列表失败')
      stores.value = []
    }
  } catch (error) {
    console.error('加载店铺列表失败:', error)
    ElMessage.error('加载店铺列表失败，请稍后重试')
    stores.value = []
  } finally {
    storeLoading.value = false
  }
}

const handleSearch = () => {
  // 搜索逻辑已在计算属性中处理
}

const handleStatusFilter = () => {
  // 筛选逻辑已在计算属性中处理
}

const refreshStoreList = () => {
  loadStores()
}

const handleEditStore = (store: StoreExtended) => {
  currentStore.value = store
  currentView.value = 'edit'
}

const handleManageProducts = (store: StoreExtended) => {
  currentStore.value = store
  currentView.value = 'products'
}

const handleDeleteStore = async (store: StoreExtended) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除店铺"${store.storeName}"吗？删除后无法恢复。\n注意：只有当店铺中没有商品时才能删除。`,
      '确认删除',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )
    
    // 调用API删除店铺
    const deleteApi = useExtendedApi.value
      ? deleteStoreExtended
      : deleteStore
      
    const response = await deleteApi(store.storeId)
      
    if (response.success) {
      // 从列表中移除被删除的店铺
      stores.value = stores.value.filter(s => s.storeId !== store.storeId)
      ElMessage.success('店铺删除成功')
      
      // 更新统计数据
      if (storeCount.value > 0) {
        storeCount.value -= 1
      }
    } else {
      ElMessage.error(response.message || '删除店铺失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除店铺失败:', error)
      ElMessage.error('删除店铺失败，请稍后重试')
    }
  }
}

const toggleStoreStatus = async (store: StoreExtended) => {
  const newStatus = store.status === 'open' ? 'suspended' : 'open'
  const action = newStatus === 'open' ? '恢复营业' : '暂停营业'
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}店铺"${store.storeName}"吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用API更新店铺状态
    const response = useExtendedApi.value
      ? await updateStoreExtended(store.storeId, { 
          storeName: store.storeName,
          storeDescription: store.storeDescription || '',
          storeLogo: store.storeLogo,
          category: store.category || '',
          status: newStatus,
          servicePromise: store.servicePromise || [],
          servicePhone: store.servicePhone,
          serviceEmail: store.serviceEmail,
          businessHours: store.businessHours || '周一至周日 9:00-22:00'
        })
      : await updateStore(store.storeId, { status: newStatus })
      
    if (response.success && response.data) {
      // 更新本地数据
      store.status = newStatus
      ElMessage.success(`${action}成功`)
    } else {
      ElMessage.error(response.message || `${action}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error(`${action}失败:`, error)
      ElMessage.error(`${action}失败，请稍后重试`)
    }
  }
}

const getStatusType = (status: string) => {
  switch (status) {
    case 'open':
      return 'success'
    case 'suspended':
      return 'warning'
    case 'closed':
      return 'danger'
    default:
      return 'info'
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'open':
      return '营业中'
    case 'suspended':
      return '已暂停'
    case 'closed':
      return '已关闭'
    default:
      return '未知'
  }
}

const formatDate = (dateString: string) => {
  if (!dateString) return '未知'
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.style.display = 'none'
}

// 订单管理相关方法
const loadOrders = async () => {
  try {
    // 模拟订单数据
    const mockOrders = [
      {
        orderId: 1,
        orderNo: 'ORD20241219001',
        customerName: '张三',
        storeName: '时尚服装店',
        totalAmount: 299.00,
        status: 'pending_payment',
        createTime: '2024-12-19T10:00:00'
      },
      {
        orderId: 2,
        orderNo: 'ORD20241219002',
        customerName: '李四',
        storeName: '数码科技馆',
        totalAmount: 2999.00,
        status: 'pending_shipment',
        createTime: '2024-12-19T11:00:00'
      },
      {
        orderId: 3,
        orderNo: 'ORD20241219003',
        customerName: '王五',
        storeName: '美食天地',
        totalAmount: 89.00,
        status: 'pending_receipt',
        createTime: '2024-12-19T12:00:00'
      },
      {
        orderId: 4,
        orderNo: 'ORD20241218001',
        customerName: '赵六',
        storeName: '时尚服装店',
        totalAmount: 599.00,
        status: 'completed',
        createTime: '2024-12-18T15:00:00'
      }
    ]
    
    orders.value = mockOrders
    
    // 计算统计数据
    totalOrders.value = mockOrders.length
    pendingOrders.value = mockOrders.filter(order => 
      order.status === 'pending_payment' || order.status === 'pending_shipment'
    ).length
    shippedOrders.value = mockOrders.filter(order => 
      order.status === 'pending_receipt'
    ).length
    completedOrders.value = mockOrders.filter(order => 
      order.status === 'completed'
    ).length
  } catch (error) {
    console.error('加载订单数据失败:', error)
    ElMessage.error('加载订单数据失败')
  }
}

const handleOrderTabChange = (tabName: string | number) => {
  activeOrderTab.value = String(tabName)
}

const handleCategoryTabChange = (tabName: string | number) => {
  categoryActiveTab.value = String(tabName)
}

const handleSwitchToAttributes = (category: any) => {
  // 切换到属性管理标签页
  categoryActiveTab.value = 'attributes'
  selectedCategoryForAttributes.value = category
  ElMessage.success(`已切换到"${category.categoryName}"的属性管理`)
}

// 统一的店铺操作成功处理
const handleStoreSuccess = async (storeData: StoreCreateData | StoreUpdateData | StoreExtendedCreateData | StoreExtendedUpdateData, storeId?: number) => {
  try {
    if (storeId) {
      // 更新操作
      const response = useExtendedApi.value
        ? await updateStoreExtended(storeId, storeData as StoreExtendedUpdateData)
        : await updateStore(storeId, storeData as StoreUpdateData)
        
      if (response.success && response.data) {
        // 更新本地店铺列表中的数据
        const storeIndex = stores.value.findIndex(store => store.storeId === storeId)
        if (storeIndex !== -1) {
          stores.value[storeIndex] = response.data as StoreExtended
        }
        
        // 返回列表视图
        currentView.value = 'list'
        currentStore.value = null
        
        ElMessage.success(`店铺"${response.data.storeName}"更新成功！`)
        
        // 重新加载Dashboard统计数据
        await loadDashboardStats()
      } else {
        ElMessage.error(response.message || '更新店铺失败')
      }
    } else {
      // 创建操作
      const response = useExtendedApi.value
        ? await createStoreExtended(storeData as StoreExtendedCreateData)
        : await createStore(storeData as StoreCreateData)
        
      if (response.success && response.data) {
        // 将新店铺添加到列表中
        stores.value.unshift(response.data as StoreExtended)
        
        // 更新统计数据
        storeCount.value = stores.value.length
        
        // 返回列表视图
        currentView.value = 'list'
        
        ElMessage.success(`店铺"${response.data.storeName}"创建成功！`)
        
        // 重新加载Dashboard统计数据
        await loadDashboardStats()
      } else {
        ElMessage.error(response.message || '创建店铺失败')
      }
    }
  } catch (error) {
    const action = storeId ? '更新' : '创建'
    console.error(`${action}店铺失败:`, error)
    ElMessage.error(`${action}店铺失败，请稍后重试`)
  }
}

// 组件挂载时检查登录状态
onMounted(async () => {
  console.log('Dashboard页面开始加载')
  
  // 初始化认证状态
  authStore.initializeAuth()
  
  // 详细的认证状态检查
  console.log('认证状态检查:')
  console.log('isLoggedIn:', authStore.isLoggedIn)
  console.log('merchantInfo:', authStore.merchantInfo)
  console.log('accessToken存在:', !!authStore.accessToken)
  console.log('localStorage token:', localStorage.getItem('merchant_access_token'))
  console.log('localStorage merchant_info:', localStorage.getItem('merchant_info'))
  
  // 检查token是否过期
  const tokenExpired = authStore.isTokenExpired()
  console.log('token是否过期:', tokenExpired)
  
  if (!authStore.isLoggedIn || tokenExpired) {
    console.log('认证失败，跳转到登录页')
    ElMessage.warning('请先登录')
    router.push('/merchant/login')
    return
  }
  
  console.log('认证通过，开始加载数据')
  
  // 加载Dashboard数据
  await loadDashboardStats()
  // 加载店铺数据
  await loadStores()
  // 加载订单数据
  await loadOrders()
  // 加载草稿数量
  loadDraftCount()
})
</script>

<style scoped>
.dashboard-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 64px;
  background-color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.header-left h1 {
  margin: 0;
  color: #333;
  font-size: 20px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-info span {
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

.dropdown-btn {
  padding: 4px;
  color: #666;
}

.main-content {
  display: flex;
  margin-top: 64px;
  min-height: calc(100vh - 64px);
}

.sidebar {
  width: 240px;
  background-color: white;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  position: relative;
  transition: width 0.3s ease;
}

.sidebar.collapsed {
  width: 64px;
}

.sidebar-menu {
  border-right: none;
  height: calc(100vh - 64px);
}

.sidebar-toggle {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  width: 32px;
  height: 32px;
  background-color: #f5f7fa;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.sidebar-toggle:hover {
  background-color: #e6f7ff;
}

.content-area {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e8e8e8;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.draft-info {
  display: flex;
  align-items: center;
}

.page-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.page-header p {
  margin: 4px 0 0 0;
  color: #666;
  font-size: 14px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.shop-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.product-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.order-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.revenue-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info h3 {
  margin: 0;
  color: #333;
  font-size: 28px;
  font-weight: 600;
}

.stat-info p {
  margin: 4px 0 0 0;
  color: #666;
  font-size: 14px;
}

.charts-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.chart-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart-card h3 {
  margin: 0 0 16px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.chart-placeholder {
  height: 200px;
  background: #f8f9fa;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}

.order-tabs {
  display: flex;
  gap: 8px;
}

.placeholder {
  background: white;
  border-radius: 12px;
  padding: 60px 24px;
  text-align: center;
  color: #666;
  font-size: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 店铺管理样式 */
.store-management-content {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.store-list-container {
  margin-top: 24px;
}

.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e8e8e8;
}

.store-cards {
  display: grid;
  gap: 16px;
}

.store-card {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 20px;
  transition: all 0.3s ease;
}

.store-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
}

.store-header {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 16px;
}

.store-logo {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.store-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-logo {
  font-size: 24px;
  color: #909399;
}

.store-info {
  flex: 1;
}

.store-info h3 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.store-description {
  margin: 0 0 12px 0;
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

.store-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #999;
}

.store-status {
  flex-shrink: 0;
}

.store-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 订单管理样式 */
.order-management-content {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.order-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 32px;
}

.all-orders {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.pending-orders {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.shipped-orders {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.completed-orders {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.order-tabs-container {
  margin-top: 24px;
}

/* 分类管理样式 */
.category-management-content {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  height: 100%;
}

.management-tabs {
  height: 100%;
}

.management-tabs :deep(.el-tabs__content) {
  height: calc(100% - 40px);
  overflow: hidden;
}

.management-tabs :deep(.el-tab-pane) {
  height: 100%;
  overflow: hidden;
}

@media (max-width: 768px) {
  .sidebar {
    width: 64px;
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .charts-section {
    grid-template-columns: 1fr;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .search-bar {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .store-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .store-actions {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .order-stats {
    grid-template-columns: 1fr;
  }
}
</style>