<template>
  <div class="checkout-container">
    <div class="checkout-header">
      <div class="header-content">
        <h2>确认订单</h2>
        <BackToHomeButton size="small" type="info" />
      </div>
    </div>

    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <div v-else class="checkout-content">
      <!-- 收货地址 -->
      <div class="address-section">
        <div class="section-header">
          <h3>收货地址</h3>
          <el-button type="primary" size="small" @click="showAddressDialog = true">
            新增地址
          </el-button>
        </div>
        
        <div v-if="addressLoading" class="loading-address">
          <el-skeleton :rows="3" animated />
        </div>
        
        <div v-else-if="addressList.length === 0" class="no-address">
          <el-empty description="您还没有收货地址，请添加">
            <el-button type="primary" @click="showAddressDialog = true">添加收货地址</el-button>
          </el-empty>
        </div>
        
        <div v-else class="address-list">
          <el-radio-group v-model="selectedAddressId">
            <div v-for="address in addressList" :key="address.shippingId" class="address-item">
              <el-radio :label="address.shippingId">
                <div class="address-content">
                  <div class="address-info">
                    <span class="name">{{ address.receiverName }}</span>
                    <span class="phone">{{ address.receiverPhone }}</span>
                    <span v-if="address.isDefault" class="default-tag">默认</span>
                  </div>
                  <div class="address-detail">
                    {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detailAddress }}
                  </div>
                </div>
              </el-radio>
              <div class="address-actions">
                <el-button type="text" @click="editAddress(address)">编辑</el-button>
                <el-button type="text" @click="deleteAddress(address.shippingId!)">删除</el-button>
              </div>
            </div>
          </el-radio-group>
        </div>
      </div>

      <!-- 商品清单 -->
      <div class="products-section">
        <h3>商品清单</h3>
        <div class="product-list">
          <div 
            v-for="item in selectedItems" 
            :key="item.cartId" 
            class="product-item"
          >
            <div class="product-image">
              <img :src="item.productImage || '/default-product.png'" :alt="item.productName" />
            </div>
            <div class="product-info">
              <h4>{{ item.productName }}</h4>
              <p class="product-attr">{{ item.skuAttr }}</p>
              <div class="price-quantity">
                <span class="price">¥{{ item.skuPrice.toFixed(2) }}</span>
                <span class="quantity">x{{ item.quantity }}</span>
              </div>
            </div>
            <div class="subtotal">
              ¥{{ item.subtotal.toFixed(2) }}
            </div>
          </div>
        </div>
      </div>

      <!-- 支付方式 -->
      <div class="payment-section">
        <h3>支付方式</h3>
        <div class="payment-methods">
          <label class="payment-option">
            <input 
              type="radio" 
              value="alipay" 
              v-model="orderForm.paymentMethod"
            />
            <span class="payment-icon">💰</span>
            支付宝
          </label>
          <label class="payment-option">
            <input 
              type="radio" 
              value="wechat" 
              v-model="orderForm.paymentMethod"
            />
            <span class="payment-icon">💚</span>
            微信支付
          </label>
          <label class="payment-option">
            <input 
              type="radio" 
              value="bank" 
              v-model="orderForm.paymentMethod"
            />
            <span class="payment-icon">🏦</span>
            银行卡
          </label>
        </div>
      </div>

      <!-- 订单备注 -->
      <div class="remark-section">
        <h3>订单备注</h3>
        <textarea 
          v-model="orderForm.remark" 
          placeholder="选填，对本次交易的说明（建议填写已与商家协商一致的说明）"
          class="remark-textarea"
          rows="3"
        ></textarea>
      </div>

      <!-- 价格汇总 -->
      <div class="price-summary">
        <div class="summary-row">
          <span>商品总价：</span>
          <span>¥{{ totalPrice.toFixed(2) }}</span>
        </div>
        <div class="summary-row">
          <span>运费：</span>
          <span>¥{{ shippingFee.toFixed(2) }}</span>
        </div>
        <div class="summary-row total">
          <span>应付总额：</span>
          <span class="total-price">¥{{ finalPrice.toFixed(2) }}</span>
        </div>
      </div>

      <!-- 提交订单 -->
      <div class="submit-section">
        <button @click="goBack" class="back-btn">{{ backButtonText }}</button>
        <button 
          @click="submitOrder" 
          :disabled="!canSubmit || submitting"
          class="submit-btn"
        >
          {{ submitting ? '提交中...' : '提交订单' }}
        </button>
      </div>
    </div>

    <!-- 地址编辑对话框 -->
    <el-dialog
      :title="addressForm.shippingId ? '编辑收货地址' : '新增收货地址'"
      v-model="showAddressDialog"
      width="500px"
    >
      <el-form :model="addressForm" label-width="100px" :rules="addressRules" ref="addressFormRef">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="addressForm.receiverName" placeholder="请输入收货人姓名"></el-input>
        </el-form-item>
        <el-form-item label="手机号码" prop="receiverPhone">
          <el-input v-model="addressForm.receiverPhone" placeholder="请输入手机号码"></el-input>
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="addressForm.province" placeholder="请输入省份"></el-input>
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="addressForm.city" placeholder="请输入城市"></el-input>
        </el-form-item>
        <el-form-item label="区/县" prop="district">
          <el-input v-model="addressForm.district" placeholder="请输入区/县"></el-input>
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input
            v-model="addressForm.detailAddress"
            type="textarea"
            :rows="2"
            placeholder="请输入详细地址"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="addressForm.isDefault">设为默认收货地址</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddressDialog = false">取消</el-button>
          <el-button type="primary" @click="saveAddress" :loading="addressSaving">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCartList, type CartItemResponseDTO } from '@/api/user/cart'
