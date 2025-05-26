package com.hunric.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商家实体类
 * 对应数据库表：merchant_info
 */
@Data
public class Merchant {
    private Long merchantId;                    // 商家ID (对应merchant_id)
    private String merchantName;                // 商家名称 (对应merchant_name)
    private String phoneNumber;                 // 手机号 (对应phone_number)
    private String email;                       // 邮箱 (对应email)
    private String password;                    // 密码 (对应password)
    private String merchantType;                // 商家类型：enterprise-企业，individual-个体 (对应merchant_type)
    private String businessLicenseNo;           // 营业执照编号 (对应business_license_no)
    private String legalPersonName;             // 法人姓名 (对应legal_person_name)
    private String legalPersonIdCard;           // 法人身份证号 (对应legal_person_id_card)
    private String contactPersonName;           // 联系人姓名 (对应contact_person_name)
    private String contactPhone;                // 联系电话 (对应contact_phone)
    private String contactEmail;                // 联系邮箱 (对应contact_email)
    private String province;                    // 省份 (对应province)
    private String city;                        // 城市 (对应city)
    private String county;                      // 县 (对应county)
    private String district;                    // 区 (对应district)
    private String detailedAddress;             // 详细地址 (对应detailed_address)
} 