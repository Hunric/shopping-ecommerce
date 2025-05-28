package com.hunric.model.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 店铺信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreDTO {
    
    private Integer storeId;
    private Integer merchantId;
    private String storeName;
    private String storeLogo;
    private String storeDescription;
    private LocalDateTime openTime;
    private String status;
    private Integer creditScore;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

/**
 * 创建店铺请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class StoreCreateDTO {
    
    private String storeName;
    private String storeLogo;
    private String storeDescription;
}

/**
 * 更新店铺请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class StoreUpdateDTO {
    
    private Integer storeId;
    private String storeName;
    private String storeLogo;
    private String storeDescription;
    private String status;
} 