// --- com.ecommerce.service.ProductService.java (接口) ---
package com.ecommerce.service;

import com.ecommerce.common.dto.PageResult;
import com.ecommerce.domain.dto.ProductCreateDTO;
import com.ecommerce.domain.dto.ProductDTO;
import com.ecommerce.domain.dto.ProductQueryDTO;
import com.ecommerce.domain.dto.ProductUpdateDTO;
import com.ecommerce.exception.CategoryNotFoundException;
import com.ecommerce.exception.InsufficientStockException; // 确保导入
import com.ecommerce.exception.ProductNotFoundException;

/**
 * 商品服务接口
 * 定义商品相关的业务操作
 */
public interface ProductService {

    /**
     * 创建新商品
     *
     * @param createDTO 包含新商品信息的 DTO
     * @return 创建成功的商品信息 (ProductDTO)
     * @throws CategoryNotFoundException 如果指定的分类 ID 不存在
     */
    ProductDTO createProduct(ProductCreateDTO createDTO);

    /**
     * 根据 ID 获取商品详情 (包含缓存逻辑)
     *
     * @param id 商品 ID
     * @return 商品详情 (ProductDTO)
     * @throws ProductNotFoundException 如果商品未找到
     */
    ProductDTO getProductById(Long id);

    /**
     * 更新商品信息 (包含缓存处理)
     *
     * @param id        要更新的商品 ID
     * @param updateDTO 包含更新信息的 DTO
     * @return 更新后的商品信息 (ProductDTO)
     * @throws ProductNotFoundException 如果商品未找到
     * @throws CategoryNotFoundException 如果更新时指定了无效的分类 ID
     */
    ProductDTO updateProduct(Long id, ProductUpdateDTO updateDTO);

    /**
     * 删除商品 (包含缓存处理)
     *
     * @param id 要删除的商品 ID
     * @throws ProductNotFoundException 如果商品未找到
     * @throws RuntimeException 如果商品有关联的未完成订单等，不允许删除 (具体逻辑待实现)
     */
    void deleteProduct(Long id);

    /**
     * 更新商品状态 (上架/下架) (包含缓存处理)
     *
     * @param id     商品 ID
     * @param status 新的状态值 (例如 1: 上架, 0: 下架)
     * @throws ProductNotFoundException 如果商品未找到
     * @throws IllegalArgumentException 如果状态值无效
     */
    void updateProductStatus(Long id, Integer status);

    /**
     * 根据查询条件分页、过滤、排序查找商品列表
     *
     * @param queryDTO 包含查询参数的 DTO
     * @return 分页后的商品列表结果 (PageResult<ProductDTO>)
     */
    PageResult<ProductDTO> listProducts(ProductQueryDTO queryDTO);

    /**
     * 扣减商品库存 (包含缓存处理)
     *
     * @param productId 商品 ID
     * @param quantity  要扣减的数量 (必须为正数)
     * @throws ProductNotFoundException   如果商品未找到
     * @throws InsufficientStockException 如果库存不足
     * @throws IllegalArgumentException   如果扣减数量不是正数
     */
    void decreaseStock(Long productId, Integer quantity);

    /**
     * 增加商品库存 (例如：订单取消、退货入库) (包含缓存处理)
     *
     * @param productId 商品 ID
     * @param quantity  要增加的数量 (必须为正数)
     * @throws ProductNotFoundException   如果商品未找到
     * @throws IllegalArgumentException   如果增加数量不是正数
     */
    void increaseStock(Long productId, Integer quantity);

}