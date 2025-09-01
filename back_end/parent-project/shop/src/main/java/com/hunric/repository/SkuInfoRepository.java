package com.hunric.repository;

import com.hunric.model.SkuInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * SKU信息Repository
 */
@Repository
public interface SkuInfoRepository extends JpaRepository<SkuInfo, Integer> {
    
    /**
     * 根据商品ID查询SKU列表
     * 
     * @param productId 商品ID
     * @return SKU列表
     */
    List<SkuInfo> findByProductId(Integer productId);
    
    /**
     * 根据SKU ID查询SKU信息
     * 
     * @param skuId SKU ID
     * @return SKU信息
     */
    Optional<SkuInfo> findBySkuId(Integer skuId);
    
    /**
     * 根据商品ID和状态查询SKU列表
     * 
     * @param productId 商品ID
     * @param status 状态
     * @return SKU列表
     */
    List<SkuInfo> findByProductIdAndStatus(Integer productId, Integer status);
    
    /**
     * 查询上架状态的SKU
     * 
     * @param skuId SKU ID
     * @return SKU信息
     */
    @Query("SELECT s FROM SkuInfo s WHERE s.skuId = :skuId AND s.status = 1")
    Optional<SkuInfo> findAvailableSkuById(@Param("skuId") Integer skuId);
    
    /**
     * 查询库存充足的SKU
     * 
     * @param skuId SKU ID
     * @param quantity 需要的数量
     * @return SKU信息
     */
    @Query("SELECT s FROM SkuInfo s WHERE s.skuId = :skuId AND s.status = 1 AND s.stockQuantity >= :quantity")
    Optional<SkuInfo> findAvailableSkuWithStock(@Param("skuId") Integer skuId, @Param("quantity") Integer quantity);
}