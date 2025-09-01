package com.hunric.model;

import lombok.Data;

/**
 * 收货信息实体类
 */
@Data
public class ShippingInfo {
    
    /**
     * 收货信息ID
     */
    private Long shippingId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 收货人姓名
     */
    private String receiverName;
    
    /**
     * 收货人手机号
     */
    private String receiverPhone;
    
    /**
     * 省份
     */
    private String province;
    
    /**
     * 城市
     */
    private String city;
    
    /**
     * 区/县
     */
    private String district;
    
    /**
     * 详细地址
     */
    private String detailAddress;
    
    /**
     * 是否为默认地址
     */
    private Boolean isDefault;
}