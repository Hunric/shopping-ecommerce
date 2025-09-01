package com.hunric.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 物流轨迹数据传输对象
 */
@Data
public class LogisticsTrackDTO {
    
    /**
     * 轨迹ID
     */
    private Long trackId;
    
    /**
     * 物流ID
     */
    private Long logisticsId;
    
    /**
     * 轨迹时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime trackTime;
    
    /**
     * 轨迹位置
     */
    private String location;
    
    /**
     * 轨迹详情
     */
    private String description;
} 