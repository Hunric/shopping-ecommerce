package com.ecommerce.product.service.impl;

import com.ecommerce.domain.product.Product;
import com.ecommerce.product.mapper.ProductMapper;
import com.ecommerce.product.service.ProductService;
import com.ecommerce.product.service.ProductSearchService;
import com.ecommerce.product.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private static final String CACHE_KEY_PRODUCT = "product:";
    private static final String CACHE_KEY_PRODUCTS = "products:list";
    private static final String CACHE_KEY_PRODUCT_SEARCH = "products:search";

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductSearchService productSearchService;

    @Autowired
    private CacheService cacheService;

    @Override
    @Transactional
    @CacheEvict(value = {"products"}, allEntries = true)
    public Product createProduct(Product product) {
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        productMapper.insert(product);
        // 同步到ES
        productSearchService.saveProduct(product);
        return product;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"products"}, allEntries = true)
    public Product updateProduct(Product product) {
        product.setUpdateTime(LocalDateTime.now());
        productMapper.update(product);
        // 同步到ES
        productSearchService.saveProduct(product);
        // 删除商品详情缓存
        cacheService.delete(CACHE_KEY_PRODUCT + product.getId());
        return product;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"products"}, allEntries = true)
    public void deleteProduct(Long id) {
        productMapper.delete(id);
        // 从ES中删除
        productSearchService.deleteProduct(id);
        // 删除商品详情缓存
        cacheService.delete(CACHE_KEY_PRODUCT + id);
    }

    @Override
    @Cacheable(value = "products", key = "'detail:' + #id")
    public Product getProductById(Long id) {
        return productMapper.findById(id);
    }

    @Override
    @Cacheable(value = "products", key = "'list:' + #pageNum + ':' + #pageSize")
    public List<Product> listProducts(Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return productMapper.findAll(offset, pageSize);
    }

    @Override
    @Cacheable(value = "products", key = "'search:' + #keyword + ':' + #categoryId + ':' + #pageNum + ':' + #pageSize")
    public List<Product> searchProducts(String keyword, Long categoryId, Integer pageNum, Integer pageSize) {
        // 使用ES搜索
        Page<Product> searchResult = productSearchService.search(keyword, categoryId, pageNum, pageSize);
        return searchResult.getContent();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"products"}, allEntries = true)
    public void updateStock(Long productId, Integer stock) {
        productMapper.updateStock(productId, stock);
        // 同步到ES
        Product product = getProductById(productId);
        if (product != null) {
            productSearchService.saveProduct(product);
        }
        // 删除商品详情缓存
        cacheService.delete(CACHE_KEY_PRODUCT + productId);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"products"}, allEntries = true)
    public void updateStatus(Long productId, Integer status) {
        productMapper.updateStatus(productId, status);
        // 同步到ES
        Product product = getProductById(productId);
        if (product != null) {
            productSearchService.saveProduct(product);
        }
        // 删除商品详情缓存
        cacheService.delete(CACHE_KEY_PRODUCT + productId);
    }
} 