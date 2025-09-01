package com.hunric.service;

import com.hunric.common.model.ApiResponse;
import com.hunric.model.dto.CartAddDTO;
import com.hunric.model.dto.CartItemResponseDTO;
import com.hunric.model.dto.CartUpdateDTO;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {
    
    /**
     * 添加商品到购物车
     * 
     * @param userId 用户ID
     * @param cartAddDTO 添加购物车DTO
     * @return 操作结果
     */
    ApiResponse<String> addToCart(Integer userId, CartAddDTO cartAddDTO);
    
    /**
     * 获取用户购物车列表
     * 
     * @param userId 用户ID
     * @return 购物车列表
     */
    ApiResponse<List<CartItemResponseDTO>> getCartList(Integer userId);
    
    /**
     * 更新购物车项
     * 
     * @param userId 用户ID
     * @param cartUpdateDTO 更新购物车DTO
     * @return 操作结果
     */
    ApiResponse<String> updateCartItem(Integer userId, CartUpdateDTO cartUpdateDTO);
    
    /**
     * 删除购物车项
     * 
     * @param userId 用户ID
     * @param cartId 购物车ID
     * @return 操作结果
     */
    ApiResponse<String> removeCartItem(Integer userId, Integer cartId);
    
    /**
     * 清空购物车
     * 
     * @param userId 用户ID
     * @return 操作结果
     */
    ApiResponse<String> clearCart(Integer userId);
    
    /**
     * 获取购物车商品数量
     * 
     * @param userId 用户ID
     * @return 商品数量
     */
    ApiResponse<Integer> getCartCount(Integer userId);
    
    /**
     * 批量更新购物车项选中状态
     * 
     * @param userId 用户ID
     * @param cartIds 购物车ID列表
     * @param isSelected 是否选中
     * @return 操作结果
     */
    ApiResponse<String> updateSelectedStatus(Integer userId, List<Integer> cartIds, Boolean isSelected);
    
    /**
     * 获取选中的购物车项
     * 
     * @param userId 用户ID
     * @return 选中的购物车列表
     */
    ApiResponse<List<CartItemResponseDTO>> getSelectedCartItems(Integer userId);
}