package com.ecommerce.mapper; // 替换为您的实际包名

import com.ecommerce.domain.entity.User; // 导入 User 实体类
import org.apache.ibatis.annotations.*; // 导入 MyBatis 注解

import java.util.Optional;

/**
 * 用户数据访问接口 (Mapper)
 * 定义与 t_user 表相关的数据库操作方法
 */
@Mapper // 标记这是一个 MyBatis Mapper 接口，Spring Boot 会扫描并为其创建代理实现
public interface UserMapper {

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return Optional<User> 包含用户对象（如果找到）或为空
     */
    @Select("SELECT id, username, password_hash, email, phone_number, status, created_at, updated_at FROM t_user WHERE username = #{username}")
    Optional<User> findByUsername(@Param("username") String username); // 使用 Optional 避免空指针

    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return Optional<User>
     */
    @Select("SELECT id, username, password_hash, email, phone_number, status, created_at, updated_at FROM t_user WHERE email = #{email}")
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * 根据手机号查找用户
     * @param phoneNumber 手机号
     * @return Optional<User>
     */
    @Select("SELECT id, username, password_hash, email, phone_number, status, created_at, updated_at FROM t_user WHERE phone_number = #{phoneNumber}")
    Optional<User> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    /**
     * 插入新用户
     * @param user 用户对象 (包含需要插入的数据)
     * @return int 影响的行数 (通常为 1 表示成功)
     */
    @Insert("INSERT INTO t_user (username, password_hash, email, phone_number, status, created_at, updated_at) " +
            "VALUES (#{username}, #{passwordHash}, #{email}, #{phoneNumber}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id") // 获取自增主键并设置回 user 对象的 id 属性
    int insert(User user);

    /**
     * 根据 ID 查找用户 (示例)
     * @param id 用户 ID
     * @return Optional<User>
     */
    @Select("SELECT id, username, password_hash, email, phone_number, status, created_at, updated_at FROM t_user WHERE id = #{id}")
    Optional<User> findById(@Param("id") Long id);

    /**
     * 更新用户信息 (示例 - 可以根据需要添加更多更新方法)
     * @param user 包含更新后信息的用户对象 (必须包含 id)
     * @return int 影响的行数
     */
    @Update("UPDATE t_user SET email = #{email}, phone_number = #{phoneNumber}, status = #{status}, updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int update(User user);

    // 注意：对于更复杂的 SQL 或动态 SQL，推荐使用 XML 映射文件而不是注解。
    // 例如，根据多个条件查询用户列表等。
}
