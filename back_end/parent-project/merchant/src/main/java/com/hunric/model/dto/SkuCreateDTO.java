package com.hunric.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;

/**
 * SKU创建DTO
 */
public class SkuCreateDTO {
    
    @JsonProperty("skuName")
    private String skuName;
    
    @JsonProperty("salePrice")
    private BigDecimal salePrice;
    
    @JsonProperty("stockQuantity")
    private Integer stockQuantity;
    
    @JsonProperty("attributeCombination")
    private Map<String, Object> attributeCombination;
    
    @JsonProperty("status")
    private Integer status;
    
    @JsonProperty("exclusiveImageUrl")
    private String exclusiveImageUrl;
    
    @JsonProperty("warnStock")
    private Integer warnStock;

    // 构造函数
    public SkuCreateDTO() {}

    // Getter和Setter方法
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

    @Override
    public String toString() {
        return "SkuCreateDTO{" +
                "skuName='" + skuName + '\'' +
                ", salePrice=" + salePrice +
                ", stockQuantity=" + stockQuantity +
                ", attributeCombination=" + attributeCombination +
                ", status=" + status +
                ", exclusiveImageUrl='" + exclusiveImageUrl + '\'' +
                ", warnStock=" + warnStock +
                '}';
    }
} 