package com.hunric.model.dto;

import lombok.Data;

/**
 * 商家信息DTO，用于接收和返回商家信息数据
 */
@Data
public class MerchantProfileDTO {
    private Long merchantId;               // 商家ID
    private String merchantName;           // 商家名称
    private String merchantType;           // 商家类型：enterprise-企业，individual-个体
    private String email;                  // 邮箱
    private String businessLicenseNo;      // 营业执照编号
    private String legalPersonName;        // 法人姓名
    private String contactPersonName;      // 联系人姓名
    private String contactPhone;           // 联系电话
    private String contactEmail;           // 联系邮箱
    private String province;               // 省份
    private String city;                   // 城市
    private String district;               // 区
    private String detailedAddress;        // 详细地址
} 