package com.hunric.service;

import com.hunric.model.ProductCategory;
import com.hunric.model.SpuInfo;

import java.util.List;
import java.util.Map;

/**
 * 用户端商品服务接口
 */
public interface UserProductService {
    
    /**
     * 获取所有商品分类
     */
    List<ProductCategory> getAllCategories();
    
    /**
     * 获取推荐商品
     */
    List<SpuInfo> getRecommendedProducts(Integer limit);
    
    /**
     * 获取热门商品
     */
    List<SpuInfo> getHotProducts(Integer limit);
    
    /**
     * 搜索商品
     */
    Map<String, Object> searchProducts(String keyword, Integer page, Integer pageSize,
                                     Integer categoryId, Double minPrice, Double maxPrice,
                                     String brandName, String sortBy, String sortOrder);
    
    /**
     * 根据分类获取商品
     */
    Map<String, Object> getProductsByCategory(Integer categoryId, Integer page, Integer pageSize,
                                            String sortBy, String sortOrder);
    
    /**
     * 获取商品详情
     */
    SpuInfo getProductDetail(Integer spuId);
    
    /**
     * 获取商品列表
     */
    Map<String, Object> getProducts(Integer page, Integer pageSize, Integer categoryId,
                                  Integer storeId, String keyword, Double minPrice, Double maxPrice,
                                  String brandName, String sortBy, String sortOrder);
}