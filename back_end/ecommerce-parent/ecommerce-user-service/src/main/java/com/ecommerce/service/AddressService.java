package com.ecommerce.service; // 替换为您的实际包名

import com.ecommerce.domain.dto.AddressDTO; // 导入 DTO (后续创建)
import com.ecommerce.domain.entity.Address;
import com.ecommerce.exception.ResourceNotFoundException;

import java.util.List;

/**
 * 地址服务接口
 * 定义地址相关的业务操作
 */
public interface AddressService {

    /**
     * 获取指定用户的所有地址
     * @param userId 用户 ID
     * @return 地址列表
     */
    List<Address> listAddressesByUserId(Long userId);

    /**
     * 添加新地址
     * @param userId 用户 ID (确保地址属于该用户)
     * @param addressDTO 包含新地址信息的 DTO
     * @return 创建成功的地址实体
     */
    Address addAddress(Long userId, AddressDTO addressDTO); // 假设有 AddressDTO

    /**
     * 更新地址信息
     * @param userId 用户 ID (确保用户只能更新自己的地址)
     * @param addressId 要更新的地址 ID
     * @param addressDTO 包含更新后地址信息的 DTO
     * @return 更新后的地址实体
     * @throws ResourceNotFoundException 如果地址不存在或不属于该用户
     */
    Address updateAddress(Long userId, Long addressId, AddressDTO addressDTO);

    /**
     * 删除地址
     * @param userId 用户 ID (确保用户只能删除自己的地址)
     * @param addressId 要删除的地址 ID
     * @throws ResourceNotFoundException 如果地址不存在或不属于该用户
     */
    void deleteAddress(Long userId, Long addressId);

    /**
     * 设置默认地址
     * @param userId 用户 ID
     * @param addressId 要设置为默认的地址 ID
     * @throws ResourceNotFoundException 如果地址不存在或不属于该用户
     */
    void setDefaultAddress(Long userId, Long addressId);
}
