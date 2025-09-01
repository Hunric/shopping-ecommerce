package com.hunric.service.impl;

import com.hunric.model.ProductCategory;
import com.hunric.model.SpuInfo;
import com.hunric.repository.ProductCategoryRepository;
import com.hunric.repository.SpuInfoRepository;
import com.hunric.service.UserProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户端商品服务实现类
 * 使用真实数据库数据
 */
@Service
@Slf4j
public class UserProductServiceImpl implements UserProductService {

    @Autowired
    private SpuInfoRepository spuInfoRepository;
    
    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Override
    public List<ProductCategory> getAllCategories() {
        log.info("查询所有可见分类（去重）");
        
        // 获取去重的分类名称
        List<String> distinctCategoryNames = productCategoryRepository.findDistinctCategoryNames();
        List<ProductCategory> uniqueCategories = new ArrayList<>();
        
        // 为每个去重的分类名称获取一个代表性的分类对象
        for (String categoryName : distinctCategoryNames) {
            ProductCategory category = productCategoryRepository.findFirstByCategoryNameAndIsVisibleTrueOrderByCategoryId(categoryName);
            if (category != null) {
                uniqueCategories.add(category);
            }
        }
        
        log.info("返回 {} 个去重后的分类", uniqueCategories.size());
        return uniqueCategories;
    }

    @Override
    public List<SpuInfo> getRecommendedProducts(Integer limit) {
        log.info("查询推荐商品，限制数量: {}", limit);
        List<SpuInfo> products = spuInfoRepository.findByStatusOrderByCreateTimeDesc("on_shelf");
        
        // 填充扩展字段
        fillExtendedFields(products);
        
        return products.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpuInfo> getHotProducts(Integer limit) {
        log.info("查询热门商品，限制数量: {}", limit);
        List<SpuInfo> products = spuInfoRepository.findByStatusOrderByCreateTimeDesc("on_shelf");
        
        // 填充扩展字段
        fillExtendedFields(products);
        
        // 按价格倒序排列（模拟热门商品）
        return products.stream()
                .sorted(Comparator.comparing(SpuInfo::getDisplayPrice).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> searchProducts(String keyword, Integer page, Integer pageSize,
                                            Integer categoryId, Double minPrice, Double maxPrice,
                                            String brandName, String sortBy, String sortOrder) {
        log.info("搜索商品 - 关键词: {}, 页码: {}, 页大小: {}, 分类ID: {}, 价格区间: {}-{}, 品牌: {}, 排序: {}:{}", 
                keyword, page, pageSize, categoryId, minPrice, maxPrice, brandName, sortBy, sortOrder);
        
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        
        Page<SpuInfo> productPage = spuInfoRepository.findByComplexConditions(
                "on_shelf", categoryId, null, keyword, minPrice, maxPrice, 
                brandName, sortBy, sortOrder, pageable);
        
        List<SpuInfo> products = productPage.getContent();
        fillExtendedFields(products);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", products);
        result.put("total", productPage.getTotalElements());
        result.put("page", page);
        result.put("pageSize", pageSize);
        
        return result;
    }

    @Override
    public Map<String, Object> getProductsByCategory(Integer categoryId, Integer page, Integer pageSize,
                                                   String sortBy, String sortOrder) {
        log.info("根据分类查询商品 - 分类ID: {}, 页码: {}, 页大小: {}, 排序: {}:{}", 
                categoryId, page, pageSize, sortBy, sortOrder);
        
        return searchProducts(null, page, pageSize, categoryId, null, null, null, sortBy, sortOrder);
    }

    @Override
    public SpuInfo getProductDetail(Integer spuId) {
        log.info("查询商品详情 - 商品ID: {}", spuId);
        
        Optional<SpuInfo> productOpt = spuInfoRepository.findById(spuId);
        if (productOpt.isPresent()) {
            SpuInfo product = productOpt.get();
            if ("on_shelf".equals(product.getStatus())) {
                fillExtendedFields(Arrays.asList(product));
                return product;
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> getProducts(Integer page, Integer pageSize, Integer categoryId,
                                         Integer storeId, String keyword, Double minPrice, Double maxPrice,
                                         String brandName, String sortBy, String sortOrder) {
        log.info("查询商品列表 - 页码: {}, 页大小: {}, 分类ID: {}, 店铺ID: {}, 关键词: {}, 价格区间: {}-{}, 品牌: {}, 排序: {}:{}", 
                page, pageSize, categoryId, storeId, keyword, minPrice, maxPrice, brandName, sortBy, sortOrder);
        
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        
        Page<SpuInfo> productPage = spuInfoRepository.findByComplexConditions(
                "on_shelf", categoryId, storeId, keyword, minPrice, maxPrice, 
                brandName, sortBy, sortOrder, pageable);
        
        List<SpuInfo> products = productPage.getContent();
        fillExtendedFields(products);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", products);
        result.put("total", productPage.getTotalElements());
        result.put("page", page);
        result.put("pageSize", pageSize);
        
        return result;
    }
    
    /**
     * 填充扩展字段（店铺名称、分类名称）
     */
    private void fillExtendedFields(List<SpuInfo> products) {
        if (products == null || products.isEmpty()) {
            return;
        }
        
        // 获取所有分类信息
        Map<Integer, String> categoryMap = productCategoryRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        ProductCategory::getCategoryId,
                        ProductCategory::getCategoryName,
                        (existing, replacement) -> existing
                ));
        
        // 填充分类名称和店铺名称
        for (SpuInfo product : products) {
            if (product.getCategoryId() != null) {
                product.setCategoryName(categoryMap.get(product.getCategoryId()));
            }
            
            // 简单的店铺名称映射（实际项目中应该查询店铺表）
            if (product.getStoreId() != null) {
                product.setStoreName("店铺" + product.getStoreId());
            }
        }
    }
}