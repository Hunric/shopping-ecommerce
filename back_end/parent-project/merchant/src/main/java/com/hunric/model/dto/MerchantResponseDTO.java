package com.hunric.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商家响应DTO，用于返回商家信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantResponseDTO {
    private Boolean success;         // 操作是否成功
    private String message;          // 消息
    private Long merchantId;         // 商家ID
    private String merchantName;     // 商家名称
    private String merchantType;     // 商家类型
    private String email;            // 商家邮箱
    private Integer status;          // 账号状态
} 