package com.hunric.controller;

import com.hunric.model.ShippingInfo;
import com.hunric.model.dto.ResponseResult;
import com.hunric.service.ShippingInfoService;
import com.hunric.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 收货地址控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user/shipping")
@Tag(name = "收货地址接口", description = "用户收货地址相关操作")
public class ShippingInfoController {

    @Autowired
    private ShippingInfoService shippingInfoService;

    /**
     * 创建收货地址
     *
     * @param shippingInfo 收货地址信息
     * @return 收货地址ID
     */
    @PostMapping
    @Operation(summary = "创建收货地址", description = "创建新的收货地址")
    public ResponseResult<Long> createShippingInfo(@RequestBody @Valid ShippingInfo shippingInfo) {
        log.info("创建收货地址: {}", shippingInfo);
        
        // 从当前登录用户的认证信息中获取userId
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            log.error("无法获取当前用户ID，用户可能未登录");
            return ResponseResult.error(401, "用户未登录");
        }
        
        // 设置userId到地址信息中
        shippingInfo.setUserId(currentUserId);
        log.info("设置用户ID: {} 到收货地址", currentUserId);
        
        return ResponseResult.success(shippingInfoService.createShippingInfo(shippingInfo));
    }

    /**
     * 获取收货地址列表
     *
     * @return 收货地址列表
     */
    @GetMapping
    @Operation(summary = "获取收货地址列表", description = "获取当前用户的所有收货地址")
    public ResponseResult<List<ShippingInfo>> getShippingInfoList() {
        log.info("获取收货地址列表");
        return ResponseResult.success(shippingInfoService.getShippingInfoListByUserId());
    }

    /**
     * 获取收货地址详情
     *
     * @param id 收货地址ID
     * @return 收货地址详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取收货地址详情", description = "根据ID获取收货地址详情")
    public ResponseResult<ShippingInfo> getShippingInfo(@PathVariable @Parameter(description = "收货地址ID") Long id) {
        log.info("获取收货地址详情: id={}", id);
        return ResponseResult.success(shippingInfoService.getShippingInfoById(id));
    }

    /**
     * 更新收货地址
     *
     * @param id 收货地址ID
     * @param shippingInfo 收货地址信息
     * @return 结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新收货地址", description = "更新指定ID的收货地址")
    public ResponseResult<Void> updateShippingInfo(@PathVariable @Parameter(description = "收货地址ID") Long id, 
                                                   @RequestBody @Valid ShippingInfo shippingInfo) {
        log.info("更新收货地址: id={}, shippingInfo={}", id, shippingInfo);
        
        // 从当前登录用户的认证信息中获取userId
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            log.error("无法获取当前用户ID，用户可能未登录");
            return ResponseResult.error(401, "用户未登录");
        }
        
        // 设置userId和shippingId
        shippingInfo.setUserId(currentUserId);
        shippingInfo.setShippingId(id);
        log.info("设置用户ID: {} 到收货地址", currentUserId);
        
        shippingInfoService.updateShippingInfo(shippingInfo);
        return ResponseResult.success();
    }

    /**
     * 删除收货地址
     *
     * @param id 收货地址ID
     * @return 结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除收货地址", description = "删除指定ID的收货地址")
    public ResponseResult<Void> deleteShippingInfo(@PathVariable @Parameter(description = "收货地址ID") Long id) {
        log.info("删除收货地址: id={}", id);
        shippingInfoService.deleteShippingInfo(id);
        return ResponseResult.success();
    }

    /**
     * 设置默认地址
     *
     * @param id 收货地址ID
     * @return 结果
     */
    @PostMapping("/{id}/default")
    @Operation(summary = "设置默认地址", description = "将指定ID的地址设为默认地址")
    public ResponseResult<Void> setDefaultShippingInfo(@PathVariable @Parameter(description = "收货地址ID") Long id) {
        log.info("设置默认地址: id={}", id);
        shippingInfoService.setDefaultShippingInfo(id);
        return ResponseResult.success();
    }
}