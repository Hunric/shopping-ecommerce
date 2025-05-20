package com.hunric.model.dto;

import lombok.Data;
import java.util.List;

/**
 * 商家注册DTO，用于接收前端提交的注册数据
 */
@Data
public class MerchantRegisterDTO {
    private String merchantName;      // 商家名称
    private String merchantType;      // 商家类型：company-企业商家，individual-个体商家
    private String email;             // 商家邮箱
    private String password;          // 密码
    private String licenseNumber;     // 营业执照编号（统一社会信用代码）
    private String legalPersonName;   // 法人姓名
    private String legalPersonId;     // 法人身份证号
    private String contactName;       // 联系人姓名
    private String contactPhone;      // 联系电话
    private String contactEmail;      // 联系邮箱
    private List<String> addressCodes; // 地址编码列表：[省份编码, 城市编码, 区县编码]
    private String detailAddress;     // 详细地址
} 