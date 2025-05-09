package com.ecommerce.cart.controller; // 假设在 cart 服务的 controller 包下

import com.ecommerce.cart.dto.AddToCartDTO;
import com.ecommerce.cart.dto.CartDTO;
import com.ecommerce.cart.dto.UpdateCartItemQuantityDTO; // 引入 DTO
import com.ecommerce.cart.service.CartService;
import com.ecommerce.common.ApiResponse; // 引入公共响应类
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody; // 引入 RequestBody 注解
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
// import jakarta.validation.constraints.Min; // 不再需要直接校验参数
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*; // 确保导入正确的注解

import java.util.Set; // 引入 Set

/**
 * 购物车 API 控制器
 */
@RestController
@RequestMapping("/api/v1/cart")
@Tag(name = "Cart Management", description = "购物车管理接口")
@Validated // 开启方法参数校验
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // --- 模拟获取用户ID ---
    // TODO: 替换为从 Spring Security 上下文获取真实用户ID的逻辑
    private Long getCurrentUserId() {
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // if (authentication != null && authentication.isAuthenticated()) {
        //     Object principal = authentication.getPrincipal();
        //     if (principal instanceof CustomUserDetails) { // 假设有 CustomUserDetails
        //         return ((CustomUserDetails) principal).getId();
        //     }
        //     // 或者从 JWT claim 中解析 userId
        // }
        // throw new RuntimeException("用户未登录"); // 或者返回 401
        return 1L; // 临时返回用户ID 1
    }
    // --- End of 模拟获取用户ID ---

    @PostMapping("/items")
    @Operation(summary = "添加商品到购物车", description = "将指定商品添加到当前用户的购物车")
    public ResponseEntity<ApiResponse<Void>> addToCart(@Valid @RequestBody AddToCartDTO addToCartDTO) {
        Long userId = getCurrentUserId();
        cartService.addToCart(userId, addToCartDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null, "商品已添加到购物车"));
    }

    @GetMapping
    @Operation(summary = "获取购物车详情", description = "获取当前用户的购物车内容")
    public ResponseEntity<ApiResponse<CartDTO>> getCart() {
        Long userId = getCurrentUserId();
        CartDTO cart = cartService.getCart(userId);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    /**
     * 更新购物车商品数量
     * @param productId 要更新的商品ID (路径参数)
     * @param updateDto 包含新数量的请求体 DTO
     * @return 操作结果
     */
    @PutMapping("/items/{productId}")
    @Operation(summary = "更新购物车商品数量", description = "更新购物车中指定商品的数量")
    public ResponseEntity<ApiResponse<Void>> updateCartItemQuantity(
            @Parameter(description = "要更新的商品ID") @PathVariable Long productId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody( // 使用 OpenAPI 的 RequestBody 注解描述请求体
                    description = "包含新数量的对象",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateCartItemQuantityDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody UpdateCartItemQuantityDTO updateDto) { // 使用 @RequestBody 接收 DTO
        Long userId = getCurrentUserId();
        // 从 DTO 中获取数量
        cartService.updateCartItemQuantity(userId, productId, updateDto.getQuantity());
        return ResponseEntity.ok(ApiResponse.success(null, "购物车商品数量已更新"));
    }

    @DeleteMapping("/items/{productId}")
    @Operation(summary = "移除购物车商品", description = "从购物车中移除指定的商品")
    public ResponseEntity<ApiResponse<Void>> removeCartItem(
            @Parameter(description = "要移除的商品ID") @PathVariable Long productId) {
        Long userId = getCurrentUserId();
        cartService.removeCartItem(userId, productId);
        return ResponseEntity.ok(ApiResponse.success(null, "商品已从购物车移除"));
    }

    @DeleteMapping("/items")
    @Operation(summary = "批量移除购物车商品", description = "从购物车中移除多个指定的商品 (例如下单后)")
    public ResponseEntity<ApiResponse<Void>> removeCartItems(
            @Parameter(description = "要移除的商品ID集合", required = true)
            @RequestParam Set<Long> productIds) { // 批量删除通常还是用 Query Param 或 Request Body 传 ID 列表
        Long userId = getCurrentUserId();
        cartService.removeCartItems(userId, productIds);
        return ResponseEntity.ok(ApiResponse.success(null, "指定商品已从购物车移除"));
    }


    @DeleteMapping
    @Operation(summary = "清空购物车", description = "清空当前用户的所有购物车商品")
    public ResponseEntity<ApiResponse<Void>> clearCart() {
        Long userId = getCurrentUserId();
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "购物车已清空"));
    }
}
