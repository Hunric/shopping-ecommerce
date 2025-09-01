package com.hunric.repository;

import com.hunric.model.SpuInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SPU信息数据访问层
 */
@Repository
public interface SpuInfoRepository extends JpaRepository<SpuInfo, Integer> {
    
    /**
     * 根据状态查询商品列表
     */
    List<SpuInfo> findByStatusOrderByCreateTimeDesc(String status);
    
    /**
     * 根据状态查询商品列表（分页）
     */
    Page<SpuInfo> findByStatusOrderByCreateTimeDesc(String status, Pageable pageable);
    
    /**
     * 根据分类ID和状态查询商品
     */
    List<SpuInfo> findByCategoryIdAndStatus(Integer categoryId, String status);
    
    /**
     * 根据店铺ID和状态查询商品
     */
    List<SpuInfo> findByStoreIdAndStatus(Integer storeId, String status);
    
    /**
     * 根据商品名称模糊查询
     */
    @Query("SELECT s FROM SpuInfo s WHERE s.status = :status AND " +
           "(s.spuName LIKE %:keyword% OR s.spuDescription LIKE %:keyword%) " +
           "ORDER BY s.createTime DESC")
    List<SpuInfo> findByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") String status);
    
    /**
     * 根据价格区间查询商品
     */
    @Query("SELECT s FROM SpuInfo s WHERE s.status = :status AND " +
           "s.displayPrice >= :minPrice AND s.displayPrice <= :maxPrice " +
           "ORDER BY s.createTime DESC")
    List<SpuInfo> findByPriceRangeAndStatus(@Param("minPrice") Double minPrice, 
                                           @Param("maxPrice") Double maxPrice, 
                                           @Param("status") String status);
    
    /**
     * 根据品牌名称查询商品
     */
    List<SpuInfo> findByBrandNameAndStatusOrderByCreateTimeDesc(String brandName, String status);
    
    /**
     * 复合条件查询商品
     */
    @Query("SELECT s FROM SpuInfo s WHERE s.status = :status " +
           "AND (:categoryId IS NULL OR s.categoryId = :categoryId) " +
           "AND (:storeId IS NULL OR s.storeId = :storeId) " +
           "AND (:keyword IS NULL OR s.spuName LIKE %:keyword% OR s.spuDescription LIKE %:keyword%) " +
           "AND (:minPrice IS NULL OR s.displayPrice >= :minPrice) " +
           "AND (:maxPrice IS NULL OR s.displayPrice <= :maxPrice) " +
           "AND (:brandName IS NULL OR s.brandName LIKE %:brandName%) " +
           "ORDER BY " +
           "CASE WHEN :sortBy = 'price' AND :sortOrder = 'asc' THEN s.displayPrice END ASC, " +
           "CASE WHEN :sortBy = 'price' AND :sortOrder = 'desc' THEN s.displayPrice END DESC, " +
           "CASE WHEN :sortBy = 'time' AND :sortOrder = 'asc' THEN s.createTime END ASC, " +
           "CASE WHEN :sortBy = 'time' AND :sortOrder = 'desc' THEN s.createTime END DESC, " +
           "s.createTime DESC")
    Page<SpuInfo> findByComplexConditions(@Param("status") String status,
                                         @Param("categoryId") Integer categoryId,
                                         @Param("storeId") Integer storeId,
                                         @Param("keyword") String keyword,
                                         @Param("minPrice") Double minPrice,
                                         @Param("maxPrice") Double maxPrice,
                                         @Param("brandName") String brandName,
                                         @Param("sortBy") String sortBy,
                                         @Param("sortOrder") String sortOrder,
                                         Pageable pageable);
}