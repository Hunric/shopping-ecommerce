package com.ecommerce.cart.service; // 假设在 cart 服务的 service 包下

import com.ecommerce.cart.dto.AddToCartDTO;
import com.ecommerce.cart.dto.CartDTO;
import com.ecommerce.cart.dto.CartItemDTO; // 引入 CartItemDTO

import java.util.List;
import java.util.Set; // 引入 Set

/**
 * 购物车服务接口
 */
public interface CartService {

    /**
     * 添加商品到购物车
     * 如果商品已存在，则增加数量
     *
     * @param userId 用户ID
     * @param dto    包含商品ID和数量的DTO
     */
    void addToCart(Long userId, AddToCartDTO dto);

    /**
     * 获取用户的购物车详情
     *
     * @param userId 用户ID
     * @return 购物车 DTO，包含商品项列表及汇总信息
     */
    CartDTO getCart(Long userId);

    /**
     * 更新购物车中商品的数量
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param quantity  新的数量 (必须大于0)
     */
    void updateCartItemQuantity(Long userId, Long productId, Integer quantity);

    /**
     * 从购物车中移除单个商品
     *
     * @param userId    用户ID
     * @param productId 要移除的商品ID
     */
    void removeCartItem(Long userId, Long productId);

    /**
     * 从购物车中移除多个商品 (例如：下单后)
     *
     * @param userId     用户ID
     * @param productIds 要移除的商品ID集合
     */
    void removeCartItems(Long userId, Set<Long> productIds); // 修改为 Set<Long>

    /**
     * 清空用户的购物车
     *
     * @param userId 用户ID
     */
    void clearCart(Long userId);

    /**
     * 获取购物车中的原始商品项列表 (仅含 productId 和 quantity)
     * 主要供订单服务等内部调用
     *
     * @param userId 用户ID
     * @return 购物车项列表
     */
    List<CartItemDTO> getRawCartItems(Long userId); // 新增方法

}
