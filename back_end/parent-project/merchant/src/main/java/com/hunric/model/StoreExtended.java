package com.hunric.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 扩展店铺实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreExtended {
    
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