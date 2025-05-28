package com.hunric.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * SPU创建DTO
 */
public class SpuCreateDTO {
    
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
    
    @JsonProperty("skus")
    private List<SkuCreateDTO> skus;
    
    @JsonProperty("status")
    private String status;

    // 构造函数
    public SpuCreateDTO() {}

    // Getter和Setter方法
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

    public List<SkuCreateDTO> getSkus() {
        return skus;
    }

    public void setSkus(List<SkuCreateDTO> skus) {
        this.skus = skus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SpuCreateDTO{" +
                "merchantId=" + merchantId +
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
                ", skus=" + skus +
                ", status='" + status + '\'' +
                '}';
    }
} 