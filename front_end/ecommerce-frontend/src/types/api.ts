// API响应类型
export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
} 