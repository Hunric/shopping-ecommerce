// --- com.ecommerce.service.impl.ProductServiceImpl.java (实现类) ---
package com.ecommerce.service.impl;

import com.ecommerce.common.dto.PageResult;
import com.ecommerce.domain.dto.ProductCreateDTO;
import com.ecommerce.domain.dto.ProductDTO;
import com.ecommerce.domain.dto.ProductQueryDTO;
import com.ecommerce.domain.dto.ProductUpdateDTO;
import com.ecommerce.domain.entity.Product;
import com.ecommerce.exception.CategoryNotFoundException;
import com.ecommerce.exception.InsufficientStockException; // 引入异常
import com.ecommerce.exception.ProductNotFoundException;
import com.ecommerce.mapper.CategoryMapper;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY_PREFIX_PRODUCT = "product:";

    @Autowired
    public ProductServiceImpl(ProductMapper productMapper,
                              CategoryMapper categoryMapper,
                              RedisTemplate<String, Object> redisTemplate) {
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    public ProductDTO createProduct(ProductCreateDTO createDTO) {
        log.info("Attempting to create product: {}", createDTO.getName());

        // 1. 校验 CategoryId 是否有效
        if (!categoryMapper.existsById(createDTO.getCategoryId())) {
            log.warn("Category not found for id: {}", createDTO.getCategoryId());
            throw new CategoryNotFoundException("指定的商品分类不存在: ID = " + createDTO.getCategoryId());
        }

        // 2. 将 DTO 转换为 Product 实体
        Product product = new Product();
        BeanUtils.copyProperties(createDTO, product);

        // 3. 设置创建和更新时间 (如果实体类没有使用 @PrePersist)
        LocalDateTime now = LocalDateTime.now();
        product.setCreatedAt(now);
        product.setUpdatedAt(now);

        // 4. 调用 Mapper 插入数据库
        productMapper.insert(product);
        log.info("Product created successfully with id: {}", product.getId());

        // 5. 将创建后的 Product 实体转换为 ProductDTO 返回
        return convertToDTO(product);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        log.debug("Fetching product by id: {}", id);
        String cacheKey = CACHE_KEY_PREFIX_PRODUCT + id;

        // 1. 尝试从 Redis 获取
        Product productFromCache = null;
        try {
            Object cachedObject = redisTemplate.opsForValue().get(cacheKey);
            if (cachedObject instanceof Product) {
                productFromCache = (Product) cachedObject;
            } else if (cachedObject != null) {
                log.warn("Unexpected object type found in Redis cache for key {}: {}", cacheKey, cachedObject.getClass().getName());
                redisTemplate.delete(cacheKey); // 删除无效缓存
            }
        } catch (Exception e) {
            log.error("Error retrieving product from Redis cache for key {}: {}", cacheKey, e.getMessage(), e);
            // 缓存异常，降级处理，继续查询数据库
        }

        if (productFromCache != null) {
            log.info("Cache hit for product id: {}", id);
            return convertToDTO(productFromCache);
        }

        log.info("Cache miss for product id: {}", id);
        // 2. 缓存未命中，查询数据库
        Product productFromDb = productMapper.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found in DB for id: {}", id);
                    // 可选：缓存空对象防止穿透 (需要确保 RedisTemplate 可以存储 null)
                    // try {
                    //     redisTemplate.opsForValue().set(cacheKey, null, 5, TimeUnit.MINUTES);
                    // } catch (Exception e) { log.error("Error caching null for product id {}: {}", id, e.getMessage()); }
                    return new ProductNotFoundException("商品未找到: ID = " + id);
                });

        // 3. 查到数据，写入 Redis
        try {
            long expiration = 600 + new Random().nextInt(1200); // 10-30 分钟随机过期
            redisTemplate.opsForValue().set(cacheKey, productFromDb, expiration, TimeUnit.SECONDS);
            log.info("Product id: {} cached successfully with expiration {} seconds", id, expiration);
        } catch (Exception e) {
            log.error("Error caching product id {}: {}", id, e.getMessage(), e);
        }

        return convertToDTO(productFromDb);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductUpdateDTO updateDTO) {
        log.info("Attempting to update product id: {}", id);
        Product product = productMapper.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("商品未找到: ID = " + id));

        // 校验 CategoryId (如果提供了更新)
        if (updateDTO.getCategoryId() != null && !updateDTO.getCategoryId().equals(product.getCategoryId())) {
            if (!categoryMapper.existsById(updateDTO.getCategoryId())) {
                log.warn("Category not found for id: {}", updateDTO.getCategoryId());
                throw new CategoryNotFoundException("指定的商品分类不存在: ID = " + updateDTO.getCategoryId());
            }
            product.setCategoryId(updateDTO.getCategoryId());
        }

        // 更新其他允许更新的字段
        boolean updated = false;
        if (updateDTO.getName() != null && !updateDTO.getName().equals(product.getName())) {
            product.setName(updateDTO.getName());
            updated = true;
        }
        if (updateDTO.getDescription() != null && !updateDTO.getDescription().equals(product.getDescription())) {
            product.setDescription(updateDTO.getDescription());
            updated = true;
        }
        if (updateDTO.getPrice() != null && updateDTO.getPrice().compareTo(product.getPrice()) != 0) {
            product.setPrice(updateDTO.getPrice());
            updated = true;
        }
        if (updateDTO.getStockQuantity() != null && !updateDTO.getStockQuantity().equals(product.getStockQuantity())) {
            product.setStockQuantity(updateDTO.getStockQuantity());
            updated = true;
        }
        if (updateDTO.getImageUrl() != null && !updateDTO.getImageUrl().equals(product.getImageUrl())) {
            product.setImageUrl(updateDTO.getImageUrl());
            updated = true;
        }
        if (updateDTO.getStatus() != null && !updateDTO.getStatus().equals(product.getStatus())) {
            product.setStatus(updateDTO.getStatus());
            updated = true;
        }
        // 如果 categoryId 更新了，也算更新
        if (updateDTO.getCategoryId() != null && !updateDTO.getCategoryId().equals(product.getCategoryId())) {
            product.setCategoryId(updateDTO.getCategoryId()); // 已在前面设置
            updated = true;
        }


        if (updated) {
            product.setUpdatedAt(LocalDateTime.now()); // 更新时间
            productMapper.update(product);

            // 更新数据库后，删除缓存
            deleteProductCache(id);
            log.info("Product updated successfully for id: {}", id);
        } else {
            log.info("No changes detected for product id: {}", id);
        }

        return convertToDTO(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Attempting to delete product id: {}", id);
        if (!productMapper.existsById(id)) {
            throw new ProductNotFoundException("商品未找到: ID = " + id);
        }

        // TODO: 检查是否有未完成订单关联此商品，若有则不允许删除或应执行逻辑删除

        // 物理删除示例
        int deletedRows = productMapper.deleteById(id);

        if (deletedRows > 0) {
            // 删除数据库后，删除缓存
            deleteProductCache(id);
            log.info("Product deleted successfully (physically) for id: {}", id);
        } else {
            log.error("Failed to delete product id {} from DB", id);
            throw new RuntimeException("删除商品失败");
        }
    }

    @Override
    @Transactional
    public void updateProductStatus(Long id, Integer status) {
        log.info("Attempting to update status for product id: {} to {}", id, status);
        if (status == null || (status != 0 && status != 1)) { // 假设 0 下架, 1 上架
            throw new IllegalArgumentException("无效的商品状态值");
        }
        Product product = productMapper.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("商品未找到: ID = " + id));

        if (!product.getStatus().equals(status)) {
            int updatedRows = productMapper.updateStatus(id, status); // 使用专门的 updateStatus 方法

            if (updatedRows > 0) {
                // 更新状态后，删除缓存
                deleteProductCache(id);
                log.info("Product status updated successfully for id: {}", id);
            } else {
                log.error("Failed to update status for product id {}", id);
                throw new RuntimeException("更新商品状态失败");
            }
        } else {
            log.info("Product id: {} already has status: {}", id, status);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<ProductDTO> listProducts(ProductQueryDTO queryDTO) {
        log.debug("Listing products with query: {}", queryDTO);

        // 准备 keyword 用于 LIKE 查询
        String originalKeyword = queryDTO.getKeyword();
        if (StringUtils.hasText(originalKeyword)) {
            queryDTO.setKeyword("%" + originalKeyword.trim() + "%");
        }

        long total = productMapper.countProducts(queryDTO);

        List<ProductDTO> productDTOList;
        if (total > 0) {
            List<Product> productList = productMapper.findProducts(queryDTO);
            productDTOList = productList.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } else {
            productDTOList = Collections.emptyList();
            log.debug("No products found for the given query.");
        }

        // 恢复原始 keyword (如果修改了 DTO)
        queryDTO.setKeyword(originalKeyword);

        return new PageResult<>(
                productDTOList,
                total,
                queryDTO.getPage(),
                queryDTO.getPageSize()
        );
    }

    @Override
    @Transactional
    public void decreaseStock(Long productId, Integer quantity) {
        log.info("Attempting to decrease stock for product id: {} by {}", productId, quantity);
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("扣减数量必须为正数");
        }
        int affectedRows = productMapper.decreaseStock(productId, quantity);
        if (affectedRows == 0) {
            Product product = productMapper.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("尝试扣减库存失败，商品未找到: ID = " + productId));
            log.warn("Insufficient stock for product id: {}, attempted to decrease by {}", productId, quantity);
            throw new InsufficientStockException("商品 " + productId + " 库存不足"); // 使用定义的异常
        }

        // 成功扣减库存后，清除缓存
        deleteProductCache(productId);
        log.info("Stock decreased successfully for product id: {} by {}", productId, quantity);
    }

    @Override
    @Transactional
    public void increaseStock(Long productId, Integer quantity) {
        log.info("Attempting to increase stock for product id: {} by {}", productId, quantity);
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("增加数量必须为正数");
        }
        int affectedRows = productMapper.increaseStock(productId, quantity);
        if (affectedRows == 0) {
            if (!productMapper.existsById(productId)) {
                throw new ProductNotFoundException("尝试增加库存失败，商品未找到: ID = " + productId);
            } else {
                log.error("Failed to increase stock for product id {}, unknown reason.", productId);
                throw new RuntimeException("增加库存失败");
            }
        }
        // 库存增加后，清除缓存
        deleteProductCache(productId);
        log.info("Stock increased successfully for product id: {} by {}", productId, quantity);
    }

    // --- Helper Methods ---
    private ProductDTO convertToDTO(Product product) {
        if (product == null) {
            return null;
        }
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(product, dto);
        // 可选：如果需要分类名称，可以在这里查询并设置
        // try {
        //     Category category = categoryMapper.findById(product.getCategoryId()).orElse(null);
        //     if (category != null) {
        //         dto.setCategoryName(category.getName());
        //     }
        // } catch (Exception e) { log.error("Error fetching category name for product {}", product.getId(), e); }
        return dto;
    }

    private void deleteProductCache(Long productId) {
        try {
            String cacheKey = CACHE_KEY_PREFIX_PRODUCT + productId;
            Boolean deleted = redisTemplate.delete(cacheKey);
            if (Boolean.TRUE.equals(deleted)) {
                log.info("Cache deleted for product id: {}", productId);
            }
        } catch (Exception e) {
            log.error("Error deleting cache for product id {}: {}", productId, e.getMessage(), e);
        }
    }
}