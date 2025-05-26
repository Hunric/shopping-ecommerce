package com.hunric.mapper;

import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;

/**
 * Dashboard统计数据Mapper接口
 */
@Mapper
public interface DashboardMapper {
    
    /**
     * 统计商家的店铺数量
     */
    @Select("SELECT COUNT(*) FROM store_info WHERE merchant_id = #{merchantId}")
    int countStoresByMerchantId(@Param("merchantId") Integer merchantId);
    
    /**
     * 统计商家的商品数量
     */
    @Select("SELECT COUNT(*) FROM spu_info s " +
            "INNER JOIN store_info st ON s.store_id = st.store_id " +
            "WHERE st.merchant_id = #{merchantId}")
    int countProductsByMerchantId(@Param("merchantId") Integer merchantId);
    
    /**
     * 统计商家的订单数量
     */
    @Select("SELECT COUNT(*) FROM order_info o " +
            "INNER JOIN store_info st ON o.store_id = st.store_id " +
            "WHERE st.merchant_id = #{merchantId}")
    int countOrdersByMerchantId(@Param("merchantId") Integer merchantId);
    
    /**
     * 统计商家的总收入
     */
    @Select("SELECT COALESCE(SUM(actual_amount), 0) FROM order_info o " +
            "INNER JOIN store_info st ON o.store_id = st.store_id " +
            "WHERE st.merchant_id = #{merchantId} AND o.order_status IN ('completed', 'pending_receipt')")
    BigDecimal getTotalRevenueByMerchantId(@Param("merchantId") Integer merchantId);
    
    /**
     * 统计商家的待处理订单数量
     */
    @Select("SELECT COUNT(*) FROM order_info o " +
            "INNER JOIN store_info st ON o.store_id = st.store_id " +
            "WHERE st.merchant_id = #{merchantId} AND o.order_status = 'pending_shipment'")
    int countPendingOrdersByMerchantId(@Param("merchantId") Integer merchantId);
    
    /**
     * 统计商家的已发货订单数量
     */
    @Select("SELECT COUNT(*) FROM order_info o " +
            "INNER JOIN store_info st ON o.store_id = st.store_id " +
            "WHERE st.merchant_id = #{merchantId} AND o.order_status = 'pending_receipt'")
    int countShippedOrdersByMerchantId(@Param("merchantId") Integer merchantId);
    
    /**
     * 统计商家本月收入
     */
    @Select("SELECT COALESCE(SUM(actual_amount), 0) FROM order_info o " +
            "INNER JOIN store_info st ON o.store_id = st.store_id " +
            "WHERE st.merchant_id = #{merchantId} " +
            "AND o.order_status IN ('completed', 'pending_receipt') " +
            "AND YEAR(o.create_time) = YEAR(CURDATE()) " +
            "AND MONTH(o.create_time) = MONTH(CURDATE())")
    BigDecimal getMonthlyRevenueByMerchantId(@Param("merchantId") Integer merchantId);
    
    /**
     * 统计商家本月订单数量
     */
    @Select("SELECT COUNT(*) FROM order_info o " +
            "INNER JOIN store_info st ON o.store_id = st.store_id " +
            "WHERE st.merchant_id = #{merchantId} " +
            "AND YEAR(o.create_time) = YEAR(CURDATE()) " +
            "AND MONTH(o.create_time) = MONTH(CURDATE())")
    int countMonthlyOrdersByMerchantId(@Param("merchantId") Integer merchantId);
} 