package com.hunric.mapper;

import com.hunric.model.LogisticsInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 物流信息Mapper接口
 */
@Mapper
public interface LogisticsInfoMapper {
    
    /**
     * 根据ID查询物流信息
     *
     * @param id 物流ID
     * @return 物流信息
     */
    LogisticsInfo findById(@Param("id") Long id);
    
    /**
     * 根据订单ID查询物流信息
     *
     * @param orderId 订单ID
     * @return 物流信息
     */
    LogisticsInfo findByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 插入物流信息
     *
     * @param logisticsInfo 物流信息
     * @return 影响行数
     */
    int insert(LogisticsInfo logisticsInfo);
    
    /**
     * 更新物流信息
     *
     * @param logisticsInfo 物流信息
     * @return 影响行数
     */
    int update(LogisticsInfo logisticsInfo);
} 