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
     * 插入订单信息
     *
     * @param orderInfo 订单信息
     * @return 影响行数
     */
    int insert(OrderInfo orderInfo);
    
    /**
     * 更新订单信息
     *
     * @param orderInfo 订单信息
     * @return 影响行数
     */
    int update(OrderInfo orderInfo);
    
    /**
     * 根据用户ID查询订单列表（分页）
     *
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit  限制数量
     * @return 订单列表
     */
    List<OrderInfo> findByUserId(@Param("userId") Long userId,
                               @Param("offset") int offset,
                               @Param("limit") int limit);
    
    /**
     * 根据用户ID查询订单数量
     *
     * @param userId 用户ID
     * @return 订单数量
     */
    Long countByUserId(@Param("userId") Long userId);
    
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
} 