package com.hunric.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * SKU信息实体类
 */
public class SkuInfo {
    
    @JsonProperty("skuId")
    private Integer skuId;
    
    @JsonProperty("spuId")
    private Integer spuId;
    
    @JsonProperty("skuName")
    private String skuName;
    
    @JsonProperty("salePrice")
    private BigDecimal salePrice;
    
    @JsonProperty("stockQuantity")
    private Integer stockQuantity;
    
    @JsonProperty("attributeCombination")
    private Map<String, Object> attributeCombination;
    
    @JsonProperty("status")
    private Integer status; // 1:上架，2:下架，3:库存不足
    
    @JsonProperty("exclusiveImageUrl")
    private String exclusiveImageUrl;
    
    @JsonProperty("warnStock")
    private Integer warnStock;
    
    @JsonProperty("createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonProperty("updateTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 构造函数
    public SkuInfo() {}

    public SkuInfo(Integer skuId, Integer spuId, String skuName, BigDecimal salePrice, 
                   Integer stockQuantity, Map<String, Object> attributeCombination, 
                   Integer status, String exclusiveImageUrl, Integer warnStock, 
                   LocalDateTime createTime, LocalDateTime updateTime) {
        this.skuId = skuId;
        this.spuId = spuId;
        this.skuName = skuName;
        this.salePrice = salePrice;
        this.stockQuantity = stockQuantity;
        this.attributeCombination = attributeCombination;
        this.status = status;
        this.exclusiveImageUrl = exclusiveImageUrl;
        this.warnStock = warnStock;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    // Getter和Setter方法
    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public Integer getSpuId() {
        return spuId;
    }

    public void setSpuId(Integer spuId) {
        this.spuId = spuId;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Map<String, Object> getAttributeCombination() {
        return attributeCombination;
    }

    public void setAttributeCombination(Map<String, Object> attributeCombination) {
        this.attributeCombination = attributeCombination;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getExclusiveImageUrl() {
        return exclusiveImageUrl;
    }

    public void setExclusiveImageUrl(String exclusiveImageUrl) {
        this.exclusiveImageUrl = exclusiveImageUrl;
    }

    public Integer getWarnStock() {
        return warnStock;
    }

    public void setWarnStock(Integer warnStock) {
        this.warnStock = warnStock;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "SkuInfo{" +
                "skuId=" + skuId +
                ", spuId=" + spuId +
                ", skuName='" + skuName + '\'' +
                ", salePrice=" + salePrice +
                ", stockQuantity=" + stockQuantity +
                ", attributeCombination=" + attributeCombination +
                ", status=" + status +
                ", exclusiveImageUrl='" + exclusiveImageUrl + '\'' +
                ", warnStock=" + warnStock +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
} 