import { createOrder, type CreateOrderDTO, type CreateOrderItemDTO } from '@/api/user/order'
import { useUserAuthStore } from '@/store/modules/userAuth'
import { 
  getShippingInfoList, 
  createShippingInfo, 
  updateShippingInfo, 
  deleteShippingInfo,
  type ShippingInfo
} from '@/api/user/shipping'
import BackToHomeButton from '@/components/common/BackToHomeButton.vue'

const router = useRouter()
const userAuthStore = useUserAuthStore()

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const selectedItems = ref<CartItemResponseDTO[]>([])
const isFromBuyNow = ref(false)

// 地址相关
const addressLoading = ref(false)
const addressList = ref<ShippingInfo[]>([])
const selectedAddressId = ref<number | null>(null)
const showAddressDialog = ref(false)
const addressSaving = ref(false)
const addressForm = reactive<ShippingInfo>({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: false
})

// 订单表单
const orderForm = ref({
  paymentMethod: 'alipay',
  remark: ''
})

// 表单校验规则
const addressRules = {
  receiverName: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  receiverPhone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  province: [
    { required: true, message: '请输入省份', trigger: 'blur' }
  ],
  city: [
    { required: true, message: '请输入城市', trigger: 'blur' }
  ],
  district: [
    { required: true, message: '请输入区/县', trigger: 'blur' }
  ],
  detailAddress: [
    { required: true, message: '请输入详细地址', trigger: 'blur' },
    { min: 5, max: 100, message: '长度在 5 到 100 个字符', trigger: 'blur' }
  ]
}

// 计算属性
const totalPrice = computed(() => {
  return selectedItems.value.reduce((total, item) => total + item.subtotal, 0)
})

const shippingFee = computed(() => {
  // 简单的运费计算逻辑，可以根据实际需求调整
  return totalPrice.value >= 99 ? 0 : 10
})

const finalPrice = computed(() => {
  return totalPrice.value + shippingFee.value
})

const canSubmit = computed(() => {
  return selectedAddressId.value !== null && 
         selectedItems.value.length > 0 &&
         orderForm.value.paymentMethod !== ''
})

const backButtonText = computed(() => {
  return isFromBuyNow.value ? '返回商品详情' : '返回购物车'
})

// 方法
const loadSelectedItems = async () => {
  try {
    loading.value = true
    const response = await getCartList()
    
    // 修复：访问response.data获取实际的API响应数据
    const apiResponse = response.data
    if (apiResponse.success) {
      // 只获取已选中的商品
      selectedItems.value = (apiResponse.data || []).filter(item => item.isSelected)
      
      if (selectedItems.value.length === 0) {
        ElMessage.warning('没有选中的商品，请返回购物车选择商品')
        router.push('/cart')
        return
      }
    } else {
      ElMessage.error(apiResponse.message || '获取购物车失败')
      router.push('/cart')
    }
  } catch (error) {
    console.error('获取购物车失败:', error)
    ElMessage.error('获取购物车失败')
    router.push('/cart')
  } finally {
    loading.value = false
  }
}

