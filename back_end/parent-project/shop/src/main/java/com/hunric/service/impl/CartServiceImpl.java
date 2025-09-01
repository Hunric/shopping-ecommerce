package com.hunric.service.impl;

import com.hunric.common.model.ApiResponse;
import com.hunric.model.CartInfo;
import com.hunric.model.SkuInfo;
import com.hunric.model.SpuInfo;
import com.hunric.model.dto.CartAddDTO;
import com.hunric.model.dto.CartItemResponseDTO;
import com.hunric.model.dto.CartUpdateDTO;
import com.hunric.repository.CartInfoRepository;
import com.hunric.repository.SkuInfoRepository;
import com.hunric.repository.SpuInfoRepository;
import com.hunric.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 购物车服务实现类
 */
@Service
@Slf4j
public class CartServiceImpl implements CartService {
    
    @Autowired
    private CartInfoRepository cartInfoRepository;
    
    @Autowired
    private SkuInfoRepository skuInfoRepository;
    
    @Autowired
    private SpuInfoRepository spuInfoRepository;
    
    @Override
    @Transactional
    public ApiResponse<String> addToCart(Integer userId, CartAddDTO cartAddDTO) {
        try {
            log.info("用户 {} 添加商品到购物车: skuId={}, quantity={}", userId, cartAddDTO.getSkuId(), cartAddDTO.getQuantity());
            
            // 参数验证
            if (cartAddDTO.getSkuId() == null || cartAddDTO.getQuantity() == null || cartAddDTO.getQuantity() <= 0) {
                return ApiResponse.error("参数错误");
            }
            
            // 检查SKU是否存在且可用
            Optional<SkuInfo> skuOptional = skuInfoRepository.findAvailableSkuWithStock(cartAddDTO.getSkuId(), cartAddDTO.getQuantity());
            if (skuOptional.isEmpty()) {
                return ApiResponse.error("商品不存在或库存不足");
            }
            
            SkuInfo skuInfo = skuOptional.get();
            
            // 检查购物车中是否已存在该SKU
            Optional<CartInfo> existingCartOptional = cartInfoRepository.findByUserIdAndSkuId(userId, cartAddDTO.getSkuId());
            
            if (existingCartOptional.isPresent()) {
                // 更新数量
                CartInfo existingCart = existingCartOptional.get();
                int newQuantity = existingCart.getQuantity() + cartAddDTO.getQuantity();
                
                // 检查库存是否足够
                if (newQuantity > skuInfo.getStockQuantity()) {
                    return ApiResponse.error("库存不足，当前库存：" + skuInfo.getStockQuantity());
                }
                
                existingCart.setQuantity(newQuantity);
                cartInfoRepository.save(existingCart);
                log.info("更新购物车项成功: cartId={}, newQuantity={}", existingCart.getCartId(), newQuantity);
            } else {
                // 新增购物车项
                CartInfo cartInfo = CartInfo.builder()
                        .userId(userId)
                        .skuId(cartAddDTO.getSkuId())
                        .quantity(cartAddDTO.getQuantity())
                        .isSelected(true)
                        .build();
                
                cartInfoRepository.save(cartInfo);
                log.info("添加购物车项成功: cartId={}", cartInfo.getCartId());
            }
            
            return ApiResponse.success("添加成功");
        } catch (Exception e) {
            log.error("添加购物车失败: userId={}, skuId={}", userId, cartAddDTO.getSkuId(), e);
            return ApiResponse.error("添加失败");
        }
    }
    
