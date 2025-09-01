package com.hunric.repository;

import com.hunric.model.CartInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 购物车信息Repository
 */
@Repository
public interface CartInfoRepository extends JpaRepository<CartInfo, Integer> {
    
    /**
     * 根据用户ID查询购物车列表
     *
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<CartInfo> findByUserIdOrderByCreateTimeDesc(Integer userId);
    
    /**
     * 根据用户ID和SKU ID查询购物车项
     *
     * @param userId 用户ID
     * @param skuId SKU ID
     * @return 购物车项
     */
    Optional<CartInfo> findByUserIdAndSkuId(Integer userId, Integer skuId);
    
    /**
     * 根据用户ID删除购物车项
     *
     * @param userId 用户ID
     * @return 删除的记录数
     */
    @Modifying
    @Query("DELETE FROM CartInfo c WHERE c.userId = :userId")
    int deleteByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据用户ID和SKU ID删除购物车项
     *
     * @param userId 用户ID
     * @param skuId SKU ID
     * @return 删除的记录数
     */
    @Modifying
    @Query("DELETE FROM CartInfo c WHERE c.userId = :userId AND c.skuId = :skuId")
    int deleteByUserIdAndSkuId(@Param("userId") Integer userId, @Param("skuId") Integer skuId);
    
    /**
     * 根据用户ID统计购物车商品数量
     *
     * @param userId 用户ID
     * @return 商品总数量
     */
    @Query("SELECT COALESCE(SUM(c.quantity), 0) FROM CartInfo c WHERE c.userId = :userId")
    Integer countTotalQuantityByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据用户ID统计购物车项数量
     *
     * @param userId 用户ID
     * @return 购物车项数量
     */
    Integer countByUserId(Integer userId);
    
    /**
     * 根据用户ID查询选中的购物车项
     *
     * @param userId 用户ID
     * @return 选中的购物车列表
     */
    List<CartInfo> findByUserIdAndIsSelectedTrueOrderByCreateTimeDesc(Integer userId);
    
    /**
     * 批量更新购物车项选中状态
     *
     * @param userId 用户ID
     * @param cartIds 购物车ID列表
     * @param isSelected 是否选中
     * @return 更新的记录数
     */
    @Modifying
    @Query("UPDATE CartInfo c SET c.isSelected = :isSelected WHERE c.userId = :userId AND c.cartId IN :cartIds")
    int updateSelectedStatus(@Param("userId") Integer userId, @Param("cartIds") List<Integer> cartIds, @Param("isSelected") Boolean isSelected);
}