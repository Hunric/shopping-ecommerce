package com.hunric.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * SPU统计DTO
 */
public class SpuStatsDTO {
    
    @JsonProperty("totalCount")
    private Integer totalCount;
    
    @JsonProperty("onShelfCount")
    private Integer onShelfCount;
    
    @JsonProperty("offShelfCount")
    private Integer offShelfCount;
    
    @JsonProperty("draftCount")
    private Integer draftCount;
    
    @JsonProperty("lowStockCount")
    private Integer lowStockCount;
    
    @JsonProperty("totalValue")
    private BigDecimal totalValue;

    // 构造函数
    public SpuStatsDTO() {}

    public SpuStatsDTO(Integer totalCount, Integer onShelfCount, Integer offShelfCount, 
                       Integer draftCount, Integer lowStockCount, BigDecimal totalValue) {
        this.totalCount = totalCount;
        this.onShelfCount = onShelfCount;
        this.offShelfCount = offShelfCount;
        this.draftCount = draftCount;
        this.lowStockCount = lowStockCount;
        this.totalValue = totalValue;
    }

    // Getter和Setter方法
    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getOnShelfCount() {
        return onShelfCount;
    }

    public void setOnShelfCount(Integer onShelfCount) {
        this.onShelfCount = onShelfCount;
    }

    public Integer getOffShelfCount() {
        return offShelfCount;
    }

    public void setOffShelfCount(Integer offShelfCount) {
        this.offShelfCount = offShelfCount;
    }

    public Integer getDraftCount() {
        return draftCount;
    }

    public void setDraftCount(Integer draftCount) {
        this.draftCount = draftCount;
    }

    public Integer getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(Integer lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    @Override
    public String toString() {
        return "SpuStatsDTO{" +
                "totalCount=" + totalCount +
                ", onShelfCount=" + onShelfCount +
                ", offShelfCount=" + offShelfCount +
                ", draftCount=" + draftCount +
                ", lowStockCount=" + lowStockCount +
                ", totalValue=" + totalValue +
                '}';
    }
} 