// 加载收货地址
const loadAddressList = async () => {
  addressLoading.value = true
  try {
    const response = await getShippingInfoList()
    
    // 修复：访问response.data获取实际的API响应数据
    const apiResponse = response.data
    console.log('收货地址API响应:', apiResponse)
    
    if (apiResponse.success) {
      addressList.value = apiResponse.data || []
      console.log('收货地址数据加载成功:', addressList.value)
      
      // 如果有默认地址，自动选中
      const defaultAddress = addressList.value.find(addr => addr.isDefault)
      if (defaultAddress) {
        selectedAddressId.value = defaultAddress.shippingId!
      } else if (addressList.value.length > 0) {
        selectedAddressId.value = addressList.value[0].shippingId!
      }
    } else {
      console.error('获取收货地址失败:', apiResponse.message)
      ElMessage.error(apiResponse.message || '获取收货地址失败')
    }
  } catch (error) {
    console.error('加载收货地址失败:', error)
    ElMessage.error('加载收货地址失败')
  } finally {
    addressLoading.value = false
  }
}

// 编辑地址
const editAddress = (address: ShippingInfo) => {
  Object.assign(addressForm, address)
  showAddressDialog.value = true
}

// 删除地址
const deleteAddress = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个地址吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await deleteShippingInfo(id)
    
    // 修复：访问response.data获取实际的API响应数据
    const apiResponse = response.data
    if (apiResponse.success) {
      ElMessage.success('地址删除成功')
      loadAddressList()
    } else {
      ElMessage.error(apiResponse.message || '地址删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除地址失败:', error)
      ElMessage.error('删除地址失败')
    }
  }
}

// 保存地址
const saveAddress = async () => {
  // 表单验证
  if (!addressForm.receiverName || !addressForm.receiverPhone || 
      !addressForm.province || !addressForm.city || !addressForm.district || 
      !addressForm.detailAddress) {
    ElMessage.warning('请填写完整的地址信息')
    return
  }
  
  addressSaving.value = true
  try {
    let response
    if (addressForm.shippingId) {
      // 更新地址
      response = await updateShippingInfo(addressForm.shippingId, addressForm)
    } else {
      // 创建地址
      response = await createShippingInfo(addressForm)
    }
    
    // 修复：访问response.data获取实际的API响应数据
    const apiResponse = response.data
    if (apiResponse.success) {
      ElMessage.success(addressForm.shippingId ? '地址更新成功' : '地址添加成功')
      showAddressDialog.value = false
      
      // 重置表单
      Object.assign(addressForm, {
        receiverName: '',
        receiverPhone: '',
        province: '',
        city: '',
        district: '',
        detailAddress: '',
        isDefault: false
      })
      
      // 重新加载地址列表
      loadAddressList()
    } else {
      ElMessage.error(apiResponse.message || '操作失败')
    }
  } catch (error) {
    console.error('保存地址失败:', error)
    ElMessage.error('保存地址失败')
  } finally {
    addressSaving.value = false
  }
}

