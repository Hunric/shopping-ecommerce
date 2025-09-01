package com.hunric.controller;

import com.hunric.common.model.ApiResponse;
import com.hunric.model.dto.CartAddDTO;
import com.hunric.model.dto.CartItemResponseDTO;
import com.hunric.model.dto.CartUpdateDTO;
import com.hunric.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/api/cart")
@Slf4j
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    /**
     * 添加商品到购物车
     * 
     * @param userId 用户ID（从JWT中获取）
     * @param cartAddDTO 添加购物车DTO
     * @return 操作结果
     */
    @PostMapping("/add")
    public ApiResponse<String> addToCart(HttpServletRequest request,
                                        @RequestBody CartAddDTO cartAddDTO) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        
        log.info("接收到添加购物车请求: userId={}, skuId={}, quantity={}",
                userId, cartAddDTO.getSkuId(), cartAddDTO.getQuantity());
        
        return cartService.addToCart(userId, cartAddDTO);
    }
    
    /**
     * 获取购物车列表
     * 
     * @param userId 用户ID（从JWT中获取）
     * @return 购物车列表
     */
    @GetMapping("/list")
    public ApiResponse<List<CartItemResponseDTO>> getCartList(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        
        log.info("接收到获取购物车列表请求: userId={}", userId);
        
        return cartService.getCartList(userId);
    }
    
    /**
     * 更新购物车项
     * 
     * @param userId 用户ID（从JWT中获取）
     * @param cartUpdateDTO 更新购物车DTO
     * @return 操作结果
     */
    @PutMapping("/update")
    public ApiResponse<String> updateCartItem(HttpServletRequest request,
                                             @RequestBody CartUpdateDTO cartUpdateDTO) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        
        log.info("接收到更新购物车请求: userId={}, cartId={}", userId, cartUpdateDTO.getCartId());
        
        return cartService.updateCartItem(userId, cartUpdateDTO);
    }
    
    /**
     * 删除购物车项
     * 
     * @param userId 用户ID（从JWT中获取）
     * @param cartId 购物车ID
     * @return 操作结果
     */
    @DeleteMapping("/remove/{cartId}")
    public ApiResponse<String> removeCartItem(HttpServletRequest request,
                                             @PathVariable Integer cartId) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        
        log.info("接收到删除购物车项请求: userId={}, cartId={}", userId, cartId);
        
        return cartService.removeCartItem(userId, cartId);
    }
    
    /**
     * 清空购物车
     * 
     * @param userId 用户ID（从JWT中获取）
     * @return 操作结果
     */
    @DeleteMapping("/clear")
    public ApiResponse<String> clearCart(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        
        log.info("接收到清空购物车请求: userId={}", userId);
        
        return cartService.clearCart(userId);
    }
    
    /**
     * 获取购物车商品数量
     * 
     * @param userId 用户ID（从JWT中获取）
     * @return 商品数量
     */
    @GetMapping("/count")
    public ApiResponse<Integer> getCartCount(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        
        log.info("接收到获取购物车数量请求: userId={}", userId);
        
        return cartService.getCartCount(userId);
    }
    
    /**
     * 批量更新购物车项选中状态
     * 
     * @param userId 用户ID（从JWT中获取）
     * @param cartIds 购物车ID列表
     * @param isSelected 是否选中
     * @return 操作结果
     */
    @PutMapping("/select")
    public ApiResponse<String> updateSelectedStatus(HttpServletRequest request,
                                                   @RequestParam List<Integer> cartIds,
                                                   @RequestParam Boolean isSelected) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        
        log.info("接收到批量更新选中状态请求: userId={}, cartIds={}, isSelected={}",
                userId, cartIds, isSelected);
        
        return cartService.updateSelectedStatus(userId, cartIds, isSelected);
    }
    
    /**
     * 获取选中的购物车项
     * 
     * @param userId 用户ID（从JWT中获取）
     * @return 选中的购物车列表
     */
    @GetMapping("/selected")
    public ApiResponse<List<CartItemResponseDTO>> getSelectedCartItems(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error("用户未登录");
        }
        
        log.info("接收到获取选中购物车项请求: userId={}", userId);
        
        return cartService.getSelectedCartItems(userId);
    }
}