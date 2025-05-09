package com.ecommerce.mapper; // 替换为您的实际包名

import com.ecommerce.domain.entity.Address; // 导入 Address 实体类
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

/**
 * 地址数据访问接口 (Mapper)
 * 定义与 t_address 表相关的数据库操作方法
 */
@Mapper
public interface AddressMapper {

    /**
     * 根据用户 ID 查询该用户的所有地址
     * @param userId 用户 ID
     * @return 地址列表
     */
    @Select("SELECT id, user_id, receiver_name, phone_number, province, city, district, detailed_address, is_default, created_at, updated_at " +
            "FROM t_address WHERE user_id = #{userId} ORDER BY is_default DESC, updated_at DESC") // 默认地址优先，然后按更新时间排序
    List<Address> findByUserId(@Param("userId") Long userId);

    /**
     * 根据地址 ID 和用户 ID 查询地址 (确保用户只能查询自己的地址)
     * @param id 地址 ID
     * @param userId 用户 ID
     * @return Optional<Address>
     */
    @Select("SELECT id, user_id, receiver_name, phone_number, province, city, district, detailed_address, is_default, created_at, updated_at " +
            "FROM t_address WHERE id = #{id} AND user_id = #{userId}")
    Optional<Address> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 插入新地址
     * @param address 地址对象
     * @return int 影响行数
     */
    @Insert("INSERT INTO t_address (user_id, receiver_name, phone_number, province, city, district, detailed_address, is_default, created_at, updated_at) " +
            "VALUES (#{userId}, #{receiverName}, #{phoneNumber}, #{province}, #{city}, #{district}, #{detailedAddress}, #{isDefault}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Address address);

    /**
     * 更新地址信息
     * @param address 包含更新信息的地址对象 (必须包含 id)
     * @return int 影响行数
     */
    @Update("UPDATE t_address SET receiver_name = #{receiverName}, phone_number = #{phoneNumber}, province = #{province}, city = #{city}, " +
            "district = #{district}, detailed_address = #{detailedAddress}, is_default = #{isDefault}, updated_at = #{updatedAt} " +
            "WHERE id = #{id} AND user_id = #{userId}") // 确保用户只能更新自己的地址
    int update(Address address);

    /**
     * 根据地址 ID 和用户 ID 删除地址
     * @param id 地址 ID
     * @param userId 用户 ID
     * @return int 影响行数
     */
    @Delete("DELETE FROM t_address WHERE id = #{id} AND user_id = #{userId}")
    int deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 将指定用户的所有地址设置为非默认
     * (通常在设置新的默认地址前调用)
     * @param userId 用户 ID
     * @return int 影响行数
     */
    @Update("UPDATE t_address SET is_default = 0 WHERE user_id = #{userId} AND is_default = 1")
    int clearDefaultAddress(@Param("userId") Long userId);

}
