package com.ecommerce.cart.service.impl; // 假设在 cart 服务的 service.impl 包下

import com.ecommerce.cart.dto.AddToCartDTO;
import com.ecommerce.cart.dto.CartDTO;
import com.ecommerce.cart.dto.CartItemDTO;
import com.ecommerce.cart.service.CartService;
// import com.ecommerce.product.client.ProductServiceClient; // 假设未来使用 Feign 调用 Product 服务
// import com.ecommerce.domain.dto.ProductDTO; // 假设 Product 服务提供 ProductDTO
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils; // 引入 CollectionUtils

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, Integer> hashOperations; // String: Key, String: Field (productId), Integer: Value (quantity)

    // TODO: 注入 ProductServiceClient (Feign 客户端或其他RPC客户端) 用于获取商品信息
    // @Autowired
    // private ProductServiceClient productServiceClient;

    private static final String CART_KEY_PREFIX = "cart:";
    private static final long CART_EXPIRATION_DAYS = 7; // 购物车过期时间（天）

    @Autowired
    public CartServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        // 注意：这里假设 Redis 配置了合适的序列化器，使得 Value 可以存 Integer
        // 如果没有特殊配置，可能需要指定 HashOperations<String, String, String> 并手动转换
        this.hashOperations = redisTemplate.opsForHash();
    }

    /**
     * 获取指定用户的购物车 Redis Key
     */
    private String getCartKey(Long userId) {
        return CART_KEY_PREFIX + userId;
    }

    @Override
    public void addToCart(Long userId, AddToCartDTO dto) {
        String cartKey = getCartKey(userId);
        String productIdStr = String.valueOf(dto.getProductId());
        Integer quantityToAdd = dto.getQuantity();

        // TODO: 校验 productId 是否有效，以及库存是否充足 (调用 ProductService)
        // ProductDTO product = productServiceClient.getProductById(dto.getProductId());
        // if (product == null || product.getStatus() != 1) { // 假设 1 为上架状态
        //     throw new RuntimeException("商品不存在或已下架");
        // }

        // 使用 HINCRBY 原子地增加数量
        Long currentQuantity = hashOperations.increment(cartKey, productIdStr, quantityToAdd);
        log.info("Added to cart: userId={}, productId={}, quantityAdded={}, newTotalQuantity={}",
                userId, dto.getProductId(), quantityToAdd, currentQuantity);

        // 设置或刷新购物车过期时间
        redisTemplate.expire(cartKey, CART_EXPIRATION_DAYS, TimeUnit.DAYS);
    }

    @Override
    public CartDTO getCart(Long userId) {
        String cartKey = getCartKey(userId);
        Map<String, Integer> cartItemsMap = hashOperations.entries(cartKey);

        if (cartItemsMap == null || cartItemsMap.isEmpty()) {
            return new CartDTO(Collections.emptyList(), 0, BigDecimal.ZERO, 0);
        }

        List<CartItemDTO> cartItemDTOs = new ArrayList<>();
        int totalQuantity = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;

        // 提取所有 Product ID，用于批量查询商品信息
        Set<Long> productIds = cartItemsMap.keySet().stream()
                .map(Long::valueOf)
                .collect(Collectors.toSet());

        // TODO: 批量调用 ProductService 获取商品信息 Map<Long, ProductDTO>
        // Map<Long, ProductDTO> productInfoMap = productServiceClient.getProductsByIds(productIds);
        Map<Long, Object> productInfoMap = new HashMap<>(); // 临时占位符

        for (Map.Entry<String, Integer> entry : cartItemsMap.entrySet()) {
            Long productId = Long.valueOf(entry.getKey());
            Integer quantity = entry.getValue();

            if (quantity <= 0) { // 理论上 HINCRBY 不会产生非正数，但以防万一
                hashOperations.delete(cartKey, entry.getKey()); // 清理无效数据
                continue;
            }

            CartItemDTO itemDTO = new CartItemDTO(productId, quantity);

            // TODO: 从 productInfoMap 中获取并填充商品信息
            // ProductDTO product = productInfoMap.get(productId);
            Object product = productInfoMap.get(productId); // 临时占位符
            if (product != null) {
                // 假设 ProductDTO 有 getName(), getPrice(), getImageUrl(), getStockQuantity(), getStatus() 方法
                // itemDTO.setName(product.getName());
                // itemDTO.setPrice(product.getPrice());
                // itemDTO.setImageUrl(product.getImageUrl());
                // itemDTO.setStockQuantity(product.getStockQuantity());
                // itemDTO.setStatus(product.getStatus());

                // 计算单项总价 (假设 price 不为 null)
                // if (product.getPrice() != null) {
                //    itemDTO.setItemTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
                //    totalPrice = totalPrice.add(itemDTO.getItemTotalPrice());
                // } else {
                //    itemDTO.setItemTotalPrice(BigDecimal.ZERO);
                // }
                itemDTO.setName("商品 " + productId); // 临时名称
                itemDTO.setPrice(BigDecimal.TEN.multiply(BigDecimal.valueOf(productId % 10 + 1))); // 临时价格
                itemDTO.setImageUrl("https://placehold.co/60x60/eee/ccc?text=IMG"); // 临时图片
                itemDTO.setStockQuantity(100); // 临时库存
                itemDTO.setStatus(1); // 临时状态
                itemDTO.setItemTotalPrice(itemDTO.getPrice().multiply(BigDecimal.valueOf(quantity)));
                totalPrice = totalPrice.add(itemDTO.getItemTotalPrice());

            } else {
                // 商品信息获取失败或商品已失效的处理
                log.warn("Product info not found or invalid for productId: {} in cart for userId: {}", productId, userId);
                // 可以选择从购物车移除，或者在 DTO 中标记为无效
                itemDTO.setName("商品已失效");
                itemDTO.setPrice(BigDecimal.ZERO);
                itemDTO.setItemTotalPrice(BigDecimal.ZERO);
                itemDTO.setStatus(0); // 标记为无效状态
                // hashOperations.delete(cartKey, entry.getKey()); // 或者直接删除
            }

            cartItemDTOs.add(itemDTO);
            totalQuantity += quantity;
        }

        // 重新设置过期时间（可选，每次访问都刷新）
        redisTemplate.expire(cartKey, CART_EXPIRATION_DAYS, TimeUnit.DAYS);

        return new CartDTO(cartItemDTOs, totalQuantity, totalPrice, cartItemDTOs.size());
    }

    @Override
    public void updateCartItemQuantity(Long userId, Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            // 如果数量小于等于0，则视为删除
            removeCartItem(userId, productId);
            return;
        }

        String cartKey = getCartKey(userId);
        String productIdStr = String.valueOf(productId);

        // TODO: 校验 productId 是否有效，以及新数量是否超过库存 (调用 ProductService)
        // ProductDTO product = productServiceClient.getProductById(productId);
        // if (product == null || product.getStatus() != 1) {
        //     throw new RuntimeException("商品不存在或已下架");
        // }
        // if (product.getStockQuantity() < quantity) {
        //     throw new RuntimeException("商品库存不足");
        // }

        // 直接设置新的数量
        hashOperations.put(cartKey, productIdStr, quantity);
        log.info("Updated cart item quantity: userId={}, productId={}, newQuantity={}", userId, productId, quantity);

        // 刷新过期时间
        redisTemplate.expire(cartKey, CART_EXPIRATION_DAYS, TimeUnit.DAYS);
    }

    @Override
    public void removeCartItem(Long userId, Long productId) {
        String cartKey = getCartKey(userId);
        String productIdStr = String.valueOf(productId);

        Long deletedCount = hashOperations.delete(cartKey, productIdStr);
        if (deletedCount != null && deletedCount > 0) {
            log.info("Removed item from cart: userId={}, productId={}", userId, productId);
        } else {
            log.warn("Item not found in cart to remove: userId={}, productId={}", userId, productId);
        }
    }

    @Override
    public void removeCartItems(Long userId, Set<Long> productIds) {
        if (CollectionUtils.isEmpty(productIds)) {
            return;
        }
        String cartKey = getCartKey(userId);
        // 将 Long 集合转换为 String 数组，因为 delete 方法接受 Object... varargs
        String[] productIdStrs = productIds.stream()
                .map(String::valueOf)
                .toArray(String[]::new);

        Long deletedCount = hashOperations.delete(cartKey, (Object[])productIdStrs); // 强制转换为 Object[]
        log.info("Attempted to remove items from cart: userId={}, productIds={}, deletedCount={}", userId, productIds, deletedCount);
    }

    @Override
    public void clearCart(Long userId) {
        String cartKey = getCartKey(userId);
        Boolean deleted = redisTemplate.delete(cartKey);
        if (Boolean.TRUE.equals(deleted)) {
            log.info("Cleared cart for userId: {}", userId);
        } else {
            log.info("Cart already empty or key does not exist for userId: {}", userId);
        }
    }

    @Override
    public List<CartItemDTO> getRawCartItems(Long userId) {
        String cartKey = getCartKey(userId);
        Map<String, Integer> cartItemsMap = hashOperations.entries(cartKey);

        if (cartItemsMap == null || cartItemsMap.isEmpty()) {
            return Collections.emptyList();
        }

        return cartItemsMap.entrySet().stream()
                .map(entry -> new CartItemDTO(Long.valueOf(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());
    }
}
