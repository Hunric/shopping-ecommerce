package com.hunric.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商家实体类
 */
@Data
public class Merchant {
    private Long id;                  // 商家ID
    private String merchantName;      // 商家名称
    private String merchantType;      // 商家类型：company-企业商家，individual-个体商家
    private String email;             // 商家邮箱，用于登录和通知
    private String password;          // 密码（加密存储）
    private String licenseNumber;     // 营业执照编号（统一社会信用代码）
    private String legalPersonName;   // 法人姓名
    private String legalPersonId;     // 法人身份证号
    private String contactName;       // 联系人姓名
    private String contactPhone;      // 联系电话
    private String contactEmail;      // 联系邮箱
    private String provinceCode;      // 省份编码
    private String cityCode;          // 城市编码
    private String districtCode;      // 区县编码
    private String detailAddress;     // 详细地址
    private Integer status;           // 账号状态：0-未审核，1-审核通过，2-审核拒绝，3-禁用
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
} 