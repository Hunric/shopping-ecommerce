package com.hunric.mapper;

import com.hunric.model.OrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单信息Mapper接口
 */
@Mapper
public interface OrderInfoMapper {
    
    /**
     * 根据ID查询订单信息
     *
     * @param id 订单ID
     * @return 订单信息
     */
    OrderInfo findById(@Param("id") Long id);
    
    /**
     * 根据条件查询订单列表（分页）
     *
     * @param condition 查询条件
     * @param offset    偏移量
     * @param limit     限制数量
     * @return 订单列表
     */
    List<OrderInfo> findByCondition(@Param("condition") OrderInfo condition, 
                                  @Param("offset") int offset, 
                                  @Param("limit") int limit);
    
    /**
     * 根据条件查询订单数量
     *
     * @param condition 查询条件
     * @return 订单数量
     */
    Long countByCondition(@Param("condition") OrderInfo condition);
    
    /**
     * 根据商家ID和日期范围查询订单列表（分页）
     *
     * @param merchantId  商家ID
     * @param orderNo     订单编号（可选）
     * @param orderStatus 订单状态（可选）
     * @param startTime   开始时间
     * @param endTime     结束时间
     * @param offset      偏移量
     * @param limit       限制数量
     * @return 订单列表
     */
    List<OrderInfo> findByMerchantIdAndDateRange(@Param("merchantId") Long merchantId,
                                               @Param("orderNo") String orderNo,
                                               @Param("orderStatus") String orderStatus,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime,
                                               @Param("offset") int offset,
                                               @Param("limit") int limit);
    
    /**
     * 根据商家ID和日期范围查询订单数量
     *
     * @param merchantId  商家ID
     * @param orderNo     订单编号（可选）
     * @param orderStatus 订单状态（可选）
     * @param startTime   开始时间
     * @param endTime     结束时间
     * @return 订单数量
     */
    Long countByMerchantIdAndDateRange(@Param("merchantId") Long merchantId,
                                     @Param("orderNo") String orderNo,
                                     @Param("orderStatus") String orderStatus,
                                     @Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据商家ID查询订单数量
     *
     * @param merchantId 商家ID
     * @return 订单数量
     */
    Long countByMerchantId(@Param("merchantId") Long merchantId);
    
    /**
     * 根据商家ID和订单状态查询订单数量
     *
     * @param merchantId  商家ID
     * @param orderStatus 订单状态
     * @return 订单数量
     */
    Long countByMerchantIdAndStatus(@Param("merchantId") Long merchantId,
                                  @Param("orderStatus") String orderStatus);
    
    /**
     * 根据商家ID和日期范围查询订单实付金额总和
     *
     * @param merchantId 商家ID
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return 实付金额总和
     */
    BigDecimal sumActualAmountByMerchantIdAndDateRange(@Param("merchantId") Long merchantId,
                                                     @Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime);
    
    /**
     * 更新订单信息
     *
     * @param orderInfo 订单信息
     * @return 影响行数
     */
    int update(OrderInfo orderInfo);
} 