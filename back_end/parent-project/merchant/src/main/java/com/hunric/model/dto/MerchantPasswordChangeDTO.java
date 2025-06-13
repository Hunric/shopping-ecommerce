package com.hunric.model.dto;

import lombok.Data;

/**
 * 商家密码修改DTO，用于接收密码修改请求数据
 */
@Data
public class MerchantPasswordChangeDTO {
    private Long merchantId;          // 商家ID
    private String currentPassword;   // 当前密码
    private String newPassword;       // 新密码
} 