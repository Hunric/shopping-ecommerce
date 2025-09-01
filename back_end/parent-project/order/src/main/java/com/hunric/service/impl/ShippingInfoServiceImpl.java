package com.hunric.service.impl;

import com.hunric.exception.BusinessException;
import com.hunric.mapper.ShippingInfoMapper;
import com.hunric.model.ShippingInfo;
import com.hunric.service.ShippingInfoService;
import com.hunric.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 收货地址服务实现类
 */
@Slf4j
@Service
public class ShippingInfoServiceImpl implements ShippingInfoService {

    @Autowired
    private ShippingInfoMapper shippingInfoMapper;

    /**
     * 获取当前登录用户ID
     * 实际项目中应从安全上下文中获取
     *
     * @return 用户ID
     */
    private Long getCurrentUserId() {
        try {
            // 尝试从SecurityUtils获取用户ID
            return SecurityUtils.getCurrentUserId();
        } catch (Exception e) {
            // 如果获取失败，使用默认值（开发环境）
            log.warn("无法从安全上下文获取用户ID，使用默认值: {}", e.getMessage());
            return 2L;  // 使用用户ID 2作为默认值
        }
    }

    @Override
    @Transactional
    public Long createShippingInfo(ShippingInfo shippingInfo) {
        // 设置用户ID
        shippingInfo.setUserId(getCurrentUserId());
        
        // 如果是默认地址，先重置其他地址
        if (Boolean.TRUE.equals(shippingInfo.getIsDefault())) {
            shippingInfoMapper.resetDefaultByUserId(getCurrentUserId());
        }
        
        // 插入数据
        shippingInfoMapper.insert(shippingInfo);
        
        log.info("收货地址创建成功: {}", shippingInfo.getShippingId());
        return shippingInfo.getShippingId();
    }

    @Override
    public List<ShippingInfo> getShippingInfoListByUserId() {
        return shippingInfoMapper.findByUserId(getCurrentUserId());
    }

    @Override
    public ShippingInfo getShippingInfoById(Long id) {
        ShippingInfo shippingInfo = shippingInfoMapper.findById(id);
        if (shippingInfo == null) {
            throw new BusinessException("收货地址不存在");
        }
        
        // 验证是否是当前用户的地址
        if (!getCurrentUserId().equals(shippingInfo.getUserId())) {
            throw new BusinessException("无权访问此收货地址");
        }
        
        return shippingInfo;
    }

    @Override
    @Transactional
    public void updateShippingInfo(ShippingInfo shippingInfo) {
        // 验证地址是否存在
        ShippingInfo existingInfo = getShippingInfoById(shippingInfo.getShippingId());
        
        // 设置用户ID，防止篡改
        shippingInfo.setUserId(getCurrentUserId());
        
        // 如果是默认地址，先重置其他地址
        if (Boolean.TRUE.equals(shippingInfo.getIsDefault())) {
            shippingInfoMapper.resetDefaultByUserId(getCurrentUserId());
        }
        
        // 更新数据
        shippingInfoMapper.update(shippingInfo);
        
        log.info("收货地址更新成功: {}", shippingInfo.getShippingId());
    }

    @Override
    @Transactional
    public void deleteShippingInfo(Long id) {
        // 验证地址是否存在
        ShippingInfo existingInfo = getShippingInfoById(id);
        
        // 删除地址
        shippingInfoMapper.deleteById(id);
        
        log.info("收货地址删除成功: {}", id);
    }

    @Override
    @Transactional
    public void setDefaultShippingInfo(Long id) {
        // 验证地址是否存在
        ShippingInfo existingInfo = getShippingInfoById(id);
        
        // 重置所有地址的默认状态
        shippingInfoMapper.resetDefaultByUserId(getCurrentUserId());
        
        // 设置当前地址为默认
        existingInfo.setIsDefault(true);
        shippingInfoMapper.update(existingInfo);
        
        log.info("默认收货地址设置成功: {}", id);
    }
}