    @Override
    public ApiResponse<List<CartItemResponseDTO>> getCartList(Integer userId) {
        try {
            log.info("获取用户购物车列表: userId={}", userId);
            
            List<CartInfo> cartList = cartInfoRepository.findByUserIdOrderByCreateTimeDesc(userId);
            
            List<CartItemResponseDTO> responseList = cartList.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());
            
            return ApiResponse.success(responseList);
        } catch (Exception e) {
            log.error("获取购物车列表失败: userId={}", userId, e);
            return ApiResponse.error("获取失败");
        }
    }
    
    @Override
    @Transactional
    public ApiResponse<String> updateCartItem(Integer userId, CartUpdateDTO cartUpdateDTO) {
        try {
            log.info("更新购物车项: userId={}, cartId={}", userId, cartUpdateDTO.getCartId());
            
            Optional<CartInfo> cartOptional = cartInfoRepository.findById(cartUpdateDTO.getCartId());
            if (cartOptional.isEmpty()) {
                return ApiResponse.error("购物车项不存在");
            }
            
            CartInfo cartInfo = cartOptional.get();
            if (!cartInfo.getUserId().equals(userId)) {
                return ApiResponse.error("无权限操作");
            }
            
            // 更新数量
            if (cartUpdateDTO.getQuantity() != null) {
                if (cartUpdateDTO.getQuantity() <= 0) {
                    return ApiResponse.error("数量必须大于0");
                }
                
                // 检查库存
                Optional<SkuInfo> skuOptional = skuInfoRepository.findAvailableSkuWithStock(cartInfo.getSkuId(), cartUpdateDTO.getQuantity());
                if (skuOptional.isEmpty()) {
                    return ApiResponse.error("库存不足");
                }
                
                cartInfo.setQuantity(cartUpdateDTO.getQuantity());
            }
            
            // 更新选中状态
            if (cartUpdateDTO.getIsSelected() != null) {
                cartInfo.setIsSelected(cartUpdateDTO.getIsSelected());
            }
            
            cartInfoRepository.save(cartInfo);
            
            return ApiResponse.success("更新成功");
        } catch (Exception e) {
            log.error("更新购物车项失败: userId={}, cartId={}", userId, cartUpdateDTO.getCartId(), e);
            return ApiResponse.error("更新失败");
        }
    }
    
    @Override
    @Transactional
    public ApiResponse<String> removeCartItem(Integer userId, Integer cartId) {
        try {
            log.info("删除购物车项: userId={}, cartId={}", userId, cartId);
            
            Optional<CartInfo> cartOptional = cartInfoRepository.findById(cartId);
            if (cartOptional.isEmpty()) {
                return ApiResponse.error("购物车项不存在");
            }
            
            CartInfo cartInfo = cartOptional.get();
            if (!cartInfo.getUserId().equals(userId)) {
                return ApiResponse.error("无权限操作");
            }
            
            cartInfoRepository.delete(cartInfo);
            
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            log.error("删除购物车项失败: userId={}, cartId={}", userId, cartId, e);
            return ApiResponse.error("删除失败");
        }
    }
    
    @Override
    @Transactional
    public ApiResponse<String> clearCart(Integer userId) {
        try {
            log.info("清空购物车: userId={}", userId);
            
            int deletedCount = cartInfoRepository.deleteByUserId(userId);
            log.info("清空购物车成功: userId={}, deletedCount={}", userId, deletedCount);
            
            return ApiResponse.success("清空成功");
        } catch (Exception e) {
            log.error("清空购物车失败: userId={}", userId, e);
            return ApiResponse.error("清空失败");
        }
    }
    
    @Override
    public ApiResponse<Integer> getCartCount(Integer userId) {
        try {
            Integer count = cartInfoRepository.countTotalQuantityByUserId(userId);
            return ApiResponse.success(count != null ? count : 0);
        } catch (Exception e) {
            log.error("获取购物车数量失败: userId={}", userId, e);
            return ApiResponse.error("获取失败");
        }
    }
    
    @Override
    @Transactional
    public ApiResponse<String> updateSelectedStatus(Integer userId, List<Integer> cartIds, Boolean isSelected) {
        try {
            log.info("批量更新购物车选中状态: userId={}, cartIds={}, isSelected={}", userId, cartIds, isSelected);
            
            int updatedCount = cartInfoRepository.updateSelectedStatus(userId, cartIds, isSelected);
            log.info("批量更新购物车选中状态成功: updatedCount={}", updatedCount);
            
            return ApiResponse.success("更新成功");
        } catch (Exception e) {
            log.error("批量更新购物车选中状态失败: userId={}", userId, e);
            return ApiResponse.error("更新失败");
        }
    }
    
    @Override
    public ApiResponse<List<CartItemResponseDTO>> getSelectedCartItems(Integer userId) {
        try {
            log.info("获取选中的购物车项: userId={}", userId);
            
            List<CartInfo> cartList = cartInfoRepository.findByUserIdAndIsSelectedTrueOrderByCreateTimeDesc(userId);
            
            List<CartItemResponseDTO> responseList = cartList.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());
            
            return ApiResponse.success(responseList);
        } catch (Exception e) {
            log.error("获取选中购物车项失败: userId={}", userId, e);
            return ApiResponse.error("获取失败");
        }
    }
    
    /**
     * 转换为响应DTO
     */
    private CartItemResponseDTO convertToResponseDTO(CartInfo cartInfo) {
        CartItemResponseDTO.CartItemResponseDTOBuilder builder = CartItemResponseDTO.builder()
                .cartId(cartInfo.getCartId())
                .skuId(cartInfo.getSkuId())
                .quantity(cartInfo.getQuantity())
                .isSelected(cartInfo.getIsSelected())
                .createTime(cartInfo.getCreateTime())
                .updateTime(cartInfo.getUpdateTime());
        
        // 获取SKU信息
        Optional<SkuInfo> skuOptional = skuInfoRepository.findById(cartInfo.getSkuId());
        if (skuOptional.isPresent()) {
            SkuInfo skuInfo = skuOptional.get();
            builder.skuPrice(skuInfo.getSalePrice())
                   .subtotal(skuInfo.getSalePrice().multiply(BigDecimal.valueOf(cartInfo.getQuantity())))
                   .skuAttr(skuInfo.getAttributeCombination());
            
            // 获取SPU信息
            Optional<SpuInfo> spuOptional = spuInfoRepository.findById(skuInfo.getProductId());
            if (spuOptional.isPresent()) {
                SpuInfo spuInfo = spuOptional.get();
                builder.productName(spuInfo.getSpuName());
                
                // 优先使用SKU专属图片，否则使用SPU图片
                String imageUrl = skuInfo.getExclusiveImageUrl();
                if (imageUrl == null || imageUrl.trim().isEmpty()) {
                    imageUrl = spuInfo.getProductImage();
                }
                builder.productImage(imageUrl);
            }
        }
        
        return builder.build();
    }
}