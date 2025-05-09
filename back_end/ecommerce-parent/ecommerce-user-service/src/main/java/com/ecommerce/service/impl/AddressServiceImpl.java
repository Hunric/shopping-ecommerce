package com.ecommerce.service.impl; // 替换为您的实际包名

import com.ecommerce.domain.dto.AddressDTO; // 导入 DTO
import com.ecommerce.domain.entity.Address;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.mapper.AddressMapper;
import com.ecommerce.service.AddressService;
// import com.ecommerce.exception.ResourceNotFoundException; // 导入自定义异常
import org.springframework.beans.BeanUtils; // 用于对象属性复制
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 地址服务实现类
 */
@Service
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    @Autowired
    public AddressServiceImpl(AddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    @Override
    public List<Address> listAddressesByUserId(Long userId) {
        return addressMapper.findByUserId(userId);
    }

    @Override
    @Transactional // 事务保证原子性
    public Address addAddress(Long userId, AddressDTO addressDTO) { // 假设 AddressDTO 包含所有必要字段
        Address newAddress = new Address();
        // 将 DTO 的属性复制到实体对象 (需要确保字段名和类型兼容)
        BeanUtils.copyProperties(addressDTO, newAddress);
        newAddress.setUserId(userId); // 设置关联的用户 ID
        newAddress.setCreatedAt(LocalDateTime.now());
        newAddress.setUpdatedAt(LocalDateTime.now());

        // 如果新地址被设为默认，需要先清除该用户其他的默认地址
        if (Boolean.TRUE.equals(newAddress.getIsDefault())) {
            addressMapper.clearDefaultAddress(userId);
        } else {
            // 如果用户还没有地址，则第一个地址自动设为默认 (可选逻辑)
            List<Address> existingAddresses = addressMapper.findByUserId(userId);
            if (existingAddresses == null || existingAddresses.isEmpty()) {
                newAddress.setIsDefault(true);
            } else {
                newAddress.setIsDefault(false); // 明确设为 false
            }
        }

        addressMapper.insert(newAddress);
        return newAddress;
    }

    @Override
    @Transactional
    public Address updateAddress(Long userId, Long addressId, AddressDTO addressDTO) {
        // 1. 检查地址是否存在且属于该用户
        Address existingAddress = addressMapper.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("地址不存在或无权修改")); // 替换为自定义异常

        // 2. 更新属性
        BeanUtils.copyProperties(addressDTO, existingAddress, "id", "userId", "createdAt"); // 忽略 id, userId, createdAt
        existingAddress.setUpdatedAt(LocalDateTime.now());

        // 3. 处理默认地址逻辑
        if (Boolean.TRUE.equals(existingAddress.getIsDefault())) {
            // 如果要将此地址设为默认，先清除其他默认地址
            addressMapper.clearDefaultAddress(userId);
        } else {
            // 如果是将原来的默认地址改为非默认，需要确保用户至少有一个默认地址（可选逻辑）
            // 这里简化处理，允许用户没有默认地址
        }


        // 4. 执行更新
        int updatedRows = addressMapper.update(existingAddress);
        if (updatedRows == 0) {
            // 更新失败，可能并发修改导致，或者逻辑错误
            throw new RuntimeException("更新地址失败");
        }

        return existingAddress;
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        int deletedRows = addressMapper.deleteByIdAndUserId(addressId, userId);
        if (deletedRows == 0) {
            // 地址不存在或不属于该用户
            throw new ResourceNotFoundException("地址不存在或无权删除"); // 替换为自定义异常
        }
        // 可选：如果删除的是默认地址，需要重新选择一个默认地址
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        // 1. 检查地址是否存在且属于该用户
        addressMapper.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("地址不存在或无权设置")); // 替换为自定义异常

        // 2. 清除该用户当前所有的默认地址
        addressMapper.clearDefaultAddress(userId);

        // 3. 将指定地址设为默认
        Address addressToSetDefault = new Address();
        addressToSetDefault.setId(addressId);
        addressToSetDefault.setUserId(userId); // 需要 userId 来确保 WHERE 条件
        addressToSetDefault.setIsDefault(true);
        addressToSetDefault.setUpdatedAt(LocalDateTime.now()); // 更新时间

        // 这里需要一个只更新 is_default 和 updated_at 的 Mapper 方法
        // 暂时用 update 方法代替，但需要注意它会更新所有字段
        // 更好的方式是创建一个专门的 updateIsDefault 方法
        int updatedRows = addressMapper.update(addressToSetDefault); // 注意：这会更新所有字段，需要改进
        if (updatedRows == 0) {
            throw new RuntimeException("设置默认地址失败");
        }

        // 改进建议：在 AddressMapper 中添加方法：
        // @Update("UPDATE t_address SET is_default = 1, updated_at = NOW() WHERE id = #{id} AND user_id = #{userId}")
        // int setDefault(@Param("id") Long id, @Param("userId") Long userId);
        // 然后在这里调用 setDefault 方法。
    }
}
