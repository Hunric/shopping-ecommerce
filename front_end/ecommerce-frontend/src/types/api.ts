/**
 * 电商平台前端API类型定义文件
 * 
 * @description 定义了前端应用中使用的API相关的TypeScript类型接口。
 *              提供统一的类型定义，确保API调用的类型安全性。
 * 
 * @features
 * - 统一的API响应格式类型定义
 * - 泛型支持，适用于不同数据类型的响应
 * - TypeScript类型安全保障
 * - 与后端API响应格式保持一致
 * 
 * @interfaces
 * - ApiResponse<T>: 通用API响应格式
 *   - success: 操作是否成功
 *   - message: 响应消息或错误描述
 *   - data: 实际响应数据（泛型）
 * 
 * @usage_examples
 * <pre>
 * // 用户信息响应
 * const userResponse: ApiResponse<User> = await getUserInfo()
 * 
 * // 商品列表响应
 * const productsResponse: ApiResponse<Product[]> = await getProducts()
 * 
 * // 操作结果响应
 * const operationResponse: ApiResponse<string> = await deleteItem(id)
 * </pre>
 * 
 * @consistency
 * 该类型定义与后端ApiResponse类保持一致，
 * 确保前后端数据交互的类型安全性。
 * 
 * @author 开发团队
 * @version 1.0.0
 * @since 2024
 * 
 * @see {@link ../api/merchant.ts} 商家API接口
 * @see {@link ../api/upload.ts} 文件上传API接口
 */

// API响应类型
export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
} 