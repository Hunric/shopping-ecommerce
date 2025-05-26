package com.hunric.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 店铺信息实体类
 * 对应数据库表：store_info
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Store {
    
    /**
     * 店铺ID
     */
    private Integer storeId;
    
    /**
     * 商家ID
     */
    private Integer merchantId;
    
    /**
     * 店铺名称
     */
    private String storeName;
    
    /**
     * 店铺Logo URL
     */
    private String storeLogo;
    
    /**
     * 店铺描述
     */
    private String storeDescription;
    
    /**
     * 开店时间
     */
    private LocalDateTime openTime;
    
    /**
     * 店铺状态：open-营业中, closed-已关闭, suspended-已暂停
     */
    private String status;
    
    /**
     * 店铺信用分
     */
    private Integer creditScore;
} 