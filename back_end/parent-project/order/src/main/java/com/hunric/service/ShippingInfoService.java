package com.hunric.service;

import com.hunric.model.ShippingInfo;
import java.util.List;

/**
 * 收货地址服务接口
 */
public interface ShippingInfoService {

    /**
     * 创建收货地址
     *
     * @param shippingInfo 收货地址信息
     * @return 收货地址ID
     */
    Long createShippingInfo(ShippingInfo shippingInfo);

    /**
     * 获取当前用户的收货地址列表
     *
     * @return 收货地址列表
     */
    List<ShippingInfo> getShippingInfoListByUserId();

    /**
     * 根据ID获取收货地址
     *
     * @param id 收货地址ID
     * @return 收货地址信息
     */
    ShippingInfo getShippingInfoById(Long id);

    /**
     * 更新收货地址
     *
     * @param shippingInfo 收货地址信息
     */
    void updateShippingInfo(ShippingInfo shippingInfo);

    /**
     * 删除收货地址
     *
     * @param id 收货地址ID
     */
    void deleteShippingInfo(Long id);

    /**
     * 设置默认收货地址
     *
     * @param id 收货地址ID
     */
    void setDefaultShippingInfo(Long id);
}