package com.hunric.model.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 扩展店铺信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreExtendedDTO {
    
    private Integer storeId;
    private Integer merchantId;
    private String storeName;
    private String storeLogo;
    private String storeDescription;
    private String category;
    private String status;
    private List<String> servicePromise;
    private String servicePhone;
    private String serviceEmail;
    private String businessHours;
    private Integer creditScore;
    private LocalDateTime openTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

/**
 * 创建店铺扩展请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class StoreExtendedCreateDTO {
    
    private Integer merchantId;
    private String storeName;
    private String storeLogo;
    private String storeDescription;
    private String category;
    private String status;
    private List<String> servicePromise;
    private String servicePhone;
    private String serviceEmail;
    private String businessHours;
}

/**
 * 更新店铺扩展请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class StoreExtendedUpdateDTO {
    
    private String storeName;
    private String storeLogo;
    private String storeDescription;
    private String category;
    private String status;
    private List<String> servicePromise;
    private String servicePhone;
    private String serviceEmail;
    private String businessHours;
} 