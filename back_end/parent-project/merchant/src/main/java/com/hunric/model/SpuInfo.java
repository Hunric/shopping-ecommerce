package com.hunric.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * SPU信息实体类
 */
public class SpuInfo {
    
    @JsonProperty("spuId")
    private Integer spuId;
    
    @JsonProperty("merchantId")
    private Integer merchantId;
    
    @JsonProperty("storeId")
    private Integer storeId;
    
    @JsonProperty("categoryId")
    private Integer categoryId;
    
    @JsonProperty("spuName")
    private String spuName;
    
    @JsonProperty("spuDescription")
    private String spuDescription;
    
    @JsonProperty("productImage")
    private String productImage;
    
    @JsonProperty("displayPrice")
    private BigDecimal displayPrice;
    
    @JsonProperty("basicAttributes")
    private Map<String, Object> basicAttributes;
    
    @JsonProperty("nonBasicAttributes")
    private Map<String, Object> nonBasicAttributes;
    
    @JsonProperty("brandName")
    private String brandName;
    
    @JsonProperty("sellingPoint")
    private String sellingPoint;
    
    @JsonProperty("unit")
    private String unit;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonProperty("updateTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 构造函数
    public SpuInfo() {}

    public SpuInfo(Integer spuId, Integer merchantId, Integer storeId, Integer categoryId, 
                   String spuName, String spuDescription, String productImage, 
                   BigDecimal displayPrice, Map<String, Object> basicAttributes, 
                   Map<String, Object> nonBasicAttributes, String brandName, 
                   String sellingPoint, String unit, String status, 
                   LocalDateTime createTime, LocalDateTime updateTime) {
        this.spuId = spuId;
        this.merchantId = merchantId;
        this.storeId = storeId;
        this.categoryId = categoryId;
        this.spuName = spuName;
        this.spuDescription = spuDescription;
        this.productImage = productImage;
        this.displayPrice = displayPrice;
        this.basicAttributes = basicAttributes;
        this.nonBasicAttributes = nonBasicAttributes;
        this.brandName = brandName;
        this.sellingPoint = sellingPoint;
        this.unit = unit;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    // Getter和Setter方法
    public Integer getSpuId() {
        return spuId;
    }

    public void setSpuId(Integer spuId) {
        this.spuId = spuId;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getSpuName() {
        return spuName;
    }

    public void setSpuName(String spuName) {
        this.spuName = spuName;
    }

    public String getSpuDescription() {
        return spuDescription;
    }

    public void setSpuDescription(String spuDescription) {
        this.spuDescription = spuDescription;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public BigDecimal getDisplayPrice() {
        return displayPrice;
    }

    public void setDisplayPrice(BigDecimal displayPrice) {
        this.displayPrice = displayPrice;
    }

    public Map<String, Object> getBasicAttributes() {
        return basicAttributes;
    }

    public void setBasicAttributes(Map<String, Object> basicAttributes) {
        this.basicAttributes = basicAttributes;
    }

    public Map<String, Object> getNonBasicAttributes() {
        return nonBasicAttributes;
    }

    public void setNonBasicAttributes(Map<String, Object> nonBasicAttributes) {
        this.nonBasicAttributes = nonBasicAttributes;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getSellingPoint() {
        return sellingPoint;
    }

    public void setSellingPoint(String sellingPoint) {
        this.sellingPoint = sellingPoint;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        return "SpuInfo{" +
                "spuId=" + spuId +
                ", merchantId=" + merchantId +
                ", storeId=" + storeId +
                ", categoryId=" + categoryId +
                ", spuName='" + spuName + '\'' +
                ", spuDescription='" + spuDescription + '\'' +
                ", productImage='" + productImage + '\'' +
                ", displayPrice=" + displayPrice +
                ", basicAttributes=" + basicAttributes +
                ", nonBasicAttributes=" + nonBasicAttributes +
                ", brandName='" + brandName + '\'' +
                ", sellingPoint='" + sellingPoint + '\'' +
                ", unit='" + unit + '\'' +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
} 