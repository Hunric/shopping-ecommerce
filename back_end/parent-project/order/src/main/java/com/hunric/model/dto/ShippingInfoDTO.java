package com.hunric.model.dto;

import lombok.Data;

/**
 * 收货信息数据传输对象
 */
@Data
public class ShippingInfoDTO {
    
    /**
     * 收货信息ID
     */
    private Long shippingId;
    
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
     * 邮政编码
     */
    private String postCode;
    
    /**
     * 是否为默认地址
     */
    private Boolean isDefault;
} 