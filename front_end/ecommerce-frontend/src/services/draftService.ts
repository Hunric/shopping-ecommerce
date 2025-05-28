import { ElMessage } from 'element-plus'

// 草稿数据接口
export interface StoreDraft {
  id: string
  merchantId: number
  storeName: string
  storeDescription: string
  category: string
  storeLogo: string
  status: string
  servicePromise: string[]
  servicePhone: string
  serviceEmail: string
  businessHours: string
  createTime: string
  updateTime: string
  title: string // 草稿标题，用于显示
}

// 草稿列表项接口
export interface DraftListItem {
  id: string
  title: string
  createTime: string
  updateTime: string
  preview: string // 预览内容
}

class DraftService {
  private readonly STORAGE_KEY = 'store_drafts'
  private readonly MAX_DRAFTS = 10 // 最大草稿数量

  /**
   * 保存草稿
   */
  saveDraft(formData: any, merchantId: number, title?: string): string {
    try {
      const drafts = this.getAllDrafts()
      const now = new Date().toISOString()
      
      // 生成草稿ID
      const draftId = `draft_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
      
      // 生成草稿标题
      const draftTitle = title || this.generateDraftTitle(formData.storeName)
      
      // 创建草稿对象
      const draft: StoreDraft = {
        id: draftId,
        merchantId,
        storeName: formData.storeName || '',
        storeDescription: formData.storeDescription || '',
        category: formData.category || '',
        storeLogo: formData.storeLogo || '',
        status: formData.status || 'open',
        servicePromise: formData.servicePromise || [],
        servicePhone: formData.servicePhone || '',
        serviceEmail: formData.serviceEmail || '',
        businessHours: formData.businessHours || '周一至周日 9:00-22:00',
        createTime: now,
        updateTime: now,
        title: draftTitle
      }

      // 添加到草稿列表
      drafts.unshift(draft)

      // 限制草稿数量
      if (drafts.length > this.MAX_DRAFTS) {
        drafts.splice(this.MAX_DRAFTS)
      }

      // 保存到本地存储
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(drafts))
      
      ElMessage.success(`草稿"${draftTitle}"已保存`)
      return draftId
    } catch (error) {
      console.error('保存草稿失败:', error)
      ElMessage.error('保存草稿失败')
      throw error
    }
  }

  /**
   * 更新草稿
   */
  updateDraft(draftId: string, formData: any, title?: string): void {
    try {
      const drafts = this.getAllDrafts()
      const draftIndex = drafts.findIndex(d => d.id === draftId)
      
      if (draftIndex === -1) {
        throw new Error('草稿不存在')
      }

      const existingDraft = drafts[draftIndex]
      const now = new Date().toISOString()
      
      // 更新草稿
      const updatedDraft: StoreDraft = {
        ...existingDraft,
        storeName: formData.storeName || '',
        storeDescription: formData.storeDescription || '',
        category: formData.category || '',
        storeLogo: formData.storeLogo || '',
        status: formData.status || 'open',
        servicePromise: formData.servicePromise || [],
        servicePhone: formData.servicePhone || '',
        serviceEmail: formData.serviceEmail || '',
        businessHours: formData.businessHours || '周一至周日 9:00-22:00',
        updateTime: now,
        title: title || existingDraft.title
      }

      drafts[draftIndex] = updatedDraft
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(drafts))
      
      ElMessage.success(`草稿"${updatedDraft.title}"已更新`)
    } catch (error) {
      console.error('更新草稿失败:', error)
      ElMessage.error('更新草稿失败')
      throw error
    }
  }

  /**
   * 获取所有草稿
   */
  getAllDrafts(): StoreDraft[] {
    try {
      const draftsJson = localStorage.getItem(this.STORAGE_KEY)
      return draftsJson ? JSON.parse(draftsJson) : []
    } catch (error) {
      console.error('获取草稿列表失败:', error)
      return []
    }
  }

  /**
   * 获取指定商家的草稿列表
   */
  getDraftsByMerchant(merchantId: number): DraftListItem[] {
    try {
      const allDrafts = this.getAllDrafts()
      return allDrafts
        .filter(draft => draft.merchantId === merchantId)
        .map(draft => ({
          id: draft.id,
          title: draft.title,
          createTime: draft.createTime,
          updateTime: draft.updateTime,
          preview: this.generatePreview(draft)
        }))
        .sort((a, b) => new Date(b.updateTime).getTime() - new Date(a.updateTime).getTime())
    } catch (error) {
      console.error('获取商家草稿列表失败:', error)
      return []
    }
  }

  /**
   * 根据ID获取草稿
   */
  getDraftById(draftId: string): StoreDraft | null {
    try {
      const drafts = this.getAllDrafts()
      return drafts.find(draft => draft.id === draftId) || null
    } catch (error) {
      console.error('获取草稿失败:', error)
      return null
    }
  }

  /**
   * 删除草稿
   */
  deleteDraft(draftId: string): void {
    try {
      const drafts = this.getAllDrafts()
      const filteredDrafts = drafts.filter(draft => draft.id !== draftId)
      
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(filteredDrafts))
      ElMessage.success('草稿已删除')
    } catch (error) {
      console.error('删除草稿失败:', error)
      ElMessage.error('删除草稿失败')
      throw error
    }
  }

  /**
   * 清空所有草稿
   */
  clearAllDrafts(): void {
    try {
      localStorage.removeItem(this.STORAGE_KEY)
      ElMessage.success('所有草稿已清空')
    } catch (error) {
      console.error('清空草稿失败:', error)
      ElMessage.error('清空草稿失败')
      throw error
    }
  }

  /**
   * 生成草稿标题
   */
  private generateDraftTitle(storeName: string): string {
    if (storeName && storeName.trim()) {
      return `${storeName.trim()} - 草稿`
    }
    
    const now = new Date()
    const timeStr = now.toLocaleString('zh-CN', {
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
    return `未命名店铺 - ${timeStr}`
  }

  /**
   * 生成草稿预览内容
   */
  private generatePreview(draft: StoreDraft): string {
    const parts = []
    
    if (draft.storeName) {
      parts.push(`店铺：${draft.storeName}`)
    }
    
    if (draft.category) {
      const categoryMap: Record<string, string> = {
        clothing: '服装鞋帽',
        electronics: '数码电器',
        food: '食品饮料',
        beauty: '美妆护肤',
        home: '家居用品',
        sports: '运动户外',
        baby: '母婴用品',
        books: '图书文具',
        other: '其他'
      }
      parts.push(`类目：${categoryMap[draft.category] || draft.category}`)
    }
    
    if (draft.storeDescription) {
      const desc = draft.storeDescription.length > 30 
        ? draft.storeDescription.substring(0, 30) + '...'
        : draft.storeDescription
      parts.push(`描述：${desc}`)
    }
    
    return parts.join(' | ') || '暂无内容'
  }

  /**
   * 检查是否有未保存的更改
   */
  hasUnsavedChanges(formData: any, lastSavedData?: any): boolean {
    if (!lastSavedData) return true
    
    // 比较关键字段
    const currentData = {
      storeName: formData.storeName || '',
      storeDescription: formData.storeDescription || '',
      category: formData.category || '',
      storeLogo: formData.storeLogo || '',
      status: formData.status || 'open',
      servicePromise: formData.servicePromise || [],
      servicePhone: formData.servicePhone || '',
      serviceEmail: formData.serviceEmail || '',
      businessHours: formData.businessHours || ''
    }
    
    return JSON.stringify(currentData) !== JSON.stringify(lastSavedData)
  }
}

// 导出单例
export const draftService = new DraftService()
export default draftService 