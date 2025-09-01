package com.hunric.mapper;

import com.hunric.model.ShippingInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收货信息Mapper接口
 */
@Mapper
public interface ShippingInfoMapper {
    
    /**
     * 根据ID查询收货信息
     *
     * @param id 收货信息ID
     * @return 收货信息
     */
    ShippingInfo findById(@Param("id") Long id);
    
    /**
     * 根据用户ID查询收货信息列表
     *
     * @param userId 用户ID
     * @return 收货信息列表
     */
    List<ShippingInfo> findByUserId(@Param("userId") Long userId);
    
    /**
     * 插入收货信息
     *
     * @param shippingInfo 收货信息
     * @return 影响行数
     */
    int insert(ShippingInfo shippingInfo);
    
    /**
     * 更新收货信息
     *
     * @param shippingInfo 收货信息
     * @return 影响行数
     */
    int update(ShippingInfo shippingInfo);
    
    /**
     * 删除收货信息
     *
     * @param id 收货信息ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 将用户的所有地址设置为非默认
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int resetDefaultByUserId(@Param("userId") Long userId);
} 