const submitOrder = async () => {
  if (!canSubmit.value) {
    if (!selectedAddressId.value) {
      ElMessage.warning('请选择收货地址')
    } else {
      ElMessage.warning('请完善订单信息')
    }
    return
  }

  try {
    await ElMessageBox.confirm(
      `确认提交订单吗？应付总额：¥${finalPrice.value.toFixed(2)}`,
      '确认订单',
      {
        confirmButtonText: '确认提交',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    submitting.value = true
    
    // 初始化用户认证状态
    userAuthStore.initializeAuth()
    
    // 检查用户是否已登录
    if (!userAuthStore.isLoggedIn || !userAuthStore.userInfo) {
      ElMessage.warning('请先登录用户账号')
      router.push('/user/login')
      return
    }

    // 获取店铺ID（假设同一个订单中的商品来自同一个店铺）
    const storeIdRaw = selectedItems.value.length > 0 ? selectedItems.value[0].storeId : null
    console.log('=== 结算页面 - 店铺ID处理 ===')
    console.log('原始storeId值:', storeIdRaw)
    console.log('selectedItems第一项:', selectedItems.value.length > 0 ? selectedItems.value[0] : 'none')
    
    const storeId = Number(storeIdRaw) || 1 // 如果转换结果为NaN，则使用默认值1
    console.log('转换后的storeId:', storeId)
    
    // 构造后端期望的订单数据
    const orderItems: CreateOrderItemDTO[] = selectedItems.value.map(item => ({
      productId: Number(item.productId) || 0, // 确保转换为数字类型
      productName: item.productName,
      productImage: item.productImage,
      skuId: Number(item.skuId) || 0, // 确保转换为数字类型
      skuSpecs: item.skuAttr,
      productPrice: Number(item.skuPrice) || 0, // 确保转换为数字类型
      quantity: Number(item.quantity) || 1, // 确保转换为数字类型
      subtotal: Number(item.subtotal) || 0 // 确保转换为数字类型
    }))

    const createOrderData: CreateOrderDTO = {
      userId: Number(userAuthStore.userInfo.userId) || 0, // 确保转换为数字类型
      storeId: Number(storeId) || 1, // 确保storeId是数字类型，如果是NaN则使用默认值1
      shippingId: Number(selectedAddressId.value) || 0, // 使用选择的地址ID
      totalAmount: Number(totalPrice.value) || 0, // 确保转换为数字类型
      actualAmount: Number(finalPrice.value) || 0, // 确保转换为数字类型
      discountAmount: 0,
      shippingFee: Number(shippingFee.value) || 0, // 确保转换为数字类型
      orderNote: orderForm.value.remark || '',
      orderItems: orderItems
    }
    
    console.log('提交订单数据:', JSON.stringify(createOrderData))
    
    const response = await createOrder(createOrderData)
    
    console.log('=== 订单创建响应数据 ===')
    console.log('响应数据:', response)
    
    const apiResponse = response.data
    if (apiResponse.success) {
      ElMessage.success('订单提交成功！')
      // 跳转到支付页面
      router.push(`/payment/${apiResponse.data}`)
    } else {
      ElMessage.error(apiResponse.message || '订单提交失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交订单失败:', error)
      ElMessage.error('订单提交失败，请重试')
    }
  } finally {
    submitting.value = false
  }
}

const goBack = () => {
  if (isFromBuyNow.value) {
    router.go(-1) // 返回上一页（商品详情页）
  } else {
    router.push('/cart')
  }
}

// 生命周期
onMounted(() => {
  // 初始化用户认证状态
  userAuthStore.initializeAuth()
  
  // 检查用户是否已登录
  if (!userAuthStore.isLoggedIn || !userAuthStore.userInfo) {
    ElMessage.warning('请先登录用户账号')
    router.push('/user/login')
    return
  }
  
  // 检查是否是从立即购买跳转过来的
  const urlParams = new URLSearchParams(window.location.search)
  const fromBuyNow = urlParams.get('from') === 'buyNow'
  isFromBuyNow.value = fromBuyNow
  
  if (fromBuyNow) {
    console.log('=== 结算页面 - 处理立即购买 ===')
    // 从 sessionStorage 获取立即购买的商品信息
    const buyNowItemStr = sessionStorage.getItem('buyNowItem')
    console.log('sessionStorage中的buyNowItem:', buyNowItemStr)
    
    if (buyNowItemStr) {
      try {
        const buyNowItem = JSON.parse(buyNowItemStr)
        console.log('解析的立即购买商品:', buyNowItem)
        console.log('立即购买商品的storeId:', buyNowItem.storeId)
        
        // 转换为结算页面需要的格式
        const checkoutItem = {
          cartId: 0, // 立即购买没有购物车ID
          productId: Number(buyNowItem.productId || buyNowItem.spuId) || 0, // 使用productId或spuId
          productName: buyNowItem.productName || buyNowItem.spuName || '',
          productImage: buyNowItem.productImage || '',
          skuId: Number(buyNowItem.skuId || buyNowItem.productId || buyNowItem.spuId) || 0, // 使用skuId或productId或spuId
          skuAttr: buyNowItem.brandName ? `品牌: ${buyNowItem.brandName}` : '默认规格',
          skuPrice: Number(buyNowItem.price || buyNowItem.displayPrice) || 0,
          quantity: Number(buyNowItem.quantity) || 1,
          subtotal: Number((buyNowItem.price || buyNowItem.displayPrice) * (buyNowItem.quantity || 1)) || 0,
          storeId: Number(buyNowItem.storeId) || 1, // 确保storeId是数字类型，如果是NaN则使用默认值1
          isSelected: true,
          createTime: new Date().toISOString(),
          updateTime: new Date().toISOString()
        }
        
        console.log('转换后的结算商品:', checkoutItem)
        console.log('结算商品的storeId:', checkoutItem.storeId)
        
        selectedItems.value = [checkoutItem]
        console.log('转换后的结算商品数组:', selectedItems.value)
        
        // 清除 sessionStorage 中的数据
        sessionStorage.removeItem('buyNowItem')
        console.log('=== 立即购买商品处理完成 ===')
      } catch (error) {
        console.error('解析立即购买商品信息失败:', error)
        ElMessage.error('商品信息获取失败')
        router.push('/')
        return
      }
    } else {
      console.error('sessionStorage中没有找到buyNowItem')
      ElMessage.error('商品信息不存在')
      router.push('/')
      return
    }
  } else {
    console.log('=== 结算页面 - 处理购物车商品 ===')
    // 正常从购物车跳转过来，加载购物车商品
    loadSelectedItems()
  }
  
  // 加载收货地址列表
  loadAddressList()
})
</script>

<style scoped>
.checkout-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.checkout-header {
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.checkout-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
}

.loading {
  text-align: center;
  padding: 50px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #409eff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.checkout-content > div {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.checkout-content h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

/* 地址相关样式 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
  border: none;
  padding: 0;
}

.loading-address {
  padding: 20px;
}

.no-address {
  text-align: center;
  padding: 40px 20px;
}

.address-list {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
}

.address-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
  position: relative;
}

.address-item:last-child {
  border-bottom: none;
}

.address-item:hover {
  background-color: #f8f9fa;
}

.address-content {
  flex: 1;
  margin-left: 10px;
}

.address-info {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 8px;
}

.address-info .name {
  font-weight: 600;
  color: #333;
}

.address-info .phone {
  color: #666;
}

.default-tag {
  background: #409eff;
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.address-detail {
  color: #666;
  font-size: 14px;
  line-height: 1.4;
}

.address-actions {
  display: flex;
  gap: 10px;
}

/* 商品清单样式 */
.product-list {
  border: 1px solid #eee;
  border-radius: 4px;
  overflow: hidden;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.product-item:last-child {
  border-bottom: none;
}

.product-image {
  width: 60px;
  height: 60px;
  margin-right: 15px;
  flex-shrink: 0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.product-info {
  flex: 1;
}

.product-info h4 {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #333;
}

.product-attr {
  margin: 0 0 8px 0;
  color: #666;
  font-size: 14px;
}

.price-quantity {
  display: flex;
  align-items: center;
  gap: 15px;
}

.price {
  color: #f56c6c;
  font-weight: 600;
}

.quantity {
  color: #666;
}

.subtotal {
  color: #f56c6c;
  font-size: 18px;
  font-weight: 600;
  margin-left: 20px;
}

/* 支付方式样式 */
.payment-methods {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.payment-option {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  border: 2px solid #eee;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  min-width: 120px;
}

.payment-option:hover {
  border-color: #409eff;
}

.payment-option input[type="radio"] {
  margin-right: 10px;
}

.payment-option input[type="radio"]:checked + .payment-icon {
  transform: scale(1.2);
}

.payment-option:has(input[type="radio"]:checked) {
  border-color: #409eff;
  background: #f0f9ff;
}

.payment-icon {
  margin-right: 8px;
  font-size: 18px;
  transition:
transform 0.3s;
}

/* 订单备注样式 */
.remark-textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  resize: vertical;
  font-family: inherit;
}

.remark-textarea:focus {
  outline: none;
  border-color: #409eff;
}

/* 价格汇总样式 */
.price-summary {
  border-top: 1px solid #eee;
  padding-top: 20px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 16px;
}

.summary-row.total {
  font-size: 18px;
  font-weight: 600;
  border-top: 1px solid #eee;
  padding-top: 15px;
  margin-top: 15px;
}

.total-price {
  color: #f56c6c;
  font-size: 20px;
}

/* 提交订单样式 */
.submit-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.back-btn {
  padding: 12px 30px;
  border: 1px solid #ddd;
  background: white;
  color: #666;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s;
}

.back-btn:hover {
  border-color: #409eff;
  color: #409eff;
}

.submit-btn {
  padding: 12px 40px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s;
}

.submit-btn:hover:not(:disabled) {
  background: #66b1ff;
}

.submit-btn:disabled {
  background: #c0c4cc;
  cursor: not-allowed;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .checkout-container {
    padding: 10px;
  }
  
  .checkout-content > div {
    padding: 15px;
    margin-bottom: 15px;
  }
  
  .address-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .address-actions {
    align-self: flex-end;
  }
  
  .product-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .product-image {
    margin-right: 0;
    margin-bottom: 10px;
  }
  
  .subtotal {
    margin-left: 0;
    align-self: flex-end;
  }
  
  .payment-methods {
    flex-direction: column;
    gap: 10px;
  }
  
  .payment-option {
    min-width: auto;
    width: 100%;
  }
  
  .submit-section {
    flex-direction: column;
    gap: 15px;
  }
  
  .back-btn,
  .submit-btn {
    width: 100%;
  }
}

/* 对话框样式调整 */
.dialog-footer {
  display: flex;
  gap: 10px;
}

/* 空状态样式 */
.el-empty {
  padding: 40px 20px;
}

/* 骨架屏样式 */
.el-skeleton {
  padding: 20px;
}

/* 单选框组样式调整 */
.el-radio-group {
  width: 100%;
}

.el-radio {
  width: 100%;
  margin-right: 0;
  margin-bottom: 0;
}

.el-radio__label {
  width: 100%;
  padding-left: 10px;
}
</style>